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
package org.noear.solon.server.tomcat.jsp;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.descriptor.TaglibDescriptor;

import org.apache.tomcat.util.descriptor.tld.TaglibXml;
import org.apache.tomcat.util.descriptor.tld.TldParser;
import org.apache.tomcat.util.descriptor.tld.TldResourcePath;
import org.noear.solon.Utils;
import org.noear.solon.core.AppClassLoader;
import org.noear.solon.core.util.ResourceUtil;
import org.noear.solon.core.util.ScanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TLD文件定位器
 */
public class JspTldLocator {
    private static final Logger log = LoggerFactory.getLogger(JspTldLocator.class);

    private JspTldLocator() {
    	
    }
    public static Map<String, TaglibDescriptor> createTldInfos(String... tldDirs) throws IOException {
        Map<String, TaglibDescriptor> tagLibInfos = new HashMap<>();

        TldParser tldParser = new TldParser(true, true, true);

        for (String tldDir : tldDirs) {
            // 扫描指定目录下的TLD文件
            Set<String> urls = ScanUtil.scan(AppClassLoader.global(), tldDir, n -> n.endsWith(".tld"));

            if (Utils.isNotEmpty(urls)) {
                for (String uri : urls) {
                    uri = "/" + uri;

                    URL resUri = ResourceUtil.getResource(uri);
                    TldResourcePath tldResourcePath = new TldResourcePath(resUri, uri);

                    try {
                        TaglibXml taglibXml = tldParser.parse(tldResourcePath);

                        // 创建TLD描述符
                        final String taglibUri = taglibXml.getUri();
                        final String taglibLocation = uri;

                        TaglibDescriptor descriptor = new TaglibDescriptor() {
                            @Override
                            public String getTaglibURI() {
                                return taglibUri;
                            }

                            @Override
                            public String getTaglibLocation() {
                                return taglibLocation;
                            }
                        };

                        tagLibInfos.put(taglibUri, descriptor);
                    } catch (Exception e) {
                        log.warn("TLD failed to load, uri={}", uri, e);
                    }
                }
            }
        }

        return tagLibInfos;
    }
    
}