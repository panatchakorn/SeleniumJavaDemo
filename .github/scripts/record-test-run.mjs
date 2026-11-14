import { execFileSync } from "node:child_process";
import { mkdir, readFile, writeFile } from "node:fs/promises";
import path from "node:path";

const required = (name) => {
  const value = process.env[name];
  if (!value) {
    throw new Error(`Missing required environment variable: ${name}`);
  }
  return value;
};

const git = (directory, args, options = {}) =>
  execFileSync("git", args, {
    cwd: directory,
    encoding: "utf8",
    ...options,
  }).trim();
  n 
const repository = required("GITHUB_REPOSITORY");
if (!/^[\w.-]+\/[\w.-]+$/.test(repository)) {
  throw new Error(`Invalid repository name: "${repository}".`);
}

const runId = required("RUN_ID");
const attempt = required("RUN_ATTEMPT");
if (
  !/^\d+$/.test(runId) ||
  !/^\d+$/.test(attempt) ||
  !Number.isSafeInteger(Number(runId)) ||
  !Number.isSafeInteger(Number(attempt))
) {
  throw new Error("Run ID and attempt must be non-negative integer identifiers.");
}

const artifactPath = path.join(required("ARTIFACT_DIRECTORY"), "result.json");
const result = JSON.parse(await readFile(artifactPath, "utf8"));
const validCount = (value) => Number.isSafeInteger(value) && value >= 0;
const allowedSuites = new Set([
  "smokeTest",
  "groupATest",
  "bundleATest",
  "bundleBTest",
  "bundleCTest",
  "bundleDTest",
  "browserstackTest",
]);

if (
  !result ||
  typeof result !== "object" ||
  !allowedSuites.has(result.suite) ||
  !validCount(result.passed) ||
  !validCount(result.failed) ||
  !validCount(result.skipped) ||
  (result.durationMs !== null && !validCount(result.durationMs))
) {
  throw new Error("The test result artifact has an invalid shape.");
}

const runUrl = new URL(required("RUN_URL"));
const serverUrl = new URL(required("GITHUB_SERVER_URL"));
if (runUrl.protocol !== "https:" || runUrl.origin !== serverUrl.origin) {
  throw new Error(`Unexpected workflow run URL: "${runUrl.href}".`);
}

const runDate = required("RUN_DATE");
if (Number.isNaN(Date.parse(runDate))) {
  throw new Error(`Invalid workflow run date: "${runDate}".`);
}

const headBranch = required("HEAD_BRANCH");
const workflowName = required("WORKFLOW_NAME");
if (!headBranch || headBranch.length > 255 || !workflowName || workflowName.length > 255) {
  throw new Error("Workflow branch or name is empty or too long.");
}

const conclusion = required("CONCLUSION");
if (!["success", "failure"].includes(conclusion)) {
  throw new Error(`Unsupported workflow conclusion: "${conclusion}".`);
}

const record = {
  runId,
  runNumber: Number(required("RUN_NUMBER")),
  attempt: Number(attempt),
  date: runDate,
  branch: headBranch,
  commit: required("HEAD_SHA"),
  workflow: workflowName,
  suite: result.suite,
  conclusion,
  passed: result.passed,
  failed: result.failed,
  skipped: result.skipped,
  durationMs: result.durationMs,
  url: runUrl.href,
};

if (
  !validCount(record.runNumber) ||
  !/^[0-9a-f]{40}$/i.test(record.commit)
) {
  throw new Error("Workflow run metadata is invalid.");
}

const historyDirectory = required("HISTORY_DIRECTORY");
await mkdir(historyDirectory, { recursive: true });
const token = required("GH_TOKEN");
const remote = `${required("GITHUB_SERVER_URL")}/${repository}.git`;
const authHeader = Buffer.from(`x-access-token:${token}`).toString("base64");
const gitAuth = (args, options = {}) =>
  git(historyDirectory, ["-c", `http.extraheader=AUTHORIZATION: basic ${authHeader}`, ...args], options);
const showRecord = (revision, recordPath) => {
  const found = git(historyDirectory, ["ls-tree", "-r", "--name-only", revision, "--", recordPath]);
  return found ? git(historyDirectory, ["show", `${revision}:${recordPath}`]) : "";
};

git(historyDirectory, ["init", "--initial-branch=test-history"]);
git(historyDirectory, ["config", "user.name", "github-actions[bot]"]);
git(historyDirectory, ["config", "user.email", "41898282+github-actions[bot]@users.noreply.github.com"]);
git(historyDirectory, ["remote", "add", "origin", remote]);

const remoteRefs = gitAuth(["ls-remote", "--heads", "origin", "refs/heads/test-history"]);
if (!remoteRefs) {
  const emptyTree = git(historyDirectory, ["mktree"], { input: "" });
  const seedCommit = git(
    historyDirectory,
    ["-c", "user.name=github-actions[bot]", "-c", "user.email=41898282+github-actions[bot]@users.noreply.github.com", "commit-tree", emptyTree, "-m", "Initialize test history"],
    {
      env: {
        ...process.env,
        GIT_AUTHOR_DATE: "2000-01-01T00:00:00Z",
        GIT_COMMITTER_DATE: "2000-01-01T00:00:00Z",
      },
    },
  );
  try {
    gitAuth(["push", "origin", `${seedCommit}:refs/heads/test-history`]);
  } catch (error) {
    if (!gitAuth(["ls-remote", "--heads", "origin", "refs/heads/test-history"])) {
      throw new Error(`Could not initialize the test-history branch: ${error.message}`);
    }
  }
}

gitAuth(["fetch", "origin", "refs/heads/test-history"]);
git(historyDirectory, ["checkout", "-B", "test-history", "FETCH_HEAD"]);

const recordPath = `runs/${runId}-${attempt}.json`;
const serializedRecord = `${JSON.stringify(record, null, 2)}\n`;
const existingRecord = showRecord("HEAD", recordPath);

if (existingRecord) {
  if (existingRecord !== serializedRecord.trim()) {
    throw new Error(`A different history record already exists for run ${runId}, attempt ${attempt}.`);
  }
} else {
  const filePath = path.join(historyDirectory, recordPath);
  await mkdir(path.dirname(filePath), { recursive: true });
  await writeFile(filePath, serializedRecord);
  git(historyDirectory, ["add", recordPath]);
  git(historyDirectory, ["commit", "-m", `Record test run ${runId} attempt ${attempt}`]);

  let pushed = false;
  for (let retry = 0; retry < 5; retry += 1) {
    try {
      gitAuth(["push", "origin", "HEAD:refs/heads/test-history"]);
      pushed = true;
      break;
    } catch (error) {
      if (retry === 4) {
        throw new Error(`Could not persist test history after ${retry + 1} push attempts: ${error.message}`);
      }
      gitAuth(["fetch", "origin", "refs/heads/test-history"]);
      const remoteRecord = showRecord("FETCH_HEAD", recordPath);
      if (remoteRecord) {
        if (remoteRecord !== serializedRecord.trim()) {
          throw new Error(`A different history record already exists for run ${runId}, attempt ${attempt}.`);
        }
        git(historyDirectory, ["checkout", "-B", "test-history", "FETCH_HEAD"]);
        pushed = true;
        break;
      }
      git(historyDirectory, ["rebase", "FETCH_HEAD"]);
    }
  }
  if (!pushed) {
    throw new Error(`Could not persist test history for run ${runId}, attempt ${attempt}.`);
  }
}

gitAuth(["fetch", "origin", "refs/heads/test-history"]);
git(historyDirectory, ["checkout", "-B", "test-history", "FETCH_HEAD"]);
