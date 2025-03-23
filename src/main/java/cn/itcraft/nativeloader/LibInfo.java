package cn.itcraft.nativeloader;

/**
 * 库信息
 * 
 * @author Helly Guo
 * <p>
 * Created on 11/16/23 10:23 PM
 */
public interface LibInfo {

    /**
     * 短称
     */
    String shortName();

    /**
     * 文件名，无文件名后缀
     */
    String prefix();

    /**
     * 完整文件名，带文件名后缀
     */
    String name();

    /**
     * jar内路径
     */
    String jarPath();

    /**
     * 文件系统内路径
     */
    String filePath();

}
