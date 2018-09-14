package org.noear.solonboot.jlhttp;

import org.noear.solonboot.XMap;
import org.noear.solonboot.XUtil;
import org.noear.solonboot.protocol.XContext;
import org.noear.solonboot.protocol.XHeader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class JlHttpContext extends XContext {
    private HTTPServer.Request _request;
    private HTTPServer.Response _response;

    public JlHttpContext(HTTPServer.Request request, HTTPServer.Response response) {
        _request = request;
        _response = response;
    }

    @Override
    public Object request() {
        return _request;
    }

    @Override
    public String ip() {
        return header("RemoteIp");
    }

    @Override
    public boolean isMultipart() {
        return header(XHeader.CONTENT_TYPE,"").toLowerCase().contains("multipart/");
    }

    @Override
    public String method() {
        return _request.getMethod();
    }

    @Override
    public String protocol() {
        return _request.getVersion();
    }

    @Override
    public URI uri() {
        return _request.getURI();
    }

    @Override
    public String path() {
        return uri().getPath();
    }

    @Override
    public String userAgent() {
        return header(XHeader.USER_AGENT);
    }

    @Override
    public String url() {
        return uri().toString();
    }

    @Override
    public long contentLength() {
        try {
            return _request.getBody().available();
        }catch (Exception ex){
            ex.printStackTrace();
            return 0;
        }
    }

    @Override
    public String contentType() {
        return header(XHeader.CONTENT_TYPE);
    }

    @Override
    public String body() throws IOException {
        InputStream inpStream = bodyAsStream();

        StringBuilder content = new StringBuilder();
        byte[] b = new byte[1024];
        int lens = -1;
        while ((lens = inpStream.read(b)) > 0) {
            content.append(new String(b, 0, lens));
        }

        return content.toString();
    }

    @Override
    public InputStream bodyAsStream() throws IOException {
        return _request.getBody();
    }

//    @Override
//    public byte[] bodyAsBytes() throws IOException {
//        InputStream inpStream = bodyAsStream();
//
//        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
//        byte[] buff = new byte[100];
//        int rc = 0;
//        while ((rc = inpStream.read(buff, 0, 100)) > 0) {
//            outStream.write(buff, 0, rc);
//        }
//
//        return outStream.toByteArray();
//    }
//
//    @Override
//    public <T> T bodyAsClass(Class<T> cls) throws IOException {
//        return XUtil.fromJson(body(),cls);
//    }

    @Override
    public String param(String key) {
        return param(key,null);
    }

    @Override
    public String param(String key, String def) {
        try {
            String temp = paramMap().get(key);

            if(XUtil.isEmpty(temp)){
                return def;
            }else{
                return temp;
            }
        }catch (Exception ex){
            ex.printStackTrace();

            return def;
        }
    }

    @Override
    public int paramAsInt(String key) {
        return Integer.parseInt(param(key,"0"));
    }

    @Override
    public long paramAsLong(String key) {
        return Long.parseLong(param(key,"0"));
    }

    @Override
    public double paramAsDouble(String key) {
        return Double.parseDouble(param(key,"0"));
    }


    private XMap _paramMap;
    @Override
    public XMap paramMap() {
        try {
            if(_paramMap == null) {
                _paramMap = new XMap(_request.getParams());

                String temp = uri().getQuery();
                if (temp != null) {
                    String[] ss = temp.split("&");
                    for (String s : ss) {
                        String[] kv = s.split("=");
                        if (kv.length == 2) {
                            _paramMap.put(kv[0], kv[1]);
                        }
                    }
                }
            }

            return _paramMap;
        }catch (Exception ex){
            ex.printStackTrace();
            return new XMap();
        }
    }

    @Override
    public String cookie(String key) {
        return cookieMap().get(key);
    }

    @Override
    public String cookie(String key, String def) {
        String temp = cookieMap().get(key);
        if(temp == null){
            return  def;
        }else{
            return temp;
        }
    }

    @Override
    public XMap cookieMap() {
        if(_cookieMap == null){
            _cookieMap = new XMap(_request.getHeaders().getParams("Cookie"));
        }

        return _cookieMap;
    }
    private XMap _cookieMap;


    @Override
    public String header(String key) {
        return _request.getHeaders().get(key);
    }

    @Override
    public String header(String key, String def) {
        String temp = _request.getHeaders().get(key);

        if (temp == null)
            return def;
        else
            return temp;
    }

    @Override
    public XMap headerMap() {
        if(_headerMap == null) {
            _headerMap = new XMap();

            HTTPServer.Headers headers = _request.getHeaders();

            if (headers != null) {
                for (HTTPServer.Header h : headers) {
                    _headerMap.put(h.getName(), h.getValue());
                }
            }
        }

        return _headerMap;
    }
    private XMap _headerMap;

    private static final String _session_id_key = "JLSESSIONID";


    private String _sessionId;
    @Override
    public String sessionId(){
        if(_sessionId == null) {
            _sessionId = cookie(_session_id_key);

            if (XUtil.isEmpty(_sessionId)) {
                _sessionId = XUtil.guid() + ".jlhttp";//生成的用_开头，可直接识别是不是ua
            }

            cookieSet(_session_id_key, _sessionId, 60 * 60 * 2); //每次都更新cookie//2小时
        }
        return _sessionId;
    }

    @Override
    public Object session(String key) {
        return null;
    }

    @Override
    public void sessionSet(String key, Object val) {

    }

    //=================================

    @Override
    public Object response() {
        return _response;
    }

    @Override
    public void charset(String charset) {
        _charset = charset;
    }
    private String _charset = "UTF-8";

    @Override
    public void contentType(String contentType) {
        headerSet(XHeader.CONTENT_TYPE, contentType);
    }

    @Override
    public void output(String str) throws IOException {
        OutputStream out = outputStream;

        out.write(str.getBytes(_charset));
        out.flush();
    }

    @Override
    public void output(InputStream stream) throws IOException {
        OutputStream out = outputStream;

        byte[] buff = new byte[100];
        int rc = 0;
        while ((rc = stream.read(buff, 0, 100)) > 0) {
            out.write(buff, 0, rc);
        }

        out.flush();
    }

    protected ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    //jlhttp 需要先输出 header ，但是 header 后面可能会有变化；所以不直接使用  response.getOutputStream()
    protected void close() throws IOException{
        outputStream.writeTo(_response.getOutputStream());
        _response.getOutputStream().close();
    }

//    @Override
//    public void outputHtml(String html) throws IOException{
//        contentType("text/html;charset=utf-8");
//        output(html);
//    }
//
//    @Override
//    public void outputJson(Object obj) throws IOException{
//        contentType("application/json;charset=utf-8");
//        output(XUtil.toJson(obj));
//    }

    @Override
    public void headerSet(String key, String val) {
        _response.getHeaders().replace(key, val);
    }


    @Override
    public void cookieSet(String key, String val, int maxAge) {
        cookieSet(key, val, null, maxAge);
    }

    @Override
    public void cookieSet(String key, String val, String domain, int maxAge) {
        if(_cookieSet == null){
            _cookieSet = new HashMap<>();
        }

        _cookieSet.put(key,val);
        StringBuilder sb = new StringBuilder();
        _cookieSet.forEach((k,v)->{
            sb.append(k).append("=").append(v).append(";");
        });

        sb.append("path=/;");
        sb.append("max-age=").append(maxAge).append(";");
        if(domain!=null) {
            sb.append("domain=").append(domain.toLowerCase()).append(";");
        }

        _response.getHeaders().replace("Set-Cookie", sb.toString());
    }

    private Map<String, String> _cookieSet;

    @Override
    public void cookieRemove(String key) {
        cookieSet(key,"",0);
    }

    @Override
    public void redirect(String url) throws IOException {
        redirect(url,302);
    }

    @Override
    public void redirect(String url, int code) throws IOException {
        headerSet(XHeader.LOCATION, url);
        _response.sendHeaders(code);
    }

    @Override
    public int status() {
        return _status;
    }
    private int _status = 200;

    @Override
    public void status(int status) throws IOException {
        _status = status;
        //_response.sendHeaders(status); //jlhttp 的 状态，由 上下文代理 负责
    }

}
