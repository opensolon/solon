package org.noear.solon.net.http.impl.jdk;

import org.noear.solon.core.util.IoUtil;
import org.noear.solon.core.util.KeyValues;
import org.noear.solon.core.util.MultiMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.zip.GZIPInputStream;

/*
使用方法:
HttpClient client = HttpClient.buildHttpClient();
Request request = client.buildRequest("http://localhost:8881/charge/testGet?key1=0111&value=shaines.cn");
// Request request = client.buildRequest("http://localhost:8881/charge/testPost").setMethod(Method.POST);
// Request request = client.buildRequest("http://localhost:8881/charge/testPut").setMethod(Method.PUT);
// Request request = client.buildRequest("http://localhost:8881/charge/testFile").setMethod(Method.POST);
request.setParam(Params.ofFormData().add("key1", "1111").add("key3", "2222")
                         .addFile("key2", new File("C:\\Users\\houyu\\Desktop\\1.txt"))
                         .addFile("key4", new File("C:\\Users\\houyu\\Desktop\\2.png")));
Response<String> response = request.execute(BodyHandlers.ofString());
System.out.println("response.getUrl() = " + response.getUrl());
System.out.println("response.getBody() = " + response.getBody());
 */

/**
 * http 客户端
 * @author for.houyu@qq.com
 * @createTime 2019/10/11 22:31
 */
public class HttpClient {

    /** 域对象 */
    private Session session;

    private HttpClient() {}

    public static HttpClient buildHttpClient() {
        HttpClient client = new HttpClient();
        client.session = new Session();
        return client;
    }

    public Request buildRequest(String url) {
        return new Request(url, this);
    }

    public <T> Response<T> execute(Request request, BodyHandler<T> bodyHandler) {
        return Executor.build(request).execute(bodyHandler);
    }

    public Session session() {
        return this.session;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder("HttpClient{");
        builder.append("session=").append(this.session);
        builder.append('}');
        return builder.toString();
    }
    /***/
    /** 内部类 start +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 内部类 start */
    /***/
    // =============================================== Session (域对象) ========================================================== //

    /**
     * 域对象
     */
    public static class Session {

        private static final Logger log = LoggerFactory.getLogger(Session.class);

        /** 请求方法 */
        private final Method method = Method.GET;
        /** 是否编码URL */
        private volatile boolean ifEncodeUrl = false;
        /** 是否缓存 */
        private volatile boolean ifCache = false;
        /** 超时时间 (单位:毫秒) 1分钟 */
        private volatile int connectTimeout = 10_000;
        private volatile int readTimeout = 60_000;
        /** 是否稳定重定向 */
        private volatile boolean ifStableRedirection = true;
        /** 是否处理https */
        private volatile boolean ifHandleHttps = true;
        /** 是否启用默认主机名验证程序 */
        private volatile boolean ifEnableDefaultHostnameVerifier = false;
        /** 推荐(上一个网页地址) */
        private volatile String referer;
        /** cookie */
        private final Map<String, String> cookie = new ConcurrentHashMap<>(16);
        /** 代理 */
        private volatile Proxy proxy;
        /** 参数编码 */
        private volatile Charset charset = StandardCharsets.UTF_8;
        /** 主机名验证程序 */
        private HostnameVerifier hostnameVerifier;
        /** SocketFactory */
        private SSLSocketFactory sslSocketFactory;
        /** 携带参数(可使用于响应之后的操作) */
        private final Map<String, Object> extra = new ConcurrentHashMap<>(16);
        /** 请求头信息 (默认的请求头信息) */
        private final Map<String, Object> header = new ConcurrentHashMap<>(8);

        /* -------------------------------- constructor -------------------------- start */

