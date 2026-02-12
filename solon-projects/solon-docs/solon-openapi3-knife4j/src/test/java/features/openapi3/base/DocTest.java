package features.openapi3.base;

import com.fasterxml.jackson.databind.JsonNode;
import demo.openapi3.base.App;
import org.junit.jupiter.api.Test;
import org.noear.solon.core.util.MimeType;
import org.noear.solon.docs.DocDocket;
import org.noear.solon.docs.models.ApiInfo;
import org.noear.solon.docs.openapi3.JacksonSerializer;
import org.noear.solon.docs.openapi3.OpenApi3Utils;
import org.noear.solon.test.SolonTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author noear 2025/12/17 created
 */
@SolonTest(App.class)
public class DocTest {
    @Test
    public void testOpenApiGeneration() throws Exception {
        final List<String> items = Arrays.asList("openapi", "info", "servers", "tags", "paths", "components");

        final String groupName = "issues";
        DocDocket docDocket = new DocDocket()
                .groupName(groupName)
                .apis("com.swagger.demo");

        String json = OpenApi3Utils.getSwaggerJson(docDocket);
        JsonNode jsonNode = JacksonSerializer.getInstance().getMapper().readTree(json);

        // 验证生成的JSON不是空的
        assert json != null && !json.isEmpty() : "Generated JSON should not be null or empty";

        // 验证JSON结构的基本完整性
        assert jsonNode.has("openapi") : "Missing openapi field";
        assert jsonNode.has("info") : "Missing info field";

        // 额外验证：确保没有多余的字段
        jsonNode.fieldNames().forEachRemaining(fieldName -> {
            assert items.contains(fieldName) : "Unexpected field: " + fieldName;
        });

        // 验证必需的根节点存在
        items.forEach(item -> {
            assert jsonNode.has(item) : "Missing required field: " + item;
        });

        // 验证具体的字段值
        jsonNode.forEachEntry((name, value) -> {
            switch (name) {
                case "openapi":
                    assert value.asText().startsWith("3.") :
                            "Expected openapi version '3.0.3', but got " + value.asText();
                    break;
                case "info":
                    // info 是一个map
                    assert value.isObject() : "info should be an object";
                    assert groupName.equals(value.get("title").asText()) : "info should have a title field and title is groupName: " + groupName;
                    break;
                case "servers":
                    assert value.isArray() : "servers should be an array";
                    break;
                case "tags":
                    assert value.isArray() : "tags should be an array";
                    break;
                case "paths":
                    assert value.isObject() : "paths should be an object";
                    break;
                case "components":
                    assert value.isObject() : "components should be an object";
                    break;
            }
        });

        jsonNode.get("paths").forEach(path -> {
            assert path.isObject() : "All values in the JSON should be objects";
            path.forEachEntry((key, operation) -> {
                assert key.equals("get") || key.equals("post") || key.equals("put") || key.equals("delete") || key.equals("patch") || key.equals("head") || key.equals("options") || key.equals("trace") :
                        "Unexpected HTTP method: " + key;
                assert operation.isObject() : "All values in the path object should be objects";
                assert operation.has("summary") : "Each operation should have a summary field";
                assert operation.has("description") : "Each operation should have a description field";
                assert operation.has("parameters") : "Each operation should have a parameters field";
                assert operation.has("responses") : "Each operation should have a responses field";
            });
        });

    }

    @Test
    public void testEmptyDocketConfiguration() throws Exception {
        // 测试空配置的DocDocket
        DocDocket emptyDocket = new DocDocket();

        String json = OpenApi3Utils.getSwaggerJson(emptyDocket);

        assert json != null : "Empty docket should still generate valid JSON";

        JsonNode jsonNode = JacksonSerializer.getInstance().getMapper().readTree(json);

        // 即使是空配置也应该包含基本的OpenAPI结构
        assert jsonNode.has("openapi") : "Even empty docket should have openapi field";
        assert jsonNode.has("info") : "Even empty docket should have info field";
    }

    @Test
    public void testDifferentApiGroups() throws Exception {
        // 测试不同的API分组配置
        DocDocket docket1 = new DocDocket().groupName("测试分组1").apis("test.package1");
        DocDocket docket2 = new DocDocket().groupName("测试分组2").apis("test.package2");

        String json1 = OpenApi3Utils.getSwaggerJson(docket1);
        String json2 = OpenApi3Utils.getSwaggerJson(docket2);

        assert json1 != null && json2 != null : "Both dockets should generate valid JSON";
        assert !json1.equals(json2) : "Different configurations should produce different JSON";

        JsonNode node1 = JacksonSerializer.getInstance().getMapper().readTree(json1);
        JsonNode node2 = JacksonSerializer.getInstance().getMapper().readTree(json2);

        assert node1.get("info").get("title").asText().equals("测试分组1") : "First docket title should match";
        assert node2.get("info").get("title").asText().equals("测试分组2") : "Second docket title should match";
    }

