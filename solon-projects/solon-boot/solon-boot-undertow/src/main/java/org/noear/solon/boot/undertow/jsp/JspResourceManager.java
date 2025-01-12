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
package org.noear.solon.boot.undertow.jsp;

import io.undertow.UndertowMessages;
import io.undertow.server.handlers.resource.Resource;
import io.undertow.server.handlers.resource.ResourceChangeListener;
import io.undertow.server.handlers.resource.ResourceManager;
import io.undertow.server.handlers.resource.URLResource;
import org.noear.solon.core.handle.Context;

import java.io.IOException;
import java.net.URI;
import java.net.URL;

/**
 * JSP资源管理器
 */
public class JspResourceManager implements ResourceManager {
    private final ClassLoader classLoader;
    private final String prefix;

    public JspResourceManager(ClassLoader classLoader, String prefix) {
        this.classLoader = classLoader;
        if (prefix.isEmpty()) {
            this.prefix = "";
        } else if (prefix.endsWith("/")) {
            this.prefix = prefix;
        } else {
            this.prefix = prefix + "/";
        }

    }

    @Override
    public Resource getResource(String path) throws IOException {
        if (path == null || path.endsWith(".jsp") == false) {
            return null;
        }

        if(Context.current() == null){
            //说明先走的是jsp请求 //禁止
            return null;
        }

        String modPath = path;
        if (path.startsWith("/")) {
            modPath = path.substring(1);
        }

        String realPath = this.prefix + modPath;
        URL resource = null;
        if (realPath.startsWith("file:")) {
            resource = URI.create(realPath).toURL();
        } else {
            resource = this.classLoader.getResource(realPath);
        }

        return resource == null ? null : new URLResource(resource, path);
    }

    @Override
    public boolean isResourceChangeListenerSupported() {
        return false;
    }

    @Override
    public void registerResourceChangeListener(ResourceChangeListener listener) {
        throw UndertowMessages.MESSAGES.resourceChangeListenerNotSupported();
    }

    @Override
    public void removeResourceChangeListener(ResourceChangeListener listener) {
        throw UndertowMessages.MESSAGES.resourceChangeListenerNotSupported();
    }

    @Override
    public void close() throws IOException {
    }
}