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
package org.noear.solon.data.sqlink.api.crud.read.group;

import io.github.kiryu1223.expressionTree.expressions.annos.Getter;
import io.github.kiryu1223.expressionTree.expressions.annos.Setter;
import org.noear.solon.data.sqlink.base.metaData.FieldMetaData;
import org.noear.solon.data.sqlink.base.metaData.MetaData;
import org.noear.solon.data.sqlink.base.metaData.MetaDataCache;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author kiryu1223
 * @since 3.0
 */
@Getter
@Setter
public abstract class Grouper {
    @Override
    public String toString() {
        try {
            List<String> strings = new ArrayList<>();
            MetaData metaData = MetaDataCache.getMetaData(this.getClass());
            for (FieldMetaData property : metaData.getPropertys()) {
                strings.add(property.getProperty() + "=" + property.getGetter().invoke(this));
            }
            return "(" + String.join(",", strings) + ")";
        }
        catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
