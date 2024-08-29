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
package org.noear.solon.logging.utils;

import org.noear.solon.annotation.Note;
import org.noear.solon.extend.impl.LogUtilExt;

/**
 * 把内核日志转到 Slf4j 接口（不再需要手动转了）
 *
 * @author noear
 * @since 1.10
 * @deprecated 2.3
 * @removal true
 */
@Note("不再需要手动转了")
@Deprecated
public class LogUtilToSlf4j extends LogUtilExt {

}
