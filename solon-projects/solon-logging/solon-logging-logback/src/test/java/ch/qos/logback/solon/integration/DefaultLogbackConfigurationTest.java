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
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import ch.qos.logback.core.util.FileSize;
import ch.qos.logback.classic.spi.LoggingEvent;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.noear.solon.Solon;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DefaultLogbackConfiguration.apply 全链路场景测试：
 * appender 装配与开关、root 级别、file 阈值过滤、rolling policy 属性、totalSizeCap 端到端
 *
 * @since 3.9.2
 */
public class DefaultLogbackConfigurationTest {

    static final String[] PROPS = {
            "solon.logging.appender.file.enable",
            "solon.logging.appender.console.enable",
            "solon.logging.appender.file.name",
            "solon.logging.appender.file.rolling",
            "solon.logging.appender.file.maxFileSize",
            "solon.logging.appender.file.totalSizeCap",
            "solon.logging.appender.file.maxHistory",
            "solon.logging.appender.file.level",
            "solon.logging.logger.root.level"
    };

    File dir;
    LoggerContext ctx;
    Map<String, String> savedProps;

    @BeforeAll
    public static void bootSolon() throws Exception {
        if (Solon.app() == null) {
            Solon.start(DefaultLogbackConfigurationTest.class, new String[]{});
        }
    }

    @AfterAll
    public static void cleanProps() {
        for (String p : PROPS) {
            System.clearProperty(p);
        }
    }

    @BeforeEach
    public void setUp() throws IOException {
        dir = Files.createTempDirectory("logback-default-test").toFile();
        // Collectors.toMap 不接受 null 值，改用 HashMap
        savedProps = new java.util.HashMap<>();
        for (String p : PROPS) {
            savedProps.put(p, Solon.cfg().getProperty(p));
        }
    }

    @AfterEach
    public void tearDown() throws IOException {
        if (ctx != null) {
            ctx.stop();
            ctx = null;
        }
        // 恢复被覆盖的运行期配置（Props 无卸载能力：null 值恢复为默认语义）
        for (Map.Entry<String, String> e : savedProps.entrySet()) {
            if (e.getValue() != null) {
                Solon.cfg().put(e.getKey(), e.getValue());
            } else if ("solon.logging.appender.console.enable".equals(e.getKey())) {
                Solon.cfg().put(e.getKey(), "true");
            }
        }
        deleteRecursively(dir);
    }

    private static void deleteRecursively(File f) throws IOException {
        if (f == null || !f.exists()) {
            return;
        }
        File[] children = f.listFiles();
        if (children != null) {
            for (File c : children) {
                deleteRecursively(c);
            }
        }
        Files.deleteIfExists(f.toPath());
    }

    private void put(String key, String value) {
        Solon.cfg().put(key, value);
    }

    private LoggerContext applyDefault() {
        ctx = new LoggerContext();
        new DefaultLogbackConfiguration().apply(new LogbackConfigurator(ctx));
        return ctx;
    }

    private Logger root() {
        return ctx.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
    }

    private void redirectFileToDir() {
        String base = new File(dir, "app").getPath().replace('\\', '/');
        put("solon.logging.appender.file.name", base);
        put("solon.logging.appender.file.rolling", base + "_%d{yyyy-MM-dd}_%i.log");
    }

    @Test
    public void test_default_assembly_all_appenders() {
        // app.yml 中 file.enable=false，系统属性优先级不足，需改写运行期配置
        put("solon.logging.appender.file.enable", "true");
        put("solon.logging.appender.console.enable", "true");
        redirectFileToDir();
        // 显式清零，避免同类的 totalSizeCap 用例污染（Props 无法卸载）
        put("solon.logging.appender.file.totalSizeCap", "0");

        applyDefault();

        Logger root = root();
        assertEquals(Level.INFO, root.getLevel()); // app.yml: root INFO

        Appender file = root.getAppender("FILE");
        Appender console = root.getAppender("CONSOLE");
        Appender solon = root.getAppender("SOLON");

        assertNotNull(file);
        assertNotNull(console);
        assertNotNull(solon);
        assertTrue(file.isStarted());
        assertTrue(console.isStarted());
        assertTrue(solon.isStarted());

        // FILE：RollingFileAppender + 活动文件路径 + INFO 阈值
        RollingFileAppender rfa = (RollingFileAppender) file;
        assertEquals(new File(dir, "app.log").getPath(), rfa.getFile());

        SizeAndTimeBasedRollingPolicy policy = (SizeAndTimeBasedRollingPolicy) rfa.getRollingPolicy();
        assertEquals(new File(dir, "app").getPath().replace('\\', '/')
                + "_%d{yyyy-MM-dd}_%i.log", policy.getFileNamePattern().replace('\\', '/'));
        assertEquals(7, (int) fieldOf(policy, "maxHistory"));
        // app.yml 将 maxFileSize 覆盖为 1 KB；FileSize 无 equals，比较字节数
        assertEquals(1024L, ((FileSize) fieldOf(policy, "maxFileSize")).getSize());
        assertEquals(0L, ((FileSize) fieldOf(policy, "totalSizeCap")).getSize()); // 默认 0（不启用）

        assertEquals("INFO", thresholdOf(rfa));

        // CONSOLE：阈值来自 app.yml（DEBUG）
        ConsoleAppender<?> ca = (ConsoleAppender<?>) console;
        assertEquals("DEBUG", thresholdOf(ca));

        // SOLON：TRACE
        assertEquals("TRACE", thresholdOf(solon));

        // tags 转换规则已注册
        Map<String, String> registry = (Map<String, String>) ctx.getObject(CoreConstants.PATTERN_RULE_REGISTRY);
        assertNotNull(registry);
        assertEquals(ch.qos.logback.solon.SolonTagsConverter.class.getName(), registry.get("tags"));
    }

