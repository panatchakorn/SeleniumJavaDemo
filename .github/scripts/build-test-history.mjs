import { readdir, readFile, mkdir, writeFile } from "node:fs/promises";
import path from "node:path";

const required = (name) => {
  const value = process.env[name];
  if (!value) {
    throw new Error(`Missing required environment variable: ${name}`);
  }
  return value;
};

const historyDirectory = path.join(required("HISTORY_DIRECTORY"), "runs");
const siteDirectory = required("SITE_DIRECTORY");
const githubOrigin = new URL(required("GITHUB_SERVER_URL")).origin;
await mkdir(siteDirectory, { recursive: true });

const validCount = (value) => Number.isSafeInteger(value) && value >= 0;
const records = [];
for (const filename of (await readdir(historyDirectory)).filter((name) => name.endsWith(".json"))) {
  const record = JSON.parse(await readFile(path.join(historyDirectory, filename), "utf8"));
  if (!record || typeof record !== "object" || Array.isArray(record)) {
    throw new Error(`History record "${filename}" must be a JSON object.`);
  }
  const validDate = typeof record.date === "string" && !Number.isNaN(Date.parse(record.date));
  const runUrl = typeof record.url === "string" ? new URL(record.url) : null;
  if (
    typeof record.runId !== "string" ||
    !validCount(record.runNumber) ||
    !validCount(record.attempt) ||
    !validDate ||
    typeof record.branch !== "string" ||
    typeof record.workflow !== "string" ||
    typeof record.suite !== "string" ||
    !/^[0-9a-f]{40}$/i.test(record.commit) ||
    !["success", "failure"].includes(record.conclusion) ||
    !validCount(record.passed) ||
    !validCount(record.failed) ||
    !validCount(record.skipped) ||
    (record.durationMs !== null && !validCount(record.durationMs)) ||
    !runUrl ||
    runUrl.protocol !== "https:" ||
    runUrl.origin !== githubOrigin
  ) {
    throw new Error(`History record "${filename}" has an invalid shape.`);
  }
  records.push(record);
}

records.sort((left, right) =>
  Date.parse(left.date) - Date.parse(right.date) ||
  left.runNumber - right.runNumber ||
  left.attempt - right.attempt,
);

const publishedRecords = records.slice(-1000);
await writeFile(
  path.join(siteDirectory, "test-history.json"),
  `${JSON.stringify(publishedRecords, null, 2)}\n`,
);

