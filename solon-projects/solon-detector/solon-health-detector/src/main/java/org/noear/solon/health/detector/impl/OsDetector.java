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
package org.noear.solon.health.detector.impl;

import org.noear.solon.health.detector.AbstractDetector;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 系统 探测器
 *
 * @author 夜の孤城
 * @since 1.2
 * */
public class OsDetector extends AbstractDetector {
    private String arch = System.getProperty("os.arch");
    private String version = System.getProperty("os.version");

    @Override
    public String getName() {
        return "os";
    }

    @Override
    public Map<String, Object> getInfo() {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("name", osName);
        info.put("arch", arch);
        info.put("version", version);
        return info;
    }
}
