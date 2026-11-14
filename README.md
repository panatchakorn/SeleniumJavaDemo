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
Extent reports can be found on ../target/reports/extent-report-yyyyMMdd_HHmmss.html

The `test-reporter` GitHub Actions workflow publishes a test-history dashboard to GitHub Pages. It shows passed, failed, and skipped counts for each run, with links to the corresponding workflow run and its downloadable reports. Configure GitHub Pages to use **GitHub Actions**, then run the workflow from the Actions tab. Each deployment carries forward the history from the previously published dashboard.
