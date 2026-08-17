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
package org.noear.solon.serialization.kryo.integration;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.serialization.EntityBytesSerializer;
import org.noear.solon.serialization.SerializerNames;
import org.noear.solon.serialization.kryo.KryoClassFilter;
import org.noear.solon.serialization.kryo.KryoBytesSerializer;
import org.noear.solon.serialization.kryo.KryoEntityConverter;

/**
 * @author noear
 * @since 3.0
 */
public class SerializationKryoPlugin implements Plugin {

    static final String CFG_ALLOW = "solon.serialization.kryo.allow";
    static final String CFG_DENY = "solon.serialization.kryo.deny";
    static final String CFG_UNRESTRICTED = "solon.serialization.kryo.unrestricted";

    @Override
    public void start(AppContext context) throws Throwable {
        applyConfig(KryoBytesSerializer.getDefault().classFilter());

        //::serializer
        KryoBytesSerializer serializer = KryoBytesSerializer.getDefault();
        context.wrapAndPut(KryoBytesSerializer.class, serializer); //用于扩展
        context.wrapAndPut(EntityBytesSerializer.class, serializer);
        context.app().serializers().register(SerializerNames.AT_KRYO, serializer);

        //entityConverter
        KryoEntityConverter entityConverter = new KryoEntityConverter(serializer);
        context.wrapAndPut(KryoEntityConverter.class, entityConverter); //用于扩展

        //会自动转为 executor, renderer
        context.app().chains().addEntityConverter(entityConverter);
    }

    static void applyConfig(KryoClassFilter filter) {
        String allow = Solon.cfg().get(CFG_ALLOW);
        if (Utils.isNotEmpty(allow)) {
            for (String s : allow.split(",")) {
                filter.allow(s.trim());
            }
        }
        String deny = Solon.cfg().get(CFG_DENY);
        if (Utils.isNotEmpty(deny)) {
            for (String s : deny.split(",")) {
                filter.deny(s.trim());
            }
        }
        if ("true".equalsIgnoreCase(Solon.cfg().get(CFG_UNRESTRICTED))) {
            filter.allowAll(true);
        }
    }
}
