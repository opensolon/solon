package org.noear.solon.extend.staticfiles;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 静态文件类型申明
 *
 * @author noear
 * @since 1.0
 * @since 1.6
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

    /**
     * 添加 Mime 记录
     */
    public synchronized static String add(String extension, String conentType) {
        return mimeMap.put(extension, conentType);
    }

    /**
     * 查找 Mime 记录（找到对应的内容类型）
     */
    public synchronized static String findByExt(String ext) {
        return mimeMap.get(ext);
    }

    /**
     * 查找 Mime 记录（找到对应的内容类型）
     */
    public synchronized static String findByFileName(String fileName) {
        String ext = resolveExt(fileName);

        return findByExt(ext);
    }

    /**
     * 获取所有 Mime 记录表
     */
    public static Map<String, String> getMap() {
        return Collections.unmodifiableMap(mimeMap);
    }

    /**
     * 分析扩展名
     */
    public static String resolveExt(String fileName) {
        String ext = "";
        int pos = fileName.lastIndexOf(35);
        if (pos > 0) {
            fileName = fileName.substring(0, pos - 1);
        }

        pos = fileName.lastIndexOf(46);
        pos = Math.max(pos, fileName.lastIndexOf(47));
        pos = Math.max(pos, fileName.lastIndexOf(63));
        if (pos != -1 && fileName.charAt(pos) == '.') {
            ext = fileName.substring(pos).toLowerCase();
        }

        return ext;
    }
}
