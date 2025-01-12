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

import org.noear.solon.core.util.TmplUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author noear 2024/5/10 created
 */
public class TmlTest {
    public static void main(String[] args) {
        String view = "user=${user}";
        Map<String, Object> model = new HashMap<>();
        model.put("user", "noear");
        model.put("label", 1);
        model.put("",model);

        System.out.println(TmplUtil.parse(view, model));


        long timeStart = System.currentTimeMillis();


        for (int i = 0; i < 100_000; i++) {
            TmplUtil.parse(view, model);
        }

        System.out.println(System.currentTimeMillis() - timeStart);
    }
}