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
package org.noear.solon.cloud.exception;

import org.noear.solon.core.exception.StatusException;

/**
 * Cloud 状态异常（主要用于触发 4xx 状态异常）
 *
 * @author noear
 * @since 2.8
 */
public class CloudStatusException extends StatusException {
    public CloudStatusException(Throwable cause, int code) {
        super(cause, code);
    }

    public CloudStatusException(String message, int code) {
        super(message, code);
    }

    public CloudStatusException(String message, Throwable cause, int code) {
        super(message, cause, code);
    }
}