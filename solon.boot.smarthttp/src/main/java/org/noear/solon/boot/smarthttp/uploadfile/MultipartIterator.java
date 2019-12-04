package org.noear.solon.boot.smarthttp.uploadfile;

import org.smartboot.http.HttpRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

public class MultipartIterator implements Iterator<MultipartIterator.Part> {

    /**
     * The {@code Part} class encapsulates a single part of the multipart.
     */
    public static class Part {

        public String name;
        public String filename;
        public Headers headers;
        public InputStream body;

        /**
         * Returns the part's name (form field name).
         *
         * @return the part's name
         */
        public String getName() { return name; }

        /**
         * Returns the part's filename (original filename entered in file form field).
         *
         * @return the part's filename, or null if there is none
         */
        public String getFilename() { return filename; }

        /**
         * Returns the part's headers.
         *
         * @return the part's headers
         */
        public Headers getHeaders() { return headers; }

        /**
         * Returns the part's body (form field value).
         *
         * @return the part's body
         */
        public InputStream getBody() { return body; }

        /***
         * Returns the part's body as a string. If the part
         * headers do not specify a charset, UTF-8 is used.
         *
         * @return the part's body as a string
         * @throws IOException if an IO error occurs
         */
        public String getString() throws IOException {
            String charset = headers.getParams("Content-Type").get("charset");
            return Utils.readToken(body, -1, charset == null ? "UTF-8" : charset, 8192);
        }
    }

    protected final MultipartInputStream in;
    protected boolean next;

    /**
     * Creates a new MultipartIterator from the given request.
     *
     * @param req the multipart/form-data request
     * @throws IOException if an IO error occurs
     * @throws IllegalArgumentException if the given request's content type
     *         is not multipart/form-data, or is missing the boundary
     */
    public MultipartIterator(HttpRequest req) throws IOException {
        Map<String, String> ct = Utils.getHeaderParams(req.getHeader("Content-Type"));
        if (!ct.containsKey("multipart/form-data"))
            throw new IllegalArgumentException("Content-Type is not multipart/form-data");

        String boundary = ct.get("boundary"); // should be US-ASCII
        if (boundary == null)
            throw new IllegalArgumentException("Content-Type is missing boundary");
        in = new MultipartInputStream(req.getInputStream(), Utils.getBytes(boundary));
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