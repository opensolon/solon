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

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.LoggingEvent;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SolonTagsConverter 场景测试
 *
 * @since 3.9.2
 */
public class SolonTagsConverterTest {

    private LoggingEvent event(Map<String, String> mdc) {
        LoggingEvent e = new LoggingEvent("fqcn",
                new LoggerContext().getLogger("test"), Level.INFO, "msg", null, null);
        // 直接构造的 LoggingEvent 必须显式设置 MDC，否则 getMDCPropertyMap 会 NPE
        e.setMDCPropertyMap(mdc != null ? mdc : new LinkedHashMap<>());
        return e;
    }

    @Test
    public void test_null_mdc_returns_empty() {
        SolonTagsConverter converter = new SolonTagsConverter();
        // 覆盖 getMDCPropertyMap 返回 null 的分支
        LoggingEvent nullMdcEvent = new LoggingEvent("fqcn",
                new LoggerContext().getLogger("test"), Level.INFO, "msg", null, null) {
            @Override
            public java.util.Map<String, String> getMDCPropertyMap() {
                return null;
            }
        };
        assertEquals("", converter.convert(nullMdcEvent));
    }

    @Test
    public void test_empty_mdc_returns_empty() {
        SolonTagsConverter converter = new SolonTagsConverter();
        assertEquals("", converter.convert(event(new LinkedHashMap<>())));
    }

    @Test
    public void test_trace_id_is_excluded() {
        SolonTagsConverter converter = new SolonTagsConverter();

        Map<String, String> mdc = new LinkedHashMap<>();
        mdc.put("traceId", "abc-123");

        assertEquals("", converter.convert(event(mdc)));
    }

    @Test
    public void test_single_tag() {
        SolonTagsConverter converter = new SolonTagsConverter();

        Map<String, String> mdc = new LinkedHashMap<>();
        mdc.put("traceId", "abc-123");
        mdc.put("tag0", "user_1");

        assertEquals("[@tag0:user_1]", converter.convert(event(mdc)));
    }

    @Test
    public void test_multiple_tags_keep_order_and_exclude_trace_id() {
        SolonTagsConverter converter = new SolonTagsConverter();

        Map<String, String> mdc = new LinkedHashMap<>();
        mdc.put("traceId", "t1");
        mdc.put("userId", "1001");
        mdc.put("channel", "app");

        assertEquals("[@userId:1001][@channel:app]", converter.convert(event(mdc)));
    }

    @Test
    public void test_only_trace_id_like_keys() {
        SolonTagsConverter converter = new SolonTagsConverter();

        Map<String, String> mdc = new LinkedHashMap<>();
        mdc.put("traceId", "t1");
        mdc.put("mytraceId", "v");

        assertEquals("[@mytraceId:v]", converter.convert(event(mdc)));
    }
}
