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
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 万能时间工具类 (Extreme Performance Optimized Version)
 *
 * @author noear
 * @since 2.8
 * @since 3.7
 */
public class DateUtil {
    // 常用格式常量 (只保留名称，不再预缓存对象)
    private static final String FMT_YMD_HMS = "yyyy-MM-dd HH:mm:ss";
    private static final String FMT_YMD = "yyyy-MM-dd";
    private static final String FMT_COMPACT_DT = "yyyyMMddHHmmss";
    private static final String FMT_COMPACT_D = "yyyyMMdd";

    // 兜底常用格式列表 (保持不变)
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

    // 自定义模式列表 (略作精简和合并，将 FMT_xx_A/B/C 替换为对应的字面量)
    private static final List<String> CUSTOM_PATTERNS = Arrays.asList(
            // 19/23 字符格式
            FMT_YMD_HMS, "yyyy/MM/dd HH:mm:ss", "yyyy.MM.dd HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy-MM-dd HH:mm:ss.SSS", "yyyy-MM-dd HH:mm:ss,SSS", "yyyy-MM-dd'T'HH:mm:ss.SSS",
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", "yyyy-MM-dd'T'HH:mm:ssXXX",
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            // 紧凑格式
            FMT_COMPACT_DT, "yyyyMMddHHmmssSSS", FMT_COMPACT_D,
            // 10 字符格式
            FMT_YMD, "yyyy/MM/dd", "yyyy.MM.dd",
            // 时间格式
            "HH:mm:ss", "HH:mm:ss.SSS", "HH:mm:ss.SSSSSS", "HH:mm:ssXXX", "HH:mm:ss.SSS+HH:mm",
            // 变长格式
            "yyyy-MM-dd HH:mm", "yyyy/MM/dd HH:mm", "yyyy.MM.dd HH:mm",
            "H:m:s", "yyyy-M-d H:m:s", "yyyy/M/d H:m:s", "yyyy.M.d H:m:s",
            "yyyy-M-d", "yyyy/M/d", "yyyy.M.d"
    );

    // 缓存区
    private static final Map<String, DateTimeFormatter> FORMATTER_CACHE = new ConcurrentHashMap<>();

    // 核心优化：按长度分桶的 Formatter 列表
    private static final Map<Integer, List<DateTimeFormatter>> FIXED_LEN_BUCKETS = new HashMap<>();
    private static final List<DateTimeFormatter> VARIABLE_LEN_FORMATTERS = new ArrayList<>();

    private static final ZoneId SYSTEM_ZONE = ZoneId.systemDefault();

    static {
        // 预加载所有格式化器
        for (String pattern : CUSTOM_PATTERNS) {
            DateTimeFormatter formatter = getOrCreateFormatter(pattern);

            if (isVariableLength(pattern)) {
                VARIABLE_LEN_FORMATTERS.add(formatter);
            } else {
                int len = pattern.replace("'", "").length();
                FIXED_LEN_BUCKETS.computeIfAbsent(len, k -> new ArrayList<>()).add(formatter);
            }
        }
    }

    // 判断模式是否产生变长字符串 (保持不变)
    private static boolean isVariableLength(String pattern) {
        String clean = pattern.replaceAll("'[^']*'", "");
        return clean.matches(".*\\b[Mdhms]\\b.*") ||
                clean.contains("-M-") || clean.contains("/M/") || clean.contains(".M.") ||
                clean.contains("-d") || clean.contains("/d") || clean.contains(".d");
    }

    // ========== 主解析入口 ==========

    public static Date parseTry(String dateStr) {
        try {
            return parse(dateStr);
        } catch (Exception e) {
            return null;
        }
    }

    public static Date parse(String dateStr) throws ParseException {
        if (dateStr == null) return null;

        // 优化点：惰性 Trim
        String trimmed = dateStr;
        int len = dateStr.length();
        if (len > 0 && (dateStr.charAt(0) <= ' ' || dateStr.charAt(len - 1) <= ' ')) {
            trimmed = dateStr.trim();
            len = trimmed.length();
        }

        if (trimmed.isEmpty()) return null;

        // 1. 超快速路径：纯算术解析 (最高频格式)
        Date result = parseUltraFast(trimmed, len);
        if (result != null) return result;

        // 2. 特殊格式检查 (保持不变)
        result = parseSpecialFormats(trimmed, len);
        if (result != null) return result;

        // 3. 智能解析 (可简化，但保持兼容性)
        result = trySmartParse(trimmed);
        if (result != null) return result;

        // 4. ISO 标准格式尝试 (保持不变)
        if (couldBeIso(trimmed)) {
            result = tryCommonFormatters(trimmed);
            if (result != null) return result;
        }

        // 5. 自定义格式分桶查找 (保持不变)
        result = tryCustomFormatters(trimmed, len);
        if (result != null) return result;

        // 6. 时间戳 (保持不变)
        if (isNumeric(trimmed)) {
            return parseTimestamp(trimmed);
        }

        throw new ParseException("Unsupported date format: " + trimmed, 0);
    }

