package org.noear.solon.boot.web;

import org.noear.solon.Utils;

/**
 * @author noear
 * @since 1.11
 */
public class HeaderUtils {
    public static String extractQuotedValueFromHeader(String header, String key) {
        if(Utils.isEmpty(header)){
            return null;
        }

        int keypos = 0;
        int pos = -1;
        boolean whiteSpace = true;
        boolean inQuotes = false;

        int i;
        int start;
        for(i = 0; i < header.length() - 1; ++i) {
            start = header.charAt(i);
            if (inQuotes) {
                if (start == 34) {
                    inQuotes = false;
                }
            } else {
                if (key.charAt(keypos) == start && (whiteSpace || keypos > 0)) {
                    ++keypos;
                    whiteSpace = false;
                } else if (start == 34) {
                    keypos = 0;
                    inQuotes = true;
                    whiteSpace = false;
                } else {
                    keypos = 0;
                    whiteSpace = start == 32 || start == 59 || start == 9;
                }

                if (keypos == key.length()) {
                    if (header.charAt(i + 1) == '=') {
                        pos = i + 2;
                        break;
                    }

                    keypos = 0;
                }
            }
        }

        if (pos == -1) {
            return null;
        } else {
            char c;
            if (header.charAt(pos) == '"') {
                start = pos + 1;

                for(i = start; i < header.length(); ++i) {
                    c = header.charAt(i);
                    if (c == '"') {
                        break;
                    }
                }

                return header.substring(start, i);
            } else {
                for(i = pos; i < header.length(); ++i) {
                    c = header.charAt(i);
                    if (c == ' ' || c == '\t' || c == ';') {
                        break;
                    }
                }

                return header.substring(pos, i);
            }
        }
    }
}
