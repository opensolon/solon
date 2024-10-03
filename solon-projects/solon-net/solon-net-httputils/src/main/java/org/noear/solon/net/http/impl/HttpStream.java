package org.noear.solon.net.http.impl;

import java.io.InputStream;

/**
 * @author noear
 * @since 3.0
 */
public class HttpStream {
    public final InputStream content;
    public final String contentType;

    public HttpStream(InputStream content, String contentType) {
        this.content = content;
        this.contentType = contentType;
    }
}
