/*******************************************************************************
 * Copyright (c) 2017-2020, org.smartboot. All rights reserved.
 * project name: smart-http
 * file name: StringUtils.java
 * Date: 2020-01-01
 * Author: sandao (zhengjunweimail@163.com)
 ******************************************************************************/
package org.smartboot.http.utils;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

/**
 * <p>Operations on {@link java.lang.String} that are
 * {@code null} safe.</p>
 *
 * <ul>
 * <li><b>IsEmpty/IsBlank</b>
 * - checks if a String contains text</li>
 * <li><b>Trim/Strip</b>
 * - removes leading and trailing whitespace</li>
 * <li><b>Equals</b>
 * - compares two strings null-safe</li>
 * <li><b>startsWith</b>
 * - check if a String starts with a prefix null-safe</li>
 * <li><b>endsWith</b>
 * - check if a String ends with a suffix null-safe</li>
 * <li><b>IndexOf/LastIndexOf/Contains</b>
 * - null-safe index-of checks
 * <li><b>IndexOfAny/LastIndexOfAny/IndexOfAnyBut/LastIndexOfAnyBut</b>
 * - index-of any of a set of Strings</li>
 * <li><b>ContainsOnly/ContainsNone/ContainsAny</b>
 * - does String contains only/none/any of these characters</li>
 * <li><b>Substring/Left/Right/Mid</b>
 * - null-safe substring extractions</li>
 * <li><b>SubstringBefore/SubstringAfter/SubstringBetween</b>
 * - substring extraction relative to other strings</li>
 * <li><b>Split/Join</b>
 * - splits a String into an array of substrings and vice versa</li>
 * <li><b>Remove/Delete</b>
 * - removes part of a String</li>
 * <li><b>Replace/Overlay</b>
 * - Searches a String and replaces one String with another</li>
 * <li><b>Chomp/Chop</b>
 * - removes the last part of a String</li>
 * <li><b>AppendIfMissing</b>
 * - appends a suffix to the end of the String if not present</li>
 * <li><b>PrependIfMissing</b>
 * - prepends a prefix to the start of the String if not present</li>
 * <li><b>LeftPad/RightPad/Center/Repeat</b>
 * - pads a String</li>
 * <li><b>UpperCase/LowerCase/SwapCase/Capitalize/Uncapitalize</b>
 * - changes the case of a String</li>
 * <li><b>CountMatches</b>
 * - counts the number of occurrences of one String in another</li>
 * <li><b>IsAlpha/IsNumeric/IsWhitespace/IsAsciiPrintable</b>
 * - checks the characters in a String</li>
 * <li><b>DefaultString</b>
 * - protects against a null input String</li>
 * <li><b>Reverse/ReverseDelimited</b>
 * - reverses a String</li>
 * <li><b>Abbreviate</b>
 * - abbreviates a string using ellipsis</li>
 * <li><b>Difference</b>
 * - compares Strings and reports on their differences</li>
 * <li><b>LevenshteinDistance</b>
 * - the number of changes needed to change one String into another</li>
 * </ul>
 *
 * <p>The {@code StringUtils} class defines certain words related to
 * String handling.</p>
 *
 * <ul>
 * <li>null - {@code null}</li>
 * <li>empty - a zero-length string ({@code ""})</li>
 * <li>space - the space character ({@code ' '}, char 32)</li>
 * <li>whitespace - the characters defined by {@link Character#isWhitespace(char)}</li>
 * <li>trim - the characters &lt;= 32 as in {@link String#trim()}</li>
 * </ul>
 *
 * <p>{@code StringUtils} handles {@code null} input Strings quietly.
 * That is to say that a {@code null} input will return {@code null}.
 * Where a {@code boolean} or {@code int} is being returned
 * details vary by method.</p>
 *
 * <p>A side effect of the {@code null} handling is that a
 * {@code NullPointerException} should be considered a bug in
 * {@code StringUtils}.</p>
 *
 * <p>Methods in this class give sample code to explain their operation.
 * The symbol {@code *} is used to indicate any input including {@code null}.</p>
 *
 * <p>#ThreadSafe#</p>
 *
 * @version $Id: StringUtils.java 1553928 2013-12-28 21:18:46Z ggregory $
 * @see java.lang.String
 * @since 1.0
 */
