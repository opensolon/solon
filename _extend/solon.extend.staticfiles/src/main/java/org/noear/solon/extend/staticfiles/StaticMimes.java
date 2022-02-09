package org.noear.solon.extend.staticfiles;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 静态文件类型申明
 *
 * @author noear
 * @since 1.0
 * */
public class StaticMimes {
    static final Map<String, String> mimeMap = new HashMap<>();

    static {
        mimeMap.put(".txt", "text/plain");

        mimeMap.put(".html", "text/html");
        mimeMap.put(".htm", "text/html");
        mimeMap.put(".xml", "text/xml");
        mimeMap.put(".css", "text/css");
        mimeMap.put(".js", "application/x-javascript");

        mimeMap.put(".ico", "image/x-icon");

        mimeMap.put(".gif", "image/gif");
        mimeMap.put(".jpg", "image/jpeg");
        mimeMap.put(".png", "image/png");
        mimeMap.put(".svg", "image/svg+xml");
        mimeMap.put(".jpeg", "image/jpeg");

        mimeMap.put(".json", "application/json");

        mimeMap.put(".mp3", "audio/mpeg");
        mimeMap.put(".mp4", "application/octet-stream");
        mimeMap.put(".flv", "application/octet-stream");

        mimeMap.put(".woff", "application/x-font-woff");
        mimeMap.put(".woff2", "application/x-font-woff2");
        mimeMap.put(".ttf", "application/x-font-truetype");
        mimeMap.put(".otf", "application/x-font-opentype");
        mimeMap.put(".eot", "application/vnd.ms-fontobject");

    }

    public synchronized static String put(String extension, String conentType) {
        return mimeMap.put(extension, conentType);
    }

    public synchronized static String putIfAbsent(String extension, String conentType) {
        return mimeMap.putIfAbsent(extension, conentType);
    }

    public synchronized static String get(String extension) {
        return mimeMap.get(extension);
    }

    public static Map<String, String> getMap() {
        return Collections.unmodifiableMap(mimeMap);
    }
}
