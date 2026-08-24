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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 清理范围自动推导 + 大小解析 的单元测试
 *
 * @since 3.9.2
 */
public class DefaultLog4j2ConfigFactoryTest {

    private static String[] scope(String filePattern) {
        return DefaultLog4j2ConfigFactory.resolveDeleteScope(filePattern);
    }

    // ---------------- resolveDeleteScope ----------------

    @Test
    public void test_relative_single_dir() {
        // 默认场景：logs/app_%d{...}_%i.log -> basePath=logs, maxDepth=1
        String[] r = scope("logs/app_%d{yyyy-MM-dd}_%i.log");
        assertEquals("logs", r[0]);
        assertEquals(1, Integer.parseInt(r[1]));
    }

    @Test
    public void test_no_dir() {
        // 无目录部分 -> 清理当前目录
        String[] r = scope("app_%d{yyyy-MM-dd}_%i.log");
        assertEquals(".", r[0]);
        assertEquals(1, Integer.parseInt(r[1]));
    }

    @Test
    public void test_relative_nested_dir() {
        // 全静态目录链：basePath 取完整目录，maxDepth=1
        String[] r = scope("logs/2024/app_%d{yyyy-MM-dd}_%i.log");
        assertEquals("logs/2024", r[0]);
        assertEquals(1, Integer.parseInt(r[1]));
    }

    @Test
    public void test_unix_absolute() {
        String[] r = scope("/var/log/app_%d{yyyy-MM-dd}_%i.log");
        assertEquals("/var/log", r[0]);
        assertEquals(1, Integer.parseInt(r[1]));
    }

    @Test
    public void test_unix_absolute_single_dir() {
        String[] r = scope("/logs/app_%d{yyyy-MM-dd}_%i.log");
        assertEquals("/logs", r[0]);
        assertEquals(1, Integer.parseInt(r[1]));
    }

    @Test
    public void test_unix_root_only() {
        // 直接位于根目录：/app_%d.log（早期版本此处会数组越界）
        String[] r = scope("/app_%d{yyyy-MM-dd}_%i.log");
        assertEquals("/", r[0]);
        assertEquals(1, Integer.parseInt(r[1]));
    }

    @Test
    public void test_windows_drive() {
        // Windows 盘符：C:\logs\app...（早期版本会得到盘符相对路径 "C:"）
        String[] r = scope("C:\\logs\\app_%d{yyyy-MM-dd}_%i.log");
        assertEquals("C:/logs", r[0]);
        assertEquals(1, Integer.parseInt(r[1]));
    }

    @Test
    public void test_windows_drive_single_dir() {
        String[] r = scope("C:\\app_%d{yyyy-MM-dd}_%i.log");
        assertEquals("C:/", r[0]);
        assertEquals(1, Integer.parseInt(r[1]));
    }

    @Test
    public void test_backslash_relative() {
        // 反斜杠相对路径（全静态）
        String[] r = scope("logs\\sub\\app_%d.log");
        assertEquals("logs/sub", r[0]);
        assertEquals(1, Integer.parseInt(r[1]));
    }

    @Test
    public void test_double_separators() {
        // 连续分隔符：空段被过滤，不增加深度
        String[] r = scope("logs//sub//app_%d.log");
        assertEquals("logs/sub", r[0]);
        assertEquals(1, Integer.parseInt(r[1]));
    }

    @Test
    public void test_trailing_separator_in_dir() {
        // 目录后紧跟文件名前的多余分隔符
        String[] r = scope("logs//app_%d.log");
        assertEquals("logs", r[0]);
        assertEquals(1, Integer.parseInt(r[1]));
    }

    @Test
    public void test_pattern_with_date_dir() {
        // pattern 中 %d 含子目录（如按月分目录）：basePath 截到静态段 logs，动态段计入深度
        String[] r = scope("logs/%d{yyyy-MM}/app_%i.log");
        assertEquals("logs", r[0]);
        assertEquals(2, Integer.parseInt(r[1]));
    }

    @Test
    public void test_pattern_with_nested_date_dir() {
        // %d 内含多级目录（yyyy/MM/dd）：展开为三级子目录，maxDepth=1+3
        String[] r = scope("logs/%d{yyyy/MM/dd}/app_%i.log");
        assertEquals("logs", r[0]);
        assertEquals(4, Integer.parseInt(r[1]));
    }

    @Test
    public void test_dynamic_first_segment_relative() {
        // 首段即动态段：basePath 退回当前目录
        String[] r = scope("%d{yyyy-MM}/app_%i.log");
        assertEquals(".", r[0]);
        assertEquals(2, Integer.parseInt(r[1]));
    }

    @Test
    public void test_three_levels() {
        // 全静态多级目录：basePath 取完整链，maxDepth=1
        String[] r = scope("/var/log/app/sub/app_%d.log");
        assertEquals("/var/log/app/sub", r[0]);
        assertEquals(1, Integer.parseInt(r[1]));
    }

    @Test
    public void test_windows_drive_forward_slash() {
        // Windows 盘符 + 正斜杠写法：basePath 归一为 C:/logs
        String[] r = scope("C:/logs/app_%d{yyyy-MM-dd}_%i.log");
        assertEquals("C:/logs", r[0]);
        assertEquals(1, Integer.parseInt(r[1]));
    }

