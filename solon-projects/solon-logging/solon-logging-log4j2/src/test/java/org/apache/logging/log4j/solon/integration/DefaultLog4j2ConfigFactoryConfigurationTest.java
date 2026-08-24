/*
 *  Copyright 2017-2025 noear.org and authors
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.logging.log4j.solon.integration;

import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.noear.solon.Solon;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * createConfiguration 全链路场景测试：
 * console/file 开关、appender 装配、root logger 引用，以及 totalSizeCap 经由工厂真实配置后的端到端删除效果
 *
 * @since 3.9.2
 */
public class DefaultLog4j2ConfigFactoryConfigurationTest {

    static final String[] PROPS = {
            "solon.logging.appender.console.enable",
            "solon.logging.appender.file.enable",
            "solon.logging.appender.file.name",
            "solon.logging.appender.file.maxFileSize",
            "solon.logging.appender.file.totalSizeCap",
            "solon.logging.appender.console.pattern",
            "solon.logging.appender.console.level",
            "solon.logging.appender.file.pattern",
            "solon.logging.appender.file.level",
            "solon.logging.appender.file.maxHistory",
            "solon.logging.logger.root.level"
    };

    File dir;
    LoggerContext ctx;

    @BeforeAll
    public static void bootSolon() {
        // createConfiguration 依赖 Solon.cfg()，需 Solon 已启动（同 JVM 中其它 @SolonTest 已启动则跳过）
        if (Solon.app() == null) {
            Solon.start(DefaultLog4j2ConfigFactoryConfigurationTest.class, new String[]{});
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
        dir = Files.createTempDirectory("log4j2-factory-test").toFile();
    }

    @AfterEach
    public void tearDown() {
        if (ctx != null) {
            ctx.stop();
            ctx = null;
        }
        if (dir != null && dir.exists()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File f : files) {
                    f.delete();
                }
            }
            dir.delete();
        }
        for (String p : PROPS) {
            System.clearProperty(p);
        }
    }

    private LoggerContext startFactoryContext(boolean fileEnable, boolean consoleEnable) {
        // app.yml 中 file.enable=false；系统属性优先级不够，直接改写运行期配置
        Solon.cfg().put("solon.logging.appender.file.enable", String.valueOf(fileEnable));
        Solon.cfg().put("solon.logging.appender.console.enable", String.valueOf(consoleEnable));
        ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();
        Configuration config = DefaultLog4j2ConfigFactory.createConfiguration(builder);
        ctx = new LoggerContext("factory-test-ctx");
        ctx.start(config);
        return ctx;
    }

    private List<String> appenderNames(LoggerContext ctx) {
        return ctx.getConfiguration().getAppenders().keySet().stream().sorted().collect(Collectors.toList());
    }

    private List<String> rootRefs(LoggerContext ctx) {
        return ctx.getConfiguration().getRootLogger().getAppenderRefs().stream()
                .map(r -> r.getRef()).sorted().collect(Collectors.toList());
    }

    // ---------------- 开关与装配 ----------------

    @Test
    public void test_default_all_appenders() {
        // 默认（不设任何属性）：Console + File + Solon 三个 appender，root 全部引用
        System.setProperty("solon.logging.appender.file.name", new File(dir, "app").getPath());
        LoggerContext ctx = startFactoryContext(true, true);

        assertEquals(Arrays.asList("Console", "File", "Solon"), appenderNames(ctx));
        assertEquals(Arrays.asList("Console", "File", "Solon"), rootRefs(ctx));

        // File appender 应为 RollingFile，活动文件指向配置的 file.name + ".log"
        RollingFileAppender fileAppender = (RollingFileAppender) ctx.getConfiguration().getAppenders().get("File");
        assertNotNull(fileAppender);
        assertEquals(new File(dir, "app.log").getPath(), fileAppender.getFileName());
    }

    @Test
    public void test_console_disabled() {
        System.setProperty("solon.logging.appender.console.enable", "false");
        System.setProperty("solon.logging.appender.file.name", new File(dir, "app").getPath());
        LoggerContext ctx = startFactoryContext(true, false);

        assertEquals(Arrays.asList("File", "Solon"), appenderNames(ctx));
        assertEquals(Arrays.asList("File", "Solon"), rootRefs(ctx));
    }

    @Test
    public void test_file_disabled() {
        System.setProperty("solon.logging.appender.file.enable", "false");
        LoggerContext ctx = startFactoryContext(false, true);

        assertEquals(Arrays.asList("Console", "Solon"), appenderNames(ctx));
        assertEquals(Arrays.asList("Console", "Solon"), rootRefs(ctx));
        assertFalse(new File(dir, "app.log").exists(), "file 关闭时不应产生日志文件");
    }

    @Test
    public void test_both_disabled() {
        // 双关闭：仅剩 Solon appender，root 只引用 Solon（极端但合法的配置）
        System.setProperty("solon.logging.appender.console.enable", "false");
        System.setProperty("solon.logging.appender.file.enable", "false");
        LoggerContext ctx = startFactoryContext(false, false);

        assertEquals(Arrays.asList("Solon"), appenderNames(ctx));
        assertEquals(Arrays.asList("Solon"), rootRefs(ctx));
    }

    @Test
    public void test_file_level_filter_blocks_low_level() {
        // file.level 默认 INFO：经工厂配置后 debug 日志不落盘
        System.setProperty("solon.logging.appender.file.name", new File(dir, "app").getPath());
        LoggerContext ctx = startFactoryContext(true, true);

        org.apache.logging.log4j.Logger log = ctx.getLogger("FactoryTest");
        log.info("info-message");
        log.debug("debug-message");

        String content = readFile(new File(dir, "app.log"));
        assertTrue(content.contains("info-message"), "INFO 应写入文件");
        assertFalse(content.contains("debug-message"), "DEBUG 不应写入文件（file.level 默认 INFO）");
    }

    private String readFile(File f) {
        try {
            return new String(Files.readAllBytes(f.toPath()), "UTF-8");
        } catch (IOException e) {
            return "";
        }
    }

    // ---------------- totalSizeCap 端到端（走工厂真实配置路径） ----------------

    @Test
    public void test_totalSize_cap_via_factory_end_to_end() throws Exception {
        // 完全经由工厂配置（含 file.name 剥离文件名、resolveDeleteScope 推导），
        // 验证 totalSizeCap 生效：归档收敛在 cap 内，活动文件健在
        System.setProperty("solon.logging.appender.file.name", new File(dir, "app").getPath());
        System.setProperty("solon.logging.appender.file.maxFileSize", "1KB");
        System.setProperty("solon.logging.appender.file.totalSizeCap", "3KB");

        LoggerContext ctx = startFactoryContext(true, true);

        org.apache.logging.log4j.Logger log = ctx.getLogger("FactoryCapTest");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1100; i++) {
            sb.append('x');
        }
        for (int i = 0; i < 30; i++) {
            log.info("[{}] {}", i, sb.toString());
        }

        // 等待异步删除收敛
        long deadline = System.currentTimeMillis() + 15000;
        List<File> archives = listArchives();
        while (System.currentTimeMillis() < deadline) {
            archives = listArchives();
            long total = archives.stream().mapToLong(File::length).sum();
            if (archives.size() <= 4 && total <= 3 * 1024 + 2048) {
                break;
            }
            Thread.sleep(200);
        }

        long total = archives.stream().mapToLong(File::length).sum();
        assertTrue(archives.size() <= 4, "归档数应被限制，实际: " + archives.size());
        assertTrue(total <= 3 * 1024 + 2048, "归档总量应收敛在 cap(±一个文件)，实际: " + total);
        assertTrue(new File(dir, "app.log").exists(), "活动文件必须存在");

        // 后续仍能继续写入（删除未破坏 appender；刚滚动后活动文件可能被重置，只验证存在且未抛异常）
        log.info("still-alive");
        Thread.sleep(200);
        assertTrue(new File(dir, "app.log").exists(), "cap 清理后应仍可继续写入");
    }

    private List<File> listArchives() {
        File[] files = dir.listFiles((d, name) -> name.matches("app_.*\\.log"));
        if (files == null) {
            return java.util.Collections.emptyList();
        }
        return Arrays.stream(files)
                .sorted(Comparator.comparingLong(File::lastModified))
                .collect(Collectors.toList());
    }
}
