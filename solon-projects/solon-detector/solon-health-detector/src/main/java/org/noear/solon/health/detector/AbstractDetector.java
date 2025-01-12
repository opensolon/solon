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
package org.noear.solon.health.detector;

import org.noear.solon.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 检测器虚拟类
 *
 * @author noear
 * @since 1.5
 * */
public abstract class AbstractDetector implements Detector {

    protected final static String osName = System.getProperty("os.name").toLowerCase();

    protected List<String[]> matcher(Pattern pattern, String text) throws Exception {
        List<String[]> infos = new ArrayList();
        if (Utils.isNotEmpty(text)) {
            String[] lines = text.trim().split("\\n");
            for (String line : lines) {
                Matcher matcher = pattern.matcher(line);
                while (matcher.find()) {
                    String[] info = new String[matcher.groupCount() + 1];

                    for (int i = 0; i <= matcher.groupCount(); ++i) {
                        info[i] = matcher.group(i).trim();
                    }
                    infos.add(info);
                }
            }
        }
        return infos;
    }
}
