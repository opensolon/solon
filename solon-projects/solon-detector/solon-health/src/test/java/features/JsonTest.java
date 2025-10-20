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
import org.noear.snack4.ONode;
import org.noear.snack4.Feature;
import org.noear.snack4.Options;

import java.util.HashMap;
import java.util.Map;

/**
 * @author noear 2022/8/7 created
 */
public class JsonTest {
    @Test
    public void test(){
        Map<String,Object> data = new HashMap<>();
        data.put("c:\\","c:\\");

        System.out.println(ONode.serialize(data, options));
    }

    private static final Options options = Options.of()
            .addFeatures(Feature.Write_EnumUsingName);

}
