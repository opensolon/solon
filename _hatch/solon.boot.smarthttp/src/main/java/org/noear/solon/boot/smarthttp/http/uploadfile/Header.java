package org.noear.solon.boot.smarthttp.http.uploadfile;

public class Header {
    protected final String name;
    protected final String value;

    public Header(String name, String value) {
        this.name = name.trim();
        this.value = value.trim();
        // RFC2616#14.23 - header can have an empty value (e.g. Host)
        if (this.name.length() == 0) // but name cannot be empty
            throw new IllegalArgumentException("name cannot be empty");
    }

    public String getName() { return name; }


    public String getValue() { return value; }
}
