package org.noear.solon.boot.jetty.http;

import org.eclipse.jetty.http.MultiPartFormInputStream;

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
    private final MultiPartFormInputStream multiPartParser;

    public JtHttpRequestWrapper(HttpServletRequest request, MultiPartFormInputStream multiPartParser) {
        super(request);
        this.multiPartParser = multiPartParser;
    }

    @Override
    public Collection<Part> getParts() throws IOException, ServletException {
        return multiPartParser.getParts();
    }
}
