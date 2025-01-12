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
package org.noear.solon.logging.event;

/**
 * 日志等级
 *
 * @author noear
 * @since 1.0
 */
public enum Level {
    TRACE(10),
    DEBUG(20),
    INFO(30),
    WARN(40),
    ERROR(50);

    public final int code;

    public static Level of(int code, Level def) {
        for (Level v : values()) {
            if (v.code == code) {
                return v;
            }
        }

        return def;
    }

    public static Level of(String name, Level def) {
        if (name == null || name.length() == 0) {
            return def;
        }

        switch (name.toUpperCase()) {
            case "TRACE":
                return TRACE;
            case "DEBUG":
                return DEBUG;
            case "INFO":
                return INFO;
            case "WARN":
                return WARN;
            case "ERROR":
                return ERROR;
            default:
                return def;
        }
    }

    Level(int code) {
        this.code = code;
    }
}
