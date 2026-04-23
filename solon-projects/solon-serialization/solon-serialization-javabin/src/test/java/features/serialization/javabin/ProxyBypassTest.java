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
package features.serialization.javabin;

import org.junit.jupiter.api.Test;
import org.noear.solon.serialization.javabin.JavabinSerializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Base64;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * 动态代理绕过回归。
 *
 * <p>ysoserial 多条链以 {@link Proxy} + 恶意 {@code InvocationHandler}（典型如
 * {@code AnnotationInvocationHandler}）起手；JVM 反序列化代理类时走
 * {@code ObjectInputStream.resolveProxyClass(String[] interfaces)} 而不是
 * {@code resolveClass}。如果过滤器只覆盖 {@code resolveClass}，这类 payload
 * 会完全绕过名单。
 *
 * <p>本测试构造一个只暴露 {@link Map} 接口 + 恶意 handler 的代理对象，用旧的
 * 只过 {@code resolveClass} 的实现会让 handler 的 {@code invoke} 被触发；新的
 * {@link org.noear.solon.serialization.javabin.SafeObjectInputStream}
 * 覆盖了 {@code resolveProxyClass}，所以 handler 永远不会被创建。
 */
public class ProxyBypassTest {

    @Test
    public void proxyHandlerDeniedByDefault() throws Exception {
        MaliciousHandler.reset();

        // 构造代理字节流：暴露 Map 接口（默认白名单里允许，不会触发 resolveClass 的拒绝）
        // + 恶意 handler（Runtime-like payload 放 handler 里，触发则意味着 gadget 成功）
        Map<?, ?> proxy = (Map<?, ?>) Proxy.newProxyInstance(
                ProxyBypassTest.class.getClassLoader(),
                new Class<?>[]{Map.class},
                new MaliciousHandler());

        String encoded = encodeRawJava((Serializable) proxy);

        JavabinSerializer ser = new JavabinSerializer();
        // 代理类反序列化必须在 resolveProxyClass 阶段被拒掉，handler 绝对不能创建
        assertThrows(InvalidClassException.class,
                () -> ser.deserialize(encoded, Map.class));

        assertFalse(MaliciousHandler.INVOKED.get(),
                "MaliciousHandler.invoke 不应被触发——resolveProxyClass 阶段就应该被拒");
    }

    @Test
    public void proxyInterfaceNotInAllowListDenied() throws Exception {
        MaliciousHandler.reset();

        // 自定义接口，不在白名单里
        Runnable proxy = (Runnable) Proxy.newProxyInstance(
                ProxyBypassTest.class.getClassLoader(),
                new Class<?>[]{Runnable.class},
                new MaliciousHandler());

        String encoded = encodeRawJava((Serializable) proxy);

        JavabinSerializer ser = new JavabinSerializer();
        // Runnable 在 java.lang，默认是允许的；但若我们再包一层拒绝 Runnable，
        // 就能验证 proxy 的每个 interface 都会被过滤
        ser.classFilter().deny("java.lang.Runnable");

        assertThrows(InvalidClassException.class,
                () -> ser.deserialize(encoded, Object.class));
        assertFalse(MaliciousHandler.INVOKED.get());
    }

    private static String encodeRawJava(Serializable value) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(value);
        }
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }

    /**
     * 模拟恶意 handler：如果 readObject 被执行到这里就意味着 gadget 成功。
     * 真实利用中这里会是 AnnotationInvocationHandler / TemplatesImpl 等。
     */
    public static final class MaliciousHandler implements InvocationHandler, Serializable {
        private static final long serialVersionUID = 1L;
        static final java.util.concurrent.atomic.AtomicBoolean INVOKED =
                new java.util.concurrent.atomic.AtomicBoolean(false);

        static void reset() { INVOKED.set(false); }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            INVOKED.set(true);
            return null;
        }
    }
}