    @Test
    public void testResponseType() throws Exception {
        final String groupName = "test";
        DocDocket docDocket = new DocDocket()
                .groupName(groupName)
                .apis("demo.openapi3.base.test2");

        String json = OpenApi3Utils.getSwaggerJson(docDocket);
        JsonNode jsonNode = JacksonSerializer.getInstance().getMapper().readTree(json);

        JsonNode post = jsonNode.get("paths").get("/appPage").get("post");

        assert post.get("responses").get("200").get("content").get(MimeType.APPLICATION_FORM_URLENCODED_VALUE).get("schema").get("$ref").asText().equals("#/components/schemas/自定义分页查询结果«Page«ApplicationVo»»") : "Response type should be correct";
        assert post.get("parameters").get(0).get("schema").get("$ref").asText().equals("#/components/schemas/自定义分页查询结果«ApplicationVo»") : "Parameter type should be correct";
        assert post.get("parameters").get(0).get("name").asText().equals("queryParam") : "Parameter name should be correct";
        assert post.get("parameters").get(0).get("in").asText().equals("query") : "Parameter in should be correct";
        assert post.get("parameters").get(0).get("description").asText().equals("Parameter 缺参数说明") : "Parameter description should be correct";
        assert post.get("parameters").get(0).get("required").asBoolean() : "Parameter required should be correct";
        assert post.get("summary").asText().equals("分页查询信息列表") : "Summary should be correct";
        assert post.get("description").asText().equals("查询条件：应用名称") : "Description should be correct";
        assert !post.get("deprecated").asBoolean() : "Deprecated should be correct";
    }

    // path Head body file 测试
    /*
    @Operation(summary = "测试请求类型注解")
    @Mapping("/testRequestType/{appid}")
    @Post
    public R<String> list(@Parameter(description = "用户授权", required = true) @Header String token,
                          @Parameter(description = "微信openId") String openId,
                          @Parameter(description = "上传附件", required = false) UploadedFile file,
                          @Path String appId,
                          @Body List<ApplicationQueryPo> applicationQueryPo) {
        return R.ok(null);
    }
    * */
    @Test
    public void testPathHeadBodyFile() throws Exception {
        final String groupName = "test";
        DocDocket docDocket = new DocDocket()
                .groupName(groupName)
                .apis("demo.openapi3.base.test2");

        String json = OpenApi3Utils.getSwaggerJson(docDocket);
        JsonNode jsonNode = JacksonSerializer.getInstance().getMapper().readTree(json);

        JsonNode post = jsonNode.get("paths").get("/testRequestType/{appid}").get("post");

        assert post.get("parameters").get(0).get("name").asText().equals("token") : "Parameter[0] name should is token";
        assert post.get("parameters").get(0).get("in").asText().equals("header") : "Parameter[0] in should is header";
        assert post.get("parameters").get(0).get("description").asText().equals("用户授权") : "Parameter[0] description should is 用户授权";
        assert post.get("parameters").get(0).get("required").asBoolean() : "Parameter[0] required should is true";

        assert post.get("parameters").get(1).get("name").asText().equals("openId") : "Parameter[1] name should is openId";
        assert post.get("parameters").get(1).get("in").asText().equals("query") : "Parameter[1] in should is query";
        assert post.get("parameters").get(1).get("description").asText().equals("微信openId") : "Parameter[1] description should is 微信openId";

        assert post.get("parameters").get(2).get("name").asText().equals("file") : "Parameter[2] name should is file";
        assert post.get("parameters").get(2).get("in").asText().equals("multipart/form-data") : "Parameter[2] in should is multipart/form-data";
        assert post.get("parameters").get(2).get("description").asText().equals("上传附件") : "Parameter [2] description should is 上传附件";
        assert post.get("parameters").get(2).get("required").asBoolean() == false : "Parameter[2] required should is false";

        assert post.get("parameters").get(3).get("name").asText().equals("appId") : "Parameter[3] name should is appId";
        assert post.get("parameters").get(3).get("in").asText().equals("path") : "Parameter[3] in should is path";
        assert post.get("parameters").get(3).get("required").asBoolean() : "Parameter[3] required should is false";

        assert post.get("parameters").get(4).get("name").asText().equals("applicationQueryPo") : "Parameter[4] name should is applicationQueryPo";
        assert post.get("parameters").get(4).get("in").asText().equals("body") : "Parameter[4] in should is body";
        assert post.get("parameters").get(4).get("required").asBoolean() == false : "Parameter[4] required should is false";


    }
}