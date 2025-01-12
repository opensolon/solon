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
