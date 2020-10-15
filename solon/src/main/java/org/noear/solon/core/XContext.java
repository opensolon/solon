package org.noear.solon.core;

import org.noear.solon.XApp;
import org.noear.solon.XUtil;
import org.noear.solon.annotation.XNote;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通用上下文接口（实现：XContext + XHandler 架构）
 *
 * @author noear
 * @since 1.0
 * */
public abstract class XContext {
    /**获取当前线程的上下文*/
    @XNote("获取当前线程的上下文")
    public static XContext current(){
        return XContextUtil.current();
    }


    /**是否已处理（用于控制处理链）*/
    private boolean _handled;
    @XNote("设置处理状态")
    public void setHandled(boolean handled){
        _handled = handled;
    }
    @XNote("获取处理状态")
    public boolean getHandled(){
        return _handled;
    }

    /**是否已渲染（用于控制渲染链）*/
    private boolean _rendered;
    @XNote("设置渲染状态")
    public void setRendered(boolean rendered){
        _rendered = rendered;
    }
    @XNote("获取渲染状态")
    public boolean getRendered(){
        return _rendered;
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

    private String _body;
    /**获取RAW内容*/
    @XNote("获取RAW内容")
    public String body() throws IOException{
        return body(null);
    }

    @XNote("获取RAW内容")
    public String body(String charset) throws IOException {
        if (_body == null) {
            try (InputStream ins = bodyAsStream()) {
                _body = XUtil.getString(ins,charset);
            }
        }

        return _body;
    }

    /**获取RAW内容为byte[]*/
    @XNote("获取RAW内容为byte[]")
    public byte[] bodyAsBytes() throws IOException{
        try (InputStream ins = bodyAsStream()) {
            if (ins == null) {
                return null;
            }

            ByteArrayOutputStream outs = new ByteArrayOutputStream(); //这个不需要关闭

            int len = 0;
            byte[] buf = new byte[512]; //0.5k
            while ((len = ins.read(buf)) != -1) {
                outs.write(buf, 0, len);
            }

            return outs.toByteArray();
        }
    }

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
    @XNote("获取参数并转为BigDecimal")
    public BigDecimal paramAsDecimal(String key){return paramAsDecimal(key, BigDecimal.ZERO);}
    @XNote("获取参数并转为BigDecimal")
    public BigDecimal paramAsDecimal(String key, BigDecimal def){
        String tmp = param(key);
        if(XUtil.isEmpty(tmp)){
            return def;
        }else{
            return new BigDecimal(tmp);
        }
    }

    @XNote("获取所有参数并转为map")
    public abstract XMap paramMap();

    @XNote("设置参数")
    public void paramSet(String key,String val) {
        paramMap().put(key, val);
        paramsAdd(key,val);
    }

    @XNote("获取所有参数并转为Map")
    public abstract Map<String,List<String>> paramsMap();
    public void paramsAdd(String key,String val) {
        if (paramsMap() != null) {
            List<String> ary = paramsMap().get(key);
            if (ary == null) {
                ary = new ArrayList<>();
                paramsMap().put(key, ary);
            }
            ary.add(val);
        }
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
    public String cookie(String key){
        return cookieMap().get(key);
    }
    @XNote("获取COOKIE")
    public String cookie(String key, String def){
        return cookieMap().getOrDefault(key,def);
    }
    @XNote("获取所有COOKIE并转为map")
    public abstract XMap cookieMap();

    /**获取HEADER*/
    @XNote("获取HEADER")
    public String header(String key){
        return headerMap().get(key);
    }
    @XNote("获取HEADER")
    public String header(String key, String def){
        return headerMap().getOrDefault(key,def);
    }
    @XNote("获取所有HEADER并转为map")
    public abstract XMap headerMap();

    /**SESSION_STATE对象*/
    private XSessionState _sessionState = XBridge.sessionState();
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
    @XNote("清空SESSION状态")
    public final void sessionClear(){_sessionState.sessionClear();}

    //======================
    /**获取输出对象*/
    @XNote("获取输出对象")
    public abstract Object response();
    /**设置字符集*/
    @XNote("设置字符集")
    public void charset(String charset) {
        _charset = Charset.forName(charset);
    }
    protected Charset _charset = StandardCharsets.UTF_8;

    /**设置内容类型*/
    @XNote("设置内容类型")
    public void contentType(String contentType){
        contentTypeDoSet(contentType);

        //只记录非默认值
        if(XContextUtil.contentTypeDef.equals(contentType) == false) {
            _contentTypeNew = contentType;
        }
    }
    @XNote("获取设置的内容类型")
    public String contentTypeNew(){
        return _contentTypeNew;
    }
    private String _contentTypeNew;
    protected abstract void contentTypeDoSet(String contentType);


    /**输出内容*/
    @XNote("输出内容:字节数组")
    public abstract void output(byte[] bytes);
    @XNote("输出内容:stream")
    public abstract void output(InputStream stream);
    @XNote("获取输出流")
    public abstract OutputStream outputStream() throws IOException;

    @XNote("输出内容:字符串")
    public void output(String str) {
        if (str != null) {
            try {
                attrSet("output", str);
                output(str.getBytes(_charset));
            } catch (Throwable ex) {
                throw XUtil.throwableWrap(ex);
            }
        }
    }
    @XNote("输出内容:异常对象")
    public void output(Throwable ex) { output(XUtil.getFullStackTrace(ex)); }

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
    @XNote("添加HEADER")
    public abstract void headerAdd(String key, String val);

    /**设置COOKIE*/
    @XNote("设置COOKIE")
    public void cookieSet(String key, String val){
        cookieSet(key, val, null, -1);
    }
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

    @Deprecated
    public void status(int status){
        statusSet(status);
    }

    /**设置输出状态*/
    @XNote("设置输出状态")
    public abstract void statusSet(int status);


    private Map<String,Object> _attrMap = null;
    @XNote("获取自定义特性并转为Map")
    public Map<String,Object> attrMap(){//改为懒加载
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

    public <T> T attr(String key){
        return (T) attrMap().get(key);
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
     * 渲染数据（不能重写，避免死循环）     */
    @XNote("渲染数据")
    public final void render(Object obj) throws Throwable {
        //ModelAndView or Data
        XRenderManager.global.render(obj, this);
    }

    @XNote("渲染数据")
    public final void render(String view, Map<String,?> data) throws Throwable {
        render(new ModelAndView(view,data));
    }

    private boolean _remoting;
    @XNote("是否为远程调用")
    public boolean remoting(){
        return _remoting;
    }
    public void remotingSet(boolean remote){
        _remoting = remote;
    }

    @XNote("冲刷")
    public abstract void flush() throws IOException;

    //一些特殊的boot才有效
    @XNote("提交响应")
    protected void commit() throws IOException{}

    //一些特殊的boot才有效
    @XNote("关闭响应")
    public void close() throws IOException{}

    //用于在处理链中透传处理结果
    @XNote("处理结果")
    public Object result;

    @XNote("控制器?")
    public Object controller() {
        return attr("controller");
    }

    @XNote("动作?")
    public XAction action() {
        return attr("action");
    }
}
