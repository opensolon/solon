/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.server.http.uploadfile;

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
        HttpHeader header = new HttpHeader(name, value);

        if (count == headers.length) {
            HttpHeader[] expanded = new HttpHeader[2 * count];
            System.arraycopy(headers, 0, expanded, 0, count);
            headers = expanded;
        }
        headers[count++] = header;
    }

    public void addAll(HttpHeaderCollection headers) {
        for (HttpHeader header : headers)
            add(header.getName(), header.getValue());
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
        for (String param : Helper.split(get(name), ";", -1)) {
            String[] pair = Helper.split(param, "=", 2);
            String val = pair.length == 1 ? "" : Helper.trimLeft(Helper.trimRight(pair[1], '"'), '"');
            params.put(pair[0], val);
        }
        return params;
    }

    public Iterator<HttpHeader> iterator() {
        return Arrays.asList(headers).subList(0, count).iterator();
    }
}
