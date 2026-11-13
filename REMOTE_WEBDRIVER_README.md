# Remote WebDriver Setup Guide

## Overview
The `WebDriverManager` now intelligently switches between local and remote WebDriver based on system properties. No code changes needed—just configure system properties to toggle between local and remote execution.

## System Properties

### `remote` - Enable/Disable Remote WebDriver
- **Default**: `false` (uses local WebDriver)
- **Values**: `true` | `false`
- Controls whether WebDriverManager uses RemoteWebDriver or local WebDriver

### `remote.url` - Remote Grid URL
- **Default**: `http://localhost:4444` (default Selenium Grid)
- **Values**: Any valid URL
- Ignored if `remote=false`
- Examples:
  - `http://localhost:4444` - Local Selenium Grid
  - `https://hub.browserstack.com/wd/hub` - BrowserStack
  - `http://192.168.1.100:4444` - Remote Grid Server

## Usage

### 1. Local WebDriver (Default)
No configuration needed—uses local WebDriver by default:

```bash
# Run test (uses local WebDriver)
mvn test
```

```java
// Code is the same regardless
WebDriver driver = WebDriverManager.getDriver(BrowserType.CHROME);
driver.navigate().to("https://www.example.com");
WebDriverManager.quitDriver();
```

### 2. Local Selenium Grid

**Setup Grid:**
```bash
docker run -d -p 4444:4444 -p 7900:7900 --shm-size=2gb selenium/standalone-chrome:latest
```

**Run Tests:**
```bash
# Option 1: Use default grid URL (http://localhost:4444)
mvn test -Dremote=true

# Option 2: Specify custom grid URL
mvn test -Dremote=true -D"remote.url=http://your-server:4444"
```

### 3. BrowserStack

**Setup Environment Variables:**
```bash
export BROWSERSTACK_USERNAME=your_username
export BROWSERSTACK_ACCESS_KEY=your_access_key

# Or create a custom URL directly
export BROWSERSTACK_URL="https://username:accesskey@hub.browserstack.com/wd/hub"
```

**Run Tests:**
```bash
mvn test -Dremote=true -D"remote.url=https://username:accesskey@hub.browserstack.com/wd/hub"
```

## How It Works

```
WebDriverManager.getDriver(BrowserType.CHROME)
  ↓
Check System Property: remote
  ├─ If true → DriverFactory.createRemoteDriver(url from remote.url property)
  └─ If false/not set → DriverFactory.createDriver()
```

## Examples

### Example 1: Toggle Between Local and Remote with Same Code

```java
// No code change needed!
WebDriver driver = WebDriverManager.getDriver(BrowserType.CHROME);

// With system properties:
// -Dremote=false  → Uses local WebDriver
// -Dremote=true   → Uses RemoteWebDriver at http://localhost:4444
// -Dremote=true -D"remote.url=..." → Uses RemoteWebDriver at specified URL
```

### Example 2: Different Environments

```bash
# Development - Local WebDriver
mvn test

# Testing - Local Grid
mvn test -Dremote=true

# Production - BrowserStack
mvn test -Dremote=true -D"remote.url=https://user:key@hub.browserstack.com/wd/hub"
```

### Example 3: Maven Profile Configuration

Add to `pom.xml`:
```xml
<profiles>
    <profile>
        <id>local</id>
        <activation><activeByDefault>true</activeByDefault></activation>
        <properties>
            <remote>false</remote>
        </properties>
    </profile>
    <profile>
        <id>grid</id>
        <properties>
            <remote>true</remote>
            <remote.url>http://localhost:4444</remote.url>
        </properties>
    </profile>
    <profile>
        <id>browserstack</id>
        <properties>
            <remote>true</remote>
            <remote.url>https://user:key@hub.browserstack.com/wd/hub</remote.url>
        </properties>
    </profile>
</profiles>
```

Run with profile:
```bash
mvn test -P grid     # Uses Selenium Grid
mvn test -P browserstack  # Uses BrowserStack
mvn test -P local    # Uses local WebDriver
```

## Supported Browsers
- CHROME
- FIREFOX
- EDGE

## Configuration Priority

1. System Property `-Dremote=true/false`
2. System Property `-D"remote.url=..."`
3. Defaults:
   - `remote` → `false`
   - `remote.url` → `http://localhost:4444`

## Error Handling

```java
try {
    WebDriver driver = WebDriverManager.getDriver(BrowserType.CHROME);
    // Use driver
} catch (RuntimeException e) {
    System.err.println("Invalid URL: " + e.getMessage());
} catch (Exception e) {
    System.err.println("Connection failed: " + e.getMessage());
} finally {
    WebDriverManager.quitDriver();
}
```

Common errors:
- **"Connection refused"** → Grid is not running
- **"Invalid URL"** → Check `remote.url` format
- **"Authentication failed"** → Check BrowserStack credentials

## Architecture

```
WebDriverManager.getDriver()
  ↓
Read System Properties (remote, remote.url)
  ↓
Decision: Remote or Local?
  ├─ Remote → DriverFactory.createRemoteDriver()
  └─ Local → DriverFactory.createDriver()
  ↓
Store in ThreadLocal
  ↓
Return WebDriver
```

## Best Practices

1. **Use Maven profiles** for environment-specific configuration
2. **Thread-safe**: WebDriverManager uses ThreadLocal for each test thread
3. **Always cleanup**: Call `WebDriverManager.quitDriver()` in `@After` or `finally`
4. **No code changes**: Toggle remote execution via system properties only
5. **Default to local**: Development uses local WebDriver by default

## CI/CD Integration

### GitHub Actions
```yaml
- name: Run tests locally
  run: mvn test

- name: Run tests on Grid
  run: mvn test -Dremote=true

- name: Run tests on BrowserStack
  env:
    BROWSERSTACK_URL: https://user:key@hub.browserstack.com/wd/hub
  run: mvn test -Dremote=true -D"remote.url=${{ env.BROWSERSTACK_URL }}"
```

### Jenkins
```groovy
stage('Test') {
    steps {
        sh "mvn test -Dremote=${REMOTE_FLAG} -D'remote.url=${GRID_URL}'"
    }
}
```

## Troubleshooting

| Issue | Solution |
|-------|----------|
| "Connection refused" | Start Selenium Grid: `docker run -d -p 4444:4444 selenium/standalone-chrome` |
| Tests use local instead of remote | Check: `mvn test -Dremote=true` (not `-Dremote="true"` or `-Dremote=True`) |
| BrowserStack auth fails | Verify username:accesskey in URL is correct |
| Tests hang | Increase timeout or check grid is responsive |

## Running Sample Test

```bash
# Local (default)
mvn test -Dtest=RemoteWebDriverTest

# Remote Grid
mvn test -Dtest=RemoteWebDriverTest -Dremote=true

# BrowserStack
mvn test -Dtest=RemoteWebDriverTest -Dremote=true -D"remote.url=https://user:key@hub.browserstack.com/wd/hub"
```

## Summary

| Scenario | Command |
|----------|---------|
| Local WebDriver | `mvn test` |
| Selenium Grid | `mvn test -Dremote=true` |
| Remote Server | `mvn test -Dremote=true -D"remote.url=http://server:4444"` |
| BrowserStack | `mvn test -Dremote=true -D"remote.url=https://user:key@hub.browserstack.com/wd/hub"` |

