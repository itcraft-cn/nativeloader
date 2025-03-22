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
 * learn from <a href="https://www.cnblogs.com/FlyingPuPu/p/7598098.html">load from jar</a><br/>
 *
 * @author Helly Guo
 * <p>
 * Created on 11/16/23 10:23 PM
 */
public class NativeLoader {

    /**
     * identify native library by system, may be need "os.arch"
     */
    static final String OS_NAME = System.getProperty("os.name");
    static final String EXT = (OS_NAME.toLowerCase().contains("win")) ? ".dll" : ".so";
    static final String LIB_DEF = "ENV_LIB_PARAM_NOT_EXIST";
    private static final Logger LOGGER = LoggerFactory.getLogger(NativeLoader.class);
    private static final String TMP_DIR = System.getProperty("java.io.tmpdir");

    public static void load(LibInfo libInfo) {
        String name = libInfo.name();
        if (loadFromSysLibPath(name, libInfo.shortName())
                || loadFromSysProperties(name, libInfo.filePath())
                || loadFromJar(libInfo.jarPath(), libInfo.prefix())) {
            LOGGER.debug("load native library[{}] success", name);
        } else {
            LOGGER.warn("load native library[{}] failed", name);
            throw new NativeLoadException("load native library failed",
                                          new UnsatisfiedLinkError(name));
        }
    }

    private static boolean loadFromSysLibPath(String name, String shortName) {
        LOGGER.debug("try load native library[{}] from sys lib path", name);
        try {
            System.loadLibrary(shortName);
            return true;
        } catch (UnsatisfiedLinkError error) {
            LOGGER.debug("try load lib from sys lib path failed: {}", error.getMessage());
            return false;
        }
    }

    private static boolean loadFromJar(String jarPath, String prefix) {
        LOGGER.debug("try load native library[{}] from classpath", jarPath);
        try (InputStream is
                     = Thread.currentThread().getContextClassLoader().getResourceAsStream(jarPath)) {
            if (is == null) {
                throw new RuntimeException(jarPath + " is not found in classpath");
            }
            Path tmpDir = Paths.get(TMP_DIR);
            Path tmpLib = Files.createTempFile(tmpDir, prefix, EXT);
            tmpLib.toFile().deleteOnExit();
            Files.copy(is, tmpLib, StandardCopyOption.REPLACE_EXISTING);
            System.load(tmpLib.toAbsolutePath().toString());
            return true;
        } catch (UnsatisfiedLinkError | IOException thrown) {
            LOGGER.debug("failed to load native library[{}] from classpath: {}", jarPath, thrown.getMessage());
            return false;
        }
    }

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
            LOGGER.debug("failed to load native library[{}] from {}: {}", name, filePath, error.getMessage());
            return false;
        }
    }
}
