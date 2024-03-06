package org.noear.solon.boot.http;

import org.noear.solon.Utils;
import org.noear.solon.boot.ServerProps;
import org.noear.solon.core.util.IoUtil;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 临时文件
 *
 * @author noear
 * @since 2.7
 */
public class HttpPartFile {
    private static Path tempdir;
    private File tempfile;
    private InputStream content;

    public HttpPartFile(InputStream ins) throws IOException {
        if (ServerProps.request_useTempfile && ins.available() > 0) {
            if (tempdir == null) {
                Utils.locker().lock();
                try {
                    if (tempdir == null) {
                        tempdir = Files.createTempDirectory("solon.");
                    }
                } finally {
                    Utils.locker().unlock();
                }
            }

            tempfile = Files.createTempFile(tempdir, "solon.", ".tmp").toFile();
            try (OutputStream outs = new BufferedOutputStream(new FileOutputStream(tempfile))) {
                IoUtil.transferTo(ins, outs);
                outs.flush();
            }

            content = new FileInputStream(tempfile);
        } else {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            IoUtil.transferTo(ins, output);

            content = new ByteArrayInputStream(output.toByteArray());
        }
    }

    /**
     * 删除
     */
    public void delete() throws IOException {
        if (tempfile != null) {
            try {
                content.close();
            } finally {
                tempfile.delete();
            }
        }
    }

    /**
     * 获取内容
     */
    public InputStream getContent() throws IOException {
        return content;
    }

    /**
     * 获取大小
     */
    public int getSize() throws IOException {
        return content.available();
    }
}