package org.noear.solon.boot.jdkhttp.uploadfile;


import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

public class HttpMultipartCollection implements Iterator<HttpMultipart> {

    protected final HttpMultipartInputStream in;
    protected boolean next;

    public HttpMultipartCollection(HttpExchange req) throws IOException {
        Map<String, String> ct = Utils.getHeaderParams(req.getRequestHeaders().getFirst("Content-Type"));
        if (!ct.containsKey("multipart/form-data"))
            throw new IllegalArgumentException("Content-Type is not multipart/form-data");

        String boundary = ct.get("boundary"); // should be US-ASCII
        if (boundary == null)
            throw new IllegalArgumentException("Content-Type is missing boundary");
        in = new HttpMultipartInputStream(req.getRequestBody(), Utils.getBytes(boundary));
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
            p.headers = Utils.readHeaders(in);
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