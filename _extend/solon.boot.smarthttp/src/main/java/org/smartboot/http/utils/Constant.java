/*******************************************************************************
 * Copyright (c) 2017-2020, org.smartboot. All rights reserved.
 * project name: smart-http
 * file name: Consts.java
 * Date: 2020-01-01
 * Author: sandao (zhengjunweimail@163.com)
 ******************************************************************************/

package org.smartboot.http.utils;

public interface Constant {

    public static final int WS_DEFAULT_MAX_FRAME_SIZE = 16384;
    public static final int WS_PLAY_LOAD_126 = 126;
    public static final int WS_PLAY_LOAD_127 = 127;

    /**
     * Post 最大长度
     */
    int maxPostSize = 2 * 1024 * 1024;

    String SCHEMA_HTTP = "http";
    String SCHEMA_HTTPS = "https";
    /**
     * Horizontal space
     */
    public static final byte SP = 32;

    /**
     * Horizontal tab
     */
    public static final byte HT = 9;

    /**
     * Carriage return
     */
    public static final byte CR = 13;

    /**
     * Equals '='
     */
    public static final byte EQUALS = 61;

    /**
     * Line feed character
     */
    public static final byte LF = 10;

    /**
     * Colon ':'
     */
    public static final byte COLON = 58;

    /**
     * Semicolon ';'
     */
    public static final byte SEMICOLON = 59;

    /**
     * Comma ','
     */
    public static final byte COMMA = 44;

    /**
     * Double quote '"'
     */
    public static final byte DOUBLE_QUOTE = '"';


    /**
     * Horizontal space
     */
    public static final char SP_CHAR = (char) SP;

    public static final byte[] CRLF = {Constant.CR, Constant.LF};

    byte[] COLON_ARRAY = {COLON};

    byte[] SP_ARRAY = {SP};

}