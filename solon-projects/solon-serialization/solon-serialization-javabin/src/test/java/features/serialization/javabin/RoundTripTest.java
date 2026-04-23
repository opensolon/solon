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
import org.noear.solon.serialization.javabin.JavabinSerializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * JavabinSerializer 基础往返测试：默认允许的 JDK 类型能正常序列化/反序列化。
 */
public class RoundTripTest {

    @Test
    public void roundTripString() throws IOException {
        JavabinSerializer ser = new JavabinSerializer();
        String encoded = ser.serialize("hello");
        Object decoded = ser.deserialize(encoded, String.class);
        assertEquals("hello", decoded);
    }

    @Test
    public void roundTripList() throws IOException {
        JavabinSerializer ser = new JavabinSerializer();
        List<String> src = new ArrayList<>(Arrays.asList("a", "b", "c"));
        String encoded = ser.serialize(src);
        Object decoded = ser.deserialize(encoded, List.class);
        assertEquals(src, decoded);
    }

    @Test
    public void roundTripMap() throws IOException {
        JavabinSerializer ser = new JavabinSerializer();
        Map<String, Integer> src = new HashMap<>();
        src.put("x", 1);
        src.put("y", 2);
        String encoded = ser.serialize(src);
        Object decoded = ser.deserialize(encoded, Map.class);
        assertEquals(src, decoded);
    }

    @Test
    public void roundTripCustomPojoAfterAllow() throws IOException {
        JavabinSerializer ser = new JavabinSerializer();
        ser.classFilter().allow("features.serialization.javabin.model.");

        UserDo src = new UserDo("Jerry", 30);
        String encoded = ser.serialize(src);
        Object decoded = ser.deserialize(encoded, UserDo.class);
        assertEquals(src, decoded);
    }

    @Test
    public void nullInputsRoundTrip() throws IOException {
        JavabinSerializer ser = new JavabinSerializer();
        assertNull(ser.serialize(null));
        assertNull(ser.deserialize(null, String.class));
    }
}
