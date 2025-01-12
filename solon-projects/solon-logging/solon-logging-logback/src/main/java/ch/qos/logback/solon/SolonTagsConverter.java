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
package ch.qos.logback.solon;

import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

import java.util.Map;

/**
 * @author 颖
 * @since 1.6
 */
public class SolonTagsConverter extends MessageConverter {

    @Override
    public String convert(ILoggingEvent event) {
        Map<String, String> eData = event.getMDCPropertyMap();

        if (eData != null) {
            StringBuilder buf = new StringBuilder();
            eData.forEach((tag, val) -> {
                if ("traceId".equals(tag) == false) {
                    buf.append("[@").append(tag).append(":").append(val).append("]");
                }
            });
            return buf.toString();
        } else {
            return "";
        }
    }
}
