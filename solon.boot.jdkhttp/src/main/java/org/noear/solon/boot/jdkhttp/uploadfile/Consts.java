package org.noear.solon.boot.jdkhttp.uploadfile;

import java.nio.charset.Charset;

public interface Consts {
    int maxPostSize = 2097152;
    String SCHEMA_HTTP = "http";
    String SCHEMA_HTTPS = "https";
    byte SP = 32;
    byte HT = 9;
    byte CR = 13;
    byte EQUALS = 61;
    byte LF = 10;
    byte COLON = 58;
    byte SEMICOLON = 59;
    byte COMMA = 44;
    byte DOUBLE_QUOTE = 34;
    Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    char SP_CHAR = ' ';
    byte[] CRLF = new byte[]{13, 10};
    byte[] COLON_ARRAY = new byte[]{58};
    byte[] SP_ARRAY = new byte[]{32};
}
