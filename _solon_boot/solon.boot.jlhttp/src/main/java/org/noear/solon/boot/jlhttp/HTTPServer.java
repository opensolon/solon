/*
 *  Copyright © 2005-2021 Amichai Rothman
 *
 *  This file is part of JLHTTP - the Java Lightweight HTTP Server.
 *
 *  JLHTTP is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  JLHTTP is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with JLHTTP.  If not, see <http://www.gnu.org/licenses/>.
 *
 *  For additional info see http://www.freeutils.net/source/jlhttp/
 */

package org.noear.solon.boot.jlhttp;

import java.io.*;
import java.lang.annotation.*;
import java.lang.reflect.*;
import java.net.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPOutputStream;
import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

/**
 * The {@code HTTPServer} class implements a light-weight HTTP server.
 * <p>
 * This server implements all functionality required by RFC 2616 ("Hypertext
 * Transfer Protocol -- HTTP/1.1"), as well as some of the optional
 * functionality (this is termed "conditionally compliant" in the RFC).
 * In fact, a couple of bugs in the RFC itself were discovered
 * (and fixed) during the development of this server.
 * <p>
 * <b>Feature Overview</b>
 * <ul>
 * <li>RFC compliant - correctness is not sacrificed for the sake of size</li>
 * <li>Virtual hosts - multiple domains and subdomains per server</li>
 * <li>File serving - built-in handler to serve files and folders from disk</li>
 * <li>Mime type mappings - configurable via API or a standard mime.types file</li>
 * <li>Directory index generation - enables browsing folder contents</li>
 * <li>Welcome files - configurable default filename (e.g. index.html)</li>
 * <li>All HTTP methods supported - GET/HEAD/OPTIONS/TRACE/POST/PUT/DELETE/custom</li>
 * <li>Conditional statuses - ETags and If-* header support</li>
 * <li>Chunked transfer encoding - for serving dynamically-generated data streams</li>
 * <li>Gzip/deflate compression - reduces bandwidth and download time</li>
 * <li>HTTPS - secures all server communications</li>
 * <li>Partial content - download continuation (a.k.a. byte range serving)</li>
 * <li>File upload - multipart/form-data handling as stream or iterator</li>
 * <li>Multiple context handlers - a different handler method per URL path</li>
 * <li>@Context annotations - auto-detection of context handler methods</li>
 * <li>Parameter parsing - from query string or x-www-form-urlencoded body</li>
 * <li>A single source file - super-easy to integrate into any application</li>
 * <li>Standalone - no dependencies other than the Java runtime</li>
 * <li>Small footprint - standard jar is ~50K, stripped jar is ~35K</li>
 * <li>Extensible design - easy to override, add or remove functionality</li>
 * <li>Reusable utility methods to simplify your custom code</li>
 * <li>Extensive documentation of API and implementation (&gt;40% of source lines)</li>
 * </ul>
 * <p>
 * <b>Use Cases</b>
 * <p>
 * Being a lightweight, standalone, easily embeddable and tiny-footprint
 * server, it is well-suited for
 * <ul>
 * <li>Resource-constrained environments such as embedded devices.
 *     For really extreme constraints, you can easily remove unneeded
 *     functionality to make it even smaller (and use the -Dstripped
 *     maven build option to strip away debug info, license, etc.)</li>
 * <li>Unit and integration tests - fast setup/teardown times, small overhead
 *     and simple context handler setup make it a great web server for testing
 *     client components under various server response conditions.</li>
 * <li>Embedding a web console into any headless application for
 *     administration, monitoring, or a full portable GUI.</li>
 * <li>A full-fledged standalone web server serving static files,
 *     dynamically-generated content, REST APIs, pseudo-streaming, etc.</li>
 * <li>A good reference for learning how HTTP works under the hood.</li>
 * </ul>
 * <p>
 * <b>Implementation Notes</b>
 * <p>
 * The design and implementation of this server attempt to balance correctness,
 * compliance, readability, size, features, extensibility and performance,
 * and often prioritize them in this order, but some trade-offs must be made.
 * <p>
 * This server is multithreaded in its support for multiple concurrent HTTP
 * connections, however most of its constituent classes are not thread-safe and
 * require external synchronization if accessed by multiple threads concurrently.
 * <p>
 * <b>Source Structure and Documentation</b>
 * <p>
 * This server is intentionally written as a single source file, in order to make
 * it as easy as possible to integrate into any existing project - by simply adding
 * this single file to the project sources. It does, however, aim to maintain a
 * structured and flexible design. There are no external package dependencies.
 * <p>
 * This file contains extensive documentation of its classes and methods, as
 * well as implementation details and references to specific RFC sections
 * which clarify the logic behind the code. It is recommended that anyone
 * attempting to modify the protocol-level functionality become acquainted with
 * the RFC, in order to make sure that protocol compliance is not broken.
 * <p>
 * <b>Getting Started</b>
 * <p>
 * For an example and a good starting point for learning how to use the API,
 * see the {@link #main main} method at the bottom of the file, and follow
 * the code into the API from there. Alternatively, you can just browse through
 * the classes and utility methods and read their documentation and code.
 *
 * @author Amichai Rothman
 * @since  2008-07-24
 */
public class HTTPServer {

    protected static int MAX_BODY_SIZE = 2097152; //2m
    protected static int MAX_HEADER_SIZE = 8192;

    /**
     * The SimpleDateFormat-compatible formats of dates which must be supported.
     * Note that all generated date fields must be in the RFC 1123 format only,
     * while the others are supported by recipients for backwards-compatibility.
     */
    public static final String[] DATE_PATTERNS = {
        "EEE, dd MMM yyyy HH:mm:ss z", // RFC 822, updated by RFC 1123
        "EEEE, dd-MMM-yy HH:mm:ss z",  // RFC 850, obsoleted by RFC 1036
        "EEE MMM d HH:mm:ss yyyy"      // ANSI C's asctime() format
    };

    /** A GMT (UTC) timezone instance. */
    protected static final TimeZone GMT = TimeZone.getTimeZone("GMT");

    /** Date format strings. */
    protected static final char[]
        DAYS = "Sun Mon Tue Wed Thu Fri Sat".toCharArray(),
        MONTHS = "Jan Feb Mar Apr May Jun Jul Aug Sep Oct Nov Dec".toCharArray();

    /** A convenience array containing the carriage-return and line feed chars. */
    public static final byte[] CRLF = { 0x0d, 0x0a };

    /** The HTTP status description strings. */
    protected static final String[] statuses = new String[600];

    static {
        // initialize status descriptions lookup table
        Arrays.fill(statuses, "Unknown Status");
        statuses[100] = "Continue";
        statuses[200] = "OK";
        statuses[204] = "No Content";
        statuses[206] = "Partial Content";
        statuses[301] = "Moved Permanently";
        statuses[302] = "Found";
        statuses[304] = "Not Modified";
        statuses[307] = "Temporary Redirect";
        statuses[400] = "Bad Request";
        statuses[401] = "Unauthorized";
        statuses[403] = "Forbidden";
        statuses[404] = "Not Found";
        statuses[405] = "Method Not Allowed";
        statuses[408] = "Request Timeout";
        statuses[412] = "Precondition Failed";
        statuses[413] = "Request Entity Too Large";
        statuses[414] = "Request-URI Too Large";
        statuses[416] = "Requested Range Not Satisfiable";
        statuses[417] = "Expectation Failed";
        statuses[500] = "Internal Server Error";
        statuses[501] = "Not Implemented";
        statuses[502] = "Bad Gateway";
        statuses[503] = "Service Unavailable";
        statuses[504] = "Gateway Time-out";
    }

    /**
     * A mapping of path suffixes (e.g. file extensions) to their
     * corresponding MIME types.
     */
    protected static final Map<String, String> contentTypes =
        new ConcurrentHashMap<String, String>();

    static {
        // add some default common content types
        // see http://www.iana.org/assignments/media-types/ for full list
        addContentType("application/font-woff", "woff");
        addContentType("application/font-woff2", "woff2");
        addContentType("application/java-archive", "jar");
        addContentType("application/javascript", "js");
        addContentType("application/json", "json");
        addContentType("application/octet-stream", "exe");
        addContentType("application/pdf", "pdf");
        addContentType("application/x-7z-compressed", "7z");
        addContentType("application/x-compressed", "tgz");
        addContentType("application/x-gzip", "gz");
        addContentType("application/x-tar", "tar");
        addContentType("application/xhtml+xml", "xhtml");
        addContentType("application/zip", "zip");
        addContentType("audio/mpeg", "mp3");
        addContentType("image/gif", "gif");
        addContentType("image/jpeg", "jpg", "jpeg");
        addContentType("image/png", "png");
        addContentType("image/svg+xml", "svg");
        addContentType("image/x-icon", "ico");
        addContentType("text/css", "css");
        addContentType("text/csv", "csv");
        addContentType("text/html; charset=utf-8", "htm", "html");
        addContentType("text/plain", "txt", "text", "log");
        addContentType("text/xml", "xml");
    }

    /** The MIME types that can be compressed (prefix/suffix wildcards allowed). */
    protected static String[] compressibleContentTypes =
        { "text/*", "*/javascript", "*icon", "*+xml", "*/json" };

    /**
     * The {@code LimitedInputStream} provides access to a limited number
     * of consecutive bytes from the underlying InputStream, starting at its
     * current position. If this limit is reached, it behaves as though the end
     * of stream has been reached (although the underlying stream remains open
     * and may contain additional data).
     */
    public static class LimitedInputStream extends FilterInputStream {

        protected long limit; // decremented when read, until it reaches zero
        protected boolean prematureEndException;

        /**
         * Constructs a LimitedInputStream with the given underlying
         * input stream and limit.
         *
         * @param in the underlying input stream
         * @param limit the maximum number of bytes that may be consumed from
         *        the underlying stream before this stream ends. If zero or
         *        negative, this stream will be at its end from initialization.
         * @param prematureEndException specifies the stream's behavior when
         *        the underlying stream end is reached before the limit is
         *        reached: if true, an exception is thrown, otherwise this
         *        stream reaches its end as well (i.e. read() returns -1)
         * @throws NullPointerException if the given stream is null
         */
        public LimitedInputStream(InputStream in, long limit, boolean prematureEndException) {
            super(in);
            if (in == null)
                throw new NullPointerException("input stream is null");
            this.limit = limit < 0 ? 0 : limit;
            this.prematureEndException = prematureEndException;
        }

        @Override
        public int read() throws IOException {
            int res = limit == 0 ? -1 : in.read();
            if (res < 0 && limit > 0 && prematureEndException)
                throw new IOException("unexpected end of stream");
            limit = res < 0 ? 0 : limit - 1;
            return res;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            int res = limit == 0 ? -1 : in.read(b, off, len > limit ? (int)limit : len);
            if (res < 0 && limit > 0 && prematureEndException)
                throw new IOException("unexpected end of stream");
            limit = res < 0 ? 0 : limit - res;
            return res;
        }

        @Override
        public long skip(long len) throws IOException {
            long res = in.skip(len > limit ? limit : len);
            limit -= res;
            return res;
        }

        @Override
        public int available() throws IOException {
            int res = in.available();
            return res > limit ? (int)limit : res;
        }

        @Override
        public boolean markSupported() {
            return false;
        }

        @Override
        public void close() {
            limit = 0; // end this stream, but don't close the underlying stream
        }
    }

    /**
     * The {@code ChunkedInputStream} decodes an InputStream whose data has the
     * "chunked" transfer encoding applied to it, providing the underlying data.
     */
    public static class ChunkedInputStream extends LimitedInputStream {

        protected Headers headers;
        protected boolean initialized;

        /**
         * Constructs a ChunkedInputStream with the given underlying stream, and
         * a headers container to which the stream's trailing headers will be
         * added.
         *
         * @param in the underlying "chunked"-encoded input stream
         * @param headers the headers container to which the stream's trailing
         *        headers will be added, or null if they are to be discarded
         * @throws NullPointerException if the given stream is null
         */
        public ChunkedInputStream(InputStream in, Headers headers) {
            super(in, 0, true);
            this.headers = headers;
        }

        @Override
        public int read() throws IOException {
            return limit <= 0 && initChunk() < 0 ? -1 : super.read();
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            return limit <= 0 && initChunk() < 0 ? -1 : super.read(b, off, len);
        }

        /**
         * Initializes the next chunk. If the previous chunk has not yet
         * ended, or the end of stream has been reached, does nothing.
         *
         * @return the length of the chunk, or -1 if the end of stream
         *         has been reached
         * @throws IOException if an IO error occurs or the stream is corrupt
         */
        protected long initChunk() throws IOException {
            if (limit == 0) { // finished previous chunk
                // read chunk-terminating CRLF if it's not the first chunk
                if (initialized && readLine(in).length() > 0)
                    throw new IOException("chunk data must end with CRLF");
                initialized = true;
                limit = parseChunkSize(readLine(in)); // read next chunk size
                if (limit == 0) { // last chunk has size 0
                    limit = -1; // mark end of stream
                    // read trailing headers, if any
                    Headers trailingHeaders = readHeaders(in);
                    if (headers != null)
                        headers.addAll(trailingHeaders);
                }
            }
            return limit;
        }