    @Test
    public void test_dynamic_mid_chain() {
        // 动态段夹在静态目录之后：其后的段全部计入动态深度
        String[] r = scope("logs/%d{yyyy}/sub/app_%i.log");
        assertEquals("logs", r[0]);
        assertEquals(3, Integer.parseInt(r[1])); // %d{yyyy} + sub 两级动态 + 文件 1 级
    }

    @Test
    public void test_all_dynamic_segments() {
        // 全动态目录链：basePath 退回当前目录，深度为段数+1
        String[] r = scope("%d{yyyy}/%d{MM}/app_%i.log");
        assertEquals(".", r[0]);
        assertEquals(3, Integer.parseInt(r[1]));
    }

    @Test
    public void test_dot_relative_dir() {
        // 显式当前目录前缀："." 作为一段参与拼接
        String[] r = scope("./logs/app_%d.log");
        assertEquals("./logs", r[0]);
        assertEquals(1, Integer.parseInt(r[1]));
    }

    @Test
    public void test_filename_with_multi_dots() {
    // 文件名含点、目录极简：不影响剥离
        String[] r = scope("logs/app.v2_%d.log");
        assertEquals("logs", r[0]);
        assertEquals(1, Integer.parseInt(r[1]));
    }

    // ---------------- parseSizeBytes ----------------

    @Test
    public void test_parse_null() {
        assertEquals(0, DefaultLog4j2ConfigFactory.parseSizeBytes(null));
    }

    @Test
    public void test_parse_empty() {
        assertEquals(0, DefaultLog4j2ConfigFactory.parseSizeBytes(""));
    }

    @Test
    public void test_parse_blank() {
        assertEquals(0, DefaultLog4j2ConfigFactory.parseSizeBytes("   "));
    }

    @Test
    public void test_parse_invalid_format() {
        assertEquals(0, DefaultLog4j2ConfigFactory.parseSizeBytes("abc"));
        assertEquals(0, DefaultLog4j2ConfigFactory.parseSizeBytes("10 MB x"));
        assertEquals(0, DefaultLog4j2ConfigFactory.parseSizeBytes("-1"));
    }

    @Test
    public void test_parse_unknown_unit() {
        assertEquals(0, DefaultLog4j2ConfigFactory.parseSizeBytes("10 XB"));
    }

    @Test
    public void test_parse_number_overflow() {
        // 正则不匹配（e 不在字符类中）：直接走 matches==false 分支
        assertEquals(0, DefaultLog4j2ConfigFactory.parseSizeBytes("1e999 GB"));
    }

    @Test
    public void test_parse_number_format_exception() {
        // 多个小数点：正则匹配成功但 parseDouble 抛 NFE，走 catch 分支返回 0
        assertEquals(0, DefaultLog4j2ConfigFactory.parseSizeBytes("1.2.3 MB"));
        assertEquals(0, DefaultLog4j2ConfigFactory.parseSizeBytes("1.2.3"));
    }

    @Test
    public void test_parse_no_unit() {
        assertEquals(100, DefaultLog4j2ConfigFactory.parseSizeBytes("100"));
    }

    @Test
    public void test_parse_bytes() {
        assertEquals(10, DefaultLog4j2ConfigFactory.parseSizeBytes("10"));
        assertEquals(10, DefaultLog4j2ConfigFactory.parseSizeBytes("10 B"));
        assertEquals(10, DefaultLog4j2ConfigFactory.parseSizeBytes("10b"));
    }

    @Test
    public void test_parse_kb() {
        assertEquals(2 * 1024, DefaultLog4j2ConfigFactory.parseSizeBytes("2 KB"));
        assertEquals(2 * 1024, DefaultLog4j2ConfigFactory.parseSizeBytes("2K"));
    }

    @Test
    public void test_parse_mb() {
        assertEquals(10 * 1024 * 1024, DefaultLog4j2ConfigFactory.parseSizeBytes("10 MB"));
        assertEquals(10 * 1024 * 1024, DefaultLog4j2ConfigFactory.parseSizeBytes("10mb"));
    }

    @Test
    public void test_parse_gb() {
        assertEquals(1L * 1024 * 1024 * 1024, DefaultLog4j2ConfigFactory.parseSizeBytes("1 GB"));
        assertEquals(1L * 1024 * 1024 * 1024, DefaultLog4j2ConfigFactory.parseSizeBytes("1G"));
    }

    @Test
    public void test_parse_tb() {
        assertEquals(1L * 1024 * 1024 * 1024 * 1024, DefaultLog4j2ConfigFactory.parseSizeBytes("1 TB"));
        assertEquals(1L * 1024 * 1024 * 1024 * 1024, DefaultLog4j2ConfigFactory.parseSizeBytes("1T"));
    }

    @Test
    public void test_parse_decimal() {
        assertEquals((long) (1.5 * 1024), DefaultLog4j2ConfigFactory.parseSizeBytes("1.5 KB"));
    }

    @Test
    public void test_parse_with_spaces() {
        assertEquals(10 * 1024 * 1024, DefaultLog4j2ConfigFactory.parseSizeBytes("  10   MB  "));
    }
}
