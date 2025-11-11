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

import org.noear.solon.aot.Settings;
import org.noear.solon.core.runtime.RuntimeService;
import org.noear.solon.core.util.Reflection;
import org.noear.solon.core.util.Scanner;

import java.io.File;

/**
 *
 * @author noear
 * @since 3.7
 */
public class RuntimeServiceExt extends RuntimeService {
    public static Settings settings;

    @Override
    public Reflection createReflection() {
        return new ReflectionExt();
    }

    @Override
    public Scanner createScanner() {
        return new ScannerExt();
    }

    @Override
    public File createClassOutputFile(String fileName) {
        if (settings == null) {
            throw new UnsupportedOperationException();
        } else {
            return new File(settings.getClassOutput() + "/" + fileName);
        }
    }
}
