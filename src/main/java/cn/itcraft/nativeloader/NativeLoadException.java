package cn.itcraft.nativeloader;

/**
 * @author Helly Guo
 * <p>
 * Created on 11/16/23 10:23 PM
 */
class NativeLoadException extends RuntimeException {
    public NativeLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
