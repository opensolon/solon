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


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;

/**
 * @author 三刀 zhengjunweimail@163.com
 * @version v1.0.0
 */
public class Mimetypes {
    /* The default MIME type */
    public static final String DEFAULT_MIMETYPE = "application/octet-stream";
    private static final String MIME_TYPES_FILE_NAME = "mime.types";

    private static Mimetypes mimetypes = null;

    private final HashMap<String, String> extensionToMimetypeMap = new HashMap<String, String>();

    private Mimetypes() {
    }

    public synchronized static Mimetypes getInstance() {
        if (mimetypes != null)
            return mimetypes;

        mimetypes = new Mimetypes();
        try (InputStream is = Mimetypes.class.getClassLoader().getResourceAsStream(MIME_TYPES_FILE_NAME)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.US_ASCII));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty() || line.charAt(0) == '#') {
                    continue;
                }
                String[] tokens = FeatUtils.splitPreserveAllTokens(line, " \t\n\r\f");
                String mediaType = tokens[0];
                for (int i = 1; i < tokens.length; i++) {
                    String fileExtension = tokens[i].toLowerCase(Locale.ENGLISH);
                    mimetypes.extensionToMimetypeMap.put(fileExtension, mediaType);
                }
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Could not load '" + MIME_TYPES_FILE_NAME + "'", ex);
        }
        return mimetypes;
    }

    public String getMimetype(String fileName) {
        String ext = getFileExtension(fileName);
        return getMimetypeByExtension(ext);
    }

    public String getMimetype(File file) {
        return getMimetype(file.getName());
    }

    public String getMimetypeByExtension(String ext) {
        String type = extensionToMimetypeMap.get(ext);
        if (type != null) {
            return type;
        }
        return DEFAULT_MIMETYPE;
    }

    /**
     * 获取文件后缀
     *
     * @param fileName
     * @return
     */
    public static String getFileExtension(String fileName) {
        int lastPeriodIndex = fileName.lastIndexOf(".");
        if (lastPeriodIndex > 0 && lastPeriodIndex + 1 < fileName.length()) {
            return fileName.substring(lastPeriodIndex + 1).toLowerCase();
        } else {
            return fileName;
        }
    }

}
