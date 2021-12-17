package org.noear.solon.core.handle;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.annotation.Note;
import org.noear.solon.core.*;
import org.noear.solon.core.util.IpUtil;
import org.noear.solon.core.util.PathUtil;
import org.noear.solon.core.wrap.ClassWrap;

import java.io.*;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 通用上下文接口（实现：Context + Handler 架构）
 *
 * @author noear
 * @since 1.0
 * */
public abstract class Context {
    /**
     * 获取当前线程的上下文
     */
    public static Context current() {
        return ContextUtil.current();
    }

    private Locale locale;

    /**
     * 获取地区
     * */
    public Locale getLocale() {
        return locale;
    }

    /**
     * 设置地区
     * */
    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    /**
     * 是否已处理（用于控制处理链）
     */
    private boolean handled;

    /**
     * 设置处理状态
     * */
    @Note("设置处理状态")
    public void setHandled(boolean handled) {
        this.handled = handled;
    }

    /**
     * 获取处理状态
     * */
    @Note("获取处理状态")
    public boolean getHandled() {
        return handled;
    }

    /**
     * 是否已渲染（用于控制渲染链）
     */
    private boolean rendered;

    /**
     * 设置渲染状态
     * */
    @Note("设置渲染状态")
    public void setRendered(boolean rendered) {
        this.rendered = rendered;
    }

    /**
     * 获取渲染状态
     * */
    @Note("获取渲染状态")
    public boolean getRendered() {
        return rendered;
    }

    /**
     * 获取请求对象
     */
    @Note("获取请求对象")
    public abstract Object request();

    /**
     * 获取远程IP
     */
    @Note("获取远程IP")
    public abstract String ip();

    private String realIp;

    /**
     * 获取客户端真实IP
     * */
    @Note("获取客户端真实IP")
    public String realIp() {
        if (realIp == null) {
            realIp = IpUtil.getIp(this);
        }

        return realIp;
    }

    /**
     * 是否为分段内容
     */
    public boolean isMultipart() {
        String temp = contentType();
        if (temp == null) {
            return false;
        } else {
            return temp.toLowerCase().contains("multipart/");
        }
    }

    /**
     * 是否为分段表单数据
     * */
    public boolean isMultipartFormData() {
        String temp = contentType();
        if (temp == null) {
            return false;
        } else {
            return temp.toLowerCase().contains("multipart/form-data");
        }
    }

    /**
     * 获取请求方法
     */
    @Note("获取请求方式")
    public abstract String method();

    /**
     * 获取请求协议
     */
    @Note("获取请求协议")
    public abstract String protocol();

    private String protocolAsUpper;

    /**
     * 获取请求协议并大写
     * */
    public String protocolAsUpper() {
        if (protocolAsUpper == null) {
            protocolAsUpper = protocol().toUpperCase();
        }

        return protocolAsUpper;
    }

    /**
     * 获取请求的URI
     */
    public abstract URI uri();

    /**
     * 获取请求的URI路径
     */
    public abstract String path();

    /**
     * 设置新路径
     */
    public void pathNew(String pathNew) {
        this.pathNew = pathNew;
    }

    private String pathNew;

    /**
     * 获取新路径，不存在则返回原路径
     */
    public String pathNew() {
        if (pathNew == null) {
            return path();
        } else {
            return pathNew;
        }
    }

    /**
     * 获取请求的URI路径变量,根据路径表达式
     */
    public NvMap pathMap(String expr) {
        return PathUtil.pathVarMap(path(), expr);
    }

    private String pathAsUpper;

    /**
     * 获取请求的URI路径并大写
     * */
    public String pathAsUpper() {
        if (pathAsUpper == null) {
            pathAsUpper = path().toUpperCase();
        }

        return pathAsUpper;
    }

    /**
     * 获取请求的UA
     */
    public String userAgent() {
        return header("User-Agent");
    }

