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
package org.noear.solon.boot.io;

import java.io.IOException;

/**
 * 限制输入异常
 *
 * @author noear
 * @since 1.9
 * @deprecated 3.5
 * */
@Deprecated
public class LimitedInputException extends IOException {
    public LimitedInputException() {
    }

    public LimitedInputException(String message) {
        super(message);
    }

    public LimitedInputException(String message, Throwable cause) {
        super(message, cause);
    }

    public LimitedInputException(Throwable cause) {
        super(cause);
    }
}

