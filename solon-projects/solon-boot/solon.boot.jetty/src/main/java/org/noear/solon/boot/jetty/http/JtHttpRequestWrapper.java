package org.noear.solon.boot.jetty.http;

import org.eclipse.jetty.util.MultiPartInputStreamParser;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.Part;
import java.io.IOException;
import java.util.Collection;

/**
 * @author noear
 * @since 2.7
 */
public class JtHttpRequestWrapper extends HttpServletRequestWrapper {
    private final MultiPartInputStreamParser multiPartParser;

    public JtHttpRequestWrapper(HttpServletRequest request, MultiPartInputStreamParser multiPartParser) {
        super(request);
        this.multiPartParser = multiPartParser;
    }

    @Override
    public Collection<Part> getParts() throws IOException, ServletException {
        return multiPartParser.getParts();
    }
}
