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
package org.noear.solon.health.detector.util;

import org.noear.solon.core.util.IoUtil;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

/**
 * 命令行执行工具
 *
 * @author noear
 * @since 2.2
 */
public class CmdUtil {

    public static String execute(String... command) throws Exception {
        return execute(false, command);
    }

    public static String execute(boolean firstLine, String... command) throws Exception {
        String text = null;
        InputStream is = null;
        try {
            ProcessBuilder builder = new ProcessBuilder(new String[0]);
            builder.command(command);
            Process process = builder.start();
            process.getOutputStream().close();
            is = process.getInputStream();
            if (firstLine) {
                InputStreamReader isr = new InputStreamReader(is);
                LineNumberReader lnr = new LineNumberReader(isr);
                text = lnr.readLine();
                lnr.close();
                isr.close();
            } else {
                text = IoUtil.transferToString(is,"utf-8");
            }
        } finally {
            if (is != null) {
                is.close();
            }
        }
        return text;
    }
}
