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
package features;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;
import org.noear.solon.net.http.HttpResponse;
import org.noear.solon.test.HttpTester;
import org.noear.solon.test.SolonTest;
import webapp.App;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author noear 2022/3/22 created
 */
@SolonTest(App.class)
public class HttpUploadBigTest extends HttpTester {
    @Test
    public void upload() throws IOException {
        StringBuilder buf = new StringBuilder();
        while (buf.length() < 1024 * 1024 * 20) {
            buf.append("test:1234567890abcdefg;");
        }

        InputStream inputStream = new ByteArrayInputStream(buf.toString().getBytes(StandardCharsets.UTF_8));

        HttpResponse resp = path("/demo3/upload/f1")
                .data("file", "装修-水电-视频.mp4", inputStream, "video/mp4")
                .exec("POST");

        int code = resp.code();
        String json = resp.bodyAsString();

        Assertions.assertEquals(200, resp.code());
        assert json.contains("装修-水电-视频.mp4");

        ONode oNode = ONode.ofJson(json);
        Assertions.assertEquals(buf.length(), oNode.get("file").get("size").getLong());
    }
}