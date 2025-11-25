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

import org.noear.solon.Utils;

import java.text.ParseException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 万能时间工具类 (Final Optimized Version with Bucketing)
 *
 * @author noear
 * @since 2.8
 * @since 3.7
 */
public class DateUtil {

    // 常用格式常量
    private static final String FMT_YMD_HMS = "yyyy-MM-dd HH:mm:ss";
    private static final String FMT_YMD = "yyyy-MM-dd";
    private static final String FMT_YMD_SLASH_HMS = "yyyy/MM/dd HH:mm:ss";
    private static final String FMT_YMD_SLASH = "yyyy/MM/dd";
    private static final String FMT_COMPACT_DT = "yyyyMMddHHmmss";
    private static final String FMT_COMPACT_D = "yyyyMMdd";

    // 兜底常用格式列表
    private static final List<DateTimeFormatter> COMMON_FORMATTERS = Arrays.asList(
            DateTimeFormatter.ISO_LOCAL_DATE_TIME,
            DateTimeFormatter.ISO_DATE_TIME,
            DateTimeFormatter.ISO_INSTANT,
            DateTimeFormatter.ISO_ZONED_DATE_TIME,
            DateTimeFormatter.ISO_OFFSET_DATE_TIME,
            DateTimeFormatter.ISO_LOCAL_DATE,
            DateTimeFormatter.ISO_DATE,
            DateTimeFormatter.ISO_LOCAL_TIME,
            DateTimeFormatter.ISO_TIME,
            DateTimeFormatter.RFC_1123_DATE_TIME
    );

    // 自定义模式列表
    private static final List<String> CUSTOM_PATTERNS = Arrays.asList(
            FMT_YMD_HMS,
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
            FMT_COMPACT_DT,
            "yyyyMMddHHmmssSSS",
            FMT_COMPACT_D,
            FMT_YMD,
            "yyyy/MM/dd",
            "yyyy.MM.dd",
            "HH:mm:ss",
            "HH:mm:ss.SSS",
            "HH:mm:ss.SSSSSS",
            "HH:mm:ssXXX",
            "HH:mm:ss.SSS+HH:mm",
            "HH时mm分ss秒",
            "yyyy-MM-dd HH:mm",
            "yyyy/MM/dd HH:mm",
            "yyyy.MM.dd HH:mm",
            "H:m:s",
            "yyyy-M-d H:m:s",
            "yyyy/M/d H:m:s",
            "yyyy.M.d H:m:s",
            "yyyy-M-d",
            "yyyy/M/d",
            "yyyy.M.d",
            "H:m"
    );

    // 缓存区
    private static final Map<String, DateTimeFormatter> FORMATTER_CACHE = new ConcurrentHashMap<>();

    // 核心优化：按长度分桶的 Formatter 列表
    // Key: 字符串长度, Value: 该长度对应的 Formatter 列表
    private static final Map<Integer, List<DateTimeFormatter>> FIXED_LEN_BUCKETS = new HashMap<>();

    // 变长格式列表 (如 yyyy-M-d，无法按长度精确分桶，单独存放)
    private static final List<DateTimeFormatter> VARIABLE_LEN_FORMATTERS = new ArrayList<>();

    private static final ZoneId SYSTEM_ZONE = ZoneId.systemDefault();

    static {
        // 预热 Fast-Path Formatter
        getOrCreateFormatter(FMT_YMD_HMS);
        getOrCreateFormatter(FMT_YMD);
        getOrCreateFormatter(FMT_COMPACT_DT);

        // 核心优化：初始化时进行分桶
        for (String pattern : CUSTOM_PATTERNS) {
            DateTimeFormatter formatter = getOrCreateFormatter(pattern);

            // 判断是否包含变长字符 (单个的 M, d, H, m, s)
            // 如果模式是 "yyyy-M-d"，它匹配的长度可能是 8, 9, 10，这种放入变长列表
            if (isVariableLength(pattern)) {
                VARIABLE_LEN_FORMATTERS.add(formatter);
            } else {
                // 定长格式，计算长度 (去除单引号转义)
                int len = pattern.replace("'", "").length();
                FIXED_LEN_BUCKETS.computeIfAbsent(len, k -> new ArrayList<>()).add(formatter);
            }
        }
    }