        /**
         * Parses a chunk-size line.
         *
         * @param line the chunk-size line to parse
         * @return the chunk size
         * @throws IllegalArgumentException if the chunk-size line is invalid
         */
        protected static long parseChunkSize(String line) throws IllegalArgumentException {
            int pos = line.indexOf(';');
            line = pos < 0 ? line : line.substring(0, pos); // ignore params, if any
            try {
                return parseULong(line, 16); // throws NFE
            } catch (NumberFormatException nfe) {
                throw new IllegalArgumentException(
                    "invalid chunk size line: \"" + line + "\"");
            }
        }
    }

    /**
     * The {@code ChunkedOutputStream} encodes an OutputStream with the
     * "chunked" transfer encoding. It should be used only when the content
     * length is not known in advance, and with the response Transfer-Encoding
     * header set to "chunked".
     * <p>
     * Data is written to the stream by calling the {@link #write(byte[], int, int)}
     * method, which writes a new chunk per invocation. To end the stream,
     * the {@link #writeTrailingChunk} method must be called or the stream closed.
     */
    public static class ChunkedOutputStream extends FilterOutputStream {

        protected int state; // the current stream state

        /**
         * Constructs a ChunkedOutputStream with the given underlying stream.
         *
         * @param out the underlying output stream to which the chunked stream
         *        is written
         * @throws NullPointerException if the given stream is null
         */
        public ChunkedOutputStream(OutputStream out) {
            super(out);
            if (out == null)
                throw new NullPointerException("output stream is null");
        }

        /**
         * Initializes a new chunk with the given size.
         *
         * @param size the chunk size (must be positive)
         * @throws IllegalArgumentException if size is negative
         * @throws IOException if an IO error occurs, or the stream has
         *         already been ended
         */
        protected void initChunk(long size) throws IOException {
            if (size < 0)
                throw new IllegalArgumentException("invalid size: " + size);
            if (state > 0)
                out.write(CRLF); // end previous chunk
            else if (state == 0)
                state = 1; // start first chunk
            else
                throw new IOException("chunked stream has already ended");
            out.write(getBytes(Long.toHexString(size)));
            out.write(CRLF);
        }

        /**
         * Writes the trailing chunk which marks the end of the stream.
         *
         * @param headers the (optional) trailing headers to write, or null
         * @throws IOException if an error occurs
         */
        public void writeTrailingChunk(Headers headers) throws IOException {
            initChunk(0); // zero-sized chunk marks the end of the stream
            if (headers == null)
                out.write(CRLF); // empty header block
            else
                headers.writeTo(out);
            state = -1;
        }

        /**
         * Writes a chunk containing the given byte. This method initializes
         * a new chunk of size 1, and then writes the byte as the chunk data.
         *
         * @param b the byte to write as a chunk
         * @throws IOException if an error occurs
         */
        @Override
        public void write(int b) throws IOException {
            write(new byte[] { (byte)b }, 0, 1);
        }

        /**
         * Writes a chunk containing the given bytes. This method initializes
         * a new chunk of the given size, and then writes the chunk data.
         *
         * @param b an array containing the bytes to write
         * @param off the offset within the array where the data starts
         * @param len the length of the data in bytes
         * @throws IOException if an error occurs
         * @throws IndexOutOfBoundsException if the given offset or length
         *         are outside the bounds of the given array
         */
        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            if (len > 0) // zero-sized chunk is the trailing chunk
                initChunk(len);
            out.write(b, off, len);
        }

