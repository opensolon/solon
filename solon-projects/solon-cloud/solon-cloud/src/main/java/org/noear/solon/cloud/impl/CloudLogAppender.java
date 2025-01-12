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
package org.noear.solon.cloud.impl;

import org.noear.solon.cloud.CloudClient;
import org.noear.solon.logging.event.AppenderBase;
import org.noear.solon.logging.event.Level;
import org.noear.solon.logging.event.LogEvent;

/**
 * Cloud Log Appender
 *
 * @author noear
 * @since 1.6
 */
public class CloudLogAppender extends AppenderBase {
    @Override
    public Level getDefaultLevel() {
        return Level.INFO;
    }

    @Override
    public void append(LogEvent logEvent) {
        if (CloudClient.log() != null) {
            CloudClient.log().append(logEvent);
        }
    }
}
