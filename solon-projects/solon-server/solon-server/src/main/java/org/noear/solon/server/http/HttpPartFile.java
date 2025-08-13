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
package org.noear.solon.server.http;

import org.noear.solon.Utils;
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
 */
public class HttpPartFile {
    private static Path tempdir;
    private File tempfile;
    private final InputStream content;
    private final long size;

    public HttpPartFile(String filename, InputStream ins) throws IOException {
        if (ServerProps.request_useTempfile && Utils.isNotEmpty(filename)) {
            if (tempdir == null) {
                Utils.locker().lock();
                try {
                    if (tempdir == null) {
                        tempdir = Files.createTempDirectory("solon.upload.");
                    }
                } finally {
                    Utils.locker().unlock();
                }
            }

            tempfile = Files.createTempFile(tempdir, "solon.", ".tmp").toFile();
            try (OutputStream outs = new BufferedOutputStream(new FileOutputStream(tempfile))) {
                IoUtil.transferTo(ins, outs);
            }

            size = tempfile.length();
            content = new FileInputStream(tempfile);
        } else {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            IoUtil.transferTo(ins, output);

            size = output.size();
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
    public long getSize() throws IOException {
        return size;
    }
}