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
package features.serialization.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.io.Output;
import features.serialization.kryo.model.UserDo;
import org.junit.jupiter.api.Test;
import org.noear.solon.serialization.kryo.KryoBytesSerializer;
import org.noear.solon.serialization.kryo.KryoClassFilter;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Kryo 反序列化类过滤与安全性测试
 */
public class KryoSecurityTest {

    public static class UntrustedPayload {
        private String command;

        public UntrustedPayload() {}

        public UntrustedPayload(String command) {
            this.command = command;
        }

        public String getCommand() {
            return command;
        }

        public void setCommand(String command) {
            this.command = command;
        }
    }

    private byte[] generateRawKryoBytes(Object obj) {
        Kryo kryo = new Kryo();
        kryo.setRegistrationRequired(false);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (Output output = new Output(baos)) {
            kryo.writeClassAndObject(output, obj);
        }
        return baos.toByteArray();
    }

    @Test
    public void unallowedClassRejectedByDefault() {
        KryoBytesSerializer serializer = new KryoBytesSerializer();
        byte[] payload = generateRawKryoBytes(new UntrustedPayload("calc.exe"));

        assertThrows(KryoException.class, () -> serializer.deserialize(payload, Object.class));
    }

    @Test
    public void allowedClassSuccess() throws Exception {
        KryoBytesSerializer serializer = new KryoBytesSerializer();
        serializer.classFilter().allow("features.serialization.kryo.");

        byte[] payload = generateRawKryoBytes(new UntrustedPayload("safe_command"));
        Object obj = serializer.deserialize(payload, Object.class);

        assertNotNull(obj);
        assertTrue(obj instanceof UntrustedPayload);
        assertEquals("safe_command", ((UntrustedPayload) obj).getCommand());
    }

    @Test
    public void defaultAllowedJdkClassesSuccess() throws Exception {
        KryoBytesSerializer serializer = new KryoBytesSerializer();

        Map<String, String> map = new HashMap<>();
        map.put("key", "value");

        byte[] payload = serializer.serialize(map);
        Object obj = serializer.deserialize(payload, Object.class);

        assertNotNull(obj);
        assertTrue(obj instanceof Map);
        assertEquals("value", ((Map<?, ?>) obj).get("key"));
    }

    @Test
    public void unrestrictedModeAllowsAll() throws Exception {
        KryoBytesSerializer serializer = new KryoBytesSerializer(KryoClassFilter.unrestricted());
        byte[] payload = generateRawKryoBytes(new UntrustedPayload("unrestricted_mode"));

        Object obj = serializer.deserialize(payload, Object.class);
        assertNotNull(obj);
        assertTrue(obj instanceof UntrustedPayload);
        assertEquals("unrestricted_mode", ((UntrustedPayload) obj).getCommand());
    }

    @Test
    public void knownGadgetRootsDenied() {
        KryoClassFilter filter = KryoClassFilter.defaults();
        assertFalse(filter.isAllowed("java.lang.Runtime"));
        assertFalse(filter.isAllowed("java.lang.ProcessBuilder"));
        assertFalse(filter.isAllowed("javax.management.BadAttributeValueExpException"));
        assertFalse(filter.isAllowed("javax.naming.InitialContext"));
        assertFalse(filter.isAllowed("com.sun.rowset.JdbcRowSetImpl"));
    }
}