//@Immutable
public class StringUtils {

    /**
     * An empty immutable {@code String} array.
     */
    public static final String[] EMPTY_STRING_ARRAY = new String[0];
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
    public static final List<StringCache>[] String_CACHE_URL = new List[512];
    public static final List<StringCache>[] String_CACHE_HEADER_NAME = new List[32];
    public static final List<StringCache>[] String_CACHE_HEADER_VALUE = new List[512];

    static {
        for (int i = 0; i < String_CACHE_URL.length; i++) {
            String_CACHE_URL[i] = new ArrayList<>(8);
        }
        for (int i = 0; i < String_CACHE_HEADER_NAME.length; i++) {
            String_CACHE_HEADER_NAME[i] = new ArrayList<>(8);
        }
        for (int i = 0; i < String_CACHE_HEADER_VALUE.length; i++) {
            String_CACHE_HEADER_VALUE[i] = new ArrayList<>(8);
        }
    }

    /**
     * <p>{@code StringUtils} instances should NOT be constructed in
     * standard programming. Instead, the class should be used as
     * {@code StringUtils.trim(" foo ");}.</p>
     *
     * <p>This constructor is public to permit tools that require a JavaBean
     * instance to operate.</p>
     */
    public StringUtils() {
        super();
    }

    public static String convertToString(char[] bytes, int length, List<StringCache>[] cacheList) {
        return convertToString(bytes, 0, length, cacheList);
    }

    public static String convertToString(char[] bytes, int offset, int length, List<StringCache>[] cacheList) {
        if (length >= cacheList.length) {
            return new String(bytes, offset, length);
        }
        List<StringCache> list = cacheList[length];
        for (int i = list.size() - 1; i > -1; i--) {
            StringCache cache = list.get(i);
            if (equals(cache.bytes, bytes, offset)) {
                return cache.value;
            }
        }
        synchronized (list) {
            for (StringCache cache : list) {
                if (equals(cache.bytes, bytes, offset)) {
                    return cache.value;
                }
            }
            String str = new String(bytes, offset, length);
            list.add(new StringCache(str.toCharArray(), str));
            return str;
        }
    }

    private static boolean equals(char[] b0, char[] b1, int offset) {
        for (int i = b0.length - 1; i > 0; i--) {
            if (b0[i] != b1[i + offset]) {
                return false;
            }
        }
        return b0[0] == b1[offset];
    }

    /**
     * <p>Checks if a CharSequence is empty ("") or null.</p>
     *
     * <pre>
     * StringUtils.isEmpty(null)      = true
     * StringUtils.isEmpty("")        = true
     * StringUtils.isEmpty(" ")       = false
     * StringUtils.isEmpty("bob")     = false
     * StringUtils.isEmpty("  bob  ") = false
     * </pre>
     *
     * <p>NOTE: This method changed in Lang version 2.0.
     * It no longer trims the CharSequence.
     * That functionality is available in isBlank().</p>
     *
     * @param cs the CharSequence to check, may be null
     * @return {@code true} if the CharSequence is empty or null
     * @since 3.0 Changed signature from isEmpty(String) to isEmpty(CharSequence)
     */
    public static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    // Empty checks
    //-----------------------------------------------------------------------

