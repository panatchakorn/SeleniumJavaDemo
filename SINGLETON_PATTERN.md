# ConfigReader Singleton Pattern

## Overview

ConfigReader now uses the **Singleton Pattern** to ensure:
- ✅ Configuration loads **only once**
- ✅ **Global access** from anywhere in the application
- ✅ **Thread-safe** initialization with double-checked locking
- ✅ **Memory efficient** - single instance in memory

## What is a Singleton?

A Singleton is a design pattern that ensures a class has only **one instance** and provides a **global point of access** to it.

### Before (Multiple Instances)
```java
// Each class creates its own instance
BaseTest:          new ConfigReader() → Loads config
WebDriverManager:  new ConfigReader() → Loads config AGAIN
Utility Class:     new ConfigReader() → Loads config AGAIN

Result: Configuration loaded 3+ times (wasteful)
```

### After (Single Instance)
```java
// All classes use the same instance
BaseTest:          ConfigReader.getInstance() → Singleton
WebDriverManager:  ConfigReader.getInstance() → Same singleton
Utility Class:     ConfigReader.getInstance() → Same singleton

Result: Configuration loaded once, shared globally
```

## Implementation: Double-Checked Locking

ConfigReader uses **double-checked locking** for efficient, thread-safe singleton initialization:

```java
public class ConfigReader {
    private static volatile ConfigReader instance;
    
    private ConfigReader() {
        // Private constructor - prevents direct instantiation
    }
    
    public static ConfigReader getInstance() {
        if (instance == null) {                           // 1st check (no lock)
            synchronized (ConfigReader.class) {
                if (instance == null) {                   // 2nd check (with lock)
                    instance = new ConfigReader();
                }
            }
        }
        return instance;
    }
}
```

### Why Double-Checked Locking?

| Aspect | Single Check | Synchronized | Double-Checked | 
|--------|------------|------------|---|
| Thread-safe | ❌ No | ✅ Yes | ✅ Yes |
| Initialization | Every time | Once | Once |
| Lock contention | None | Always | First call only |
| After 1st call | No overhead | Lock overhead | No overhead |
| **Recommended** | | | ✅ YES |

---

## Usage

### Access ConfigReader from Anywhere

```java
// No need to store a reference - just call getInstance()
ConfigReader config = ConfigReader.getInstance();
String browser = config.getConfigKey("browser");
boolean isRemote = config.getConfigKeyAsBoolean("remote");
```

### In Different Classes

**BaseTest:**
```java
@BeforeMethod
public void beforeMethod(Method method) {
    // Directly use getInstance() without declaring a field
    driver = WebDriverManager.getDriver(
        BrowserType.valueOf(ConfigReader.getInstance().getConfigKey("browser").toUpperCase())
    );
}
```

**WebDriverManager:**
```java
public static WebDriver getDriver(BrowserType browserType) {
    ConfigReader configReader = ConfigReader.getInstance();
    boolean isRemote = configReader.getConfigKeyAsBoolean("remote", false);
    // ... rest of logic
}
```

**Utility Classes:**
```java
public class UrlBuilder {
    public static String buildTestUrl() {
        return ConfigReader.getInstance().getConfigKey("url");
    }
}
```

---

## Key Features

### 1. Global Access
```java
// No need to pass as parameter - available everywhere
String url = ConfigReader.getInstance().getConfigKey("url");
```

### 2. Single Instance
```java
ConfigReader c1 = ConfigReader.getInstance();
ConfigReader c2 = ConfigReader.getInstance();
assert c1 == c2; // Same object in memory ✓
```

### 3. Thread-Safe
```java
// Safe for concurrent access from multiple test threads
Thread t1 = new Thread(() -> ConfigReader.getInstance());
Thread t2 = new Thread(() -> ConfigReader.getInstance());
t1.start();
t2.start();
// Both threads get same instance safely
```

### 4. Lazy Initialization
```java
// Loads only when first getInstance() is called
// If ConfigReader never used, configuration never loaded
ConfigReader config = ConfigReader.getInstance(); // Loads here
```

