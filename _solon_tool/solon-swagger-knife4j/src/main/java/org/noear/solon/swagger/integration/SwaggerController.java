package org.noear.solon.swagger.integration;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;


import org.noear.solon.annotation.Singleton;
import org.noear.solon.swagger.SwaggerConst;
import org.noear.solon.swagger.annotation.*;

import org.noear.solon.swagger.util.Base64Utils;
import org.noear.solon.swagger.util.KvMap;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.*;
import org.noear.solon.core.route.Routing;

/**
 * Swagger UI Controller
 *
 * @author: lbq
 * 联系方式: 526509994@qq.com
 * 创建日期: 2020/9/16
 */
@Singleton(false)
public class SwaggerController {

    /**
     * swagger tag
     */
    private List<KvMap> tagsList = new ArrayList<KvMap>();

    /**
     * swagger models
     */
    private Map<String, KvMap> definitionsMap = new LinkedHashMap<String, KvMap>();

    /**
     * swagger path
     */
    private List pathList = new ArrayList();

    /**
     * 通用返回类 名称
     */
    private String commonResName;

    /**
     * 通用返回类 返回值
     */
    private List commonResProperties;

    /**
     * 分组包配置的basePath
     */
    private String groupPackageBasePath;

    /**
     * 默认转发
     */
    @Mapping(value = "swagger",produces = "text/html; charset=utf-8")
    public void index(Context ctx) throws IOException {
        ctx.forward("/doc.html");
    }

    /**
     * 获取分组信息
     */
    @Mapping(value = "swagger-resources", produces = "application/json; charset=utf-8")
    public ModelAndView resources() {
        KvMap kv = new KvMap();
        kv.set("swagger_resources", SwaggerConst.CONFIG.get("swagger_resources"));
        kv.set("swaggerVersion", SwaggerConst.CONFIG.get("swaggerVersion"));

        return new ModelAndView("swagger-template/swagger-resources.shtm", kv);

        // renderJson(Engine.use("swagger").getTemplate("/swagger-resources.jf").renderToString(kv));
    }

    /**
     * 获取api接口解析JSON
     */
    @Mapping(value = "swagger/api", produces = "application/json; charset=utf-8")
    public ModelAndView api(Context ctx) throws IOException {
        if (!this.basicAuth(ctx)) {
            this.response401(ctx);
            return null;
        }

        // 解析通用返回
        KvMap commonResKv = this.parseSwaggerModel(SwaggerConst.COMMON_RES);
        this.commonResName = commonResKv.getStr("name");
        this.commonResProperties = (List) commonResKv.get("properties");

        // 解析JSON
        this.parseGroupPackage(ctx.param("group", ""));

        KvMap kv = new KvMap();
        kv.set("swagger", SwaggerConst.CONFIG);
        kv.set("host", this.getHost());

        kv.set("tags", this.tagsList);
        kv.set("paths", this.pathList);
        kv.set("definitions", this.toDefinitionList(this.definitionsMap));

        kv.set("groupPackageBasePath", this.groupPackageBasePath);

        return new ModelAndView("swagger-template/api-docs.shtm", kv);

        //renderJson(Engine.use("swagger").getTemplate("/api-docs.jf").renderToString(kv));
    }

    /**
     * 解析分组包
     *
     * @param groupPackage 分组包名
     */
    private void parseGroupPackage(String groupPackage) {
        // 解析分组包basePath
        this.parseGroupPackageBasePath(groupPackage);

        Map<Class<?>, List<Action>> classMap = this.getApiAction(groupPackage);
        classMap.keySet().forEach((Class<?> clazz) -> {
            List<Action> actions = classMap.get(clazz);

            // 解析controller
            this.parseController(clazz, actions);

        });
    }

    /**
     * 解析分组包basePath
     */
    private void parseGroupPackageBasePath(String groupPackage) {
        String swaggerResources = SwaggerConst.CONFIG.get("swagger_resources");
        String[] arrs = swaggerResources.split(",");
        for (String resources : arrs) {
            String[] items = resources.split("#");
            if (items.length > 2 && items[1].equals(groupPackage)) {
                String basePath = items[2];
                this.groupPackageBasePath = basePath;
                break;
            }
        }
    }

