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
package features.aot;

import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;
import org.noear.solon.Solon;
import org.noear.solon.aot.RuntimeNativeMetadata;
import org.noear.solon.aot.hint.ExecutableMode;
import org.noear.solon.aot.hint.MemberCategory;
import org.noear.solon.core.util.ResourceUtil;
import org.noear.solon.test.SolonTest;
import webapp.App;

import java.io.IOException;
import java.lang.reflect.Constructor;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author songyinyin
 * @since 2023/4/7 16:43
 */
@SolonTest(App.class)
public class RuntimeNativeMetadataTest {

    @Test
    public void testReflectionJson() throws NoSuchMethodException, NoSuchFieldException, IOException {
        RuntimeNativeMetadata metadata = new RuntimeNativeMetadata();
        metadata.registerReflection(Solon.class, hint -> {
                    hint.getMemberCategories().add(MemberCategory.PUBLIC_FIELDS);
                    hint.getMemberCategories().add(MemberCategory.DECLARED_FIELDS);
                    hint.setReachableType("org.noear.solon.Solon");
                })
                .registerMethod(Solon.class.getMethod("version"), ExecutableMode.INVOKE)
                .registerField(Solon.class.getDeclaredField("encoding"));

        Constructor<?>[] constructors = Solon.class.getConstructors();
        for (Constructor<?> constructor : constructors) {
            metadata.registerConstructor(constructor, ExecutableMode.INTROSPECT);
        }
        metadata.registerField(NativeDTO.NativeDTO2.class.getDeclaredField("subName"));

        String resourcesJson = metadata.toReflectionJson();
        System.out.println(resourcesJson);
        String resourceAsString = ResourceUtil.getResourceAsString("test-reflect-config.json");

        assertEquals(ONode.ofJson(resourcesJson).toJson(), ONode.ofJson(resourceAsString).toJson());
    }

    @Test
    public void testResourceJson() throws IOException {
        RuntimeNativeMetadata metadata = new RuntimeNativeMetadata();

        metadata.registerResourceInclude("/")
                .registerResourceInclude("app.*\\.yml")
                .registerResourceInclude("app.*\\.properties")
                .registerResourceInclude("META-INF")
                .registerResourceInclude("META-INF/solon")
                .registerResourceInclude("META-INF/solon/.*")
                .registerResourceInclude("META-INF/solon_def/.*");

        String resourcesJson = metadata.toResourcesJson();
        System.out.println(resourcesJson);

        String resourceAsString = ResourceUtil.getResourceAsString("test-resource-config.json");

        assertEquals(ONode.ofJson(resourcesJson).toJson(), ONode.ofJson(resourceAsString).toJson());
    }

    @Test
    public void testSerializationJson() throws IOException {
        RuntimeNativeMetadata metadata = new RuntimeNativeMetadata();

        metadata.registerSerialization(NativeDTO.NativeDTO2.class)
                .registerSerialization(MyUser.class);

        String resourcesJson = metadata.toSerializationJson();
        System.out.println(resourcesJson);

        String resourceAsString = ResourceUtil.getResourceAsString("test-serialization-config.json");
        System.out.println(resourceAsString);

        assertEquals(ONode.ofJson(resourcesJson).toJson(), ONode.ofJson(resourceAsString).toJson());
    }
}
