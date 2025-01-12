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
package benchmark;

import features.nami.Contributor;
import features.nami.GitHub;
import features.nami.Issue;
import org.junit.jupiter.api.Test;
import org.noear.nami.Nami;
import org.noear.nami.coder.snack3.SnackDecoder;
import org.noear.solon.test.SolonTest;

import java.util.List;

/**
 * @author noear 2021/1/1 created
 */
@SolonTest
public class NamiTest {
    @Test
    public void test() {
        GitHub github = Nami.builder()
                .decoder(SnackDecoder.instance)
                .upstream(() -> "https://api.github.com")
                .create(GitHub.class);

        github.contributors("OpenFeign", "feign");

        long start = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            github.contributors("OpenFeign", "feign");
        }
        System.out.println(System.currentTimeMillis() - start);
    }

    @Test
    public void test2() {
        GitHub github = Nami.builder()
                .decoder(SnackDecoder.instance)
                .upstream(() -> "https://api.github.com")
                .create(GitHub.class);

        Issue issue = new Issue();
        issue.title = "测试";
        issue.body = "同题";

        github.createIssue(issue, "OpenFeign", "feign");

        long start = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            github.createIssue(issue, "OpenFeign", "feign");
        }
        System.out.println(System.currentTimeMillis() - start);
    }
}