    /**
     * 获取全部Action
     */
    private Map<Class<?>, List<Action>> getApiAction(String basePackage) {
        Map<Class<?>, List<Action>> apiMap = new HashMap<>(16);

        Collection<Routing<Handler>> routingCollection = Solon.app().router().getAll(Endpoint.main);
        for(Routing<Handler> routing : routingCollection){
            if (routing.target() instanceof Action == false) {
                continue;
            }

            Action action = (Action) routing.target();
            Class<?> controller = action.controller().clz();

            if (!controller.getName().startsWith(basePackage)) {
                continue;
            }

            if (apiMap.containsKey(controller)) {
                if (action.method().isAnnotationPresent(ApiOperation.class)) {
                    List<Action> actions = apiMap.get(controller);
                    if (!actions.contains(action)) {
                        actions.add(action);
                        apiMap.put(controller, actions);
                    }
                }
            } else {
                if (controller.isAnnotationPresent(Api.class)) {
                    if (action.method().isAnnotationPresent(ApiOperation.class)) {
                        List<Action> actions = new ArrayList<>();
                        actions.add(action);
                        apiMap.put(controller, actions);
                    }
                }
            }
        }

        List<Class<?>> ctlList = new ArrayList<>(apiMap.keySet());
        ctlList.sort((clazz1, clazz2) -> clazz1.getAnnotation(Api.class).position() - clazz2.getAnnotation(Api.class).position());

        Map<Class<?>, List<Action>> result = new LinkedHashMap<>();
        ctlList.forEach(i -> {
            List<Action> actions = apiMap.get(i);
            actions.sort((action1, action2) -> action1.method().getAnnotation(ApiOperation.class).position() - action2.method().getAnnotation(ApiOperation.class).position());
            result.put(i, actions);
        });

        return result;
    }

    /**
     * 解析controller
     */
    private void parseController(Class<?> clazz, List<Action> actions) {
        // controller 信息
        Api api = clazz.getAnnotation(Api.class);
        boolean hidden = api.hidden();
        if (hidden) {
            return;
        }

        String controllerKey = getControllerKey(clazz);

        for (String tags : api.tags()) {
            KvMap tag = new KvMap();
            tag.set("name", tags);
            tag.set("controllerKey", controllerKey);//getControllerKey()
            tag.set("controllerName", clazz.getSimpleName());

            this.tagsList.add(tag);
        }

        // 解析action
        this.parseAction(actions);
    }

    /**
     * 解析action
     */
    private void parseAction(List<Action> actions) {
        for (Action action : actions) {
            Method method = action.method().getMethod();

            ApiOperation apiAction = method.getAnnotation(ApiOperation.class);

            if (apiAction.hidden()) {
                return;
            }

            String controllerKey = this.getControllerKey(action.controller().clz());
            String actionName = action.name();//action.getMethodName();

            Set<String> actionTags = new HashSet<>();
            actionTags.addAll(Arrays.asList(actions.get(0).controller().clz().getAnnotation(Api.class).tags()));
            actionTags.addAll(Arrays.asList(apiAction.tags()));
            actionTags.remove("");

            KvMap actionKv = new KvMap();
            actionKv.set("tags", actionTags)
                    .set("summary", apiAction.value())
                    .set("description", apiAction.notes())
                    .set("deprecated", method.isAnnotationPresent(Deprecated.class))
                    .set("noAuthorize", method.isAnnotationPresent(ApiNoAuthorize.class) || actions.get(0).controller().clz().isAnnotationPresent(ApiNoAuthorize.class))
                    .set("parameters", this.parseActionParameters(method))
                    .set("responses", this.parseActionResponse(controllerKey, actionName, method))
                    .set("methods", Utils.isBlank(apiAction.httpMethod()) ? ApiEnum.METHOD_GET : apiAction.httpMethod())
                    .set("consumes", Utils.isBlank(apiAction.consumes()) ? ApiEnum.CONSUMES_URLENCODED : apiAction.consumes())
                    .set("produces", Utils.isBlank(apiAction.produces()) ? ApiEnum.PRODUCES_DEFAULT : apiAction.produces())
                    .set("controllerKey", controllerKey)
                    .set("actionName", actionName);

            pathList.add(actionKv);
        }
    }

