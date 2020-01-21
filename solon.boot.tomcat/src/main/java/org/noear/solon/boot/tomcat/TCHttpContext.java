package org.noear.solon.boot.tomcat;

import org.noear.solon.XUtil;
import org.noear.solon.boot.tomcat.ext.MultipartUtil;
import org.noear.solon.core.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

public class TCHttpContext extends XContext{
    private HttpServletRequest _request;
    private HttpServletResponse _response;

    public TCHttpContext(HttpServletRequest request, HttpServletResponse response) {
        _request = request;
        _response = response;

        if(sessionState().replaceable()) {
            sessionStateInit(new XSessionState() {
                @Override
                public String sessionId() {
                    return _request.getRequestedSessionId();
                }

                @Override
                public Object sessionGet(String key) {
                    return _request.getSession().getAttribute(key);
                }

                @Override
                public void sessionSet(String key, Object val) {
                    _request.getSession().setAttribute(key, val);
                }
            });
        }
    }

    @Override
    public Object request() {
        return _request;
    }

    @Override
    public String ip() {
        return _request.getRemoteAddr();
    }

    @Override
    public String method() {
        return _request.getMethod();
    }

    @Override
    public String protocol() {
        return _request.getProtocol();
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
        return _request.getRequestURI();
    }

    @Override
    public String url() {
        return _request.getRequestURL().toString();
    }

    @Override
    public long contentLength() {
        return _request.getContentLength();
    }

    @Override
    public String contentType() {
        return _request.getContentType();
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
        return _request.getInputStream();
    }

    @Override
    public String[] paramValues(String key){
        return  _request.getParameterValues(key);
    }

    @Override
    public String param(String key) {
        //要充许为字符串
        //默认不能为null
        return paramMap().get(key);
    }

    @Override
    public String param(String key, String def) {
        String temp = paramMap().get(key);
        if(XUtil.isEmpty(temp)){
            return def;
        }else{
            return temp;
        }
    }

    private XMap _paramMap;

    @Override
    public XMap paramMap() {
        if(_paramMap == null){
            _paramMap = new XMap();

            Enumeration<String> names = _request.getParameterNames();

            while (names.hasMoreElements()) {
                String name = names.nextElement();
                String value = _request.getParameter(name);
                _paramMap.put(name, value);
            }
        }

        return _paramMap;
    }

    @Override
    public Map<String, String[]> paramsMap() {
        return _request.getParameterMap();
    }


    @Override
    public List<XFile> files(String key) throws Exception{
         if (isMultipartFormData()){
             return MultipartUtil.getUploadedFiles(this, key);
         }  else {
             return new ArrayList<>();
         }
    }

    @Override
    public String cookie(String key) {
        return cookie(key,null);
    }

    @Override
    public String cookie(String key, String def) {
        String temp = cookieMap().get(key);
        if(temp == null) {
            return def;
        }else{
            return temp;
        }
    }

    private XMap _cookieMap;

    @Override
    public XMap cookieMap() {
        if (_cookieMap == null) {
            _cookieMap = new XMap();

            Cookie[] _cookies = _request.getCookies();

            if (_cookies != null) {
                for (Cookie c : _cookies) {
                    _cookieMap.put(c.getName(), c.getValue());
                }
            }
        }

        return _cookieMap;
    }

    @Override
    public String header(String key) {
        return _request.getHeader(key);
    }

    @Override
    public String header(String key, String def) {
        String temp = _request.getHeader(key);
        if(XUtil.isEmpty(temp)){
            return def;
        }else{
            return temp;
        }
    }

    @Override
    public XMap headerMap() {
        if(_headerMap == null) {
            _headerMap = new XMap();
            Enumeration<String> headers = _request.getHeaderNames();

            while (headers.hasMoreElements()) {
                String key = (String) headers.nextElement();
                String value = _request.getHeader(key);
                _headerMap.put(key, value);
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
    public void charset(String charset) {
        _response.setCharacterEncoding(charset);
    }

    @Override
    protected void contentTypeDoSet(String contentType) {
        _response.setContentType(contentType);
    }


    @Override
    public OutputStream outputStream() throws IOException {
        return _response.getOutputStream();
    }

    @Override
    public void output(String str)  {
        try {
            PrintWriter writer = _response.getWriter();
            writer.write(str);
            writer.flush();
        }catch (Throwable ex){
            throw  new RuntimeException(ex);
        }
    }

    @Override
    public void output(InputStream stream)  {
        try {
            OutputStream out = _response.getOutputStream();

            byte[] buff = new byte[100];
            int rc = 0;
            while ((rc = stream.read(buff, 0, 100)) > 0) {
                out.write(buff, 0, rc);
            }

            out.flush();
        }catch (Throwable ex){
            throw  new RuntimeException(ex);
        }
    }

    @Override
    public void headerSet(String key, String val) {
        _response.setHeader(key,val);
    }

    @Override
    public void cookieSet(String key, String val, String domain, String path, int maxAge) {
        Cookie c = new Cookie(key,val);
        if (XUtil.isNotEmpty(path)) {
            c.setPath(path);
        }

        c.setMaxAge(maxAge);

        if (XUtil.isNotEmpty(domain)) {
            c.setDomain(domain);
        }

        _response.addCookie(c);
    }

    @Override
    public void redirect(String url) {
        try {
            _response.sendRedirect(url);
        }catch (Throwable ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void redirect(String url, int code) {
        status(code);
        _response.setHeader("Location", url);
    }

    @Override
    public int status() {
        return _response.getStatus();
    }

    @Override
    public void status(int status)  {
        _response.setStatus(status);
    }
}
