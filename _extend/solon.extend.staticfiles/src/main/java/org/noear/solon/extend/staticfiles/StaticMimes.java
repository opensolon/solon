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

        mimeMap.put(".abs", "audio/x-mpeg");
        mimeMap.put(".ai", "application/postscript");
        mimeMap.put(".aif", "audio/x-aiff");
        mimeMap.put(".aifc", "audio/x-aiff");
        mimeMap.put(".aiff", "audio/x-aiff");
        mimeMap.put(".aim", "application/x-aim");
        mimeMap.put(".art", "image/x-jg");
        mimeMap.put(".asf", "video/x-ms-asf");
        mimeMap.put(".asx", "video/x-ms-asf");
        mimeMap.put(".au", "audio/basic");
        mimeMap.put(".avi", "video/x-msvideo");
        mimeMap.put(".avx", "video/x-rad-screenplay");
        mimeMap.put(".bcpio", "application/x-bcpio");
        mimeMap.put(".bin", "application/octet-stream");
        mimeMap.put(".bmp", "image/bmp");
        mimeMap.put(".body", "text/html");
        mimeMap.put(".cdf", "application/x-cdf");
        mimeMap.put(".cer", "application/pkix-cert");
        mimeMap.put(".class", "application/java");
        mimeMap.put(".cpio", "application/x-cpio");
        mimeMap.put(".csh", "application/x-csh");
        mimeMap.put(".css", "text/css");
        mimeMap.put(".dib", "image/bmp");
        mimeMap.put(".doc", "application/msword");
        mimeMap.put(".dtd", "application/xml-dtd");
        mimeMap.put(".dv", "video/x-dv");
        mimeMap.put(".dvi", "application/x-dvi");
        mimeMap.put(".eot", "application/vnd.ms-fontobject");
        mimeMap.put(".eps", "application/postscript");
        mimeMap.put(".etx", "text/x-setext");
        mimeMap.put(".exe", "application/octet-stream");
        mimeMap.put(".gif", "image/gif");
        mimeMap.put(".gtar", "application/x-gtar");
        mimeMap.put(".gz", "application/x-gzip");
        mimeMap.put(".hdf", "application/x-hdf");
        mimeMap.put(".hqx", "application/mac-binhex40");
        mimeMap.put(".htc", "text/x-component");
        mimeMap.put(".htm", "text/html");
        mimeMap.put(".html", "text/html");
        mimeMap.put(".ief", "image/ief");
        mimeMap.put(".jad", "text/vnd.sun.j2me.app-descriptor");
        mimeMap.put(".jar", "application/java-archive");
        mimeMap.put(".java", "text/x-java-source");
        mimeMap.put(".jnlp", "application/x-java-jnlp-file");
        mimeMap.put(".jpe", "image/jpeg");
        mimeMap.put(".jpeg", "image/jpeg");
        mimeMap.put(".jpg", "image/jpeg");
        mimeMap.put(".js", "application/javascript");
        mimeMap.put(".jsf", "text/plain");
        mimeMap.put(".json", "application/json");
        mimeMap.put(".jspf", "text/plain");
        mimeMap.put(".kar", "audio/midi");
        mimeMap.put(".latex", "application/x-latex");
        mimeMap.put(".m3u", "audio/x-mpegurl");
        mimeMap.put(".mac", "image/x-macpaint");
        mimeMap.put(".man", "text/troff");
        mimeMap.put(".mathml", "application/mathml+xml");
        mimeMap.put(".me", "text/troff");
        mimeMap.put(".mid", "audio/midi");
        mimeMap.put(".midi", "audio/midi");
        mimeMap.put(".mif", "application/x-mif");
        mimeMap.put(".mov", "video/quicktime");
        mimeMap.put(".movie", "video/x-sgi-movie");
        mimeMap.put(".mp1", "audio/mpeg");
        mimeMap.put(".mp2", "audio/mpeg");
        mimeMap.put(".mp3", "audio/mpeg");
        mimeMap.put(".mp4", "video/mp4");
        mimeMap.put(".mpa", "audio/mpeg");
        mimeMap.put(".mpe", "video/mpeg");
        mimeMap.put(".mpeg", "video/mpeg");
        mimeMap.put(".mpega", "audio/x-mpeg");
        mimeMap.put(".mpg", "video/mpeg");
        mimeMap.put(".mpv2", "video/mpeg2");
        mimeMap.put(".ms", "application/x-wais-source");
        mimeMap.put(".nc", "application/x-netcdf");
        mimeMap.put(".oda", "application/oda");
        mimeMap.put(".odb", "application/vnd.oasis.opendocument.database");
        mimeMap.put(".odc", "application/vnd.oasis.opendocument.chart");
        mimeMap.put(".odf", "application/vnd.oasis.opendocument.formula");
        mimeMap.put(".odg", "application/vnd.oasis.opendocument.graphics");
        mimeMap.put(".odi", "application/vnd.oasis.opendocument.image");
        mimeMap.put(".odm", "application/vnd.oasis.opendocument.text-master");
        mimeMap.put(".odp", "application/vnd.oasis.opendocument.presentation");
        mimeMap.put(".ods", "application/vnd.oasis.opendocument.spreadsheet");
        mimeMap.put(".odt", "application/vnd.oasis.opendocument.text");
        mimeMap.put(".otg", "application/vnd.oasis.opendocument.graphics-template");
        mimeMap.put(".oth", "application/vnd.oasis.opendocument.text-web");
        mimeMap.put(".otp", "application/vnd.oasis.opendocument.presentation-template");
        mimeMap.put(".ots", "application/vnd.oasis.opendocument.spreadsheet-template ");
        mimeMap.put(".ott", "application/vnd.oasis.opendocument.text-template");
        mimeMap.put(".ogx", "application/ogg");
        mimeMap.put(".ogv", "video/ogg");
        mimeMap.put(".oga", "audio/ogg");
        mimeMap.put(".ogg", "audio/ogg");
        mimeMap.put(".otf", "application/x-font-opentype");
        mimeMap.put(".spx", "audio/ogg");
        mimeMap.put(".flac", "audio/flac");
        mimeMap.put(".anx", "application/annodex");
        mimeMap.put(".axa", "audio/annodex");
        mimeMap.put(".axv", "video/annodex");
        mimeMap.put(".xspf", "application/xspf+xml");
        mimeMap.put(".pbm", "image/x-portable-bitmap");
        mimeMap.put(".pct", "image/pict");
        mimeMap.put(".pdf", "application/pdf");
        mimeMap.put(".pgm", "image/x-portable-graymap");
        mimeMap.put(".pic", "image/pict");
        mimeMap.put(".pict", "image/pict");
        mimeMap.put(".pls", "audio/x-scpls");
        mimeMap.put(".png", "image/png");
        mimeMap.put(".pnm", "image/x-portable-anymap");
        mimeMap.put(".pnt", "image/x-macpaint");
        mimeMap.put(".ppm", "image/x-portable-pixmap");
        mimeMap.put(".ppt", "application/vnd.ms-powerpoint");
        mimeMap.put(".pps", "application/vnd.ms-powerpoint");
        mimeMap.put(".ps", "application/postscript");
        mimeMap.put(".psd", "image/vnd.adobe.photoshop");
        mimeMap.put(".qt", "video/quicktime");
        mimeMap.put(".qti", "image/x-quicktime");
        mimeMap.put(".qtif", "image/x-quicktime");
        mimeMap.put(".ras", "image/x-cmu-raster");
        mimeMap.put(".rdf", "application/rdf+xml");
        mimeMap.put(".rgb", "image/x-rgb");
        mimeMap.put(".rm", "application/vnd.rn-realmedia");
        mimeMap.put(".roff", "text/troff");
        mimeMap.put(".rtf", "application/rtf");
        mimeMap.put(".rtx", "text/richtext");
        mimeMap.put(".sfnt", "application/font-sfnt");
        mimeMap.put(".sh", "application/x-sh");
        mimeMap.put(".shar", "application/x-shar");
        mimeMap.put(".sit", "application/x-stuffit");
        mimeMap.put(".snd", "audio/basic");
        mimeMap.put(".src", "application/x-wais-source");
        mimeMap.put(".sv4cpio", "application/x-sv4cpio");
        mimeMap.put(".sv4crc", "application/x-sv4crc");
        mimeMap.put(".svg", "image/svg+xml");
        mimeMap.put(".svgz", "image/svg+xml");
        mimeMap.put(".swf", "application/x-shockwave-flash");
        mimeMap.put(".t", "text/troff");
        mimeMap.put(".tar", "application/x-tar");
        mimeMap.put(".tcl", "application/x-tcl");
        mimeMap.put(".tex", "application/x-tex");
        mimeMap.put(".texi", "application/x-texinfo");
        mimeMap.put(".texinfo", "application/x-texinfo");
        mimeMap.put(".tif", "image/tiff");
        mimeMap.put(".tiff", "image/tiff");
        mimeMap.put(".tr", "text/troff");
        mimeMap.put(".tsv", "text/tab-separated-values");
        mimeMap.put(".ttf", "application/x-font-ttf");
        mimeMap.put(".txt", "text/plain");
        mimeMap.put(".ulw", "audio/basic");
        mimeMap.put(".ustar", "application/x-ustar");
        mimeMap.put(".vxml", "application/voicexml+xml");
        mimeMap.put(".xbm", "image/x-xbitmap");
        mimeMap.put(".xht", "application/xhtml+xml");
        mimeMap.put(".xhtml", "application/xhtml+xml");
        mimeMap.put(".xls", "application/vnd.ms-excel");
        mimeMap.put(".xml", "application/xml");
        mimeMap.put(".xpm", "image/x-xpixmap");
        mimeMap.put(".xsl", "application/xml");
        mimeMap.put(".xslt", "application/xslt+xml");
        mimeMap.put(".xul", "application/vnd.mozilla.xul+xml");
        mimeMap.put(".xwd", "image/x-xwindowdump");
        mimeMap.put(".vsd", "application/vnd.visio");
        mimeMap.put(".wav", "audio/x-wav");
        mimeMap.put(".wbmp", "image/vnd.wap.wbmp");
        mimeMap.put(".wml", "text/vnd.wap.wml");
        mimeMap.put(".wmlc", "application/vnd.wap.wmlc");
        mimeMap.put(".wmls", "text/vnd.wap.wmlsc");
        mimeMap.put(".wmlscriptc", "application/vnd.wap.wmlscriptc");
        mimeMap.put(".wmv", "video/x-ms-wmv");
        mimeMap.put(".woff", "application/font-woff");
        mimeMap.put(".woff2", "application/font-woff2");
        mimeMap.put(".wrl", "model/vrml");
        mimeMap.put(".wspolicy", "application/wspolicy+xml");
        mimeMap.put(".z", "application/x-compress");
        mimeMap.put(".zip", "application/zip");
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
