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
package webapp.models;

import org.noear.solon.annotation.Component;
import org.noear.solon.core.convert.Converter;
import org.noear.solon.core.convert.ConverterFactory;
import org.noear.solon.core.exception.ConvertException;

/**
 * @author noear 2023/7/17 created
 */
@Component
public class CatTypeConverter implements ConverterFactory<String, Enum> {
    @Override
    public <T extends Enum> Converter<String, T> getConverter(Class<T> targetType) {
        if (CatType.class == targetType) {
            return (Converter<String, T>) new CatTypeConverter1();
        } else {
            return null;
        }
    }

    public class CatTypeConverter1 implements Converter<String, CatType> {
        @Override
        public CatType convert(String value) throws ConvertException {
            return "2".equals(value) ? CatType.Demo2 : CatType.Demo1;
        }
    }
}
