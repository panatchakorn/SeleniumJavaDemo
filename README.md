# Selenium Java TestNG ExtentReport Project

This project demonstrates how to set up a Selenium Java test automation framework using TestNG and ExtentReports for generating detailed test reports.

The test can be run in GitHub Actions CI/CD pipeline.

## Table of Contents

- [Prerequisites](#prerequisites)
- [Running Tests](#running-tests)
- [Test Reports](#test-reports)

## Prerequisites

Before you begin, ensure you have met the following requirements:

- Java Development Kit (JDK) installed
- Apache Maven installed
- Web drivers for your target browsers (Chrome, Firefox, Edge)
- TestNG test framework
- IDE (e.g., IntelliJ IDEA or Eclipse) for test development

## Running Tests
Clone the project
Right click on test and run as TestNG test
Run from command line: mvn clean test -DsuiteXmlFile=src/test/testsuite/<testsuite>.xml

## Test Reports
Extent reports can be found under `target/reports/extent-report-yyyyMMdd_HHmmss.html`.

The `test-reporter` and `on-demand-test` workflows run the selected TestNG suite and upload its JUnit result for the `collect-test-history` workflow. The collector stores a small JSON record for every run on the data-only `test-history` branch, then publishes a GitHub Pages dashboard with suite, branch, and workflow filters. Its line chart shows the latest 50 matching runs; the dashboard includes the latest 1,000 records and the history branch retains all records.

To enable publishing, configure GitHub Pages to use **GitHub Actions** and allow the repository's `GITHUB_TOKEN` to write contents and deploy Pages. The collector creates `test-history` on its first successful run. The collector workflow must be merged to the default branch before GitHub will trigger it for completed test workflows.
