package cn.itcraft.nativeloader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * learn from <a href="https://www.cnblogs.com/FlyingPuPu/p/7598098.html">load from jar</a><br>
 * 加载原生库
 *
 * @author Helly Guo
 * <p>
 * Created on 11/16/23 10:23 PM
 */
public class NativeLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(NativeLoader.class);

    /**
     * identify native library by system, may be need "os.arch"
     */
    static final String OS_NAME = System.getProperty("os.name");
    static final String EXT = (OS_NAME.toLowerCase().contains("win")) ? ".dll" : ".so";
    static final String LIB_DEF = "ENV_LIB_PARAM_NOT_EXIST";
    private static final String TMP_DIR = System.getProperty("java.io.tmpdir");

    /**
     * 加载库
     * 
     * @param libInfo 库信息
     */
    public static void load(LibInfo libInfo) {
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
     * 
     * @param name 全名
     * @param shortName 短名
     * @return 成功/失败
     * 
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
     * @param jarPath jar路径
     * @param prefix 文件名，不带后缀
     * @return 成功/失败
     */
    private static boolean loadFromJar(String name, String jarPath, String prefix) {
        LOGGER.debug("try load native library[{}] from classpath", jarPath);
        try (InputStream is
                     = Thread.currentThread().getContextClassLoader().getResourceAsStream(jarPath)) {
            if (is == null) {
                LOGGER.warn("try load lib[{}] in jar failed, {} is not found in classpath", name, jarPath);
                return false;
            }
            Path tmpDir = Paths.get(TMP_DIR);
            Path tmpLib = Files.createTempFile(tmpDir, prefix, EXT);
            tmpLib.toFile().deleteOnExit();
            Files.copy(is, tmpLib, StandardCopyOption.REPLACE_EXISTING);
            System.load(tmpLib.toAbsolutePath().toString());
            return true;
        } catch (UnsatisfiedLinkError | IOException thrown) {
            LOGGER.warn("failed to load native library[{}] from classpath: {}", jarPath, thrown.getMessage());
            return false;
        }
    }

    /**
     * 基于JVM特定配置加载
     * @param name 文件名
     * @param filePath 文件路径
     * @return 成功/失败
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
