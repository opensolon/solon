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
package org.apache.logging.log4j.solon.integration;

import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * totalSizeCap 自动删日志效果集成测试：
 * 完全复用 DefaultLog4j2ConfigFactory 的清理范围推导与大小解析逻辑，
 * 构建真实 RollingFile 配置并启动 LoggerContext 实际写入，触发滚动后验证最旧归档被自动删除
 */
public class DefaultLog4j2ConfigFactoryDeleteTest {

    File dir;

    @BeforeEach
    public void setUp() throws IOException {
        dir = Files.createTempDirectory("log4j2-delete-test").toFile();
    }

    @AfterEach
    public void tearDown() {
        if (dir != null && dir.exists()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File f : files) {
                    f.delete();
                }
            }
            dir.delete();
        }
    }

    /**
     * 构建 RollingFile 配置（镜像 DefaultLog4j2ConfigFactory 中 File Appender 的构建逻辑，
     * 清理范围推导/大小解析直接调用工厂的静态方法）
     */
    private LoggerContext buildContext(String fileLogName, String maxFileSize, String totalSizeCap) {
        ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();

        String filePattern = fileLogName + "_%d{yyyy-MM-dd}_%i.log";

        ComponentBuilder<?> policies = builder.newComponent("Policies")
                .addComponent(builder.newComponent("TimeBasedTriggeringPolicy"))
                .addComponent(builder.newComponent("SizeBasedTriggeringPolicy").addAttribute("size", maxFileSize));

        AppenderComponentBuilder fileAppender = builder.newAppender("File", "RollingFile")
                .addAttribute("fileName", fileLogName + ".log")
                .addAttribute("filePattern", filePattern);
        fileAppender.add(builder.newLayout("PatternLayout").addAttribute("pattern", "%msg%n"));
        fileAppender.addComponent(policies);

        ComponentBuilder<?> rolloverStrategy = builder.newComponent("DefaultRolloverStrategy")
                .addAttribute("max", "7");

        // ---- 以下与工厂 createConfiguration 中的 Delete 构建逻辑保持一致 ----
        if (DefaultLog4j2ConfigFactory.parseSizeBytes(totalSizeCap) > 0) {
            String fileName = fileLogName.replaceAll("^.*[\\\\/]", "");
            String[] scope = DefaultLog4j2ConfigFactory.resolveDeleteScope(filePattern);
            String basePath = scope[0];
            int maxDepth = Integer.parseInt(scope[1]);

            rolloverStrategy.addComponent(builder.newComponent("Delete")
                    .addAttribute("basePath", basePath)
                    .addAttribute("maxDepth", String.valueOf(maxDepth))
                    .addComponent(builder.newComponent("IfFileName")
                            .addAttribute("glob", "{" + fileName + "_*.log,**/" + fileName + "_*.log}"))
                    .addComponent(builder.newComponent("IfAccumulatedFileSize")
                            .addAttribute("exceeds", totalSizeCap)));
        }

        fileAppender.addComponent(rolloverStrategy);
        builder.add(fileAppender);
        builder.add(builder.newRootLogger("INFO").add(builder.newAppenderRef("File")));

        LoggerContext ctx = new LoggerContext("delete-test-ctx");
        ctx.start(builder.build());
        return ctx;
    }

    private void writeLogs(LoggerContext ctx, int count, int sizePerLog) {
        org.apache.logging.log4j.Logger log = ctx.getLogger("DeleteTest");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < sizePerLog; i++) {
            sb.append('x');
        }
        for (int i = 0; i < count; i++) {
            log.info("[{}] {}", i, sb.toString());
        }
    }

    private List<File> archivedFiles() {
        File[] files = dir.listFiles((d, name) -> name.matches("app_.*\\.log"));
        if (files == null) {
            return java.util.Collections.emptyList();
        }
        return java.util.Arrays.stream(files)
                .sorted(Comparator.comparingLong(File::lastModified))
                .collect(Collectors.toList());
    }

    private long totalArchivedSize() {
        return archivedFiles().stream().mapToLong(File::length).sum();
    }

    private void awaitDeleted(long capBytes, long timeoutMs) throws InterruptedException {
        // Delete 动作由 DefaultRolloverStrategy 异步执行，需轮询等待
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            if (totalArchivedSize() <= capBytes) {
                return;
            }
            Thread.sleep(200);
        }
    }

    @Test
    public void testTotalSizeCapDeletesOldestArchives() throws Exception {
        // 每个归档约 1.2KB，cap 3KB：累计 3 个归档就应触发删除
        long capBytes = DefaultLog4j2ConfigFactory.parseSizeBytes("3KB");
        LoggerContext ctx = buildContext(new File(dir, "app").getPath(), "1KB", "3KB");

        try {
            writeLogs(ctx, 30, 1100); // 约 30 次滚动

            // 等待异步删除收敛：归档总量不超过 cap + 一个文件的余量
            awaitDeleted(capBytes + 2048, 15000);

            List<File> archives = archivedFiles();
            assertTrue(archives.size() <= 4, "归档数应被限制在少量，实际: " + archives.size());
            assertTrue(totalArchivedSize() <= capBytes + 2048,
                    "归档总量应不超过 totalSizeCap(±一个文件余量)，实际: " + totalArchivedSize());

            // 活动文件绝不能被删（glob 排除了它）
            assertTrue(new File(dir, "app.log").exists(), "活动文件 app.log 必须存在");
        } finally {
            ctx.stop();
        }
    }

    @Test
    public void testTotalSizeCapDisabledKeepsAllArchives() throws Exception {
        // totalSizeCap=0 时不配置 Delete，归档应持续累积（对照组）
        LoggerContext ctx = buildContext(new File(dir, "app").getPath(), "1KB", "0");

        try {
            writeLogs(ctx, 10, 1100);

            Thread.sleep(500); // 无删除动作，稍等归档落定
            List<File> archives = archivedFiles();
            assertTrue(archives.size() >= 5, "未启用 totalSizeCap 时归档应累积，实际: " + archives.size());
        } finally {
            ctx.stop();
        }
    }

    @Test
    public void testActiveFileNeverDeletedEvenWhenCapTiny() throws Exception {
        // 极小 cap：任何滚动后归档全被删光，但活动文件仍在持续写入
        LoggerContext ctx = buildContext(new File(dir, "app").getPath(), "1KB", "100B");

        try {
            writeLogs(ctx, 20, 1100);
            awaitDeleted(2048, 15000);

            File active = new File(dir, "app.log");
            assertTrue(active.exists(), "活动文件必须存在");
            assertTrue(active.length() > 0, "活动文件应持续写入");
            assertEquals(0, archivedFiles().size(), "cap=100B 时归档应全被删除，实际: " + archivedFiles().size());
        } finally {
            ctx.stop();
        }
    }
}