    // 判断模式是否产生变长字符串 (简单启发式判断)
    private static boolean isVariableLength(String pattern) {
        // 移除转义内容后检查
        String clean = pattern.replaceAll("'[^']*'", "");
        // 检查是否有单个的时间占位符，例如 "d" (1-31), "M" (1-12)
        // 也就是检查前面没有相同字符，后面也没有相同字符的情况
        // 简单做法：检查是否包含单字母模式。
        // 为了安全起见，只要包含 "y-M", "y/M", "M-d" 这种分隔符紧挨着单字符的，视为变长
        return clean.matches(".*\\b[Mdhms]\\b.*") ||
                clean.contains("-M-") || clean.contains("/M/") || clean.contains(".M.") ||
                clean.contains("-d") || clean.contains("/d") || clean.contains(".d");
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

        // 1. Fast-Path (Length + Char check) -> 命中率极高，0 Try-Catch
        try {
            if (len == 19) {
                if (trimmed.charAt(4) == '-' && trimmed.charAt(7) == '-') {
                    return parseLocal(trimmed, getOrCreateFormatter(FMT_YMD_HMS));
                }
                if (trimmed.charAt(4) == '/' && trimmed.charAt(7) == '/') {
                    return parseLocal(trimmed, getOrCreateFormatter(FMT_YMD_SLASH_HMS));
                }
            } else if (len == 10) {
                if (trimmed.charAt(4) == '-' && trimmed.charAt(7) == '-') {
                    return parseLocalDate(trimmed, getOrCreateFormatter(FMT_YMD));
                }
                if (trimmed.charAt(4) == '/' && trimmed.charAt(7) == '/') {
                    return parseLocalDate(trimmed, getOrCreateFormatter(FMT_YMD_SLASH));
                }
            } else if (len == 14 && isNumeric(trimmed)) {
                return parseLocal(trimmed, getOrCreateFormatter(FMT_COMPACT_DT));
            }
        } catch (Exception ignored) {
            // Fast-Path failed, continue
        }

        // 2. 特殊格式检查 (Manual Parsing -> 0 Try-Catch for checks)

        // 紧凑带时区: 20231025143045123+0800
        if (len == 22) {
            char sign = trimmed.charAt(17);
            if ((sign == '+' || sign == '-') && isNumeric(trimmed.substring(0, 17))) {
                try { return parseCompactDateTimeWithTimezoneManual(trimmed); } catch (Exception ignored) {}
            }
        }

        // 中文格式
        if (hasChineseTimeChar(trimmed)) {
            try { return parseChineseTime(trimmed); } catch (Exception ignored) {}
        }

        // 3. 兜底逻辑

        // 智能解析 (处理微秒、ISO变体等)
        Date result = trySmartParse(trimmed);
        if (result != null) return result;

        // ISO 标准格式尝试 (已优化为 Single Parse)
        if (couldBeIso(trimmed)) {
            result = tryCommonFormatters(trimmed);
            if (result != null) return result;
        }

        // 自定义格式全量尝试 -> 优化为分桶查找
        result = tryCustomFormatters(trimmed, len);
        if (result != null) return result;

        // 时间戳
        if (isNumeric(trimmed)) {
            return parseTimestamp(trimmed);
        }

        throw new ParseException("Unsupported date format: " + trimmed, 0);
    }

    // --- Parsing Logic ---

    /**
     * 优化后的自定义格式尝试：优先查桶
     */
    private static Date tryCustomFormatters(String dateStr, int len) {
        // 1. 优先尝试定长桶 (精确匹配长度)
        // 这消除了绝大多数因长度不一致导致的 parse 异常
        List<DateTimeFormatter> bucket = FIXED_LEN_BUCKETS.get(len);
        if (bucket != null) {
            for (DateTimeFormatter formatter : bucket) {
                Date d = parseWithFormatter(dateStr, formatter);
                if (d != null) return d;
            }
        }

        // 2. 尝试变长格式 (作为最后的手段)
        for (DateTimeFormatter formatter : VARIABLE_LEN_FORMATTERS) {
            Date d = parseWithFormatter(dateStr, formatter);
            if (d != null) return d;
        }

        return null;
    }

    private static Date trySmartParse(String dateStr) {
        int len = dateStr.length();
        boolean hasT = dateStr.indexOf('T') > 0;

        if (hasT) {
            if (dateStr.indexOf('Z') > 0) return parseISOWithZ(dateStr);
            if (dateStr.indexOf('+') > 0 || dateStr.indexOf('-') > 0) return parseISOWithOffset(dateStr);
        }

        if (len == 8 && isNumeric(dateStr)) return parseCompactDate(dateStr);

        if (dateStr.indexOf(':') > 0) {
            return parseTimeWithOptionalOffset(dateStr);
        }

        if (dateStr.indexOf('-') > 0 || dateStr.indexOf('/') > 0 || dateStr.indexOf('.') > 0) {
            return parseDateWithVariousSeparators(dateStr);
        }

        return null;
    }

