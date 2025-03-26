package cn.itcraft.nativeloader;

/**
 * 库信息
 * Native library metadata information
 *
 * @author Helly Guo
 * <p>
 * Created on 11/16/23 10:23 PM
 */
public interface LibInfo {

    /**
     * 短称
     * Short identifier for the library (e.g. used in logs/configurations)
     */
    String shortName();

    /**
     * 文件名，无文件名后缀
     * Base filename without platform-specific extension (e.g. "libmylib" when full name is "libmylib.so")
     */
    String prefix();

    /**
     * 完整文件名，带文件名后缀
     * Full filename with platform-specific extension (e.g. "libmylib.so" on Linux)
     */
    String name();

    /**
     * jar内路径
     * Relative path within JAR file (e.g. "/native/linux-x86_64/libmylib.so")
     */
    String jarPath();

    /**
     * 文件系统内路径
     * Absolute filesystem path after extraction (e.g. "/tmp/nativeloader/libmylib.so")
     */
    String filePath();

}
