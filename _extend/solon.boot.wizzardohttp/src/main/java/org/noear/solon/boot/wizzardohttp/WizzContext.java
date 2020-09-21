package org.noear.solon.boot.wizzardohttp;


import com.wizzardo.http.MultiValue;
import com.wizzardo.http.request.Request;
import com.wizzardo.http.response.Response;

import com.wizzardo.http.response.Status;
import org.noear.solon.XUtil;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XFile;
import org.noear.solon.core.XMap;
import org.noear.solon.core.XSessionState;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.*;



public class WizzContext extends XContext {
    private Request _request;
    private Response _response;
    protected Map<String,List<XFile>> _fileMap;

    static final String Content_Type = "Content-Type";

    public WizzContext(Request request, Response response) {
        _request = request;
        _response = response;

        if(sessionState().replaceable()){
            sessionStateInit(new XSessionState() {
                @Override
                public String sessionId() {
                    return _request.session().getId();
                }

                @Override
                public Object sessionGet(String key) {
                    return _request.session().get(key);
                }

                @Override
                public void sessionSet(String key, Object val) {
                    _request.session().put(key,val);
                }
            });
        }


        //文件上传需要
//        if (isMultipart()) {
//            try {
//                _fileMap = new HashMap<>();
//
//                MultipartUtil.buildParamsAndFiles(this);
//            } catch (Throwable ex) {
//                ex.printStackTrace();
//            }
//        }
    }

    @Override
    public Object request() {
        return _request;
    }

    private String _ip;
    @Override
    public String ip() {
        if(_ip == null) {
            _ip = header("X-Forwarded-For");

            if (_ip == null) {
                _ip = _request.connection().getIp();
            }
        }

        return _ip;
    }

    @Override
    public String method() {
        return _request.method().name();
    }

    @Override
    public String protocol() {
        return _request.protocol();
    }

    @Override
    public URI uri() {
        if(_uri == null) {
            _uri = URI.create(url());
        }

        return _uri;
    }
    private URI _uri;

    @Override
    public String path() {
        return uri().getPath();
    }

    @Override
    public String url() {
        return _request.path().toString();
    }

    @Override
    public long contentLength() {
        return _request.contentLength();
    }

    @Override
    public String contentType() {
        return _request.header(Content_Type);
    }


    @Override
    public InputStream bodyAsStream() throws IOException {
        return _request.getInputStream();
    }

    @Override
    public String[] paramValues(String key){
        return  (String[]) _request.params(key).toArray();
    }

    @Override
    public String param(String key) {
        //要充许为字符串
        //默认不能为null
        return paramMap().get(key);
    }

    @Override
    public String param(String key, String def) {
        String temp = paramMap().get(key); //因为会添加参数，所以必须用这个

        if(XUtil.isEmpty(temp)){
            return def;
        }else{
            return temp;
        }
    }


    private XMap _paramMap;
    @Override
    public XMap paramMap() {
        if (_paramMap == null) {
            _paramMap = new XMap();

            _request.params().forEach((k, vv)->{
                _paramMap.put(k, vv.value());
            });
        }

        return _paramMap;
    }

    private Map<String, List<String>> _paramsMap;
    @Override
    public Map<String, List<String>> paramsMap() {
        if (_paramsMap == null) {
            _paramsMap = new LinkedHashMap<>();

            _request.params().forEach((k, vv) -> {
                _paramsMap.put(k, vv.getValues());
            });
        }

        return _paramsMap;
    }

    @Override
    public List<XFile> files(String key) throws Exception{
        if (isMultipartFormData()){
            List<XFile> temp = _fileMap.get(key);
            if(temp == null){
                return new ArrayList<>();
            }else{
                return temp;
            }
        }  else {
            return new ArrayList<>();
        }
    }

    private XMap _cookieMap;

    @Override
    public XMap cookieMap() {
        if (_cookieMap == null) {
            _cookieMap = new XMap();

            _cookieMap.putAll(_request.cookies());
        }

        return _cookieMap;
    }

    @Override
    public XMap headerMap() {
        if (_headerMap == null) {
            _headerMap = new XMap();

            for (Object kv : _request.headers().entrySet()) {
                Map.Entry<String, MultiValue> kv2 = (Map.Entry<String, MultiValue>) kv;
                _headerMap.put(kv2.getKey(), kv2.getValue().getValue());
            }
        }

        return _headerMap;
    }
    private XMap _headerMap;



    //====================================

    @Override
    public Object response() {
        return _response;
    }


    @Override
    protected void contentTypeDoSet(String contentType) {
        if (_charset != null) {
            if (contentType.indexOf(";") < 0) {
                headerSet(Content_Type, contentType + ";charset=" + _charset);
                return;
            }
        }

        headerSet(Content_Type, contentType);
    }

    @Override
    public OutputStream outputStream() throws IOException {
        return _response.getOutputStream(_request.connection());
    }

    @Override
    public void output(byte[] bytes) {
        try {
            OutputStream out = outputStream();

            if(out == null){ // on HEAD method
                return;
            }

            out.write(bytes);
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void output(InputStream stream) {
        try {
            OutputStream out = outputStream();

            if(out == null){ // on HEAD method
                return;
            }

            int len = 0;
            byte[] buf = new byte[512]; //0.5k
            while ((len = stream.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void headerSet(String key, String val) {
        _response.setHeader(key,val);
    }

    @Override
    public void headerAdd(String key, String val) {
        _response.appendHeader(key,val);
    }

    @Override
    public void cookieSet(String key, String val, String domain, String path, int maxAge) {
        StringBuilder sb = new StringBuilder();
        sb.append(key).append("=").append(val).append(";");

        if (XUtil.isNotEmpty(path)) {
            sb.append("path=").append(path).append(";");
        }

        if (maxAge >= 0) {
            sb.append("max-age=").append(maxAge).append(";");
        }

        if (XUtil.isNotEmpty(domain)) {
            sb.append("domain=").append(domain.toLowerCase()).append(";");
        }

        _response.appendHeader("Set-Cookie", sb.toString());
    }

    @Override
    public void redirect(String url) {
        try {
            _response.setRedirectTemporarily(url);//302
        }catch (Throwable ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void redirect(String url, int code)  {
        statusSet(code);
        _response.setHeader("Location", url);
    }

    @Override
    public int status() {
        return _response.status().code;
    }

    @Override
    public void statusSet(int status) {
        _response.setStatus(Status.valueOf(status));
    }
}
