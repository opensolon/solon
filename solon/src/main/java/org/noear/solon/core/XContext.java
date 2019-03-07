package org.noear.solon.core;

import org.noear.solon.XApp;
import org.noear.solon.XUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通用上下文接口
 * */
public abstract class XContext {

    /**获取当前线程的上下文*/
    public static XContext current(){
        return XApp.currentContext();
    }

    /**是否已处理*/
    private boolean _handled;
    public void setHandled(boolean handled){
        _handled = handled;
    }
    public boolean getHandled(){
        return _handled;
    }

    /**获取请求对象*/
    public abstract Object request();
    /**获取远程IP*/
    public abstract String ip();
    /**是否为分段上传*/
    public boolean isMultipart() {
        return contentType().toLowerCase().contains("multipart/");
    }
    public boolean isMultipartFormData(){return contentType().toLowerCase().contains("multipart/form-data");}

    /**获取请求方法*/
    public abstract String method();
    /**获取请求协议*/
    public abstract String protocol();

    /**获取请求协议并大写*/
    private String _protocolAsUpper;
    public String protocolAsUpper(){
        if (_protocolAsUpper == null) {
            _protocolAsUpper = protocol().toUpperCase();
        }

        return _protocolAsUpper;
    }
    /**获取请求的URI*/
    public abstract URI uri();
    /**获取请求的URI路径*/
    public abstract String path();
    /**获取请求的URI路径并大写*/
    private String _pathAsUpper;
    public String pathAsUpper() {
        if (_pathAsUpper == null) {
            _pathAsUpper = path().toUpperCase();
        }

        return _pathAsUpper;
    }

    /**获取请求的UA*/
    public String userAgent(){return header("User-Agent");}
    /**获取请求的URL字符串*/
    public abstract String url();
    /**获取内容长度*/
    public abstract long contentLength();
    /**获取内容类型*/
    public abstract String contentType();

    /**获取RAW内容*/
    public abstract String body() throws IOException;
    /**获取RAW内容为Stream*/
    public abstract InputStream bodyAsStream() throws IOException;

    /**获取参数*/
    public abstract String[] paramValues(String key);
    public abstract String param(String key);
    public abstract String param(String key, String def);
    public abstract int paramAsInt(String key);
    public int paramAsInt(String key, int def){return Integer.parseInt(param(key,String.valueOf(def)));}
    public abstract long paramAsLong(String key);
    public long paramAsLong(String key, long def){return Long.parseLong(param(key,String.valueOf(def)));}
    public abstract double paramAsDouble(String key);
    public double paramAsDouble(String key, double def){return Double.parseDouble(param(key,String.valueOf(def)));}
    public abstract XMap paramMap();
    public abstract void paramSet(String key,String val);
    public <T> T paramAsEntity(Class<T> clz) throws Exception{
        return (T)XActionUtil.params2Entity(this, clz);
    }

    /**获取文件*/
    public abstract List<XFile> files(String key) throws Exception;
    public  XFile file(String key) throws Exception{
        return XUtil.firstOrNull(files(key));
    }

    /**获取COOKIE*/
    public abstract String cookie(String key);
    public abstract String cookie(String key, String def);
    public abstract XMap cookieMap();

    /**获取HEADER*/
    public abstract String header(String key);
    public abstract String header(String key, String def);
    public abstract XMap headerMap();

    /**获取SESSION_ID*/
    public abstract String sessionId();
    /**获取SESSION状态*/
    public abstract Object session(String key);
    /**设置SESSION状态*/
    public abstract void sessionSet(String key, Object val);

    //======================
    /**获取输出对象*/
    public abstract Object response();
    /**设置字符集*/
    public abstract void charset(String charset);
    /**设置内容类型*/
    public abstract void contentType(String contentType);

    /**输出内容*/
    public abstract void output(String str) throws IOException;
    public void output(Exception ex) throws IOException{ output(XUtil.getFullStackTrace(ex)); }
    public abstract void output(InputStream stream) throws IOException;
    public abstract OutputStream outputStream() throws IOException;
    public void outputAsJson(String json) throws IOException{
        contentType("application/json;charset=utf-8");
        output(json);
    }
    public void outputAsHtml(String html) throws IOException{
        contentType("text/html;charset=utf-8");
        if(html.startsWith("<html") == false) {
            StringBuilder sb = new StringBuilder();
            sb.append("<!doctype html>");
            sb.append("<html>");
            sb.append(html);
            sb.append("</html>");

            output(sb.toString());
        }else{
            output(html);
        }
    }

    /**设置HEADER*/
    public abstract void headerSet(String key, String val);

    /**设置COOKIE*/
    public abstract void cookieSet(String key, String val, int maxAge);
    public abstract void cookieSet(String key, String val, String domain, int maxAge);
    public abstract void cookieSet(String key, String val, String domain, String path,int maxAge);
    /**移徐COOKIE*/
    public abstract void cookieRemove(String key);

    /**跳转地址*/
    public abstract void redirect(String url) throws IOException;
    public abstract void redirect(String url, int code) throws IOException;

    /**获取输出状态*/
    public abstract int status();
    /**设置输出状态*/
    public abstract void status(int status) throws IOException;

    private Map<String,Object> _attrMap = new HashMap<>();
    /**获取上下文特性*/
    public <T> T attr(String key, T def){
        Object val = _attrMap.get(key);

        if(val == null) {
            return def;
        }

        return (T) val;
    }
    /**设置上下文特性*/
    public void attrSet(String key, Object val){
        _attrMap.put(key,val);
    }
    /**清除上下文特性*/
    public void attrClear(){
        _attrMap.clear();
    }
}
