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

## usage 2

implement `LibInfo`

```java
public class LibAInfo implements LibInfo {
    ...
}

        // usage
        try{
            NativeLoader.load(new LibAInfo());
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
            throw e;
        }
```
