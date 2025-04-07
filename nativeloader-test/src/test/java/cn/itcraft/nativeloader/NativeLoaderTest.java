package cn.itcraft.nativeloader;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Helly Guo
 * <p>
 * Created on 2025-03-22 23:07
 */
class NativeLoaderTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(NativeLoader.class);

    @Test
    public void test() {
        Assertions.assertThrows(Exception.class, () -> testThrow());
    }

    private void testThrow() throws Exception {
        try {
            NativeLoader.load(new SimpleLibInfo("x"));
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
            throw e;
        }
    }

}
