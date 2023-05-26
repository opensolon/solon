package cn.afterturn.easypoi.wps.entity.resreq;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * @author noear
 * @since 1.10
 */
public class ResponseEntity<T> {
    T body;
    Map<String, Object> headers;
    int status;

    public T getBody() {
        return body;
    }

    public Map<String, Object> getHeaders() {
        return headers;
    }

    public int getStatus() {
        return status;
    }

    public ResponseEntity(T body, Map<String, Object> headers) {
        this.body = body;
        Map<String, Object> tempHeaders = new LinkedHashMap<>();
        if (headers != null) {
            tempHeaders.putAll(headers);
        }

        this.headers = Collections.unmodifiableMap(tempHeaders);
    }
    public ResponseEntity(T body, Map<String, Object> headers, int status) {
        this(body, headers);
        this.status = status;
    }
}
