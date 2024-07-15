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
package org.noear.solon.data.dynamicds;


/**
 * 动态数据源切换
 *
 * @author noear
 * @since 1.11
 * @deprecated 2.5
 */
@Deprecated
public class DynamicDsHolder {
    /**
     * 清除
     */
    @Deprecated
    public static void remove() {
        DynamicDsKey.remove();
    }

    /**
     * 获取
     */
    @Deprecated
    public static String get() {
        return DynamicDsKey.getCurrent();
    }

    /**
     * 设置
     */
    @Deprecated
    public static void set(String name) {
        DynamicDsKey.setCurrent(name);
    }
}
