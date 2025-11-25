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
package org.noear.solon.core.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.text.ParseException;

/**
 * 万能时间工具类
 * 支持解析各种常见时间格式
 *
 * @author noear
 * @since 2.8
 */
public class DateUtil {

    // 常用格式模式
    private static final List<DateTimeFormatter> COMMON_FORMATTERS = Arrays.asList(
            // ISO 格式
            DateTimeFormatter.ISO_LOCAL_DATE_TIME,
            DateTimeFormatter.ISO_DATE_TIME,
            DateTimeFormatter.ISO_INSTANT,
            DateTimeFormatter.ISO_ZONED_DATE_TIME,
            DateTimeFormatter.ISO_OFFSET_DATE_TIME,
            DateTimeFormatter.ISO_LOCAL_DATE,
            DateTimeFormatter.ISO_DATE,
            DateTimeFormatter.ISO_LOCAL_TIME,
            DateTimeFormatter.ISO_TIME,

            // RFC 1123 (HTTP 日期格式)
            DateTimeFormatter.RFC_1123_DATE_TIME
    );

    // 自定义格式化器模式
    private static final List<String> CUSTOM_PATTERNS = Arrays.asList(
            "yyyy-MM-dd HH:mm:ss",
            "yyyy/MM/dd HH:mm:ss",
            "yyyy.MM.dd HH:mm:ss",
            "yyyy-MM-dd HH:mm:ss.SSS",
            "yyyy/MM/dd HH:mm:ss.SSS",
            "yyyy.MM.dd HH:mm:ss.SSS",
            "yyyy-MM-dd HH:mm:ss,SSS",
            "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy-MM-dd'T'HH:mm:ss.SSS",
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
            "yyyy-MM-dd'T'HH:mm:ss.SSSXXX'Z'",
            "yyyy-MM-dd'T'HH:mm:ssXXX",
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            "yyyyMMddHHmmss",
            "yyyyMMddHHmmssSSS",
            "yyyyMMdd",
            "yyyy-MM-dd",
            "yyyy/MM/dd",
            "yyyy.MM.dd",
            "HH:mm:ss",
            "HH:mm:ss.SSS",
            "HH:mm:ss.SSSSSS",
            "HH:mm:ssXXX",
            "HH:mm:ss.SSS+HH:mm",  // FORMAT_18
            "HH时mm分ss秒",
            // FORMAT_16 格式（只到分钟）
            "yyyy-MM-dd HH:mm",
            "yyyy/MM/dd HH:mm",
            "yyyy.MM.dd HH:mm",
            // 单数字模式
            "H:m:s",
            "yyyy-M-d H:m:s",
            "yyyy/M/d H:m:s",
            "yyyy.M.d H:m:s",
            "yyyy-M-d",
            "yyyy/M/d",
            "yyyy.M.d",
            "H:m"                       // 单数字时间（只到分钟）
    );

    // 线程安全的格式化器缓存
    private static final Map<String, DateTimeFormatter> FORMATTER_CACHE = new ConcurrentHashMap<>();