    private static Date parseTimeWithOptionalOffset(String dateStr) {
        try {
            String tempStr = dateStr;
            int dotIndex = tempStr.indexOf('.');
            if (dotIndex > 0) {
                int endOfFraction = dotIndex + 1;
                while (endOfFraction < tempStr.length() && Character.isDigit(tempStr.charAt(endOfFraction))) {
                    endOfFraction++;
                }

                int fractionLen = endOfFraction - (dotIndex + 1);
                // 仅截断微秒 (4-6位)，保留纳秒让其失败(符合单测预期)
                if (fractionLen > 3 && fractionLen <= 6) {
                    tempStr = tempStr.substring(0, dotIndex + 4) + tempStr.substring(endOfFraction);
                }
            }

            if (tempStr.contains("+") || tempStr.contains("-")) {
                try {
                    // 尝试直接作为 OffsetDateTime 解析 (如果格式标准)
                    String fullDateTime = LocalDate.now().toString() + "T" + tempStr;
                    return Date.from(OffsetDateTime.parse(fullDateTime).toInstant());
                } catch (Exception e) {
                    // 尝试特定格式
                    DateTimeFormatter formatter = getOrCreateFormatter("HH:mm:ss.SSS+HH:mm");
                    LocalTime lt = LocalTime.parse(tempStr, formatter);
                    return Date.from(LocalDateTime.of(LocalDate.now(), lt).atZone(SYSTEM_ZONE).toInstant());
                }
            }

            DateTimeFormatter formatter = tempStr.contains(".") ?
                    getOrCreateFormatter("HH:mm:ss.SSS") : getOrCreateFormatter("HH:mm:ss");

            LocalTime localTime = LocalTime.parse(tempStr, formatter);
            return Date.from(LocalDateTime.of(LocalDate.now(), localTime).atZone(SYSTEM_ZONE).toInstant());
        } catch (Exception e) {
            return null;
        }
    }

    // Manual parser remains same...
    private static Date parseCompactDateTimeWithTimezoneManual(String dateStr) throws ParseException {
        try {
            int year = Integer.parseInt(dateStr.substring(0, 4));
            int month = Integer.parseInt(dateStr.substring(4, 6));
            int day = Integer.parseInt(dateStr.substring(6, 8));
            int hour = Integer.parseInt(dateStr.substring(8, 10));
            int minute = Integer.parseInt(dateStr.substring(10, 12));
            int second = Integer.parseInt(dateStr.substring(12, 14));
            int millis = Integer.parseInt(dateStr.substring(14, 17));
            String zonePart = dateStr.substring(17);

            LocalDateTime ldt = LocalDateTime.of(year, month, day, hour, minute, second, millis * 1_000_000);

            String normalizedZone = zonePart;
            if (zonePart.length() == 5 && zonePart.indexOf(':') < 0) {
                normalizedZone = zonePart.substring(0, 3) + ":" + zonePart.substring(3);
            }
            ZoneOffset zoneOffset = ZoneOffset.of(normalizedZone);
            return Date.from(OffsetDateTime.of(ldt, zoneOffset).toInstant());
        } catch (Exception e) {
            throw new ParseException("Failed to parse compact TZ manual: " + dateStr, 0);
        }
    }

