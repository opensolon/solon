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
package org.noear.solon.serialization.snack3;

import org.noear.snack.core.Options;
import org.noear.snack.core.NodeEncoder;
import org.noear.solon.core.convert.Converter;
import org.noear.solon.serialization.JsonRenderFactory;

/**
 * Json 渲染器工厂基类
 *
 * @author noear
 * @since 1.5
 */
public abstract class SnackRenderFactoryBase implements JsonRenderFactory {

    public abstract Options config();

    /**
     * 添加编码器
     */
    public <T> void addEncoder(Class<T> clz, NodeEncoder<T> encoder) {
        config().addEncoder(clz, encoder);
    }

    @Override
    public <T> void addConvertor(Class<T> clz, Converter<T,Object> converter) {
        addEncoder(clz, (source, target) -> {
            Object val = converter.convert((T) source);

            if (val == null) {
                target.asNull();
            } else if (val instanceof String) {
                target.val().setString((String) val);
            } else if (val instanceof Number) {
                target.val().setNumber((Number) val);
            } else {
                throw new IllegalArgumentException("The result type of the converter is not supported: " + val.getClass().getName());
            }
        });
    }
}
