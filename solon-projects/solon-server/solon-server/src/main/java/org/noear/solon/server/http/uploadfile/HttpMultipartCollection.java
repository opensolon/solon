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

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

public class HttpMultipartCollection implements Iterator<HttpMultipart> {

    protected final HttpMultipartInputStream in;
    protected boolean next;


    public HttpMultipartCollection(String contextType, InputStream bodyStream) throws IOException {
        Map<String, String> ct = Helper.getHeaderParams(contextType);
        if (!ct.containsKey("multipart/form-data"))
            throw new IllegalArgumentException("Content-Type is not multipart/form-data");

        String boundary = ct.get("boundary");
        if (boundary == null)
            throw new IllegalArgumentException("Content-Type is missing boundary");
        in = new HttpMultipartInputStream(bodyStream, Helper.getBytes(boundary));
    }

    public boolean hasNext() {
        try {
            return next || (next = in.nextPart());
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    public HttpMultipart next() {
        if (!hasNext())
            throw new NoSuchElementException();
        next = false;
        HttpMultipart p = new HttpMultipart();
        try {
            p.headers = Helper.readHeaders(in);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        Map<String, String> cd = p.headers.getParams("Content-Disposition");
        p.name = cd.get("name");
        p.filename = cd.get("filename");
        p.body = in;
        return p;
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
}