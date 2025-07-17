package cn.itcraft.nativeloader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * learn from <a href="https://www.cnblogs.com/FlyingPuPu/p/7598098.html">load from jar</a><br>
 * 加载原生库
 * Native library loader with multi-source support (system path/JAR file/custom path)
 *
 * @author Helly Guo
 * <p>
 * Created on 11/16/23 10:23 PM
 */
public class NativeLoader {

    /**
     * identify native library by system, may be need "os.arch"
     * System properties for platform detection
     */
    static final String OS_NAME = System.getProperty("os.name");
    static final boolean OS_IS_WIN = (OS_NAME.toLowerCase().contains("win"));
    // File extension based on OS
    static final String EXT = OS_IS_WIN ? ".dll" : ".so";
    // Default marker for undefined library path
    static final String LIB_DEF = "ENV_LIB_PARAM_NOT_EXIST";
    // System temp directory
    private static final String TMP_DIR = System.getProperty("java.io.tmpdir");

    private static final Logger LOGGER = LoggerFactory.getLogger(NativeLoader.class);

    private static final Set<String> LIB_LOADED_SET = new ConcurrentSkipListSet<>();

    /**
     * 加载库
     * Load native library with fallback strategies
     *
     * @param libInfo 库信息 - Library metadata container
     */
    public static void load(LibInfo libInfo) {
        String name = libInfo.name();
        synchronized (LIB_LOADED_SET) {
            if (LIB_LOADED_SET.contains(name)) {
                LOGGER.debug("native library[{}] already loaded", name);
                return;
            }
            load0(libInfo);
            LIB_LOADED_SET.add(name);
        }
    }

    private static void load0(LibInfo libInfo) {
        String name = libInfo.name();
        if (loadFromSysLibPath(name, libInfo.shortName())
                || loadFromJar(name, libInfo.jarPath(), libInfo.prefix())
                || loadFromSysProperties(name, libInfo.filePath())) {
            LOGGER.info("load native library[{}] success", name);
        } else {
            throw new NativeLoadException("load native library[" + name + "] failed", new UnsatisfiedLinkError(name));
        }
    }

    /**
     * 基于系统环境变量加载
     * Try loading from system library path (java.library.path)
     *
     * @param name      全名 - Full library name with extension
     * @param shortName 短名 - Library base name without prefix/extension
     * @return 成功/失败 - Whether loading succeeded
     */
    private static boolean loadFromSysLibPath(String name, String shortName) {
        LOGGER.debug("try load native library[{}] from sys lib path", name);
        try {
            System.loadLibrary(shortName);
            return true;
        } catch (UnsatisfiedLinkError error) {
            LOGGER.warn("try load lib[{}] from sys lib path failed: {}", name, error.getMessage());
            return false;
        }
    }

    /**
     * 从jar加载
     * Extract and load library from JAR resources
     *
     * @param jarPath jar路径 - Resource path in JAR file
     * @param prefix  文件名，不带后缀 - Filename prefix without extension
     * @return 成功/失败 - Whether extraction and loading succeeded
     */
    private static boolean loadFromJar(String name, String jarPath, String prefix) {
        LOGGER.debug("try load native library[{}] from classpath", jarPath);
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(jarPath)) {
            if (is == null) {
                LOGGER.warn("try load lib[{}] in jar failed, {} is not found in classpath", name, jarPath);
                return false;
            }
            // Create temp file with proper naming
            Path tmpDir = Paths.get(TMP_DIR);
            Path tmpLib = Files.createTempFile(tmpDir, prefix, EXT);
            tmpLib.toFile().deleteOnExit();  // Ensure cleanup on JVM exit

            // Copy resource to temp file
            Files.copy(is, tmpLib, StandardCopyOption.REPLACE_EXISTING);

            // Load from temp path
            System.load(tmpLib.toAbsolutePath().toString());
            return true;
        } catch (UnsatisfiedLinkError | IOException thrown) {
            LOGGER.warn("failed to load native library[{}] from classpath: {}", jarPath, thrown.getMessage());
            return false;
        }
    }

    /**
     * 基于JVM特定配置加载
     * Load from filesystem path specified in system properties
     *
     * @param name     文件名 - Library filename for logging
     * @param filePath 文件路径 - Absolute filesystem path
     * @return 成功/失败 - Whether loading from custom path succeeded
     */
    private static boolean loadFromSysProperties(String name, String filePath) {
        if (LIB_DEF.equals(filePath)) {
            LOGGER.debug("skip loading native library[{}] from env", name);
            return false;
        }
        LOGGER.debug("try load native library[{}] from {}", name, filePath);
        try {
            System.load(filePath);
            return true;
        } catch (UnsatisfiedLinkError error) {
            LOGGER.warn("failed to load native library[{}] from {}: {}", name, filePath, error.getMessage());
            return false;
        }
    }
}
