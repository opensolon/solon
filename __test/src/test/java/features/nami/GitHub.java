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
package features.nami;

import org.noear.nami.annotation.NamiBody;
import org.noear.nami.annotation.NamiMapping;

import java.util.List;

/**
 * @author noear 2021/1/1 created
 */
public interface GitHub {
    @NamiMapping("GET /repos/{owner}/{repo}/contributors")
    List<Contributor> contributors(String owner, String repo);

    @NamiMapping("POST /repos/{owner}/{repo}/issues")
    void createIssue(@NamiBody Issue issue, String owner, String repo);

    default String hello(){
        return "hello";
    }
}
