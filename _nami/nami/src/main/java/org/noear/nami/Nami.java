package org.noear.nami;

import org.noear.nami.common.Constants;
import org.noear.nami.common.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * Nami（Solon rest * rpc client）
 *
 * @author noear
 * @since 1.0
 * */
public class Nami {
    static final Logger log = LoggerFactory.getLogger(Nami.class);

    /**
     * 默认的序列化器（涉及第三方框架引用，不做定义）
     */
    public static Encoder defaultEncoder;

    /**
     * 默认的反序列化器（涉及第三方框架引用，不做定义）
     */
    public static Decoder defaultDecoder;


    private String _url;
    private String _action = "POST";
    private Object _target;
    private Method _method;
    private final Config _config;

    public Nami() {
        _config = new Config().init();
    }

    /**
     * 给Builder使用
     */
    protected Nami(Config config) {
        _config = config;
        _config.init();
    }

    /**
     * 设置请求方法
     */
    public Nami method(Object target, Method method) {
        if (method != null) {
            _target = target;
            _method = method;
        }
        return this;
    }


    /**
     * 设置请求动作
     */
    public Nami action(String action) {
        if (action != null && action.length() > 0) {
            _action = action;
        }
        return this;
    }

    /**
     * 设置请求地址
     */
    public Nami url(String url) {
        _url = url;
        return this;
    }

    /**
     * 设置请求地址
     */
    public Nami url(String url, String fun) {
        if (url.indexOf("{fun}") > 0) {
            _url = url.replace("{fun}", fun);
        } else {
            if (fun == null) {
                _url = url;
            } else {
                StringBuilder sb = new StringBuilder(200);

                sb.append(url);
                if (url.endsWith("/")) {
                    if (fun.startsWith("/")) {
                        sb.append(fun.substring(1));
                    } else {
                        sb.append(fun);
                    }
                } else {
                    if (fun.startsWith("/")) {
                        sb.append(fun);
                    } else {
                        sb.append("/").append(fun);
                    }
                }

                _url = sb.toString();
            }
        }
        return this;
    }

    /**
     * 执行完成呼叫
     */
    public Nami call(Map<String, String> headers, Map args) {
        return call(headers, args, null);
    }

    public Nami call(Map<String, String> headers, Map args, Object body) {
        try {
            Invocation invocation = new Invocation(_config, _target,_method, _action, _url, this::callDo);


            if (headers != null) {
                invocation.headers.putAll(headers);
            }

            if (args != null) {
                invocation.args.putAll(args);
            }

            if (body != null) {
                invocation.body = body;
            }

            _result = invocation.invoke();
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }

        return this;
    }


    private Result callDo(Invocation inv) throws Throwable {
        Channel channel = _config.getChannel();

        if (channel == null) {
            //通过 scheme 获取通道
            int idx = inv.url.indexOf("://");
            if (idx > 0) {
                String scheme = inv.url.substring(0, idx);
                channel = NamiManager.getChannel(scheme);
            }
        }

        if (channel == null) {
            StringBuilder buf = new StringBuilder();
            buf.append("There is no channel available for the request: ");
            if (TextUtils.isNotEmpty(_config.getName())) {
                buf.append("'").append(_config.getName()).append("'");
            }
            buf.append(": ").append(inv.url);

            throw new NamiException(buf.toString());
        }

        if (inv.body == null) {
            inv.body = inv.args;
        }

        log.trace("Nami call: {}", inv.url);

        return channel.call(inv);
    }

    private Result _result;

    public Result result() {
        return _result;
    }

    /**
     * 获取结果（以string形式）
     */
    public String getString() {
        if (_result == null) {
            return null;
        } else {
            return _result.bodyAsString();
        }
    }

    /**
     * 获取结果（返序列化为object）
     */
    public <T> T getObject(Type returnType) {
        if (_result == null) {
            return null;
        }

        if (Void.TYPE.equals(returnType)) {
            if (_result.body() == null || _result.body().length < 20) {
                return null;
            }
        }


        Decoder decoder = _config.getDecoder();

        if (decoder == null) {
            decoder = NamiManager.getDecoder(Constants.CONTENT_TYPE_JSON);
        }

        return decoder.decode(_result, returnType);
    }

    public static NamiBuilder builder() {
        return new NamiBuilder();
    }
}
