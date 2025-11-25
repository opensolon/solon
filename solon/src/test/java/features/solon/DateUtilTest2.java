package features.solon;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.noear.solon.core.util.DateUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class DateUtilTest2 {

    @ParameterizedTest
    @MethodSource("provideAllDateFormats")
    @DisplayName("测试所有支持的日期格式")
    void testAllSupportedFormats(String dateString, String description) {
        Date date = DateUtil.parseTry(dateString);
        assertNotNull(date, "Failed to parse: " + description + " - " + dateString);
        assertTrue(date.getTime() > 0);
    }

    private static Stream<Arguments> provideAllDateFormats() {
        return Stream.of(
                // 时间戳格式
                Arguments.of("1698215445123", "13-digit timestamp"),
                Arguments.of("1698215445", "10-digit timestamp"),

                // ISO 8601 格式
                Arguments.of("2023-10-25T14:30:45Z", "ISO 8601 basic"),
                Arguments.of("2023-10-25T14:30:45.123Z", "ISO 8601 with milliseconds"),
                Arguments.of("2023-10-25T14:30:45+08:00", "ISO 8601 with offset"),
                Arguments.of("2023-10-25T14:30:45.123+08:00", "ISO 8601 with ms and offset"),
                Arguments.of("2023-10-25T14:30:45.123+00:00Z", "ISO 8601 mixed format"),

                // RFC 1123 格式
                Arguments.of("Wed, 25 Oct 2023 14:30:45 GMT", "RFC 1123"),

                // 标准日期时间格式
                Arguments.of("2023-10-25 14:30:45", "Standard datetime with dash"),
                Arguments.of("2023/10/25 14:30:45", "Standard datetime with slash"),
                Arguments.of("2023.10.25 14:30:45", "Standard datetime with dot"),
                Arguments.of("2023-10-25 14:30:45.123", "Datetime with milliseconds"),
                Arguments.of("2023-10-25 14:30:45,123", "Datetime with milliseconds comma"),

                // 紧凑格式
                Arguments.of("20231025143045", "Compact datetime"),
                Arguments.of("20231025", "Compact date"),
                Arguments.of("20231025143045123", "Compact datetime with ms"),

                // 日期格式
                Arguments.of("2023-10-25", "Date with dash"),
                Arguments.of("2023/10/25", "Date with slash"),
                Arguments.of("2023.10.25", "Date with dot"),

                // 时间格式
                Arguments.of("14:30:45", "Time only"),
                Arguments.of("14:30:45.123", "Time with milliseconds"),
                Arguments.of("14:30:45.123456", "Time with microseconds"),
                Arguments.of("14:30:45+08:00", "Time with offset"),

                // 中文格式
                Arguments.of("14时30分45秒", "Chinese time format"),

                // 各种分隔符和位数
                Arguments.of("2023-1-5 1:2:3", "Single digit datetime"),
                Arguments.of("2023/1/5 1:2:3", "Single digit datetime with slash"),
                Arguments.of("2023.1.5", "Single digit date with dot")
        );
    }

    @Test
    @DisplayName("测试边界情况")
    void testEdgeCases() {
        assertNull(DateUtil.parseTry(null));
        assertNull(DateUtil.parseTry(""));
        assertNull(DateUtil.parseTry("   "));
        assertNull(DateUtil.parseTry("invalid-date"));

        // 测试非常早和非常晚的日期
        assertNotNull(DateUtil.parseTry("1970-01-01"));
        assertNotNull(DateUtil.parseTry("2099-12-31"));
    }

    @Test
    @DisplayName("测试格式化功能")
    void testFormatFunctions() {
        // 1698215445123L 对应 UTC 时间 2023-10-25T06:30:45Z
        // 对应北京时间 2023-10-25 14:30:45 (UTC+8)
        Date date = new Date(1698215445123L);

        String iso = DateUtil.toISOString(date);
        // UTC 时间应该是 2023-10-25T06:30:45Z
        assertTrue(iso.contains("2023-10-25T06:30:45Z"), "ISO should be UTC time: " + iso);

        String gmt = DateUtil.toGmtString(date);
        // GMT 字符串应该包含 GMT（RFC 1123 格式）
        assertTrue(gmt.contains("GMT"), "GMT string should contain GMT: " + gmt);
        // RFC 1123 格式示例: "Wed, 25 Oct 2023 06:30:45 GMT"

        String custom = DateUtil.format(date, "yyyy-MM-dd HH:mm:ss");
        // 系统默认时区（北京时间）应该是 2023-10-25 14:30:45
        assertEquals("2023-10-25 14:30:45", custom, "Custom format should be local time");
    }

    @Test
    @DisplayName("测试往返一致性")
    void testRoundTrip() {
        String[] testFormats = {
                "2023-10-25T14:30:45.123Z",
                "2023-10-25 14:30:45",
                "20231025143045",
                "1698215445123"
        };

        for (String format : testFormats) {
            Date date = DateUtil.parseTry(format);
            assertNotNull(date);

            String isoString = DateUtil.toISOString(date);
            Date roundTripDate = DateUtil.parseTry(isoString);

            assertNotNull(roundTripDate);
            // 允许一定的时间差异（时区转换等）
            long diff = Math.abs(date.getTime() - roundTripDate.getTime());
            assertTrue(diff < 1000, "Round trip time difference should be less than 1 second");
        }
    }
}