/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.core.util;

import java.text.ParseException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 万能时间工具类
 *
 * @author noear
 * @since 2.8
 * @since 3.7
 */
public class DateUtil {
    private static final ZoneId SYSTEM_ZONE = ZoneId.systemDefault();
    private static final Map<String, DateTimeFormatter> FORMATTER_CACHE = new ConcurrentHashMap<>();
    private static final Map<Integer, List<DateTimeFormatter>> LENGTH_BUCKETS = new HashMap<>();
    private static final List<DateTimeFormatter> VARIABLE_FORMATTERS = new ArrayList<>();

    private static final DateTimeFormatter[] COMMON_FORMATTERS = {
            DateTimeFormatter.ISO_LOCAL_DATE_TIME, DateTimeFormatter.ISO_DATE_TIME,
            DateTimeFormatter.ISO_INSTANT, DateTimeFormatter.ISO_ZONED_DATE_TIME,
            DateTimeFormatter.ISO_OFFSET_DATE_TIME, DateTimeFormatter.ISO_LOCAL_DATE,
            DateTimeFormatter.ISO_DATE, DateTimeFormatter.ISO_LOCAL_TIME,
            DateTimeFormatter.ISO_TIME, DateTimeFormatter.RFC_1123_DATE_TIME
    };

    private static final String[] PATTERNS = {
            "yyyy-MM-dd'T'HH:mm:ss.SSSXXX'Z'",
            "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd'T'HH:mm:ss.SSS",
            "yyyy-MM-dd'T'HH:mm:ssXXX",
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            "yyyy-MM-dd'T'HH:mm:ss",

            "yyyy-MM-dd HH:mm:ss.SSS",
            "yyyy-MM-dd HH:mm:ss,SSS",
            "yyyy-MM-dd HH:mm:ss",
            "yyyy/MM/dd HH:mm:ss",
            "yyyy.MM.dd HH:mm:ss",
            "yyyy-MM-dd HH:mm:ss.S",
            "yyyy-MM-dd HH:mm",
            "yyyy/MM/dd HH:mm",
            "yyyy.MM.dd HH:mm",
            "yyyy-MM-dd",
            "yyyy/MM/dd",
            "yyyy.MM.dd",

            "yyyy-M-d H:m:s",
            "yyyy/M/d H:m:s",
            "yyyy.M.d H:m:s",
            "yyyy-M-d",
            "yyyy/M/d",
            "yyyy.M.d",

            "yyyyMMddHHmmssSSSZ",
            "yyyyMMddHHmmssSSS",
            "yyyyMMddHHmmss",
            "yyyyMMdd",

            "yyyy-MM-dd'T'HH:mm:ss+HH:mm",

            "HH:mm:ss",
            "HH:mm:ss.SSS",
            "HH:mm:ss.SSSSSS",
            "HH:mm:ssXXX",
            "HH:mm:ss.SSS+HH:mm",
            "HH时mm分ss秒",

            "H:m:s",
            "H:m"
    };

    static {
        for (String pattern : PATTERNS) {
            DateTimeFormatter fmt = getFormatter(pattern);
            if (isVariableLength(pattern)) {
                VARIABLE_FORMATTERS.add(fmt);
            } else {
                int len = pattern.replace("'", "").length();
                LENGTH_BUCKETS.computeIfAbsent(len, k -> new ArrayList<>()).add(fmt);
            }
        }
    }

    private static boolean isVariableLength(String pattern) {
        String clean = pattern.replaceAll("'[^']*'", "");
        return clean.matches(".*\\b[Mdhms]\\b.*") || clean.contains("-M-") ||
                clean.contains("/M/") || clean.contains(".M.") || clean.contains("-d") ||
                clean.contains("/d") || clean.contains(".d");
    }

    public static Date parseTry(String dateStr) {
        try {
            return parse(dateStr);
        } catch (Exception e) {
            return null;
        }
    }

    public static Date parse(String dateStr) throws ParseException {
        if (dateStr == null) return null;
        String trimmed = dateStr.trim();
        if (trimmed.isEmpty()) return null;

        int len = trimmed.length();
        Date result;

        if ((result = parseFast(trimmed, len)) != null) return result;
        if ((result = parseSpecial(trimmed, len)) != null) return result;
        if ((result = parseSmart(trimmed)) != null) return result;
        if ((result = parseFormatters(trimmed, len)) != null) return result;
        if (isNumeric(trimmed)) return parseTimestamp(trimmed);

        throw new ParseException("Unsupported date format: " + trimmed, 0);
    }

