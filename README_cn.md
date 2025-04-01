# nativeloader

一个简单的本地库加载器，用于加载本地库。

[English Document](README.md)

## usage 1

使用 `SimpleLibInfo`

```java
try {
    NativeLoader.load(new SimpleLibInfo("x"));
} catch (Exception e) {
    LOGGER.warn(e.getMessage(), e);
    throw e;
}
```

`NativeLoader` 将尝试：

1. 尝试从系统路径加载库
2. 尝试从类路径加载库，并复制到临时目录
3. 尝试从给定路径(经过设置java环境)加载库

## usage 2

实现 `LibInfo`

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
