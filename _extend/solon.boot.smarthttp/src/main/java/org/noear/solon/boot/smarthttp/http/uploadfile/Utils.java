package org.noear.solon.boot.smarthttp.http.uploadfile;

import org.noear.solon.boot.ServerProps;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class Utils {
    public static String trimRight(String s, char c) {
        int len = s.length() - 1;
        int end;
        for (end = len; end >= 0 && s.charAt(end) == c; end--);
        return end == len ? s : s.substring(0, end + 1);
    }

    public static String trimLeft(String s, char c) {
        int len = s.length();
        int start;
        for (start = 0; start < len && s.charAt(start) == c; start++);
        return start == 0 ? s : s.substring(start);
    }

    public static String[] split(String str, String delimiters, int limit) {
        if (str == null)
            return new String[0];
        Collection<String> elements = new ArrayList<String>();
        int len = str.length();
        int start = 0;
        int end;
        while (start < len) {
            for (end = --limit == 0 ? len : start;
                 end < len && delimiters.indexOf(str.charAt(end)) < 0; end++);
            String element = str.substring(start, end).trim();
            if (element.length() > 0)
                elements.add(element);
            start = end + 1;
        }
        return elements.toArray(new String[elements.size()]);
    }

    public static String readToken(InputStream in, int delim,
                                   String enc, int maxLength) throws IOException {
        // note: we avoid using a ByteArrayOutputStream here because it
        // suffers the overhead of synchronization for each byte written
        int buflen = maxLength < 512 ? maxLength : 512; // start with less
        byte[] buf = new byte[buflen];
        int count = 0;
        int c;
        while ((c = in.read()) != -1 && c != delim) {
            if (count == buflen) { // expand buffer
                if (count == maxLength)
                    throw new IOException("token too large (" + count + ")");
                buflen = maxLength < 2 * buflen ? maxLength : 2 * buflen;
                byte[] expanded = new byte[buflen];
                System.arraycopy(buf, 0, expanded, 0, count);
                buf = expanded;
            }
            buf[count++] = (byte)c;
        }
        if (c < 0 && delim != -1)
            throw new EOFException("unexpected end of stream");
        return new String(buf, 0, count, enc);
    }


    public static String readLine(InputStream in) throws IOException {
        String s = readToken(in, '\n', ServerProps.encoding_request, 8192);//ISO8859_1//xyj,20181220
        return s.length() > 0 && s.charAt(s.length() - 1) == '\r'
                ? s.substring(0, s.length() - 1) : s;
    }

    public static byte[] getBytes(String... strings) {
        int n = 0;
        for (String s : strings)
            n += s.length();
        byte[] dest = new byte[n];
        n = 0;
        for (String s : strings)
            for (int i = 0, len = s.length(); i < len; i++)
                dest[n++] = (byte)s.charAt(i);
        return dest;
    }

    public static Headers readHeaders(InputStream in) throws IOException {
        Headers headers = new Headers();
        String line;
        String prevLine = "";
        int count = 0;
        while ((line = readLine(in)).length() > 0) {
            int first;
            for (first = 0; first < line.length() &&
                    Character.isWhitespace(line.charAt(first)); first++);
            if (first > 0) // unfold header continuation line
                line = prevLine + ' ' + line.substring(first);
            int separator = line.indexOf(':');
            if (separator < 0)
                throw new IOException("invalid header: \"" + line + "\"");
            String name = line.substring(0, separator);
            String value = line.substring(separator + 1).trim(); // ignore LWS
            Header replaced = headers.replace(name, value);
            // concatenate repeated headers (distinguishing repeated from folded)
            if (replaced != null && first == 0) {
                value = replaced.getValue() + ", " + value;
                line = name + ": " + value;
                headers.replace(name, value);
            }
            prevLine = line;
            if (++count > 100)
                throw new IOException("too many header lines");
        }
        return headers;
    }

    public static Map<String, String> getHeaderParams(String headerVal) {
        Map<String, String> params = new LinkedHashMap<String, String>();
        for (String param : Utils.split(headerVal, ";", -1)) {
            String[] pair = Utils.split(param, "=", 2);
            String val = pair.length == 1 ? "" : Utils.trimLeft(Utils.trimRight(pair[1], '"'), '"');
            params.put(pair[0], val);
        }
        return params;
    }
}
