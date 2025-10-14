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
package org.noear.solon.serialization.snack4.integration;

import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.serialization.SerializerNames;
import org.noear.solon.serialization.prop.JsonProps;
import org.noear.solon.serialization.snack4.Snack4EntityConverter;
import org.noear.solon.serialization.snack4.Snack4StringSerializer;

public class SerializationSnack4Plugin implements Plugin {
    @Override
    public void start(AppContext context) {
        JsonProps jsonProps = JsonProps.create(context);

        //::serializer
        Snack4StringSerializer serializer = new Snack4StringSerializer(jsonProps);
        context.wrapAndPut(Snack4StringSerializer.class, serializer); //用于扩展
        context.app().serializers().register(SerializerNames.AT_JSON, serializer);

        //::entityConverter
        Snack4EntityConverter entityConverter = new Snack4EntityConverter(serializer);
        context.wrapAndPut(Snack4EntityConverter.class, entityConverter); //用于扩展

        //会自动转为 executor, renderer
        context.app().chains().addEntityConverter(entityConverter);
    }
}