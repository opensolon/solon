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
package demo2;

import org.noear.solon.Solon;
import org.noear.solon.aot.Settings;
import org.noear.solon.aot.proxy.ProxyClassGenerator;
import org.noear.solon.core.runtime.NativeDetector;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author noear 2023/4/26 created
 */
public class Test {
    public static void main(String[] args) {
        System.setProperty(NativeDetector.AOT_PROCESSING, "1");

        Solon.start(Test.class, args);

        String pathStr = new File("/Users/noear/Downloads/temp3").getPath();
        Path path = Paths.get(pathStr);

        Settings settings = new Settings(path, path, "", "", "");

        ProxyClassGenerator proxyClassGenerator = new ProxyClassGenerator();
        proxyClassGenerator.generateCode(settings, TagServiceImpl.class);

    }
}
