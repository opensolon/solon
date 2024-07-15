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
package org.noear.solon.admin.server.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.noear.solon.lang.Nullable;

/**
 * 应用程序数据传输 Dto
 *
 * @param <T> 要传输的数据类型
 * @author shaokeyibb
 * @since 2.3
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationWebsocketTransfer<T> {

    @Nullable
    private Application scope;

    private String type;

    private T data;

}
