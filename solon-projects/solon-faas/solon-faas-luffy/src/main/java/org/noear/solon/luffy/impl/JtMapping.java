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
package org.noear.solon.luffy.impl;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 文件与执行器映射关系
 *
 * @author noear
 * @since 1.3
 */
public class JtMapping {
    static Map<String, String> fileMapping = new LinkedHashMap<>();

    static {
        fileMapping.put("", "javascript");
        fileMapping.put("js", "javascript");
        fileMapping.put("btl","beetl");
        fileMapping.put("enj","enjoy");
        fileMapping.put("ftl","freemarker");
        fileMapping.put("thy","thymeleaf");
        fileMapping.put("vm","velocity");
        fileMapping.put("groovy", "groovy");
        fileMapping.put("lua", "lua");
        fileMapping.put("py","python");
        fileMapping.put("rb","ruby");
    }

    public static void addMapping(String suffix, String actuator) {
        fileMapping.put(suffix, actuator);
    }

    public static String getActuator(String suffix) {
        return fileMapping.get(suffix);
    }
}
