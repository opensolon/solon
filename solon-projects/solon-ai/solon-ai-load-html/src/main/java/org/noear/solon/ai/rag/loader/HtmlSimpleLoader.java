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
package org.noear.solon.ai.rag.loader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.noear.solon.Utils;
import org.noear.solon.ai.rag.Document;
import org.noear.solon.ai.rag.DocumentLoader;

/**
 * 简单html 页面的数据拉取，支持从单个或多个URL加载HTML内容，并转换为Document对象
 * 适用场景：博客、新闻、论坛等
 *
 * @author 小奶奶花生米
 * @since 3.1
 */
public class HtmlSimpleLoader extends AbstractDocumentLoader {
    /** 要加载的网页URL列表 */
    private final List<String> webPaths;
    /** HTTP请求头 */
    private final Map<String, String> headers;
    /** 代理设置，格式: {"http": "host:port", "https": "host:port"} */
    private final Map<String, String> proxies;
    /** 遇到错误时是否继续处理其他URL */
    private final boolean continueOnFailure;
    /** 每秒最大请求数 */
    private final int requestsPerSecond;
    /** 请求超时时间(毫秒) */
    private final int timeout;
    /** 是否使用并发加载 */
    private final boolean concurrent;

    /**
     * 构造函数 - 单URL模式
     * @param webPath
     */
    public HtmlSimpleLoader(String webPath) {
        this(Collections.singletonList(webPath));
    }

    /**
     * 构造函数 - 多URL模式
     * @param webPaths
     */
    public HtmlSimpleLoader(List<String> webPaths) {
        this(new Builder(webPaths));
    }

    /**
     * 构造函数 - 多URL模式，并指定是否启用并发
     * @param webPaths
     * @param concurrent
     */
    public HtmlSimpleLoader(List<String> webPaths, boolean concurrent) {
        this(new Builder(webPaths).concurrent(concurrent));
    }

    /**
     * 私有构造函数，用于构建HtmlSimpleLoader对象
     * @param builder
     */
    private HtmlSimpleLoader(Builder builder) {
        this.webPaths = new ArrayList<>(builder.webPaths);
        this.headers = new HashMap<>(builder.headers);
        this.proxies = builder.proxies != null ? new HashMap<>(builder.proxies) : null;
        this.continueOnFailure = builder.continueOnFailure;
        this.requestsPerSecond = builder.requestsPerSecond;
        this.timeout = builder.timeout;
        this.concurrent = builder.concurrent;
    }

    /**
     * HtmlSimpleLoader 构建器
     */
    public static class Builder {
        private final List<String> webPaths;
        private Map<String, String> headers = getDefaultHeaders();
        private Map<String, String> proxies = null;
        private boolean continueOnFailure = false;
        private int requestsPerSecond = 2;
        private int timeout = 30000;
        private boolean concurrent = false;  // 默认不启用并发

        public Builder(String webPath) {
            this(Collections.singletonList(webPath));
        }

        public Builder(List<String> webPaths) {
            this.webPaths = new ArrayList<>(webPaths);
        }

        public Builder headers(Map<String, String> headers) {
            this.headers = headers;
            return this;
        }

        public Builder proxies(Map<String, String> proxies) {
            this.proxies = proxies;
            return this;
        }

        public Builder continueOnFailure(boolean continueOnFailure) {
            this.continueOnFailure = continueOnFailure;
            return this;
        }

        public Builder requestsPerSecond(int requestsPerSecond) {
            this.requestsPerSecond = requestsPerSecond;
            return this;
        }

        public Builder timeout(int timeout) {
            this.timeout = timeout;
            return this;
        }

        public Builder concurrent(boolean concurrent) {
            this.concurrent = concurrent;
            return this;
        }

        public HtmlSimpleLoader build() {
            return new HtmlSimpleLoader(this);
        }
    }

