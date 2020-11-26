package org.noear.solon.core.handle;

import org.noear.solon.Utils;
import org.noear.solon.annotation.Note;
import org.noear.solon.core.*;
import org.noear.solon.core.message.Session;
import org.noear.solon.core.wrap.ClassWrap;

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
 * 通用上下文接口（实现：Context + Handler 架构）
 *
 * @author noear
 * @since 1.0
 * */
public abstract class Context {
    /**获取当前线程的上下文*/
    @Note("获取当前线程的上下文")
    public static Context current(){
        return ContextUtil.current();
    }


    /**是否已处理（用于控制处理链）*/
    private boolean handled;
    @Note("设置处理状态")
    public void setHandled(boolean handled){
        this.handled = handled;
    }
    @Note("获取处理状态")
    public boolean getHandled(){
        return handled;
    }

    /**是否已渲染（用于控制渲染链）*/
    private boolean rendered;
    @Note("设置渲染状态")
    public void setRendered(boolean rendered){
        this.rendered = rendered;
    }
    @Note("获取渲染状态")
    public boolean getRendered(){
        return rendered;
    }

    /**获取请求对象*/
    @Note("获取请求对象")
    public abstract Object request();
    /**获取远程IP*/
    @Note("获取远程IP")
    public abstract String ip();
    /**是否为分段上传*/
    @Note("是否为分段内容")
    public boolean isMultipart() {
        String temp = contentType();
        if(temp==null){
            return false;
        }else {
            return temp.toLowerCase().contains("multipart/");
        }
    }
    @Note("是否为分段表单数据")
    public boolean isMultipartFormData() {
        String temp = contentType();
        if (temp == null) {
            return false;
        } else {
            return temp.toLowerCase().contains("multipart/form-data");
        }
    }

    /**获取请求方法*/
    @Note("获取请求方法")
    public abstract String method();
    /**获取请求协议*/
    @Note("获取请求协议")
    public abstract String protocol();

    /**获取请求协议并大写*/
    private String protocolAsUpper;
    @Note("获取请求协议并大写")
    public String protocolAsUpper(){
        if (protocolAsUpper == null) {
            protocolAsUpper = protocol().toUpperCase();
        }

        return protocolAsUpper;
    }
    /**获取请求的URI*/
    @Note("获取请求的URI")
    public abstract URI uri();
    /**获取请求的URI路径*/
    @Note("获取请求的URI路径")
    public abstract String path();
    /**获取请求的URI路径变量,根据路径表达式*/
    @Note("获取请求的URI路径变量,根据路径表达式")
    public NvMap pathMap(String expr) {
        return Utils.pathVarMap(path(),expr);
    }

    /**获取请求的URI路径并大写*/
    private String pathAsUpper;
    @Note("获取请求的URI路径并大写")
    public String pathAsUpper() {
        if (pathAsUpper == null) {
            pathAsUpper = path().toUpperCase();
        }

        return pathAsUpper;
    }

    /**获取请求的UA*/
    @Note("获取请求的UA")
    public String userAgent(){return header("User-Agent");}
    /**获取请求的URL字符串*/
    @Note("获取请求的URL字符串")
    public abstract String url();
    /**获取内容长度*/
    @Note("获取内容长度")
    public abstract long contentLength();
    /**获取内容类型*/
    @Note("获取内容类型")
    public abstract String contentType();

    private String body;
    /**获取RAW内容*/
    @Note("获取RAW内容")
    public String body() throws IOException{
        return body(null);
    }

    @Note("获取RAW内容")
    public String body(String charset) throws IOException {
        if (body == null) {
            try (InputStream ins = bodyAsStream()) {
                body = Utils.getString(ins,charset);
            }
        }

        return body;
    }

    /**获取RAW内容为byte[]*/
    @Note("获取RAW内容为byte[]")
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
    @Note("获取RAW内容为Stream")
    public abstract InputStream bodyAsStream() throws IOException;