    /**
     * <p>Checks if a CharSequence is whitespace, empty ("") or null.</p>
     *
     * <pre>
     * StringUtils.isBlank(null)      = true
     * StringUtils.isBlank("")        = true
     * StringUtils.isBlank(" ")       = true
     * StringUtils.isBlank("bob")     = false
     * StringUtils.isBlank("  bob  ") = false
     * </pre>
     *
     * @param cs the CharSequence to check, may be null
     * @return {@code true} if the CharSequence is null, empty or whitespace
     * @since 2.0
     * @since 3.0 Changed signature from isBlank(String) to isBlank(CharSequence)
     */
    public static boolean isBlank(final CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (Character.isWhitespace(cs.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * <p>Checks if a CharSequence is not empty (""), not null and not whitespace only.</p>
     *
     * <pre>
     * StringUtils.isNotBlank(null)      = false
     * StringUtils.isNotBlank("")        = false
     * StringUtils.isNotBlank(" ")       = false
     * StringUtils.isNotBlank("bob")     = true
     * StringUtils.isNotBlank("  bob  ") = true
     * </pre>
     *
     * @param cs the CharSequence to check, may be null
     * @return {@code true} if the CharSequence is
     * not empty and not null and not whitespace
     * @since 2.0
     * @since 3.0 Changed signature from isNotBlank(String) to isNotBlank(CharSequence)
     */
    public static boolean isNotBlank(final CharSequence cs) {
        return !StringUtils.isBlank(cs);
    }

    /**
     * <p>Compares two CharSequences, returning {@code true} if they represent
     * equal sequences of characters.</p>
     *
     * <p>{@code null}s are handled without exceptions. Two {@code null}
     * references are considered to be equal. The comparison is case sensitive.</p>
     *
     * <pre>
     * StringUtils.equals(null, null)   = true
     * StringUtils.equals(null, "abc")  = false
     * StringUtils.equals("abc", null)  = false
     * StringUtils.equals("abc", "abc") = true
     * StringUtils.equals("abc", "ABC") = false
     * </pre>
     *
     * @param cs1 the first CharSequence, may be {@code null}
     * @param cs2 the second CharSequence, may be {@code null}
     * @return {@code true} if the CharSequences are equal (case-sensitive), or both {@code null}
     * @see Object#equals(Object)
     * @since 3.0 Changed signature from equals(String, String) to equals(CharSequence, CharSequence)
     */
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


    // Equals
    //-----------------------------------------------------------------------

    private static boolean regionMatches(final CharSequence cs, final boolean ignoreCase, final int thisStart,
                                         final CharSequence substring, final int start, final int length) {
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
                if (Character.toUpperCase(c1) != Character.toUpperCase(c2)
                        && Character.toLowerCase(c1) != Character.toLowerCase(c2)) {
                    return false;
                }
            }

            return true;
        }
    }

    /**
     * <p>Finds the first index within a CharSequence, handling {@code null}.
     * This method uses {@link String#indexOf(String, int)} if possible.</p>
     *
     * <p>A {@code null} CharSequence will return {@code -1}.
     * A negative start position is treated as zero.
     * An empty ("") search CharSequence always matches.
     * A start position greater than the string length only matches
     * an empty search CharSequence.</p>
     *
     * <pre>
     * StringUtils.indexOf(null, *, *)          = -1
     * StringUtils.indexOf(*, null, *)          = -1
     * StringUtils.indexOf("", "", 0)           = 0
     * StringUtils.indexOf("", *, 0)            = -1 (except when * = "")
     * StringUtils.indexOf("aabaabaa", "a", 0)  = 0
     * StringUtils.indexOf("aabaabaa", "b", 0)  = 2
     * StringUtils.indexOf("aabaabaa", "ab", 0) = 1
     * StringUtils.indexOf("aabaabaa", "b", 3)  = 5
     * StringUtils.indexOf("aabaabaa", "b", 9)  = -1
     * StringUtils.indexOf("aabaabaa", "b", -1) = 2
     * StringUtils.indexOf("aabaabaa", "", 2)   = 2
     * StringUtils.indexOf("abc", "", 9)        = 3
     * </pre>
     *
     * @param seq       the CharSequence to check, may be null
     * @param searchSeq the CharSequence to find, may be null
     * @param startPos  the start position, negative treated as zero
     * @return the first index of the search CharSequence (always &ge; startPos),
     * -1 if no match or {@code null} string input
     * @since 2.0
     * @since 3.0 Changed signature from indexOf(String, String, int) to indexOf(CharSequence, CharSequence, int)
     */
    public static int indexOf(final CharSequence seq, final CharSequence searchSeq, final int startPos) {
        if (seq == null || searchSeq == null) {
            return INDEX_NOT_FOUND;
        }
        return seq.toString().indexOf(searchSeq.toString(), startPos);
    }

    /**
     * <p>Gets a substring from the specified String avoiding exceptions.</p>
     *
     * <p>A negative start position can be used to start {@code n}
     * characters from the end of the String.</p>
     *
     * <p>A {@code null} String will return {@code null}.
     * An empty ("") String will return "".</p>
     *
     * <pre>
     * StringUtils.substring(null, *)   = null
     * StringUtils.substring("", *)     = ""
     * StringUtils.substring("abc", 0)  = "abc"
     * StringUtils.substring("abc", 2)  = "c"
     * StringUtils.substring("abc", 4)  = ""
     * StringUtils.substring("abc", -2) = "bc"
     * StringUtils.substring("abc", -4) = "abc"
     * </pre>
     *
     * @param str   the String to get the substring from, may be null
     * @param start the position to start from, negative means
     *              count back from the end of the String by this many characters
     * @return substring from start position, {@code null} if null String input
     */
    public static String substring(final String str, int start) {
        if (str == null) {
            return null;
        }

        // handle negatives, which means last n characters
        if (start < 0) {
            start = str.length() + start; // remember start is negative
        }

        if (start < 0) {
            start = 0;
        }
        if (start > str.length()) {
            return EMPTY;
        }

        return str.substring(start);
    }


    // Substring
    //-----------------------------------------------------------------------

    /**
     * <p>Gets a substring from the specified String avoiding exceptions.</p>
     *
     * <p>A negative start position can be used to start/end {@code n}
     * characters from the end of the String.</p>
     *
     * <p>The returned substring starts with the character in the {@code start}
     * position and ends before the {@code end} position. All position counting is
     * zero-based -- i.e., to start at the beginning of the string use
     * {@code start = 0}. Negative start and end positions can be used to
     * specify offsets relative to the end of the String.</p>
     *
     * <p>If {@code start} is not strictly to the left of {@code end}, ""
     * is returned.</p>
     *
     * <pre>
     * StringUtils.substring(null, *, *)    = null
     * StringUtils.substring("", * ,  *)    = "";
     * StringUtils.substring("abc", 0, 2)   = "ab"
     * StringUtils.substring("abc", 2, 0)   = ""
     * StringUtils.substring("abc", 2, 4)   = "c"
     * StringUtils.substring("abc", 4, 6)   = ""
     * StringUtils.substring("abc", 2, 2)   = ""
     * StringUtils.substring("abc", -2, -1) = "b"
     * StringUtils.substring("abc", -4, 2)  = "ab"
     * </pre>
     *
     * @param str   the String to get the substring from, may be null
     * @param start the position to start from, negative means
     *              count back from the end of the String by this many characters
     * @param end   the position to end at (exclusive), negative means
     *              count back from the end of the String by this many characters
     * @return substring from start position to end position,
     * {@code null} if null String input
     */
    public static String substring(final String str, int start, int end) {
        if (str == null) {
            return null;
        }

        // handle negatives
        if (end < 0) {
            end = str.length() + end; // remember end is negative
        }
        if (start < 0) {
            start = str.length() + start; // remember start is negative
        }

        // check length next
        if (end > str.length()) {
            end = str.length();
        }

        // if start is greater than end, return ""
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

    /**
     * <p>Gets the substring before the first occurrence of a separator.
     * The separator is not returned.</p>
     *
     * <p>A {@code null} string input will return {@code null}.
     * An empty ("") string input will return the empty string.
     * A {@code null} separator will return the input string.</p>
     *
     * <p>If nothing is found, the string input is returned.</p>
     *
     * <pre>
     * StringUtils.substringBefore(null, *)      = null
     * StringUtils.substringBefore("", *)        = ""
     * StringUtils.substringBefore("abc", "a")   = ""
     * StringUtils.substringBefore("abcba", "b") = "a"
     * StringUtils.substringBefore("abc", "c")   = "ab"
     * StringUtils.substringBefore("abc", "d")   = "abc"
     * StringUtils.substringBefore("abc", "")    = ""
     * StringUtils.substringBefore("abc", null)  = "abc"
     * </pre>
     *
     * @param str       the String to get a substring from, may be null
     * @param separator the String to search for, may be null
     * @return the substring before the first occurrence of the separator,
     * {@code null} if null String input
     * @since 2.0
     */
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

    // SubStringAfter/SubStringBefore
    //-----------------------------------------------------------------------

    /**
     * <p>Gets the substring after the first occurrence of a separator.
     * The separator is not returned.</p>
     *
     * <p>A {@code null} string input will return {@code null}.
     * An empty ("") string input will return the empty string.
     * A {@code null} separator will return the empty string if the
     * input string is not {@code null}.</p>
     *
     * <p>If nothing is found, the empty string is returned.</p>
     *
     * <pre>
     * StringUtils.substringAfter(null, *)      = null
     * StringUtils.substringAfter("", *)        = ""
     * StringUtils.substringAfter(*, null)      = ""
     * StringUtils.substringAfter("abc", "a")   = "bc"
     * StringUtils.substringAfter("abcba", "b") = "cba"
     * StringUtils.substringAfter("abc", "c")   = ""
     * StringUtils.substringAfter("abc", "d")   = ""
     * StringUtils.substringAfter("abc", "")    = "abc"
     * </pre>
     *
     * @param str       the String to get a substring from, may be null
     * @param separator the String to search for, may be null
     * @return the substring after the first occurrence of the separator,
     * {@code null} if null String input
     * @since 2.0
     */
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

    /**
     * <p>Splits the provided text into an array, separators specified.
     * This is an alternative to using StringTokenizer.</p>
     *
     * <p>The separator is not included in the returned String array.
     * Adjacent separators are treated as one separator.
     * For more control over the split use the StrTokenizer class.</p>
     *
     * <p>A {@code null} input String returns {@code null}.
     * A {@code null} separatorChars splits on whitespace.</p>
     *
     * <pre>
     * StringUtils.split(null, *)         = null
     * StringUtils.split("", *)           = []
     * StringUtils.split("abc def", null) = ["abc", "def"]
     * StringUtils.split("abc def", " ")  = ["abc", "def"]
     * StringUtils.split("abc  def", " ") = ["abc", "def"]
     * StringUtils.split("ab:cd:ef", ":") = ["ab", "cd", "ef"]
     * </pre>
     *
     * @param str            the String to parse, may be null
     * @param separatorChars the characters used as the delimiters,
     *                       {@code null} splits on whitespace
     * @return an array of parsed Strings, {@code null} if null String input
     */
    public static String[] split(final String str, final String separatorChars) {
        return splitWorker(str, separatorChars, -1, false);
    }

    /**
     * <p>Splits the provided text into an array, separators specified,
     * preserving all tokens, including empty tokens created by adjacent
     * separators. This is an alternative to using StringTokenizer.</p>
     *
     * <p>The separator is not included in the returned String array.
     * Adjacent separators are treated as separators for empty tokens.
     * For more control over the split use the StrTokenizer class.</p>
     *
     * <p>A {@code null} input String returns {@code null}.
     * A {@code null} separatorChars splits on whitespace.</p>
     *
     * <pre>
     * StringUtils.splitPreserveAllTokens(null, *)           = null
     * StringUtils.splitPreserveAllTokens("", *)             = []
     * StringUtils.splitPreserveAllTokens("abc def", null)   = ["abc", "def"]
     * StringUtils.splitPreserveAllTokens("abc def", " ")    = ["abc", "def"]
     * StringUtils.splitPreserveAllTokens("abc  def", " ")   = ["abc", "", def"]
     * StringUtils.splitPreserveAllTokens("ab:cd:ef", ":")   = ["ab", "cd", "ef"]
     * StringUtils.splitPreserveAllTokens("ab:cd:ef:", ":")  = ["ab", "cd", "ef", ""]
     * StringUtils.splitPreserveAllTokens("ab:cd:ef::", ":") = ["ab", "cd", "ef", "", ""]
     * StringUtils.splitPreserveAllTokens("ab::cd:ef", ":")  = ["ab", "", cd", "ef"]
     * StringUtils.splitPreserveAllTokens(":cd:ef", ":")     = ["", cd", "ef"]
     * StringUtils.splitPreserveAllTokens("::cd:ef", ":")    = ["", "", cd", "ef"]
     * StringUtils.splitPreserveAllTokens(":cd:ef:", ":")    = ["", cd", "ef", ""]
     * </pre>
     *
     * @param str            the String to parse, may be {@code null}
     * @param separatorChars the characters used as the delimiters,
     *                       {@code null} splits on whitespace
     * @return an array of parsed Strings, {@code null} if null String input
     * @since 2.1
     */
    public static String[] splitPreserveAllTokens(final String str, final String separatorChars) {
        return splitWorker(str, separatorChars, -1, true);
    }

    /**
     * Performs the logic for the {@code split} and
     * {@code splitPreserveAllTokens} methods that return a maximum array
     * length.
     *
     * @param str               the String to parse, may be {@code null}
     * @param separatorChars    the separate character
     * @param max               the maximum number of elements to include in the
     *                          array. A zero or negative value implies no limit.
     * @param preserveAllTokens if {@code true}, adjacent separators are
     *                          treated as empty token separators; if {@code false}, adjacent
     *                          separators are treated as one separator.
     * @return an array of parsed Strings, {@code null} if null String input
     */
    private static String[] splitWorker(final String str, final String separatorChars, final int max, final boolean preserveAllTokens) {
        // Performance tuned for 2.0 (JDK1.4)
        // Direct code is quicker than StringTokenizer.
        // Also, StringTokenizer uses isSpace() not isWhitespace()

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

    public static boolean hasLength(CharSequence str) {
        return (str != null && str.length() > 0);
    }

    /**
     * Check that the given String is neither {@code null} nor of length 0.
     * Note: Will return {@code true} for a String that purely consists of whitespace.
     *
     * @param str the String to check (may be {@code null})
     * @return {@code true} if the String is not null and has length
     * @see #hasLength(CharSequence)
     */
    public static boolean hasLength(String str) {
        return hasLength((CharSequence) str);
    }

    public static String[] tokenizeToStringArray(
            String str, String delimiters, boolean trimTokens, boolean ignoreEmptyTokens) {

        if (str == null) {
            return null;
        }
        StringTokenizer st = new StringTokenizer(str, delimiters);
        List<String> tokens = new ArrayList<String>();
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (trimTokens) {
                token = token.trim();
            }
            if (!ignoreEmptyTokens || token.length() > 0) {
                tokens.add(token);
            }
        }
        return toStringArray(tokens);
    }

    public static String[] toStringArray(Collection<String> collection) {
        if (collection == null) {
            return null;
        }
        return collection.toArray(new String[collection.size()]);
    }

    /**
     * Gets a CharSequence length or {@code 0} if the CharSequence is
     * {@code null}.
     *
     * @param cs a CharSequence or {@code null}
     * @return CharSequence length or {@code 0} if the CharSequence is
     * {@code null}.
     * @since 2.4
     * @since 3.0 Changed signature from length(String) to length(CharSequence)
     */
    public static int length(final CharSequence cs) {
        return cs == null ? 0 : cs.length();
    }

    /**
     * <p>Check if a CharSequence starts with a specified prefix.</p>
     *
     * <p>{@code null}s are handled without exceptions. Two {@code null}
     * references are considered to be equal. The comparison is case sensitive.</p>
     *
     * <pre>
     * StringUtils.startsWith(null, null)      = true
     * StringUtils.startsWith(null, "abc")     = false
     * StringUtils.startsWith("abcdef", null)  = false
     * StringUtils.startsWith("abcdef", "abc") = true
     * StringUtils.startsWith("ABCDEF", "abc") = false
     * </pre>
     *
     * @param str    the CharSequence to check, may be null
     * @param prefix the prefix to find, may be null
     * @return {@code true} if the CharSequence starts with the prefix, case sensitive, or
     * both {@code null}
     * @see java.lang.String#startsWith(String)
     * @since 2.4
     * @since 3.0 Changed signature from startsWith(String, String) to startsWith(CharSequence, CharSequence)
     */
    public static boolean startsWith(final CharSequence str, final CharSequence prefix) {
        return startsWith(str, prefix, false);
    }


    // startsWith
    //-----------------------------------------------------------------------

    /**
     * <p>Check if a CharSequence starts with a specified prefix (optionally case insensitive).</p>
     *
     * @param str        the CharSequence to check, may be null
     * @param prefix     the prefix to find, may be null
     * @param ignoreCase indicates whether the compare should ignore case
     *                   (case insensitive) or not.
     * @return {@code true} if the CharSequence starts with the prefix or
     * both {@code null}
     * @see java.lang.String#startsWith(String)
     */
    private static boolean startsWith(final CharSequence str, final CharSequence prefix, final boolean ignoreCase) {
        if (str == null || prefix == null) {
            return str == null && prefix == null;
        }
        if (prefix.length() > str.length()) {
            return false;
        }
        return regionMatches(str, ignoreCase, 0, prefix, 0, prefix.length());
    }

    /**
     * <p>Check if a CharSequence ends with a specified suffix.</p>
     *
     * <p>{@code null}s are handled without exceptions. Two {@code null}
     * references are considered to be equal. The comparison is case sensitive.</p>
     *
     * <pre>
     * StringUtils.endsWith(null, null)      = true
     * StringUtils.endsWith(null, "def")     = false
     * StringUtils.endsWith("abcdef", null)  = false
     * StringUtils.endsWith("abcdef", "def") = true
     * StringUtils.endsWith("ABCDEF", "def") = false
     * StringUtils.endsWith("ABCDEF", "cde") = false
     * </pre>
     *
     * @param str    the CharSequence to check, may be null
     * @param suffix the suffix to find, may be null
     * @return {@code true} if the CharSequence ends with the suffix, case sensitive, or
     * both {@code null}
     * @see java.lang.String#endsWith(String)
     * @since 2.4
     * @since 3.0 Changed signature from endsWith(String, String) to endsWith(CharSequence, CharSequence)
     */
    public static boolean endsWith(final CharSequence str, final CharSequence suffix) {
        return endsWith(str, suffix, false);
    }


    // endsWith
    //-----------------------------------------------------------------------

    /**
     * <p>Check if a CharSequence ends with a specified suffix (optionally case insensitive).</p>
     *
     * @param str        the CharSequence to check, may be null
     * @param suffix     the suffix to find, may be null
     * @param ignoreCase indicates whether the compare should ignore case
     *                   (case insensitive) or not.
     * @return {@code true} if the CharSequence starts with the prefix or
     * both {@code null}
     * @see java.lang.String#endsWith(String)
     */
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

    public static int scanUntilAndTrim(ByteBuffer buffer, byte split, char[] cacheChars, int charsIndex, boolean trim) {
        buffer.mark();
        int i = 0;
        if (trim) {
            while ((cacheChars[charsIndex] = (char) (buffer.get() & 0xFF)) == Constant.SP) ;
            i = 1;
        }
        while (buffer.hasRemaining()) {
            cacheChars[charsIndex + i] = (char) (buffer.get() & 0xFF);
            if (cacheChars[charsIndex + i] == split) {
                //反向去空格
                while (trim && cacheChars[charsIndex + i - 1] == Constant.SP) {
                    i--;
                }
                return i;
            }
            i++;
        }
        buffer.reset();
        return -1;
    }

    public static int scanUntilAndTrim(ByteBuffer buffer, byte split, char[] cacheChars, boolean trim) {
        return scanUntilAndTrim(buffer, split, cacheChars, 0, trim);
    }

    static class StringCache {
        final char[] bytes;
        final String value;

        public StringCache(char[] bytes, String value) {
            this.bytes = bytes;
            this.value = value;
        }
    }


}
