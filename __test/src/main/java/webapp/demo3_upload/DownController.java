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
package webapp.demo3_upload;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.server.util.OutputUtils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.DownloadedFile;
import org.noear.solon.core.util.ResourceUtil;
import reactor.core.publisher.Mono;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * @author noear 2021/2/10 created
 */
@Mapping("/demo3/down")
@Controller
public class DownController {
    @Mapping("f1")
    public DownloadedFile down1() {
        InputStream stream = new ByteArrayInputStream("{code:1}".getBytes(StandardCharsets.UTF_8));

        //使用 InputStream 实例化
        return new DownloadedFile("text/json", stream, "test.json");
    }

    @Mapping("f2")
    public DownloadedFile down2() {
        byte[] bytes = "test".getBytes(StandardCharsets.UTF_8);

        //使用 byte[] 实例化
        return new DownloadedFile("text/json", bytes, "test.txt");

    }

    @Mapping("f3")
    public File down3() {
        String filePath = ResourceUtil.getResource("WEB-INF/static/debug.htm").getFile();

        return new File(filePath);
    }

    @Mapping("f3_2")
    public DownloadedFile down3_2() throws Exception {
        String filePath = ResourceUtil.getResource("WEB-INF/static/debug.htm").getFile();

        File file = new File(filePath);
        return new DownloadedFile(file)
                .asAttachment(false)
                .cacheControl(360)
                .eTag(String.valueOf(file.lastModified()));
    }


    @Mapping("f4")
    public void down4(Context ctx) throws IOException {
        String filePath = ResourceUtil.getResource("WEB-INF/static/debug.htm").getFile();

        File file = new File(filePath);

        ctx.outputAsFile(file);
    }

    @Mapping("f4_2")
    public void down4_2(Context ctx) throws IOException {
        try (InputStream stream = ResourceUtil.getResource("WEB-INF/static/debug.htm").openStream()) {
            OutputUtils.global().outputStreamAsGzip(ctx, stream);
        }
    }

    @Mapping("f5")
    public Mono<DownloadedFile> down5() throws Exception {
        File file = new File("/Users/noear/Movies/down_test.mp4");
        return Mono.just(new DownloadedFile(file).asAttachment(false));
    }
}