    /**获取参数*/
    @Note("获取参数数组")
    public abstract String[] paramValues(String key);
    @Note("获取参数")
    public abstract String param(String key);
    @Note("获取参数")
    public abstract String param(String key, String def);
    @Note("获取参数并转为int")
    public int paramAsInt(String key){return paramAsInt(key,0);}
    @Note("获取参数并转为int")
    public int paramAsInt(String key, int def){return Integer.parseInt(param(key,String.valueOf(def)));}
    @Note("获取参数并转为long")
    public long paramAsLong(String key){return paramAsLong(key,0);}
    @Note("获取参数并转为long")
    public long paramAsLong(String key, long def){return Long.parseLong(param(key,String.valueOf(def)));}
    @Note("获取参数并转为double")
    public double paramAsDouble(String key){return paramAsDouble(key,0);}
    @Note("获取参数并转为double")
    public double paramAsDouble(String key, double def){return Double.parseDouble(param(key,String.valueOf(def)));}
    @Note("获取参数并转为BigDecimal")
    public BigDecimal paramAsDecimal(String key){return paramAsDecimal(key, BigDecimal.ZERO);}
    @Note("获取参数并转为BigDecimal")
    public BigDecimal paramAsDecimal(String key, BigDecimal def){
        String tmp = param(key);
        if(Utils.isEmpty(tmp)){
            return def;
        }else{
            return new BigDecimal(tmp);
        }
    }
    @Note("获取参数并转为Bean")
    public <T> T paramAsBean(Class<T> type){
        //不如参数注入的强；不支持 body 转换;
        return ClassWrap.get(type).newBy(this::param,this);
    }

    @Note("获取所有参数并转为map")
    public abstract NvMap paramMap();

    @Note("设置参数")
    public void paramSet(String key,String val) {
        paramMap().put(key, val);
        paramsAdd(key,val);
    }

    @Note("获取所有参数并转为Map")
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
    @Note("获取上传文件")
    public abstract List<UploadedFile> files(String key) throws Exception;
    @Note("获取上传文件")
    public UploadedFile file(String key) throws Exception{
        return Utils.firstOrNull(files(key));
    }

    /**获取COOKIE*/
    @Note("获取COOKIE")
    public String cookie(String key){
        return cookieMap().get(key);
    }
    @Note("获取COOKIE")
    public String cookie(String key, String def){
        return cookieMap().getOrDefault(key,def);
    }
    @Note("获取所有COOKIE并转为map")
    public abstract NvMap cookieMap();

    /**获取HEADER*/
    @Note("获取HEADER")
    public String header(String key){
        return headerMap().get(key);
    }
    @Note("获取HEADER")
    public String header(String key, String def){
        return headerMap().getOrDefault(key,def);
    }
    @Note("获取所有HEADER并转为map")
    public abstract NvMap headerMap();

    /**SESSION_STATE对象*/
    private SessionState sessionState = Bridge.sessionState();
    protected void sessionStateInit(SessionState sessionState){
        if(this.sessionState.replaceable()){
            this.sessionState = sessionState;
        }
    }
    protected SessionState sessionState(){
        return sessionState;
    }

    /**获取SESSION_ID*/
    @Note("获取SESSION_ID")
    public final String sessionId(){
        return sessionState.sessionId();
    }
    /**获取SESSION状态*/
    @Note("获取SESSION状态")
    public final Object session(String key){
        return sessionState.sessionGet(key);
    }
    /**设置SESSION状态*/
    @Note("设置SESSION状态")
    public final void sessionSet(String key, Object val){
        sessionState.sessionSet(key,val);
    }
    @Note("清空SESSION状态")
    public final void sessionClear(){
        sessionState.sessionClear();}

    /** SESSION for SOCKET */
    @Note("SESSION for SOCKET")
    public Session session() {
        throw new UnsupportedOperationException();
    }

    //======================
    /**获取输出对象*/
    @Note("获取输出对象")
    public abstract Object response();
    /**设置字符集*/
    @Note("设置字符集")
    public void charset(String charset) {
        this.charset = Charset.forName(charset);
    }
    protected Charset charset = StandardCharsets.UTF_8;

    /**设置内容类型*/
    @Note("设置内容类型")
    public void contentType(String contentType){
        contentTypeDoSet(contentType);

        //只记录非默认值
        if(ContextUtil.contentTypeDef.equals(contentType) == false) {
            contentTypeNew = contentType;
        }
    }
    @Note("获取设置的内容类型")
    public String contentTypeNew(){
        return contentTypeNew;
    }
    private String contentTypeNew;
    protected abstract void contentTypeDoSet(String contentType);


