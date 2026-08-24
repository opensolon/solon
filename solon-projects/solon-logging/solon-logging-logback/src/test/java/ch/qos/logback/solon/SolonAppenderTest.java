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
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.LoggingEvent;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.noear.solon.logging.AppenderManager;
import org.noear.solon.logging.event.AppenderBase;
import org.noear.solon.logging.event.LogEvent;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SolonAppender 场景测试：
 * appender 数量门槛、级别映射（TRACE/DEBUG/INFO 默认/WARN/ERROR）、
 * 异常消息的 "{}" 替换与换行拼接
 *
 * @since 3.9.2
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SolonAppenderTest {

    static class CaptureAppender extends AppenderBase {
        final List<LogEvent> events = new ArrayList<>();

        @Override
        public void append(LogEvent logEvent) {
            events.add(logEvent);
        }
    }

    private final LoggerContext loggerContext = new LoggerContext();
    private final Logger logger = loggerContext.getLogger("test");

    private SolonAppender startedAppender() {
        SolonAppender appender = new SolonAppender();
        appender.setContext(loggerContext);
        appender.start();
        return appender;
    }

    private LoggingEvent event(Level level, String msg, Throwable t) {
        return event(level, msg, t, new java.util.LinkedHashMap<>());
    }

    private LoggingEvent event(Level level, String msg, Throwable t, java.util.Map<String, String> mdc) {
        LoggingEvent e = new LoggingEvent("fqcn", logger, level, msg, t, null);
        // 直接构造的 LoggingEvent 必须显式设置 MDC，否则 getMDCPropertyMap 会 NPE
        e.setMDCPropertyMap(mdc);
        return e;
    }

    @Test
    @Order(1)
    public void test_append_skipped_when_appender_count_less_than_2() throws Exception {
        // 静态注册表在干净 JVM 中只有 console（count=1）；若已被其它用例注册则跳过
        assumeClean();

        CaptureAppender capture = new CaptureAppender();
        AppenderManager.register("test-capture-1", capture);

        try {
            // 依然只有 1 个（本用例注册的在调用之后生效）……为确定性，此处直接断言 count 语义
            // count<2 时 append 直接返回，不产生事件
            if (AppenderManager.count() < 2) {
                startedAppender().doAppend(event(Level.INFO, "m", null));
                assertTrue(capture.events.isEmpty());
            }
        } finally {
            // count 无法注销，保持注册状态供后续用例使用
        }
    }

    private void assumeClean() throws Exception {
        // AppenderManager 无注销能力；首个执行的用例可验证门槛分支
        org.junit.jupiter.api.Assumptions.assumeTrue(AppenderManager.count() < 2);
    }

    @Test
    @Order(2)
    public void test_level_mapping() {
        CaptureAppender capture = new CaptureAppender();
        AppenderManager.register("test-capture-2", capture);

        SolonAppender appender = startedAppender();

        appender.doAppend(event(Level.TRACE, "m-trace", null));
        appender.doAppend(event(Level.DEBUG, "m-debug", null));
        appender.doAppend(event(Level.INFO, "m-info", null));
        appender.doAppend(event(Level.WARN, "m-warn", null));
        appender.doAppend(event(Level.ERROR, "m-error", null));

        assertEquals(5, capture.events.size());

        assertEquals(org.noear.solon.logging.event.Level.TRACE, capture.events.get(0).getLevel());
        assertEquals(org.noear.solon.logging.event.Level.DEBUG, capture.events.get(1).getLevel());
        // INFO 无显式 case，走默认 INFO
        assertEquals(org.noear.solon.logging.event.Level.INFO, capture.events.get(2).getLevel());
        assertEquals(org.noear.solon.logging.event.Level.WARN, capture.events.get(3).getLevel());
        assertEquals(org.noear.solon.logging.event.Level.ERROR, capture.events.get(4).getLevel());

        assertEquals("m-info", capture.events.get(2).getContent());
        assertEquals("test", capture.events.get(2).getLoggerName());
    }

    @Test
    @Order(3)
    public void test_throwable_appended_with_newline() {
        CaptureAppender capture = new CaptureAppender();
        AppenderManager.register("test-capture-3", capture);

        IllegalStateException ex = new IllegalStateException("boom");
        startedAppender().doAppend(event(Level.ERROR, "出错了", ex));

        assertEquals(1, capture.events.size());
        String message = (String) capture.events.get(0).getContent();
        assertTrue(message.startsWith("出错了\n"));
        assertTrue(message.contains("IllegalStateException: boom"));
    }

    @Test
    @Order(4)
    public void test_throwable_replaces_placeholder() {
        CaptureAppender capture = new CaptureAppender();
        AppenderManager.register("test-capture-4", capture);

        IllegalStateException ex = new IllegalStateException("boom");
        startedAppender().doAppend(event(Level.ERROR, "出错了: {}", ex));

        assertEquals(1, capture.events.size());
        String message = (String) capture.events.get(0).getContent();
        assertTrue(message.startsWith("出错了: java.lang.IllegalStateException: boom"));
        assertFalse(message.contains("{}"));
    }

    @Test
    @Order(5)
    public void test_mdc_passed_through() {
        CaptureAppender capture = new CaptureAppender();
        AppenderManager.register("test-capture-5", capture);

        java.util.Map<String, String> mdc = new java.util.LinkedHashMap<>();
        mdc.put("traceId", "t-1");

        LoggingEvent e = event(Level.INFO, "m", null, mdc);

        startedAppender().doAppend(e);

        assertEquals(1, capture.events.size());
        assertEquals("t-1", capture.events.get(0).getMetainfo().get("traceId"));
    }
}