        /**
         * Writes the trailing chunk if necessary, and closes the underlying stream.
         *
         * @throws IOException if an error occurs
         */
        @Override
        public void close() throws IOException {
            if (state > -1)
                writeTrailingChunk(null);
            super.close();
        }
    }

    /**
     * The {@code ResponseOutputStream} encompasses a single response over a connection,
     * and does not close the underlying stream so that it can be used by subsequent responses.
     */
    public static class ResponseOutputStream extends FilterOutputStream {

        /**
         * Constructs a ResponseOutputStream.
         *
         * @param out the underlying output stream
         */
        public ResponseOutputStream(OutputStream out) {
            super(out);
        }

        @Override
        public void close() {} // keep underlying connection stream open

        @Override // override the very inefficient default implementation
        public void write(byte[] b, int off, int len) throws IOException { out.write(b, off, len); }
    }

    /**
     * The {@code MultipartInputStream} decodes an InputStream whose data has
     * a "multipart/*" content type (see RFC 2046), providing the underlying
     * data of its various parts.
     * <p>
     * The {@code InputStream} methods (e.g. {@link #read}) relate only to
     * the current part, and the {@link #nextPart} method advances to the
     * beginning of the next part.
     */
    public static class MultipartInputStream extends FilterInputStream {

        protected final byte[] boundary; // including leading CRLF--
        protected final byte[] buf = new byte[4096];
        protected int head, tail; // indices of current part's data in buf
        protected int end; // last index of input data read into buf
        protected int len; // length of found boundary
        protected int state; // initial, started data, start boundary, EOS, last boundary, epilogue

        /**
         * Constructs a MultipartInputStream with the given underlying stream.
         *
         * @param in the underlying multipart stream
         * @param boundary the multipart boundary
         * @throws NullPointerException if the given stream or boundary is null
         * @throws IllegalArgumentException if the given boundary's size is not
         *         between 1 and 70
         */
        protected MultipartInputStream(InputStream in, byte[] boundary) {
            super(in);
            int len = boundary.length;
            if (len == 0 || len > 70)
                throw new IllegalArgumentException("invalid boundary length");
            this.boundary = new byte[len + 4]; // CRLF--boundary
            System.arraycopy(CRLF, 0, this.boundary, 0, 2);
            this.boundary[2] = this.boundary[3] = '-';
            System.arraycopy(boundary, 0, this.boundary, 4, len);
        }

        @Override
        public int read() throws IOException {
            if (!fill())
                return -1;
            return buf[head++] & 0xFF;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            if (!fill())
                return -1;
            len = Math.min(tail - head, len);
            System.arraycopy(buf, head, b, off, len); // throws IOOBE as necessary
            head += len;
            return len;
        }

        @Override
        public long skip(long len) throws IOException {
            if (len <= 0 || !fill())
                return 0;
            len = Math.min(tail - head, len);
            head += len;
            return len;
        }

        @Override
        public int available() throws IOException {
            return tail - head;
        }

        @Override
        public boolean markSupported() {
            return false;
        }

        /**
         * Advances the stream position to the beginning of the next part.
         * Data read before calling this method for the first time is the preamble,
         * and data read after this method returns false is the epilogue.
         *
         * @return true if successful, or false if there are no more parts
         * @throws IOException if an error occurs
         */
        public boolean nextPart() throws IOException {
            while (skip(buf.length) != 0); // skip current part (until boundary)
            head = tail += len; // the next part starts right after boundary
            state |= 1; // started data (after first boundary)
            if (state >= 8) { // found last boundary
                state |= 0x10; // now beyond last boundary (epilogue)
                return false;
            }
            findBoundary(); // update indices
            return true;
        }

        /**
         * Fills the buffer with more data from the underlying stream.
         *
         * @return true if there is available data for the current part,
         *         or false if the current part's end has been reached
         * @throws IOException if an error occurs or the input format is invalid
         */
        protected boolean fill() throws IOException {
            // check if we already have more available data
            if (head != tail) // remember that if we continue, head == tail below
                return true;
            // if there's no more room, shift extra unread data to beginning of buffer
            if (tail > buf.length - 256) { // max boundary + whitespace supported size
                System.arraycopy(buf, tail, buf, 0, end -= tail);
                head = tail = 0;
            }
            // read more data and look for boundary (or potential partial boundary)
            int read;
            do {
                read = super.read(buf, end, buf.length - end);
                if (read < 0)
                    state |= 4; // end of stream (EOS)
                else
                    end += read;
                findBoundary(); // updates tail and length to next potential boundary
                // if we found a partial boundary with no data before it, we must
                // continue reading to determine if there is more data or not
            } while (read > 0 && tail == head && len == 0);
            // update and validate state
            if (tail != 0) // anything but a boundary right at the beginning
                state |= 1; // started data (preamble or after boundary)
            if (state < 8 && len > 0)
                state |= 2; // found start boundary
            if ((state & 6) == 4 // EOS but no start boundary found
                || len == 0 && ((state & 0xFC) == 4 // EOS but no last and no more boundaries
                    || read == 0 && tail == head)) // boundary longer than buffer
                        throw new IOException("missing boundary");
            if (state >= 0x10) // in epilogue
                tail = end; // ignore boundaries, return everything
            return tail > head; // available data in current part
        }

        /**
         * Finds the first (potential) boundary within the buffer's remaining data.
         * Updates tail, length and state fields accordingly.
         *
         * @throws IOException if an error occurs or the input format is invalid
         */
        protected void findBoundary() throws IOException {
            // see RFC2046#5.1.1 for boundary syntax
            len = 0;
            int off = tail - ((state & 1) != 0 || buf[0] != '-' ? 0 : 2); // skip initial CRLF?
            for (int end = this.end; tail < end; tail++, off = tail) {
                int j = tail; // end of potential boundary
                // try to match boundary value (leading CRLF is optional at first boundary)
                while (j < end && j - off < boundary.length && buf[j] == boundary[j - off])
                    j++;
                // return potential partial boundary which is cut off at end of current data
                if (j + 1 >= end) // at least two more chars needed for full boundary (CRLF or --)
                    return;
                // if we found the boundary value, expand selection to include full line
                if (j - off == boundary.length) {
                    // check if last boundary of entire multipart
                    if (buf[j] == '-' && buf[j + 1] == '-') {
                        j += 2;
                        state |= 8; // found last boundary that ends multipart
                    }
                    // allow linear whitespace after boundary
                    while (j < end && (buf[j] == ' ' || buf[j] == '\t'))
                        j++;
                    // check for CRLF (required, except in last boundary with no epilogue)
                    if (j + 1 < end && buf[j] == '\r' && buf[j + 1] == '\n') // found CRLF
                        len = j - tail + 2; // including optional whitespace and CRLF
                    else if (j + 1 < end || (state & 4) != 0 && j + 1 == end) // should have found or never will
                        throw new IOException("boundary must end with CRLF");
                    else if ((state & 4) != 0) // last boundary with no CRLF at end of data is valid
                        len = j - tail;
                    return;
                }
            }
        }
    }

    /**
     * The {@code MultipartIterator} iterates over the parts of a multipart/form-data request.
     * <p>
     * For example, to support file upload from a web browser:
     * <ol>
     * <li>Create an HTML form which includes an input field of type "file", attributes
     *     method="post" and enctype="multipart/form-data", and an action URL of your choice,
     *     for example action="/upload". This form can be served normally like any other
     *     resource, e.g. from an HTML file on disk.
     * <li>Add a context handler for the action path ("/upload" in this example), using either
     *     the explicit {@link VirtualHost#addContext} method or the {@link Context} annotation.
     * <li>In the context handler implementation, construct a {@code MultipartIterator} from
     *     the client {@code Request}.
     * <li>Iterate over the form {@link Part}s, processing each named field as appropriate -
     *     for the file input field, read the uploaded file using the body input stream.
     * </ol>
     */
    public static class MultipartIterator implements Iterator<MultipartIterator.Part> {

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
                return readToken(body, -1, charset == null ? "UTF-8" : charset, MAX_HEADER_SIZE);
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
        public MultipartIterator(Request req) throws IOException {
            Map<String, String> ct = req.getHeaders().getParams("Content-Type");
            if (!ct.containsKey("multipart/form-data"))
                throw new IllegalArgumentException("Content-Type is not multipart/form-data");
            String boundary = ct.get("boundary"); // should be US-ASCII
            if (boundary == null)
                throw new IllegalArgumentException("Content-Type is missing boundary");
            in = new MultipartInputStream(req.getBody(), getBytes(boundary));
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
                p.headers = readHeaders(in);
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

    /**
     * The {@code VirtualHost} class represents a virtual host in the server.
     */
    public static class VirtualHost {

        /**
         * The {@code ContextInfo} class holds a single context's information.
         */
        public class ContextInfo {

            protected final String path;
            protected final Map<String, ContextHandler> handlers =
                new ConcurrentHashMap<String, ContextHandler>(2);

            /**
             * Constructs a ContextInfo with the given context path.
             *
             * @param path the context path (without trailing slash)
             */
            public ContextInfo(String path) {
                this.path = path;
            }

            /**
             * Returns the context path.
             *
             * @return the context path, or null if there is none
             */
            public String getPath() {
                return path;
            }

            /**
             * Returns the map of supported HTTP methods and their corresponding handlers.
             *
             * @return the map of supported HTTP methods and their corresponding handlers
             */
            public Map<String, ContextHandler> getHandlers() {
                return handlers;
            }

            /**
             * Adds (or replaces) a context handler for the given HTTP methods.
             *
             * @param handler the context handler
             * @param methods the HTTP methods supported by the handler (default is "GET")
             */
            public void addHandler(ContextHandler handler, String... methods) {
                if (methods.length == 0)
                    methods = new String[] { "GET" };
                for (String method : methods) {
                    handlers.put(method, handler);
                    VirtualHost.this.methods.add(method); // it's now supported by server
                }
            }
        }

        protected final String name;
        protected final Set<String> aliases = new CopyOnWriteArraySet<String>();
        protected volatile String directoryIndex = "index.html";
        protected volatile boolean allowGeneratedIndex;
        protected final Set<String> methods = new CopyOnWriteArraySet<String>();
        protected final ContextInfo emptyContext = new ContextInfo(null);
        protected final ConcurrentMap<String, ContextInfo> contexts =
            new ConcurrentHashMap<String, ContextInfo>();

        /**
         * Constructs a VirtualHost with the given name.
         *
         * @param name the host's name, or null if it is the default host
         */
        public VirtualHost(String name) {
            this.name = name;
            contexts.put("*", new ContextInfo(null)); // for "OPTIONS *"
        }

        /**
         * Returns this host's name.
         *
         * @return this host's name, or null if it is the default host
         */
        public String getName() {
            return name;
        }

        /**
         * Adds an alias for this host.
         *
         * @param alias the alias
         */
        public void addAlias(String alias) {
            aliases.add(alias);
        }

        /**
         * Returns this host's aliases.
         *
         * @return the (unmodifiable) set of aliases (which may be empty)
         */
        public Set<String> getAliases() {
            return Collections.unmodifiableSet(aliases);
        }

        /**
         * Sets the directory index file. For every request whose URI ends with
         * a '/' (i.e. a directory), the index file is appended to the path,
         * and the resulting resource is served if it exists. If it does not
         * exist, an auto-generated index for the requested directory may be
         * served, depending on whether {@link #setAllowGeneratedIndex
         * a generated index is allowed}, otherwise an error is returned.
         * The default directory index file is "index.html".
         *
         * @param directoryIndex the directory index file, or null if no
         *        index file should be used
         */
        public void setDirectoryIndex(String directoryIndex) {
            this.directoryIndex = directoryIndex;
        }

        /**
         * Gets this host's directory index file.
         *
         * @return the directory index file, or null
         */
        public String getDirectoryIndex() {
            return directoryIndex;
        }

        /**
         * Sets whether auto-generated indices are allowed. If false, and a
         * directory resource is requested, an error will be returned instead.
         *
         * @param allowed specifies whether generated indices are allowed
         */
        public void setAllowGeneratedIndex(boolean allowed) {
            this.allowGeneratedIndex = allowed;
        }

        /**
         * Returns whether auto-generated indices are allowed.
         *
         * @return whether auto-generated indices are allowed
         */
        public boolean isAllowGeneratedIndex() {
            return allowGeneratedIndex;
        }

        /**
         * Returns all HTTP methods explicitly supported by at least one context
         * (this may or may not include the methods with required or built-in support).
         *
         * @return all HTTP methods explicitly supported by at least one context
         */
        public Set<String> getMethods() {
            return methods;
        }

        /**
         * Returns the context handler for the given path.
         * <p>
         * If a context is not found for the given path, the search is repeated for
         * its parent path, and so on until a base context is found. If neither the
         * given path nor any of its parents has a context, an empty context is returned.
         *
         * @param path the context's path
         * @return the context info for the given path, or an empty context if none exists
         */
        public ContextInfo getContext(String path) {
            // all context paths are without trailing slash
            for (path = trimRight(path, '/'); path != null; path = getParentPath(path)) {
                ContextInfo info = contexts.get(path);
                if (info != null)
                    return info;
            }
            return emptyContext;
        }

        /**
         * Adds a context and its corresponding context handler to this server.
         * Paths are normalized by removing trailing slashes (except the root).
         *
         * @param path the context's path (must start with '/')
         * @param handler the context handler for the given path
         * @param methods the HTTP methods supported by the context handler (default is "GET")
         * @throws IllegalArgumentException if path is malformed
         */
        public void addContext(String path, ContextHandler handler, String... methods) {
            if (path == null || !path.startsWith("/") && !path.equals("*"))
                throw new IllegalArgumentException("invalid path: " + path);
            path = trimRight(path, '/'); // remove trailing slash
            ContextInfo info = new ContextInfo(path);
            ContextInfo existing = contexts.putIfAbsent(path, info);
            info = existing != null ? existing : info;
            info.addHandler(handler, methods);
        }

        /**
         * Adds contexts for all methods of the given object that
         * are annotated with the {@link Context} annotation.
         *
         * @param o the object whose annotated methods are added
         * @throws IllegalArgumentException if a Context-annotated
         *         method has an {@link Context invalid signature}
         */
        public void addContexts(Object o) throws IllegalArgumentException {
            for (Class<?> c = o.getClass(); c != null; c = c.getSuperclass()) {
                // add to contexts those with @Context annotation
                for (Method m : c.getDeclaredMethods()) {
                    Context context = m.getAnnotation(Context.class);
                    if (context != null) {
                        m.setAccessible(true); // allow access to private method
                        ContextHandler handler = new MethodContextHandler(m, o);
                        addContext(context.value(), handler, context.methods());
                    }
                }
            }
        }
    }

    /**
     * The {@code Context} annotation decorates methods which are mapped
     * to a context (path) within the server, and provide its contents.
     * <p>
     * The annotated methods must have the same signature and contract
     * as {@link ContextHandler#serve}, but can have arbitrary names.
     *
     * @see VirtualHost#addContexts(Object)
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface Context {

        /**
         * The context (path) that this field maps to (must begin with '/').
         *
         * @return the context (path) that this field maps to
         */
        String value();

        /**
         * The HTTP methods supported by this context handler (default is "GET").
         *
         * @return the HTTP methods supported by this context handler
         */
        String[] methods() default "GET";
    }

    /**
     * A {@code ContextHandler} serves the content of resources within a context.
     *
     * @see VirtualHost#addContext
     */
    public interface ContextHandler {

        /**
         * Serves the given request using the given response.
         *
         * @param req the request to be served
         * @param resp the response to be filled
         * @return an HTTP status code, which will be used in returning
         *         a default response appropriate for this status. If this
         *         method invocation already sent anything in the response
         *         (headers or content), it must return 0, and no further
         *         processing will be done
         * @throws IOException if an IO error occurs
         */
        int serve(Request req, Response resp) throws IOException;
    }

    /**
     * The {@code FileContextHandler} services a context by mapping it
     * to a file or folder (recursively) on disk.
     */
    public static class FileContextHandler implements ContextHandler {

        protected final File base;

        public FileContextHandler(File dir) throws IOException {
            this.base = dir.getCanonicalFile();
        }

        public int serve(Request req, Response resp) throws IOException {
            return serveFile(base, req.getContext().getPath(), req, resp);
        }
    }

    /**
     * The {@code MethodContextHandler} services a context
     * by invoking a handler method on a specified object.
     * <p>
     * The method must have the same signature and contract as
     * {@link ContextHandler#serve}, but can have an arbitrary name.
     *
     * @see VirtualHost#addContexts(Object)
     */
    public static class MethodContextHandler implements ContextHandler {

        protected final Method m;
        protected final Object obj;

        public MethodContextHandler(Method m, Object obj) throws IllegalArgumentException {
            this.m = m;
            this.obj = obj;
            Class<?>[] params = m.getParameterTypes();
            if (params.length != 2
                || !Request.class.isAssignableFrom(params[0])
                || !Response.class.isAssignableFrom(params[1])
                || !int.class.isAssignableFrom(m.getReturnType()))
                    throw new IllegalArgumentException("invalid method signature: " + m);
        }

        public int serve(Request req, Response resp) throws IOException {
            try {
                return (Integer)m.invoke(obj, req, resp);
            } catch (InvocationTargetException ite) {
                throw new IOException("error: " + ite.getCause().getMessage());
            } catch (Exception e) {
                throw new IOException("error: " + e);
            }
        }
    }

    /**
     * The {@code Header} class encapsulates a single HTTP header.
     */
    public static class Header {

        protected final String name;
        protected final String value;

        /**
         * Constructs a header with the given name and value.
         * Leading and trailing whitespace are trimmed.
         *
         * @param name the header name
         * @param value the header value
         * @throws NullPointerException if name or value is null
         * @throws IllegalArgumentException if name is empty
         */
        public Header(String name, String value) {
            this.name = name.trim();
            this.value = value.trim();
            // RFC2616#14.23 - header can have an empty value (e.g. Host)
            if (this.name.length() == 0) // but name cannot be empty
                throw new IllegalArgumentException("name cannot be empty");
        }

        /**
         * Returns this header's name.
         *
         * @return this header's name
         */
        public String getName() { return name; }

        /**
         * Returns this header's value.
         *
         * @return this header's value
         */
        public String getValue() { return value; }
    }

    /**
     * The {@code Headers} class encapsulates a collection of HTTP headers.
     * <p>
     * Header names are treated case-insensitively, although this class retains
     * their original case. Header insertion order is maintained as well.
     */
    public static class Headers implements Iterable<Header> {

        // due to the requirements of case-insensitive name comparisons,
        // retaining the original case, and retaining header insertion order,
        // and due to the fact that the number of headers is generally
        // quite small (usually under 12 headers), we use a simple array with
        // linear access times, which proves to be more efficient and
        // straightforward than the alternatives
        protected Header[] headers = new Header[12];
        protected int count;

        /**
         * Returns the number of added headers.
         *
         * @return the number of added headers
         */
        public int size() {
            return count;
        }

        /**
         * Returns the value of the first header with the given name.
         *
         * @param name the header name (case insensitive)
         * @return the header value, or null if none exists
         */
        public String get(String name) {
            for (int i = 0; i < count; i++)
                if (headers[i].getName().equalsIgnoreCase(name))
                    return headers[i].getValue();
            return null;
        }

        /**
         * Returns the Date value of the header with the given name.
         *
         * @param name the header name (case insensitive)
         * @return the header value as a Date, or null if none exists
         *         or if the value is not in any supported date format
         */
        public Date getDate(String name) {
            try {
                String header = get(name);
                return header == null ? null : parseDate(header);
            } catch (IllegalArgumentException iae) {
                return null;
            }
        }

        /**
         * Returns whether there exists a header with the given name.
         *
         * @param name the header name (case insensitive)
         * @return whether there exists a header with the given name
         */
        public boolean contains(String name) {
            return get(name) != null;
        }

        /**
         * Adds a header with the given name and value to the end of this
         * collection of headers. Leading and trailing whitespace are trimmed.
         *
         * @param name the header name (case insensitive)
         * @param value the header value
         */
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

        /**
         * Adds all given headers to the end of this collection of headers,
         * in their original order.
         *
         * @param headers the headers to add
         */
        public void addAll(Headers headers) {
            for (Header header : headers)
                add(header.getName(), header.getValue());
        }

        /**
         * Adds a header with the given name and value, replacing the first
         * existing header with the same name. If there is no existing header
         * with the same name, it is added as in {@link #add}.
         *
         * @param name the header name (case insensitive)
         * @param value the header value
         * @return the replaced header, or null if none existed
         */
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

        /**
         * Removes all headers with the given name (if any exist).
         *
         * @param name the header name (case insensitive)
         */
        public void remove(String name) {
            int j = 0;
            for (int i = 0; i < count; i++)
                if (!headers[i].getName().equalsIgnoreCase(name))
                    headers[j++] = headers[i];
            while (count > j)
                headers[--count] = null;
        }

        /**
         * Writes the headers to the given stream (including trailing CRLF).
         *
         * @param out the stream to write the headers to
         * @throws IOException if an error occurs
         */
        public void writeTo(OutputStream out) throws IOException {
            for (int i = 0; i < count; i++) {
                out.write(getBytes(headers[i].getName(), ": ", headers[i].getValue()));
                out.write(CRLF);
            }
            out.write(CRLF); // ends header block
        }

        /**
         * Returns a header's parameters. Parameter order is maintained,
         * and the first key (in iteration order) is the header's value
         * without the parameters.
         *
         * @param name the header name (case insensitive)
         * @return the header's parameter names and values
         */
        public Map<String, String> getParams(String name) {
            Map<String, String> params = new LinkedHashMap<String, String>();
            for (String param : split(get(name), ";", -1)) {
                String[] pair = split(param, "=", 2);
                String val = pair.length == 1 ? "" : trimLeft(trimRight(pair[1], '"'), '"');
                params.put(pair[0], val);
            }
            return params;
        }

        /**
         * Returns an iterator over the headers, in their insertion order.
         * If the headers collection is modified during iteration, the
         * iteration result is undefined. The remove operation is unsupported.
         *
         * @return an Iterator over the headers
         */
        public Iterator<Header> iterator() {
            // we use the built-in wrapper instead of a trivial custom implementation
            // since even a tiny anonymous class here compiles to a 1.5K class file
            return Arrays.asList(headers).subList(0, count).iterator();
        }
    }

    /**
     * The {@code Request} class encapsulates a single HTTP request.
     */
    public class Request {

        protected String method;
        protected URI uri;
        protected URL baseURL; // cached value
        protected String version;
        protected Headers headers;
        protected InputStream body;
        protected Socket sock;
        protected Map<String, String> params; // cached value
        protected VirtualHost host; // cached value
        protected VirtualHost.ContextInfo context; // cached value

        /**
         * Constructs a Request from the data in the given input stream.
         *
         * @param in the input stream from which the request is read
         * @param sock the underlying connected socket
         * @throws IOException if an error occurs
         */
        public Request(InputStream in, Socket sock) throws IOException {
            this.sock = sock;
            readRequestLine(in);
            headers = readHeaders(in);
            // RFC2616#3.6 - if "chunked" is used, it must be the last one
            // RFC2616#4.4 - if non-identity Transfer-Encoding is present,
            // it must either include "chunked" or close the connection after
            // the body, and in any case ignore Content-Length.
            // if there is no such Transfer-Encoding, use Content-Length
            // if neither header exists, there is no body
            String header = headers.get("Transfer-Encoding");
            if (header != null && !header.toLowerCase(Locale.US).equals("identity")) {
                if (Arrays.asList(splitElements(header, true)).contains("chunked"))
                    body = new ChunkedInputStream(in, headers);
                else
                    body = in; // body ends when connection closes
            } else {
                header = headers.get("Content-Length");
                long len = header == null ? 0 : parseULong(header, 10);
                body = new LimitedInputStream(in, len, false);
            }
        }

        /**
         * Returns the request method.
         *
         * @return the request method
         */
        public String getMethod() { return method; }

        /**
         * Returns the request URI.
         *
         * @return the request URI
         */
        public URI getURI() { return uri; }

        /**
         * Returns the request version string.
         *
         * @return the request version string
         */
        public String getVersion() { return version; }

        /**
         * Returns the request headers.
         *
         * @return the request headers
         */
        public Headers getHeaders() { return headers; }

        /**
         * Returns the input stream containing the request body.
         *
         * @return the input stream containing the request body
         */
        public InputStream getBody() { return body; }

        /**
         * Returns the underlying socket, which can be used to retrieve connection meta-data.
         *
         * @return the underlying socket
         */
        public Socket getSocket() { return sock; }

        /**
         * Returns the path component of the request URI, after
         * URL decoding has been applied (using the UTF-8 charset).
         *
         * @return the decoded path component of the request URI
         */
        public String getPath() {
            return uri.getPath();
        }

        /**
         * Sets the path component of the request URI. This can be useful
         * in URL rewriting, etc.
         *
         * @param path the path to set
         * @throws IllegalArgumentException if the given path is malformed
         */
        public void setPath(String path) {
            try {
                uri = new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(),
                    trimDuplicates(path, '/'), uri.getQuery(), uri.getFragment());
                context = null; // clear cached context so it will be recalculated
            } catch (URISyntaxException use) {
                throw new IllegalArgumentException("error setting path", use);
            }
        }

        /**
         * Returns the base URL (scheme, host and port) of the request resource.
         * The host name is taken from the request URI or the Host header or a
         * default host (see RFC2616#5.2).
         *
         * @return the base URL of the requested resource, or null if it
         *         is malformed
         */
        public URL getBaseURL() {
            if (baseURL != null)
                return baseURL;
            // normalize host header
            String host = uri.getHost();
            if (host == null) {
                host = headers.get("Host");
                if (host == null) // missing in HTTP/1.0
                    host = detectLocalHostName();
            }
            int pos = host.indexOf(':');
            host = pos < 0 ? host : host.substring(0, pos);
            try {
                return baseURL = new URL(secure ? "https" : "http", host, port, "");
            } catch (MalformedURLException mue) {
                return null;
            }
        }

        /**
         * Returns the request parameters, which are parsed both from the query
         * part of the request URI, and from the request body if its content
         * type is "application/x-www-form-urlencoded" (i.e. a submitted form).
         * UTF-8 encoding is assumed in both cases.
         * <p>
         * The parameters are returned as a list of string arrays, each containing
         * the parameter name as the first element and its corresponding value
         * as the second element (or an empty string if there is no value).
         * <p>
         * The list retains the original order of the parameters.
         *
         * @return the request parameters name-value pairs,
         *         or an empty list if there are none
         * @throws IOException if an error occurs
         * @see HTTPServer#parseParamsList(String)
         */
        private List<String[]> _paramsList; //noear,20210801
        public List<String[]> getParamsList() throws IOException {
            if(_paramsList == null) {
                List<String[]> queryParams = parseParamsList(uri.getRawQuery());
                List<String[]> bodyParams = Collections.emptyList();
                String ct = headers.get("Content-Type");
                if (ct != null && ct.toLowerCase(Locale.US).startsWith("application/x-www-form-urlencoded"))
                    bodyParams = parseParamsList(readToken(body, -1, "UTF-8", MAX_BODY_SIZE)); // 2MB limit


                _paramsList = new ArrayList<>(); //noear,20211218,最终都汇总

                if (queryParams.isEmpty() == false)
                    _paramsList.addAll(queryParams);

                if (bodyParams.isEmpty() == false)
                    _paramsList.addAll(bodyParams);
            }

            return _paramsList;
        }

        /**
         * Returns the request parameters, which are parsed both from the query
         * part of the request URI, and from the request body if its content
         * type is "application/x-www-form-urlencoded" (i.e. a submitted form).
         * UTF-8 encoding is assumed in both cases.
         * <p>
         * For multivalued parameters (i.e. multiple parameters with the same
         * name), only the first one is considered. For access to all values,
         * use {@link #getParamsList()} instead.
         * <p>
         * The map iteration retains the original order of the parameters.
         *
         * @return the request parameters name-value pairs,
         *         or an empty map if there are none
         * @throws IOException if an error occurs
         * @see #getParamsList()
         */
        public Map<String, String> getParams() throws IOException {
            if (params == null)
                params = toMap(getParamsList());
            return params;
        }

        /**
         * Returns the absolute (zero-based) content range value read
         * from the Range header. If multiple ranges are requested, a single
         * range containing all of them is returned.
         *
         * @param length the full length of the requested resource
         * @return the requested range, or null if the Range header
         *         is missing or invalid
         */
        public long[] getRange(long length) {
            String header = headers.get("Range");
            return header == null || !header.startsWith("bytes=")
                ? null : parseRange(header.substring(6), length);
        }

        /**
         * Reads the request line, parsing the method, URI and version string.
         *
         * @param in the input stream from which the request line is read
         * @throws IOException if an error occurs or the request line is invalid
         */
        protected void readRequestLine(InputStream in) throws IOException {
            // RFC2616#4.1: should accept empty lines before request line
            // RFC2616#19.3: tolerate additional whitespace between tokens
            String line;
            try {
                do { line = readLine(in); } while (line.length() == 0);
            } catch (IOException ioe) { // if EOF, timeout etc.
                throw new IOException("missing request line"); // signal that the request did not begin
            }
            String[] tokens = split(line, " ", -1);
            if (tokens.length != 3)
                throw new IOException("invalid request line: \"" + line + "\"");
            try {
                method = tokens[0];
                // must remove '//' prefix which constructor parses as host name
                uri = new URI(tokens[1]); //todo: by noear 20220707 new URI(trimDuplicates(tokens[1], '/'));
                version = tokens[2]; // RFC2616#2.1: allow implied LWS; RFC7230#3.1.1: disallow it
            } catch (URISyntaxException use) {
                throw new IOException("invalid URI: " + use.getMessage());
            }
        }

        /**
         * Returns the virtual host corresponding to the requested host name,
         * or the default host if none exists.
         *
         * @return the virtual host corresponding to the requested host name,
         *         or the default virtual host
         */
        public VirtualHost getVirtualHost() {
            return host != null ? host
                : (host = HTTPServer.this.getVirtualHost(getBaseURL().getHost())) != null ? host
                : (host = HTTPServer.this.getVirtualHost(null));
        }

        /**
         * Returns the info of the context handling this request.
         *
         * @return the info of the context handling this request, or an empty context
         */
        public VirtualHost.ContextInfo getContext() {
            return context != null ? context : (context = getVirtualHost().getContext(getPath()));
        }
    }

    /**
     * The {@code Response} class encapsulates a single HTTP response.
     */
    public class Response implements Closeable {

        protected OutputStream out; // the underlying output stream
        protected OutputStream encodedOut; // chained encoder streams
        protected Headers headers;
        protected boolean discardBody;
        protected int state; // nothing sent, headers sent, or closed
        protected Request req; // request used in determining client capabilities

        /**
         * Constructs a Response whose output is written to the given stream.
         *
         * @param out the stream to which the response is written
         */
        public Response(OutputStream out) {
            this.out = out;
            this.headers = new Headers();
        }

        /**
         * Sets whether this response's body is discarded or sent.
         *
         * @param discardBody specifies whether the body is discarded or not
         */
        public void setDiscardBody(boolean discardBody) {
            this.discardBody = discardBody;
        }

        /**
         * Sets the request which is used in determining the capabilities
         * supported by the client (e.g. compression, encoding, etc.)
         *
         * @param req the request
         */
        public void setClientCapabilities(Request req) { this.req = req; }

        /**
         * Returns the request headers collection.
         *
         * @return the request headers collection
         */
        public Headers getHeaders() { return headers; }

        /**
         * Returns the underlying output stream to which the response is written.
         * Except for special cases, you should use {@link #getBody()} instead.
         *
         * @return the underlying output stream to which the response is written
         */
        public OutputStream getOutputStream() { return out; }

        /**
         * Returns whether the response headers were already sent.
         *
         * @return whether the response headers were already sent
         */
        public boolean headersSent() { return state == 1; }

        /**
         * Returns an output stream into which the response body can be written.
         * The stream applies encodings (e.g. compression) according to the sent headers.
         * This method must be called after response headers have been sent
         * that indicate there is a body. Normally, the content should be
         * prepared (not sent) even before the headers are sent, so that any
         * errors during processing can be caught and a proper error response returned -
         * after the headers are sent, it's too late to change the status into an error.
         *
         * @return an output stream into which the response body can be written,
         *         or null if the body should not be written (e.g. it is discarded)
         * @throws IOException if an error occurs
         */
        public OutputStream getBody() throws IOException {
            if (encodedOut != null || discardBody)
                return encodedOut; // return the existing stream (or null)
            // set up chain of encoding streams according to headers
            List<String> te = Arrays.asList(splitElements(headers.get("Transfer-Encoding"), true));
            List<String> ce = Arrays.asList(splitElements(headers.get("Content-Encoding"), true));
            encodedOut = new ResponseOutputStream(out); // leaves underlying stream open when closed
            if (te.contains("chunked"))
                encodedOut = new ChunkedOutputStream(encodedOut);
            if (ce.contains("gzip") || te.contains("gzip"))
                encodedOut = new GZIPOutputStream(encodedOut, 4096);
            else if (ce.contains("deflate") || te.contains("deflate"))
                encodedOut = new DeflaterOutputStream(encodedOut);

            return encodedOut; // return the outer-most stream
        }

        /**
         * Closes this response and flushes all output.
         *
         * @throws IOException if an error occurs
         */
        public void close() throws IOException {
            state = -1; // closed
            if (encodedOut != null)
                encodedOut.close(); // close all chained streams (except the underlying one)
            out.flush(); // always flush underlying stream (even if getBody was never called)
        }

        /**
         * Sends the response headers with the given response status.
         * A Date header is added if it does not already exist.
         * If the response has a body, the Content-Length/Transfer-Encoding
         * and Content-Type headers must be set before sending the headers.
         *
         * @param status the response status
         * @throws IOException if an error occurs or headers were already sent
         * @see #sendHeaders(int, long, long, String, String, long[])
         */
        public void sendHeaders(int status) throws IOException {
            if (headersSent())
                throw new IOException("headers were already sent");
            if (!headers.contains("Date"))
                headers.add("Date", formatDate(System.currentTimeMillis()));
            //headers.add("Server", "JLHTTP/2.6");//todo: 不要输出产品标识
            out.write(getBytes("HTTP/1.1 ", Integer.toString(status), " ", statuses[status]));
            out.write(CRLF);
            headers.writeTo(out);
            state = 1; // headers sent
        }

        /**
         * Sends the response headers, including the given response status
         * and description, and all response headers. If they do not already
         * exist, the following headers are added as necessary:
         * Content-Range, Content-Type, Transfer-Encoding, Content-Encoding,
         * Content-Length, Last-Modified, ETag, Connection  and Date. Ranges are
         * properly calculated as well, with a 200 status changed to a 206 status.
         *
         * @param status the response status
         * @param length the response body length, or zero if there is no body,
         *        or negative if there is a body but its length is not yet known
         * @param lastModified the last modified date of the response resource,
         *        or non-positive if unknown. A time in the future will be
         *        replaced with the current system time.
         * @param etag the ETag of the response resource, or null if unknown
         *        (see RFC2616#3.11)
         * @param contentType the content type of the response resource, or null
         *        if unknown (in which case "application/octet-stream" will be sent)
         * @param range the content range that will be sent, or null if the
         *        entire resource will be sent
         * @throws IOException if an error occurs
         */
        public void sendHeaders(int status, long length, long lastModified,
                String etag, String contentType, long[] range) throws IOException {
            if (range != null) {
                headers.add("Content-Range", "bytes " + range[0] + "-" +
                    range[1] + "/" + (length >= 0 ? length : "*"));
                length = range[1] - range[0] + 1;
                if (status == 200)
                    status = 206;
            }
            String ct = headers.get("Content-Type");
            if (ct == null) {
                ct = contentType != null ? contentType : "application/octet-stream";
                headers.add("Content-Type", ct);
            }else{
                if (contentType != null) { //noear,20181220
                    ct = contentType;
                    headers.replace("Content-Type", ct);
                }
            }


            if (!headers.contains("Content-Length") && !headers.contains("Transfer-Encoding")) {
                // RFC2616#3.6: transfer encodings are case-insensitive and must not be sent to an HTTP/1.0 client
                boolean modern = req != null && req.getVersion().endsWith("1.1");
                String accepted = req == null ? null : req.getHeaders().get("Accept-Encoding");
                List<String> encodings = Arrays.asList(splitElements(accepted, true));
                String compression = encodings.contains("gzip") ? "gzip" :
                                     encodings.contains("deflate") ? "deflate" : null;
                if (compression != null && (length < 0 || length > 300) && isCompressible(ct) && modern) {
                    //todo: by noear 20220316; add() -> replace()
                    headers.replace("Transfer-Encoding", "chunked"); // compressed data is always unknown length
                    headers.replace("Content-Encoding", compression);
                } else if (length < 0 && modern) {
                    headers.replace("Transfer-Encoding", "chunked"); // unknown length
                } else if (length >= 0) {
                    headers.replace("Content-Length", Long.toString(length)); // known length
                }
            }
            if (!headers.contains("Vary")) // RFC7231#7.1.4: Vary field should include headers
                headers.add("Vary", "Accept-Encoding"); // that are used in selecting representation
            if (lastModified > 0 && !headers.contains("Last-Modified")) // RFC2616#14.29
                headers.add("Last-Modified", formatDate(Math.min(lastModified, System.currentTimeMillis())));
            if (etag != null && !headers.contains("ETag"))
                headers.add("ETag", etag);
            if (req != null && "close".equalsIgnoreCase(req.getHeaders().get("Connection"))
                    && !headers.contains("Connection"))
                headers.add("Connection", "close"); // #RFC7230#6.6: should reply to close with close
            sendHeaders(status);
        }

        /**
         * Sends the full response with the given status, and the given string
         * as the body. The text is sent in the UTF-8 charset. If a
         * Content-Type header was not explicitly set, it will be set to
         * text/html, and so the text must contain valid (and properly
         * {@link HTTPServer#escapeHTML escaped}) HTML.
         *
         * @param status the response status
         * @param text the text body (sent as text/html)
         * @throws IOException if an error occurs
         */
        public void send(int status, String text) throws IOException {
            byte[] content = text.getBytes("UTF-8");
            sendHeaders(status, content.length, -1,
                "W/\"" + Integer.toHexString(text.hashCode()) + "\"",
                "text/html; charset=utf-8", null);
            OutputStream out = getBody();
            if (out != null)
                out.write(content);
        }

        /**
         * Sends an error response with the given status and detailed message.
         * An HTML body is created containing the status and its description,
         * as well as the message, which is escaped using the
         * {@link HTTPServer#escapeHTML escape} method.
         *
         * @param status the response status
         * @param text the text body (sent as text/html)
         * @throws IOException if an error occurs
         */
        public void sendError(int status, String text) throws IOException {
            send(status, String.format(
                "<!DOCTYPE html>%n<html>%n<head><title>%d %s</title></head>%n" +
                "<body><h1>%d %s</h1>%n<p>%s</p>%n</body></html>",
                status, statuses[status], status, statuses[status], escapeHTML(text)));
        }

        /**
         * Sends an error response with the given status and default body.
         *
         * @param status the response status
         * @throws IOException if an error occurs
         */
        public void sendError(int status) throws IOException {
            String text = status < 400 ? ":)" : "sorry it didn't work out :(";
            sendError(status, text);
        }

        /**
         * Sends the response body. This method must be called only after the
         * response headers have been sent (and indicate that there is a body).
         *
         * @param body a stream containing the response body
         * @param length the full length of the response body, or -1 for the whole stream
         * @param range the sub-range within the response body that should be
         *        sent, or null if the entire body should be sent
         * @throws IOException if an error occurs
         */
        public void sendBody(InputStream body, long length, long[] range) throws IOException {
            OutputStream out = getBody();
            if (out != null) {
                if (range != null) {
                    long offset = range[0];
                    length = range[1] - range[0] + 1;
                    while (offset > 0) {
                        long skip = body.skip(offset);
                        if (skip == 0)
                            throw new IOException("can't skip to " + range[0]);
                        offset -= skip;
                    }
                }
                transfer(body, out, length);
            }
        }

        /**
         * Sends a 301 or 302 response, redirecting the client to the given URL.
         *
         * @param url the absolute URL to which the client is redirected
         * @param permanent specifies whether a permanent (301) or
         *        temporary (302) redirect status is sent
         * @throws IOException if an IO error occurs or url is malformed
         */
        public void redirect(String url, boolean permanent) throws IOException {
            try {
                url = new URI(url).toASCIIString();
            } catch (URISyntaxException e) {
                throw new IOException("malformed URL: " + url);
            }
            headers.add("Location", url);
            // some user-agents expect a body, so we send it
            if (permanent)
                sendError(301, "Permanently moved to " + url);
            else
                sendError(302, "Temporarily moved to " + url);
        }
    }

    /**
     * The {@code SocketHandlerThread} handles accepted sockets.
     */
    protected class SocketHandlerThread extends Thread {
        @Override
        public void run() {
            setName(getClass().getSimpleName() + "-" + port);
            try {
                ServerSocket serv = HTTPServer.this.serv; // keep local to avoid NPE when stopped
                while (serv != null && !serv.isClosed()) {
                    final Socket sock = serv.accept();
                    executor.execute(new Runnable() {
                        public void run() {
                            try {
                                try {
                                    sock.setSoTimeout(socketTimeout);
                                    sock.setTcpNoDelay(true); // we buffer anyway, so improve latency
                                    handleConnection(sock.getInputStream(), sock.getOutputStream(), sock);
                                } finally {
                                    try {
                                        // RFC7230#6.6 - close socket gracefully
                                        // (except SSL socket which doesn't support half-closing)
                                        if (!(sock instanceof SSLSocket)) {
                                            sock.shutdownOutput(); // half-close socket (only output)
                                            transfer(sock.getInputStream(), null, -1); // consume input
                                        }
                                    } finally {
                                        sock.close(); // and finally close socket fully
                                    }
                                }
                            } catch (IOException ignore) {}
                        }
                    });
                }
            } catch (IOException ignore) {}
        }
    }

    protected volatile int port;
    protected volatile String host;
    protected volatile int socketTimeout = 10000;
    protected volatile ServerSocketFactory serverSocketFactory;
    protected volatile boolean secure;
    protected volatile Executor executor;
    protected volatile ServerSocket serv;
    protected final Map<String, VirtualHost> hosts = new ConcurrentHashMap<String, VirtualHost>();

    /**
     * Constructs an HTTPServer which can accept connections on the given port.
     * Note: the {@link #start()} method must be called to start accepting
     * connections.
     *
     * @param port the port on which this server will accept connections
     */
    public HTTPServer(int port) {
        setPort(port);
        addVirtualHost(new VirtualHost(null)); // add default virtual host
    }
    /**
     * Constructs an HTTPServer which can accept connections on the given port.
     * Note: the {@link #start()} method must be called to start accepting
     * connections.
     *
     * @param port the port on which this server will accept connections
     * @param host only accept request from this host .if host check fail, will
     *             work like  HTTPServer(int port)
     */
    public HTTPServer(String host,int port) {
        setPort(port);
        setHost(host);
        addVirtualHost(new VirtualHost(null)); // add default virtual host
    }
    /**
     * Constructs an HTTPServer which can accept connections on the default HTTP port 80.
     * Note: the {@link #start()} method must be called to start accepting connections.
     */
    public HTTPServer() {
        this(80);
    }

    /**
     * Sets the port on which this server will accept connections.
     *
     * @param port the port on which this server will accept connections
     */
    public void setPort(int port) {
        this.port = port;
    }
    public void setHost(String host) {
        this.host = host;
    }
    /**
     * Sets the factory used to create the server socket.
     * If null or not set, the default {@link ServerSocketFactory#getDefault()} is used.
     * For secure sockets (HTTPS), use an SSLServerSocketFactory instance.
     * The port should usually also be changed for HTTPS, e.g. port 443 instead of 80.
     * <p>
     * If using the default SSLServerSocketFactory returned by
     * {@link SSLServerSocketFactory#getDefault()}, the appropriate system properties
     * must be set to configure the default JSSE provider, such as
     * {@code javax.net.ssl.keyStore} and {@code javax.net.ssl.keyStorePassword}.
     *
     * @param factory the server socket factory to use
     */
    public void setServerSocketFactory(ServerSocketFactory factory) {
        this.serverSocketFactory = factory;
        this.secure = factory instanceof SSLServerSocketFactory;
    }

    /**
     * Sets the socket timeout for established connections.
     *
     * @param timeout the socket timeout in milliseconds
     */
    public void setSocketTimeout(int timeout) { this.socketTimeout = timeout; }

    /**
     * Sets the executor used in servicing HTTP connections.
     * If null, a default executor is used. The caller is responsible
     * for shutting down the provided executor when necessary.
     *
     * @param executor the executor to use
     */
    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    /**
     * Returns the virtual host with the given name.
     *
     * @param name the name of the virtual host to return,
     *        or null for the default virtual host
     * @return the virtual host with the given name, or null if it doesn't exist
     */
    public VirtualHost getVirtualHost(String name) {
        return hosts.get(name == null ? "" : name);
    }

    /**
     * Returns all virtual hosts.
     *
     * @return all virtual hosts (as an unmodifiable set)
     */
    public Set<VirtualHost> getVirtualHosts() {
        return Collections.unmodifiableSet(new HashSet<VirtualHost>(hosts.values()));
    }

    /**
     * Adds the given virtual host to the server.
     * If the host's name or aliases already exist, they are overwritten.
     *
     * @param host the virtual host to add
     */
    public void addVirtualHost(VirtualHost host) {
        String name = host.getName();
        hosts.put(name == null ? "" : name, host);
    }

    /**
     * Creates the server socket used to accept connections, using the configured
     * {@link #setServerSocketFactory ServerSocketFactory} and {@link #setPort port}.
     * <p>
     * Cryptic errors seen here often mean the factory configuration details are wrong.
     *
     * @return the created server socket
     * @throws IOException if the socket cannot be created
     */
    protected ServerSocket createServerSocket() throws IOException {
        ServerSocket serv = serverSocketFactory.createServerSocket();
        serv.setReuseAddress(true);
        InetSocketAddress address = null;
        if (host==null){
            address=new InetSocketAddress(port);
        }else{
            address=new InetSocketAddress(host,port);
        }
        serv.bind(address);

        return serv;
    }

    /**
     * Starts this server. If it is already started, does nothing.
     * Note: Once the server is started, configuration-altering methods
     * of the server and its virtual hosts must not be used. To modify the
     * configuration, the server must first be stopped.
     *
     * @throws IOException if the server cannot begin accepting connections
     */
    public synchronized void start() throws IOException {
        if (serv != null)
            return;
        if (serverSocketFactory == null) // assign default server socket factory if needed
            serverSocketFactory = ServerSocketFactory.getDefault(); // plain sockets
        serv = createServerSocket();
        if (executor == null) // assign default executor if needed
            executor = Executors.newCachedThreadPool(); // consumes no resources when idle
        // register all host aliases (which may have been modified)
        for (VirtualHost host : getVirtualHosts())
            for (String alias : host.getAliases())
                hosts.put(alias, host);
        // start handling incoming connections
        new SocketHandlerThread().start();
    }

    /**
     * Stops this server. If it is already stopped, does nothing.
     * Note that if an {@link #setExecutor Executor} was set, it must be closed separately.
     */
    public synchronized void stop() {
        try {
            if (serv != null)
                serv.close();
        } catch (IOException ignore) {}
        serv = null;
    }

    /**
     * Handles communications for a single connection over the given streams.
     * Multiple subsequent transactions are handled on the connection,
     * until the streams are closed, an error occurs, or the request
     * contains a "Connection: close" header which explicitly requests
     * the connection be closed after the transaction ends.
     *
     * @param in the stream from which the incoming requests are read
     * @param out the stream into which the outgoing responses are written
     * @param sock the connected socket
     * @throws IOException if an error occurs
     */
    protected void handleConnection(InputStream in, OutputStream out, Socket sock) throws IOException {
        in = new BufferedInputStream(in, 4096);
        out = new BufferedOutputStream(out, 4096);
        Request req;
        Response resp;
        do {
            // create request and response and handle transaction
            req = null;
            resp = new Response(out);
            try {
                req = new Request(in, sock);
                handleTransaction(req, resp);
            } catch (Throwable t) { // unhandled errors (not normal error responses like 404)
                if (req == null) { // error reading request
                    if (t instanceof IOException && t.getMessage().contains("missing request line"))
                        break; // we're not in the middle of a transaction - so just disconnect
                    resp.getHeaders().add("Connection", "close"); // about to close connection
                    if (t instanceof InterruptedIOException) // e.g. SocketTimeoutException
                        resp.sendError(408, "Timeout waiting for client request");
                    else
                        resp.sendError(400, "Invalid request: " + t.getMessage());
                } else if (!resp.headersSent()) { // if headers were not already sent, we can send an error response
                    resp = new Response(out); // ignore whatever headers may have already been set
                    resp.getHeaders().add("Connection", "close"); // about to close connection
                    resp.sendError(500, "Error processing request: " + t.getMessage());
                } // otherwise just abort the connection since we can't recover
                break; // proceed to close connection
            } finally {
                resp.close(); // close response and flush output
            }
            // consume any leftover body data so next request can be processed
            transfer(req.getBody(), null, -1);
            // RFC7230#6.6: persist connection unless client or server close explicitly (or legacy client)
        } while (!"close".equalsIgnoreCase(req.getHeaders().get("Connection"))
            && !"close".equalsIgnoreCase(resp.getHeaders().get("Connection")) && req.getVersion().endsWith("1.1"));
    }

    /**
     * Handles a single transaction on a connection.
     * <p>
     * Subclasses can override this method to perform filtering on the
     * request or response, apply wrappers to them, or further customize
     * the transaction processing in some other way.
     *
     * @param req the transaction request
     * @param resp the transaction response (into which the response is written)
     * @throws IOException if and error occurs
     */
    protected void handleTransaction(Request req, Response resp) throws IOException {
        resp.setClientCapabilities(req);
        if (preprocessTransaction(req, resp))
            handleMethod(req, resp);
    }

    /**
     * Preprocesses a transaction, performing various validation checks
     * and required special header handling, possibly returning an
     * appropriate response.
     *
     * @param req the request
     * @param resp the response
     * @return whether further processing should be performed on the transaction
     * @throws IOException if an error occurs
     */
    protected boolean preprocessTransaction(Request req, Response resp) throws IOException {
        Headers reqHeaders = req.getHeaders();
        // validate request
        String version = req.getVersion();
        if (version.equals("HTTP/1.1")) {
            if (!reqHeaders.contains("Host")) {
                // RFC2616#14.23: missing Host header gets 400
                resp.sendError(400, "Missing required Host header");
                return false;
            }
            // return a continue response before reading body
            String expect = reqHeaders.get("Expect");
            if (expect != null) {
                if (expect.equalsIgnoreCase("100-continue")) {
                    Response tempResp = new Response(resp.getOutputStream());
                    tempResp.sendHeaders(100);
                    resp.getOutputStream().flush();
                } else {
                    // RFC2616#14.20: if unknown expect, send 417
                    resp.sendError(417);
                    return false;
                }
            }
        } else if (version.equals("HTTP/1.0") || version.equals("HTTP/0.9")) {
            // RFC2616#14.10 - remove connection headers from older versions
            for (String token : splitElements(reqHeaders.get("Connection"), false))
                reqHeaders.remove(token);
        } else {
            resp.sendError(400, "Unknown version: " + version);
            return false;
        }
        return true;
    }

    /**
     * Handles a transaction according to the request method.
     *
     * @param req the transaction request
     * @param resp the transaction response (into which the response is written)
     * @throws IOException if and error occurs
     */
    protected void handleMethod(Request req, Response resp) throws IOException {
        //todo: 去掉 methods 相关的限制 by noear,2022-12-08
        String method = req.getMethod();

        if (method.equals("TRACE")) { // default TRACE handler
            handleTrace(req, resp);
        }else {
            serve(req, resp); // method is handled by context handler (or 404)
        }

        //todo: 这段代码，没机会进入了 by noear,2022-12-08
        /*
        // RFC 2616#5.1.1 - GET and HEAD must be supported
        if (method.equals("HEAD")) { // default HEAD handler
            req.method = "GET"; // identical to a GET
            resp.setDiscardBody(true); // process normally but discard body
            serve(req, resp);
        } else if (method.equals("TRACE")) { // default TRACE handler
            handleTrace(req, resp);
        } else {



            // "*" is a special server-wide (no-context) request supported by OPTIONS
            boolean isOptions = method.equals("OPTIONS");

            if(isOptions) {
                Set<String> methods = new LinkedHashSet<String>();
                methods.addAll(Arrays.asList("GET", "HEAD", "TRACE", "OPTIONS")); // built-in methods
                Map<String, ContextHandler> handlers = req.getContext().getHandlers();
                methods.addAll(isOptions ? req.getVirtualHost().getMethods() : handlers.keySet());
                resp.getHeaders().add("Allow", join(", ", methods));
            }

            if (isOptions) { // default OPTIONS handler
                resp.getHeaders().add("Content-Length", "0"); // RFC2616#9.2
                resp.sendHeaders(200);
            } else if (req.getVirtualHost().getMethods().contains(method)) {
                resp.sendHeaders(405); // supported by server, but not this context (nor built-in)
            } else {
                resp.sendError(501); // unsupported method
            }
        }
        */
    }

    /**
     * Handles a TRACE method request.
     *
     * @param req the request
     * @param resp the response into which the content is written
     * @throws IOException if an error occurs
     */
    public void handleTrace(Request req, Response resp) throws IOException {
        resp.sendHeaders(200, -1, -1, null, "message/http", null);
        OutputStream out = resp.getBody();
        out.write(getBytes("TRACE ", req.getURI().toString(), " ", req.getVersion()));
        out.write(CRLF);
        req.getHeaders().writeTo(out);
        transfer(req.getBody(), out, -1);
    }

    /**
     * Serves the content for a request by invoking the context
     * handler for the requested context (path) and HTTP method.
     *
     * @param req the request
     * @param resp the response into which the content is written
     * @throws IOException if an error occurs
     */
    protected void serve(Request req, Response resp) throws IOException {
        // get context handler to handle request
        ContextHandler handler = req.getContext().getHandlers().get("*");
        if (handler == null) {
            resp.sendError(404);
            return;
        }
        // serve request
        int status = handler.serve(req, resp);

        //todo: 不支持目录首页 by noear,2022-12-08

        // add directory index if necessary
        /*String path = req.getPath();
        if (path.endsWith("/")) {
            String index = req.getVirtualHost().getDirectoryIndex();
            if (index != null) {
                req.setPath(path + index);
                status = handler.serve(req, resp);
                req.setPath(path);
            }
        }
        if (status == 404)
            status = handler.serve(req, resp);*/

        if (status > 0)
            resp.sendError(status);
    }

    /**
     * Adds a Content-Type mapping for the given path suffixes.
     * If any of the path suffixes had a previous Content-Type associated
     * with it, it is replaced with the given one. Path suffixes are
     * considered case-insensitive, and contentType is converted to lowercase.
     *
     * @param contentType the content type (MIME type) to be associated with
     *        the given path suffixes
     * @param suffixes the path suffixes which will be associated with
     *        the contentType, e.g. the file extensions of served files
     *        (excluding the '.' character)
     */
    public static void addContentType(String contentType, String... suffixes) {
        for (String suffix : suffixes)
            contentTypes.put(suffix.toLowerCase(Locale.US), contentType.toLowerCase(Locale.US));
    }

    /**
     * Adds Content-Type mappings from a standard mime.types file.
     *
     * @param in a stream containing a mime.types file
     * @throws IOException if an error occurs
     * @throws FileNotFoundException if the file is not found or cannot be read
     */
    public static void addContentTypes(InputStream in) throws IOException {
        try {
            while (true) {
                String line = readLine(in).trim(); // throws EOFException when done
                if (line.length() > 0 && line.charAt(0) != '#') {
                    String[] tokens = split(line, " \t", -1);
                    for (int i = 1; i < tokens.length; i++)
                        addContentType(tokens[0], tokens[i]);
                }
            }
        } catch (EOFException ignore) { // the end of file was reached - it's ok
        } finally {
            in.close();
        }
    }

    /**
     * Returns the content type for the given path, according to its suffix,
     * or the given default content type if none can be determined.
     *
     * @param path the path whose content type is requested
     * @param def a default content type which is returned if none can be
     *        determined
     * @return the content type for the given path, or the given default
     */
    public static String getContentType(String path, String def) {
        int dot = path.lastIndexOf('.');
        String type = dot < 0 ? def : contentTypes.get(path.substring(dot + 1).toLowerCase(Locale.US));
        return type != null ? type : def;
    }

    /**
     * Checks whether data of the given content type (MIME type) is compressible.
     *
     * @param contentType the content type
     * @return true if the data is compressible, false if not
     */
    public static boolean isCompressible(String contentType) {
        int pos = contentType.indexOf(';'); // exclude params
        String ct = pos < 0 ? contentType : contentType.substring(0, pos);
        for (String s : compressibleContentTypes)
            if (s.equals(ct) || s.charAt(0) == '*' && ct.endsWith(s.substring(1))
                || s.charAt(s.length() - 1) == '*' && ct.startsWith(s.substring(0, s.length() - 1)))
                    return true;
        return false;
    }

    /**
     * Returns the local host's auto-detected name.
     *
     * @return the local host name
     */
    public static String detectLocalHostName() {
        try {
            return InetAddress.getLocalHost().getCanonicalHostName();
        } catch (UnknownHostException uhe) {
            return "localhost";
        }
    }

    /**
     * Parses name-value pair parameters from the given "x-www-form-urlencoded"
     * MIME-type string. This is the encoding used both for parameters passed
     * as the query of an HTTP GET method, and as the content of HTML forms
     * submitted using the HTTP POST method (as long as they use the default
     * "application/x-www-form-urlencoded" encoding in their ENCTYPE attribute).
     * UTF-8 encoding is assumed.
     * <p>
     * The parameters are returned as a list of string arrays, each containing
     * the parameter name as the first element and its corresponding value
     * as the second element (or an empty string if there is no value).
     * <p>
     * The list retains the original order of the parameters.
     *
     * @param s an "application/x-www-form-urlencoded" string
     * @return the parameter name-value pairs parsed from the given string,
     *         or an empty list if there are none
     */
    public static List<String[]> parseParamsList(String s) {
        if (s == null || s.length() == 0)
            return Collections.emptyList();
        List<String[]> params = new ArrayList<String[]>(8);
        for (String pair : split(s, "&", -1)) {
            int pos = pair.indexOf('=');
            String name = pos < 0 ? pair : pair.substring(0, pos);
            String val = pos < 0 ? "" : pair.substring(pos + 1);
            try {
                name = URLDecoder.decode(name.trim(), "UTF-8");
                val = URLDecoder.decode(val.trim(), "UTF-8");
                if (name.length() > 0)
                    params.add(new String[] { name, val });
            } catch (UnsupportedEncodingException ignore) {} // never thrown
        }
        return params;
    }

    /**
     * Converts a collection of pairs of objects (arrays of size two,
     * each representing a key and corresponding value) into a Map.
     * Duplicate keys are ignored (only the first occurrence of each key is considered).
     * The map retains the original collection's iteration order.
     *
     * @param pairs a collection of arrays, each containing a key and corresponding value
     * @param <K> the key type
     * @param <V> the value type
     * @return a map containing the paired keys and values, or an empty map
     */
    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> toMap(Collection<? extends Object[]> pairs) {
        if (pairs == null || pairs.isEmpty())
            return Collections.emptyMap();
        Map<K, V> map = new LinkedHashMap<K, V>(pairs.size());
        for (Object[] pair : pairs)
            if (!map.containsKey(pair[0]))
                map.put((K)pair[0], (V)pair[1]);
        return map;
    }

    /**
     * Returns the absolute (zero-based) content range value specified
     * by the given range string. If multiple ranges are requested, a single
     * range containing all of them is returned.
     *
     * @param range the string containing the range description
     * @param length the full length of the requested resource
     * @return the requested range, or null if the range value is invalid
     */
    public static long[] parseRange(String range, long length) {
        long min = Long.MAX_VALUE;
        long max = Long.MIN_VALUE;
        try {
            for (String token : splitElements(range, false)) {
                long start, end;
                int dash = token.indexOf('-');
                if (dash == 0) { // suffix range
                    start = length - parseULong(token.substring(1), 10);
                    end = length - 1;
                } else if (dash == token.length() - 1) { // open range
                    start = parseULong(token.substring(0, dash), 10);
                    end = length - 1;
                } else { // explicit range
                    start = parseULong(token.substring(0, dash), 10);
                    end = parseULong(token.substring(dash + 1), 10);
                }
                if (end < start)
                    throw new RuntimeException();
                if (start < min)
                    min = start;
                if (end > max)
                    max = end;
            }
            if (max < 0) // no tokens
                throw new RuntimeException();
            if (max >= length && min < length)
                max = length - 1;
            return new long[] { min, max }; // start might be >= length!
        } catch (RuntimeException re) { // NFE, IOOBE or explicit RE
            return null; // RFC2616#14.35.1 - ignore header if invalid
        }
    }

    /**
     * Parses an unsigned long value. This method behaves the same as calling
     * {@link Long#parseLong(String, int)}, but considers the string invalid
     * if it starts with an ASCII minus sign ('-') or plus sign ('+').
     *
     * @param s the String containing the long representation to be parsed
     * @param radix the radix to be used while parsing s
     * @return the long represented by s in the specified radix
     * @throws NumberFormatException if the string does not contain a parsable
     *         long, or if it starts with an ASCII minus sign or plus sign
     */
    public static long parseULong(String s, int radix) throws NumberFormatException {
        long val = Long.parseLong(s, radix); // throws NumberFormatException
        if (s.charAt(0) == '-' || s.charAt(0) == '+')
            throw new NumberFormatException("invalid digit: " + s.charAt(0));
        return val;
    }

    /**
     * Parses a date string in one of the supported {@link #DATE_PATTERNS}.
     * <p>
     * Received date header values must be in one of the following formats:
     * Sun, 06 Nov 1994 08:49:37 GMT  ; RFC 822, updated by RFC 1123
     * Sunday, 06-Nov-94 08:49:37 GMT ; RFC 850, obsoleted by RFC 1036
     * Sun Nov  6 08:49:37 1994       ; ANSI C's asctime() format
     *
     * @param time a string representation of a time value
     * @return the parsed date value
     * @throws IllegalArgumentException if the given string does not contain
     *         a valid date format in any of the supported formats
     */
    public static Date parseDate(String time) {
        for (String pattern : DATE_PATTERNS) {
            try {
                SimpleDateFormat df = new SimpleDateFormat(pattern, Locale.US);
                df.setLenient(false);
                df.setTimeZone(GMT);
                return df.parse(time);
            } catch (ParseException ignore) {}
        }
        throw new IllegalArgumentException("invalid date format: " + time);
    }

    /**
     * Formats the given time value as a string in RFC 1123 format.
     *
     * @param time the time in milliseconds since January 1, 1970, 00:00:00 GMT
     * @return the given time value as a string in RFC 1123 format
     */
    public static String formatDate(long time) {
        // this implementation performs far better than SimpleDateFormat instances, and even
        // quite better than ThreadLocal SDFs - the server's CPU-bound benchmark gains over 20%!
        if (time < -62167392000000L || time > 253402300799999L)
            throw new IllegalArgumentException("year out of range (0001-9999): " + time);
        char[] s = "DAY, 00 MON 0000 00:00:00 GMT".toCharArray(); // copy the format template
        Calendar cal = new GregorianCalendar(GMT, Locale.US);
        cal.setTimeInMillis(time);
        System.arraycopy(DAYS, 4 * (cal.get(Calendar.DAY_OF_WEEK) - 1), s, 0, 3);
        System.arraycopy(MONTHS, 4 * cal.get(Calendar.MONTH), s, 8, 3);
        int n = cal.get(Calendar.DATE);    s[5]  += n / 10;      s[6]  += n % 10;
        n = cal.get(Calendar.YEAR);        s[12] += n / 1000;    s[13] += n / 100 % 10;
                                           s[14] += n / 10 % 10; s[15] += n % 10;
        n = cal.get(Calendar.HOUR_OF_DAY); s[17] += n / 10;      s[18] += n % 10;
        n = cal.get(Calendar.MINUTE);      s[20] += n / 10;      s[21] += n % 10;
        n = cal.get(Calendar.SECOND);      s[23] += n / 10;      s[24] += n % 10;
        return new String(s);
    }

    /**
     * Splits the given element list string (comma-separated header value)
     * into its constituent non-empty trimmed elements.
     * (RFC2616#2.1: element lists are delimited by a comma and optional LWS,
     * and empty elements are ignored).
     *
     * @param list the element list string
     * @param lower specifies whether the list elements should be lower-cased
     * @return the non-empty elements in the list, or an empty array
     */
    public static String[] splitElements(String list, boolean lower) {
        return split(lower && list != null ? list.toLowerCase(Locale.US) : list, ",", -1);
    }

    /**
     * Splits the given string into its constituent non-empty trimmed elements,
     * which are delimited by any of the given delimiter characters.
     * This is a more direct and efficient implementation than using a regex
     * (e.g. String.split()), trimming the elements and removing empty ones.
     *
     * @param str the string to split
     * @param delimiters the characters used as the delimiters between elements
     * @param limit if positive, limits the returned array size (remaining of str in last element)
     * @return the non-empty elements in the string, or an empty array
     */
    public static String[] split(String str, String delimiters, int limit) {
        if (str == null)
            return new String[0];
        Collection<String> elements = new ArrayList<String>();
        int len = str.length();
        int start = 0;
        int end;
        while (start < len) {
            for (end = --limit == 0 ? len : start;
                 end < len && delimiters.indexOf(str.charAt(end)) < 0; end++);
            String element = str.substring(start, end).trim();
            if (element.length() > 0)
                elements.add(element);
            start = end + 1;
        }
        return elements.toArray(new String[0]);
    }

    /**
     * Returns a string constructed by joining the string representations of the
     * iterated objects (in order), with the delimiter inserted between them.
     *
     * @param delim the delimiter that is inserted between the joined strings
     * @param items the items whose string representations are joined
     * @param <T> the item type
     * @return the joined string
     */
    public static <T> String join(String delim, Iterable<T> items) {
        StringBuilder sb = new StringBuilder();
        for (Iterator<T> it = items.iterator(); it.hasNext(); )
            sb.append(it.next()).append(it.hasNext() ? delim : "");
        return sb.toString();
    }

    /**
     * Returns the parent of the given path.
     *
     * @param path the path whose parent is returned (must start with '/')
     * @return the parent of the given path (excluding trailing slash),
     *         or null if given path is the root path
     */
    public static String getParentPath(String path) {
        path = trimRight(path, '/'); // remove trailing slash
        int slash = path.lastIndexOf('/');
        return slash < 0 ? null : path.substring(0, slash);
    }

    /**
     * Returns the given string with all occurrences of the given character
     * removed from its right side.
     *
     * @param s the string to trim
     * @param c the character to remove
     * @return the trimmed string
     */
    public static String trimRight(String s, char c) {
        int len = s.length() - 1;
        int end;
        for (end = len; end >= 0 && s.charAt(end) == c; end--);
        return end == len ? s : s.substring(0, end + 1);
    }

    /**
     * Returns the given string with all occurrences of the given character
     * removed from its left side.
     *
     * @param s the string to trim
     * @param c the character to remove
     * @return the trimmed string
     */
    public static String trimLeft(String s, char c) {
        int len = s.length();
        int start;
        for (start = 0; start < len && s.charAt(start) == c; start++);
        return start == 0 ? s : s.substring(start);
    }

    /**
     * Trims duplicate consecutive occurrences of the given character within the
     * given string, replacing them with a single instance of the character.
     *
     * @param s the string to trim
     * @param c the character to trim
     * @return the given string with duplicate consecutive occurrences of c
     *         replaced by a single instance of c
     */
    public static String trimDuplicates(String s, char c) {
        int start = 0;
        while ((start = s.indexOf(c, start) + 1) > 0) {
            int end;
            for (end = start; end < s.length() && s.charAt(end) == c; end++);
            if (end > start)
                s = s.substring(0, start) + s.substring(end);
        }
        return s;
    }

    /**
     * Returns a human-friendly string approximating the given data size,
     * e.g. "316", "1.8K", "324M", etc.
     *
     * @param size the size to display
     * @return a human-friendly string approximating the given data size
     */
    public static String toSizeApproxString(long size) {
        final char[] units = { ' ', 'K', 'M', 'G', 'T', 'P', 'E' };
        int u;
        double s;
        for (u = 0, s = size; s >= 1000; u++, s /= 1024);
        return String.format(s < 10 ? "%.1f%c" : "%.0f%c", s, units[u]);
    }

    /**
     * Returns an HTML-escaped version of the given string for safe display
     * within a web page. The characters '&amp;', '&gt;' and '&lt;' must always
     * be escaped, and single and double quotes must be escaped within
     * attribute values; this method escapes them always. This method can
     * be used for generating both HTML and XHTML valid content.
     *
     * @param s the string to escape
     * @return the escaped string
     * @see <a href="http://www.w3.org/International/questions/qa-escapes">The W3C FAQ</a>
     */
    public static String escapeHTML(String s) {
        int len = s.length();
        StringBuilder sb = new StringBuilder(len + 30);
        int start = 0;
        for (int i = 0; i < len; i++) {
            String ref = null;
            switch (s.charAt(i)) {
                case '&': ref = "&amp;"; break;
                case '>': ref = "&gt;"; break;
                case '<': ref = "&lt;"; break;
                case '"': ref = "&quot;"; break;
                case '\'': ref = "&#39;"; break;
            }
            if (ref != null) {
                sb.append(s.substring(start, i)).append(ref);
                start = i + 1;
            }
        }
        return start == 0 ? s : sb.append(s.substring(start)).toString();
    }

    /**
     * Converts strings to bytes by casting the chars to bytes.
     * This is a fast way to encode a string as ISO-8859-1/US-ASCII bytes.
     * If multiple strings are provided, their bytes are concatenated.
     *
     * @param strings the strings to convert (containing only ISO-8859-1 chars)
     * @return the byte array
     */
    public static byte[] getBytes(String... strings) {
        int n = 0;
        for (String s : strings)
            n += s.length();
        byte[] b = new byte[n];
        n = 0;
        for (String s : strings)
            for (int i = 0, len = s.length(); i < len; i++)
                b[n++] = (byte)s.charAt(i);
        return b;
    }

    /**
     * Transfers data from an input stream to an output stream.
     *
     * @param in the input stream to transfer from
     * @param out the output stream to transfer to (or null to discard output)
     * @param len the number of bytes to transfer. If negative, the entire
     *        contents of the input stream are transferred.
     * @throws IOException if an IO error occurs or the input stream ends
     *         before the requested number of bytes have been read
     */
    public static void transfer(InputStream in, OutputStream out, long len) throws IOException {
        if (len == 0 || out == null && len < 0 && in.read() < 0)
            return; // small optimization - avoid buffer creation
        byte[] buf = new byte[4096];
        while (len != 0) {
            int count = len < 0 || buf.length < len ? buf.length : (int)len;
            count = in.read(buf, 0, count);
            if (count < 0) {
                if (len > 0)
                    throw new IOException("unexpected end of stream");
                break;
            }
            if (out != null)
                out.write(buf, 0, count);
            len -= len > 0 ? count : 0;
        }
    }

    /**
     * Reads the token starting at the current stream position and ending at
     * the first occurrence of the given delimiter byte, in the given encoding.
     * If LF is specified as the delimiter, a CRLF pair is also treated as one.
     *
     * @param in the stream from which the token is read
     * @param delim the byte value which marks the end of the token,
     *        or -1 if the token ends at the end of the stream
     * @param enc a character-encoding name
     * @param maxLength the maximum length (in bytes) to read
     * @return the read token, excluding the delimiter
     * @throws UnsupportedEncodingException if the encoding is not supported
     * @throws EOFException if the stream end is reached before a delimiter is found
     * @throws IOException if an IO error occurs, or the maximum length
     *         is reached before the token end is reached
     */
    public static String readToken(InputStream in, int delim,
            String enc, int maxLength) throws IOException {
        // note: we avoid using a ByteArrayOutputStream here because it
        // suffers the overhead of synchronization for each byte written
        int b;
        int len = 0; // buffer length
        int count = 0; // number of read bytes
        byte[] buf = null; // optimization - lazy allocation only if necessary
        while ((b = in.read()) != -1 && b != delim) {
            if (count == len) { // expand buffer
                if (count == maxLength)
                    throw new IOException("token too large (" + count + ")");
                len = len > 0 ? 2 * len : 256; // start small, double each expansion
                len = maxLength < len ? maxLength : len;
                byte[] expanded = new byte[len];
                if (buf != null)
                    System.arraycopy(buf, 0, expanded, 0, count);
                buf = expanded;
            }
            buf[count++] = (byte)b;
        }
        if (b < 0 && delim != -1)
            throw new EOFException("unexpected end of stream");
        if (delim == '\n' && count > 0 && buf[count - 1] == '\r')
            count--;
        return count > 0 ? new String(buf, 0, count, enc) : "";
    }

    /**
     * Reads the ISO-8859-1 encoded string starting at the current stream
     * position and ending at the first occurrence of the LF character.
     *
     * @param in the stream from which the line is read
     * @return the read string, excluding the terminating LF character
     *         and (if exists) the CR character immediately preceding it
     * @throws EOFException if the stream end is reached before an LF character is found
     * @throws IOException if an IO error occurs, or the line is longer than 8192 bytes
     * @see #readToken(InputStream, int, String, int)
     */
    public static String readLine(InputStream in) throws IOException {
        //todo: update utf-8 by noear, 2021-11-11
        return readToken(in, '\n', "UTF-8", MAX_HEADER_SIZE);
    }

    /**
     * Reads headers from the given stream. Headers are read according to the
     * RFC, including folded headers, element lists, and multiple headers
     * (which are concatenated into a single element list header).
     * Leading and trailing whitespace is removed.
     *
     * @param in the stream from which the headers are read
     * @return the read headers (possibly empty, if none exist)
     * @throws IOException if an IO error occurs or the headers are malformed
     *         or there are more than 100 header lines
     */
    public static Headers readHeaders(InputStream in) throws IOException {
        Headers headers = new Headers();
        String line;
        String prevLine = "";
        int count = 0;
        while ((line = readLine(in)).length() > 0) {
            int start; // start of line data (after whitespace)
            for (start = 0; start < line.length() &&
                Character.isWhitespace(line.charAt(start)); start++);
            if (start > 0) // unfold header continuation line
                line = prevLine + ' ' + line.substring(start);
            int separator = line.indexOf(':');
            if (separator < 0)
                throw new IOException("invalid header: \"" + line + "\"");
            String name = line.substring(0, separator);
            String value = line.substring(separator + 1).trim(); // ignore LWS
            Header replaced = headers.replace(name, value);
            // concatenate repeated headers (distinguishing repeated from folded)
            if (replaced != null && start == 0) {
                value = replaced.getValue() + ", " + value;
                line = name + ": " + value;
                headers.replace(name, value);
            }
            prevLine = line;
            if (++count > 100)
                throw new IOException("too many header lines");
        }
        return headers;
    }

    /**
     * Matches the given ETag value against the given ETags. A match is found
     * if the given ETag is not null, and either the ETags contain a "*" value,
     * or one of them is identical to the given ETag. If strong comparison is
     * used, tags beginning with the weak ETag prefix "W/" never match.
     * See RFC2616#3.11, RFC2616#13.3.3.
     *
     * @param strong if true, strong comparison is used, otherwise weak
     *        comparison is used
     * @param etags the ETags to match against
     * @param etag the ETag to match
     * @return true if the ETag is matched, false otherwise
     */
    public static boolean match(boolean strong, String[] etags, String etag) {
        if (etag == null || strong && etag.startsWith("W/"))
            return false;
        for (String e : etags)
            if (e.equals("*") || (e.equals(etag) && !(strong && (e.startsWith("W/")))))
                return true;
        return false;
    }

    /**
     * Calculates the appropriate response status for the given request and
     * its resource's last-modified time and ETag, based on the conditional
     * headers present in the request.
     *
     * @param req the request
     * @param lastModified the resource's last modified time
     * @param etag the resource's ETag
     * @return the appropriate response status for the request
     */
    public static int getConditionalStatus(Request req, long lastModified, String etag) {
        Headers headers = req.getHeaders();
        // If-Match
        String header = headers.get("If-Match");
        if (header != null && !match(true, splitElements(header, false), etag))
            return 412;
        // If-Unmodified-Since
        Date date = headers.getDate("If-Unmodified-Since");
        if (date != null && lastModified > date.getTime())
            return 412;
        // If-Modified-Since
        int status = 200;
        boolean force = false;
        date = headers.getDate("If-Modified-Since");
        if (date != null && date.getTime() <= System.currentTimeMillis()) {
            if (lastModified > date.getTime())
                force = true;
            else
                status = 304;
        }
        // If-None-Match
        header = headers.get("If-None-Match");
        if (header != null) {
            if (match(false, splitElements(header, false), etag)) // RFC7232#3.2: use weak matching
                status = req.getMethod().equals("GET")
                    || req.getMethod().equals("HEAD") ? 304 : 412;
            else
                force = true;
        }
        return force ? 200 : status;
    }

    /**
     * Serves a context's contents from a file based resource.
     * <p>
     * The file is located by stripping the given context prefix from
     * the request's path, and appending the result to the given base directory.
     * <p>
     * Missing, forbidden and otherwise invalid files return the appropriate
     * error response. Directories are served as an HTML index page if the
     * virtual host allows one, or a forbidden error otherwise. Files are
     * sent with their corresponding content types, and handle conditional
     * and partial retrievals according to the RFC.
     *
     * @param base the base directory to which the context is mapped
     * @param context the context which is mapped to the base directory
     * @param req the request
     * @param resp the response into which the content is written
     * @return the HTTP status code to return, or 0 if a response was sent
     * @throws IOException if an error occurs
     */
    public static int serveFile(File base, String context,
            Request req, Response resp) throws IOException {
        String relativePath = req.getPath().substring(context.length());
        File file = new File(base, relativePath).getCanonicalFile();
        if (!file.exists() || file.isHidden() || file.getName().startsWith(".")) {
            return 404;
        } else if (!file.canRead() || !file.getPath().startsWith(base.getPath())) { // validate
            return 403;
        } else if (file.isDirectory()) {
            if (relativePath.endsWith("/")) {
                if (!req.getVirtualHost().isAllowGeneratedIndex())
                    return 403;
                resp.send(200, createIndex(file, req.getPath()));
            } else { // redirect to the normalized directory URL ending with '/'
                resp.redirect(req.getBaseURL() + req.getPath() + "/", true);
            }
        } else if (relativePath.endsWith("/")) {
            return 404; // non-directory ending with slash (File constructor removed it)
        } else {
            serveFileContent(file, req, resp);
        }
        return 0;
    }

    /**
     * Serves the contents of a file, with its corresponding content type,
     * last modification time, etc. conditional and partial retrievals are
     * handled according to the RFC.
     *
     * @param file the existing and readable file whose contents are served
     * @param req the request
     * @param resp the response into which the content is written
     * @throws IOException if an error occurs
     */
    public static void serveFileContent(File file, Request req, Response resp) throws IOException {
        long len = file.length();
        long lastModified = file.lastModified();
        String etag = "W/\"" + lastModified + "\""; // a weak tag based on date
        int status = 200;
        // handle range or conditional request
        long[] range = req.getRange(len);
        if (range == null || len == 0) {
            status = getConditionalStatus(req, lastModified, etag);
        } else {
            String ifRange = req.getHeaders().get("If-Range");
            if (ifRange == null) {
                if (range[0] >= len)
                    status = 416; // unsatisfiable range
                else
                    status = getConditionalStatus(req, lastModified, etag);
            } else if (range[0] >= len) {
                // RFC2616#14.16, 10.4.17: invalid If-Range gets everything
                range = null;
            } else { // send either range or everything
                if (!ifRange.startsWith("\"") && !ifRange.startsWith("W/")) {
                    Date date = req.getHeaders().getDate("If-Range");
                    if (date != null && lastModified > date.getTime())
                        range = null; // modified - send everything
                } else if (!ifRange.equals(etag)) {
                    range = null; // modified - send everything
                }
            }
        }
        // send the response
        Headers respHeaders = resp.getHeaders();
        switch (status) {
            case 304: // no other headers or body allowed
                respHeaders.add("ETag", etag);
                respHeaders.add("Vary", "Accept-Encoding");
                respHeaders.add("Last-Modified", formatDate(lastModified));
                resp.sendHeaders(304);
                break;
            case 412:
                resp.sendHeaders(412);
                break;
            case 416:
                respHeaders.add("Content-Range", "bytes */" + len);
                resp.sendHeaders(416);
                break;
            case 200:
                // send OK response
                resp.sendHeaders(200, len, lastModified, etag,
                    getContentType(file.getName(), "application/octet-stream"), range);
                // send body
                InputStream in = new FileInputStream(file);
                try {
                    resp.sendBody(in, len, range);
                } finally {
                    in.close();
                }
                break;
            default:
                resp.sendHeaders(500); // should never happen
                break;
        }
    }

    /**
     * Serves the contents of a directory as an HTML file index.
     *
     * @param dir the existing and readable directory whose contents are served
     * @param path the displayed base path corresponding to dir
     * @return an HTML string containing the file index for the directory
     */
    public static String createIndex(File dir, String path) {
        if (!path.endsWith("/"))
            path += "/";
        // calculate name column width
        int w = 21; // minimum width
        for (String name : dir.list())
            if (name.length() > w)
                w = name.length();
        w += 2; // with room for added slash and space
        // note: we use apache's format, for consistent user experience
        Formatter f = new Formatter(Locale.US);
        f.format("<!DOCTYPE html>%n" +
            "<html><head><title>Index of %s</title></head>%n" +
            "<body><h1>Index of %s</h1>%n" +
            "<pre> Name%" + (w - 5) + "s Last modified      Size<hr>",
            path, path, "");
        if (path.length() > 1) // add parent link if not root path
            f.format(" <a href=\"%s/\">Parent Directory</a>%"
                + (w + 5) + "s-%n", getParentPath(path), "");
        for (File file : dir.listFiles()) {
            try {
                String name = file.getName() + (file.isDirectory() ? "/" : "");
                String size = file.isDirectory() ? "- " : toSizeApproxString(file.length());
                // properly url-encode the link
                String link = new URI(null, path + name, null).toASCIIString();
                if (!file.isHidden() && !name.startsWith("."))
                    f.format(" <a href=\"%s\">%s</a>%-" + (w - name.length()) +
                        "s&#8206;%td-%<tb-%<tY %<tR%6s%n",
                        link, name, "", file.lastModified(), size);
            } catch (URISyntaxException ignore) {}
        }
        f.format("</pre></body></html>");
        return f.toString();
    }

    /**
     * Starts a stand-alone HTTP server, serving files from disk.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            if (args.length == 0) {
                System.err.printf("Usage: java [-options] %s <directory> [port]%n" +
                    "To enable SSL: specify options -Djavax.net.ssl.keyStore, " +
                    "-Djavax.net.ssl.keyStorePassword, etc.%n", HTTPServer.class.getName());
                return;
            }
            File dir = new File(args[0]);
            if (!dir.canRead())
                throw new FileNotFoundException(dir.getAbsolutePath());
            int port = args.length < 2 ? 80 : (int)parseULong(args[1], 10);
            // set up server
            for (File f : Arrays.asList(new File("/etc/mime.types"), new File(dir, ".mime.types")))
                if (f.exists())
                    addContentTypes(new FileInputStream(f));
            HTTPServer server = new HTTPServer(port);
            if (System.getProperty("javax.net.ssl.keyStore") != null) // enable SSL if configured
                server.setServerSocketFactory(SSLServerSocketFactory.getDefault());
            VirtualHost host = server.getVirtualHost(null); // default host
            host.setAllowGeneratedIndex(true); // with directory index pages
            host.addContext("/", new FileContextHandler(dir));
            host.addContext("/api/time", new ContextHandler() {
                public int serve(Request req, Response resp) throws IOException {
                    long now = System.currentTimeMillis();
                    resp.getHeaders().add("Content-Type", "text/plain");
                    resp.send(200, String.format("%tF %<tT", now));
                    return 0;
                }
            });
            server.start();
            System.out.println("HTTPServer is listening on port " + port);
        } catch (Exception e) {
            System.err.println("error: " + e);
        }
    }
}
