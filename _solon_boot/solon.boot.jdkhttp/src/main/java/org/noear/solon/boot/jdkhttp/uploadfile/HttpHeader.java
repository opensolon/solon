package org.noear.solon.boot.jdkhttp.uploadfile;

public class HttpHeader {
    private final String name;
    private final String value;

    public HttpHeader(String name, String value) {
        this.name = name.trim();
        this.value = value.trim();
        // RFC2616#14.23 - header can have an empty value
        if (this.name.length() == 0) { // name cannot be empty
            throw new IllegalArgumentException("name cannot be empty");
        }
    }

    public String getName() {
        return name;
    }


    public String getValue() {
        return value;
    }
}