    /**
     * 超快速路径：纯算术解析 (Zero-Allocation for Hot Path)
     */
    private static Date parseUltraFast(String str, int len) {
        try {
            switch (len) {
                case 19:
                    // yyyy-MM-dd HH:mm:ss
                    if (str.charAt(4) == '-' && str.charAt(7) == '-' && str.charAt(10) == ' ') {
                        return parseFastYmdHms(str);
                    }
                    // yyyy-MM-dd'T'HH:mm:ss
                    if (str.charAt(4) == '-' && str.charAt(7) == '-' && str.charAt(10) == 'T') {
                        // ISO T 格式，虽然可以纯算术，但沿用 Formatter 兼容性更好
                        return parseWithFormatter(str, getOrCreateFormatter("yyyy-MM-dd'T'HH:mm:ss"));
                    }
                    break;
                case 10:
                    // yyyy-MM-dd
                    if (str.charAt(4) == '-' && str.charAt(7) == '-') {
                        return parseFastYmd(str);
                    }
                    break;
                case 14:
                    // yyyyMMddHHmmss
                    if (isNumeric(str)) {
                        return parseWithFormatter(str, getOrCreateFormatter(FMT_COMPACT_DT));
                    }
                    break;
                case 8:
                    // yyyyMMdd
                    if (isNumeric(str)) {
                        return parseWithFormatter(str, getOrCreateFormatter(FMT_COMPACT_D));
                    }
                    break;
            }
        } catch (Exception ignored) {
            // 纯算术解析失败 (非数字字符或越界)，安全回退到后续的通用解析
        }
        return null;
    }

    /**
     * [极致性能] 手动解析 yyyy-MM-dd HH:mm:ss，零 Substring 零 Integer.parseInt
     */
    private static Date parseFastYmdHms(String s) {
        // 直接操作 char 进行乘法和减法，由 JVM JIT 优化成最快指令
        int year  = (s.charAt(0) - '0') * 1000 + (s.charAt(1) - '0') * 100 + (s.charAt(2) - '0') * 10 + (s.charAt(3) - '0');
        int month = (s.charAt(5) - '0') * 10 + (s.charAt(6) - '0');
        int day   = (s.charAt(8) - '0') * 10 + (s.charAt(9) - '0');
        int hour  = (s.charAt(11) - '0') * 10 + (s.charAt(12) - '0');
        int min   = (s.charAt(14) - '0') * 10 + (s.charAt(15) - '0');
        int sec   = (s.charAt(17) - '0') * 10 + (s.charAt(18) - '0');

        return Date.from(LocalDateTime.of(year, month, day, hour, min, sec)
                .atZone(SYSTEM_ZONE).toInstant());
    }

    /**
     * [极致性能] 手动解析 yyyy-MM-dd，零 Substring 零 Integer.parseInt
     */
    private static Date parseFastYmd(String s) {
        int year  = (s.charAt(0) - '0') * 1000 + (s.charAt(1) - '0') * 100 + (s.charAt(2) - '0') * 10 + (s.charAt(3) - '0');
        int month = (s.charAt(5) - '0') * 10 + (s.charAt(6) - '0');
        int day   = (s.charAt(8) - '0') * 10 + (s.charAt(9) - '0');

        return Date.from(LocalDate.of(year, month, day)
                .atStartOfDay(SYSTEM_ZONE).toInstant());
    }

    // 以下方法保持不变，因为它们已是现有逻辑中最优化或最简洁的兼容方案

