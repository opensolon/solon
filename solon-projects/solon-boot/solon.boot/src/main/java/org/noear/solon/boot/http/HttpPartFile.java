package org.noear.solon.boot.http;

import org.noear.solon.Utils;
import org.noear.solon.boot.ServerProps;
import org.noear.solon.core.util.IoUtil;

import java.io.*;
import java.nio.file.Files;

/**
 * 临时文件
 *
 * @author noear
 * @since 2.7
 */
public class HttpPartFile {
    private File tmpfile;
    private InputStream inputStream;

    public HttpPartFile(InputStream ins) throws IOException {
        if (ServerProps.request_useTempfile) {
            tmpfile = Files.createTempFile(Utils.guid(), ".tmp").toFile();
            try (OutputStream outs = new FileOutputStream(tmpfile)) {
                IoUtil.transferTo(ins, outs);
            }

            inputStream = new FileInputStream(tmpfile);
        } else {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            IoUtil.transferTo(ins, output);

            inputStream = new ByteArrayInputStream(output.toByteArray());
        }
    }

    /**
     * 删除
     */
    public void delete() throws IOException {
        if (tmpfile != null) {
            tmpfile.delete();
        }
    }

    /**
     * 获取内容
     */
    public InputStream getContent() throws IOException {
        return inputStream;
    }

    /**
     * 获取大小
     */
    public int getSize() throws IOException {
        return inputStream.available();
    }
}
