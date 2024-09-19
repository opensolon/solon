/*
 * Copyright 2017-2024 noear.org and authors
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
package org.noear.solon.logging.appender;

import java.io.Console;
import java.io.PrintWriter;

/**
 * 控制台添加器实现类（限制在调试模式或文件模式下打印）
 *
 * @author noear
 * @since 1.3
 */
public class ConsoleAppender extends OutputStreamAppender {
    public ConsoleAppender() {
        Console console = System.console();

        if (console != null && console.writer() != null) {
            setOutput(new PrintWriter(console.writer(), true));
        } else {
            setOutput(new PrintWriter(System.out, true));
        }
    }
}
