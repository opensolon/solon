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
package org.noear.solon.auth;

/**
 * 适配器提供者（可以通过路径前缀限制效果）
 *
 * @author noear
 * @since 1.10
 * @deprecated 3.0
 */
@Deprecated
public interface AuthAdapterSupplier {
    /**
     * 路径前缀
     * */
    String pathPrefix();
    /**
     * 适配器
     * */
    AuthAdapter adapter();
}
