package org.noear.solon.docs.openapi2;

import io.swagger.annotations.*;
import io.swagger.models.*;
import io.swagger.models.Info;
import io.swagger.models.Tag;
import io.swagger.models.parameters.*;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.properties.*;
import io.swagger.models.refs.RefFormat;

import io.swagger.solon.annotation.ApiNoAuthorize;
import io.swagger.solon.annotation.ApiRes;
import io.swagger.solon.annotation.ApiResProperty;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.annotation.Get;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Action;
import org.noear.solon.core.handle.Endpoint;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.core.route.Routing;
import org.noear.solon.core.util.PathUtil;

import org.noear.solon.docs.ApiEnum;
import org.noear.solon.docs.DocDocket;

import java.lang.reflect.*;
import java.text.Collator;
import java.util.*;

/**
 * openapi v2 json builder
 *
 * @author noear
 * @since 2.3
 */
public class Swagger2Builder {
    private final DocDocket docket;

    /**
     * 公共返回模型
     */
    private ModelImpl globalResultModel;

    Swagger swagger = new Swagger();

    public Swagger2Builder(DocDocket docket) {
        this.docket = docket;
    }

    public Swagger build() {
        // 解析通用返回
        if (docket.globalResult() != null) {
            this.globalResultModel = (ModelImpl) this.parseSwaggerModel(docket.globalResult());
        }

        // 解析JSON
        this.parseGroupPackage();


        swagger.setSwagger(docket.version());
        swagger.info(new Info()
                .title(docket.info().title())
                .description(docket.info().description())
                .termsOfService(docket.info().termsOfService())
                .version(docket.info().version())
                .license(docket.info().license())
                .contact(docket.info().contact()));

        swagger.host(this.getHost(docket));
        swagger.basePath(docket.basePath());
        swagger.schemes(docket.schemes());
        swagger.externalDocs(docket.externalDocs());
        swagger.vendorExtensions(docket.vendorExtensions());
        //new ExternalDocs().url("https://swagger.io/").description("Find out more about Swagger")


        swagger.getTags().sort((t1, t2) -> {
            String name1 = t1.getDescription();
            String name2 = t2.getDescription();

            return Collator.getInstance(Locale.UK).compare(name1, name2);
        });

        List<String> definitionKeys = new ArrayList<>(swagger.getDefinitions().keySet());
        Map<String, Model> definitionMap = new LinkedHashMap<>();
        definitionKeys.sort((name1, name2) -> Collator.getInstance(Locale.UK).compare(name1, name2));
        for (String name : definitionKeys) {
            definitionMap.put(name, swagger.getDefinitions().get(name));
        }
        swagger.setDefinitions(definitionMap);

        swagger.setSecurityDefinitions(docket.securityDefinitions());

        return swagger;
    }

    /**
     * 解析分组包
     */
    private void parseGroupPackage() {
        Map<Class<?>, List<ActionHolder>> classMap = this.getApiAction();
        classMap.keySet().forEach((Class<?> clazz) -> {
            List<ActionHolder> actionHolders = classMap.get(clazz);

            // 解析controller
            this.parseController(clazz, actionHolders);

        });
    }


