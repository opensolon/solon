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

/**
 * @author noear
 * @since 2.4
 */
public class SizeUtil {
    public static final long ONE_KB = 1024L;
    public static final long ONE_MB = 1048576L;
    public static final long ONE_GB = 1073741824L;
    public static final long ONE_TB = 1099511627776L;
    public static final long ONE_PB = 1125899906842624L;
    public static final long ONE_EB = 1152921504606846976L;

    public static String formatByteSize(long byteSize) {
        double dataSize = byteSize;
        if (dataSize >= ONE_EB)
            return (Math.round(dataSize / ONE_EB * 100.0D) / 100.0D) + "EB";
        if (dataSize >= ONE_PB)
            return (Math.round(dataSize / ONE_PB * 100.0D) / 100.0D) + "PB";
        if (dataSize >= ONE_TB)
            return (Math.round(dataSize / ONE_TB * 100.0D) / 100.0D) + "TB";
        if (dataSize >= ONE_GB)
            return (Math.round(dataSize / ONE_GB * 100.0D) / 100.0D) + "GB";
        if (dataSize >= ONE_MB)
            return (Math.round(dataSize / ONE_MB * 100.0D) / 100.0D) + "MB";
        if (dataSize >= ONE_KB)
            return (Math.round(dataSize / ONE_KB * 100.0D) / 100.0D) + "KB";
        if (dataSize > 0.0D) {
            return (Math.round(dataSize)) + "B";
        }
        return "0";
    }
}
