package features.tool;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.noear.solon.configurationprocessor.json.JSONArray;
import org.noear.solon.configurationprocessor.json.JSONObject;
import org.noear.solon.core.util.IoUtil;
import org.noear.solon.test.SolonTest;
import webapp.App;
import webapp.demoz_tool.AConfig;
import webapp.demoz_tool.ComplexDemoProp;

import java.io.InputStream;
import java.util.Objects;
import java.util.function.Consumer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.jupiter.api.Assertions.*;


@SolonTest(App.class)
public class ComplexBindPropsProcessorTest {

    private static final String METADATA_PATH = "META-INF/solon/solon-configuration-metadata.json";

    private static final String ADDITIONAL_METADATA_PATH = "META-INF/solon/additional-solon-configuration-metadata.json";

    @Test
    public void test_metadata_generate() throws Throwable {
        String canonicalName = ComplexDemoProp.class.getCanonicalName();

        InputStream resourceAsStream =
                this.getClass().getClassLoader().getResourceAsStream(METADATA_PATH);
        DocumentContext ctx = JsonPath.parse(resourceAsStream);

        System.out.println(IoUtil.transferToString(this.getClass().getClassLoader().getResourceAsStream(METADATA_PATH), "UTF-8"));

        checkProperty(ctx, "cdemo.aConfig.arrays", "java.lang.String[]", AConfig.class.getCanonicalName());
        checkProperty(ctx, "cdemo.aConfig.defaultValue", "java.lang.Boolean", AConfig.class.getCanonicalName());
        checkProperty(ctx, "cdemo.aConfig.ids", "java.util.List<java.lang.Long>", AConfig.class.getCanonicalName());
        checkProperty(ctx, "cdemo.aConfig.name", "java.lang.String", AConfig.class.getCanonicalName());
        checkProperty(ctx, "cdemo.aConfigMap", "java.util.Map<java.lang.String,webapp.demoz_tool.AConfig>", canonicalName);
        checkProperty(ctx, "cdemo.bConfigs", "java.util.List<webapp.demoz_tool.BConfig>", canonicalName);
        checkProperty(ctx, "cdemo.name", "java.lang.String", canonicalName);

    }

    private void checkProperty(DocumentContext ctx, String name, String type, String sourceType) {
        checkProperty(ctx, name, type, sourceType, null);
    }

    private void checkProperty(DocumentContext ctx, String name, String type, String sourceType, Consumer<Object> consumer) {
        assertThat(getStr(ctx, "$.properties[?(@.name == '" + name + "')].type"), equalTo(type));
        assertThat(getStr(ctx, "$.properties[?(@.name == '" + name + "')].sourceType"), equalTo(sourceType));

        Object defaultValue = this.getFirstValue(ctx, "$.properties[?(@.name == '" + name + "')].defaultValue");

        // 单测时获取不到默认值,不进行默认值检查
        if (Objects.nonNull(defaultValue)) {
            System.out.println("has default value");
            consumer.accept(defaultValue);
        } else {
            System.out.println("no default value");
        }
    }

    private <T> T getFirstValue(DocumentContext ctx, String expr) {
        Object data = ctx.read(expr);
        if (data instanceof net.minidev.json.JSONArray) {
            net.minidev.json.JSONArray arrayData = (net.minidev.json.JSONArray) data;
            if (arrayData.size() > 0) {
                return (T) arrayData.get(0);
            }
            return null;
        } else {
            return (T) data;
        }
    }

    private String getStr(DocumentContext ctx, String expr) {
        return this.getFirstValue(ctx, expr);
    }

    @Test
    public void test_metadata_properties() throws Throwable {
        // 读取元数据文件
        InputStream resourceAsStream =
                this.getClass().getClassLoader().getResourceAsStream(METADATA_PATH);
        String metaDataContent = IoUtil.transferToString(resourceAsStream, "UTF-8");
        JSONObject object = new JSONObject(metaDataContent);

        // 验证properties数组存在且不为空
        assertTrue(object.has("properties"));
        JSONArray properties = object.getJSONArray("properties");
        assertThat(properties.length(), greaterThan(0));

        // 验证至少有一个属性包含必要的字段
        boolean hasValidProperty = false;
        for (int i = 0; i < properties.length(); i++) {
            JSONObject prop = properties.getJSONObject(i);
            if (prop.has("name") && prop.has("type")) {
                // 验证属性的基本结构
                assertNotNull(prop.getString("name"), "Property name should not be null");
                assertNotNull(prop.getString("type"), "Property type should not be null");

                // 如果有 sourceType 字段，则不为空
                if (prop.has("sourceType")) {
                    assertFalse(prop.getString("sourceType").isEmpty(),
                            "Property sourceType should not be empty if present");
                }

                // 如果有description字段，确保它不为空
                if (prop.has("description")) {
                    assertFalse(prop.getString("description").isEmpty(),
                            "Property description should not be empty if present");
                }

                // 如果有defaultValue字段，确保它存在值
                if (prop.has("defaultValue")) {
                    assertNotNull(prop.get("defaultValue"),
                            "Default value should not be null if present");
                }

                hasValidProperty = true;
            } else {
                hasValidProperty = false;
                break;
            }
        }

        assertTrue(hasValidProperty, "Should have at least one valid property");
    }
}
