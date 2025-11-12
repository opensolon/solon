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
package org.noear.solon.extend.impl;

import org.noear.solon.core.runtime.RuntimeDetector;
import org.noear.solon.aot.graalvm.GraalvmUtil;
import org.noear.solon.core.util.Scanner;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * native 运行时，优先从元数据文件（solon-resource.json）里获取
 *
 * @author songyinyin
 * @since 2.2
 */
public class ScannerExt extends Scanner {
    @Override
    public void scan(ClassLoader classLoader, String path, boolean fileMode, Predicate<String> filter, Consumer<String> consumer) {
        super.scan(classLoader, path, fileMode, filter, consumer);

        if (fileMode == false) {
            //3.native image
            if (RuntimeDetector.inNativeImage()) {
                GraalvmUtil.scanResource(path, filter, consumer);
            }
        }
    }
}