### 5. Cached Configuration
```java
// After first load, all subsequent accesses use cache
for (int i = 0; i < 1000; i++) {
    String value = ConfigReader.getInstance().getConfigKey("browser");
    // No file I/O - all from cached HashMap
}
```

---

## API Reference

### getInstance()
Returns the singleton instance of ConfigReader.

```java
ConfigReader config = ConfigReader.getInstance();
```

**Thread-safe:** Yes (double-checked locking)  
**Lazy initialized:** Yes  
**Return:** Singleton ConfigReader instance

### getConfigKey(String key)
Retrieves a configuration value as String, returns empty string if not found.

```java
String browser = ConfigReader.getInstance().getConfigKey("browser");
```

### getConfigKey(String key, String defaultValue)
Retrieves a configuration value with a default fallback.

```java
String url = ConfigReader.getInstance().getConfigKey("url", "https://example.com");
```

### getConfigKeyAsBoolean(String key)
Retrieves a configuration value as boolean.

```java
boolean isRemote = ConfigReader.getInstance().getConfigKeyAsBoolean("remote");
```

### getConfigKeyAsBoolean(String key, boolean defaultValue)
Retrieves a configuration value as boolean with default.

```java
boolean isRemote = ConfigReader.getInstance().getConfigKeyAsBoolean("remote", false);
```

### getAllConfig()
Returns all loaded configuration as a Map.

```java
Map<String, String> allConfig = ConfigReader.getInstance().getAllConfig();
```

---

## Thread Safety Analysis

### Scenario 1: Two Threads Calling getInstance() Simultaneously

```
Time  | Thread 1                    | Thread 2
------|-----------------------------|-----------------------
T0    | getInstance()               | getInstance()
T1    | if(instance==null) → TRUE   | (blocked)
T2    | synchronized...             | if(instance==null) → TRUE
T3    | if(instance==null) → TRUE   | (blocked)
T4    | instance = new ConfigReader | (blocked)
T5    | exit synchronized           | synchronized...
T6    | return instance             | if(instance==null) → FALSE
T7    |                             | return instance
      |                             | (same object as Thread 1)
```

✅ Both threads safely receive the same instance

### Scenario 2: Concurrent Calls After Initialization

```
Time  | Thread 1              | Thread 2              | Thread 3
------|----------------------|----------------------|----------------------
T0    | getInstance()        |                      |
T1    | if(instance!=null)   |                      |
T2    | → TRUE (return)      |                      |
T3    |                      | getInstance()        |
T4    |                      | if(instance!=null)   |
T5    |                      | → TRUE (return)      |
T6    |                      |                      | getInstance()
T7    |                      |                      | if(instance!=null)
T8    |                      |                      | → TRUE (return)
```

✅ No synchronization overhead after first initialization

---

## Migration Guide

### Before (Multiple Instances)
```java
// Old code - each class creates instance
public class BaseTest {
    static final ConfigReader configReader = new ConfigReader();
    
    public void test() {
        String browser = configReader.getConfigKey("browser");
    }
}

public class WebDriverManager {
    private static final ConfigReader configReader = new ConfigReader();
    
    public static WebDriver getDriver(BrowserType type) {
        boolean remote = configReader.getConfigKeyAsBoolean("remote");
        // ...
    }
}
```

### After (Singleton)
```java
// New code - all classes use singleton
public class BaseTest {
    // No ConfigReader field needed!
    
    public void test() {
        String browser = ConfigReader.getInstance().getConfigKey("browser");
    }
}

public class WebDriverManager {
    // No static ConfigReader field needed!
    
    public static WebDriver getDriver(BrowserType type) {
        boolean remote = ConfigReader.getInstance().getConfigKeyAsBoolean("remote");
        // ...
    }
}
```

---

## Testing

### Singleton Tests Included
File: `src/test/java/testcase/SingletonConfigReaderTest.java`

Tests verify:
1. ✅ Same instance returned on multiple calls
2. ✅ Configuration loads only once
3. ✅ Accessible from anywhere
4. ✅ Thread-safe concurrent access
5. ✅ Default values work correctly
6. ✅ Configuration is cached
7. ✅ Integration with existing code

