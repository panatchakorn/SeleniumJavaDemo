import { readFile, writeFile } from "node:fs/promises";
import path from "node:path";

const required = (name) => {
  const value = process.env[name];
  if (!value) {
    throw new Error(`Missing required environment variable: ${name}`);
  }
  return value;
};

const count = (name) => {
  const value = required(name);
  if (!/^\d+$/.test(value)) {
    throw new Error(`Expected ${name} to be a non-negative integer, received "${value}".`);
  }
  return Number(value);
};

const historyFile = required("HISTORY_FILE");
const siteDir = required("SITE_DIR");
const historyText = await readFile(historyFile, "utf8");
let runs;

try {
  runs = JSON.parse(historyText);
} catch (error) {
  throw new Error(`Previous test history is not valid JSON: ${error.message}`);
}

if (!Array.isArray(runs)) {
  throw new Error("Previous test history must be a JSON array.");
}

for (const [index, run] of runs.entries()) {
  const validCount = (value) => Number.isSafeInteger(value) && value >= 0;
  let validUrl = false;
  try {
    validUrl = new URL(run.url).protocol === "https:";
  } catch {
    validUrl = false;
  }

  if (
    !run ||
    typeof run !== "object" ||
    typeof run.runId !== "string" ||
    !validCount(run.runNumber) ||
    !validCount(run.attempt) ||
    typeof run.date !== "string" ||
    !validCount(run.passed) ||
    !validCount(run.failed) ||
    !validCount(run.skipped) ||
    (run.durationMs !== null && !validCount(run.durationMs)) ||
    !validUrl
  ) {
    throw new Error(`Previous test history entry ${index} has an invalid shape.`);
  }
}

const runUrl = required("RUN_URL");
if (new URL(runUrl).protocol !== "https:") {
  throw new Error(`Expected RUN_URL to use HTTPS, received "${runUrl}".`);
}

const duration = process.env.DURATION_MS;
runs.push({
  runId: required("RUN_ID"),
  runNumber: count("RUN_NUMBER"),
  attempt: count("RUN_ATTEMPT"),
  date: new Date().toISOString(),
  passed: count("PASSED"),
  failed: count("FAILED"),
  skipped: count("SKIPPED"),
  durationMs: duration && /^\d+$/.test(duration) ? Number(duration) : null,
  url: runUrl,
});

const escapeHtml = (value) =>
  String(value).replace(/[&<>"']/g, (character) => ({
    "&": "&amp;",
    "<": "&lt;",
    ">": "&gt;",
    '"': "&quot;",
    "'": "&#39;",
  })[character]);

const colors = {
  passed: "#16803c",
  failed: "#c53030",
  skipped: "#718096",
};
const chartHeight = 180;
const chartTop = 16;
const chartBottom = chartTop + chartHeight;
const chartWidth = Math.max(640, 52 * runs.length + 40);
const maxTotal = Math.max(1, ...runs.map((run) => run.passed + run.failed + run.skipped));
const bars = runs.map((run, index) => {
  const groupX = 36 + index * 52;
  const values = ["passed", "failed", "skipped"];
  const group = values.map((key, barIndex) => {
    const height = (run[key] / maxTotal) * chartHeight;
    const x = groupX + barIndex * 12;
    const y = chartBottom - height;
    return `<rect x="${x}" y="${y}" width="9" height="${height}" fill="${colors[key]}"><title>${key}: ${run[key]}</title></rect>`;
  }).join("");
  return `${group}<text x="${groupX + 13}" y="${chartBottom + 20}" text-anchor="middle" class="axis-label">#${run.runNumber}</text>`;
}).join("");

const rows = [...runs].reverse().map((run) => {
  const date = escapeHtml(run.date);
  const durationText = run.durationMs === null ? "-" : `${(run.durationMs / 1000).toFixed(1)} s`;
  return `<tr>
    <td>${date}</td>
    <td><a href="${escapeHtml(run.url)}">#${run.runNumber} (attempt ${run.attempt})</a></td>
    <td class="passed">${run.passed}</td>
    <td class="failed">${run.failed}</td>
    <td class="skipped">${run.skipped}</td>
    <td>${durationText}</td>
  </tr>`;
}).join("\n");

const latest = runs.at(-1);
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
    .chart, .table-wrap { overflow-x: auto; }
    svg { min-width: 100%; }
    table { width: 100%; border-collapse: collapse; text-align: left; }
    th, td { padding: .65rem; border-bottom: 1px solid #8885; white-space: nowrap; }
    .passed { color: ${colors.passed}; }
    .failed { color: ${colors.failed}; }
    .skipped { color: ${colors.skipped}; }
    .legend { display: flex; gap: 1rem; margin: .75rem 0; }
    .legend span::before { content: ""; display: inline-block; width: .7rem; height: .7rem; margin-right: .4rem; background: var(--color); }
  </style>
</head>
<body>
  <h1>Test history</h1>
  <p class="muted">Pass, fail, and skip counts from each workflow run. Click a run to open its logs and downloadable reports.</p>
  <h2>Latest run</h2>
  <p><a href="${escapeHtml(latest.url)}">Run #${latest.runNumber} (attempt ${latest.attempt})</a>:
    <span class="passed">${latest.passed} passed</span>,
    <span class="failed">${latest.failed} failed</span>,
    <span class="skipped">${latest.skipped} skipped</span></p>
  <h2>Trend</h2>
  <div class="legend">
    <span style="--color:${colors.passed}">Passed</span>
    <span style="--color:${colors.failed}">Failed</span>
    <span style="--color:${colors.skipped}">Skipped</span>
  </div>
  <div class="chart">
    <svg viewBox="0 0 ${chartWidth} ${chartBottom + 30}" role="img" aria-label="Test result counts by workflow run">
      <line x1="30" y1="${chartBottom}" x2="${chartWidth}" y2="${chartBottom}" stroke="#8885"></line>
      ${bars}
    </svg>
  </div>
  <h2>All runs (${runs.length})</h2>
  <div class="table-wrap">
    <table>
      <thead><tr><th>Date (UTC)</th><th>Workflow run</th><th>Passed</th><th>Failed</th><th>Skipped</th><th>Duration</th></tr></thead>
      <tbody>${rows}</tbody>
    </table>
  </div>
</body>
</html>
`;

await writeFile(path.join(siteDir, "test-history.json"), `${JSON.stringify(runs, null, 2)}\n`);
await writeFile(path.join(siteDir, "index.html"), html);