    /**
     * 获取全部Action
     */
    private Map<Class<?>, List<ActionHolder>> getApiAction() {
        Map<Class<?>, List<ActionHolder>> apiMap = new HashMap<>(16);

        Collection<Routing<Handler>> routingCollection = Solon.app().router().getAll(Endpoint.main);
        for (Routing<Handler> routing : routingCollection) {
            if (routing.target() instanceof Action == false) {
                continue;
            }

            Action action = (Action) routing.target();
            Class<?> controller = action.controller().clz();

            boolean matched = docket.apis().stream().anyMatch(res -> res.test(action));
            if (matched == false) {
                continue;
            }

            ActionHolder actionHolder = new ActionHolder(routing, action);

            if (apiMap.containsKey(controller)) {
                if (action.method().isAnnotationPresent(ApiOperation.class)) {
                    List<ActionHolder> actionHolders = apiMap.get(controller);
                    if (!actionHolders.contains(actionHolder)) {
                        actionHolders.add(actionHolder);
                        apiMap.put(controller, actionHolders);
                    }
                }
            } else {
                if (controller.isAnnotationPresent(Api.class)) {
                    if (action.method().isAnnotationPresent(ApiOperation.class)) {
                        List<ActionHolder> actionHolders = new ArrayList<>();
                        actionHolders.add(actionHolder);
                        apiMap.put(controller, actionHolders);
                    }
                }
            }
        }

        List<Class<?>> ctlList = new ArrayList<>(apiMap.keySet());
        ctlList.sort(Comparator.comparingInt(clazz -> clazz.getAnnotation(Api.class).position()));

        Map<Class<?>, List<ActionHolder>> result = new LinkedHashMap<>();
        ctlList.forEach(i -> {
            List<ActionHolder> actionHolders = apiMap.get(i);
            actionHolders.sort(Comparator.comparingInt(ah -> ah.getAnnotation(ApiOperation.class).position()));
            result.put(i, actionHolders);
        });

        return result;
    }

    /**
     * 解析controller
     */
    private void parseController(Class<?> clazz, List<ActionHolder> actionHolders) {
        // controller 信息
        Api api = clazz.getAnnotation(Api.class);
        boolean hidden = api.hidden();
        if (hidden) {
            return;
        }

        String controllerKey = getControllerKey(clazz);

        for (String tags : api.tags()) {
            Tag tag = new Tag();
            tag.setName(tags);
            tag.setDescription(controllerKey + " (" + clazz.getSimpleName() + ")");

            swagger.addTag(tag);
        }


        // 解析action
        this.parseAction(actionHolders);
    }

    /**
     * 解析action
     */
    private void parseAction(List<ActionHolder> actionHolders) {
        for (ActionHolder actionHolder : actionHolders) {

            ApiOperation apiAction = actionHolder.getAnnotation(ApiOperation.class);

            if (apiAction.hidden()) {
                return;
            }

            String controllerKey = this.getControllerKey(actionHolder.controllerClz());
            String actionName = actionHolder.action().name();//action.getMethodName();
            Method actionMethod = actionHolder.action().method().getMethod();

            Set<String> actionTags = new HashSet<>();
            actionTags.addAll(Arrays.asList(actionHolder.controllerClz().getAnnotation(Api.class).tags()));
            actionTags.addAll(Arrays.asList(apiAction.tags()));
            actionTags.remove("");

            Path path = new Path();
            String pathKey = PathUtil.mergePath(controllerKey, actionName);
            Operation operation = new Operation();

            operation.setTags(new ArrayList<>(actionTags));
            operation.setSummary(apiAction.value());
            operation.setDescription(apiAction.notes());
            operation.setDeprecated(actionHolder.isAnnotationPresent(Deprecated.class));

            if ((actionHolder.isAnnotationPresent(ApiNoAuthorize.class) ||
                    actionHolder.controllerClz().isAnnotationPresent(ApiNoAuthorize.class)) == false) {
                for (String securityName : docket.securityDefinitions().keySet()) {
                    operation.security(new SecurityRequirement(securityName).scope("global"));
                }
            }


            String operationMethod = getHttpMethod(actionHolder, apiAction);


            operation.setParameters(this.parseActionParameters(actionMethod));
            operation.setResponses(this.parseActionResponse(controllerKey, actionName, actionMethod));
            operation.setVendorExtension("controllerKey", controllerKey);
            operation.setVendorExtension("actionName", actionName);

            if (Utils.isBlank(apiAction.consumes())) {
                if (operationMethod.equals(ApiEnum.METHOD_GET)) {
                    operation.consumes(""); //如果是 get ，则没有 content-type
                } else {
                    operation.consumes(ApiEnum.CONSUMES_URLENCODED);
                }
            } else {
                operation.consumes(apiAction.consumes());
            }

            operation.produces(Utils.isBlank(apiAction.produces()) ? ApiEnum.PRODUCES_DEFAULT : apiAction.produces());


            operation.setOperationId(operationMethod + "_" + pathKey.replace("/", "_"));

            path.set(operationMethod, operation);

            swagger.path(pathKey, path);
        }
    }

