package org.noear.solon.boot.reactornetty;

import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import org.noear.solon.core.handle.UploadedFile;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 对 Request 的参数进行解析
 * */
class HttpRequestParser {
    private final DefaultFullHttpRequest _request;

    protected final Map<String, List<String>> parmMap = new HashMap<>();
    protected final Map<String, List<UploadedFile>> fileMap = new HashMap<>();

    public HttpRequestParser(DefaultFullHttpRequest req) {
        _request = req;
    }

    protected HttpRequestParser parse() throws Exception {
        HttpMethod method = _request.method();

        // url 参数
        if(_request.uri().indexOf("?") > 0) {
            QueryStringDecoder decoder = new QueryStringDecoder(_request.uri());

            decoder.parameters().entrySet().forEach(entry -> {
                parmMap.put(entry.getKey(), entry.getValue());
            });
        }


        // body
        if (HttpMethod.POST == method
                || HttpMethod.PUT == method
                ||  HttpMethod.DELETE == method
                || HttpMethod.PATCH == method) {

            HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(_request);
            decoder.offer(_request);

            List<InterfaceHttpData> parmList = decoder.getBodyHttpDatas();

            for (InterfaceHttpData p1 : parmList) {
                if(p1.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute){
                    //参数
                    //
                    Attribute a1 = (Attribute) p1;

                    List<String> tmp = parmMap.get(a1.getName());
                    if(tmp == null){
                        tmp = new ArrayList<>();
                        parmMap.put(a1.getName(), tmp);
                    }

                    tmp.add(a1.getValue());
                    continue;
                }

                if(p1.getHttpDataType() == InterfaceHttpData.HttpDataType.FileUpload){
                    //文件
                    //
                    FileUpload f0 = (FileUpload) p1;

                    List<UploadedFile> tmp = fileMap.get(p1.getName());
                    if(tmp == null){
                        tmp = new ArrayList<>();
                        fileMap.put(p1.getName(), tmp);
                    }

                    UploadedFile f1 = new UploadedFile();
                    f1.contentType = f0.getContentType();
                    f1.content = new FileInputStream(f0.getFile());
                    f1.contentSize = f1.content.available();
                    f1.name = f0.getFilename();

                    int idx = f1.name.lastIndexOf(".");
                    if(idx>0){
                        f1.extension = f1.name.substring(idx+1);
                    }

                    tmp.add(f1);
                }
            }
        }

        return this;
    }
}
