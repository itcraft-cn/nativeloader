package cn.itcraft.nativeloader;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Helly Guo
 * <p>
 * Created on 2025-03-22 23:07
 */
class NativeLoaderTest {

    @Test
    public void test() {
        Assertions.assertThrows(Exception.class, () -> NativeLoader.load(new SimpleLibInfo("x")));
    }

}
