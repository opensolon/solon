package org.noear.nami;

import org.noear.nami.annotation.NamiClient;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Fairy - 代理器
 *
 * @author noear
 * @since 1.0
 * */
public class Nami {
    /**
     * 默认的通讯通道（涉及第三方框架引用，不做定义）
     */
    public static NamiChannel defaultChannel;
    /**
     * 默认的序列化器（涉及第三方框架引用，不做定义）
     */
    public static Encoder defaultEncoder;

    /**
     * 默认的反序列化器（涉及第三方框架引用，不做定义）
     */
    public static Decoder defaultDecoder;


    private String _url;
    private String _method = "POST";
    private final NamiConfig _config;

    public Nami() {
        _config = new NamiConfig().tryInit();
    }

    /**
     * 给Builder使用
     */
    protected Nami(NamiConfig config) {
        _config = config;
        config.tryInit();
    }

    /**
     * 设置请求方式
     */
    public Nami method(String method) {
        if (method != null && method.length() > 0) {
            _method = method;
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
        try {
            if (headers == null) {
                headers = new HashMap<>();
            }

            if (args == null) {
                args = new HashMap<>();
            }

            for (Filter filter : _config.getFilters()) {
                filter.filter(_config, _method, _url, headers, args);
            }

            _result = _config.getChannel().call(_config, _method, _url, headers, args);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }

        return this;
    }

    private Result _result;

    public Result result() {
        return _result;
    }

    /**
     * 获取结果（以string形式）
     */
    public String getString() {
        return _result.bodyAsString();
    }

    /**
     * 获取结果（返序列化为object）
     */
    public <T> T getObject(Class<T> returnType) {
        if (Void.TYPE.equals(returnType)) {
            return null;
        }else {
            return _config.getDecoder().decode(_result, returnType);
        }
    }

    //////////////////////////////////
    //
    // 下面为动态代理部分
    //
    //////////////////////////////////


    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final NamiConfig _config;

        protected Builder() {
            _config = new NamiConfig();
        }

        protected Builder(NamiConfig config) {
            _config = config;
        }

        /**
         * 设置负载代理
         */
        public Builder upstream(Supplier<String> upstream) {
            _config.setUpstream(upstream);
            return this;
        }

        /**
         * 设置序列化器
         */
        public Builder encoder(Encoder encoder) {
            _config.setEncoder(encoder);
            return this;
        }

        /**
         * 设置反序列器
         */
        public Builder decoder(Decoder decoder) {
            _config.setDecoder(decoder);
            return this;
        }


        /**
         * 设置通信通道
         */
        public Builder channel(NamiChannel channel) {
            _config.setChannel(channel);
            return this;
        }


        /**
         * 添加过滤器
         */
        public Builder filterAdd(Filter filter) {
            _config.filterAdd(filter);
            return this;
        }


        /**
         * 设置服务端
         */
        public Builder uri(String uri) {
            _config.setUri(uri);
            return this;
        }

        public Nami build() {
            return new Nami(_config);
        }

        /**
         * 创建接口代理
         */
        public <T> T create(Class<?> clz) {
            NamiClient client = clz.getAnnotation(NamiClient.class);

            return (T) create(clz, client);
        }

        /**
         * 创建接口代理
         */
        public Object create(Class<?> clz, NamiClient client) {
            if (clz == null) {
                return null;
            }

            if (clz.isInterface() == false) {
                throw new NamiException("FairyClient only support interfaces");
            }

            NamiHandler handler = new NamiHandler(clz, _config, client);

            return Proxy.newProxyInstance(
                    clz.getClassLoader(),
                    new Class[]{clz},
                    handler);
        }
    }
}
