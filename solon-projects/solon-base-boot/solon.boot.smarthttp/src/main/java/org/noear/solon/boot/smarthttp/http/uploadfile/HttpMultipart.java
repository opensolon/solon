package org.noear.solon.boot.smarthttp.http.uploadfile;

import org.noear.solon.Solon;
import org.noear.solon.boot.ServerProps;

import java.io.IOException;
import java.io.InputStream;

public class HttpMultipart {
    public String name;
    public String filename;
    public HttpHeaderCollection headers;
    public InputStream body;

    public String getName() {
        return name;
    }

    public String getFilename() {
        return filename;
    }

    public HttpHeaderCollection getHeaders() {
        return headers;
    }

    public InputStream getBody() {
        return body;
    }

    public String getString() throws IOException {
        String charset = headers.getParams("Content-Type").get("charset");
        if (charset == null) {
            charset = Solon.encoding();
        }

        if (ServerProps.request_maxBodySize > Integer.MAX_VALUE) {
            return Utils.readToken(body, -1, charset, Integer.MAX_VALUE);
        } else {
            return Utils.readToken(body, -1, charset, (int) ServerProps.request_maxBodySize);
        }
    }
}
