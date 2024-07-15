/*
 * Copyright 2017-2024 noear.org and authors
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

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.noear.solon.test.HttpTester;

import java.io.IOException;

/**
 * @author noear 2023/2/14 created
 */
public class MockWebTest extends HttpTester {
    public final static String EXPECTED = "{\"status\": \"ok\"}";
    @Rule
    public MockWebServer server = new MockWebServer();

    @Test
    public void testSimple() throws IOException {
        server.enqueue(new MockResponse().setBody(EXPECTED));

        String rst = http(server.getPort()).get();

        assert rst != null;
        assert EXPECTED.equals(rst);
    }

    @Test
    public void testDispatcher() throws IOException {
        server.setDispatcher(new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
                if (request.getPath().equals("/v1/login/auth/")) {
                    return new MockResponse().setResponseCode(200);
                } else if (request.getPath().equals("/v1/check/version")) {
                    return new MockResponse().setResponseCode(200).setBody("version=9");
                } else if (request.getPath().equals("/v1/profile/info")) {
                    return new MockResponse().setResponseCode(200).setBody("{\\\"info\\\":{\\\"name\":\"Lucas Albuquerque\",\"age\":\"21\",\"gender\":\"male\"}}");
                }
                return new MockResponse().setResponseCode(404);
            }
        });

        assert path(server.getPort(), "/v1/login/auth/").execAsCode("GET") == 200;
    }
}