const html = `<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Test history</title>
  <style>
    :root { color-scheme: light dark; font-family: system-ui, sans-serif; }
    body { max-width: 1100px; margin: 2rem auto; padding: 0 1rem; }
    h1 { margin-bottom: .25rem; }
    .muted, .axis-label { color: #718096; }
    .filters { display: flex; flex-wrap: wrap; gap: 1rem; margin: 1.5rem 0; }
    label { display: grid; gap: .3rem; }
    select { min-width: 11rem; padding: .4rem; }
    .summary { display: flex; flex-wrap: wrap; gap: 1rem; }
    .passed { color: #16803c; }
    .failed { color: #c53030; }
    .skipped { color: #718096; }
    .chart { overflow-x: auto; }
    svg { display: block; width: 100%; min-width: 620px; }
    table { width: 100%; border-collapse: collapse; text-align: left; }
    th, td { padding: .65rem; border-bottom: 1px solid #8885; white-space: nowrap; }
    .table-wrap { overflow-x: auto; }
    .legend { display: flex; gap: 1rem; margin: .75rem 0; }
    .legend span::before { content: ""; display: inline-block; width: .7rem; height: .7rem; margin-right: .4rem; background: var(--color); }
  </style>
</head>
<body>
  <h1>Test history</h1>
  <p class="muted">The history branch stores every run. The dashboard includes the latest 1,000 records, charts the latest 50 matching runs, and lists the latest 100.</p>
  <div class="filters">
    <label>Test suite<select id="suite-filter"><option value="">All suites</option></select></label>
    <label>Branch<select id="branch-filter"><option value="">All branches</option></select></label>
    <label>Workflow<select id="workflow-filter"><option value="">All workflows</option></select></label>
  </div>
  <h2>Latest matching run</h2>
  <div id="summary" class="summary"></div>
  <p id="empty" class="muted" hidden>No runs match these filters.</p>
  <h2>Trend (latest 50 matching runs)</h2>
  <div class="legend">
    <span style="--color:#16803c">Passed</span>
    <span style="--color:#c53030">Failed</span>
    <span style="--color:#718096">Skipped</span>
  </div>
  <div class="chart"><svg id="chart" viewBox="0 0 900 320" role="img" aria-label="Test result counts by workflow run"></svg></div>
  <h2>Runs (latest 100 matching runs)</h2>
  <div class="table-wrap">
    <table>
      <thead><tr><th>Date (UTC)</th><th>Workflow run</th><th>Suite</th><th>Branch</th><th>Passed</th><th>Failed</th><th>Skipped</th><th>Duration</th></tr></thead>
      <tbody id="runs"></tbody>
    </table>
  </div>
  <script>
    const colors = { passed: "#16803c", failed: "#c53030", skipped: "#718096" };
    const filters = {
      suite: document.querySelector("#suite-filter"),
      branch: document.querySelector("#branch-filter"),
      workflow: document.querySelector("#workflow-filter"),
    };
    const svg = document.querySelector("#chart");
    const addSvg = (name, attributes, parent = svg) => {
      const element = document.createElementNS("http://www.w3.org/2000/svg", name);
      for (const [key, value] of Object.entries(attributes)) element.setAttribute(key, value);
      parent.append(element);
      return element;
    };
    const addOptionValues = (select, values) => {
      for (const value of [...new Set(values)].sort()) {
        const option = document.createElement("option");
        option.value = value;
        option.textContent = value;
        select.append(option);
      }
    };
    const addCell = (row, value, className = "") => {
      const cell = row.insertCell();
      cell.textContent = value;
      if (className) cell.className = className;
      return cell;
    };
    const render = (allRecords) => {
      const matching = allRecords.filter((record) =>
        (!filters.suite.value || record.suite === filters.suite.value) &&
        (!filters.branch.value || record.branch === filters.branch.value) &&
        (!filters.workflow.value || record.workflow === filters.workflow.value)
      );
      const latest = matching.at(-1);
      document.querySelector("#empty").hidden = matching.length > 0;
      const summary = document.querySelector("#summary");
      summary.replaceChildren();
      if (latest) {
        const link = document.createElement("a");
        link.href = latest.url;
        link.textContent = "Run #" + latest.runNumber + " (attempt " + latest.attempt + ")";
        summary.append(link);
        for (const key of ["passed", "failed", "skipped"]) {
          const item = document.createElement("span");
          item.className = key;
          item.textContent = latest[key] + " " + key;
          summary.append(item);
        }
      }

      svg.replaceChildren();
      const plot = { left: 52, right: 875, top: 20, bottom: 260 };
      const chartRuns = matching.slice(-50);
      const maxValue = Math.max(1, ...chartRuns.map((record) =>
        Math.max(record.passed, record.failed, record.skipped)
      ));
      const gridCount = 4;
      for (let index = 0; index <= gridCount; index += 1) {
        const y = plot.bottom - (index / gridCount) * (plot.bottom - plot.top);
        const value = Math.round((index / gridCount) * maxValue);
        addSvg("line", { x1: plot.left, y1: y, x2: plot.right, y2: y, stroke: "#8885" });
        const label = addSvg("text", { x: plot.left - 8, y: y + 4, "text-anchor": "end", class: "axis-label" });
        label.textContent = value;
      }
      for (const [key, color] of Object.entries(colors)) {
        const points = chartRuns.map((record, index) => {
          const x = chartRuns.length === 1
            ? (plot.left + plot.right) / 2
            : plot.left + (index / (chartRuns.length - 1)) * (plot.right - plot.left);
          const y = plot.bottom - (record[key] / maxValue) * (plot.bottom - plot.top);
          return { x, y, record };
        });
        if (points.length > 1) {
          addSvg("polyline", {
            points: points.map((point) => point.x + "," + point.y).join(" "),
            fill: "none", stroke: color, "stroke-width": 2,
          });
        }
        for (const point of points) {
          const circle = addSvg("circle", { cx: point.x, cy: point.y, r: 3.5, fill: color });
          const title = addSvg("title", {}, circle);
          title.textContent = "#" + point.record.runNumber + " " + key + ": " + point.record[key];
        }
      }
      const labelIndexes = new Set([0, Math.floor((chartRuns.length - 1) / 2), chartRuns.length - 1]);
      for (const index of labelIndexes) {
        const record = chartRuns[index];
        if (!record) continue;
        const x = chartRuns.length === 1
          ? (plot.left + plot.right) / 2
          : plot.left + (index / (chartRuns.length - 1)) * (plot.right - plot.left);
        const label = addSvg("text", { x, y: plot.bottom + 24, "text-anchor": "middle", class: "axis-label" });
        label.textContent = "#" + record.runNumber;
      }

      const tbody = document.querySelector("#runs");
      tbody.replaceChildren();
      for (const record of matching.slice(-100).reverse()) {
        const row = tbody.insertRow();
        addCell(row, new Date(record.date).toISOString().replace("T", " ").replace("Z", ""));
        const runCell = row.insertCell();
        const link = document.createElement("a");
        link.href = record.url;
        link.textContent = "#" + record.runNumber + " (attempt " + record.attempt + ")";
        runCell.append(link);
        addCell(row, record.suite);
        addCell(row, record.branch);
        for (const key of ["passed", "failed", "skipped"]) addCell(row, String(record[key]), key);
        addCell(row, record.durationMs === null ? "-" : (record.durationMs / 1000).toFixed(1) + " s");
      }
    };

    fetch("./test-history.json")
      .then((response) => {
        if (!response.ok) throw new Error("Could not load test-history.json (HTTP " + response.status + ").");
        return response.json();
      })
      .then((records) => {
        if (!Array.isArray(records)) throw new Error("Test history must be a JSON array.");
        for (const [key, select] of Object.entries(filters)) {
          addOptionValues(select, records.map((record) => record[key]));
          select.addEventListener("change", () => render(records));
        }
        render(records);
      })
      .catch((error) => {
        document.querySelector("#empty").hidden = false;
        document.querySelector("#empty").textContent = error.message;
      });
  </script>
</body>
</html>
`;

await writeFile(path.join(siteDirectory, "index.html"), html);
