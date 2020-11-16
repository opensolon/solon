package org.noear.solon.extend.staticfiles;

import java.util.HashMap;

public class StaticFiles extends HashMap<String,String> {
    private StaticFiles() {
        super();
        put(".txt", "text/plain");

        put(".html", "text/html");
        put(".htm", "text/html");
        put(".css", "text/css");
        put(".js", "application/x-javascript");

        put(".ico", "image/x-icon");

        put(".gif", "image/gif");
        put(".jpg", "image/jpeg");
        put(".png", "image/png");
        put(".svg", "image/svg+xml");
        put(".jpeg", "image/jpeg");

        put(".json", "application/json");

        put(".mp3", "audio/mpeg");
        put(".mp4", "application/octet-stream");
        put(".flv", "application/octet-stream");

        put(".woff", "application/x-font-woff");
        put(".woff2", "application/x-font-woff2");
        put(".ttf", "application/x-font-truetype");
        put(".otf", "application/x-font-opentype");
        put(".eot", "application/vnd.ms-fontobject");

    }

    private static StaticFiles _instance;

    public static StaticFiles instance() {
        if (_instance == null) {
            _instance = new StaticFiles();
        }

        return _instance;
    }
}
