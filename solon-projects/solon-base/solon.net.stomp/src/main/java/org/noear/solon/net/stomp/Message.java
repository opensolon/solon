package org.noear.solon.net.stomp;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;


public class Message {

    private final String command;
    private final List<Header> headers;
    private final String payload;

    public Message(String command, String payload) {
        this(command, null, payload);
    }

    public Message(String command, List<Header> headers) {
        this(command, headers, null);
    }

    public Message(String command, List<Header> headers, String payload) {
        this.command = command;
        this.headers = (headers == null) ? new ArrayList<>() : headers;
        this.payload = payload;
    }

    public Message headers(String key, String val) {
        this.headers.add(new Header(key, val));
        return this;
    }

    public List<Header> getHeaders() {
        return headers;
    }

    public String getHeader(String key) {
        if (headers == null) {
            return null;
        }

        AtomicReference<String> stringAtomicReference = new AtomicReference<>();
        headers.stream().filter(header -> key.equals(header.getKey())).findFirst().ifPresent(header -> {
            stringAtomicReference.set(header.getValue());
        });
        return stringAtomicReference.get();
    }

    public String getPayload() {
        return payload;
    }

    public String getCommand() {
        return command;
    }


    public String headerValue(String key) {
        Header header = header(key);
        if (header != null) {
            return header.getValue();
        }
        return null;
    }

    public Header header(String key) {
        if (headers != null) {
            for (Header header : headers) {
                if (header.getKey().equals(key)) return header;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "Message {command='" + command + "', headers=" + headers + ", payload='" + payload + "'}";
    }

}
