package org.noear.solon.core;

import org.noear.solon.XUtil;
import org.noear.solon.annotation.XNote;

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
    @XNote("获取当前线程的上下文")
    public static XContext current(){
        return XContextUtil.current();
    }

    /**是否已处理*/
    private boolean _handled;
    @XNote("设置处理状态")
    public void setHandled(boolean handled){
        _handled = handled;
    }
    @XNote("获取处理状态")
    public boolean getHandled(){
        return _handled;
    }

    /**获取请求对象*/
    @XNote("获取请求对象")
    public abstract Object request();
    /**获取远程IP*/
    @XNote("获取远程IP")
    public abstract String ip();
    /**是否为分段上传*/
    @XNote("是否为分段内容")
    public boolean isMultipart() {
        String temp = contentType();
        if(temp==null){
            return false;
        }else {
            return temp.toLowerCase().contains("multipart/");
        }
    }
    @XNote("是否为分段表单数据")
    public boolean isMultipartFormData() {
        String temp = contentType();
        if (temp == null) {
            return false;
        } else {
            return temp.toLowerCase().contains("multipart/form-data");
        }
    }

    /**获取请求方法*/
    @XNote("获取请求方法")
    public abstract String method();
    /**获取请求协议*/
    @XNote("获取请求协议")
    public abstract String protocol();

    /**获取请求协议并大写*/
    private String _protocolAsUpper;
    @XNote("获取请求协议并大写")
    public String protocolAsUpper(){
        if (_protocolAsUpper == null) {
            _protocolAsUpper = protocol().toUpperCase();
        }

        return _protocolAsUpper;
    }
    /**获取请求的URI*/
    @XNote("获取请求的URI")
    public abstract URI uri();
    /**获取请求的URI路径*/
    @XNote("获取请求的URI路径")
    public abstract String path();
    /**获取请求的URI路径变量,根据路径表达式*/
    @XNote("获取请求的URI路径变量,根据路径表达式")
    public XMap pathMap(String expr) {
        return XUtil.pathVarMap(path(),expr);
    }

    /**获取请求的URI路径并大写*/
    private String _pathAsUpper;
    @XNote("获取请求的URI路径并大写")
    public String pathAsUpper() {
        if (_pathAsUpper == null) {
            _pathAsUpper = path().toUpperCase();
        }

        return _pathAsUpper;
    }

    /**获取请求的UA*/
    @XNote("获取请求的UA")
    public String userAgent(){return header("User-Agent");}
    /**获取请求的URL字符串*/
    @XNote("获取请求的URL字符串")
    public abstract String url();
    /**获取内容长度*/
    @XNote("获取内容长度")
    public abstract long contentLength();
    /**获取内容类型*/
    @XNote("获取内容类型")
    public abstract String contentType();

    /**获取RAW内容*/
    @XNote("获取RAW内容")
    public abstract String body() throws IOException;
    /**获取RAW内容为Stream*/
    @XNote("获取RAW内容为Stream")
    public abstract InputStream bodyAsStream() throws IOException;

    /**获取参数*/
    @XNote("获取参数数组")
    public abstract String[] paramValues(String key);
    @XNote("获取参数")
    public abstract String param(String key);
    @XNote("获取参数")
    public abstract String param(String key, String def);
    @XNote("获取参数并转为int")
    public int paramAsInt(String key){return paramAsInt(key,0);}
    @XNote("获取参数并转为int")
    public int paramAsInt(String key, int def){return Integer.parseInt(param(key,String.valueOf(def)));}
    @XNote("获取参数并转为long")
    public long paramAsLong(String key){return paramAsLong(key,0);}
    @XNote("获取参数并转为long")
    public long paramAsLong(String key, long def){return Long.parseLong(param(key,String.valueOf(def)));}
    @XNote("获取参数并转为double")
    public double paramAsDouble(String key){return paramAsDouble(key,0);}
    @XNote("获取参数并转为double")
    public double paramAsDouble(String key, double def){return Double.parseDouble(param(key,String.valueOf(def)));}
    @XNote("获取所有参数并转为map")
    public abstract XMap paramMap();
    @XNote("设置参数")
    public abstract void paramSet(String key,String val);
    @XNote("获取所有参数并转为class")
    public <T> T paramAsEntity(Class<T> clz) throws Exception{
        return (T)XActionUtil.params2Entity(this, clz);
    }

    /**获取文件*/
    @XNote("获取上传文件")
    public abstract List<XFile> files(String key) throws Exception;
    @XNote("获取上传文件")
    public  XFile file(String key) throws Exception{
        return XUtil.firstOrNull(files(key));
    }

    /**获取COOKIE*/
    @XNote("获取COOKIE")
    public abstract String cookie(String key);
    @XNote("获取COOKIE")
    public abstract String cookie(String key, String def);
    @XNote("获取所有COOKIE并转为map")
    public abstract XMap cookieMap();

    /**获取HEADER*/
    @XNote("获取HEADER")
    public abstract String header(String key);
    @XNote("获取HEADER")
    public abstract String header(String key, String def);
    @XNote("获取所有HEADER并转为map")
    public abstract XMap headerMap();

    /**SESSION_STATE对象*/
    private XSessionState _sessionState = XSessionStateDefault.global;
    protected void sessionStateInit(XSessionState sessionState){
        if(_sessionState.replaceable()){
            _sessionState = sessionState;
        }
    }
    protected XSessionState sessionState(){
        return _sessionState;
    }

    /**获取SESSION_ID*/
    @XNote("获取SESSION_ID")
    public final String sessionId(){
        return _sessionState.sessionId();
    }
    /**获取SESSION状态*/
    @XNote("获取SESSION状态")
    public final Object session(String key){
        return _sessionState.sessionGet(key);
    }
    /**设置SESSION状态*/
    @XNote("设置SESSION状态")
    public final void sessionSet(String key, Object val){
        _sessionState.sessionSet(key,val);
    }

    //======================
    /**获取输出对象*/
    @XNote("获取输出对象")
    public abstract Object response();
    /**设置字符集*/
    @XNote("设置字符集")
    public abstract void charset(String charset);
    /**设置内容类型*/
    @XNote("设置内容类型")
    public abstract void contentType(String contentType);

    /**输出内容*/
    @XNote("输出内容:字符串")
    public abstract void output(String str);
    @XNote("输出内容:异常对象")
    public void output(Exception ex) { output(XUtil.getFullStackTrace(ex)); }
    @XNote("输出内容:stream")
    public abstract void output(InputStream stream);
    @XNote("获取输出流")
    public abstract OutputStream outputStream() throws IOException;
    @XNote("输出json")
    public void outputAsJson(String json) {
        contentType("application/json;charset=utf-8");
        output(json);
    }
    @XNote("输出html")
    public void outputAsHtml(String html){
        contentType("text/html;charset=utf-8");
        if(html.startsWith("<") == false) {
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
    @XNote("设置HEADER")
    public abstract void headerSet(String key, String val);

    /**设置COOKIE*/
    @XNote("设置COOKIE")
    public void cookieSet(String key, String val, int maxAge){
        cookieSet(key, val, null, maxAge);
    }
    @XNote("设置COOKIE")
    public void cookieSet(String key, String val, String domain, int maxAge){
        cookieSet(key, val, domain, "/", maxAge);
    }
    @XNote("设置COOKIE")
    public abstract void cookieSet(String key, String val, String domain, String path,int maxAge);
    /**移徐COOKIE*/
    @XNote("移徐COOKIE")
    public void cookieRemove(String key){cookieSet(key,"",0);}

    /**跳转地址*/
    @XNote("跳转地址")
    public abstract void redirect(String url);
    @XNote("跳转地址")
    public abstract void redirect(String url, int code);

    /**获取输出状态*/
    @XNote("获取输出状态")
    public abstract int status();
    /**设置输出状态*/
    @XNote("设置输出状态")
    public abstract void status(int status);

    private Map<String,Object> _attrMap = null;
    private Map<String,Object> attrMap(){//改为懒加载
        if(_attrMap == null){
            _attrMap = new HashMap<>();
        }

        return _attrMap;
    }
    /**获取自定义特性*/
    @XNote("获取自定义特性")
    public <T> T attr(String key, T def){
        Object val = attrMap().get(key);

        if(val == null) {
            return def;
        }

        return (T) val;
    }
    /**设置上下文特性*/
    @XNote("设置自定义特性")
    public void attrSet(String key, Object val){
        attrMap().put(key,val);
    }
    @XNote("设置自定义特性")
    public void attrSet(Map<String,Object> map){
        attrMap().putAll(map);
    }
    /**清除上下文特性*/
    @XNote("清空自定义特性")
    public void attrClear(){
        attrMap().clear();
    }

    /**
     * 渲染数据
     */
    @XNote("渲染数据")
    public void render(Object obj) throws Throwable {
        //ModelAndView or Data
        XRenderManager.global.render(obj, this);
    }

    @XNote("渲染数据")
    public void render(String view, Map<String,?> data) throws Throwable {
        XRenderManager.global.render(new ModelAndView(view,data), this);
    }

    //一些特殊的boot才有效
    @XNote("提交响应")
    protected void commit() throws IOException{}

    //一些特殊的boot才有效
    @XNote("关闭响应")
    public void close() throws IOException{}
}
