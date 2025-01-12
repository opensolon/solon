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

import org.noear.solon.web.staticfiles.StaticRepository;

/**
 * @author noear 2022/12/7 created
 */
public class PreheatDemo {
    StaticRepository staticRepository = null;

    public void demo() throws Exception {
        staticRepository.preheat("demo/file.htm", false);
        staticRepository.preheat("demo/file.js", false);
        staticRepository.preheat("demo/file.pin", false);

    }
}
