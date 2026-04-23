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

import features.serialization.javabin.model.UserDo;
import org.junit.jupiter.api.Test;
import org.noear.solon.serialization.javabin.JavabinClassFilter;
import org.noear.solon.serialization.javabin.JavabinSerializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 安全性回归测试：
 *
 * <ul>
 *   <li>默认过滤器下，非白名单的 POJO 类会在 {@code readObject} 之前被
 *       {@link InvalidClassException} 拒绝——这是本次修复的关键断言</li>
 *   <li>即使声明类型是 {@code String.class}，也不会绕过过滤器先反序列化</li>
 *   <li>{@link JavabinClassFilter#unrestricted()} 兼容旧行为</li>
 * </ul>
 */
public class UnsafeClassDeniedTest {

    @Test
    public void probePayloadRejectedBeforeReadObject() throws Exception {
        ProbePayload.reset();

        JavabinSerializer ser = new JavabinSerializer();
        String encoded = encodeRawJava(new ProbePayload("attacker-controlled bytes"));

        assertThrows(InvalidClassException.class,
                () -> ser.deserialize(encoded, Object.class));

        assertFalse(ProbePayload.READ_OBJECT_CALLED.get(),
                "readObject 应该在反序列化前就被过滤掉");
    }

    @Test
    public void declaredTypeStringDoesNotBypassFilter() throws Exception {
        ProbePayload.reset();

        JavabinSerializer ser = new JavabinSerializer();
        String encoded = encodeRawJava(new ProbePayload("attacker-controlled bytes"));

        // 老版本会先反序列化、再抛 ClassCastException；新版本应该在反序列化之前
        // 就抛 InvalidClassException
        assertThrows(InvalidClassException.class,
                () -> ser.deserialize(encoded, String.class));
        assertFalse(ProbePayload.READ_OBJECT_CALLED.get(),
                "String.class 不应该绕开过滤器");
    }

    @Test
    public void allowedCustomClassStillWorks() throws Exception {
        JavabinSerializer ser = new JavabinSerializer();
        ser.classFilter().allow("features.serialization.javabin.model.");

        UserDo src = new UserDo("Jerry", 30);
        String encoded = ser.serialize(src);
        Object decoded = ser.deserialize(encoded, UserDo.class);
        assertTrue(decoded instanceof UserDo);
    }

    @Test
    public void unrestrictedFilterAllowsEverything() throws Exception {
        ProbePayload.reset();

        JavabinSerializer ser = new JavabinSerializer(JavabinClassFilter.unrestricted());
        String encoded = encodeRawJava(new ProbePayload("attacker-controlled bytes"));

        Object decoded = ser.deserialize(encoded, Object.class);
        assertTrue(decoded instanceof ProbePayload);
        assertTrue(ProbePayload.READ_OBJECT_CALLED.get());
    }

    @Test
    public void knownGadgetRootsDenied() {
        JavabinClassFilter filter = JavabinClassFilter.defaults();
        assertFalse(filter.isAllowed("java.lang.Runtime"));
        assertFalse(filter.isAllowed("javax.management.BadAttributeValueExpException"));
        assertFalse(filter.isAllowed("javax.naming.InitialContext"));
        assertFalse(filter.isAllowed("com.sun.rowset.JdbcRowSetImpl"));
    }

    private static String encodeRawJava(Serializable value) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(value);
        }
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }

    /**
     * 探针：用 readObject side effect 来观察反序列化是否真的发生了。
     * 真实攻击里这里会是 ysoserial 的 gadget 链。
     */
    static final class ProbePayload implements Serializable {
        private static final long serialVersionUID = 1L;
        static final AtomicBoolean READ_OBJECT_CALLED = new AtomicBoolean(false);

        String note;

        ProbePayload(String note) { this.note = note; }

        static void reset() { READ_OBJECT_CALLED.set(false); }

        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            in.defaultReadObject();
            READ_OBJECT_CALLED.set(true);
        }
    }
}
