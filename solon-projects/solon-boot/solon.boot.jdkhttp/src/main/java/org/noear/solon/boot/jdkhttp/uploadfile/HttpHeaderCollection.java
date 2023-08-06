package org.noear.solon.boot.jdkhttp.uploadfile;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpHeaderCollection implements Iterable<HttpHeader> {

    protected HttpHeader[] headers = new HttpHeader[12];
    protected int count;

    public int size() {
        return count;
    }

    public String get(String name) {
        for (int i = 0; i < count; i++)
            if (headers[i].getName().equalsIgnoreCase(name))
                return headers[i].getValue();
        return null;
    }


    public boolean contains(String name) {
        return get(name) != null;
    }

    public void add(String name, String value) {
        HttpHeader header = new HttpHeader(name, value); // also validates

        // expand array if necessary
        if (count == headers.length) {
            HttpHeader[] expanded = new HttpHeader[2 * count];
            System.arraycopy(headers, 0, expanded, 0, count);
            headers = expanded;
        }
        headers[count++] = header; // inlining header would cause a bug!
    }

    public void addAll(HttpHeaderCollection headers) {
        for (HttpHeader header : headers) {
            add(header.getName(), header.getValue());
        }
    }

    public HttpHeader replace(String name, String value) {
        for (int i = 0; i < count; i++) {
            if (headers[i].getName().equalsIgnoreCase(name)) {
                HttpHeader prev = headers[i];
                headers[i] = new HttpHeader(name, value);
                return prev;
            }
        }
        add(name, value);
        return null;
    }

    public void remove(String name) {
        int j = 0;
        for (int i = 0; i < count; i++)
            if (!headers[i].getName().equalsIgnoreCase(name))
                headers[j++] = headers[i];
        while (count > j)
            headers[--count] = null;
    }

    public Map<String, String> getParams(String name) {
        Map<String, String> params = new LinkedHashMap<String, String>();
        for (String param : Utils.split(get(name), ";", -1)) {
            String[] pair = Utils.split(param, "=", 2);
            String val = pair.length == 1 ? "" : Utils.trimLeft(Utils.trimRight(pair[1], '"'), '"');
            params.put(pair[0], val);
        }
        return params;
    }

    public Iterator<HttpHeader> iterator() {
        return Arrays.asList(headers).subList(0, count).iterator();
    }
}