    /**
     * 获取默认的HTTP请求头
     * @return 默认的请求头Map
     */
    private static Map<String, String> getDefaultHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0 (compatible; SolonAI/1.0;)");
        headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        headers.put("Accept-Language", "zh-CN,en;q=0.5");
        headers.put("Referer", "https://www.baidu.com/");
        headers.put("DNT", "1");
        headers.put("Connection", "keep-alive");
        headers.put("Upgrade-Insecure-Requests", "1");
        return headers;
    }

    /**
     * 从HTML文档中提取元数据
     * @param soup Jsoup文档对象
     * @param url 网页URL
     * @return 元数据Map，包含source、title、description、language等信息
     */
    private Map<String, Object> buildMetadata(org.jsoup.nodes.Document soup, String url) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("source", url);

        String title = soup.title();
        if (!Utils.isEmpty(title)) {
            metadata.put("title", title);
        }

        String description = soup.select("meta[name=description]").attr("content");
        if (!Utils.isEmpty(description)) {
            metadata.put("description", description);
        }

        String language = soup.select("html").attr("lang");
        if (!Utils.isEmpty(language)) {
            metadata.put("language", language);
        }

        return metadata;
    }

    /**
     * 抓取单个URL的内容
     * @param url 要抓取的URL
     * @return Jsoup文档对象
     * @throws IOException 如果网络请求失败
     */
    private org.jsoup.nodes.Document fetch(String url) throws IOException {
        Connection conn = Jsoup.connect(url)
                .timeout(timeout)
                .ignoreHttpErrors(true)
                .headers(headers);

        if (proxies != null && !proxies.isEmpty()) {
            // 如果有代理设置，添加代理
            String proxyKey = url.startsWith("https") ? "https" : "http";
            String proxy = String.valueOf(proxies.get(proxyKey));
            if (!Utils.isEmpty(proxy)) {
                String[] parts = proxy.split(":");
                conn.proxy(parts[0], Integer.parseInt(parts[1]));
            }
        }

        return conn.get();
    }

    /**
     * 从单个URL加载文档
     * @param url 要加载的URL
     * @return 加载的Document对象
     * @throws Exception 如果加载失败
     */
    private Document loadUrl(String url) throws Exception {
        org.jsoup.nodes.Document soup = fetch(url);
        String text = soup.body().text();
        Map<String, Object> metadata = buildMetadata(soup, url);
        return new Document(text, metadata);
    }

    /**
     * 加载文档列表
     * @return 加载的Document对象列表
     * @throws Exception 如果加载失败
     */
    @Override
    public List<Document> load() {
        List<Document> documents = new ArrayList<>();

        // 单个URL或禁用并发时，使用同步处理
        if (webPaths.size() == 1 || !concurrent) {
            for (String url : webPaths) {
                try {
                    documents.add(loadUrl(url));
                } catch (Exception e) {
                    if (continueOnFailure) {
                        continue;
                    }
                    throw new RuntimeException("Failed to fetch URL: " + url, e);
                }
            }
            return documents;
        }

        // 多个URL且启用并发时，使用线程池
        ExecutorService executor = Executors.newFixedThreadPool(requestsPerSecond);
        try {
            List<Future<Document>> futures = new ArrayList<>();

            for (String url : webPaths) {
                futures.add(executor.submit(() -> {
                    try {
                        return loadUrl(url);
                    } catch (Exception e) {
                        if (continueOnFailure) {
                            return null;
                        }
                        throw new RuntimeException("Failed to fetch URL: " + url, e);
                    }
                }));
            }

            for (Future<Document> future : futures) {
                try {
                    Document doc = future.get(timeout, TimeUnit.MILLISECONDS);
                    if (doc != null) {
                        documents.add(doc);
                    }
                } catch (Exception e) {
                    if (!continueOnFailure) {
                        throw new RuntimeException("Error while fetching pages", e);
                    }
                }
            }
        } finally {
            executor.shutdown();
        }

        return documents;
    }
}
