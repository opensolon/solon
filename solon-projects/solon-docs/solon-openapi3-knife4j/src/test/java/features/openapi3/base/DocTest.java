package features.openapi3.base;

import demo.openapi3.base.App;
import org.junit.jupiter.api.Test;
import org.noear.solon.docs.DocDocket;
import org.noear.solon.docs.openapi3.OpenApi3Utils;
import org.noear.solon.test.SolonTest;

/**
 * @author noear 2025/12/17 created
 */
@SolonTest(App.class)
public class DocTest {
    @Test
    public void test() throws Exception {
        DocDocket docDocket = new DocDocket()
                .groupName("app端接口")
                .apis("demo.openapi3.base");

        String json = OpenApi3Utils.getSwaggerJson(docDocket);

        System.out.println(json);

        assert "{\"openapi\":\"3.0.1\",\"info\":{},\"servers\":[{\"url\":\"localhost:8080\"}],\"tags\":[{\"name\":\"用户信息表 控制器\",\"description\":\"test/user (UserController)\"}],\"paths\":{\"/test/user/page\":{\"get\":{\"tags\":[\"用户信息表 控制器\"],\"summary\":\"page\",\"description\":\"\",\"operationId\":\"get__test_user_page\",\"parameters\":[],\"responses\":{\"200\":{\"description\":\"Success\"}},\"deprecated\":false}},\"/test/user\":{\"put\":{\"tags\":[\"用户信息表 控制器\"],\"summary\":\"根据Id更新用户信息\",\"description\":\"\",\"operationId\":\"put__test_user\",\"parameters\":[{\"name\":\"userBo\",\"in\":\"query\",\"required\":false,\"schema\":{\"$ref\":\"#/components/schemas/UserBo\",\"exampleSetFlag\":false}}],\"responses\":{\"200\":{\"description\":\"Success\"}},\"deprecated\":false},\"post\":{\"tags\":[\"用户信息表 控制器\"],\"summary\":\"新增用户信息\",\"description\":\"\",\"operationId\":\"post__test_user\",\"parameters\":[{\"name\":\"userBo\",\"in\":\"query\",\"required\":false,\"schema\":{\"$ref\":\"#/components/schemas/UserBo\",\"exampleSetFlag\":false}}],\"responses\":{\"200\":{\"description\":\"Success\"}},\"deprecated\":false}},\"/test/user/list\":{\"get\":{\"tags\":[\"用户信息表 控制器\"],\"summary\":\"查询用户列表\",\"description\":\"\",\"operationId\":\"get__test_user_list\",\"parameters\":[{\"name\":\"userBo\",\"in\":\"query\",\"required\":false,\"schema\":{\"$ref\":\"#/components/schemas/UserBo\",\"exampleSetFlag\":false}}],\"responses\":{\"200\":{\"description\":\"Success\"}},\"deprecated\":false}},\"/test/user/{id}\":{\"get\":{\"tags\":[\"用户信息表 控制器\"],\"summary\":\"根据Id查询用户信息\",\"description\":\"\",\"operationId\":\"get__test_user_{id}\",\"parameters\":[{\"name\":\"id\",\"in\":\"query\",\"required\":false,\"schema\":{\"type\":\"string\",\"exampleSetFlag\":false,\"types\":[\"string\"]}}],\"responses\":{\"200\":{\"description\":\"Success\"}},\"deprecated\":false}},\"/test/user/info/{id}\":{\"get\":{\"tags\":[\"用户信息表 控制器\"],\"summary\":\"根据Id查询用户信息2\",\"description\":\"\",\"operationId\":\"get__test_user_info_{id}\",\"parameters\":[{\"name\":\"id\",\"in\":\"query\",\"required\":false,\"schema\":{\"type\":\"string\",\"exampleSetFlag\":false,\"types\":[\"string\"]}}],\"responses\":{\"200\":{\"description\":\"Success\"}},\"deprecated\":false}},\"/test/user/{ids}\":{\"delete\":{\"tags\":[\"用户信息表 控制器\"],\"summary\":\"删除用户信息\",\"description\":\"\",\"operationId\":\"delete__test_user_{ids}\",\"parameters\":[{\"name\":\"ids\",\"in\":\"query\",\"required\":false,\"schema\":{\"type\":\"string\",\"exampleSetFlag\":false,\"types\":[\"string\"]}}],\"responses\":{\"200\":{\"description\":\"Success\"}},\"deprecated\":false}}},\"components\":{\"schemas\":{\"UserBo\":{\"title\":\"UserBo\",\"type\":\"object\",\"properties\":{\"userId\":{\"type\":\"long\",\"exampleSetFlag\":false,\"types\":[\"string\"]},\"tenantId\":{\"type\":\"string\",\"exampleSetFlag\":false,\"types\":[\"string\"]},\"deptId\":{\"type\":\"long\",\"exampleSetFlag\":false,\"types\":[\"string\"]},\"userName\":{\"type\":\"string\",\"exampleSetFlag\":false,\"types\":[\"string\"]},\"nickName\":{\"type\":\"string\",\"exampleSetFlag\":false,\"types\":[\"string\"]},\"certificateNumber\":{\"type\":\"string\",\"exampleSetFlag\":false,\"types\":[\"string\"]},\"userType\":{\"type\":\"string\",\"exampleSetFlag\":false,\"types\":[\"string\"]},\"email\":{\"type\":\"string\",\"exampleSetFlag\":false,\"types\":[\"string\"]},\"phonenumber\":{\"type\":\"string\",\"exampleSetFlag\":false,\"types\":[\"string\"]},\"sex\":{\"type\":\"string\",\"exampleSetFlag\":false,\"types\":[\"string\"]},\"avatar\":{\"type\":\"long\",\"exampleSetFlag\":false,\"types\":[\"string\"]},\"password\":{\"type\":\"string\",\"exampleSetFlag\":false,\"types\":[\"string\"]},\"status\":{\"type\":\"string\",\"exampleSetFlag\":false,\"types\":[\"string\"]},\"delFlag\":{\"type\":\"string\",\"exampleSetFlag\":false,\"types\":[\"string\"]},\"loginIp\":{\"type\":\"string\",\"exampleSetFlag\":false,\"types\":[\"string\"]},\"loginDate\":{\"type\":\"localdatetime\",\"exampleSetFlag\":false,\"types\":[\"string\"]},\"remark\":{\"type\":\"string\",\"exampleSetFlag\":false,\"types\":[\"string\"]}},\"exampleSetFlag\":false}}}}"
                .length() == json.length();
    }
}
