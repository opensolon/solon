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
package org.noear.solon.config.yaml;

import org.noear.solon.Utils;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * Yaml 属性
 *
 * @author noear
 * @since 1.5
 * @since 2.5
 * */
public class PropertiesYaml extends Properties {
    static final String SOLON_ENV = "solon.env";
    static final String SOLON_ENV_ON = "solon.env.on";
    static final String YML_PART_SPLIT = "---";

    AtomicReference<String> envRef = new AtomicReference<>(System.getProperty(SOLON_ENV));


    private Yaml createYaml() {
        return new Yaml();
    }

    public void loadYml(InputStream inputStream) throws Exception {
        try (InputStreamReader is = new InputStreamReader(inputStream)) {
            this.loadYml(is);
        }
    }

    public void loadYml(Reader reader) throws IOException {
        //支持多部分切割
        List<String> partList = splitParts(reader);

        //开始加载多部分属性

        String partStr = null;
        for (int i = 0; i < partList.size(); i++) {
            partStr = partList.get(i);
            if (Utils.isBlank(partStr)) {
                continue;
            }
            final int fi = i;
            this.loadYml(partStr, partProp -> {
                //2.同步环境变量
                if (envRef.get() == null && fi == 0 && partProp.containsKey(SOLON_ENV)) {
                    envRef.set(String.valueOf(partProp.get(SOLON_ENV)));
                }
            });
        }
    }

    public void loadYml(String str) {
        this.loadYml(str, null);
    }

    public void loadYml(String str, Consumer<Map<Object, Object>> c) {
        //1.加载部分属性
        Object tmp = createYaml().load(str);
        Map<Object, Object> partProp = new TreeMap<>();
        load0(partProp, "", tmp);

        if (partProp.size() == 0) {
            return;
        }

        if (c != null) {
            //2.其他操作，如同步环境变量
            c.accept(partProp);
        }

        //3.根据条件过滤加载
        if (partProp.containsKey(SOLON_ENV_ON)) {
            //如果有环境条件，尝试匹配 //支持多环境匹配。例：solon.env.on: pro1 | pro2
            String envOn = String.valueOf(partProp.get(SOLON_ENV_ON));
            if (Arrays.stream(envOn.split("\\|")).anyMatch(e -> e.trim().equals(envRef.get()))) {
                super.putAll(partProp);
            }
        } else {
            super.putAll(partProp);
        }
    }

    /**
     * 切割多部分
     */
    private List<String> splitParts(Reader reader) throws IOException {
        List<String> partList = new ArrayList<>();
        //支持多部分切割
        try (BufferedReader in = new BufferedReader(reader)) {
            StringBuffer buffer = new StringBuffer();
            String line = null;
            while ((line = in.readLine()) != null) {
                if (line.startsWith(YML_PART_SPLIT)) { //用 starts 替代 pattern 提高性能
                    partList.add(buffer.toString());
                    buffer.setLength(0);
                } else {
                    buffer.append(line).append("\n");
                }
            }
            partList.add(buffer.toString());
        }

        return partList;
    }

    private void load0(Map<Object, Object> temProp, String prefix, Object tmp) {
        if (tmp instanceof Map) {
            ((Map) tmp).forEach((k, v) -> {
                String prefix2 = prefix + "." + k;
                load0(temProp, prefix2, v);
            });
            return;
        }

        if (tmp instanceof List) {
            //do_put(prefix, tmp);
            int index = 0;
            for (Object v : ((List) tmp)) {
                String prefix2 = prefix + "[" + index + "]";
                load0(temProp, prefix2, v);
                index++;
            }
            return;
        }

        if (tmp == null) {
            put0(temProp, prefix, "");
        } else {
            put0(temProp, prefix, String.valueOf(tmp));
        }
    }

    private void put0(Map<Object, Object> temProp, String key, Object val) {
        if (key.startsWith(".")) {
            temProp.put(key.substring(1), val);
        } else {
            temProp.put(key, val);
        }
    }
}
