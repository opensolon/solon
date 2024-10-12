package org.noear.solon.net.http.impl;

import org.noear.solon.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author noear
 * @since 3.0
 */
public class HttpStream {
    private final String contentType;
    private InputStream content;

    private File file;

    public HttpStream(InputStream content, String type) {
        this.contentType = type;
        this.content = content;
    }

    public HttpStream(File file) {
        this.contentType = Utils.mime(file.getName());
        this.file = file;
    }

    public InputStream getContent() throws IOException {
        if (content == null) {
            content = new FileInputStream(file);
        }

        return content;
    }

    public long getContentLength() throws IOException {
        if (file != null) {
            return file.length();
        } else {
            return content.available();
        }
    }

    public String getContentType() {
        return contentType;
    }
}
