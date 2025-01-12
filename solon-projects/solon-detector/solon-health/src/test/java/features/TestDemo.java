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

import org.junit.jupiter.api.Test;
import org.noear.solon.core.handle.Result;
import org.noear.solon.health.HealthChecker;
import org.noear.solon.net.http.HttpResponse;
import org.noear.solon.test.HttpTester;
import org.noear.solon.test.SolonTest;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author noear 2021/10/5 created
 */
@SolonTest(TestApp.class)
public class TestDemo extends HttpTester {
    @Test
    public void test1() throws Exception {
        HttpResponse resp = path("/healthz").exec("GET");
        System.out.println(resp.bodyAsString());
        assert resp.code() == 200;

        assert path("/healthz").head() == 200;
    }

    @Test
    public void test2() throws Exception {
        HealthChecker.addIndicator("preflight", Result::succeed);
        HttpResponse resp = path("/healthz").exec("GET");
        System.out.println(resp.bodyAsString());
        assert resp.code() == 200;

        assert path("/healthz").head() == 200;
    }

    @Test
    public void test3() throws Exception {
        HealthChecker.addIndicator("preflight", Result::succeed);
        HealthChecker.addIndicator("test", Result::failure);


        HttpResponse resp = path("/healthz").exec("GET");
        System.out.println(resp.bodyAsString());
        assert resp.code() == 503;

        assert path("/healthz").head() == 503;
    }

    @Test
    public void test4() throws Exception {
        Map<String, Object> preflightMap = new LinkedHashMap<>();
        preflightMap.put("total", 987656789);
        preflightMap.put("free", 6783);
        preflightMap.put("threshold", 7989031);

        HealthChecker.addIndicator("preflight", () -> Result.succeed(preflightMap));
        HealthChecker.addIndicator("test", () -> Result.failure());
        HealthChecker.addIndicator("boom", () -> {
            throw new IllegalStateException();
        });

        int code = path("/healthz").head();
        assert code == 500;
    }
}
