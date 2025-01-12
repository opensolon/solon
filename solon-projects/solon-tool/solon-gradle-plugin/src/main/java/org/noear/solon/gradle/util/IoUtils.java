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
package org.noear.solon.gradle.util;

import java.io.*;

public class IoUtils {

    public static void writeString(File file, String str) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(str.getBytes());
        }
    }

    public static String readString(File file) throws IOException {
        try (BufferedReader fis = new BufferedReader(new FileReader(file))) {

            StringBuilder sb = new StringBuilder();

            String line;

            while ((line = fis.readLine()) != null) {
                sb.append(line).append("\n");
            }

            return sb.toString();
        }
    }
}
