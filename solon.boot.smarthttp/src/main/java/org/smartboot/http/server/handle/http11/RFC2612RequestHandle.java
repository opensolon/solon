package org.smartboot.http.server.handle.http11;

import org.apache.commons.lang.StringUtils;
import org.smartboot.http.HttpRequest;
import org.smartboot.http.HttpResponse;
import org.smartboot.http.enums.HttpStatus;
import org.smartboot.http.enums.MethodEnum;
import org.smartboot.http.exception.HttpException;
import org.smartboot.http.server.handle.HttpHandle;
import org.smartboot.http.utils.HttpHeaderConstant;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 三刀
 * @version V1.0 , 2018/6/3
 */
public class RFC2612RequestHandle extends HttpHandle {
    public static final int MAX_LENGTH = 255 * 1024;
    private Map<String, UriCache> uriCacheMap = new HashMap<>();

    @Override
    public void doHandle(HttpRequest request, HttpResponse response) throws IOException {
        methodCheck(request);
        hostCheck(request);
        uriCheck(request);
        doNext(request, response);
    }

    /**
     * RFC2616 5.1.1 方法标记指明了在被 Request-URI 指定的资源上执行的方法。
     * 这种方法是大小写敏感的。 资源所允许的方法由 Allow 头域指定(14.7 节)。
     * 响应的返回码总是通知客户某个方法对当前资源是否是被允许的，因为被允许的方法能被动态的改变。
     * 如果服务器能理解某方法但此方法对请求资源不被允许的，
     * 那么源服务器应该返回 405 状态码(方法不允许);
     * 如果源服务器不能识别或没有实现某个方法，那么服务器应返回 501 状态码(没有实现)。
     * 方法 GET 和 HEAD 必须被所有一般的服务器支持。 所有其它的方法是可选的;
     * 然而，如果上面的方法都被实现， 这些方法遵循的语意必须和第 9 章指定的相同
     *
     * @param request
     */
    private void methodCheck(HttpRequest request) {
        MethodEnum methodEnum = request.getMethodRange();//大小写敏感
        if (methodEnum == null) {
            throw new HttpException(HttpStatus.NOT_IMPLEMENTED);
        }

        //暂时只支持GET/POST
        if (methodEnum != MethodEnum.GET && methodEnum != MethodEnum.POST) {
            throw new HttpException(HttpStatus.METHOD_NOT_ALLOWED);
        }
    }

    /**
     * 1、客户端和服务器都必须支持 Host 请求头域。
     * 2、发送 HTTP/1.1 请求的客户端必须发送 Host 头域。
     * 3、如果 HTTP/1.1 请求不包括 Host 请求头域，服务器必须报告错误 400(Bad Request)。 --服务器必须接受绝对 URIs(absolute URIs)。
     *
     * @param request
     */
    private void hostCheck(HttpRequest request) {
        if (request.getHeader(HttpHeaderConstant.Names.HOST) == null) {
            throw new HttpException(HttpStatus.BAD_REQUEST);
        }
    }


    /**
     * RFC2616 3.2.1
     * HTTP 协议不对 URI 的长度作事先的限制，服务器必须能够处理任何他们提供资源的 URI，并 且应该能够处理无限长度的 URIs，这种无效长度的 URL 可能会在客户端以基于 GET 方式的 请求时产生。如果服务器不能处理太长的 URI 的时候，服务器应该返回 414 状态码(此状态码 代表 Request-URI 太长)。
     * 注:服务器在依赖大于 255 字节的 URI 时应谨慎，因为一些旧的客户或代理实现可能不支持这 些长度。
     *
     * @param request
     */
    private void uriCheck(HttpRequest request) {
        if (StringUtils.length(request.getOriginalUri()) > MAX_LENGTH) {
            throw new HttpException(HttpStatus.URI_TOO_LONG);
        }
        /**
         *http_URL = "http:" "//" host [ ":" port ] [ abs_path [ "?" query ]]
         *1. 如果 Request-URI 是绝对地址(absoluteURI)，那么主机(host)是 Request-URI 的 一部分。任何出现在请求里 Host 头域的值应当被忽略。
         *2. 假如 Request-URI 不是绝对地址(absoluteURI)，并且请求包括一个 Host 头域，则主 机(host)由该 Host 头域的值决定.
         *3. 假如由规则1或规则2定义的主机(host)对服务器来说是一个无效的主机(host)， 则应当以一个 400(坏请求)错误消息返回。
         */
        String originalUri = request.getOriginalUri();
        UriCache uriCache = uriCacheMap.get(originalUri);
        if (uriCache != null) {
            request.setRequestURI(uriCache.uri);
            request.setQueryString(uriCache.queryString);
            return;
        }
        int schemeIndex = originalUri.indexOf("://");
        int queryStringIndex = StringUtils.indexOf(originalUri, "?", schemeIndex);
        String queryString = null;
        if (queryStringIndex != StringUtils.INDEX_NOT_FOUND) {
            queryString = StringUtils.substring(originalUri, queryStringIndex + 1);
            request.setQueryString(queryString);
        }

        if (schemeIndex > 0) {//绝对路径
//            request.setScheme(originalUri.substring(0, schemeIndex));
            int uriIndex = originalUri.indexOf('/', schemeIndex + 3);
            if (uriIndex == StringUtils.INDEX_NOT_FOUND) {
                request.setRequestURI("/");
            } else {

                request.setRequestURI(queryStringIndex > 0 ?
                        StringUtils.substring(originalUri, uriIndex, queryStringIndex)
                        : StringUtils.substring(originalUri, uriIndex));
            }

        } else {
            request.setRequestURI(queryStringIndex > 0 ?
                    StringUtils.substring(originalUri, 0, queryStringIndex)
                    : originalUri);
        }
        uriCacheMap.put(originalUri, new UriCache(request.getRequestURI(), queryString));
    }

    private class UriCache {
        private String uri;
        private String queryString;

        public UriCache(String uri, String queryString) {
            this.uri = uri;
            this.queryString = queryString;
        }

        public String getUri() {
            return uri;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }
    }
}
