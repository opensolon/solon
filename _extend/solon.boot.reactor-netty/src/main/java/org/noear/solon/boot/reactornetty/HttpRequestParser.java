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
                    FileUpload f1 = (FileUpload) p1;

                    List<UploadedFile> tmp = fileMap.get(p1.getName());
                    if(tmp == null){
                        tmp = new ArrayList<>();
                        fileMap.put(p1.getName(), tmp);
                    }

                    UploadedFile file = new UploadedFile();
                    file.name = f1.getFilename();
                    file.contentType = f1.getContentType();
                    file.content = new FileInputStream(f1.getFile());
                    int idx = file.name.lastIndexOf(".");
                    if(idx>0){
                        file.extension = file.name.substring(idx+1);
                    }

                    tmp.add(file);
                }
            }
        }

        return this;
    }
}
