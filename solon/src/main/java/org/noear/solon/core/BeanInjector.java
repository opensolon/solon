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
package org.noear.solon.core;

import java.lang.annotation.Annotation;

/**
 * Bean 注入器（此类用于扩展 AppContext，为其添加注入器）
 *
 * <pre><code>
 * //@Db 注入器添加
 * context.beanInjectorAdd(Db.classs, (vh, anno)->{
 *     ...
 * });
 *
 * //@Db demo
 * @Component
 * public class DemoBean{
 *     @Db("db1")
 *     UserMapper userMapper;
 * }
 * </code></pre>
 *
 * @author noear
 * @since 1.0
 * */
@FunctionalInterface
public interface BeanInjector<T extends Annotation> {
    /**
     * 注入
     */
    void doInject(VarHolder vh, T anno);

    /**
     * 填充
     */
    default void doFill(Object obj, T anno) {

    }
}
