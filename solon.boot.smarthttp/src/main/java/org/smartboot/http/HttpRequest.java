package org.smartboot.http;

import org.smartboot.http.enums.MethodEnum;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * @author 三刀
 * @version V1.0 , 2018/8/7
 */
public interface HttpRequest {

    String getHeader(String headName);

    //noear::这是新加的
    Map<String, String> getHeadMap();

    InputStream getInputStream();

    String getRequestURI();

    void setRequestURI(String uri);

    String getProtocol();

    MethodEnum getMethodRange();

    String getOriginalUri();

    void setQueryString(String queryString);

    String getContentType();

    int getContentLength();

    String getParameter(String name);

    Map<String,String> getParameterMap();
    //noear::这是新加的
    List<String[]> getParameterList();
}