    @Test
    public void test_custom_rolling_properties() {
        put("solon.logging.appender.file.enable", "true");
        put("solon.logging.appender.console.enable", "false");
        redirectFileToDir();
        put("solon.logging.appender.file.maxHistory", "3");
        put("solon.logging.appender.file.maxFileSize", "1 KB");
        put("solon.logging.appender.file.totalSizeCap", "3 KB");

        applyDefault();

        RollingFileAppender rfa = (RollingFileAppender) root().getAppender("FILE");
        SizeAndTimeBasedRollingPolicy policy = (SizeAndTimeBasedRollingPolicy) rfa.getRollingPolicy();

        assertEquals(3, (int) fieldOf(policy, "maxHistory"));
        assertEquals(FileSize.valueOf("1 KB").getSize(), ((FileSize) fieldOf(policy, "maxFileSize")).getSize());
        assertEquals(3L * 1024, ((FileSize) fieldOf(policy, "totalSizeCap")).getSize());
    }

    @Test
    public void test_file_disabled() {
        put("solon.logging.appender.file.enable", "false");
        put("solon.logging.appender.console.enable", "true");
        redirectFileToDir();

        applyDefault();

        Logger root = root();
        assertNull(root.getAppender("FILE"));
        assertNotNull(root.getAppender("CONSOLE"));
        assertNotNull(root.getAppender("SOLON"));
    }

    @Test
    public void test_console_disabled() {
        put("solon.logging.appender.file.enable", "true");
        put("solon.logging.appender.console.enable", "false");
        redirectFileToDir();

        applyDefault();

        Logger root = root();
        assertNotNull(root.getAppender("FILE"));
        assertNull(root.getAppender("CONSOLE"));
        assertNotNull(root.getAppender("SOLON"));
    }

    @Test
    public void test_both_disabled_only_solon() {
        put("solon.logging.appender.file.enable", "false");
        put("solon.logging.appender.console.enable", "false");
        redirectFileToDir();

        applyDefault();

        Logger root = root();
        assertNull(root.getAppender("FILE"));
        assertNull(root.getAppender("CONSOLE"));
        assertNotNull(root.getAppender("SOLON"));
    }

    @Test
    public void test_root_level_override() {
        put("solon.logging.appender.file.enable", "false");
        put("solon.logging.appender.console.enable", "false");
        put("solon.logging.logger.root.level", "WARN");

        applyDefault();

        assertEquals(Level.WARN, root().getLevel());
    }

    @Test
    public void test_file_threshold_filters_debug() throws Exception {
        put("solon.logging.appender.file.enable", "true");
        put("solon.logging.appender.console.enable", "false");
        put("solon.logging.appender.file.level", "INFO");
        redirectFileToDir();

        applyDefault();

        // 绕过 logger 事件管线（非全局 LoggerContext 的事件 loggerContext 为空，
        // getMDCPropertyMap 会 NPE），直接向 FILE appender 投递带 MDC 的完整事件
        RollingFileAppender rfa = (RollingFileAppender) root().getAppender("FILE");
        LoggerContext lc = ctx;

        LoggingEvent debug = newEvent(lc, Level.DEBUG, "debug-msg-should-not-exist");
        LoggingEvent info = newEvent(lc, Level.INFO, "info-msg-should-exist");

        rfa.doAppend(debug);
        rfa.doAppend(info);

        File active = new File(dir, "app.log");
        assertTrue(active.exists());
        String content = new String(Files.readAllBytes(active.toPath()), StandardCharsets.UTF_8);
        assertFalse(content.contains("debug-msg-should-not-exist"));
        assertTrue(content.contains("info-msg-should-exist"));
    }

    private LoggingEvent newEvent(LoggerContext lc, Level level, String msg) {
        LoggingEvent e = new LoggingEvent("fqcn", lc.getLogger("probe"), level, msg, null, null);
        e.setMDCPropertyMap(new java.util.LinkedHashMap<>());
        return e;
    }

