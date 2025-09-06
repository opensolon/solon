/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.nami;

import org.noear.nami.common.ContentTypes;
import org.noear.nami.common.TextUtils;
import org.noear.solon.core.util.PathUtil;
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
    public Nami url(String baseUrl, String path) {
        if (baseUrl.indexOf("{fun}") > 0) {
            _url = baseUrl.replace("{fun}", path);
        } else {
            if (path == null) {
                _url = baseUrl;
            } else {
                _url = PathUtil.joinUri(baseUrl, path);
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

    /**
     * 执行完成呼叫
     */
    public Nami call(Map<String, String> headers, Map args, Object body) {
        try {
            return callOrThrow(headers, args, body);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 执行完成呼叫或异常
     */
    public Nami callOrThrow(Map<String, String> headers, Map args, Object body) throws Throwable {
        Invocation invocation = new Invocation(_config, _target, _method, _action, _url, body, this::callDo);

        if (headers != null) {
            invocation.headers.putAll(headers);
        }

        if (args != null) {
            invocation.args.putAll(args);
        }

        _result = invocation.invoke();
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

        log.trace("Nami call: {}", inv.url);

        return channel.call(inv);
    }

    private Result _result;

    /**
     * 获取结果
     */
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
            //断言成功
            _result.assertSuccess();

            return _result.bodyAsString();
        }
    }

    /**
     * 获取结果（返序列化为object）
     *
     * @param returnType 返回类型
     */
    public <T> T getObject(Type returnType) {
        try {
            return getObjectOrThrow(returnType);
        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取结果或异步（返序列化为object）
     *
     * @param returnType 返回类型
     */
    public <T> T getObjectOrThrow(Type returnType) throws Throwable {
        if (_result == null) {
            return null;
        } else {
            _result.assertSuccess();

            if (Void.TYPE.equals(returnType)) {
                return null;
            }

            Decoder decoder = _config.getDecoder();

            if (decoder == null) {
                decoder = NamiManager.getDecoder(ContentTypes.JSON_VALUE);
            }

            Object returnVal = decoder.decode(_result, returnType);

            if (returnVal != null && returnVal instanceof Throwable) {
                if (returnVal instanceof RuntimeException) {
                    throw (RuntimeException) returnVal;
                } else {
                    throw new RuntimeException((Throwable) returnVal);
                }
            } else {
                return (T) returnVal;
            }
        }
    }

    /**
     * 新建构建器
     */
    public static NamiBuilder builder() {
        return new NamiBuilder();
    }
}