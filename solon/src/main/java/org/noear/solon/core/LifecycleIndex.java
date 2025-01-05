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
package org.noear.solon.core;

/**
 * 生命周期手动索引
 *
 * @author noear
 * @since 2.7
 * @deprecated 3.0 {@link Constants}
 */
@Deprecated
public interface LifecycleIndex {
    int CLASS_CONDITION_IF_MISSING = -99;
    int METHOD_CONDITION_IF_MISSING = -98;
    int PLUGIN_BEAN_BUILD = -97;
    int PARAM_COLLECTION_INJECT = -96;
    int FIELD_COLLECTION_INJECT = -95;
    int PLUGIN_BEAN_USES = -94;
    int GATEWAY_BEAN_USES = -93;
}