    /**
     * 获取请求的URL字符串
     */
    public abstract String url();

    /**
     * 获取内容长度
     */
    public abstract long contentLength();

    /**
     * 获取内容类型
     */
    public abstract String contentType();

    /**
     * 获取查询字符串
     * */
    public abstract String queryString();

    private String accept;

    /**
     * 获取 Accept 头信息
     * */
    public String accept() {
        if (accept == null) {
            accept = header("Accept", "");
        }

        return accept;
    }


    private String body;

    /**
     * 获取body内容
     */
    public String body() throws IOException {
        return body(null);
    }

    /**
     * 获取body内容
     * */
    public String body(String charset) throws IOException {
        if (body == null) {
            try (InputStream ins = bodyAsStream()) {
                body = Utils.transferToString(ins, charset);
            }
        }

        return body;
    }

    private String bodyNew;

    /**
     * 获取新的body
     * */
    public String bodyNew() throws IOException {
        if (bodyNew == null) {
            return body();
        } else {
            return bodyNew;
        }
    }

    /**
     * 设置新的body
     * */
    public void bodyNew(String bodyNew) {
        this.bodyNew = bodyNew;
    }

    /**
     * 获取body内容为byte[]
     */
    public byte[] bodyAsBytes() throws IOException {
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

    /**
     * 获取body内容为Stream
     */
    public abstract InputStream bodyAsStream() throws IOException;

    /**
     * 获取参数数组
     */
    public abstract String[] paramValues(String name);

    /**
     * 获取参数
     * */
    public abstract String param(String name);

    /**
     * 获取参数
     * */
    public abstract String param(String name, String def);

    /**
     * 获取参数并转为int
     * */
    public int paramAsInt(String name) {
        return paramAsInt(name, 0);
    }

    /**
     * 获取参数并转为int
     * */
    public int paramAsInt(String name, int def) {
        return Integer.parseInt(param(name, String.valueOf(def)));
    }

    /**
     * 获取参数并转为long
     * */
    public long paramAsLong(String name) {
        return paramAsLong(name, 0);
    }

    /**
     * 获取参数并转为long
     * */
    public long paramAsLong(String name, long def) {
        return Long.parseLong(param(name, String.valueOf(def)));
    }

    /**
     * 获取参数并转为double
     * */
    public double paramAsDouble(String name) {
        return paramAsDouble(name, 0);
    }

    /**
     * 获取参数并转为double
     * */
    public double paramAsDouble(String name, double def) {
        return Double.parseDouble(param(name, String.valueOf(def)));
    }

    /**
     * 获取参数并转为BigDecimal
     * */
    public BigDecimal paramAsDecimal(String name) {
        return paramAsDecimal(name, BigDecimal.ZERO);
    }

    /**
     * 获取参数并转为BigDecimal
     * */
    public BigDecimal paramAsDecimal(String name, BigDecimal def) {
        String tmp = param(name);
        if (Utils.isEmpty(tmp)) {
            return def;
        } else {
            return new BigDecimal(tmp);
        }
    }

    /**
     * 获取参数并转为Bean
     * */
    public <T> T paramAsBean(Class<T> type) {
        //不如参数注入的强；不支持 body 转换;
        return ClassWrap.get(type).newBy(this::param, this);
    }

    /**
     * 获取所有参数并转为map
     * */
    public abstract NvMap paramMap();

    /**
     * 设置参数
     * */
    public void paramSet(String name, String val) {
        paramMap().put(name, val);
        paramsAdd(name, val);
    }

    /**
     * 获取所有参数并转为Map
     * */
    public abstract Map<String, List<String>> paramsMap();

    /**
     * 添加参数
     * */
    public void paramsAdd(String name, String val) {
        if (paramsMap() != null) {
            List<String> ary = paramsMap().get(name);
            if (ary == null) {
                ary = new ArrayList<>();
                paramsMap().put(name, ary);
            }
            ary.add(val);
        }
    }

    /**
     * 获取上传文件
     *
     * @param name 文件名
     */
    public abstract List<UploadedFile> files(String name) throws Exception;

    /**
     * 获取上传文件
     *
     * @param name 文件名
     */
    public UploadedFile file(String name) throws Exception {
        return Utils.firstOrNull(files(name));
    }

    /**
     * 获取 cookie
     *
     * @param name cookie名
     */
    public String cookie(String name) {
        return cookieMap().get(name);
    }

    /**
     * 获取 cookie
     *
     * @param name cookie名
     * @param def 默认值
     */
    public String cookie(String name, String def) {
        return cookieMap().getOrDefault(name, def);
    }

    /**
     * 获取 cookieMap
     */
    public abstract NvMap cookieMap();

    /**
     * 获取 header
     *
     * @param name header名
     */
    public String header(String name) {
        return headerMap().get(name);
    }

    /**
     * 获取 header
     *
     * @param name header名
     * @param def 默认值
     */
    public String header(String name, String def) {
        return headerMap().getOrDefault(name, def);
    }

    /**
     * 获取 headerMap
     */
    public abstract NvMap headerMap();

    private SessionState sessionState;

    /**
     * 初始化 sessionState 对象
     * */
    protected void sessionStateInit(SessionState sessionState) {
        if (sessionState().replaceable()) {
            this.sessionState = sessionState;
        }
    }

    /**
     * 获取 sessionState
     * */
    public SessionState sessionState() {
        if (sessionState == null) {
            sessionState = Bridge.sessionState(this);
        }

        return sessionState;
    }

    /**
     * 获取 sessionId
     */
    public final String sessionId() {
        return sessionState().sessionId();
    }

    /**
     * 获取 session 状态
     *
     * @param name 状态名
     */
    public final Object session(String name) {
        return sessionState().sessionGet(name);
    }

    /**
     * 获取 session 状态（类型转换，存在风险）
     *
     * @param name 状态名
     */
    @Note("泛型转换，存在转换风险")
    public final <T> T session(String name, T def) {
        Object tmp = session(name);
        if (tmp == null) {
            return def;
        } else {
            return (T) tmp;
        }
    }

    /**
     * 获取 session 状态，并以 int 型输出
     *
     * @since 1.6
     * @param name 状态名
     */
    public final int sessionAsInt(String name){
        return sessionAsInt(name, 0);
    }

    /**
     * 获取 session 状态，并以 int 型输出
     *
     * @since 1.6
     * @param name 状态名
     */
    public final int sessionAsInt(String name, int def) {
        Object tmp = session(name);
        if (tmp == null) {
            return def;
        } else {
            if (tmp instanceof Number) {
                return ((Number) tmp).intValue();
            } else if (tmp instanceof String) {
                String str = (String) tmp;
                if (str.length() > 0) {
                    return Integer.parseInt(str);
                }
            }

            return def;
        }
    }

    /**
     * 获取 session 状态，并以 long 型输出
     *
     * @since 1.6
     * @param name 状态名
     */
    public final long sessionAsLong(String name){
        return sessionAsLong(name, 0L);
    }

    /**
     * 获取 session 状态，并以 long 型输出
     *
     * @since 1.6
     * @param name 状态名
     */
    public final long sessionAsLong(String name, long def) {
        Object tmp = session(name);
        if (tmp == null) {
            return def;
        } else {
            if (tmp instanceof Number) {
                return ((Number) tmp).longValue();
            } else if (tmp instanceof String) {
                String str = (String) tmp;
                if (str.length() > 0) {
                    return Long.parseLong(str);
                }
            }

            return def;
        }
    }

    /**
     * 获取 session 状态，并以 double 型输出
     *
     * @since 1.6
     * @param name 状态名
     */
    public final double sessionAsDouble(String name) {
        return sessionAsDouble(name, 0.0D);
    }

    /**
     * 获取 session 状态，并以 double 型输出
     *
     * @since 1.6
     * @param name 状态名
     */
    public final double sessionAsDouble(String name, double def) {
        Object tmp = session(name);
        if (tmp == null) {
            return def;
        } else {
            if (tmp instanceof Number) {
                return ((Number) tmp).doubleValue();
            } else if (tmp instanceof String) {
                String str = (String) tmp;
                if (str.length() > 0) {
                    return Double.parseDouble(str);
                }
            }

            return def;
        }
    }

    /**
     * 设置 session 状态
     *
     * @param name 状态名
     * @param val 值
     */
    public final void sessionSet(String name, Object val) {
        sessionState().sessionSet(name, val);
    }

    /**
     * 移除 session 状态
     *
     * @param name 状态名
     * */
    public final void  sessionRemove(String name){
        sessionState().sessionRemove(name);
    }

    /**
     * 清空 session 状态
     * */
    public final void sessionClear() {
        sessionState().sessionClear();
    }

    //======================

    /**
     * 获取响应对象
     */
    public abstract Object response();

    /**
     * 设置字符集
     */
    public void charset(String charset) {
        this.charset = Charset.forName(charset);
    }

    protected Charset charset = StandardCharsets.UTF_8;

    /**
     * 设置内容类型
     */
    public void contentType(String contentType) {
        contentTypeDoSet(contentType);

        //只记录非默认值
        if (ContextUtil.contentTypeDef.equals(contentType) == false) {
            contentTypeNew = contentType;
        }
    }

    /**
     * 获取设置的新内容类型
     * */
    public String contentTypeNew() {
        return contentTypeNew;
    }

    private String contentTypeNew;

    protected abstract void contentTypeDoSet(String contentType);


    /**
     * 输出 字节数组
     */
    public abstract void output(byte[] bytes);

    /**
     * 输出 流对象
     */
    public abstract void output(InputStream stream);

    /**
     * 获取输出流
     */
    public abstract OutputStream outputStream() throws IOException;

    /**
     * 输出 字符串
     */
    public void output(String str) {
        if (str != null) {
            attrSet("output", str);
            output(str.getBytes(charset));
        }
    }

    /**
     * 输出 异常对象
     */
    public void output(Throwable ex) {
        output(Utils.getFullStackTrace(ex));
    }

    /**
     * 输出为json文本
     */
    public void outputAsJson(String json) {
        contentType("application/json;charset=utf-8");
        output(json);
    }

    /**
     * 输出为html文本
     */
    public void outputAsHtml(String html) {
        contentType("text/html;charset=utf-8");
        if (html.startsWith("<") == false) {
            StringBuilder sb = new StringBuilder();
            sb.append("<!doctype html>");
            sb.append("<html>");
            sb.append(html);
            sb.append("</html>");

            output(sb.toString());
        } else {
            output(html);
        }
    }

    /**
     * 输出为文件
     */
    public void outputAsFile(DownloadedFile file) throws IOException {
        if (Utils.isNotEmpty(file.contentType)) {
            contentType(file.contentType);
        }

        if (Utils.isNotEmpty(file.name)) {
            String fileName = URLEncoder.encode(file.name,Solon.cfg().fileEncoding());
            headerSet("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        }

        Utils.transferTo(file.content, outputStream());
    }

    /**
     * 输出为文件
     */
    public void outputAsFile(File file) throws IOException {
        if (Utils.isNotEmpty(file.getName())) {
            String fileName = URLEncoder.encode(file.getName(), Solon.cfg().fileEncoding());
            headerSet("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        }

        try (InputStream ins = new FileInputStream(file)) {
            Utils.transferTo(ins, outputStream());
        }
    }

    /**
     * 设置 header
     */
    public abstract void headerSet(String name, String val);

    /**
     * 添加 header
     */
    public abstract void headerAdd(String name, String val);

    /**
     * 设置 cookie
     * */
    public void cookieSet(String name, String val) {
        cookieSet(name, val, null, -1);
    }

    /**
     * 设置 cookie
     * */
    public void cookieSet(String name, String val, int maxAge) {
        cookieSet(name, val, null, maxAge);
    }

    /**
     * 设置 cookie
     * */
    public void cookieSet(String name, String val, String domain, int maxAge) {
        cookieSet(name, val, domain, "/", maxAge);
    }

    /**
     * 设置 cookie
     * */
    public abstract void cookieSet(String name, String val, String domain, String path, int maxAge);

    /**
     * 移徐 cookie
     */
    public void cookieRemove(String name) {
        cookieSet(name, "", 0);
    }

    /**
     * 跳转地址
     */
    @Note("跳转地址")
    public abstract void redirect(String url);

    @Note("跳转地址")
    public abstract void redirect(String url, int code);

    @Note("转发")
    public void forward(String pathNew){
        pathNew(pathNew);
        Solon.global().tryHandle(this);
        setHandled(true);
        setRendered(true);
    }

    /**
     * 获取输出状态
     */
    @Note("获取输出状态")
    public abstract int status();

    /**
     * 设置输出状态
     */
    @Note("设置输出状态")
    public void status(int status){
        statusDoSet(status);
    }

    @Deprecated
    public void statusSet(int status){
        statusDoSet(status);
    }

    protected abstract void statusDoSet(int status);


    private NdMap attrMap = null;

    @Note("获取自定义特性并转为Map")
    public Map<String, Object> attrMap() {//改为懒加载
        if (attrMap == null) {
            attrMap = new NdMap();
        }

        return attrMap;
    }


    /**
     * 获取上下文特性
     */
    public <T> T attr(String name, T def) {
        Object val = attrMap().get(name);

        if (val == null) {
            return def;
        }

        return (T) val;
    }

    /**
     * 获取上下文特性
     */
    public <T> T attr(String name) {
        return (T) attrMap().get(name);
    }

    /**
     * 设置上下文特性
     */
    public void attrSet(String name, Object val) {
        attrMap().put(name, val);
    }

    /**
     * 设置上下文特性
     */
    public void attrSet(Map<String, Object> map) {
        attrMap().putAll(map);
    }

    /**
     * 清除上下文特性
     */
    public void attrClear() {
        attrMap().clear();
    }

    /**
     * 渲染数据（不能重写，避免死循环）
     */
    public final void render(Object obj) throws Throwable {
        //ModelAndView or Data
        setRendered(true); //用于后续做最多一次渲染的控制
        RenderManager.global.render(obj, this);
    }

    /**
     * 渲染数据
     * */
    public final void render(String view, Map<String, ?> data) throws Throwable {
        render(new ModelAndView(view, data));
    }

    /**
     * 渲染数据并返回
     * */
    public final String renderAndReturn(Object obj) throws Throwable {
        return RenderManager.global.renderAndReturn(obj, this);
    }

    private boolean _remoting;

    /**
     * 是否为远程调用
     * */
    @Note("是否为远程调用")
    public boolean remoting() {
        return _remoting;
    }

    public void remotingSet(boolean remoting) {
        _remoting = remoting;
    }

    @Note("冲刷")
    public abstract void flush() throws IOException;

    //一些特殊的boot才有效
    @Note("提交响应")
    protected void commit() throws IOException {
    }

    //一些特殊的boot才有效
    @Note("关闭响应")
    public void close() throws IOException {
    }

    /**
     * 用于在处理链中透传处理结果
     * */
    @Note("处理结果")
    public Object result;

    /**
     * 用于在处理链中透传处理错误
     * */
    @Note("处理错误")
    public Throwable errors;


    /**
     * 获取当前控制器
     * */
    @Note("控制器?")
    public Object controller() {
        return attr("controller");
    }

    /**
     * 获取当前动作
     * */
    @Note("动作?")
    public Action action() {
        return attr("action");
    }
}
