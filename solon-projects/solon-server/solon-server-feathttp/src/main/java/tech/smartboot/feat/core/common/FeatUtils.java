/*
 *  Copyright (C) [2024] smartboot [zhengjunweimail@163.com]
 *
 *  企业用户未经smartboot组织特别许可，需遵循Apache-2.0开源协议合理合法使用本项目。
 *
 *   Enterprise users are required to use this project reasonably
 *   and legally in accordance with the Apache-2.0 open source agreement
 *  without special permission from the smartboot organization.
 */

package tech.smartboot.feat.core.common;

import io.github.smartboot.socket.timer.HashedWheelTimer;
import tech.smartboot.feat.Feat;
import tech.smartboot.feat.core.common.exception.FeatException;
import tech.smartboot.feat.core.common.logging.Logger;
import tech.smartboot.feat.core.common.logging.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author 三刀 zhengjunweimail@163.com
 * @version v1.0.0
 */
public class FeatUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(FeatUtils.class);
    public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
    private static final String COOKIE_PATTERN = "EEE, dd-MMM-yyyy HH:mm:ss z";
    public static final String RFC1123_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";
    private static final TimeZone GMT = TimeZone.getTimeZone("GMT");
    //必须采用该规范的字段：Date、Expires
    private static final ThreadLocal<SimpleDateFormat> RFC1123_DATE_FORMAT = ThreadLocal.withInitial(() -> {
        SimpleDateFormat sdf = new SimpleDateFormat(RFC1123_FORMAT, Locale.US);
        // rfc2616 3.3.1
        // All HTTP date/time stamps MUST be represented in Greenwich Mean Time (GMT), without exception.
        sdf.setTimeZone(GMT);
        return sdf;
    });

    private static final ThreadLocal<SimpleDateFormat> COOKIE_FORMAT = ThreadLocal.withInitial(() -> {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(COOKIE_PATTERN, Locale.US);
        simpleDateFormat.setTimeZone(GMT);
        return simpleDateFormat;
    });
    /**
     * 当前时间
     */
    private static final Date currentTime = new Date();

    static {
        HashedWheelTimer.DEFAULT_TIMER.scheduleWithFixedDelay(() -> {
            currentTime.setTime(System.currentTimeMillis());
        }, 1, TimeUnit.SECONDS);
    }


    /**
     * Post 最大长度
     */
    public static final int maxBodySize = 2 * 1024 * 1024;

    public static final String SCHEMA_HTTP = "http";
    public static final String SCHEMA_HTTPS = "https";


    /**
     * Horizontal space
     */
    public static final byte SP = 32;

    /**
     * Carriage return
     */
    public static final byte CR = 13;

    /**
     * Line feed character
     */
    public static final byte LF = 10;

    public static final byte[] CRLF_BYTES = {CR, LF};

    public static final byte[] CRLF_CRLF_BYTES = {CR, LF, CR, LF};


    public static final byte[] CHUNKED_END_BYTES = "0\r\n\r\n".getBytes(StandardCharsets.US_ASCII);

    public static final byte[] EMPTY_BYTES = {};

    public static Date currentTime() {
        return currentTime;
    }

    public static Date parseRFC1123(String date) throws ParseException {
        return RFC1123_DATE_FORMAT.get().parse(date);
    }

    public static String formatRFC1123(Date date) {
        return RFC1123_DATE_FORMAT.get().format(date);
    }


    public static String formatCookieExpire(Date date) {
        return COOKIE_FORMAT.get().format(date);
    }

    public static String getResourceAsString(String fileName) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (InputStream inputStream = FeatUtils.class.getClassLoader().getResourceAsStream(fileName);) {
            if (inputStream == null) {
                LOGGER.error("resource {} not found", fileName);
                return null;
            }
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            return outputStream.toString();
        } catch (IOException e) {
            LOGGER.error("read resource {} error", fileName, e);
            return null;
        }
    }

    public static byte[] toByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, len);
        }
        return outputStream.toByteArray();
    }

    public static String asString(InputStream inputStream) throws IOException {
        return new String(toByteArray(inputStream));
    }


    public static int toInt(final String str, final int defaultValue) {
        if (str == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(str);
        } catch (final NumberFormatException nfe) {
            return defaultValue;
        }
    }

    public static long toLong(final String str, final long defaultValue) {
        if (str == null) {
            return defaultValue;
        }
        try {
            return Long.parseLong(str);
        } catch (final NumberFormatException nfe) {
            return defaultValue;
        }
    }

    public static <T> boolean isEmpty(Collection<T> collection) {
        return collection == null || collection.isEmpty();
    }

    public static <K, V> boolean isEmpty(Map<K, V> map) {
        return map == null || map.isEmpty();
    }

    public static <T> boolean isNotEmpty(Collection<T> collection) {
        return !isEmpty(collection);
    }

    public static <K, V> boolean isNotEmpty(Map<K, V> map) {
        return !isEmpty(map);
    }

    public static final String GZIP_ENCODE_UTF_8 = "UTF-8";
    public static final String GZIP_ENCODE_ISO_8859_1 = "ISO-8859-1";

    public static byte[] gzip(byte[] data) {
        return gzip(data, 0, data.length);
    }

    public static byte[] gzip(byte[] data, int offset, int length) {
        return gzip(data, offset, length, GZIP_ENCODE_UTF_8);
    }

    public static byte[] gzip(byte[] data, int offset, int length, String encoding) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip;
        try {
            gzip = new GZIPOutputStream(out);
            gzip.write(data, offset, length);
            gzip.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }

    public static byte[] ungzip(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        try {
            GZIPInputStream ungzip = new GZIPInputStream(in);
            byte[] buffer = new byte[256];
            int n;
            while ((n = ungzip.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }

    public static String ungzipToString(byte[] bytes, String encoding) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        try {
            GZIPInputStream ungzip = new GZIPInputStream(in);
            byte[] buffer = new byte[256];
            int n;
            while ((n = ungzip.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }
            return out.toString(encoding);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String ungzipToString(byte[] bytes) {
        return ungzipToString(bytes, GZIP_ENCODE_UTF_8);
    }

    private static final String DOMAIN = "$Domain";
    private static final String VERSION = "$Version";
    private static final String PATH = "$Path";

    /**
     * 解码URI中的参数
     *
     * @param paramStr http参数字符串： aa=111&bb=222
     * @param paramMap 参数存放Map
     */
    public static void decodeParamString(String paramStr, Map<String, String[]> paramMap) {
        if (isBlank(paramStr)) {
            return;
        }
        String[] uriParamStrArray = split(paramStr, "&");
        for (String param : uriParamStrArray) {
            int index = param.indexOf("=");
            if (index == -1) {
                continue;
            }
            try {
                String key = URLDecoder.decode(substring(param, 0, index), "utf8");
                String value = URLDecoder.decode(substring(param, index + 1), "utf8");
                String[] values = paramMap.get(key);
                if (values == null) {
                    paramMap.put(key, new String[]{value});
                } else {
                    String[] newValue = new String[values.length + 1];
                    System.arraycopy(values, 0, newValue, 0, values.length);
                    newValue[values.length] = value;
                    paramMap.put(key, newValue);
                }

            } catch (Throwable e) {
                throw new FeatException(e);
            }
        }
    }

    public static List<Cookie> decodeCookies(String cookieStr) {
        List<Cookie> cookies = new ArrayList<>();
        decode(cookies, cookieStr, 0, new HashMap<>());
        return cookies;
    }

    private static void decode(List<Cookie> cookies, String cookieStr, int offset, Map<String, String> cache) {
        while (offset < cookieStr.length() && cookieStr.charAt(offset) == ' ') {
            offset++;
        }
        if (offset >= cookieStr.length()) {
            return;
        }
        int index = cookieStr.indexOf('=', offset);
        if (index == -1) {
            return;
        }
        String name = cookieStr.substring(offset, index);
        int end = cookieStr.indexOf(';', index);
        int trimEnd = end;
        if (trimEnd == -1) {
            trimEnd = cookieStr.length();
        }
        while (cookieStr.charAt(trimEnd - 1) == ' ') {
            trimEnd--;
        }
        String value = cookieStr.substring(index + 1, trimEnd);

        if (name.charAt(0) == '$') {
            if (cookies.isEmpty()) {
                cache.put(name, value);
            } else {
                Cookie cookie = cookies.get(cookies.size() - 1);
                switch (name) {
                    case DOMAIN:
                        cookie.setDomain(value);
                        break;
                    case PATH:
                        cookie.setPath(value);
                        break;
                }
            }
        } else {
            Cookie cookie = new Cookie(name, value);
            if (!cache.isEmpty()) {
                cache.forEach((key, v) -> {
                    switch (key) {
                        case DOMAIN:
                            cookie.setDomain(v);
                            break;
                        case PATH:
                            cookie.setPath(v);
                            break;
                    }
                });
                cache.clear();
            }
            cookies.add(cookie);
        }
        if (end != -1) {
            decode(cookies, cookieStr, end + 1, cache);
        }
    }

    /**
     * An empty immutable {@code String} array.
     */
    private static final String[] EMPTY_STRING_ARRAY = new String[0];
    /**
     * The empty String {@code ""}.
     *
     * @since 2.0
     */
    public static final String EMPTY = "";


    /**
     * Represents a failed index search.
     *
     * @since 2.1
     */
    public static final int INDEX_NOT_FOUND = -1;

    public static String trim(final String str) {
        return str == null ? null : str.trim();
    }

    public static int convertHexString(ByteBuffer buffer, int offset, int length) {
        int v = 0;
        for (int i = 0; i < length; i++) {
            byte b = buffer.get(offset + i);
            if (b >= '0' && b <= '9') {
                v = v * 16 + (b - '0');
            } else if (b >= 'a' && b <= 'f') {
                v = v * 16 + (b - 'a' + 10);
            } else if (b >= 'A' && b <= 'F') {
                v = v * 16 + (b - 'A' + 10);
            } else {
                throw new IllegalArgumentException("Invalid hex char");
            }
        }
        return v;
    }


    public static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    public static boolean isBlank(final CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNotBlank(final CharSequence cs) {
        return !isBlank(cs);
    }

    public static boolean equals(final CharSequence cs1, final CharSequence cs2) {
        if (cs1 == cs2) {
            return true;
        }
        if (cs1 == null || cs2 == null) {
            return false;
        }
        if (cs1 instanceof String && cs2 instanceof String) {
            return cs1.equals(cs2);
        }
        return regionMatches(cs1, false, 0, cs2, 0, Math.max(cs1.length(), cs2.length()));
    }

    private static boolean regionMatches(final CharSequence cs, final boolean ignoreCase, final int thisStart, final CharSequence substring, final int start, final int length) {
        if (cs instanceof String && substring instanceof String) {
            return ((String) cs).regionMatches(ignoreCase, thisStart, (String) substring, start, length);
        } else {
            int index1 = thisStart;
            int index2 = start;
            int tmpLen = length;

            while (tmpLen-- > 0) {
                char c1 = cs.charAt(index1++);
                char c2 = substring.charAt(index2++);

                if (c1 == c2) {
                    continue;
                }

                if (!ignoreCase) {
                    return false;
                }

                // The same check as in String.regionMatches():
                if (Character.toUpperCase(c1) != Character.toUpperCase(c2) && Character.toLowerCase(c1) != Character.toLowerCase(c2)) {
                    return false;
                }
            }

            return true;
        }
    }

    public static String substring(final String str, int start) {
        if (str == null) {
            return null;
        }

        if (start < 0) {
            start = str.length() + start;
        }

        if (start < 0) {
            start = 0;
        }
        if (start > str.length()) {
            return EMPTY;
        }

        return str.substring(start);
    }

    public static String substring(final String str, int start, int end) {
        if (str == null) {
            return null;
        }

        if (end < 0) {
            end = str.length() + end;
        }
        if (start < 0) {
            start = str.length() + start;
        }

        if (end > str.length()) {
            end = str.length();
        }

        if (start > end) {
            return EMPTY;
        }

        if (start < 0) {
            start = 0;
        }
        if (end < 0) {
            end = 0;
        }

        return str.substring(start, end);
    }

    public static String substringBefore(final String str, final String separator) {
        if (isEmpty(str) || separator == null) {
            return str;
        }
        if (separator.isEmpty()) {
            return EMPTY;
        }
        final int pos = str.indexOf(separator);
        if (pos == INDEX_NOT_FOUND) {
            return str;
        }
        return str.substring(0, pos);
    }

    public static String substringAfter(final String str, final String separator) {
        if (isEmpty(str)) {
            return str;
        }
        if (separator == null) {
            return EMPTY;
        }
        final int pos = str.indexOf(separator);
        if (pos == INDEX_NOT_FOUND) {
            return EMPTY;
        }
        return str.substring(pos + separator.length());
    }

    public static String[] split(final String str, final String separatorChars) {
        return splitWorker(str, separatorChars, -1, false);
    }

    public static String[] splitPreserveAllTokens(final String str, final String separatorChars) {
        return splitWorker(str, separatorChars, -1, true);
    }

    private static String[] splitWorker(final String str, final String separatorChars, final int max, final boolean preserveAllTokens) {

        if (str == null) {
            return null;
        }
        final int len = str.length();
        if (len == 0) {
            return EMPTY_STRING_ARRAY;
        }
        final List<String> list = new ArrayList<String>();
        int sizePlus1 = 1;
        int i = 0, start = 0;
        boolean match = false;
        boolean lastMatch = false;
        if (separatorChars == null) {
            // Null separator means use whitespace
            while (i < len) {
                if (Character.isWhitespace(str.charAt(i))) {
                    if (match || preserveAllTokens) {
                        lastMatch = true;
                        if (sizePlus1++ == max) {
                            i = len;
                            lastMatch = false;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                    continue;
                }
                lastMatch = false;
                match = true;
                i++;
            }
        } else if (separatorChars.length() == 1) {
            // Optimise 1 character case
            final char sep = separatorChars.charAt(0);
            while (i < len) {
                if (str.charAt(i) == sep) {
                    if (match || preserveAllTokens) {
                        lastMatch = true;
                        if (sizePlus1++ == max) {
                            i = len;
                            lastMatch = false;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                    continue;
                }
                lastMatch = false;
                match = true;
                i++;
            }
        } else {
            // standard case
            while (i < len) {
                if (separatorChars.indexOf(str.charAt(i)) >= 0) {
                    if (match || preserveAllTokens) {
                        lastMatch = true;
                        if (sizePlus1++ == max) {
                            i = len;
                            lastMatch = false;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                    continue;
                }
                lastMatch = false;
                match = true;
                i++;
            }
        }
        if (match || preserveAllTokens && lastMatch) {
            list.add(str.substring(start, i));
        }
        return list.toArray(new String[list.size()]);
    }


    public static int length(final CharSequence cs) {
        return cs == null ? 0 : cs.length();
    }

    public static boolean startsWith(final CharSequence str, final CharSequence prefix) {
        return startsWith(str, prefix, false);
    }

    private static boolean startsWith(final CharSequence str, final CharSequence prefix, final boolean ignoreCase) {
        if (str == null || prefix == null) {
            return str == null && prefix == null;
        }
        if (prefix.length() > str.length()) {
            return false;
        }
        return regionMatches(str, ignoreCase, 0, prefix, 0, prefix.length());
    }

    public static boolean endsWith(final CharSequence str, final CharSequence suffix) {
        return endsWith(str, suffix, false);
    }

    private static boolean endsWith(final CharSequence str, final CharSequence suffix, final boolean ignoreCase) {
        if (str == null || suffix == null) {
            return str == null && suffix == null;
        }
        if (suffix.length() > str.length()) {
            return false;
        }
        final int strOffset = str.length() - suffix.length();
        return regionMatches(str, ignoreCase, strOffset, suffix, 0, suffix.length());
    }

    public static <T> ByteTree<T> scanByteTree(ByteBuffer buffer, ByteTree.EndMatcher endMatcher, ByteTree<T> cache) {
        return cache.search(buffer, endMatcher);
    }


    public static int scanUntilAndTrim(ByteBuffer buffer, byte split) {
        trimBuffer(buffer);
        int position = buffer.position() + buffer.arrayOffset();
        int limit = buffer.limit() + buffer.arrayOffset();
        byte[] data = buffer.array();
        while (position < limit) {
            if (data[position++] == split) {
                int length = position - buffer.arrayOffset() - buffer.position() - 1;
                buffer.position(position - buffer.arrayOffset());
                return length;
            }
        }
        return -1;
    }

    private static void trimBuffer(ByteBuffer buffer) {
        while (buffer.hasRemaining() && buffer.get(buffer.position()) == FeatUtils.SP) {
            buffer.position(buffer.position() + 1);
        }
    }

    private final static byte[] DEFAULT_BYTES = ("feat:" + Feat.VERSION).getBytes();
    private final static int maskLength = 4;
    private final static String MAGIC_NUMBER = "feat-";

    public static String createSessionId() {
        Random random = new Random();
        //掩码+固定前缀+时间戳
        byte[] bytes = new byte[maskLength + DEFAULT_BYTES.length + Integer.BYTES];

        for (int i = 0; i < maskLength; ) {
            for (int rnd = random.nextInt(), n = Math.min(maskLength - i, Integer.SIZE / Byte.SIZE); n-- > 0; rnd >>= Byte.SIZE)
                bytes[i++] = (byte) rnd;
        }
        System.arraycopy(DEFAULT_BYTES, 0, bytes, maskLength, DEFAULT_BYTES.length);
        //将System.nanoTime()填充至bytes后四字节
        int time = (int) System.nanoTime();
        bytes[maskLength + DEFAULT_BYTES.length] = (byte) (time >>> 24);
        bytes[maskLength + DEFAULT_BYTES.length + 1] = (byte) (time >>> 16);
        bytes[maskLength + DEFAULT_BYTES.length + 2] = (byte) (time >>> 8);
        bytes[maskLength + DEFAULT_BYTES.length + 3] = (byte) (time);

        for (int i = maskLength; i < bytes.length; i++) {
            bytes[i] = (byte) (bytes[i] ^ bytes[i % maskLength]);
        }

        return MAGIC_NUMBER + Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
