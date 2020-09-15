package demo;

import com.power.common.util.DateTimeUtil;
import com.power.common.util.JsonFormatUtil;
import com.power.doc.builder.*;
import com.power.doc.constants.DocGlobalConstants;
import com.power.doc.model.*;
import com.power.doc.model.rpc.RpcApiDependency;
import org.junit.Test;
import org.noear.snack.ONode;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Description:
 * ApiDoc测试
 *
 * @author yu 2018/06/11.
 */
public class ApiDocTest {

    @Test
    public void test(){
        Path path = Paths.get("test");
        System.out.println(path.toAbsolutePath());
    }

    /**
     * 包括设置请求头，缺失注释的字段批量在文档生成期使用定义好的注释
     */
    @Deprecated
    @Test
    public void testBuilderControllersApi() {
        ApiConfig config = new ApiConfig();
        config.setServerUrl("http://localhost:8080");
        //true会严格要求注释，推荐设置true
        config.setStrict(false);
        //true会将文档合并导出到一个markdown
        config.setAllInOne(true);
        config.setShowAuthor(true);
        //生成html时加密文档名不暴露controller的名称
        config.setMd5EncryptedHtmlName(true);
        config.setRecursionLimit(3);

        config.setOutPath("test");

        config.setCoverOld(true);

        //是否显示接口作者 默认true
        config.setShowAuthor(true);

        //自动将驼峰入参字段在文档中转为下划线格式,//@since 1.8.7 版本开始
        config.setRequestFieldToUnderline(false);

        //自动将驼峰入参字段在文档中转为下划线格式,//@since 1.8.7 版本开始
        config.setResponseFieldToUnderline(false);

        config.setInlineEnum(true);

//        config.setProjectName("Smart-doc测试样例");


        //指定文档输出路径
        //@since 1.7 版本开始，选择生成静态html doc文档可使用该路径：DocGlobalConstants.HTML_DOC_OUT_PATH;
        config.setOutPath(DocGlobalConstants.HTML_DOC_OUT_PATH);
//        config.setOutPath("E:\\chen\\test-smart");
        // @since 1.2,如果不配置该选项，则默认匹配全部的controller,
        // 如果需要配置有多个controller可以使用逗号隔开
//        config.setPackageFilters("com.power.doc.controller.app");

        config.setRequestHeaders(
                ApiReqHeader.header().setName("token").setRequired(true).setType("string").setDesc("token"),
                ApiReqHeader.header().setName("partnerId").setType("string").setRequired(true).setDesc("合作方账号")
        );
        //Configure your own constant class, smart-doc automatically replaces with a specific value when parsing to a constant
//        config.setApiConstants(
//                ApiConstant.builder().setConstantsClass(RequestParamConstant.class),
//                ApiConstant.builder().setConstantsClass(RequestValueConstant.class),
//                ApiConstant.builder().setConstantsClass(ApiVersion.class)
//        );

        //添加数据字典
//        config.setDataDictionaries(
//                ApiDataDictionary.dict().setTitle("订单状态").setEnumClass(OrderEnum.class)
//                        .setCodeField("code") //字典码值字段名
//                        .setDescField("desc"), //字段码
//                ApiDataDictionary.dict().setTitle("订单状态1").setEnumClass(OrderEnum.class)
//                        .setCodeField("code").setDescField("desc"),
//                ApiDataDictionary.dict().setTitle("性别字典").setEnumClass(GenderEnum.class)
//                        .setCodeField("code").setDescField("desc")
//        );
        //1.7.9 添加错误码处理，用于替代遍历代码
//        config.setErrorCodeDictionaries(
//                ApiErrorCodeDictionary.dict().setEnumClass(ErrorCodeEnum.class)
//                        .setCodeField("code") //错误码值字段名
//                        .setDescField("desc")//错误码描述
//        );
        //1.8.5，使用自定义对象替换指定对象做文档渲染，必须使用全类名
        //泛型书写例子com.power.doc.model.LoginDto<com.power.doc.User>
        config.setApiObjectReplacements(
                ApiObjectReplacement.builder().setClassName("org.springframework.data.domain.Pageable")
                        .setReplacementClassName("com.power.doc.model.LoginDto")
        );
        // 添加dubbo api模块的依赖，方便smart-doc将依赖写入文档中，方便他人集成。
        config.setRpcApiDependencies(
                RpcApiDependency.builder().setGroupId("com.demo").setArtifactId("SpringBoot2-Dubbo-Api").setVersion("1.0.0"),
                RpcApiDependency.builder().setGroupId("com.demo").setArtifactId("SpringBoot2-Dubbo-Api").setVersion("1.0.0")
        );

        String json = ONode.stringify(config);
        System.out.println(JsonFormatUtil.formatJson(json));
//        ApiConfig config1 = JSON.parseObject(json,ApiConfig.class);
//        System.out.println(JSON.toJSONString(config1));

        long start = System.currentTimeMillis();
        //获取接口数据后自行处理
        HtmlApiDocBuilder.buildApiDoc(config);
//        AdocDocBuilder.builderControllersApi(config);
//        ApiDocBuilder.builderControllersApi(config);
//        PostmanJsonBuilder.buildPostmanApi(config);
//        ApiDataBuilder.getApiData(config);
       // ApiAllData apiAllData = ApiDataBuilder.getApiData(config);
        //List<ApiDoc> docList = ApiDocBuilder.listOfApiData(config);
        long end = System.currentTimeMillis();
        DateTimeUtil.printRunTime(end, start);
    }
}
