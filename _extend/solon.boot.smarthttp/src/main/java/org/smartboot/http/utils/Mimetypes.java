/*******************************************************************************
 * Copyright (c) 2017-2020, org.smartboot. All rights reserved.
 * project name: smart-http
 * file name: Mimetypes.java
 * Date: 2020-01-01
 * Author: sandao (zhengjunweimail@163.com)
 ******************************************************************************/

package org.smartboot.http.utils;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;

public class Mimetypes {
    /* The default MIME type */
    public static final String DEFAULT_MIMETYPE = "application/octet-stream";
    private static final String MIME_TYPES_FILE_NAME = "mime.types";

    private static Mimetypes mimetypes = null;

    private HashMap<String, String> extensionToMimetypeMap = new HashMap<String, String>();

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
                String[] tokens = StringUtils.splitPreserveAllTokens(line, " \t\n\r\f");
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
        int lastPeriodIndex = fileName.lastIndexOf(".");
        if (lastPeriodIndex > 0 && lastPeriodIndex + 1 < fileName.length()) {
            String ext = fileName.substring(lastPeriodIndex + 1).toLowerCase();
            if (extensionToMimetypeMap.keySet().contains(ext)) {
                String mimetype = (String) extensionToMimetypeMap.get(ext);
                return mimetype;
            }
        }
        return DEFAULT_MIMETYPE;
    }

    public String getMimetype(File file) {
        return getMimetype(file.getName());
    }

}