    /**
     * 解析action 参数文档
     */
    private List<KvMap> parseActionParameters(Method method) {
        // 获取参数注解信息
        List<ApiImplicitParam> params = new ArrayList<>();
        if (method.isAnnotationPresent(ApiImplicitParams.class)) {
            params.addAll(Arrays.asList(method.getAnnotation(ApiImplicitParams.class).value()));
        }
        if (method.isAnnotationPresent(ApiImplicitParams.class)) {
            ApiImplicitParam[] paramArray = method.getAnnotationsByType(ApiImplicitParam.class);
            params.addAll(Arrays.asList(paramArray));
        }

        // 构建参数列表(包含全局参数)
        List<KvMap> paramList = new ArrayList<>();

        params.forEach(param -> {
            KvMap kv = KvMap.by("name", param.name())
                    .set("description", param.value())
                    .set("required", param.required())
                    .set("format", param.format())
                    .set("defaultValue", param.defaultValue())
                    .set("allowMultiple", param.allowMultiple())
                    .set("schema", this.toParameterSchema(param))
                    .set("dataType", Utils.isBlank(param.dataType()) ? ApiEnum.STRING : param.dataType())
                    .set("paramType", Utils.isBlank(param.paramType()) ? ApiEnum.PARAM_TYPE_QUERY : param.paramType());

            paramList.add(kv);
        });
        return paramList;
    }

    /**
     * 解析action 返回文档
     */
    private List parseActionResponse(String controllerKey, String actionName, Method method) {
        List responseList = new ArrayList();

        SwaggerConst.HTTP_CODE.forEach((key, value) -> {
            if (key == 200) {
                responseList.add(KvMap.by("name", key).set("description", value)
                        .set("schema", this.parseResponse(controllerKey, actionName, method)));
            } else {
                responseList.add(KvMap.by("name", key).set("description", value));
            }

        });
        return responseList;
    }

    /**
     * 解析返回值
     */
    private String parseResponse(String controllerKey, String actionName, Method method) {
        // swagger model 引用
        String swaggerModelName;

        List<ApiResProperty> responses = new ArrayList<>();
        if (method.isAnnotationPresent(ApiRes.class)) {
            responses.addAll(Arrays.asList(method.getAnnotation(ApiRes.class).value()));
        }
        if (method.isAnnotationPresent(ApiRes.class)) {
            ApiResProperty[] paramArray = method.getAnnotationsByType(ApiResProperty.class);
            responses.addAll(Arrays.asList(paramArray));
        }

        // 2.9.1 实验性质 自定义返回值
        Class<?> apiResClz = method.getReturnType();
        if (apiResClz != Void.class) {
            if(apiResClz.isAnnotationPresent(ApiModel.class)){
                KvMap commonResKv = this.parseSwaggerModel(apiResClz);
                swaggerModelName = commonResKv.getStr("name");
                return swaggerModelName;
            }
        }

        if (responses.size() == 0) {
            swaggerModelName = this.commonResName;
        } else {
            // 将参数放入commonRes中,作为新的swagger Model引用(knife4j 约定)
            KvMap swaggerModelKv = this.parseSwaggerModel(controllerKey, actionName, responses);
            swaggerModelName = swaggerModelKv.getStr("name");

            // 在data中返回参数
            if (SwaggerConst.RESPONSE_IN_DATA) {
                swaggerModelName = this.toResponseInData(swaggerModelName);
            }
        }

        return swaggerModelName;
    }

