package org.noear.solon.core.handle;

import org.noear.solon.Utils;
import org.noear.solon.annotation.Note;
import org.noear.solon.core.*;
import org.noear.solon.core.util.IpUtil;
import org.noear.solon.core.util.PathUtil;
import org.noear.solon.core.wrap.ClassWrap;

import java.io.*;
import java.math.BigDecimal;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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
    private String realIp;
    @Note("获取客户端真实IP")
    public String realIp(){
        if(realIp == null) {
            realIp = IpUtil.getIp(this);
        }

        return realIp;
    }

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

    /**
     * 设置新路径
     * */
    public void pathNew(String pathNew){
        this.pathNew = pathNew;
    }

    private String pathNew;
    /**
     * 获取新路径，不存在则返回原路径
     * */
    public String pathNew() {
        if (pathNew == null) {
            return path();
        } else {
            return pathNew;
        }
    }

    /**获取请求的URI路径变量,根据路径表达式*/
    @Note("获取请求的URI路径变量,根据路径表达式")
    public NvMap pathMap(String expr) {
        return PathUtil.pathVarMap(path(),expr);
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
    @Note("获取查询字符串")
    public abstract String queryString();

    private String accept;
    public String accept(){
        if(accept == null) {
            accept = header("Accept", "");
        }

        return accept;
    }


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
                body = Utils.transferToString(ins,charset);
            }
        }

        return body;
    }

    private String bodyNew;
    public String bodyNew() throws IOException{
        if(bodyNew == null){
            return body();
        }else{
            return bodyNew;
        }
    }

    public void bodyNew(String bodyNew){
        this.bodyNew = bodyNew;
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
    public abstract String[] paramValues(String name);
    @Note("获取参数")
    public abstract String param(String name);
    @Note("获取参数")
    public abstract String param(String name, String def);
    @Note("获取参数并转为int")
    public int paramAsInt(String name){return paramAsInt(name,0);}
    @Note("获取参数并转为int")
    public int paramAsInt(String name, int def){return Integer.parseInt(param(name,String.valueOf(def)));}
    @Note("获取参数并转为long")
    public long paramAsLong(String name){return paramAsLong(name,0);}
    @Note("获取参数并转为long")
    public long paramAsLong(String name, long def){return Long.parseLong(param(name,String.valueOf(def)));}
    @Note("获取参数并转为double")
    public double paramAsDouble(String name){return paramAsDouble(name,0);}
    @Note("获取参数并转为double")
    public double paramAsDouble(String name, double def){return Double.parseDouble(param(name,String.valueOf(def)));}
    @Note("获取参数并转为BigDecimal")
    public BigDecimal paramAsDecimal(String name){return paramAsDecimal(name, BigDecimal.ZERO);}
    @Note("获取参数并转为BigDecimal")
    public BigDecimal paramAsDecimal(String name, BigDecimal def){
        String tmp = param(name);
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
    public void paramSet(String name,String val) {
        paramMap().put(name, val);
        paramsAdd(name,val);
    }

    @Note("获取所有参数并转为Map")
    public abstract Map<String,List<String>> paramsMap();
    public void paramsAdd(String name,String val) {
        if (paramsMap() != null) {
            List<String> ary = paramsMap().get(name);
            if (ary == null) {
                ary = new ArrayList<>();
                paramsMap().put(name, ary);
            }
            ary.add(val);
        }
    }

    /**获取文件*/
    @Note("获取上传文件")
    public abstract List<UploadedFile> files(String name) throws Exception;
    @Note("获取上传文件")
    public UploadedFile file(String name) throws Exception{
        return Utils.firstOrNull(files(name));
    }

    /**获取COOKIE*/
    @Note("获取COOKIE")
    public String cookie(String name){
        return cookieMap().get(name);
    }
    @Note("获取COOKIE")
    public String cookie(String name, String def){
        return cookieMap().getOrDefault(name,def);
    }
    @Note("获取所有COOKIE并转为map")
    public abstract NvMap cookieMap();

    /**获取HEADER*/
    @Note("获取HEADER")
    public String header(String name){
        return headerMap().get(name);
    }
    @Note("获取HEADER")
    public String header(String name, String def){
        return headerMap().getOrDefault(name,def);
    }
    @Note("获取所有HEADER并转为map")
    public abstract NvMap headerMap();

    /**SESSION_STATE对象*/
    private SessionState sessionState;
    protected void sessionStateInit(SessionState sessionState) {
        if (sessionState().replaceable()) {
            this.sessionState = sessionState;
        }
    }
    public SessionState sessionState() {
        if (sessionState == null) {
            sessionState = Bridge.sessionState(this);
        }

        return sessionState;
    }

    /**获取SESSION_ID*/
    @Note("获取SESSION_ID")
    public final String sessionId(){
        return sessionState().sessionId();
    }
    /**获取SESSION状态*/
    @Note("获取SESSION状态")
    public final Object session(String name){
        return sessionState().sessionGet(name);
    }
    /**获取SESSION状态*/
    @Note("获取SESSION状态")
    public final <T> T session(String name, T def) {
        Object tmp = session(name);
        if (tmp == null) {
            return def;
        } else {
            return (T) tmp;
        }
    }

    /**设置SESSION状态*/
    @Note("设置SESSION状态")
    public final void sessionSet(String name, Object val){
        sessionState().sessionSet(name,val);
    }
    @Note("清空SESSION状态")
    public final void sessionClear(){
        sessionState().sessionClear();
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
            attrSet("output", str);
            output(str.getBytes(charset));
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

    @Note("输出file")
    public void outputAsFile(UploadedFile file) throws IOException {
        if (Utils.isNotEmpty(file.contentType)) {
            contentType(file.contentType);
        }

        if (Utils.isNotEmpty(file.name)) {
            headerSet("Content-Disposition", "attachment; filename=\"" + file.name + "\"");
        }

        Utils.transferTo(file.content, outputStream());
    }

    @Note("输出file")
    public void outputAsFile(File file) throws IOException {
        if (Utils.isNotEmpty(file.getName())) {
            headerSet("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
        }

        try (InputStream ins = new FileInputStream(file)) {
            Utils.transferTo(ins, outputStream());
        }
    }

    /**设置HEADER*/
    @Note("设置HEADER")
    public abstract void headerSet(String name, String val);
    @Note("添加HEADER")
    public abstract void headerAdd(String name, String val);

    /**设置COOKIE*/
    @Note("设置COOKIE")
    public void cookieSet(String name, String val){
        cookieSet(name, val, null, -1);
    }
    @Note("设置COOKIE")
    public void cookieSet(String name, String val, int maxAge){
        cookieSet(name, val, null, maxAge);
    }
    @Note("设置COOKIE")
    public void cookieSet(String name, String val, String domain, int maxAge){
        cookieSet(name, val, domain, "/", maxAge);
    }
    @Note("设置COOKIE")
    public abstract void cookieSet(String name, String val, String domain, String path,int maxAge);
    /**移徐COOKIE*/
    @Note("移徐COOKIE")
    public void cookieRemove(String name){cookieSet(name,"",0);}

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


    private NdMap attrMap = null;
    @Note("获取自定义特性并转为Map")
    public Map<String,Object> attrMap(){//改为懒加载
        if(attrMap == null){
            attrMap = new NdMap();
        }

        return attrMap;
    }
    /**获取自定义特性*/
    @Note("获取自定义特性")
    public <T> T attr(String name, T def){
        Object val = attrMap().get(name);

        if(val == null) {
            return def;
        }

        return (T) val;
    }

    public <T> T attr(String name){
        return (T) attrMap().get(name);
    }
    /**设置上下文特性*/
    @Note("设置自定义特性")
    public void attrSet(String name, Object val){
        attrMap().put(name,val);
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
        setRendered(true);
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

    //用于在处理链中透传处理错误
    @Note("处理错误")
    public Throwable errors;

    @Note("控制器?")
    public Object controller() {
        return attr("controller");
    }

    @Note("动作?")
    public Action action() {
        return attr("action");
    }
}
