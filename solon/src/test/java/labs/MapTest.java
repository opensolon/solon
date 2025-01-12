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

import org.noear.solon.core.util.MultiMap;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author noear 2023/9/13 created
 */
public class MapTest {
    public static void main(String[] args) {
        Map<String, String> loadKeyMap = new TreeMap<>();
        loadKeyMap.put("[3]", "d");
        loadKeyMap.put("[0]", "a");
        loadKeyMap.put("[2]", "c");
        loadKeyMap.put("[1]", "b");
        loadKeyMap.put("","x");

        for (String v : loadKeyMap.values()) {
            System.out.println(v);
        }

        /////////


        MultiMap<String> map = new MultiMap<>();
        map.add("a", "a");
        map.add("b", "b1");
        map.add("b", "b2");

        System.out.println(map);
    }
}