    private static Date parseSpecialFormats(String str, int len) {
        // 紧凑带时区: 20231025143045123+0800
        if (len == 22) {
            char sign = str.charAt(17);
            if ((sign == '+' || sign == '-') && isNumeric(str.substring(0, 17))) {
                try {
                    return parseCompactDateTimeWithTimezoneManual(str);
                } catch (Exception ignored) {
                }
            }
        }

        // 中文格式
        if (hasChineseTimeChar(str)) {
            try {
                return parseChineseTime(str);
            } catch (Exception ignored) {
            }
        }

        return null;
    }

    private static Date trySmartParse(String dateStr) {
        boolean hasT = dateStr.indexOf('T') > 0;

        if (hasT) {
            if (dateStr.indexOf('Z') > 0) return parseISOWithZ(dateStr);
            if (dateStr.indexOf('+') > 0 || dateStr.indexOf('-') > 0) return parseISOWithOffset(dateStr);
        }

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
                if (fractionLen > 3 && fractionLen <= 6) {
                    tempStr = tempStr.substring(0, dotIndex + 4) + tempStr.substring(endOfFraction);
                }
            }

            if (tempStr.contains("+") || tempStr.contains("-")) {
                try {
                    String fullDateTime = LocalDate.now().toString() + "T" + tempStr;
                    return Date.from(OffsetDateTime.parse(fullDateTime).toInstant());
                } catch (Exception e) {
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

    private static Date parseChineseTime(String timeStr) throws ParseException {
        try {
            int hIdx = timeStr.indexOf("时");
            int mIdx = timeStr.indexOf("分");
            int sIdx = timeStr.indexOf("秒");
            if (hIdx > 0 && mIdx > hIdx && sIdx > mIdx) {
                int hour = Integer.parseInt(timeStr.substring(0, hIdx));
                int minute = Integer.parseInt(timeStr.substring(hIdx + 1, mIdx));
                int second = Integer.parseInt(timeStr.substring(mIdx + 1, sIdx));
                return Date.from(LocalDateTime.of(LocalDate.now(), LocalTime.of(hour, minute, second)).atZone(SYSTEM_ZONE).toInstant());
            }
            String[] parts = timeStr.split("[时分秒]");
            if (parts.length >= 3) {
                return Date.from(LocalDateTime.of(LocalDate.now(),
                                LocalTime.of(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2])))
                        .atZone(SYSTEM_ZONE).toInstant());
            }
        } catch (Exception ignored) {
        }
        throw new ParseException("Invalid Chinese time: " + timeStr, 0);
    }

    private static Date parseDateWithVariousSeparators(String dateStr) {
        String normalized = dateStr.replace('/', '-').replace('.', '-');
        if (normalized.length() == 19 && normalized.charAt(13) == ':') {
            try {
                return parseWithFormatter(normalized, getOrCreateFormatter(FMT_YMD_HMS));
            } catch (Exception e) {
            }
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
                        if (tItems.length >= 2) {
                            stdDate += " " + String.format("%02d", Integer.parseInt(tItems[0])) + ":" +
                                    String.format("%02d", Integer.parseInt(tItems[1])) + ":" +
                                    (tItems.length > 2 ? String.format("%02d", Integer.parseInt(tItems[2])) : "00");
                        }
                    }
                    return tryCustomFormatters(stdDate, stdDate.length());
                }
            }
        } catch (Exception ignored) {
        }
        return null;
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
        } catch (Exception ignored) {
        }
        return null;
    }

    /**
     * 优化后的自定义格式尝试：优先查桶
     */
    private static Date tryCustomFormatters(String dateStr, int len) {
        // 1. 优先尝试定长桶
        List<DateTimeFormatter> bucket = FIXED_LEN_BUCKETS.get(len);
        if (bucket != null) {
            for (DateTimeFormatter formatter : bucket) {
                Date d = parseWithFormatter(dateStr, formatter);
                if (d != null) return d;
            }
        }

        // 2. 尝试变长格式
        for (DateTimeFormatter formatter : VARIABLE_LEN_FORMATTERS) {
            Date d = parseWithFormatter(dateStr, formatter);
            if (d != null) return d;
        }

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
     * 核心通用解析逻辑 (保持不变)
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
        } catch (Exception ignored) {
        }
        return null;
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

    // ========== 工具方法 ==========
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

    private static DateTimeFormatter getOrCreateFormatter(String pattern) {
        return FORMATTER_CACHE.computeIfAbsent(pattern,
                p -> DateTimeFormatter.ofPattern(p).withZone(SYSTEM_ZONE));
    }

    // ========== 格式化方法 ==========
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