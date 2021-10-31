package org.noear.solon.boot.smarthttp.http.uploadfile;

import java.util.*;

public class Headers implements Iterable<Header> {

    protected Header[] headers = new Header[12];
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
        Header header = new Header(name, value); // also validates
        // expand array if necessary
        if (count == headers.length) {
            Header[] expanded = new Header[2 * count];
            System.arraycopy(headers, 0, expanded, 0, count);
            headers = expanded;
        }
        headers[count++] = header; // inlining header would cause a bug!
    }

    public void addAll(Headers headers) {
        for (Header header : headers)
            add(header.getName(), header.getValue());
    }

    public Header replace(String name, String value) {
        for (int i = 0; i < count; i++) {
            if (headers[i].getName().equalsIgnoreCase(name)) {
                Header prev = headers[i];
                headers[i] = new Header(name, value);
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

    public Iterator<Header> iterator() {
        // we use the built-in wrapper instead of a trivial custom implementation
        // since even a tiny anonymous class here compiles to a 1.5K class file
        return Arrays.asList(headers).subList(0, count).iterator();
    }
}
