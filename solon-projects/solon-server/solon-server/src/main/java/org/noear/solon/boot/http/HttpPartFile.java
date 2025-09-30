/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.boot.http;

import org.noear.solon.server.ServerProps;
import org.noear.solon.core.util.IoUtil;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 临时文件
 *
 * @author noear
 * @since 2.7
 * @deprecated 3.5
 * */
@Deprecated
public class HttpPartFile {
    private File tempfile;
    private InputStream content;
    private final long size;
    private final String filename;

    public HttpPartFile(String filename, InputStream ins) throws IOException {
        this.filename = filename;

        ByteArrayOutputStream thresholdBuffer = new ByteArrayOutputStream();
        if (ServerProps.request_fileSizeThreshold > 0) {
            IoUtil.transferTo(ins, thresholdBuffer, 0, ServerProps.request_fileSizeThreshold);
        }

        if (thresholdBuffer.size() < ServerProps.request_fileSizeThreshold) {
            //说明没到阀值
            size = thresholdBuffer.size();
            content = new ByteArrayInputStream(thresholdBuffer.toByteArray());
        } else {
            //说明到阀值
            Path tempdir = ServerProps.request_tempDir.toPath();

            tempfile = Files.createTempFile(tempdir, "solon.", ".tmp").toFile();
            try (OutputStream outs = new BufferedOutputStream(new FileOutputStream(tempfile))) {
                //转入缓冲
                outs.write(thresholdBuffer.toByteArray());
                //转入剩余部分
                IoUtil.transferTo(ins, outs);
            }

            size = tempfile.length();
            content = new FileInputStream(tempfile);
        }
    }

    /**
     * 删除
     */
    public void delete() throws IOException {
        if (content != null) {
            try {
                content.close();
            } catch (Exception ignore) {

            }

            content = null;
        }

        if (tempfile != null) {
            try {
                tempfile.delete();
            } catch (Exception ignore) {

            }

            tempfile = null;
        }
    }

    /**
     * 获取文件名
     */
    public String getFilename() {
        return filename;
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
    public long getSize() throws IOException {
        return size;
    }
}