package org.noear.solon.net.http.impl;

/**
 * @author noear
 * @since 3.0
 */
public class HttpUploadFile {
    public final String fileName;
    public final HttpStream fileStream;

    public HttpUploadFile(String fileName, HttpStream fileStream) {
        this.fileName = fileName;
        this.fileStream = fileStream;
    }
}