    // 正则表达式模式
    private static final Pattern TIMESTAMP_PATTERN = Pattern.compile("^\\d+$");
    private static final Pattern ISO8601_PATTERN = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}");
    private static final Pattern COMPACT_DATETIME_PATTERN = Pattern.compile("^\\d{14}$");
    private static final Pattern COMPACT_DATE_PATTERN = Pattern.compile("^\\d{8}$");
    private static final Pattern TIME_ONLY_PATTERN = Pattern.compile("^\\d{1,2}:\\d{1,2}(:\\d{1,2})?(\\.\\d{1,9})?([+-]\\d{2}:\\d{2})?$");
    private static final Pattern CHINESE_TIME_PATTERN = Pattern.compile("^\\d{1,2}时\\d{1,2}分\\d{1,2}秒$");
    private static final Pattern COMPACT_DATETIME_WITH_TIMEZONE = Pattern.compile("^\\d{17}[+-]\\d{4}$");

    static {
        // 预加载所有自定义格式化器
        for (String pattern : CUSTOM_PATTERNS) {
            getOrCreateFormatter(pattern);
        }
    }

    /**
     * 尝试解析日期字符串，失败返回 null
     */
    public static Date parseTry(String dateStr) {
        try {
            return parse(dateStr);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 解析日期字符串，支持多种格式
     */
    public static Date parse(String dateStr) throws ParseException {
        if (dateStr == null) {
            return null;
        }

        String trimmed = dateStr.trim();

        if(trimmed.isEmpty()) {
            return null;
        }

        // 1. 尝试中文时间格式
        if (CHINESE_TIME_PATTERN.matcher(trimmed).matches()) {
            return parseChineseTime(trimmed);
        }

        // 2. 尝试紧凑格式带时区 (FORMAT_22)
        if (COMPACT_DATETIME_WITH_TIMEZONE.matcher(trimmed).matches()) {
            return parseCompactDateTimeWithTimezone(trimmed);
        }

        // 3. 尝试常见格式
        Date result = tryCommonFormatters(trimmed);
        if (result != null) {
            return result;
        }

        // 4. 尝试自定义格式
        result = tryCustomFormatters(trimmed);
        if (result != null) {
            return result;
        }

        // 5. 尝试智能解析
        result = trySmartParse(trimmed);
        if (result != null) {
            return result;
        }

        // 6. 首先尝试时间戳
        if (TIMESTAMP_PATTERN.matcher(trimmed).matches()) {
            return parseTimestamp(trimmed);
        }

        throw new ParseException("Unsupported date format: " + trimmed, 0);
    }

    /**
     * 解析时间戳
     */
    private static Date parseTimestamp(String timestampStr) throws ParseException {
        try {
            long timestamp = Long.parseLong(timestampStr);
            // 处理秒级时间戳（10位）和毫秒级时间戳（13位）
            if (timestampStr.length() <= 10) {
                timestamp *= 1000;
            }
            return new Date(timestamp);
        } catch (NumberFormatException e) {
            throw new ParseException("Invalid timestamp: " + timestampStr, 0);
        }
    }

    /**
     * 解析中文时间格式
     */
    private static Date parseChineseTime(String timeStr) throws ParseException {
        try {
            // 提取数字部分
            String[] parts = timeStr.split("[时分秒]");
            if (parts.length >= 3) {
                int hour = Integer.parseInt(parts[0]);
                int minute = Integer.parseInt(parts[1]);
                int second = Integer.parseInt(parts[2]);

                // 组合当前日期
                LocalDateTime localDateTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(hour, minute, second));
                return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
            }
            throw new ParseException("Invalid Chinese time format: " + timeStr, 0);
        } catch (Exception e) {
            throw new ParseException("Failed to parse Chinese time: " + timeStr, 0);
        }
    }

    /**
     * 解析紧凑日期时间带时区格式 (FORMAT_22: yyyyMMddHHmmssSSSZ)
     */
    private static Date parseCompactDateTimeWithTimezone(String dateStr) throws ParseException {
        try {
            // 提取时间和时区部分
            String timePart = dateStr.substring(0, 17); // yyyyMMddHHmmssSSS
            String zonePart = dateStr.substring(17);    // +0800 or -0500

            // 解析时间部分 - 使用自定义解析逻辑
            int year = Integer.parseInt(timePart.substring(0, 4));
            int month = Integer.parseInt(timePart.substring(4, 6));
            int day = Integer.parseInt(timePart.substring(6, 8));
            int hour = Integer.parseInt(timePart.substring(8, 10));
            int minute = Integer.parseInt(timePart.substring(10, 12));
            int second = Integer.parseInt(timePart.substring(12, 14));
            int millis = Integer.parseInt(timePart.substring(14, 17));

            LocalDateTime localDateTime = LocalDateTime.of(year, month, day, hour, minute, second, millis * 1_000_000);

            // 解析时区部分
            String normalizedZone = zonePart.substring(0, 3) + ":" + zonePart.substring(3);
            ZoneOffset zoneOffset = ZoneOffset.of(normalizedZone);

            OffsetDateTime offsetDateTime = OffsetDateTime.of(localDateTime, zoneOffset);
            return Date.from(offsetDateTime.toInstant());

        } catch (Exception e) {
            throw new ParseException("Failed to parse compact datetime with timezone: " + dateStr, 0);
        }
    }

    /**
     * 尝试常见格式化器
     */
    private static Date tryCommonFormatters(String dateStr) {
        for (DateTimeFormatter formatter : COMMON_FORMATTERS) {
            try {
                return parseWithFormatter(dateStr, formatter);
            } catch (DateTimeParseException e) {
                // 继续尝试下一个格式化器
            }
        }
        return null;
    }

    /**
     * 尝试自定义格式化器
     */
    private static Date tryCustomFormatters(String dateStr) {
        for (String pattern : CUSTOM_PATTERNS) {
            try {
                DateTimeFormatter formatter = getOrCreateFormatter(pattern);
                return parseWithFormatter(dateStr, formatter);
            } catch (DateTimeParseException e) {
                // 继续尝试下一个格式化器
            }
        }
        return null;
    }

    /**
     * 智能解析
     */
    private static Date trySmartParse(String dateStr) {
        // 根据字符串特征选择解析策略
        if (dateStr.contains("T") && dateStr.contains("Z")) {
            return parseISOWithZ(dateStr);
        } else if (dateStr.contains("T") && (dateStr.contains("+") || dateStr.contains("-"))) {
            return parseISOWithOffset(dateStr);
        } else if (COMPACT_DATETIME_PATTERN.matcher(dateStr).matches()) {
            return parseCompactDateTime(dateStr);
        } else if (COMPACT_DATE_PATTERN.matcher(dateStr).matches()) {
            return parseCompactDate(dateStr);
        } else if (TIME_ONLY_PATTERN.matcher(dateStr).matches()) {
            return parseTimeWithOptionalOffset(dateStr);
        } else if (dateStr.matches("\\d{4}[-/.]\\d{1,2}[-/.]\\d{1,2}.*")) {
            return parseDateWithVariousSeparators(dateStr);
        }

        return null;
    }

    /**
     * 解析带 Z 的 ISO 格式
     */
    private static Date parseISOWithZ(String dateStr) {
        try {
            Instant instant = Instant.parse(dateStr);
            return Date.from(instant);
        } catch (DateTimeParseException e) {
            // 尝试其他变体
            if (dateStr.endsWith("Z") && dateStr.contains("+")) {
                // 处理 "2023-10-25T14:30:45.123+00:00Z" 这种混合格式
                String cleaned = dateStr.substring(0, dateStr.length() - 1);
                return parseWithFormatter(cleaned, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            }
        }
        return null;
    }

    /**
     * 解析带时区偏移的 ISO 格式
     */
    private static Date parseISOWithOffset(String dateStr) {
        try {
            // 尝试 OffsetDateTime
            OffsetDateTime offsetDateTime = OffsetDateTime.parse(dateStr);
            return Date.from(offsetDateTime.toInstant());
        } catch (DateTimeParseException e1) {
            try {
                // 尝试 ZonedDateTime
                ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateStr);
                return Date.from(zonedDateTime.toInstant());
            } catch (DateTimeParseException e2) {
                // 最后尝试 Instant
                try {
                    Instant instant = Instant.parse(dateStr);
                    return Date.from(instant);
                } catch (DateTimeParseException e3) {
                    return null;
                }
            }
        }
    }

    /**
     * 解析紧凑日期时间格式 (yyyyMMddHHmmss)
     */
    private static Date parseCompactDateTime(String dateStr) {
        try {
            // 使用更精确的解析方式确保时间一致性
            int year = Integer.parseInt(dateStr.substring(0, 4));
            int month = Integer.parseInt(dateStr.substring(4, 6));
            int day = Integer.parseInt(dateStr.substring(6, 8));
            int hour = Integer.parseInt(dateStr.substring(8, 10));
            int minute = Integer.parseInt(dateStr.substring(10, 12));
            int second = Integer.parseInt(dateStr.substring(12, 14));

            LocalDateTime localDateTime = LocalDateTime.of(year, month, day, hour, minute, second);
            return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        } catch (Exception e) {
            // 如果精确解析失败，回退到格式化器方式
            DateTimeFormatter formatter = getOrCreateFormatter("yyyyMMddHHmmss");
            LocalDateTime localDateTime = LocalDateTime.parse(dateStr, formatter);
            return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        }
    }

    /**
     * 解析紧凑日期格式 (yyyyMMdd)
     */
    private static Date parseCompactDate(String dateStr) {
        DateTimeFormatter formatter = getOrCreateFormatter("yyyyMMdd");
        LocalDate localDate = LocalDate.parse(dateStr, formatter);
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 解析时间（可能带时区偏移）
     */
    private static Date parseTimeWithOptionalOffset(String dateStr) {
        try {
            // 处理微秒（6位小数）
            if (dateStr.matches("\\d{1,2}:\\d{2}:\\d{2}\\.\\d{6}")) {
                // 将微秒转换为纳秒（前3位作为毫秒，后3位忽略或作为纳秒）
                String[] parts = dateStr.split("\\.");
                String timePart = parts[0];
                String micros = parts[1];

                // 取前3位作为毫秒
                String millis = micros.substring(0, Math.min(3, micros.length()));
                String normalized = timePart + "." + millis;

                DateTimeFormatter formatter = getOrCreateFormatter("HH:mm:ss.SSS");
                LocalTime localTime = LocalTime.parse(normalized, formatter);
                LocalDateTime localDateTime = LocalDateTime.of(LocalDate.now(), localTime);
                return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
            }

            // 处理带时区偏移的时间
            if (dateStr.contains("+") || dateStr.contains("-")) {
                // 构建完整的日期时间字符串（使用当前日期）
                String currentDate = LocalDate.now().toString();
                String fullDateTime = currentDate + "T" + dateStr;

                try {
                    OffsetDateTime offsetDateTime = OffsetDateTime.parse(fullDateTime);
                    return Date.from(offsetDateTime.toInstant());
                } catch (DateTimeParseException e) {
                    // 尝试 FORMAT_18: HH:mm:ss.SSS+HH:mm
                    if (dateStr.matches("\\d{2}:\\d{2}:\\d{2}\\.\\d{3}[+-]\\d{2}:\\d{2}")) {
                        DateTimeFormatter formatter = getOrCreateFormatter("HH:mm:ss.SSS+HH:mm");
                        LocalTime localTime = LocalTime.parse(dateStr, formatter);
                        LocalDateTime localDateTime = LocalDateTime.of(LocalDate.now(), localTime);
                        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
                    }
                }
            }

            // 普通时间解析
            DateTimeFormatter formatter;
            if (dateStr.contains(".")) {
                formatter = getOrCreateFormatter("HH:mm:ss.SSS");
            } else {
                formatter = getOrCreateFormatter("HH:mm:ss");
            }

            LocalTime localTime = LocalTime.parse(dateStr, formatter);
            LocalDateTime localDateTime = LocalDateTime.of(LocalDate.now(), localTime);
            return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 解析各种分隔符的日期格式
     */
    private static Date parseDateWithVariousSeparators(String dateStr) {
        try {
            // 处理只到分钟的时间格式 (FORMAT_16)
            if (dateStr.matches("\\d{4}[-/.]\\d{1,2}[-/.]\\d{1,2} \\d{1,2}:\\d{1,2}$")) {
                String normalized = dateStr.replace('/', '-').replace('.', '-');
                String[] mainParts = normalized.split(" ");
                String datePart = mainParts[0];
                String timePart = mainParts[1] + ":00"; // 补全秒数

                // 标准化日期部分
                String[] dateParts = datePart.split("-");
                String year = dateParts[0];
                String month = String.format("%02d", Integer.parseInt(dateParts[1]));
                String day = String.format("%02d", Integer.parseInt(dateParts[2]));
                String standardDate = year + "-" + month + "-" + day;

                // 标准化时间部分
                String[] timeParts = timePart.split(":");
                String hour = String.format("%02d", Integer.parseInt(timeParts[0]));
                String minute = String.format("%02d", Integer.parseInt(timeParts[1]));
                String second = String.format("%02d", Integer.parseInt(timeParts[2]));
                String standardTime = hour + ":" + minute + ":" + second;

                String standardDateTime = standardDate + " " + standardTime;
                return parseWithFormatter(standardDateTime, getOrCreateFormatter("yyyy-MM-dd HH:mm:ss"));
            }

            // 标准化分隔符
            String normalized = dateStr.replace('/', '-').replace('.', '-');

            // 处理单数字日期时间
            if (normalized.matches("\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}")) {
                String[] mainParts = normalized.split(" ");
                String datePart = mainParts[0];
                String timePart = mainParts[1];

                // 标准化日期部分
                String[] dateParts = datePart.split("-");
                String year = dateParts[0];
                String month = String.format("%02d", Integer.parseInt(dateParts[1]));
                String day = String.format("%02d", Integer.parseInt(dateParts[2]));
                String standardDate = year + "-" + month + "-" + day;

                // 标准化时间部分
                String[] timeParts = timePart.split(":");
                String hour = String.format("%02d", Integer.parseInt(timeParts[0]));
                String minute = String.format("%02d", Integer.parseInt(timeParts[1]));
                String second = String.format("%02d", Integer.parseInt(timeParts[2]));
                String standardTime = hour + ":" + minute + ":" + second;

                String standardDateTime = standardDate + " " + standardTime;
                return parseWithFormatter(standardDateTime, getOrCreateFormatter("yyyy-MM-dd HH:mm:ss"));
            }

            // 提取日期部分
            String datePart = normalized.split(" ")[0];
            String[] dateParts = datePart.split("-");

            if (dateParts.length == 3) {
                // 补齐月份和日期为两位
                String year = dateParts[0];
                String month = String.format("%02d", Integer.parseInt(dateParts[1]));
                String day = String.format("%02d", Integer.parseInt(dateParts[2]));

                String standardDate = year + "-" + month + "-" + day;

                // 如果有时间部分，也标准化
                if (normalized.contains(" ")) {
                    String timePart = normalized.split(" ", 2)[1];
                    // 标准化时间部分
                    if (timePart.matches("\\d{1,2}:\\d{1,2}:\\d{1,2}")) {
                        String[] timeParts = timePart.split(":");
                        String hour = String.format("%02d", Integer.parseInt(timeParts[0]));
                        String minute = String.format("%02d", Integer.parseInt(timeParts[1]));
                        String second = String.format("%02d", Integer.parseInt(timeParts[2]));
                        timePart = hour + ":" + minute + ":" + second;
                    }
                    standardDate += " " + timePart;
                }

                return tryCustomFormatters(standardDate);
            }
        } catch (Exception e) {
            // 忽略异常，继续其他解析方式
        }
        return null;
    }

    /**
     * 使用指定格式化器解析
     */
    private static Date parseWithFormatter(String dateStr, DateTimeFormatter formatter) throws DateTimeParseException {
        try {
            // 尝试 LocalDateTime
            LocalDateTime localDateTime = LocalDateTime.parse(dateStr, formatter);
            return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        } catch (DateTimeParseException e1) {
            try {
                // 尝试 LocalDate
                LocalDate localDate = LocalDate.parse(dateStr, formatter);
                return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            } catch (DateTimeParseException e2) {
                try {
                    // 尝试 ZonedDateTime
                    ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateStr, formatter);
                    return Date.from(zonedDateTime.toInstant());
                } catch (DateTimeParseException e3) {
                    try {
                        // 尝试 OffsetDateTime
                        OffsetDateTime offsetDateTime = OffsetDateTime.parse(dateStr, formatter);
                        return Date.from(offsetDateTime.toInstant());
                    } catch (DateTimeParseException e4) {
                        try {
                            // 尝试 Instant
                            Instant instant = Instant.parse(dateStr);
                            return Date.from(instant);
                        } catch (DateTimeParseException e5) {
                            throw new DateTimeParseException("Failed to parse with formatter: " + formatter, dateStr, 0);
                        }
                    }
                }
            }
        }
    }

    /**
     * 获取或创建格式化器
     */
    private static DateTimeFormatter getOrCreateFormatter(String pattern) {
        return FORMATTER_CACHE.computeIfAbsent(pattern,
                p -> DateTimeFormatter.ofPattern(p).withZone(ZoneId.systemDefault()));
    }

    /**
     * 格式化为字符串（使用系统默认时区）
     */
    public static String format(Date date, String pattern) {
        if (date == null || pattern == null) {
            return null;
        }

        DateTimeFormatter formatter = getOrCreateFormatter(pattern);
        return formatter.format(date.toInstant().atZone(ZoneId.systemDefault()));
    }

    /**
     * 格式化为指定时区的字符串
     */
    public static String format(Date date, String pattern, TimeZone timeZone) {
        if (date == null || pattern == null || timeZone == null) {
            return null;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern)
                .withZone(timeZone.toZoneId());
        return formatter.format(date.toInstant());
    }

    /**
     * 格式化为 ISO 8601 字符串 (UTC 时间)
     */
    public static String toISOString(Date date) {
        if (date == null) {
            return null;
        }
        // 使用 UTC 时区格式化为 ISO 字符串
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
                .withZone(ZoneId.of("UTC"));
        return formatter.format(date.toInstant());
    }

    /**
     * 格式化为 GMT 字符串 (RFC 1123 格式)
     */
    public static String toGmtString(Date date) {
        if (date == null) {
            return null;
        }
        // 使用 RFC 1123 格式，包含 GMT
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss 'GMT'")
                .withZone(ZoneId.of("GMT"))
                .withLocale(Locale.US);
        return formatter.format(date.toInstant());
    }
}