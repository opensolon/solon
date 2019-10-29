package org.smartboot.http.server.decode;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.smartboot.http.HttpRequest;
import org.smartboot.http.enums.MethodEnum;
import org.smartboot.http.enums.State;
import org.smartboot.http.utils.Consts;
import org.smartboot.http.utils.EmptyInputStream;
import org.smartboot.http.utils.HttpHeaderConstant;
import org.smartboot.socket.extension.decoder.DelimiterFrameDecoder;
import org.smartboot.socket.extension.decoder.SmartDecoder;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 三刀
 * @version V1.0 , 2018/8/31
 */
public class Http11Request implements HttpRequest {
    State state = State.method;
    MethodEnum methodEnum;
    String originalUri;
    String protocol;
    Map<String, String> headMap = new HashMap<>();
    String tmpHeaderName;
    boolean tmpValEnable = false;
    DelimiterFrameDecoder tmpHeaderValue = new DelimiterFrameDecoder(new byte[]{Consts.CR}, 1024);
    SmartDecoder bodyContentDecoder;
    private Map<String, String> paramMap;
    //noear::这是新加的(在 paramMap 之外，再搞个 paramList)
    private List<String[]> paramList;
    private InputStream inputStream;
    private String requestUri;
    private String contentType;
    private int contentLength = -1;


    @Override
    public String getHeader(String headName) {
        return headMap.get(headName);
    }

    @Override
    public Map<String, String> getHeadMap() {
        return headMap;
    }

    @Override
    public InputStream getInputStream() {
        return this.inputStream == null ? new EmptyInputStream() : this.inputStream;
    }

    void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public String getRequestURI() {
        return requestUri;
    }

    @Override
    public void setRequestURI(String uri) {
        this.requestUri = uri;
    }

    @Override
    public String getProtocol() {
        return protocol;
    }

    @Override
    public MethodEnum getMethodRange() {
        return methodEnum;
    }

    @Override
    public String getOriginalUri() {
        return originalUri;
    }

    @Override
    public void setQueryString(String queryString) {

    }

    @Override
    public String getContentType() {
        return contentType == null ? contentType = headMap.get(HttpHeaderConstant.Names.CONTENT_TYPE) : contentType;
    }

    @Override
    public int getContentLength() {
        return contentLength;
    }

    void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }


    //noear::这是新加的
    private void initParameter() {
        if (paramMap != null) {
            return;
        }
        paramMap = new HashMap<>();
        paramList = new ArrayList<>();

        //识别url中的参数
        String urlParamStr = StringUtils.substringAfter(originalUri, "?");
        if (StringUtils.isNotBlank(urlParamStr)) {
            urlParamStr = StringUtils.substringBefore(urlParamStr, "#");
            decodeParamString(urlParamStr, paramMap);
        }

        //识别body中的参数
        if (bodyContentDecoder != null) {

            ByteBuffer buffer = bodyContentDecoder.getBuffer();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            decodeParamString(new String(bytes), paramMap);
        }
    }

    //noear::这里改写了一下
    @Override
    public String getParameter(String name) {
        initParameter();

        return paramMap.get(name);
    }

    //noear::这是新加的
    @Override
    public Map<String, String> getParameterMap() {
        initParameter();

        return paramMap;
    }

    //noear::这是新加的
    @Override
    public List<String[]> getParameterList() {
        initParameter();

        return paramList;
    }

    private void decodeParamString(String paramStr, Map<String, String> paramMap) {
        if (StringUtils.isBlank(paramStr)) {
            return;
        }
        String[] uriParamStrArray = StringUtils.split(paramStr, "&");
        for (String param : uriParamStrArray) {
            int index = param.indexOf("=");
            if (index == -1) {
                continue;
            }
            try {
                String name = StringUtils.substring(param, 0, index);
                String value = URLDecoder.decode(StringUtils.substring(param, index + 1), "utf8");

                paramMap.put(name,value);
                paramList.add(new String[]{name,value});
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    public void rest() {
        state = State.method;
        headMap.clear();
        tmpHeaderName = null;
        tmpValEnable = false;
        tmpHeaderValue.reset();
        bodyContentDecoder = null;
        originalUri = null;
        paramMap = null;
        contentType = null;
        contentLength = -1;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