    private static Date parseFast(String s, int len) {
        try {
            switch (len) {
                case 19:
                    if (s.charAt(4) == '-' && s.charAt(7) == '-') {
                        if (s.charAt(10) == ' ') {
                            return Date.from(LocalDateTime.of(
                                    (s.charAt(0) - '0') * 1000 + (s.charAt(1) - '0') * 100 + (s.charAt(2) - '0') * 10 + (s.charAt(3) - '0'),
                                    (s.charAt(5) - '0') * 10 + (s.charAt(6) - '0'),
                                    (s.charAt(8) - '0') * 10 + (s.charAt(9) - '0'),
                                    (s.charAt(11) - '0') * 10 + (s.charAt(12) - '0'),
                                    (s.charAt(14) - '0') * 10 + (s.charAt(15) - '0'),
                                    (s.charAt(17) - '0') * 10 + (s.charAt(18) - '0')
                            ).atZone(SYSTEM_ZONE).toInstant());
                        } else if (s.charAt(10) == 'T') {
                            return parseWithFormatter(s, getFormatter("yyyy-MM-dd'T'HH:mm:ss"));
                        }
                    }
                    break;
                case 10:
                    if (s.charAt(4) == '-' && s.charAt(7) == '-') {
                        return Date.from(LocalDate.of(
                                (s.charAt(0) - '0') * 1000 + (s.charAt(1) - '0') * 100 + (s.charAt(2) - '0') * 10 + (s.charAt(3) - '0'),
                                (s.charAt(5) - '0') * 10 + (s.charAt(6) - '0'),
                                (s.charAt(8) - '0') * 10 + (s.charAt(9) - '0')
                        ).atStartOfDay(SYSTEM_ZONE).toInstant());
                    }
                    break;
                case 14:
                case 8:
                    if (isNumeric(s)) {
                        return parseWithFormatter(s, getFormatter(len == 14 ? "yyyyMMddHHmmss" : "yyyyMMdd"));
                    }
                    break;
                case 23:
                    if (s.charAt(10) == 'T') {
                        return parseWithFormatter(s, getFormatter("yyyy-MM-dd'T'HH:mm:ss.SSS"));
                    } else if (s.charAt(19) == ',') {
                        return parseWithFormatter(s, getFormatter("yyyy-MM-dd HH:mm:ss,SSS"));
                    } else if (s.charAt(19) == '.') {
                        return parseWithFormatter(s, getFormatter("yyyy-MM-dd HH:mm:ss.SSS"));
                    }
                    break;
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private static Date parseSpecial(String s, int len) throws ParseException{
        // 紧凑带时区: 20231025143045123+0800 (FORMAT_22)
        if (len == 22 && (s.charAt(17) == '+' || s.charAt(17) == '-') && isNumeric(s.substring(0, 17))) {
            try {
                return Date.from(OffsetDateTime.of(
                        parseInt(s, 0, 4), parseInt(s, 4, 6), parseInt(s, 6, 8),
                        parseInt(s, 8, 10), parseInt(s, 10, 12), parseInt(s, 12, 14),
                        parseInt(s, 14, 17) * 1_000_000,
                        ZoneOffset.of(s.substring(17).length() == 5 ? s.substring(17, 20) + ":" + s.substring(20) : s.substring(17))
                ).toInstant());
            } catch (Exception ignored) {
            }
        }

        // 中文时间: 14时30分45秒 (FORMAT_9)
        if (s.indexOf('时') > 0 || s.indexOf('分') > 0) {
            try {
                String[] parts = s.split("[时分秒]");
                if (parts.length >= 3) {
                    LocalTime time = LocalTime.of(parseInt(parts[0]), parseInt(parts[1]), parseInt(parts[2]));
                    return Date.from(LocalDateTime.of(LocalDate.now(), time).atZone(SYSTEM_ZONE).toInstant());
                }
            } catch (Exception ignored) {
            }
        }

        return null;
    }

    private static Date parseSmart(String s) throws ParseException {
        if (s.indexOf('T') > 0) {
            try {
                // 处理混合格式: 2023-10-25T14:30:45.123+00:00Z
                if (s.endsWith("Z") && s.contains("+")) {
                    String cleaned = s.substring(0, s.length() - 1);
                    return Date.from(OffsetDateTime.parse(cleaned).toInstant());
                }
                if (s.indexOf('Z') > 0) return Date.from(Instant.parse(s));
                if (s.indexOf('+') > 0 || s.indexOf('-') > 0) return Date.from(OffsetDateTime.parse(s).toInstant());
            } catch (Exception ignored) {
            }
        }

        // 时间格式处理
        if (s.indexOf(':') > 0) {
            Date result = parseTime(s);
            if (result != null) return result;
        }

        // 变长日期处理
        if (s.indexOf('-') > 0 || s.indexOf('/') > 0 || s.indexOf('.') > 0) {
            Date result = parseVariableDate(s);
            if (result != null) return result;
        }

        return null;
    }

    private static Date parseTime(String s) throws ParseException {
        try {
            String normalized = s;
            int dotIdx = s.indexOf('.');
            if (dotIdx > 0) {
                int end = dotIdx + 1;
                while (end < s.length() && Character.isDigit(s.charAt(end))) end++;
                int fractionLen = end - dotIdx - 1;
                // 处理微秒 (FORMAT_15): 14:30:45.123456
                if (fractionLen > 3 && fractionLen <= 6) {
                    normalized = s.substring(0, dotIdx + 4) + s.substring(end);
                }
                // 纳秒位数过多应该抛出异常
                else if (fractionLen > 6) {
                    throw new ParseException("Fraction seconds too long", 0);
                }
            }

            // 带时区的时间 (FORMAT_18, FORMAT_14_b)
            if (normalized.contains("+") || normalized.contains("-")) {
                try {
                    return Date.from(OffsetDateTime.parse(LocalDate.now() + "T" + normalized).toInstant());
                } catch (Exception e) {
                    // 尝试特定格式 HH:mm:ss.SSS+HH:mm
                    LocalTime time = LocalTime.parse(normalized, getFormatter("HH:mm:ss.SSS+HH:mm"));
                    return Date.from(LocalDateTime.of(LocalDate.now(), time).atZone(SYSTEM_ZONE).toInstant());
                }
            }

            DateTimeFormatter fmt = normalized.contains(".") ?
                    getFormatter("HH:mm:ss.SSS") : getFormatter("HH:mm:ss");
            LocalTime time = LocalTime.parse(normalized, fmt);
            return Date.from(LocalDateTime.of(LocalDate.of(1970, 1, 1), time)
                    .atZone(SYSTEM_ZONE)
                    .toInstant());
        } catch (ParseException e) {
            throw e;
        } catch (Exception e) {
            return null;
        }
    }

    private static Date parseVariableDate(String s) {
        try {
            String normalized = s.replace('/', '-').replace('.', '-');
            String[] parts = normalized.split(" ", 2);
            String[] dateParts = parts[0].split("-");

            if (dateParts.length == 3) {
                // 验证日期有效性
                int year = parseInt(dateParts[0]);
                int month = parseInt(dateParts[1]);
                int day = parseInt(dateParts[2]);

                // 基本日期验证 (帮助无效日期测试用例抛出异常)
                if (month < 1 || month > 12 || day < 1 || day > 31) {
                    throw new ParseException("Invalid date", 0);
                }

                StringBuilder std = new StringBuilder()
                        .append(dateParts[0]).append('-')
                        .append(String.format("%02d", month)).append('-')
                        .append(String.format("%02d", day));

                if (parts.length > 1) {
                    String[] timeParts = parts[1].split(":");
                    // 验证时间有效性
                    int hour = parseInt(timeParts[0]);
                    int minute = timeParts.length > 1 ? parseInt(timeParts[1]) : 0;
                    int second = timeParts.length > 2 ? parseInt(timeParts[2]) : 0;

                    if (hour < 0 || hour > 23 || minute < 0 || minute > 59 || second < 0 || second > 59) {
                        throw new ParseException("Invalid time", 0);
                    }

                    std.append(" ").append(String.format("%02d", hour))
                            .append(":").append(String.format("%02d", minute))
                            .append(":").append(String.format("%02d", second));
                }

                return parseFormatters(std.toString(), std.length());
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private static Date parseFormatters(String s, int len) {
        // 1. 尝试标准ISO格式化器
        for (DateTimeFormatter fmt : COMMON_FORMATTERS) {
            Date result = parseWithFormatter(s, fmt);
            if (result != null) return result;
        }

        // 2. 长度分桶匹配
        List<DateTimeFormatter> bucket = LENGTH_BUCKETS.get(len);
        if (bucket != null) {
            for (DateTimeFormatter fmt : bucket) {
                Date result = parseWithFormatter(s, fmt);
                if (result != null) return result;
            }
        }

        // 3. 变长格式
        for (DateTimeFormatter fmt : VARIABLE_FORMATTERS) {
            Date result = parseWithFormatter(s, fmt);
            if (result != null) return result;
        }

        return null;
    }

    private static Date parseWithFormatter(String s, DateTimeFormatter fmt) {
        Instant instant = parseWithFormatter(s, fmt, SYSTEM_ZONE);

        if (instant != null) {
            return Date.from(instant);
        } else {
            return null;
        }
    }

    private static Instant parseWithFormatter(String s, DateTimeFormatter fmt, ZoneId zoneId) {
        try {
            TemporalAccessor accessor = fmt.parse(s);

            if (accessor.isSupported(ChronoField.OFFSET_SECONDS) || accessor.isSupported(ChronoField.INSTANT_SECONDS)) {
                return Instant.from(accessor);
            }

            if (accessor.isSupported(ChronoField.YEAR) && accessor.isSupported(ChronoField.HOUR_OF_DAY)) {
                return LocalDateTime.from(accessor).atZone(zoneId).toInstant();
            }

            if (accessor.isSupported(ChronoField.DAY_OF_MONTH)) {
                return LocalDate.from(accessor).atStartOfDay(zoneId).toInstant();
            }

            if (accessor.isSupported(ChronoField.HOUR_OF_DAY)) {
                return LocalDate.of(1970, 1, 1)
                        .atTime(LocalTime.from(accessor))
                        .atZone(zoneId)
                        .toInstant();
            }

            return Instant.from(accessor);

        } catch (Exception e) {
            return null;
        }
    }

    private static Date parseTimestamp(String s) throws ParseException {
        try {
            long ts = Long.parseLong(s);
            return new Date(s.length() <= 10 ? ts * 1000 : ts);
        } catch (NumberFormatException e) {
            throw new ParseException("Invalid timestamp: " + s, 0);
        }
    }

    // ========== 工具方法 ==========
    private static int parseInt(String s) {
        return parseInt(s, 0, s.length());
    }

    private static int parseInt(CharSequence s, int start, int end) {
        int result = 0;
        for (int i = start; i < end; i++) {
            char c = s.charAt(i);
            if (c >= '0' && c <= '9') {
                result = result * 10 + (c - '0');
            } else {
                throw new NumberFormatException("Invalid digit: " + c);
            }
        }
        return result;
    }

    private static boolean isNumeric(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) < '0' || s.charAt(i) > '9') return false;
        }
        return true;
    }

    private static DateTimeFormatter getFormatter(String pattern) {
        return FORMATTER_CACHE.computeIfAbsent(pattern,
                p -> DateTimeFormatter.ofPattern(p).withZone(SYSTEM_ZONE));
    }

    // ========== 格式化方法 ==========
    public static String format(Date date, String pattern) {
        return date == null || pattern == null ? null :
                getFormatter(pattern)
                        .format(date.toInstant());
    }

    public static String format(Date date, String pattern, ZoneId zoneId) {
        return date == null || pattern == null ? null :
                getFormatter(pattern)
                        .withZone(zoneId)
                        .format(date.toInstant());
    }

    public static String format(Date date, String pattern, TimeZone timeZone) {
        return date == null || pattern == null ? null :
                getFormatter(pattern).withZone(timeZone.toZoneId()).format(date.toInstant());
    }

    public static String toISOString(Date date) {
        return date == null ? null :
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
                        .withZone(ZoneId.of("UTC")).format(date.toInstant());
    }

    public static String toGmtString(Date date) {
        return date == null ? null :
                DateTimeFormatter.RFC_1123_DATE_TIME
                        .withZone(ZoneId.of("GMT")).format(date.toInstant());
    }
}