    /**
     * 解析action 参数文档
     */
    private List<Parameter> parseActionParameters(Method method) {
        // 获取参数注解信息
        List<ApiImplicitParam> apiParams = new ArrayList<>();
        if (method.isAnnotationPresent(ApiImplicitParams.class)) {
            apiParams.addAll(Arrays.asList(method.getAnnotation(ApiImplicitParams.class).value()));
        }

        if (method.isAnnotationPresent(ApiImplicitParams.class)) {
            ApiImplicitParam[] paramArray = method.getAnnotationsByType(ApiImplicitParam.class);
            apiParams.addAll(Arrays.asList(paramArray));
        }

        // 构建参数列表(包含全局参数)
        List<Parameter> paramList = new ArrayList<>();

        for (ApiImplicitParam apiParam : apiParams) {
            String paramSchema = this.toParameterSchema(apiParam);
            String dataType = Utils.isBlank(apiParam.dataType()) ? ApiEnum.STRING : apiParam.dataType();

            Parameter parameter;

            if (apiParam.allowMultiple() && Utils.isNotEmpty(paramSchema)) {
                //array model
                BodyParameter modelParameter = new BodyParameter();
                modelParameter.setSchema(new ArrayModel().items(new RefProperty(paramSchema)));

                parameter = modelParameter;
            } else if (apiParam.allowMultiple() && "file".equals(dataType)) {
                //array file
                FormParameter formParameter = new FormParameter();
                formParameter.type("array");
                formParameter.items(new FileProperty());
                formParameter.collectionFormat("multi");

                parameter = formParameter;
            } else if (Utils.isNotEmpty(paramSchema)) {
                //model
                BodyParameter modelParameter = new BodyParameter();
                modelParameter.setSchema(new RefModel(paramSchema));

                parameter = modelParameter;
            } else {
                if (method.getAnnotation(Get.class) != null) {
                    QueryParameter formParameter = new QueryParameter();
                    formParameter.setFormat(apiParam.format());
                    formParameter.setType(dataType);
                    formParameter.setDefaultValue(apiParam.defaultValue());

                    parameter = formParameter;
                } else {
                    FormParameter formParameter = new FormParameter();
                    formParameter.setFormat(apiParam.format());
                    formParameter.setType(dataType);
                    formParameter.setDefaultValue(apiParam.defaultValue());

                    parameter = formParameter;
                }
            }

            parameter.setName(apiParam.name());
            parameter.setDescription(apiParam.value());
            parameter.setRequired(apiParam.required());
            parameter.setReadOnly(parameter.isReadOnly());
            parameter.setIn(Utils.isBlank(apiParam.paramType()) ? ApiEnum.PARAM_TYPE_QUERY : apiParam.paramType());

            paramList.add(parameter);
        }

        return paramList;
    }

    /**
     * 解析action 返回文档
     */
    private Map<String, Response> parseActionResponse(String controllerKey, String actionName, Method method) {
        Map<String, Response> responseMap = new LinkedHashMap<>();

        docket.globalResponseCodes().forEach((key, value) -> {
            Response response = new Response();
            response.description(value);

            if (key == 200) {
                String schema = this.parseResponse(controllerKey, actionName, method);
                if (schema != null) {
                    response.setResponseSchema(new RefModel(schema));
                }
            }

            responseMap.put(String.valueOf(key), response);

        });

        return responseMap;
    }

    /**
     * 解析返回值
     */
    private String parseResponse(String controllerKey, String actionName, Method method) {
        // swagger model 引用
        String swaggerModelName = null;

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
            if (apiResClz.isAnnotationPresent(ApiModel.class)) {
                ModelImpl commonResKv = (ModelImpl) this.parseSwaggerModel(apiResClz);
                swaggerModelName = commonResKv.getName();
                return swaggerModelName;
            }
        }