        protected Session() {
            this.header.put("Accept", "text/html,application/xhtml+xml,application/xml,application/json;q=0.9,*/*;q=0.8");
            // "Mozilla/5.0 (Linux; Android 8.1.0; 1809-A01 Build/OPM1; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/62.0.3202.97 Mobile Safari/537.36"
            this.header.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36");
            this.header.put("Accept-Encoding", "gzip");
            this.header.put("Accept-Language", "zh-CN,zh;q=0.9,zh-TW;q=0.8,en-US;q=0.7,en;q=0.6");
            // this.header.put("Content-Type", "application/x-www-form-urlencoded");
            // this.header = Collections.unmodifiableMap(header);
            // 初始化全局主机名验证程序
            this.hostnameVerifier = (s, sslSession) -> true;
            // 初始化全局主机名验证程序
            final X509TrustManager x509TrustManager = new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    // return new X509Certificate[0];
                    return null;
                }
            };
            try {
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, new TrustManager[] {x509TrustManager}, new SecureRandom());
                this.sslSocketFactory = sslContext.getSocketFactory();
            } catch(NoSuchAlgorithmException | KeyManagementException e) {
                log.warn("init SSLContext has exception ", e);
            }
        }

        /* -------------------------------- constructor -------------------------- end */


        /* ---------------------------------- setter ----------------------------- start */

        public Session setIfEncodeUrl(boolean ifEncodeUrl) {
            this.ifEncodeUrl = ifEncodeUrl;
            return this;
        }

        public Session setIfCache(boolean ifCache) {
            this.ifCache = ifCache;
            return this;
        }

        public Session setConnectTimeout(int connectTimeout) {
            this.connectTimeout = connectTimeout < 0 ? this.connectTimeout : connectTimeout;
            return this;
        }

        public Session setReadTimeout(int readTimeout) {
            this.readTimeout = readTimeout < 0 ? this.readTimeout : readTimeout;
            return this;
        }

        public Session setIfStableRedirection(boolean ifStableRedirection) {
            this.ifStableRedirection = ifStableRedirection;
            return this;
        }

        public Session setIfHandleHttps(boolean ifHandleHttps) {
            this.ifHandleHttps = ifHandleHttps;
            return this;
        }

        public Session setIfEnableDefaultHostnameVerifier(boolean ifEnableDefaultHostnameVerifier) {
            this.ifEnableDefaultHostnameVerifier = ifEnableDefaultHostnameVerifier;
            return this;
        }

        public Session setReferer(String referer) {
            this.referer = Util.nullOfDefault(referer, this.referer);
            return this;
        }

        public Session addCookie(Map<String, String> cookie) {
            if(Util.isNotEmpty(cookie)) {
                this.cookie.putAll(cookie);
            }
            return this;
        }

        /**
         * 覆盖之前的所有 cookie
         */
        public Session setCookie(String cookie) {
            if(Util.isNotEmpty(cookie)) {
                String[] split = cookie.split(Constant.COOKIE_SPLIT);
                for(String cookieObject : split) {
                    String[] keyAndVal = cookieObject.split(Constant.EQU, 2);
                    this.cookie.put(keyAndVal[0], keyAndVal[1]);
                }
            }
            return this;
        }

        public Session setProxy(Proxy proxy) {
            this.proxy = Util.nullOfDefault(proxy, this.proxy);
            return this;
        }

        public Session setCharset(Charset charset) {
            this.charset = Util.nullOfDefault(charset, this.charset);
            return this;
        }

        public Session setHostnameVerifier(HostnameVerifier hostnameVerifier) {
            this.hostnameVerifier = Util.nullOfDefault(hostnameVerifier, this.hostnameVerifier);
            return this;
        }

        public Session setSslSocketFactory(SSLSocketFactory sslSocketFactory) {
            this.sslSocketFactory = Util.nullOfDefault(sslSocketFactory, this.sslSocketFactory);
            return this;
        }

        public Session addExtra(Map<String, Object> extra) {
            if(Util.isNotEmpty(extra)) {
                this.extra.putAll(extra);
            }
            return this;
        }

        public Session addExtra(String key, Object val) {
            if(Util.isNotEmpty(key) && Util.isNotEmpty(val)) {
                this.extra.put(key, val);
            }
            return this;
        }

        /**
         * 覆盖之前的所有 extra
         * @param extra 如果不为 null ,那就覆盖之前所有的 extra
         */
        public Session setExtra(Map<String, Object> extra) {
            if(Objects.nonNull(extra)) {
                this.extra.clear();
                this.extra.putAll(extra);
            }
            return this;
        }

        public Session addHeader(Map<String, Object> header) {
            if(Util.isNotEmpty(header)) {
                this.header.putAll(header);
            }
            return this;
        }

        public Session addHeader(String key, Object val) {
            if(Util.isNotEmpty(key) && Util.isNotEmpty(val)) {
                this.header.put(key, val);
            }
            return this;
        }

        /**
         * 覆盖之前的所有 header
         * @param header 如果不为 null ,那就覆盖之前所有的 header
         */
        public Session setHeader(Map<String, Object> header) {
            if(Objects.nonNull(header)) {
                this.header.clear();
                this.header.putAll(header);
            }
            return this;
        }

        /* ---------------------------------- setter ----------------------------- end */

        /* ---------------------------------- getter ----------------------------- start */

        protected Method getMethod() {
            return this.method;
        }

        protected boolean getIfEncodeUrl() {
            return this.ifEncodeUrl;
        }

        protected boolean getIfCache() {
            return this.ifCache;
        }

        protected int getConnectTimeout() {
            return this.connectTimeout;
        }

        protected int getReadTimeout() {
            return this.readTimeout;
        }

        protected boolean getIfStableRedirection() {
            return this.ifStableRedirection;
        }

        protected boolean getIfHandleHttps() {
            return this.ifHandleHttps;
        }

        protected boolean getIfEnableDefaultHostnameVerifier() {
            return this.ifEnableDefaultHostnameVerifier;
        }

        protected String getReferer() {
            return this.referer;
        }

        protected String getCookie() {
            // cookie ex:key2=val2; key1=val1
            StringBuilder builder = new StringBuilder(128);
            for(Entry<String, String> entry : this.cookie.entrySet()) {
                builder.append(entry.getKey()).append("=").append(entry.getValue()).append("; ");
            }
            return builder.length() > 0 ? builder.delete(builder.length() - 2, builder.length()).toString() : "";
        }

        protected Proxy getProxy() {
            return this.proxy == null ? null : Proxy.of(this.proxy.getHost(), this.proxy.getPort(), this.proxy.getUsername(), this.proxy.getPassword());
        }

        protected Charset getCharset() {
            return this.charset;
        }

        protected HostnameVerifier getHostnameVerifier() {
            return this.hostnameVerifier;
        }

        protected SSLSocketFactory getSslSocketFactory() {
            return this.sslSocketFactory;
        }

        protected Map<String, Object> getExtra() {
            return new HashMap<>(this.extra);
        }

        protected Map<String, Object> getHeader() {
            return new HashMap<>(this.header);
        }

        /* ---------------------------------- getter ----------------------------- end */

        /* ---------------------------------- toString ----------------------------- start */

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder("Session{");
            builder.append("method=").append(this.method);
            builder.append(", ifEncodeUrl=").append(this.ifEncodeUrl);
            builder.append(", ifCache=").append(this.ifCache);
            builder.append(", readTimeout=").append(this.readTimeout);
            builder.append(", connectTimeout=").append(this.connectTimeout);
            builder.append(", ifStableRedirection=").append(this.ifStableRedirection);
            builder.append(", ifHandleHttps=").append(this.ifHandleHttps);
            builder.append(", ifEnableDefaultHostnameVerifier=").append(this.ifEnableDefaultHostnameVerifier);
            builder.append(", referer='").append(this.referer).append('\'');
            builder.append(", cookie=").append(this.cookie);
            builder.append(", proxy=").append(this.proxy);
            builder.append(", charset=").append(this.charset);
            builder.append(", hostnameVerifier=").append(this.hostnameVerifier);
            builder.append(", sslSocketFactory=").append(this.sslSocketFactory);
            builder.append(", extra=").append(this.extra);
            builder.append(", header=").append(this.header);
            builder.append('}');
            return builder.toString();
        }

        /* ---------------------------------- toString ----------------------------- end */

    }
    // =============================================== Executor (执行对象) ======================================================= //

    /**
     * 执行对象
     */
    public static class Executor {

        private static final Logger log = LoggerFactory.getLogger(Executor.class);

        /** 请求对象 */
        private Request request;
        /** 重定向的url列表 */
        private List<String> redirectUrlList;
        /** HttpURLConnection对象 */
        private HttpURLConnection http;

        protected Executor(Request request) {
            this.request = request;
        }

        /* ---------------------------------- getter ----------------------------- start */

        protected Request getRequest() {
            return this.request;
        }

        protected List<String> getRedirectUrlList() {
            return this.redirectUrlList;
        }

        protected HttpURLConnection getHttp() {
            return this.http;
        }

        /* ---------------------------------- getter ----------------------------- end   */

        protected static Executor build(Request request) {
            return new Executor(request);
        }

        protected <T> Response<T> execute(BodyHandler<T> handler) {
            try {
                return handleHttpConnection(handler);
            } catch(Throwable e) {
                log.warn("handleHttpConnection has exception, use error response. message info: {}", e.getMessage());
                return (Response<T>) Response.getErrorResponse(this.request, e);
            }
        }

        private <T> Response<T> handleHttpConnection(BodyHandler<T> handler) {
            // 处理URL参数问题
            this.handleUrlParam();
            // 初始化连接
            this.initConnection();
            // 发送数据包裹
            this.send();
            // 处理重定向
            boolean ifRedirect = this.handleRedirect();
            if(ifRedirect) {
                // 递归实现重定向
                return this.handleHttpConnection(handler);
            }
            // 返回响应
            return new Response<>(this, handler);
        }

        private boolean handleRedirect() {
            if(this.request.getIfStableRedirection()) {
                // 采用稳定重定向方式, 需要处理重定向问题
                int responseCode;
                try {
                    responseCode = this.http.getResponseCode();
                } catch(IOException var3) {
                    throw new RuntimeException(String.format("%s get response code has exception", this.request.getUrl()), var3);
                }
                if(Constant.REDIRECT_CODES.contains(responseCode)) {
                    String redirectURL = this.http.getHeaderField(Constant.LOCATION);
                    try {
                        redirectURL = processRedirectURL(redirectURL);
                    } catch(MalformedURLException e) {
                        throw new RuntimeException(String.format("%s processRedirectURL has exception", this.request.getUrl()), e);
                    }
                    this.request.setUrl(redirectURL);
                    this.redirectUrlList = Util.nullOfDefault(this.redirectUrlList, new ArrayList<String>(8));
                    this.redirectUrlList.add(this.request.getUrl());
                    if(this.redirectUrlList.size() < 8) {
                        // 断开本次连接, 然后重新请求
                        this.http.disconnect();
                        log.debug("{} request redirecting ", this.request.getUrl());
                        return true;
                    }
                }
            } else {
                // 使用默认的重定向规则处理, 无序手动处理, 但是有可能出现重定向失败
                // do non thing
            }
            return false;
        }

        private String processRedirectURL(String redirectURL) throws MalformedURLException {
            URL previousURL = new URL(this.request.getUrl());
            if(redirectURL.startsWith("/")) {
                // 重定向的URL 是一个绝对路径 https://www.baodu.com/test        https://www.baidu.com:10086/test
                StringBuilder builder = new StringBuilder(previousURL.getProtocol());
                builder.append("://");
                builder.append(previousURL.getHost());
                builder.append(previousURL.getPort() == -1 ? "" : (":" + previousURL.getPort()));
                builder.append(redirectURL);
                redirectURL = builder.toString();
                // } else if(redirectURL.startsWith("./")) {
                //     // 重定向的URL 是一个相对路径 TODO 暂时不处理, 后面有需求再弄
            }
            return redirectURL;
        }

        /** 发送数据 */
        private void send() {
            try {
                if(Method.GET.equals(this.request.getMethod())) {
                    this.http.connect();
                } else {
                    // POST...
                    this.handleContentTypeAndBody();
                }
            } catch(IOException e) {
                throw new RuntimeException(String.format("%s send data has exception", this.request.getUrl()), e);
            }
        }

        /** 处理 ContentType 和 传输内容 */
        private void handleContentTypeAndBody() throws IOException {
            if (!Method.GET.equals(this.request.getMethod())) {
                // non GET
                /* handle ContentType 有可能多个content-type, 大小写不一致的问题 */
                if (this.request.getParam() == null) {
                    this.request.setParam(Params.ofForm());
                }
                this.request.removeHeader("content-type");
                Object tempContentType = this.request.getHeader(Constant.CONTENT_TYPE);
                String contentType = tempContentType == null ? this.request.getParam().contentType : String.valueOf(tempContentType);
                this.addAndRefreshHead(Constant.CONTENT_TYPE, contentType);
                /* handle body */
                // 非GET 所有的请求头必须在调用getOutputStream()之前设置好, 这里相当于GET的connect();
                Param param = this.request.getParam().ok();
                if (param != null) {
                    try (OutputStream outputStream = this.http.getOutputStream()) {
                        // 使用 try-with-resource 方式处理流, 无需手动关闭流操作
                        param.write(outputStream);
                        outputStream.flush();
                    }
                }
            }
        }

        /** 刷新 请求头信息 */
        private void addAndRefreshHead(String key, Object value) {
            if(Util.isNotEmpty(key) && Util.isNotEmpty(value)) {
                this.request.addHeader(key, value);
                this.http.setRequestProperty(key, String.valueOf(value));
            }
        }

        /** 初始化连接 */
        private void initConnection() throws RuntimeException {
            URL url;
            try {
                url = new URL(this.request.getUrl());
            } catch(MalformedURLException e) {
                throw new RuntimeException(String.format("%s create URL has exception", this.request.getUrl()), e);
            }
            //
            try {
                this.http = this.openConnection(url, this.request.getProxy());
                // 设置超时
                if(this.request.getConnectTimeout() > 0) {
                    this.http.setConnectTimeout(this.request.getConnectTimeout());
                }
                if(this.request.getReadTimeout() > 0) {
                    this.http.setReadTimeout(this.request.getReadTimeout());
                }
                // 设置请求方法
                this.http.setRequestMethod(this.request.getMethod().name());
            } catch(IOException e) {
                throw new RuntimeException(String.format("%s open connection has exception", this.request.getUrl()), e);
            }
            //
            this.http.setDoInput(true);
            if(!Method.GET.equals(this.request.getMethod())) {
                // 非GET方法需要设置可输入
                http.setDoOutput(true);
                http.setUseCaches(false);
            }
            // 设置cookie
            this.setCookie();
            // 设置请求头到连接中
            this.request.getHeader().forEach((k, v) -> this.http.setRequestProperty(k, String.valueOf(v)));
            // 设置缓存
            if(this.request.getIfCache() && !Method.GET.equals(this.request.getMethod())) {
                this.http.setUseCaches(true);
            }
            // 设置是否自动重定向
            this.http.setInstanceFollowRedirects(!(this.request.getIfStableRedirection()));
        }

        private void setCookie() {
            if(Util.isNotEmpty(this.request.getCookie())) {
                log.debug("{} set cookie {}", this.request.getUrl(), this.request.getCookie());
                this.request.removeHeader("cookie");
                this.request.addHeader(Constant.REQUEST_COOKIE, this.request.getCookie());
            }
        }

        /** 打开连接 */
        private HttpURLConnection openConnection(URL url, Proxy proxy) throws IOException {
            URLConnection connection;
            if(this.request.getProxy() == null) {
                connection = url.openConnection();
            } else if(Util.isNotEmpty(proxy.getUsername())) {
                // 设置代理服务器
                java.net.Proxy javaNetProxy = new java.net.Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress(proxy.getHost(), proxy.getPort()));
                connection = url.openConnection(javaNetProxy);
                String authString = proxy.getUsername() + ":" + proxy.getPassword();
                String auth = "Basic " + Base64.getEncoder().encodeToString(authString.getBytes(this.request.getClient().session().getCharset()));
                connection.setRequestProperty(Constant.PROXY_AUTHORIZATION, auth);
                log.debug("{} do proxy server ", this.request.getUrl());
            } else if(Util.isNotEmpty(proxy.getHost())) {
                // 设置代理主机和端口
                java.net.Proxy javaNetProxy = new java.net.Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress(proxy.getHost(), proxy.getPort()));
                connection = url.openConnection(javaNetProxy);
                log.debug("{} do proxy ", this.request.getUrl());
            } else {
                // 不设置代理
                connection = url.openConnection();
            }
            if(this.request.getIfHandleHttps() && connection instanceof HttpsURLConnection) {
                HttpsURLConnection httpsConn = (HttpsURLConnection) connection;
                // 设置主机名验证程序
                if(this.request.getIfEnableDefaultHostnameVerifier()) {
                    httpsConn.setHostnameVerifier(this.request.getHostnameVerifier());
                }
                // 设置ssl factory
                httpsConn.setSSLSocketFactory(this.request.getSslSocketFactory());
            }
            return (HttpURLConnection) connection;
        }

        /**
         * 设置 url 参数问题
         */
        private void handleUrlParam() {
            // 处理url中的query进行url编码
            int indexOf;
            if(this.request.getIfEncodeUrl() && (indexOf = this.request.getUrl().indexOf(Constant.queryFlag)) > -1) {
                String query = this.request.getUrl().substring(indexOf);
                query = Util.urlEncode(query, request.getClient().session().getCharset());
                query = query.replace("%3F", "?").replace("%2F", "/").replace("%3A", ":").replace("%3D", "=").replace("%26", "&").replace("%23", "#");
                this.request.setUrl(this.request.getUrl().substring(0, indexOf) + query);
            }
        }

    }
    // =============================================== Request (请求对象) ======================================================== //

    /**
     * 请求对象
     */
    public static class Request {

        /** 请求网站地址 */
        protected String url;
        /** 请求方法 */
        protected Method method;
        /** 请求头 */
        protected Map<String, Object> header;
        /** 请求参数 */
        protected Param param;
        /** 携带参数(可使用于响应之后的操作) */
        protected Map<String, Object> extra;
        /** 代理 */
        protected Proxy proxy;
        /** 是否编码URL */
        protected boolean ifEncodeUrl;
        /** 是否缓存 */
        protected boolean ifCache;
        /** 连接超时(单位:毫秒) */
        protected int readTimeout;
        protected int connectTimeout;
        /** 携带cookie(优先) ex: key1=val1; key2=val2 */
        protected String cookie;
        /** 是否稳定重定向 */
        protected boolean ifStableRedirection;
        /** 是否处理https */
        protected boolean ifHandleHttps;
        /** 是否启用默认主机名验证程序 */
        protected boolean ifEnableDefaultHostnameVerifier;
        /** 主机名验证程序 */
        protected HostnameVerifier hostnameVerifier;
        /** SocketFactory */
        protected SSLSocketFactory sslSocketFactory;
        /** 客户端 */
        protected HttpClient client;

        protected Request(String url, HttpClient client) {
            this.setUrl(url);
            this.client = client;
            this.init();
        }

        private void init() {
            Session session = client.session();
            this.setMethod(session.getMethod());
            this.setHeader(session.getHeader());
            if(Util.isNotEmpty(session.getReferer())) {
                this.addHeader(Constant.REFERER, session.getReferer());
            }
            this.setExtra(session.getExtra());
            this.setProxy(session.getProxy());
            this.setIfEncodeUrl(session.getIfEncodeUrl());
            this.setIfCache(session.getIfCache());
            this.setConnectTimeout(session.getConnectTimeout());
            this.setReadTimeout(session.getReadTimeout());
            this.setCookie(session.getCookie());
            this.setIfStableRedirection(session.getIfStableRedirection());
            this.setIfHandleHttps(session.getIfHandleHttps());
            this.setIfEnableDefaultHostnameVerifier(session.getIfEnableDefaultHostnameVerifier());
            this.setHostnameVerifier(session.getHostnameVerifier());
            this.setSslSocketFactory(session.getSslSocketFactory());
        }

        public <T> Response<T> execute(BodyHandler<T> bodyHandler) {
            return this.client.execute(this, bodyHandler);
        }

        /* ---------------------------- setter ---------------------------------- start */

        protected Request setUrl(String url) {
            // 校验url地址
            this.url = String.valueOf(url);
            if (url.startsWith("//")) {
                this.url = "http:" + this.url;
            } else if (url.startsWith("://")) {
                this.url = "http" + this.url;
            } else if(!this.url.toLowerCase().startsWith(Constant.HTTP)) {
                this.url = Constant.HTTP + "://" + this.url;
            }
            return this;
        }

        public Request setMethod(Method method) {
            this.method = Util.nullOfDefault(method, Method.GET);
            return this;
        }

        public Request GET() {
            return this.setMethod(Method.GET);
        }

        public Request POST() {
            return this.setMethod(Method.POST);
        }

        public Request PUT() {
            return this.setMethod(Method.PUT);
        }

        public Request DELETE() {
            return this.setMethod(Method.DELETE);
        }

        /**
         * 调用这个方法会覆盖之前所有的请求头
         * @param header 如果 header 不为 null ,那就覆盖之前的所有 header
         */
        public Request setHeader(Map<String, Object> header) {
            this.header = Util.nullOfDefault(header, this.header);
            return this;
        }

        public Request addHeader(String key, Object val) {
            if(Util.isNotEmpty(key) && Util.isNotEmpty(val)) {
                // 这里 header 不可以能为 null
                this.header.put(key, val);
            }
            return this;
        }

        public Request removeHeader(String key) {
            if(Util.isNotEmpty(key)) {
                // 这里 header 不可以能为 null
                this.header.remove(key);
            }
            return this;
        }

        public Request setParam(Param param) {
            this.param = Util.nullOfDefault(param, this.param);
            return this;
        }

        /**
         * 调用这个方法会覆盖之前所有的extra
         * @param extra 如果 extra 不为 null ,那就覆盖之前的所有 extra
         */
        public Request setExtra(Map<String, Object> extra) {
            this.extra = Util.nullOfDefault(extra, this.extra);
            return this;
        }

        public Request addExtra(String key, Object val) {
            if(Util.isNotEmpty(key) && Util.isNotEmpty(val)) {
                // 这里 extra 不可以能为 null
                this.extra.put(key, val);
            }
            return this;
        }

        public Request setProxy(Proxy proxy) {
            this.proxy = Util.nullOfDefault(proxy, this.proxy);
            return this;
        }

        public Request setIfEncodeUrl(boolean ifEncodeUrl) {
            this.ifEncodeUrl = ifEncodeUrl;
            return this;
        }

        public Request setIfCache(boolean ifCache) {
            this.ifCache = ifCache;
            return this;
        }

        public Request setConnectTimeout(int connectTimeout) {
            this.connectTimeout = connectTimeout < 0 ? this.connectTimeout : connectTimeout;
            return this;
        }

        public Request setReadTimeout(int readTimeout) {
            this.readTimeout = readTimeout < 0 ? this.readTimeout : readTimeout;
            return this;
        }

        public Request setCookie(String cookie) {
            this.cookie = Util.nullOfDefault(cookie, this.cookie);
            return this;
        }

        public Request setIfStableRedirection(boolean ifStableRedirection) {
            this.ifStableRedirection = ifStableRedirection;
            return this;
        }

        public Request setIfHandleHttps(boolean ifHandleHttps) {
            this.ifHandleHttps = ifHandleHttps;
            return this;
        }

        public Request setIfEnableDefaultHostnameVerifier(boolean ifEnableDefaultHostnameVerifier) {
            this.ifEnableDefaultHostnameVerifier = ifEnableDefaultHostnameVerifier;
            return this;
        }

        public Request setHostnameVerifier(HostnameVerifier hostnameVerifier) {
            this.hostnameVerifier = Util.nullOfDefault(hostnameVerifier, this.hostnameVerifier);
            return this;
        }

        public Request setSslSocketFactory(SSLSocketFactory sslSocketFactory) {
            this.sslSocketFactory = Util.nullOfDefault(sslSocketFactory, this.sslSocketFactory);
            return this;
        }

        /* ---------------------------- setter ---------------------------------- end */

        /* ---------------------------- getter ---------------------------------- start */

        protected Map<String, Object> getExtra() {
            return this.extra;
        }

        protected Proxy getProxy() {
            return this.proxy;
        }

        protected String getCookie() {
            return this.cookie;
        }

        protected String getUrl() {
            return this.url;
        }

        protected Method getMethod() {
            return this.method;
        }

        protected Map<String, Object> getHeader() {
            return this.header;
        }

        protected Object getHeader(String key) {
            return Util.isNotEmpty(key) ? this.header.get(key) : null;
        }

        protected Param getParam() {
            return this.param;
        }

        protected boolean getIfEncodeUrl() {
            return this.ifEncodeUrl;
        }

        protected boolean getIfCache() {
            return this.ifCache;
        }

        protected int getConnectTimeout() {
            return this.connectTimeout;
        }

        protected int getReadTimeout(){
            return this.readTimeout;
        }

        protected boolean getIfStableRedirection() {
            return this.ifStableRedirection;
        }

        protected boolean getIfHandleHttps() {
            return this.ifHandleHttps;
        }

        protected boolean getIfEnableDefaultHostnameVerifier() {
            return this.ifEnableDefaultHostnameVerifier;
        }

        protected HostnameVerifier getHostnameVerifier() {
            return this.hostnameVerifier;
        }

        protected SSLSocketFactory getSslSocketFactory() {
            return this.sslSocketFactory;
        }

        protected HttpClient getClient() {
            return this.client;
        }

        /* ---------------------------- getter ---------------------------------- end */


        /* ---------------------------- toString ---------------------------------- start */

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder("Request{");
            builder.append("url='").append(this.url).append('\'');
            builder.append(", method=").append(this.method);
            builder.append(", header=").append(this.header);
            builder.append(", param=").append(this.param);
            builder.append(", extra=").append(this.extra);
            builder.append(", proxy=").append(this.proxy);
            builder.append(", ifEncodeUrl=").append(this.ifEncodeUrl);
            builder.append(", ifCache=").append(this.ifCache);
            builder.append(", readTimeout=").append(this.readTimeout);
            builder.append(", connectTimeout=").append(this.connectTimeout);
            builder.append(", cookie='").append(this.cookie).append('\'');
            builder.append(", ifStableRedirection=").append(this.ifStableRedirection);
            builder.append(", ifHandleHttps=").append(this.ifHandleHttps);
            builder.append(", ifEnableDefaultHostnameVerifier=").append(this.ifEnableDefaultHostnameVerifier);
            builder.append(", hostnameVerifier=").append(this.hostnameVerifier);
            builder.append(", sslSocketFactory=").append(this.sslSocketFactory);
            // builder.append(", client=").append(this.client);
            builder.append('}');
            return builder.toString();
        }

        /* ---------------------------- toString ---------------------------------- end   */

    }
    // =============================================== Param (请求参数) ========================================================== //

    /**
     * 请求参数
     */
    public static abstract class Param {
        /** 请求头 内容类型 */
        String contentType;

        /**
         * 写入
         * */
        abstract void write(OutputStream out) throws IOException;

        /**
         * 获取 param 之前会先调用 ok() 方法, 确保准备完毕
         */
        public abstract Param ok();
    }
    // =============================================== Params (请求参数工具) ===================================================== //

    /**
     * 请求参数工具
     */
    public static abstract class Params {

        /* ---------------------------------- 实现 Param 的内部类 ----------------------------- start */

        public static class ParamRaw extends Param {
            /** 请求内容 */
            InputStream body;

            ParamRaw(String contentType, byte[] body) {
                this(contentType, new ByteArrayInputStream(body));
            }

            ParamRaw(String contentType, InputStream body) {
                this.contentType = contentType;
                this.body = body;
            }

            @Override
            void write(OutputStream out) throws IOException {
                try {
                    IoUtil.transferTo(body, out);
                } finally {
                    body.close();
                }
            }

            @Override
            public Param ok() {
                return this;
            }
        }

        public static class ParamForm extends Param {

            MultiMap<Object> paramMap;
            Charset charset;

            ParamForm(Charset charset) {
                this.charset = charset;
                this.contentType = Constant.CONTENT_TYPE_WITH_FORM + this.charset.name();
                this.paramMap = new MultiMap<>();
            }

            public ParamForm add(String key, Object val) {
                if (Util.isNotEmpty(key) && Util.isNotEmpty(val)) {
                    this.paramMap.add(key, val);
                }
                return this;
            }

            public ParamForm add(Map<String, Object> paramMap) {
                if (Util.isNotEmpty(paramMap)) {
                    paramMap.forEach(this::add);
                }
                return this;
            }

            @Override
            void write(OutputStream out) throws IOException {
                InputStream inputStream = new ByteArrayInputStream(Util.paramMapAsString(this.paramMap, this.charset).getBytes(this.charset));
                IoUtil.transferTo(inputStream, out);
            }

            @Override
            public Param ok() {
                return this;
            }
        }

        public static class ParamFormData extends Param {

            /* ------------------------------------------------------------------------------------------- */

            public static class Resource {
                public final String filename;
                public final InputStream inputStream;
                public final String contentType;
                public final Charset charset;

                public Resource(String filename, InputStream inputStream, String contentType, Charset charset) {
                    this.filename = filename;
                    this.inputStream = inputStream;
                    this.contentType = contentType;
                    this.charset = Util.nullOfDefault(charset, Constant.defaultCharset);
                }
            }

            private static final String horizontalLine = "--------------------------";
            private static final String lineFeed = System.lineSeparator();
            private static final String fileFormat = "Content-Disposition: form-data; name=\"%s\"; filename=\"%s\"\nContent-Type: %s";
            private static final String textFormat = "Content-Disposition: form-data; name=\"%s\"";

            Charset charset;
            String separator;
            String endFlag;
            MultiMap<Object> tempMap;

            protected ParamFormData(Charset charset) {
                this.charset = charset;
                long randomNumber = ThreadLocalRandom.current().nextLong();
                this.contentType = Constant.CONTENT_TYPE_WITH_FORM_DATA + horizontalLine + randomNumber;
                this.separator = "--" + horizontalLine + randomNumber;
                this.endFlag = separator + "--" + lineFeed;
                this.tempMap = new MultiMap<>();
            }

            @Override
            void write(OutputStream out) throws IOException {
                this.fillData(out);
            }

            @Override
            public Param ok() {
                return this;
            }

            public ParamFormData add(String key, Object val) {
                if(Util.isNotEmpty(key) && Util.isNotEmpty(val)) {
                    this.tempMap.put(key, val);
                }
                return this;
            }

            public ParamFormData addFile(String key, String filename, InputStream inputStream, String contentType) {
                if(Util.isNotEmpty(key) && filename != null) {
                    this.add(key, new Resource(filename, inputStream, contentType, null));
                }
                return this;
            }

            private void fillData(OutputStream out) throws IOException {
                for (KeyValues<Object> kv : this.tempMap) {
                    for (Object val : kv.getValues()) {
                        if (val instanceof Resource) {
                            this.appendResource(out, kv.getKey(), (Resource) val);
                        } else {
                            this.appendText(out, kv.getKey(), val);
                        }
                    }
                }

                out.write(this.endFlag.getBytes(this.charset));
                out.flush();
            }

            private void appendResource(OutputStream outputStream, String key, Resource value) throws IOException {
                StringBuilder builder = new StringBuilder(1024);

                // append 头部信息
                builder.append(separator).append(lineFeed);
                builder.append(String.format(fileFormat, key, value.filename, value.contentType)).append(lineFeed);
                builder.append(lineFeed);
                outputStream.write(builder.toString().getBytes(value.charset));
                // append 实体
                IoUtil.transferTo(value.inputStream, outputStream);
                // append 换行
                outputStream.write(lineFeed.getBytes(this.charset));
                outputStream.flush();
            }

            private void appendText(OutputStream outputStream, String key, Object value) throws IOException {
                StringBuilder builder = new StringBuilder(1024);

                // append 头部信息
                builder.append(separator).append(lineFeed);
                builder.append(String.format(textFormat, key)).append(lineFeed);
                builder.append(lineFeed);
                // append 实体
                builder.append(value);
                outputStream.write(builder.toString().getBytes(this.charset));
                // append 换行
                outputStream.write(lineFeed.getBytes(this.charset));
                outputStream.flush();
            }
        }

        /* ---------------------------------- 实现 Param 的内部类 ----------------------------- end   */

        /* ---------------------------------- 提供快捷实现静态方法 ----------------------------- start   */

        public static ParamRaw ofRaw(String contentType, InputStream body) {
            return new ParamRaw(contentType, body);
        }

        public static ParamForm ofForm(Charset charset) {
            return new ParamForm(charset);
        }

        public static ParamForm ofForm() {
            return ofForm(Constant.defaultCharset);
        }

        public static ParamFormData ofFormData(Charset charset) {
            return new ParamFormData(charset);
        }

        public static ParamFormData ofFormData() {
            return ofFormData(Constant.defaultCharset);
        }

        /* ---------------------------------- 提供快捷实现静态方法 ----------------------------- end   */

    }
    // =============================================== Response (响应对象) ======================================================= //

    /**
     * 响应对象
     * @param <T> 响应体类型
     */
    public static class Response<T> {

        /** 执行者 */
        private Executor executor;
        /** 响应处理 */
        private BodyHandler<T> bodyHandler;
        /***/
        /** HttpURLConnection */
        private HttpURLConnection http;
        /** 请求url */
        private String url;
        /** 重定向的url列表 */
        private List<String> redirectUrlList;
        /** http响应状态码(HttpURLConnection.HTTP_OK) */
        private int statusCode = -1;
        /** 响应头信息 */
        private Map<String, List<String>> header;
        /** cookie ex:key2=val2; key1=val1 */
        private Map<String, String> cookie;
        /** 携带参数(可使用于响应之后的操作) */
        private Map<String, Object> extra;
        /** 响应体 */
        private T body;
        /** 错误信息 */
        private String errorMessage;

        private Response() {}

        protected Response(Executor executor, BodyHandler<T> bodyHandler) {
            this.executor = executor;
            this.bodyHandler = bodyHandler;
            this.init();
        }

        protected static Response<Object> getErrorResponse(Request request, Throwable e) {
            Response<Object> errorResponse = new Response<>();
            errorResponse.http = null;
            errorResponse.url = request.getUrl();
            errorResponse.redirectUrlList = Collections.emptyList();
            errorResponse.statusCode = 400;
            errorResponse.header = Collections.emptyMap();
            errorResponse.cookie = Collections.emptyMap();
            errorResponse.extra = new HashMap<>(request.getExtra());
            errorResponse.body = null;
            errorResponse.errorMessage = Util.getThrowableStackTrace(e);
            return errorResponse;
        }

        private void init() {
            try {
                this.http = this.executor.getHttp();
                this.url = this.executor.getRequest().getUrl();
                this.redirectUrlList = this.executor.getRedirectUrlList();
                this.redirectUrlList = this.redirectUrlList == null ? Collections.emptyList() : this.redirectUrlList;
                this.statusCode = this.executor.getHttp().getResponseCode();
                this.header = this.executor.getHttp().getHeaderFields();
                this.cookie = this.parseCookieAsMap();
                this.extra = new HashMap<>(this.executor.getRequest().getExtra());
                this.handleHttpClientSession();
                if (this.bodyHandler != null) {
                    this.body = this.bodyHandler.accept(this.executor.getRequest(), this.http);
                }
            } catch (IOException e) {
                throw new RuntimeException(String.format("%s init HttpResponse has exception", this.url), e);
            } finally {
                if (this.body == null) {
                    http.disconnect();
                }
            }
        }

        private void handleHttpClientSession() {
            Session session = this.executor.getRequest().getClient().session();
            session.setReferer(this.url);
            session.addCookie(this.cookie);
            session.addExtra(this.extra);
        }

        /** 获取 cookieMap */
        private Map<String, String> parseCookieAsMap() {
            List<String> cookieList = this.header.get(Constant.RESPONSE_COOKIE);
            Map<String, String> cookieMap = Collections.emptyMap();
            if(Util.isNotEmpty(cookieList)) {
                cookieMap = new HashMap<>(cookieList.size());
                if(Util.isNotEmpty(cookieList)) {
                    for(String cookieObj : cookieList) {
                        String[] split = cookieObj.split(Constant.COOKIE_SPLIT);
                        if(split.length > 0) {
                            String[] keyAndVal = split[0].split(Constant.EQU, 2);
                            cookieMap.put(keyAndVal[0], keyAndVal[1]);
                        }
                    }
                }
            }
            return cookieMap;
        }

        /* ---------------------------------------------- getter ---------------------------------------------- start */

        public String getUrl() {
            return this.url;
        }

        public List<String> getRedirectUrlList() {
            return this.redirectUrlList;
        }

        public HttpURLConnection getHttp() {
            return this.http;
        }

        public int getStatusCode() {
            return this.statusCode;
        }

        public Map<String, List<String>> getHeader() {
            return this.header;
        }

        public Map<String, String> getCookie() {
            return this.cookie;
        }

        public T getBody() {
            return this.body;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public Map<String, Object> getExtra() {
            return this.extra;
        }

        /* ---------------------------------------------- getter ---------------------------------------------- end */

        /* ---------------------------------------------- toString -------------------------------------------- start */

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder("Response{");
            builder.append("executor=").append(this.executor);
            builder.append(", bodyHandler=").append(this.bodyHandler);
            builder.append(", http=").append(this.http);
            builder.append(", url='").append(this.url).append('\'');
            builder.append(", redirectUrlList=").append(this.redirectUrlList);
            builder.append(", statusCode=").append(this.statusCode);
            builder.append(", header=").append(this.header);
            builder.append(", cookie=").append(this.cookie);
            builder.append(", extra=").append(this.extra);
            builder.append(", body=").append(this.body);
            builder.append('}');
            return builder.toString();
        }

        /* ---------------------------------------------- toString -------------------------------------------- end */
    }
    // =============================================== BodyHandler (响应处理接口) ================================================ //

    /**
     * 响应处理接口
     * @param <T> 响应体类型
     */
    public static interface BodyHandler<T> {

        /**
         * 回调接口
         * @param request Request 对象
         * @param http HttpURLConnection 对象
         * @return
         * @throws IOException
         */
        T accept(Request request, HttpURLConnection http) throws IOException;
    }
    // =============================================== CallbackByteArray (回调 byte[] 接口) ===================================== //

    /**
     * 回调 byte[] 接口
     */
    public static interface CallbackByteArray {

        /**
         * 回调 byte[] 方法
         * @param data byte[] 对象
         * @param index byte[] 的开始下标( 通常是0 )
         * @param length byte[] 结束下标
         */
        void accept(byte[] data, int index, int length) throws IOException;
    }
    // =============================================== BodyHandlers (响应处理工具) =============================================== //

    /**
     * 响应处理工具
     */
    public static abstract class BodyHandlers {

        public static BodyHandler<InputStream> ofInputStream() {
            return (request, http) -> {
                InputStream inputStream = http.getResponseCode() < 400 ? http.getInputStream() : http.getErrorStream();
                // 获取响应头是否有Content-Encoding=gzip
                String gzip = http.getHeaderField(Constant.CONTENT_ENCODING);
                if(Util.isNotEmpty(gzip) && gzip.contains(Constant.GZIP)) {
                    inputStream = new GZIPInputStream(inputStream);
                }
                return new InputStreamWrapper(http, inputStream);
            };
        }

        public static BodyHandler<Void> ofCallbackByteArray(CallbackByteArray callback) {
            return (request, http) -> {
                try(InputStream inputStream = ofInputStream().accept(request, http)) {
                    byte[] bytes = new byte[1024 * 3];
                    for(int i; (i = inputStream.read(bytes)) > -1; ) {
                        callback.accept(bytes, 0, i);
                    }
                    return null;
                }
            };
        }

        public static BodyHandler<byte[]> ofByteArray() {
            return (request, http) -> {
                try(ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                    ofCallbackByteArray((data, index, length) -> {
                        outputStream.write(data, index, length);
                        outputStream.flush();
                    }).accept(request, http);
                    return outputStream.toByteArray();
                }
            };
        }

        public static BodyHandler<String> ofString(Charset... charset) {
            return (request, http) -> {
                byte[] body = ofByteArray().accept(request, http);
                Charset currentCharset = charset != null && charset.length > 0 ? charset[0] : Constant.defaultCharset;
                return new String(body, currentCharset);
            };
        }

        public static BodyHandler<Path> ofFile(Path path) {
            return (request, http) -> {
                try(OutputStream outputStream = new FileOutputStream(path.toFile())) {
                    ofCallbackByteArray(outputStream::write).accept(request, http);
                    return path;
                }
            };
        }

    }
    // =============================================== Method (请求方法) ======================================================== //

    /**
     * 请求方法
     */
    public static enum Method {GET, POST, PUT, PATCH, DELETE, OPTIONS, HEAD}
    // =============================================== Proxy (代理对象) ========================================================= //

    /**
     * 代理对象
     */
    public static class Proxy {

        /** 主机 */
        private String host;
        /** 端口 */
        private Integer port;
        /** 用户名 */
        private String username;
        /** 密码 */
        private String password;

        protected Proxy(String host, Integer port) {
            this(host, port, null, null);
        }

        protected Proxy(String host, Integer port, String username, String password) {
            this.host = host;
            this.port = port;
            this.username = username;
            this.password = password;
        }

        public static Proxy of(String host, Integer port) {
            return of(host, port, null, null);
        }

        public static Proxy of(String host, Integer port, String username, String password) {
            return new Proxy(host, port, username, password);
        }

        /* ---------------------------------------------- getter ---------------------------------------------- start */

        public String getHost() {
            return this.host;
        }

        public Integer getPort() {
            return this.port;
        }

        public String getUsername() {
            return this.username;
        }

        public String getPassword() {
            return this.password;
        }

        /* ---------------------------------------------- getter ---------------------------------------------- end   */

        /* ---------------------------------------------- toString -------------------------------------------- start */

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder("Proxy{");
            builder.append("host='").append(this.host).append('\'');
            builder.append(", port=").append(this.port);
            builder.append(", username='").append(this.username).append('\'');
            builder.append(", password='").append(this.password).append('\'');
            builder.append('}');
            return builder.toString();
        }

        /* ---------------------------------------------- toString -------------------------------------------- end   */
    }
    // =============================================== Constant (常量) ========================================================== //

    /**
     * 常量
     */
    public static interface Constant {

        String CONTENT_LENGTH = "Content-Length";
        String CONTENT_TYPE = "Content-Type";
        /** 获取响应的COOKIE */
        String RESPONSE_COOKIE = "Set-Cookie";
        /** 设置发送的COOKIE */
        String REQUEST_COOKIE = "Cookie";
        String REFERER = "Referer";
        String PROXY_AUTHORIZATION = "Proxy-Authorization";
        String CONTENT_ENCODING = "Content-Encoding";
        String LOCATION = "Location";

        String CONTENT_TYPE_WITH_FORM = "application/x-www-form-urlencoded; charset=";
        String CONTENT_TYPE_WITH_FORM_DATA = "multipart/form-data; boundary=";
        String CONTENT_TYPE_WITH_JSON = "application/json; charset=";
        String GZIP = "gzip";

        int REDIRECT_CODE_301 = 301;
        int REDIRECT_CODE_302 = 302;
        int REDIRECT_CODE_303 = 303;
        Set<Integer> REDIRECT_CODES = new HashSet<>(Arrays.asList(REDIRECT_CODE_301, REDIRECT_CODE_302, REDIRECT_CODE_303));

        String COOKIE_SPLIT = "; ";
        String EQU = "=";
        String HTTP = "http";
        String AND_SIGN = "&";
        String queryFlag = "?";

        Charset defaultCharset = Charset.defaultCharset();
    }
    // =============================================== Util (工具类) ============================================================ //

    /**
     * 工具类
     */
    public static abstract class Util {

        public static boolean isEmpty(Object o) {
            if(o == null) {
                return true;
            }
            if(o instanceof String) {
                return ((String) o).isEmpty();
            } else if(o instanceof Collection) {
                return ((Collection) o).isEmpty();
            } else if(o instanceof Map) {
                return ((Map) o).isEmpty();
            } else if(o instanceof Object[]) {
                return ((Object[]) o).length == 0;
            } else {
                return false;
            }
        }

        public static boolean isNotEmpty(Object o) {
            return !isEmpty(o);
        }

        public static <T> T emptyOfDefault(T t, T defaultValue) {
            return isEmpty(t) ? defaultValue : t;
        }

        public static <T> T nullOfDefault(T t, T defaultValue) {
            return t == null ? defaultValue : t;
        }

        /** url 编码 */
        public static String urlEncode(String text, Charset charset) {
            if(isNotEmpty(text) && isNotEmpty(charset)) {
                // 不为空 并且charset可用
                try {
                    return URLEncoder.encode(text, charset.name());
                } catch(UnsupportedEncodingException e) {
                    // do non thing
                }
            }
            return text;
        }

        /**
         * @description Map => key1=val1&key2=val2
         * @date 2019-08-20 20:42:59
         * @author houyu for.houyu@foxmail.com
         */
        public static String paramMapAsString(MultiMap<Object> paramMap, Charset charset) {
            if (isNotEmpty(paramMap)) {
                StringBuilder builder = new StringBuilder(128);
                paramMap.forEach(kv -> {
                    // urlEncode : if charset is empty not do Encode
                    for (Object val : kv.getValues()) {
                        builder.append(urlEncode(kv.getKey(), charset));
                        builder.append(Constant.EQU);
                        builder.append(urlEncode(String.valueOf(val), charset));
                        builder.append(Constant.AND_SIGN);
                    }
                });

                return builder.delete(builder.length() - 1, builder.length()).toString();
            }
            return "";
        }

        /**
         * 把浏览器的Form字符串转为Map
         */
        public static Map<String, Object> parseFormStringAsMap(String s) {
            String[] split = s.split("\n");
            Map<String, Object> targetMap = new HashMap<>(split.length);
            for(String keyAndVal : split) {
                String[] keyVal = keyAndVal.split(": ", 2);
                targetMap.put(keyVal[0], keyVal[1]);
            }
            return targetMap;
        }

        /**
         * 获取错误的详细信息
         */
        @SuppressWarnings("Duplicates")
        public static String getThrowableStackTrace(Throwable e) {
            try (ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream()) {
                e.printStackTrace(new PrintWriter(arrayOutputStream, true));
                return arrayOutputStream.toString();
            } catch(IOException e1) {
                e1.printStackTrace();
                return "";
            }
        }

    }

    static class InputStreamWrapper extends InputStream {
        private HttpURLConnection http;
        private InputStream in;

        public InputStreamWrapper(HttpURLConnection http, InputStream in) {
            this.http = http;
            this.in = in;
        }

        @Override
        public int read() throws IOException {
            return in.read();
        }

        @Override
        public void close() throws IOException {
            try {
                in.close();
            } finally {
                http.disconnect();
            }
        }
    }
}