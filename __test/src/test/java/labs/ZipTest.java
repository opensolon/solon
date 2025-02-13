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
package labs;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.DownloadedFile;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author noear 2023/2/15 created
 */
public class ZipTest {
    public void test(Context ctx) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        ctx.headerSet("Content-Disposition", "attachment; filename=\"test.json.zip\"");
        ctx.contentType("application/zip");

        //创建压缩包
        ZipOutputStream zipOutputStream = new ZipOutputStream(ctx.outputStream());


        //压缩包里创建一个空文件
        zipOutputStream.putNextEntry(new ZipEntry("test.json"));

        byte[] bytes = "{code:1}".getBytes();

        //写入压缩文件
        zipOutputStream.write(bytes, 0, bytes.length);
        //关闭压缩包打包
        zipOutputStream.closeEntry();

        zipOutputStream.flush();
        zipOutputStream.close();

        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());


        DownloadedFile downloadedFile = new org.noear.solon.core.handle.DownloadedFile("zip", inputStream, "test.json.zip");

        ctx.outputAsFile(downloadedFile);
    }
}
