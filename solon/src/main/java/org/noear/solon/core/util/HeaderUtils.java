package org.noear.solon.core.util;

import org.noear.solon.Utils;
import org.noear.solon.core.NvMap;

import java.util.Map;

public class HeaderUtils {
    public static String encodeHeaderMap(Map<String, String> headers) {
        StringBuilder header = new StringBuilder();
        if (headers != null) {
            headers.forEach((k, v) -> {
                header.append(k).append("=").append(v).append("&");
            });
        }

        return header.toString();
    }

    public static Map<String, String> decodeHeaderMap(String header) {
        NvMap headerMap = new NvMap();

        if (Utils.isNotEmpty(header)) {
            String[] ss = header.split("&");
            for (String s : ss) {
                String[] kv = s.split("=");

                headerMap.put(kv[0], kv[1]);
            }
        }

        return headerMap;
    }
}
