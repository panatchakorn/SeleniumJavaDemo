import { mkdir, writeFile } from "node:fs/promises";
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
  if (!/^\d+$/.test(value) || !Number.isSafeInteger(Number(value))) {
    throw new Error(`Expected ${name} to be a non-negative integer, received "${value}".`);
  }
  return Number(value);
};

const suite = required("TEST_SUITE");
if (!/^[A-Za-z0-9_-]+$/.test(suite)) {
  throw new Error(`Invalid suite identifier: "${suite}".`);
}

const durationText = process.env.DURATION_MS ?? "";
if (durationText && (!/^\d+$/.test(durationText) || !Number.isSafeInteger(Number(durationText)))) {
  throw new Error(`Expected DURATION_MS to be a non-negative integer, received "${durationText}".`);
}

const result = {
  suite,
  passed: count("PASSED"),
  failed: count("FAILED"),
  skipped: count("SKIPPED"),
  durationMs: durationText ? Number(durationText) : null,
};

const outputDirectory = required("OUTPUT_DIRECTORY");
await mkdir(outputDirectory, { recursive: true });
await writeFile(
  path.join(outputDirectory, "result.json"),
  `${JSON.stringify(result, null, 2)}\n`,
);
