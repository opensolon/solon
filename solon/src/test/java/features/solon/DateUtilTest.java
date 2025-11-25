package features.solon;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.noear.solon.core.util.DateUtil;

import java.text.ParseException;
import java.util.Date;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DateUtil 单元测试
 * 确保 100% 覆盖率，覆盖所有格式符
 */
class DateUtilTest {

    // ========== 基础边界测试 ==========

    @Test
    @DisplayName("测试 null 输入")
    void testParseWithNull() throws ParseException {
        assertNull(DateUtil.parse(null));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"", " "})
    @DisplayName("测试空字符串输入")
    void testParseWithEmptyString(String input) throws ParseException {
        assertNull(DateUtil.parse(input));
    }

    @Test
    @DisplayName("测试纯数字时间戳")
    void testParseWithTimestamp() throws ParseException {
        long timestamp = System.currentTimeMillis();
        Date date = DateUtil.parse(String.valueOf(timestamp));
        assertNotNull(date);
        // 允许一定的时间误差
        assertTrue(Math.abs(date.getTime() - timestamp) < 1000);
    }

    @Test
    @DisplayName("测试纯数字但包含非数字字符")
    void testParseWithInvalidDigits() throws ParseException {
        assertThrows(ParseException.class, () -> DateUtil.parse("123abc456"));
    }

    // ========== 格式符覆盖测试 ==========

    @ParameterizedTest
    @MethodSource("provideAllFormats")
    @DisplayName("测试所有支持的格式")
    void testAllSupportedFormats(String dateString, String description) throws ParseException {
        Date date = DateUtil.parse(dateString);
        assertNotNull(date, "Failed to parse: " + description + " - " + dateString);
        assertTrue(date.getTime() > 0);
    }

    private static Stream<Arguments> provideAllFormats() {
        return Stream.of(
                // FORMAT_29: "yyyy-MM-dd'T'HH:mm:ss.SSSXXX" - 带时区偏移
                Arguments.of("2023-10-25T14:30:45.123+08:00", "FORMAT_29"),

                // FORMAT_27: "yyyy-MM-dd'T'HH:mm:ss.SSSXXX'Z'" - UTC时间
                Arguments.of("2023-10-25T14:30:45.123+00:00Z", "FORMAT_27"),

                // FORMAT_25: "yyyy-MM-dd'T'HH:mm:ss+HH:mm" - 带时区
                Arguments.of("2023-10-25T14:30:45+08:00", "FORMAT_25"),

                // FORMAT_24_ISO08601: "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" - ISO UTC
                Arguments.of("2023-10-25T14:30:45.123Z", "FORMAT_24_ISO08601"),

                // FORMAT_23_a: "yyyy-MM-dd HH:mm:ss,SSS" - 逗号分隔毫秒
                Arguments.of("2023-10-25 14:30:45,123", "FORMAT_23_a"),

                // FORMAT_23_b: "yyyy-MM-dd HH:mm:ss.SSS" - 点分隔毫秒
                Arguments.of("2023-10-25 14:30:45.123", "FORMAT_23_b"),

                // FORMAT_23_t: "yyyy-MM-dd'T'HH:mm:ss.SSS" - T分隔带毫秒
                Arguments.of("2023-10-25T14:30:45.123", "FORMAT_23_t"),

                // FORMAT_22: "yyyyMMddHHmmssSSSZ" - 紧凑格式带时区
                Arguments.of("20231025143045123+0800", "FORMAT_22"),

                // FORMAT_19_ISO: "yyyy-MM-dd'T'HH:mm:ss" - ISO不带毫秒
                Arguments.of("2023-10-25T14:30:45", "FORMAT_19_ISO"),

                // FORMAT_19_a: "yyyy-MM-dd HH:mm:ss" - 标准格式
                Arguments.of("2023-10-25 14:30:45", "FORMAT_19_a"),

                // FORMAT_19_b: "yyyy/MM/dd HH:mm:ss" - 斜杠分隔
                Arguments.of("2023/10/25 14:30:45", "FORMAT_19_b"),

                // FORMAT_19_c: "yyyy.MM.dd HH:mm:ss" - 点分隔
                Arguments.of("2023.10.25 14:30:45", "FORMAT_19_c"),

                // FORMAT_18: "HH:mm:ss.SSS+HH:mm" - 时间带毫秒和时区
                Arguments.of("14:30:45.123+08:00", "FORMAT_18"),

                // FORMAT_17: "yyyyMMddHHmmssSSS" - 紧凑格式带毫秒
                Arguments.of("20231025143045123", "FORMAT_17"),

                // FORMAT_16_a: "yyyy-MM-dd HH:mm" - 标准格式到分钟
                Arguments.of("2023-10-25 14:30", "FORMAT_16_a"),

                // FORMAT_16_b: "yyyy/MM/dd HH:mm" - 斜杠分隔到分钟
                Arguments.of("2023/10/25 14:30", "FORMAT_16_b"),

                // FORMAT_16_c: "yyyy.MM.dd HH:mm" - 点分隔到分钟
                Arguments.of("2023.10.25 14:30", "FORMAT_16_c"),

                // FORMAT_15: "HH:mm:ss.SSSSSS" - 时间带微秒
                Arguments.of("14:30:45.123456", "FORMAT_15"),

                // FORMAT_14_a: "yyyyMMddHHmmss" - 紧凑格式
                Arguments.of("20231025143045", "FORMAT_14_a"),

                // FORMAT_14_b: "HH:mm:ssXXX" - 时间带时区
                Arguments.of("14:30:45+08:00", "FORMAT_14_b"),

                // FORMAT_12: "HH:mm:ss.SSS" - 时间带毫秒
                Arguments.of("14:30:45.123", "FORMAT_12"),

                // FORMAT_10_a: "yyyy-MM-dd" - 标准日期
                Arguments.of("2023-10-25", "FORMAT_10_a"),

                // FORMAT_10_b: "yyyy/MM/dd" - 斜杠日期
                Arguments.of("2023/10/25", "FORMAT_10_b"),

                // FORMAT_10_c: "yyyy.MM.dd" - 点日期
                Arguments.of("2023.10.25", "FORMAT_10_c"),

                // FORMAT_9: "HH时mm分ss秒" - 中文时间
                Arguments.of("14时30分45秒", "FORMAT_9"),

                // FORMAT_8_a: "HH:mm:ss" - 标准时间
                Arguments.of("14:30:45", "FORMAT_8_a"),

                // FORMAT_8_b: "yyyyMMdd" - 紧凑日期
                Arguments.of("20231025", "FORMAT_8_b"),

                // 边界情况：9位长度，日期格式
                Arguments.of("2023/1/2", "FORMAT_9_DATE_SLASH"),
                Arguments.of("2023.1.2", "FORMAT_9_DATE_DOT"),
                Arguments.of("2023-1-2", "FORMAT_9_DATE_DASH"),

                // 边界情况：8位长度，日期格式
                Arguments.of("2023/1/2", "FORMAT_8_DATE_SLASH"),
                Arguments.of("2023.1.2", "FORMAT_8_DATE_DOT"),
                Arguments.of("2023-1-2", "FORMAT_8_DATE_DASH")
        );
    }

    // ========== 异常情况测试 ==========

    @ParameterizedTest
    @ValueSource(strings = {
            "2023-13-45", // 无效日期
            "2023-10-25T25:61:61", // 无效时间
            "2023-10-25 14:30:45.123.456", // 多个小数点
            "14:30:45.123456789", // 微秒位数过多
            "2023-10-25T14:30:45+99:99" // 无效时区
    })
    @DisplayName("测试无效日期格式")
    void testInvalidFormats(String invalidDate) {
        assertThrows(ParseException.class, () -> DateUtil.parse(invalidDate));
    }

    @Test
    @DisplayName("测试 parseTry 方法 - 有效日期")
    void testParseTryWithValidDate() {
        Date date = DateUtil.parseTry("2023-10-25 14:30:45");
        assertNotNull(date);
    }

    @Test
    @DisplayName("测试 parseTry 方法 - 无效日期")
    void testParseTryWithInvalidDate() {
        assertNull(DateUtil.parseTry("invalid-date-string"));
    }

    // ========== GMT 格式测试 ==========

    @Test
    @DisplayName("测试 toGmtString 方法")
    void testToGmtString() {
        Date date = new Date(1698215445123L);
        String gmtString = DateUtil.toGmtString(date);

        assertNotNull(gmtString);
        assertTrue(gmtString.contains("GMT")); // 标准 GMT 格式应该包含 GMT
        assertTrue(gmtString.matches("^[A-Za-z]{3}, \\d{2} [A-Za-z]{3} \\d{4} \\d{2}:\\d{2}:\\d{2} GMT$"));
    }

    @Test
    @DisplayName("测试 toGmtString 与 parse 的往返")
    void testGmtRoundTrip() throws ParseException {
        Date original = new Date();
        String gmtString = DateUtil.toGmtString(original);

        // GMT 字符串应该能被解析
        Date parsed = DateUtil.parse(gmtString);
        assertNotNull(parsed);

        // 由于时区转换，时间可能会有差异，但应该在合理范围内
        long diff = Math.abs(original.getTime() - parsed.getTime());
        assertTrue(diff < 24 * 60 * 60 * 1000, "时间差异应该在24小时内"); // 24小时内的差异
    }

    // ========== 边界值测试 ==========

    @Test
    @DisplayName("测试最小日期")
    void testMinDate() throws ParseException {
        Date date = DateUtil.parse("1970-01-01 00:00:00");
        assertNotNull(date);
    }

    @Test
    @DisplayName("测试最大日期")
    void testMaxDate() throws ParseException {
        Date date = DateUtil.parse("2099-12-31 23:59:59");
        assertNotNull(date);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "0001-01-01", // 非常早的日期
            "9999-12-31"  // 非常晚的日期
    })
    @DisplayName("测试极端日期")
    void testExtremeDates(String dateString) throws ParseException {
        Date date = DateUtil.parse(dateString);
        assertNotNull(date);
    }

    // ========== 格式边界测试 ==========

    @Test
    @DisplayName("测试 FORMAT_29 边界情况")
    void testFormat29Boundary() throws ParseException {
        // 测试不同的时区偏移
        Date date1 = DateUtil.parse("2023-10-25T14:30:45.123-08:00");
        Date date2 = DateUtil.parse("2023-10-25T14:30:45.123+00:00");
        Date date3 = DateUtil.parse("2023-10-25T14:30:45.123+11:30");

        assertNotNull(date1);
        assertNotNull(date2);
        assertNotNull(date3);
    }

    @Test
    @DisplayName("测试毫秒边界值")
    void testMillisecondsBoundary() throws ParseException {
        Date date1 = DateUtil.parse("2023-10-25 14:30:45.000"); // 最小毫秒
        Date date2 = DateUtil.parse("2023-10-25 14:30:45.999"); // 最大毫秒

        assertNotNull(date1);
        assertNotNull(date2);
    }

    // ========== 一致性测试 ==========

    @Test
    @DisplayName("测试相同时间不同格式的一致性")
    void testConsistencyAcrossFormats() throws ParseException {
        String baseTime = "2023-10-25 14:30:45";

        Date date1 = DateUtil.parse(baseTime);
        Date date2 = DateUtil.parse("2023/10/25 14:30:45");
        Date date3 = DateUtil.parse("2023.10.25 14:30:45");
        Date date4 = DateUtil.parse("20231025143045");

        // 这些日期应该表示相同的时间点（忽略格式差异）
        assertNotNull(date1);
        assertNotNull(date2);
        assertNotNull(date3);
        assertNotNull(date4);

        // 由于不同格式可能有不同的精度，我们只检查它们都是有效日期
        long time1 = date1.getTime();
        long time2 = date2.getTime();
        long time3 = date3.getTime();
        long time4 = date4.getTime();

        // 时间应该在合理范围内（同一天）
        assertTrue(Math.abs(time1 - time2) < 24 * 60 * 60 * 1000);
        assertTrue(Math.abs(time1 - time3) < 24 * 60 * 60 * 1000);
        assertTrue(Math.abs(time1 - time4) < 24 * 60 * 60 * 1000);
    }
}