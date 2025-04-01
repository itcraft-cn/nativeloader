# nativeloader

a simple native loader for java

## usage 1

use `SimpleLibInfo`

```java
try {
    NativeLoader.load(new SimpleLibInfo("x"));
} catch (Exception e) {
    LOGGER.warn(e.getMessage(), e);
    throw e;
}
```

the `NativeLoader` will try to:

1. try to load the library from the system path
2. try to load the library from the classpath, and copy it to the tmp directory
3. try to load the library from given path by set in java env

## usage 2

implement `LibInfo`

```java
public class LibAInfo implements LibInfo {
    ...
}
```

```java
// usage
try{
    NativeLoader.load(new LibAInfo());
} catch (Exception e) {
    LOGGER.warn(e.getMessage(), e);
    throw e;
}
```
