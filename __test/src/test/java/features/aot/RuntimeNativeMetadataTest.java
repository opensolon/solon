package features.aot;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.Solon;
import org.noear.solon.aot.RuntimeNativeMetadata;
import org.noear.solon.aot.hint.ExecutableMode;
import org.noear.solon.aot.hint.MemberCategory;
import org.noear.solon.core.util.ResourceUtil;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;
import webapp.App;

import java.io.IOException;
import java.lang.reflect.Constructor;

/**
 * @author songyinyin
 * @since 2023/4/7 16:43
 */
@RunWith(SolonJUnit4ClassRunner.class)
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

        String reflectionJson = metadata.toReflectionJson();
        System.out.println(reflectionJson);
        String resourceAsString = ResourceUtil.getResourceAsString("test-reflect-config.json");

        Assert.assertEquals(reflectionJson, resourceAsString);
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

        Assert.assertEquals(resourcesJson, resourceAsString);
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

        Assert.assertEquals(resourcesJson, resourceAsString);
    }
}
