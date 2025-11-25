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
 * 万能时间工具类 (Ultimate Optimized Version)
 *
 * @author noear
 * @since 2.8
 */
public class DateUtil {
    private static final ZoneId SYSTEM_ZONE = ZoneId.systemDefault();
    private static final Map<String, DateTimeFormatter> FORMATTER_CACHE = new ConcurrentHashMap<>();
    private static final Map<Integer, DateTimeFormatter[]> LENGTH_BUCKETS = new HashMap<>(); // 改为数组
    private static final DateTimeFormatter[] VARIABLE_FORMATTERS; // 改为数组

    // 预定义高频格式化器（避免重复查找）
    private static final DateTimeFormatter FMT_YMD_HMS = createFormatter("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter FMT_YMD = createFormatter("yyyy-MM-dd");
    private static final DateTimeFormatter FMT_COMPACT_DT = createFormatter("yyyyMMddHHmmss");
    private static final DateTimeFormatter FMT_COMPACT_D = createFormatter("yyyyMMdd");
    private static final DateTimeFormatter FMT_ISO_T = createFormatter("yyyy-MM-dd'T'HH:mm:ss");
    private static final DateTimeFormatter FMT_ISO_TS = createFormatter("yyyy-MM-dd'T'HH:mm:ss.SSS");
    private static final DateTimeFormatter FMT_TIME = createFormatter("HH:mm:ss");
    private static final DateTimeFormatter FMT_TIME_MS = createFormatter("HH:mm:ss.SSS");
    private static final DateTimeFormatter FMT_TIME_TZ = createFormatter("HH:mm:ss.SSS+HH:mm");

    private static final DateTimeFormatter[] COMMON_FORMATTERS = {
            DateTimeFormatter.ISO_LOCAL_DATE_TIME, DateTimeFormatter.ISO_DATE_TIME,
            DateTimeFormatter.ISO_INSTANT, DateTimeFormatter.ISO_ZONED_DATE_TIME,
            DateTimeFormatter.ISO_OFFSET_DATE_TIME, DateTimeFormatter.ISO_LOCAL_DATE,
            DateTimeFormatter.ISO_DATE, DateTimeFormatter.ISO_LOCAL_TIME,
            DateTimeFormatter.ISO_TIME, DateTimeFormatter.RFC_1123_DATE_TIME
    };

    private static final String[] PATTERNS = {
            "yyyy-MM-dd HH:mm:ss", "yyyy/MM/dd HH:mm:ss", "yyyy.MM.dd HH:mm:ss",
            "yyyy-MM-dd", "yyyy/MM/dd", "yyyy.MM.dd", "yyyyMMddHHmmss", "yyyyMMdd",
            "HH:mm:ss", "yyyy-MM-dd HH:mm:ss.SSS", "yyyy-MM-dd HH:mm:ss,SSS",
            "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss.SSS",
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
            "yyyy-MM-dd'T'HH:mm:ss.SSSXXX'Z'", "yyyy-MM-dd'T'HH:mm:ssXXX",
            "yyyy-MM-dd'T'HH:mm:ss'Z'", "yyyyMMddHHmmssSSS",
            "HH:mm:ss.SSS", "HH:mm:ss.SSSSSS", "HH:mm:ssXXX", "HH:mm:ss.SSS+HH:mm",
            "HH时mm分ss秒", "yyyy-MM-dd HH:mm", "yyyy/MM/dd HH:mm", "yyyy.MM.dd HH:mm"
    };

    private static final String[] VARIABLE_PATTERNS = {
            "H:m:s", "yyyy-M-d H:m:s", "yyyy/M/d H:m:s", "yyyy.M.d H:m:s",
            "yyyy-M-d", "yyyy/M/d", "yyyy.M.d", "H:m"
    };

    static {
        // 初始化变长格式化器数组
        List<DateTimeFormatter> variableList = new ArrayList<>();
        for (String pattern : VARIABLE_PATTERNS) {
            variableList.add(createFormatter(pattern));
        }
        VARIABLE_FORMATTERS = variableList.toArray(new DateTimeFormatter[0]);

        // 初始化长度分桶（使用数组提升性能）
        for (String pattern : PATTERNS) {
            if (!isVariableLength(pattern)) {
                DateTimeFormatter fmt = createFormatter(pattern);
                int len = pattern.replace("'", "").length();
                LENGTH_BUCKETS.compute(len, (k, v) -> {
                    if (v == null) return new DateTimeFormatter[]{fmt};
                    // 数组扩容逻辑
                    DateTimeFormatter[] newArr = Arrays.copyOf(v, v.length + 1);
                    newArr[v.length] = fmt;
                    return newArr;
                });
            }
        }
    }

    private static DateTimeFormatter createFormatter(String pattern) {
        return DateTimeFormatter.ofPattern(pattern).withZone(SYSTEM_ZONE);
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

        // 优化：避免不必要的trim
        int start = 0, end = dateStr.length();
        while (start < end && dateStr.charAt(start) <= ' ') start++;
        while (end > start && dateStr.charAt(end - 1) <= ' ') end--;

        if (start >= end) return null;
        String trimmed = start > 0 || end < dateStr.length() ? dateStr.substring(start, end) : dateStr;

        int len = trimmed.length();
        Date result;

        if ((result = parseFast(trimmed, len)) != null) return result;
        if ((result = parseSpecial(trimmed, len)) != null) return result;
        if ((result = parseSmart(trimmed)) != null) return result;
        if ((result = parseFormatters(trimmed, len)) != null) return result;
        if (isAllDigits(trimmed)) return parseTimestamp(trimmed);

        throw new ParseException("Unsupported date format: " + trimmed, 0);
    }

    private static Date parseFast(String s, int len) {
        try {
            switch (len) {
                case 19:
                    if (s.charAt(4) == '-' && s.charAt(7) == '-') {
                        if (s.charAt(10) == ' ') {
                            // 极致性能：手动解析，避免方法调用
                            return Date.from(LocalDateTime.of(
                                    fastParseInt(s,0,4), fastParseInt(s,5,7), fastParseInt(s,8,10),
                                    fastParseInt(s,11,13), fastParseInt(s,14,16), fastParseInt(s,17,19)
                            ).atZone(SYSTEM_ZONE).toInstant());
                        } else if (s.charAt(10) == 'T') {
                            return parseWithFormatter(s, FMT_ISO_T);
                        }
                    }
                    break;
                case 10:
                    if (s.charAt(4) == '-' && s.charAt(7) == '-') {
                        return Date.from(LocalDate.of(
                                fastParseInt(s,0,4), fastParseInt(s,5,7), fastParseInt(s,8,10)
                        ).atStartOfDay(SYSTEM_ZONE).toInstant());
                    }
                    break;
                case 14:
                    if (isAllDigits(s)) return parseWithFormatter(s, FMT_COMPACT_DT);
                    break;
                case 8:
                    if (isAllDigits(s)) return parseWithFormatter(s, FMT_COMPACT_D);
                    break;
                case 23:
                    if (s.charAt(10) == 'T') return parseWithFormatter(s, FMT_ISO_TS);
                    else if (s.charAt(19) == ',') return parseWithFormatter(s, getFormatter("yyyy-MM-dd HH:mm:ss,SSS"));
                    else if (s.charAt(19) == '.') return parseWithFormatter(s, getFormatter("yyyy-MM-dd HH:mm:ss.SSS"));
                    break;
            }
        } catch (Exception ignored) {}
        return null;
    }

    // 极致性能的整数解析
    private static int fastParseInt(String s, int start, int end) {
        int result = 0;
        for (int i = start; i < end; i++) {
            result = result * 10 + (s.charAt(i) - '0');
        }
        return result;
    }

    private static Date parseSpecial(String s, int len) {
        // 紧凑带时区: 20231025143045123+0800
        if (len == 22 && (s.charAt(17) == '+' || s.charAt(17) == '-') && isAllDigits(s.substring(0, 17))) {
            try {
                return Date.from(OffsetDateTime.of(
                        fastParseInt(s,0,4), fastParseInt(s,4,6), fastParseInt(s,6,8),
                        fastParseInt(s,8,10), fastParseInt(s,10,12), fastParseInt(s,12,14),
                        fastParseInt(s,14,17) * 1_000_000,
                        ZoneOffset.of(s.substring(17).length() == 5 ?
                                s.substring(17,20) + ":" + s.substring(20) : s.substring(17))
                ).toInstant());
            } catch (Exception ignored) {}
        }

        // 中文时间
        if (s.indexOf('时') > 0 || s.indexOf('分') > 0) {
            try {
                String[] parts = s.split("[时分秒]");
                if (parts.length >= 3) {
                    LocalTime time = LocalTime.of(fastParseInt(parts[0]), fastParseInt(parts[1]), fastParseInt(parts[2]));
                    return Date.from(LocalDateTime.of(LocalDate.now(), time).atZone(SYSTEM_ZONE).toInstant());
                }
            } catch (Exception ignored) {}
        }

        return null;
    }

    private static Date parseSmart(String s) {
        // ISO 格式处理
        if (s.indexOf('T') > 0) {
            try {
                // 处理混合格式: 2023-10-25T14:30:45.123+00:00Z
                if (s.endsWith("Z") && s.contains("+")) {
                    return Date.from(OffsetDateTime.parse(s.substring(0, s.length() - 1)).toInstant());
                }
                if (s.indexOf('Z') > 0) return Date.from(Instant.parse(s));
                if (s.indexOf('+') > 0 || s.indexOf('-') > 0) return Date.from(OffsetDateTime.parse(s).toInstant());
            } catch (Exception ignored) {}
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

    private static Date parseTime(String s) {
        try {
            String normalized = s;
            int dotIdx = s.indexOf('.');
            if (dotIdx > 0) {
                int end = dotIdx + 1;
                while (end < s.length() && Character.isDigit(s.charAt(end))) end++;
                int fractionLen = end - dotIdx - 1;
                if (fractionLen > 3 && fractionLen <= 6) {
                    normalized = s.substring(0, dotIdx + 4) + s.substring(end);
                } else if (fractionLen > 6) {
                    throw new DateTimeException("Fraction seconds too long");
                }
            }

            if (normalized.contains("+") || normalized.contains("-")) {
                try {
                    return Date.from(OffsetDateTime.parse(LocalDate.now() + "T" + normalized).toInstant());
                } catch (Exception e) {
                    LocalTime time = LocalTime.parse(normalized, FMT_TIME_TZ);
                    return Date.from(LocalDateTime.of(LocalDate.now(), time).atZone(SYSTEM_ZONE).toInstant());
                }
            }

            DateTimeFormatter fmt = normalized.contains(".") ? FMT_TIME_MS : FMT_TIME;
            LocalTime time = LocalTime.parse(normalized, fmt);
            return Date.from(LocalDateTime.of(LocalDate.now(), time).atZone(SYSTEM_ZONE).toInstant());
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
                int year = fastParseInt(dateParts[0]);
                int month = fastParseInt(dateParts[1]);
                int day = fastParseInt(dateParts[2]);

                if (month < 1 || month > 12 || day < 1 || day > 31) {
                    throw new DateTimeException("Invalid date");
                }

                StringBuilder std = new StringBuilder(19)
                        .append(year).append('-')
                        .append(month < 10 ? "0" : "").append(month).append('-')
                        .append(day < 10 ? "0" : "").append(day);

                if (parts.length > 1) {
                    String[] timeParts = parts[1].split(":");
                    int hour = fastParseInt(timeParts[0]);
                    int minute = timeParts.length > 1 ? fastParseInt(timeParts[1]) : 0;
                    int second = timeParts.length > 2 ? fastParseInt(timeParts[2]) : 0;

                    if (hour < 0 || hour > 23 || minute < 0 || minute > 59 || second < 0 || second > 59) {
                        throw new DateTimeException("Invalid time");
                    }

                    std.append(' ').append(hour < 10 ? "0" : "").append(hour)
                            .append(':').append(minute < 10 ? "0" : "").append(minute)
                            .append(':').append(second < 10 ? "0" : "").append(second);
                }

                return parseFormatters(std.toString(), std.length());
            }
        } catch (Exception ignored) {}
        return null;
    }

    private static Date parseFormatters(String s, int len) {
        // 1. 标准ISO格式化器
        for (DateTimeFormatter fmt : COMMON_FORMATTERS) {
            Date result = parseWithFormatter(s, fmt);
            if (result != null) return result;
        }

        // 2. 长度分桶匹配（数组遍历更快）
        DateTimeFormatter[] bucket = LENGTH_BUCKETS.get(len);
        if (bucket != null) {
            for (DateTimeFormatter fmt : bucket) {
                Date result = parseWithFormatter(s, fmt);
                if (result != null) return result;
            }
        }

        // 3. 变长格式（数组遍历）
        for (DateTimeFormatter fmt : VARIABLE_FORMATTERS) {
            Date result = parseWithFormatter(s, fmt);
            if (result != null) return result;
        }

        return null;
    }

    private static Date parseWithFormatter(String s, DateTimeFormatter fmt) {
        try {
            TemporalAccessor accessor = fmt.parse(s);
            if (accessor.isSupported(ChronoField.OFFSET_SECONDS)) {
                return Date.from(OffsetDateTime.from(accessor).toInstant());
            }
            if (accessor.isSupported(ChronoField.HOUR_OF_DAY)) {
                return Date.from(LocalDateTime.from(accessor).atZone(SYSTEM_ZONE).toInstant());
            }
            if (accessor.isSupported(ChronoField.DAY_OF_MONTH)) {
                return Date.from(LocalDate.from(accessor).atStartOfDay(SYSTEM_ZONE).toInstant());
            }
            return Date.from(Instant.from(accessor));
        } catch (Exception e) {
            return null;
        }
    }

    private static Date parseTimestamp(String s) throws ParseException {
        try {
            long ts = 0;
            for (int i = 0; i < s.length(); i++) {
                ts = ts * 10 + (s.charAt(i) - '0');
            }
            return new Date(s.length() <= 10 ? ts * 1000 : ts);
        } catch (Exception e) {
            throw new ParseException("Invalid timestamp: " + s, 0);
        }
    }

    // ========== 工具方法 ==========
    private static int fastParseInt(String s) {
        return fastParseInt(s, 0, s.length());
    }

    private static boolean isAllDigits(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) < '0' || s.charAt(i) > '9') return false;
        }
        return true;
    }

    private static DateTimeFormatter getFormatter(String pattern) {
        return FORMATTER_CACHE.computeIfAbsent(pattern, DateUtil::createFormatter);
    }

    // ========== 格式化方法 ==========
    public static String format(Date date, String pattern) {
        return date == null || pattern == null ? null :
                getFormatter(pattern).format(date.toInstant());
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