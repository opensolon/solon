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
package ch.qos.logback.solon.integration;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.helpers.NOPAppender;
import ch.qos.logback.core.CoreConstants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * LogbackConfigurator 场景测试：conversionRule、appender、logger、root、start
 *
 * @since 3.9.2
 */
public class LogbackConfiguratorTest {

    LoggerContext context;
    LogbackConfigurator configurator;

    @BeforeEach
    public void setUp() {
        context = new LoggerContext();
        configurator = new LogbackConfigurator(context);
    }

    @AfterEach
    public void tearDown() {
        context.stop();
    }

    @Test
    public void test_get_context() {
        assertSame(context, configurator.getContext());
    }

    @Test
    public void test_get_configuration_lock() {
        assertSame(context.getConfigurationLock(), configurator.getConfigurationLock());
    }

    @Test
    public void test_conversion_rule_creates_registry() {
        configurator.conversionRule("tags", ch.qos.logback.solon.SolonTagsConverter.class);

        Map<String, String> registry = (Map<String, String>) context.getObject(CoreConstants.PATTERN_RULE_REGISTRY);
        assertNotNull(registry);
        assertEquals(ch.qos.logback.solon.SolonTagsConverter.class.getName(), registry.get("tags"));
    }

    @Test
    public void test_conversion_rule_reuses_existing_registry() {
        Map<String, String> existing = new java.util.HashMap<>();
        existing.put("a", "A");
        context.putObject(CoreConstants.PATTERN_RULE_REGISTRY, existing);

        configurator.conversionRule("tags", ch.qos.logback.solon.SolonTagsConverter.class);

        Map<String, String> registry = (Map<String, String>) context.getObject(CoreConstants.PATTERN_RULE_REGISTRY);
        assertSame(existing, registry);
        assertEquals("A", registry.get("a"));
        assertEquals(ch.qos.logback.solon.SolonTagsConverter.class.getName(), registry.get("tags"));
    }

    @Test
    public void test_appender_sets_name_and_starts() {
        NOPAppender<ILoggingEvent> appender = new NOPAppender<>();
        configurator.appender("MY_CONSOLE", appender);

        assertEquals("MY_CONSOLE", appender.getName());
        assertTrue(appender.isStarted());
        // appender() 只命名并启动，不挂到 root
        assertNull(context.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME).getAppender("MY_CONSOLE"));
    }

    @Test
    public void test_logger_two_args_defaults() {
        configurator.logger("a.b", Level.DEBUG);

        Logger logger = context.getLogger("a.b");
        assertEquals(Level.DEBUG, logger.getLevel());
        assertTrue(logger.isAdditive());
        assertFalse(logger.iteratorForAppenders().hasNext());
    }

    @Test
    public void test_logger_three_args_non_additive() {
        configurator.logger("a.b", Level.WARN, false);

        Logger logger = context.getLogger("a.b");
        assertEquals(Level.WARN, logger.getLevel());
        assertFalse(logger.isAdditive());
    }

    @Test
    public void test_logger_four_args_attaches_appender() {
        NOPAppender<ILoggingEvent> appender = new NOPAppender<>();
        configurator.appender("ATTACH_ME", appender);

        configurator.logger("a.b", Level.INFO, false, appender);

        Logger logger = context.getLogger("a.b");
        assertEquals(Level.INFO, logger.getLevel());
        assertFalse(logger.isAdditive());
        assertSame(appender, logger.getAppender("ATTACH_ME"));
    }

    @Test
    public void test_logger_null_level_keeps_existing() {
        Logger logger = context.getLogger("a.b");
        logger.setLevel(Level.WARN);

        configurator.logger("a.b", null);

        assertEquals(Level.WARN, logger.getLevel());
    }

    @Test
    public void test_root_sets_level_and_appenders() {
        NOPAppender<ILoggingEvent> a1 = new NOPAppender<>();
        NOPAppender<ILoggingEvent> a2 = new NOPAppender<>();
        a1.setName("R1");
        a2.setName("R2");

        configurator.root(Level.ERROR, a1, a2);

        Logger root = context.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        assertEquals(Level.ERROR, root.getLevel());
        assertNotNull(root.getAppender(a1.getName()));
        assertNotNull(root.getAppender(a2.getName()));
    }

    @Test
    public void test_root_null_level_keeps_existing() {
        Logger root = context.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.WARN);

        configurator.root(null);

        assertEquals(Level.WARN, root.getLevel());
    }

    @Test
    public void test_root_empty_appenders() {
        configurator.root(Level.INFO);

        Logger root = context.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        assertEquals(Level.INFO, root.getLevel());
        Iterator<Appender<ILoggingEvent>> it = root.iteratorForAppenders();
        assertFalse(it.hasNext());
    }

    @Test
    public void test_start_context_aware_gets_context_and_starts() {
        NOPAppender<ILoggingEvent> appender = new NOPAppender<>();
        configurator.start(appender);

        assertTrue(appender.isStarted());
        assertSame(context, appender.getContext());
    }
}
