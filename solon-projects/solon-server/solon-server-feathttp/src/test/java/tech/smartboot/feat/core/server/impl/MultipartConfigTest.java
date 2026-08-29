package tech.smartboot.feat.core.server.impl;

import org.junit.jupiter.api.Test;
import tech.smartboot.feat.core.common.multipart.MultipartConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MultipartConfigTest {
    @Test
    public void standardFields() {
        MultipartConfig config = new MultipartConfig(null, 1024, 4096);

        assertEquals("", config.getLocation());
        assertEquals(1024, config.getMaxFileSize());
        assertEquals(4096, config.getMaxRequestSize());
    }
}
