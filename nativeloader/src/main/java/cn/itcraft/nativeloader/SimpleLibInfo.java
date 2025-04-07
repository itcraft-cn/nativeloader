package cn.itcraft.nativeloader;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static cn.itcraft.nativeloader.NativeLoader.EXT;
import static cn.itcraft.nativeloader.NativeLoader.LIB_DEF;

/**
 * 简易实现
 * Simple implementation of LibInfo with automatic naming conventions
 *
 * @author Helly Guo
 * <p>
 * Created on 11/16/23 10:23 PM
 */
public final class SimpleLibInfo implements LibInfo {

    // Cache system properties for library paths (thread-safe)
    private static final Map<String, String> LIB_NAME_MAP = new ConcurrentHashMap<>();

    // Library base name (e.g. "mylib")
    private final String shortName;
    // Filename prefix (e.g. "libmylib")
    private final String prefix;
    // Full filename (e.g. "libmylib.so")
    private final String name;
    // Default JAR resource path
    private final String inJarPath;
    // Custom path from system property
    private final String libFilePath;

    /**
     * 根据短称自动生成标准库名称和路径
     *
     * @param shortName 库基础名称 (e.g. "mylib")
     */
    public SimpleLibInfo(String shortName) {
        this.shortName = shortName;
        // Follow common library naming convention
        this.prefix = "lib" + shortName;
        // Append platform-specific extension
        this.name = prefix + EXT;
        // Default location in JAR resources
        this.inJarPath = "resources/" + name;
        // Get custom path from system property
        this.libFilePath = getPropertyByName(shortName);
    }

    /**
     * 通过系统属性获取库路径（带缓存）
     * Get system property with caching mechanism
     *
     * @param name 属性名称 - System property key (e.g. "mylibLib")
     * @return 属性值或默认值 - Property value or LIB_DEF if not set
     */
    private static String getPropertyByName(String name) {
        return LIB_NAME_MAP.computeIfAbsent(name,
                                            // Property naming convention: [shortName]Lib
                                            n -> System.getProperty(n + "Lib", LIB_DEF)
                                           );
    }

    /**
     * 短称 - Base library identifier
     */
    @Override
    public String shortName() {
        return shortName;
    }

    /**
     * 文件名前缀 - Platform-independent filename prefix
     */
    @Override
    public String prefix() {
        return prefix;
    }

    /**
     * 完整文件名 - Platform-specific filename
     */
    @Override
    public String name() {
        return name;
    }

    /**
     * jar内路径 - Default resource path in JAR package
     */
    @Override
    public String jarPath() {
        return inJarPath;
    }

    /**
     * 文件系统路径 - Custom filesystem path from system property
     */
    @Override
    public String filePath() {
        return libFilePath;
    }
}