        if (responses.size() == 0) {
            if (globalResultModel != null) {
                swaggerModelName = globalResultModel.getName();
            }
        } else {
            // 将参数放入commonRes中,作为新的swagger Model引用(knife4j 约定)
            ModelImpl swaggerModelKv = (ModelImpl) this.parseSwaggerModel(controllerKey, actionName, responses);
            swaggerModelName = swaggerModelKv.getName();

            // 在data中返回参数
            if (docket.globalResponseInData()) {
                swaggerModelName = this.toResponseInData(swaggerModelName);
            }
        }

        return swaggerModelName;
    }

    /**
     * 在data中返回
     */
    private String toResponseInData(String swaggerModelName) {
        if(globalResultModel != null) {
            Map<String, Property> propertyMap = new LinkedHashMap<>();

            propertyMap.putAll(this.globalResultModel.getProperties());

            RefProperty property = new RefProperty(swaggerModelName, RefFormat.INTERNAL);
            property.setDescription("返回值");

            propertyMap.put("data", property);

            swaggerModelName = this.globalResultModel.getName() + "«" + swaggerModelName + "»";

            Model model = new ModelImpl();
            model.setTitle(swaggerModelName);
            model.setProperties(propertyMap);

            swagger.addDefinition(swaggerModelName, model);
        }

        return swaggerModelName;
    }


    /**
     * 将class解析为swagger model
     */
    private Model parseSwaggerModel(Class<?> clazz) {
        String modelName = clazz.getSimpleName();

        // 已存在,不重复解析
        if (swagger.getDefinitions() != null) {
            Model model = swagger.getDefinitions().get(modelName);

            if (null != model) {
                return model;
            }
        }

        ApiModel apiModel = clazz.getAnnotation(ApiModel.class);
        String title;
        if (apiModel != null) {
            title = apiModel.description();
        } else {
            title = modelName;
        }

        Map<String, Property> fieldList = new LinkedHashMap<>();

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if(Modifier.isStatic(field.getModifiers())){
                //静态的跳过
                continue;
            }

            ApiModelProperty apiField = field.getAnnotation(ApiModelProperty.class);


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

                    ModelImpl swaggerModel = (ModelImpl) this.parseSwaggerModel(genericClazz);


                    ObjectProperty fieldKv = new ObjectProperty();
                    fieldKv.setName(swaggerModel.getName());
                    if(apiField != null){
                        fieldKv.setDescription(apiField.value());
                    }
                    fieldKv.setType(ApiEnum.RES_OBJECT);

                    fieldList.put(field.getName(), fieldKv);
                }
                continue;
            }

            Class<?> typeClazz = field.getType();
            if (typeClazz.isAnnotationPresent(ApiModel.class)) {
                ModelImpl swaggerModel = (ModelImpl) this.parseSwaggerModel(typeClazz);

                ObjectProperty fieldKv = new ObjectProperty();
                fieldKv.setName(swaggerModel.getName());
                if(apiField != null){
                    fieldKv.setDescription(apiField.value());
                }
                fieldKv.setType(ApiEnum.RES_OBJECT);

                fieldList.put(field.getName(), fieldKv);
            } else {
                ObjectProperty fieldKv = new ObjectProperty();
                fieldKv.setName(field.getName());

                if(apiField != null) {
                    fieldKv.setDescription(apiField.value());
                    fieldKv.setType(Utils.isBlank(apiField.dataType()) ? field.getType().getSimpleName().toLowerCase() : apiField.dataType());
                    fieldKv.setExample(apiField.example());
                }else{
                    fieldKv.setType(field.getType().getSimpleName().toLowerCase());
                }

                fieldList.put(field.getName(), fieldKv);
            }
        }

        ModelImpl model = new ModelImpl();
        model.setProperties(fieldList);
        model.setName(modelName);
        model.setTitle(title);
        model.setType(ApiEnum.RES_OBJECT);

        swagger.addDefinition(modelName, model);

        return model;
    }

    /**
     * 将action response解析为swagger model
     */
    private Model parseSwaggerModel(String controllerKey, String actionName, List<ApiResProperty> responses) {
        String modelName = controllerKey + "_" + actionName;

        Map<String, Property> propertiesList = new LinkedHashMap<>();

        //todo: 不在Data中返回参数
        if (!docket.globalResponseInData()) {
            if (globalResultModel != null) {
                propertiesList.putAll(this.globalResultModel.getProperties());
            }
        }

        for (ApiResProperty apiResponse : responses) {

            if (apiResponse.dataTypeClass() != Void.class) {
                ModelImpl swaggerModel = (ModelImpl) this.parseSwaggerModel(apiResponse.dataTypeClass());

                if (apiResponse.allowMultiple()) {
                    ArrayProperty fieldKv = new ArrayProperty();
                    fieldKv.setName(swaggerModel.getName());
                    fieldKv.setDescription(apiResponse.value());
                    fieldKv.items(new RefProperty(swaggerModel.getName()));

                    propertiesList.put(apiResponse.name(), fieldKv);
                } else {
                    ObjectProperty fieldKv = new ObjectProperty();
                    fieldKv.setName(swaggerModel.getName());
                    fieldKv.setDescription(apiResponse.value());

                    propertiesList.put(apiResponse.name(), fieldKv);
                }

                //fieldKv.set("key", apiResponse.name());
                //fieldKv.set("name", swaggerModel.getStr("name"));
                //fieldKv.set("description", apiResponse.value());
                //fieldKv.set("type", ApiEnum.RES_OBJECT);
                //fieldKv.set("allowMultiple", apiResponse.allowMultiple());


            } else {
                if (apiResponse.allowMultiple()) {
                    ArrayProperty fieldKv = new ArrayProperty();

                    fieldKv.setName(apiResponse.name());
                    fieldKv.setDescription(apiResponse.value());
                    fieldKv.setFormat(Utils.isBlank(apiResponse.format()) ? ApiEnum.FORMAT_STRING : apiResponse.format());
                    fieldKv.setExample(apiResponse.example());

                    UntypedProperty itemsProperty = new UntypedProperty();
                    itemsProperty.setType(Utils.isBlank(apiResponse.dataType()) ? ApiEnum.RES_STRING : apiResponse.dataType());
                    fieldKv.items(itemsProperty);

                    propertiesList.put(apiResponse.name(), fieldKv);
                } else {
                    UntypedProperty fieldKv = new UntypedProperty();

                    fieldKv.setName(apiResponse.name());
                    fieldKv.setDescription(apiResponse.value());
                    fieldKv.setType(Utils.isBlank(apiResponse.dataType()) ? ApiEnum.RES_STRING : apiResponse.dataType());
                    fieldKv.setFormat(Utils.isBlank(apiResponse.format()) ? ApiEnum.FORMAT_STRING : apiResponse.format());
                    fieldKv.setExample(apiResponse.example());

                    propertiesList.put(apiResponse.name(), fieldKv);
                }

                //fieldKv.set("exampleEnum", apiResponse.exampleEnum());
                //fieldKv.set("allowMultiple", apiResponse.allowMultiple());


            }
        }

        ModelImpl model = new ModelImpl();
        model.setProperties(propertiesList);
        model.setName(modelName);

        swagger.addDefinition(modelName, model);

        return model;
    }

    /**
     * 解析对象参数
     */
    private String toParameterSchema(ApiImplicitParam apiParam) {
        if (apiParam.dataTypeClass() != Void.class) {
            ModelImpl swaggerModel = (ModelImpl) this.parseSwaggerModel(apiParam.dataTypeClass());

            return swaggerModel.getName();
        }
        return null;
    }

    public String getHttpMethod(ActionHolder actionHolder, ApiOperation apiAction) {
        if (Utils.isBlank(apiAction.httpMethod())) {
            MethodType methodType = actionHolder.routing().method();

            if (methodType == null) {
                return ApiEnum.METHOD_GET;
            } else {
                if (methodType.ordinal() < MethodType.UNKNOWN.ordinal()) {
                    return methodType.name.toLowerCase();
                } else {
                    return ApiEnum.METHOD_GET;
                }
            }
        } else {
            return apiAction.httpMethod();
        }
    }

    /**
     * 获取host配置
     */
    private String getHost(DocDocket swaggerDock) {
        String host = swaggerDock.host();
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
}