    @Test
    public void test_appender_name_and_encoder_utf8() {
        put("solon.logging.appender.file.enable", "true");
        put("solon.logging.appender.console.enable", "false");
        redirectFileToDir();

        applyDefault();

        RollingFileAppender rfa = (RollingFileAppender) root().getAppender("FILE");
        assertEquals("FILE", rfa.getName());

        PatternLayoutEncoder encoder = (PatternLayoutEncoder) rfa.getEncoder();
        assertEquals(StandardCharsets.UTF_8, encoder.getCharset());
        assertNotNull(encoder.getPattern());
        assertTrue(encoder.getPattern().contains("%msg"));
    }

    @Test
    public void test_property_substitution_of_app_name() {
        put("solon.logging.appender.file.enable", "false");
        put("solon.logging.appender.console.enable", "false");
        // 显式使用含 ${APP_NAME} 的模板（避免受其它用例对 file.name 的污染影响）
        put("solon.logging.appender.file.name", "logs/${APP_NAME}");
        put("solon.logging.appender.file.rolling", "${FILE_LOG_NAME}_%d{yyyy-MM-dd}_%i.log");

        applyDefault();

        // APP_NAME 来自 solon.app.name（app.yml: demoapp），替换链生效
        assertEquals("logs/demoapp", ctx.getProperty("FILE_LOG_NAME"));
        // 滚动模式引用 FILE_LOG_NAME 已完成替换
        assertEquals("logs/demoapp_%d{yyyy-MM-dd}_%i.log", ctx.getProperty("FILE_LOG_ROLLING"));
    }

    @Test
    public void test_total_size_cap_deletes_oldest_end_to_end() throws Exception {
        put("solon.logging.appender.file.enable", "true");
        put("solon.logging.appender.console.enable", "false");
        redirectFileToDir();
        put("solon.logging.appender.file.maxFileSize", "1 KB");
        put("solon.logging.appender.file.totalSizeCap", "4 KB");

        applyDefault();

        RollingFileAppender rfa = (RollingFileAppender) root().getAppender("FILE");

        String longMsg = repeat('x', 200);
        for (int i = 0; i < 80; i++) {
            rfa.doAppend(newEvent(ctx, Level.INFO, "batch-{} " + longMsg));
        }

        // 滚动/清理是异步的，轮询等待收敛
        long deadline = System.currentTimeMillis() + 15_000;
        while (System.currentTimeMillis() < deadline) {
            if (totalArchivesSize() <= 4 * 1024L) {
                break;
            }
            Thread.sleep(200);
        }

        long total = totalArchivesSize();
        assertTrue(total <= 4 * 1024L, "归档总量应收敛在 totalSizeCap 内，实际: " + total);

        File active = new File(dir, "app.log");
        assertTrue(active.exists(), "活动文件不应被删除");

        // 清理后仍可继续写入
        rfa.doAppend(newEvent(ctx, Level.INFO, "after-clean-marker"));
        String content = new String(Files.readAllBytes(active.toPath()), StandardCharsets.UTF_8);
        assertTrue(content.contains("after-clean-marker"),
                "清理后应能继续写入");
    }

    private long totalArchivesSize() {
        File[] files = dir.listFiles((d, n) -> n.startsWith("app_"));
        if (files == null) {
            return 0;
        }
        long sum = 0;
        for (File f : files) {
            sum += f.length();
        }
        return sum;
    }

    private static String repeat(char c, int n) {
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            sb.append(c);
        }
        return sb.toString();
    }

    /**
     * 读取 policy 私有字段（logback 1.3 未提供 getter）
     */
    private static <T> T fieldOf(Object target, String name) {
        try {
            Class<?> c = target.getClass();
            while (c != null) {
                try {
                    java.lang.reflect.Field f = c.getDeclaredField(name);
                    f.setAccessible(true);
                    return (T) f.get(target);
                } catch (NoSuchFieldException e) {
                    c = c.getSuperclass();
                }
            }
            throw new NoSuchFieldException(name);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * 用 decide 验证 appender 上的 ThresholdFilter 阈值
     */
    private String thresholdOf(Appender appender) {
        ThresholdFilter filter = (ThresholdFilter) appender.getCopyOfAttachedFiltersList().get(0);

        LoggerContext probeCtx = new LoggerContext();
        for (Level level : new Level[]{Level.TRACE, Level.DEBUG, Level.INFO, Level.WARN, Level.ERROR}) {
            LoggingEvent e = new LoggingEvent("fqcn", probeCtx.getLogger("probe"),
                    level, "m", null, null);
            if (filter.decide(e) != ch.qos.logback.core.spi.FilterReply.DENY) {
                return level.toString();
            }
        }
        return "NONE";
    }
}
