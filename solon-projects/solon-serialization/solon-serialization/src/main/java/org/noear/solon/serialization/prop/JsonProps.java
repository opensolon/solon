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
package org.noear.solon.serialization.prop;

import org.noear.solon.core.AppContext;

import java.io.Serializable;

/**
 * Json 序列化框架通用配置属性
 *
 * @author noear
 * @since 1.12
 */
public class JsonProps implements Serializable {
    public static JsonProps create(AppContext context) {
        JsonProps tmp = context.cfg().toBean("solon.serialization.json", JsonProps.class);

        if (tmp == null) {
            tmp = new JsonProps();
            tmp.dateAsTicks = true;
        } else {
            tmp.dateAsTicks = (tmp.dateAsFormat == null);
        }

        return tmp;
    }

    public String dateAsTimeZone;
    public String dateAsFormat;
    public boolean dateAsTicks;

    public boolean longAsString;
    public boolean boolAsInt;

    public boolean nullStringAsEmpty;
    public boolean nullBoolAsFalse;
    public boolean nullNumberAsZero;
    public boolean nullArrayAsEmpty;
    public boolean nullAsWriteable;

    public boolean enumAsName;
}
