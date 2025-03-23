package cn.itcraft.nativeloader;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static cn.itcraft.nativeloader.NativeLoader.EXT;
import static cn.itcraft.nativeloader.NativeLoader.LIB_DEF;

/**
 * 简易实现
 * 
 * @author Helly Guo
 * <p>
 * Created on 11/16/23 10:23 PM
 */
public final class SimpleLibInfo implements LibInfo {

    private static final Map<String, String> LIB_NAME_MAP = new ConcurrentHashMap<>();

    private final String shortName;
    private final String prefix;
    private final String name;
    private final String inJarPath;
    private final String libFilePath;

    public SimpleLibInfo(String shortName) {
        this.shortName = shortName;
        this.prefix = "lib" + shortName;
        this.name = prefix + EXT;
        this.inJarPath = "resources/" + name;
        this.libFilePath = getPropertyByName(shortName);
    }

    @Override
    public String shortName() {
        return shortName;
    }

    @Override
    public String prefix() {
        return prefix;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String jarPath() {
        return inJarPath;
    }

    @Override
    public String filePath() {
        return libFilePath;
    }

    private static String getPropertyByName(String name) {
        return LIB_NAME_MAP.computeIfAbsent(name, n -> System.getProperty(n + "Lib", LIB_DEF));
    }
}
