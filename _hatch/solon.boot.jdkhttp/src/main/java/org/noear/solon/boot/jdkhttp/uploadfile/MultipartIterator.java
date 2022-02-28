package org.noear.solon.boot.jdkhttp.uploadfile;


import com.sun.net.httpserver.HttpExchange;
import org.noear.solon.boot.ServerProps;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

public class MultipartIterator implements Iterator<MultipartIterator.Part> {

    public static class Part {

        public String name;
        public String filename;
        public Headers headers;
        public InputStream body;

        public String getName() { return name; }

        public String getFilename() { return filename; }

        public Headers getHeaders() { return headers; }

        public InputStream getBody() { return body; }

        public String getString() throws IOException {
            String charset = headers.getParams("Content-Type").get("charset");
            return Utils.readToken(body, -1, charset == null ? ServerProps.encoding_request : charset, 8192);
        }
    }

    protected final MultipartInputStream in;
    protected boolean next;


    public MultipartIterator(HttpExchange req) throws IOException {
        Map<String, String> ct = Utils.getHeaderParams(req.getRequestHeaders().getFirst("Content-Type"));
        if (!ct.containsKey("multipart/form-data"))
            throw new IllegalArgumentException("Content-Type is not multipart/form-data");

        String boundary = ct.get("boundary"); // should be US-ASCII
        if (boundary == null)
            throw new IllegalArgumentException("Content-Type is missing boundary");
        in = new MultipartInputStream(req.getRequestBody(), Utils.getBytes(boundary));
    }

    public boolean hasNext() {
        try {
            return next || (next = in.nextPart());
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    public Part next() {
        if (!hasNext())
            throw new NoSuchElementException();
        next = false;
        Part p = new Part();
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