Run tests:
```bash
mvn clean compile
mvn test -Dtest=SingletonConfigReaderTest
```

---

## Performance Benefits

### Memory Usage
- **Before:** 3+ ConfigReader instances × HashMap ≈ 3X memory
- **After:** 1 ConfigReader instance × HashMap ≈ X memory
- **Savings:** ~66% memory reduction

### Startup Time
- **Before:** Configuration file read 3+ times
- **After:** Configuration file read 1 time
- **Savings:** ~66% faster startup

### Runtime Performance
After first initialization, all calls are lock-free:
```java
// No synchronization, no file I/O, just HashMap lookup
String value = ConfigReader.getInstance().getConfigKey("browser");
```

---

## Best Practices

### 1. Always Use getInstance()
```java
// ✅ CORRECT
ConfigReader config = ConfigReader.getInstance();

// ❌ WRONG - Constructor is private
ConfigReader config = new ConfigReader(); // Compile error
```

### 2. Cache for Multiple Accesses
```java
// ✅ EFFICIENT
ConfigReader config = ConfigReader.getInstance();
String browser = config.getConfigKey("browser");
String url = config.getConfigKey("url");
String project = config.getConfigKey("projectName");

// ❌ INEFFICIENT (still works but redundant)
String browser = ConfigReader.getInstance().getConfigKey("browser");
String url = ConfigReader.getInstance().getConfigKey("url");
String project = ConfigReader.getInstance().getConfigKey("projectName");
```

### 3. Use Defaults
```java
// ✅ GOOD - Provides fallback
String url = ConfigReader.getInstance().getConfigKey("url", "https://default.com");

// ⚠️ RISKY - Empty string if not found
String url = ConfigReader.getInstance().getConfigKey("url");
```

### 4. Use Typed Accessors
```java
// ✅ CORRECT
boolean isRemote = ConfigReader.getInstance().getConfigKeyAsBoolean("remote", false);

// ❌ ERROR-PRONE - Manual parsing
boolean isRemote = Boolean.parseBoolean(ConfigReader.getInstance().getConfigKey("remote"));
```

---

## Common Questions

### Q: Is singleton thread-safe?
**A:** Yes! Double-checked locking ensures thread-safe initialization even with concurrent access.

### Q: When is configuration loaded?
**A:** Lazy - loaded only on first `getInstance()` call, not on application startup.

### Q: Can I have multiple instances?
**A:** No - constructor is private. This is the design pattern of Singleton.

### Q: What if I need to reload configuration?
**A:** Current implementation doesn't support dynamic reload. For advanced scenarios, contact the development team.

### Q: Is it safe in multi-threaded tests?
**A:** Yes! Each test thread gets the same singleton instance safely.

### Q: How do I use it in new code?
**A:** Simply call `ConfigReader.getInstance()` anywhere you need configuration.

---

## Summary Table

| Feature | Implementation |
|---------|-----------------|
| **Pattern** | Singleton with double-checked locking |
| **Initialization** | Lazy (on first getInstance() call) |
| **Thread-safe** | Yes (double-checked locking) |
| **Memory** | Single instance + HashMap cache |
| **Access** | ConfigReader.getInstance() |
| **Constructor** | Private (enforces singleton) |
| **Performance** | Lock-free after initialization |
| **Caching** | Built-in (HashMap) |

---

## Files Modified

1. **ConfigReader.java**
   - Added getInstance() method
   - Made constructor private
   - Added volatile instance variable

2. **BaseTest.java**
   - Removed static ConfigReader field
   - Updated to use ConfigReader.getInstance()

3. **WebDriverManager.java**
   - Removed static ConfigReader field
   - Updated to use ConfigReader.getInstance()

---

## Files Created

1. **SingletonConfigReaderTest.java**
   - Tests singleton instance behavior
   - Tests thread-safety
   - Tests caching
   - Tests integration

---

## Next Steps

1. ✅ Singleton pattern implemented
2. ✅ All classes updated
3. ✅ Tests created
4. ✅ Documentation complete
5. Ready to use!

Access ConfigReader from anywhere:
```java
ConfigReader.getInstance().getConfigKey("any.key");
```

No more instantiation needed! 🎉
