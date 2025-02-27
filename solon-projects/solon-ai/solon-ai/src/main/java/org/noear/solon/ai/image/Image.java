package org.noear.solon.ai.image;

import java.util.Base64;

/**
 * @author noear 2025/2/27 created
 */
public class Image {
    private String url;

    public Image(String url) {
        this.url = url;
    }

    public Image(byte[] data) {
        this.url = "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(data);
    }

    public String getUrl() {
        return url;
    }
}
