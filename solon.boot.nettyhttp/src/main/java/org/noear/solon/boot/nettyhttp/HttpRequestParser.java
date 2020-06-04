package org.noear.solon.boot.nettyhttp;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import org.noear.solon.core.XFile;

import java.io.FileInputStream;
import java.util.*;

/**
 * 对 Request 的参数进行解析
 * */
class HttpRequestParser {
    private final FullHttpRequest _request;

    protected final Map<String, List<String>> parmMap = new LinkedHashMap<>();
    protected final Map<String, List<XFile>> fileMap = new HashMap<>();


    public HttpRequestParser(FullHttpRequest request) {
        _request = request;
    }


    protected HttpRequestParser parse() throws Exception {
        HttpMethod method = _request.method();

        // url 参数
        if (_request.uri().indexOf("?") > 0) {
            QueryStringDecoder decoder = new QueryStringDecoder(_request.uri());

            decoder.parameters().entrySet().forEach(entry -> {
                parmMap.put(entry.getKey(), entry.getValue());
            });
        }


        // body
        if (HttpMethod.POST == method
                || HttpMethod.PUT == method
                || HttpMethod.DELETE == method
                || HttpMethod.PATCH == method) {

            parseBodyTry();
        }

        return this;
    }

    private void parseBodyTry() throws Exception{
        String ct = _request.headers().get("Content-Type");

        if (ct == null) {
            return;
        }

        ct = ct.toLowerCase(Locale.US);

        if (ct.startsWith("application/x-www-form-urlencoded") == false
                && ct.startsWith("multipart/") == false) {
            return;
        }

        HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(_request);
        decoder.offer(_request);

        List<InterfaceHttpData> parmList = decoder.getBodyHttpDatas();

        for (InterfaceHttpData p1 : parmList) {
            if (p1.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
                //参数
                //
                Attribute a1 = (Attribute) p1;

                List<String> tmp = parmMap.get(a1.getName());
                if (tmp == null) {
                    tmp = new ArrayList<>();
                    parmMap.put(a1.getName(), tmp);
                }

                tmp.add(a1.getValue());
                continue;
            }

            if (p1.getHttpDataType() == InterfaceHttpData.HttpDataType.FileUpload) {
                //文件
                //
                FileUpload f1 = (FileUpload) p1;

                List<XFile> tmp = fileMap.get(p1.getName());
                if (tmp == null) {
                    tmp = new ArrayList<>();
                    fileMap.put(p1.getName(), tmp);
                }

                XFile file = new XFile();
                file.name = f1.getFilename();
                file.contentType = f1.getContentType();
                file.content = new FileInputStream(f1.getFile());
                int idx = file.name.lastIndexOf(".");
                if (idx > 0) {
                    file.extension = file.name.substring(idx + 1);
                }

                tmp.add(file);
            }
        }
    }
}
