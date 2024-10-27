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
package org.noear.solon.data.sqlink.base.metaData;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author kiryu1223
 * @since 3.0
 */
public class ConverterCache
{
    private ConverterCache()
    {
    }

    private static final Map<Class<? extends IConverter<?, ?>>, IConverter<?, ?>> converterCache = new ConcurrentHashMap<>();

    public static IConverter<?, ?> get(Class<? extends IConverter<?, ?>> c)
    {
        IConverter<?, ?> converter = converterCache.get(c);
        if (converter == null)
        {
            try
            {
                converter = c.newInstance();
                converterCache.put(c, converter);
            }
            catch (InstantiationException | IllegalAccessException e)
            {
                throw new RuntimeException(e);
            }

        }
        return converter;
    }
}