    /**
     * 在data中返回
     */
    private String toResponseInData(String swaggerModelName) {
        KvMap fieldKv = new KvMap();
        List propertiesList = new ArrayList();
        propertiesList.addAll(this.commonResProperties);


        fieldKv.set("key", "data");
        fieldKv.set("name", swaggerModelName);
        fieldKv.set("description", "返回值");
        fieldKv.set("type", ApiEnum.RES_OBJECT);

        propertiesList.add(fieldKv);

        swaggerModelName = this.commonResName + "«" + swaggerModelName + "»";

        KvMap kv = new KvMap();
        kv.set("properties", propertiesList);
        kv.set("name", swaggerModelName);

        this.definitionsMap.put(swaggerModelName, kv);
        return swaggerModelName;
    }


    /**
     * swagger models
     */
    private List toDefinitionList(Map<String, KvMap> map) {
        List list = new ArrayList();
        map.forEach((key, value) -> list.add(value));
        return list;
    }


    /**
     * 将class解析为swagger model
     */
    private KvMap parseSwaggerModel(Class<?> clazz) {
        String modelName = clazz.getSimpleName();

        // 已存在,不重复解析
        KvMap modelKv = this.definitionsMap.get(modelName);
        if (null != modelKv) {
            return modelKv;
        }

        ApiModel apiModel = clazz.getAnnotation(ApiModel.class);
        String title = apiModel.description();

        List fieldList = new ArrayList();

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            ApiModelProperty apiField = field.getAnnotation(ApiModelProperty.class);
            if(apiField == null){
                continue;
            }

            // List<Class> 类型
            if (field.getType() == List.class) {
                // 如果是List类型，得到其Generic的类型
                Type genericType = field.getGenericType();
                if (genericType == null) {
                    continue;
                }

                // 如果是泛型参数的类型
                if (genericType instanceof ParameterizedType) {
                    ParameterizedType pt = (ParameterizedType) genericType;
                    //得到泛型里的class类型对象
                    Class<?> genericClazz = (Class<?>) pt.getActualTypeArguments()[0];

                    KvMap swaggerModel = this.parseSwaggerModel(genericClazz);

                    KvMap fieldKv = new KvMap();

                    fieldKv.set("key", field.getName());
                    fieldKv.set("name", swaggerModel.getStr("name"));
                    fieldKv.set("description", apiField.value());
                    fieldKv.set("type", ApiEnum.RES_OBJECT);
                    fieldKv.set("allowMultiple", true);

                    fieldList.add(fieldKv);
                }
                continue;
            }

            Class<?> typeClazz = field.getType();
            if (typeClazz.isAnnotationPresent(ApiModel.class)) {

                KvMap swaggerModel = this.parseSwaggerModel(typeClazz);

                KvMap fieldKv = new KvMap();

                fieldKv.set("key", field.getName());
                fieldKv.set("name", swaggerModel.getStr("name"));
                fieldKv.set("description", apiField.value());
                fieldKv.set("type", ApiEnum.RES_OBJECT);

                fieldList.add(fieldKv);
            } else {
                KvMap fieldKv = new KvMap();
                fieldKv.set("name", field.getName());
                fieldKv.set("description", apiField.value());
                fieldKv.set("type", Utils.isBlank(apiField.dataType()) ? field.getType().getSimpleName().toLowerCase() : apiField.dataType());
                fieldKv.set("example", apiField.example());

                fieldList.add(fieldKv);
            }
        }

        KvMap kv = new KvMap();
        kv.set("properties", fieldList);
        kv.set("name", modelName);
        kv.set("title", title);

        this.definitionsMap.put(modelName, kv);