    /**输出内容*/
    @Note("输出内容:字节数组")
    public abstract void output(byte[] bytes);
    @Note("输出内容:stream")
    public abstract void output(InputStream stream);
    @Note("获取输出流")
    public abstract OutputStream outputStream() throws IOException;

    @Note("输出内容:字符串")
    public void output(String str) {
        if (str != null) {
            try {
                attrSet("output", str);
                output(str.getBytes(charset));
            } catch (Throwable ex) {
                throw Utils.throwableWrap(ex);
            }
        }
    }
    @Note("输出内容:异常对象")
    public void output(Throwable ex) { output(Utils.getFullStackTrace(ex)); }

    @Note("输出json")
    public void outputAsJson(String json) {
        contentType("application/json;charset=utf-8");
        output(json);
    }
    @Note("输出html")
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
    @Note("设置HEADER")
    public abstract void headerSet(String key, String val);
    @Note("添加HEADER")
    public abstract void headerAdd(String key, String val);

    /**设置COOKIE*/
    @Note("设置COOKIE")
    public void cookieSet(String key, String val){
        cookieSet(key, val, null, -1);
    }
    @Note("设置COOKIE")
    public void cookieSet(String key, String val, int maxAge){
        cookieSet(key, val, null, maxAge);
    }
    @Note("设置COOKIE")
    public void cookieSet(String key, String val, String domain, int maxAge){
        cookieSet(key, val, domain, "/", maxAge);
    }
    @Note("设置COOKIE")
    public abstract void cookieSet(String key, String val, String domain, String path,int maxAge);
    /**移徐COOKIE*/
    @Note("移徐COOKIE")
    public void cookieRemove(String key){cookieSet(key,"",0);}

    /**跳转地址*/
    @Note("跳转地址")
    public abstract void redirect(String url);
    @Note("跳转地址")
    public abstract void redirect(String url, int code);

    /**获取输出状态*/
    @Note("获取输出状态")
    public abstract int status();

    @Deprecated
    public void status(int status){
        statusSet(status);
    }

    /**设置输出状态*/
    @Note("设置输出状态")
    public abstract void statusSet(int status);


    private Map<String,Object> attrMap = null;
    @Note("获取自定义特性并转为Map")
    public Map<String,Object> attrMap(){//改为懒加载
        if(attrMap == null){
            attrMap = new HashMap<>();
        }

        return attrMap;
    }
    /**获取自定义特性*/
    @Note("获取自定义特性")
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
    @Note("设置自定义特性")
    public void attrSet(String key, Object val){
        attrMap().put(key,val);
    }
    @Note("设置自定义特性")
    public void attrSet(Map<String,Object> map){
        attrMap().putAll(map);
    }
    /**清除上下文特性*/
    @Note("清空自定义特性")
    public void attrClear(){
        attrMap().clear();
    }

    /**
     * 渲染数据（不能重写，避免死循环）     */
    @Note("渲染数据")
    public final void render(Object obj) throws Throwable {
        //ModelAndView or Data
        RenderManager.global.render(obj, this);
    }

    @Note("渲染数据")
    public final void render(String view, Map<String,?> data) throws Throwable {
        render(new ModelAndView(view,data));
    }

    @Note("渲染数据")
    public final String renderAndReturn(ModelAndView modelAndView) throws Throwable {
        return RenderManager.global.renderAndReturn(modelAndView, this);
    }

    private boolean _remoting;
    @Note("是否为远程调用")
    public boolean remoting(){
        return _remoting;
    }
    public void remotingSet(boolean remote){
        _remoting = remote;
    }

    @Note("冲刷")
    public abstract void flush() throws IOException;

    //一些特殊的boot才有效
    @Note("提交响应")
    protected void commit() throws IOException{}

    //一些特殊的boot才有效
    @Note("关闭响应")
    public void close() throws IOException{}

    //用于在处理链中透传处理结果
    @Note("处理结果")
    public Object result;

    @Note("控制器?")
    public Object controller() {
        return attr("controller");
    }

    @Note("动作?")
    public Action action() {
        return attr("action");
    }
}
