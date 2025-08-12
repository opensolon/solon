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
package org.noear.solon.boot.jetty.jsp;

import org.apache.tomcat.util.descriptor.tld.TaglibXml;
import org.apache.tomcat.util.descriptor.tld.TldParser;
import org.apache.tomcat.util.descriptor.tld.TldResourcePath;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.noear.solon.Utils;
import org.noear.solon.core.AppClassLoader;
import org.noear.solon.core.util.ResourceUtil;
import org.noear.solon.core.util.ScanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.descriptor.TaglibDescriptor;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Tld 探测器
 *
 * @author noear
 * @since 2.6
 */
public class JspTldLocator {
    static final Logger log = LoggerFactory.getLogger(JspTldLocator.class);

    public static Map<String, TaglibDescriptor> createTldInfos(String... dltDirs) throws IOException {
        HashMap<String, TaglibDescriptor> tagLibInfos = new HashMap<>();

        TldParser tldParser = new TldParser(true, true, true);

        for (String dltDir : dltDirs) {
            //加载模板目录下的 tdl
            Set<String> urls = ScanUtil.scan(AppClassLoader.global(), dltDir, n -> n.endsWith(".tld"));

            if (Utils.isNotEmpty(urls)) {
                for (String uri : urls) {
                    uri = "/" + uri;

                    URL resUri = ResourceUtil.getResource(uri);
                    TldResourcePath tldResourcePath = new TldResourcePath(resUri, uri);

                    try {
                        TaglibXml taglibXml = tldParser.parse(tldResourcePath);

                        ServletContextHandler.TagLib tagLib = new ServletContextHandler.TagLib();
                        tagLib.setTaglibURI(taglibXml.getUri());
                        tagLib.setTaglibLocation(uri);

                        tagLibInfos.put(taglibXml.getUri(), tagLib);
                    } catch (Exception e) {
                        log.warn("Tdl failed to load, uri={}", uri, e);
                    }
                }
            }
        }

        return tagLibInfos;
    }
}