        return kv;
    }

    /**
     * 将action response解析为swagger model
     */
    private KvMap parseSwaggerModel(String controllerKey, String actionName, List<ApiResProperty> responses) {
        String modelName = controllerKey + "_" + actionName;

        List propertiesList = new ArrayList();

        // 不在Data中返回参数
        if (!SwaggerConst.RESPONSE_IN_DATA) {
            propertiesList.addAll(this.commonResProperties);
        }

        responses.forEach(apiResponse -> {

            if (apiResponse.dataTypeClass() != Void.class) {
                KvMap swaggerModel = this.parseSwaggerModel(apiResponse.dataTypeClass());

                KvMap fieldKv = new KvMap();

                fieldKv.set("key", apiResponse.name());
                fieldKv.set("name", swaggerModel.getStr("name"));
                fieldKv.set("description", apiResponse.value());
                fieldKv.set("type", ApiEnum.RES_OBJECT);
                fieldKv.set("allowMultiple", apiResponse.allowMultiple());

                propertiesList.add(fieldKv);
            } else {
                KvMap fieldKv = new KvMap();

                fieldKv.set("name", apiResponse.name());
                fieldKv.set("description", apiResponse.value());
                fieldKv.set("type", Utils.isBlank(apiResponse.dataType()) ? ApiEnum.RES_STRING : apiResponse.dataType());
                fieldKv.set("format", Utils.isBlank(apiResponse.format()) ? ApiEnum.FORMAT_STRING : apiResponse.format());
                fieldKv.set("example", apiResponse.example());
                fieldKv.set("exampleEnum", apiResponse.exampleEnum());
                fieldKv.set("allowMultiple", apiResponse.allowMultiple());

                propertiesList.add(fieldKv);
            }
        });

        KvMap kv = new KvMap();
        kv.set("properties", propertiesList);
        kv.set("name", modelName);

        this.definitionsMap.put(modelName, kv);

        return kv;
    }

    /**
     * 解析对象参数
     */
    private String toParameterSchema(ApiImplicitParam apiParam) {
        if (apiParam.dataTypeClass() != Void.class) {
            KvMap swaggerModel = this.parseSwaggerModel(apiParam.dataTypeClass());

            return swaggerModel.getStr("name");
        }
        return null;
    }

    /**
     * 获取host配置
     */
    private String getHost() {
        String host = SwaggerConst.CONFIG.get("host");
        if (Utils.isBlank(host)) {
            host = "localhost";
            if (Solon.cfg().serverPort() != 80) {
                host += ":" + Solon.cfg().serverPort();
            }
        }
        return host;
    }

    /**
     * 避免ControllerKey 设置前缀后,与swagger basePath 设置导致前端生成2次
     */
    private String getControllerKey(Class<?> controllerClz) {
        Mapping mapping = controllerClz.getAnnotation(Mapping.class);
        if (mapping == null) {
            return "";
        }

        String path = Utils.annoAlias(mapping.value(), mapping.path());
        if (path.startsWith("/")) {
            return path.substring(1);
        } else {
            return path;
        }
    }

    /**
     * WWW-Authenticate 简单认证
     */
    private boolean basicAuth(Context ctx) throws IOException {
        String basicAuth = SwaggerConst.CONFIG.get("basicAuth");
        if (Utils.isBlank(basicAuth)) {
            // 未启用简单认证
            return true;
        }

        String authorization = ctx.header("Authorization");
        if (Utils.isBlank(authorization)) {
            // 请求头无认证信息
            return false;
        }

        Map<String, String> baseAuthMap = new HashMap<>(16);

        String[] baseAuthArr = basicAuth.split(",");
        for (String auth : baseAuthArr) {
            baseAuthMap.put(auth.split("#")[0], auth.split("#")[1]);
        }

        String nameAndPwd = Base64Utils.decodeToStr(authorization.substring(6));
        String[] upArr = nameAndPwd.split(":");

        if (upArr.length != 2) {
            return false;
        }
        String iptName = upArr[0];
        String iptPwd = upArr[1];

        return iptPwd.equals(baseAuthMap.get(iptName));
    }

    private void response401(Context ctx) throws IOException {
        ctx.status(401);
        ctx.headerSet("WWW-Authenticate", "Basic realm=\"请输入Swagger文档访问账号密码\"");
        ctx.output("无权限访问");
    }
}
