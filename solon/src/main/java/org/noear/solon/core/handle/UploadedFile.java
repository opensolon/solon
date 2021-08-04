package org.noear.solon.core.handle;

import java.io.InputStream;

/**
 * @author noear
 * @since 1.5
 */
@Deprecated
public class UploadedFile extends MultipartFile {
    public UploadedFile() {
        super();
    }

    public UploadedFile(String contentType, long contentSize, InputStream content, String name, String extension) {
        super(contentType, contentSize, content, name, extension);
    }
}