    private static boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) return false;
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) return false;
        }
        return true;
    }

    private static boolean hasChineseTimeChar(String str) {
        return str.indexOf('时') > 0 || str.indexOf('分') > 0;
    }

    private static boolean couldBeIso(String str) {
        return str.indexOf('-') > 0 || str.indexOf(':') > 0 || str.indexOf('T') > 0;
    }

    private static Date parseTimestamp(String timestampStr) throws ParseException {
        try {
            long timestamp = Long.parseLong(timestampStr);
            if (timestampStr.length() <= 10) timestamp *= 1000;
            return new Date(timestamp);
        } catch (NumberFormatException e) {
            throw new ParseException("Invalid timestamp: " + timestampStr, 0);
        }
    }

    private static Date parseISOWithZ(String dateStr) {
        try {
            return Date.from(Instant.parse(dateStr));
        } catch (DateTimeParseException e) {
            if (dateStr.endsWith("Z") && dateStr.contains("+")) {
                return parseWithFormatter(dateStr.substring(0, dateStr.length() - 1), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            }
        }
        return null;
    }

    private static Date parseISOWithOffset(String dateStr) {
        try {
            TemporalAccessor accessor = DateTimeFormatter.ISO_DATE_TIME.parse(dateStr);
            if (accessor.isSupported(ChronoField.OFFSET_SECONDS)) {
                return Date.from(Instant.from(accessor));
            }
        } catch (Exception ignored) { }
        return null;
    }

    private static Date parseCompactDate(String dateStr) {
        return parseLocalDate(dateStr, getOrCreateFormatter(FMT_COMPACT_D));
    }

    private static Date parseChineseTime(String timeStr) throws ParseException {
        try {
            int hIdx = timeStr.indexOf("时");
            int mIdx = timeStr.indexOf("分");
            int sIdx = timeStr.indexOf("秒");
            if (hIdx > 0 && mIdx > hIdx && sIdx > mIdx) {
                int hour = Integer.parseInt(timeStr.substring(0, hIdx));
                int minute = Integer.parseInt(timeStr.substring(hIdx+1, mIdx));
                int second = Integer.parseInt(timeStr.substring(mIdx+1, sIdx));
                return Date.from(LocalDateTime.of(LocalDate.now(), LocalTime.of(hour, minute, second)).atZone(SYSTEM_ZONE).toInstant());
            }
            String[] parts = timeStr.split("[时分秒]");
            if (parts.length >= 3) {
                return Date.from(LocalDateTime.of(LocalDate.now(),
                                LocalTime.of(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2])))
                        .atZone(SYSTEM_ZONE).toInstant());
            }
        } catch (Exception ignored) {}
        throw new ParseException("Invalid Chinese time: " + timeStr, 0);
    }

    private static Date parseDateWithVariousSeparators(String dateStr) {
        String normalized = dateStr.replace('/', '-').replace('.', '-');
        if (normalized.length() == 19 && normalized.charAt(13) == ':') {
            try { return parseLocal(normalized, getOrCreateFormatter(FMT_YMD_HMS)); } catch(Exception e){}
        }
        try {
            String[] parts = normalized.split(" ");
            if (parts.length >= 1) {
                String[] dItems = parts[0].split("-");
                if (dItems.length == 3) {
                    String stdDate = dItems[0] + "-" + String.format("%02d", Integer.parseInt(dItems[1])) + "-" + String.format("%02d", Integer.parseInt(dItems[2]));
                    if (parts.length > 1) {
                        String tPart = parts[1];
                        if (tPart.indexOf(':') == tPart.lastIndexOf(':')) tPart += ":00";
                        String[] tItems = tPart.split(":");
                        if(tItems.length>=2) {
                            stdDate += " " + String.format("%02d", Integer.parseInt(tItems[0])) + ":" +
                                    String.format("%02d", Integer.parseInt(tItems[1])) + ":" +
                                    (tItems.length > 2 ? String.format("%02d", Integer.parseInt(tItems[2])) : "00");
                        }
                    }
                    // 递归调用优化后的分桶查找
                    return tryCustomFormatters(stdDate, stdDate.length());
                }
            }
        } catch (Exception ignored) {}
        return null;
    }

    private static Date tryCommonFormatters(String dateStr) {
        for (DateTimeFormatter formatter : COMMON_FORMATTERS) {
            Date d = parseWithFormatter(dateStr, formatter);
            if (d != null) return d;
        }
        return null;
    }

    /**
     * 核心通用解析逻辑：解析一次，按字段转换，避免异常
     */
    private static Date parseWithFormatter(String dateStr, DateTimeFormatter formatter) {
        try {
            TemporalAccessor accessor = formatter.parse(dateStr);
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
        } catch (DateTimeParseException ignored) {
            // 格式不匹配，正常忽略
        } catch (Exception ignored) {
            // 兜底
        }
        return null;
    }

    private static Date parseLocal(String str, DateTimeFormatter formatter) {
        return Date.from(LocalDateTime.parse(str, formatter).atZone(SYSTEM_ZONE).toInstant());
    }

    private static Date parseLocalDate(String str, DateTimeFormatter formatter) {
        return Date.from(LocalDate.parse(str, formatter).atStartOfDay(SYSTEM_ZONE).toInstant());
    }

    private static DateTimeFormatter getOrCreateFormatter(String pattern) {
        return FORMATTER_CACHE.computeIfAbsent(pattern,
                p -> DateTimeFormatter.ofPattern(p).withZone(SYSTEM_ZONE));
    }

    public static String format(Date date, String pattern) {
        if (date == null || pattern == null) return null;
        return getOrCreateFormatter(pattern).format(date.toInstant());
    }

    public static String format(Date date, String pattern, TimeZone timeZone) {
        if (date == null || pattern == null || timeZone == null) return null;
        return DateTimeFormatter.ofPattern(pattern).withZone(timeZone.toZoneId()).format(date.toInstant());
    }

    public static String toISOString(Date date) {
        if (date == null) return null;
        return DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").withZone(ZoneId.of("UTC")).format(date.toInstant());
    }

    public static String toGmtString(Date date) {
        if (date == null) return null;
        return DateTimeFormatter.RFC_1123_DATE_TIME.withZone(ZoneId.of("GMT")).withLocale(Locale.US).format(date.toInstant());
    }
}