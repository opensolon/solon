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
package org.noear.solon.core.handle;

import org.noear.solon.core.route.Router;

/**
 * 处理点（配合路由器使用）
 *
 * @see Router#add(String, Endpoint, MethodType, int, Handler)
 * @author noear
 * @since 1.0
 * @deprecated 2.9
 * */
@Deprecated
public enum Endpoint {
    /**前置处理*/
    before(0),
    /**主体处理*/
    main(1),
    /**后置处理*/
    after(2);

    public final int code;
    Endpoint(int code){
        this.code = code;
    }
}
