package org.noear.solon.docs.openapi2;

import io.swagger.annotations.*;
import io.swagger.models.*;
import io.swagger.models.ExternalDocs;
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
import org.noear.solon.core.handle.*;
import org.noear.solon.core.route.Routing;
import org.noear.solon.core.util.GenericUtil;
import org.noear.solon.core.util.PathUtil;

import org.noear.solon.core.wrap.ClassWrap;
import org.noear.solon.core.wrap.FieldWrap;
import org.noear.solon.core.wrap.ParamWrap;
import org.noear.solon.docs.ApiEnum;
import org.noear.solon.docs.DocDocket;
import org.noear.solon.docs.exception.DocException;
import org.noear.solon.docs.models.ApiScheme;

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
    private final Swagger swagger = new Swagger();
    private final DocDocket docket;

    /**
     * 公共返回模型
     */
    private ModelImpl globalResultModel;

    public Swagger2Builder(DocDocket docket) {
        this.docket = docket;
    }

    public Swagger build() {
        // 解析通用返回
        if (docket.globalResult() != null) {
            this.globalResultModel = (ModelImpl) this.parseSwaggerModel(docket.globalResult(), docket.globalResult());
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

        swagger.host(BuilderHelper.getHost(docket));
        swagger.basePath(docket.basePath());

        if(docket.schemes() != null) {
            for (ApiScheme scheme : docket.schemes()) {
                swagger.scheme(Scheme.forValue(scheme.toValue()));
            }
        }

        if(docket.externalDocs() != null) {
            swagger.externalDocs(new ExternalDocs(docket.externalDocs().description(), docket.externalDocs().url()));
        }

        swagger.vendorExtensions(docket.vendorExtensions());
        swagger.setSecurityDefinitions(docket.securityDefinitions());

        if (swagger.getTags() != null) {
            //排序
            swagger.getTags().sort((t1, t2) -> {
                String name1 = t1.getDescription();
                String name2 = t2.getDescription();

                return Collator.getInstance(Locale.UK).compare(name1, name2);
            });
        }

        if (swagger.getDefinitions() != null) {
            //排序
            List<String> definitionKeys = new ArrayList<>(swagger.getDefinitions().keySet());
            Map<String, Model> definitionMap = new LinkedHashMap<>();
            definitionKeys.sort((name1, name2) -> Collator.getInstance(Locale.UK).compare(name1, name2));
            for (String name : definitionKeys) {
                definitionMap.put(name, swagger.getDefinitions().get(name));
            }
            swagger.setDefinitions(definitionMap);
        }

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
            if (routing.target() instanceof Action) {
                //如果是 Action
                resolveAction(apiMap, routing);
            }

            if (routing.target() instanceof Gateway) {
                //如果是 Gateway (网关)
                for (Routing<Handler> routing2 : ((Gateway) routing.target()).getMainRouting().getAll()) {
                    if (routing2.target() instanceof Action) {
                        resolveAction(apiMap, routing2);
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

    private void resolveAction(Map<Class<?>, List<ActionHolder>> apiMap, Routing<Handler> routing){
        Action action = (Action) routing.target();
        Class<?> controller = action.controller().clz();

        boolean matched = docket.apis().stream().anyMatch(res -> res.test(action));
        if (matched == false) {
            return;
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

        String controllerKey = BuilderHelper.getControllerKey(clazz);

        for (String tagName : api.tags()) {
            Tag tag = new Tag();
            tag.setName(tagName);
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

            String controllerKey = BuilderHelper.getControllerKey(actionHolder.controllerClz());
            String actionName = actionHolder.action().name();//action.getMethodName();
            Method actionMethod = actionHolder.action().method().getMethod();

            Set<String> actionTags = new HashSet<>();
            actionTags.addAll(Arrays.asList(actionHolder.controllerClz().getAnnotation(Api.class).tags()));
            actionTags.addAll(Arrays.asList(apiAction.tags()));
            actionTags.remove("");

            Path path = new Path();
            String pathKey = actionHolder.routing().path(); //PathUtil.mergePath(controllerKey, actionName);
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


            String operationMethod = BuilderHelper.getHttpMethod(actionHolder, apiAction);


            operation.setParameters(this.parseActionParameters(actionHolder));
            operation.setResponses(this.parseActionResponse(controllerKey, actionName, actionMethod));
            operation.setVendorExtension("controllerKey", controllerKey);
            operation.setVendorExtension("actionName", actionName);

            if (Utils.isBlank(apiAction.consumes())) {
                if (operationMethod.equals(ApiEnum.METHOD_GET)) {
                    operation.consumes(ApiEnum.CONSUMES_URLENCODED); //如果是 get ，则没有 content-type
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
    private List<Parameter> parseActionParameters(ActionHolder actionHolder) {
        Map<String, ParamHolder> actionParamMap = new LinkedHashMap<>();
        for (ParamWrap p1 : actionHolder.action().method().getParamWraps()) {
            actionParamMap.put(p1.getName(), new ParamHolder(p1));
        }

        // 获取参数注解信息
        {
            List<ApiImplicitParam> apiParams = new ArrayList<>();
            if (actionHolder.isAnnotationPresent(ApiImplicitParams.class)) {
                apiParams.addAll(Arrays.asList(actionHolder.getAnnotation(ApiImplicitParams.class).value()));
            }

            if (actionHolder.isAnnotationPresent(ApiImplicitParams.class)) {
                ApiImplicitParam[] paramArray = actionHolder.getAnnotationsByType(ApiImplicitParam.class);
                apiParams.addAll(Arrays.asList(paramArray));
            }

            for (ApiImplicitParam a1 : apiParams) {
                ParamHolder paramHolder = actionParamMap.get(a1.name());
                if (paramHolder == null) {
                    paramHolder = new ParamHolder(null);
                    actionParamMap.put(a1.name(), paramHolder);
                }
                paramHolder.binding(a1);
            }
        }

        // 构建参数列表(包含全局参数)
        List<Parameter> paramList = new ArrayList<>();

        for (ParamHolder paramHolder : actionParamMap.values()) {
            if (paramHolder.isIgnore()) {
                continue;
            }

            String paramSchema = this.getParameterSchema(paramHolder);
            String dataType = paramHolder.dataType();

            Parameter parameter;

            if (paramHolder.allowMultiple()) {
                if (Utils.isNotEmpty(paramSchema)) {
                    //array model
                    BodyParameter modelParameter = new BodyParameter();
                    modelParameter.setSchema(new ArrayModel().items(new RefProperty(paramSchema)));
                    if (paramHolder.getParam() != null && paramHolder.getParam().requireBody() == false) {
                        modelParameter.setIn(ApiEnum.PARAM_TYPE_QUERY);
                    }

                    parameter = modelParameter;
                } else if ("file".equals(dataType)) {
                    //array file
                    FormParameter formParameter = new FormParameter();
                    formParameter.type("array");
                    formParameter.items(new FileProperty());

                    parameter = formParameter;
                } else {
                    //array
                    ObjectProperty objectProperty = new ObjectProperty();
                    objectProperty.setType(dataType);

                    QueryParameter queryParameter = new QueryParameter();
                    queryParameter.type("array");
                    queryParameter.items(objectProperty);

                    parameter = queryParameter;
                }
            } else {
                if (Utils.isNotEmpty(paramSchema)) {
                    //model
                    if(paramHolder.isRequiredBody() || paramHolder.getParam() == null) {
                        //做为 body
                        BodyParameter modelParameter = new BodyParameter();
                        modelParameter.setSchema(new RefModel(paramSchema));
                        if (paramHolder.getParam() != null && paramHolder.getParam().requireBody() == false) {
                            modelParameter.setIn(ApiEnum.PARAM_TYPE_QUERY);
                        }

                        parameter = modelParameter;
                    }else {
                        parseActionParametersByFields(paramHolder, paramList);

                        continue;
                    }

                } else if ("file".equals(dataType)) {
                    //array file
                    FormParameter formParameter = new FormParameter();
                    formParameter.items(new FileProperty());

                    parameter = formParameter;
                } else {
                    if (paramHolder.isRequiredHeader()) {
                        parameter = new HeaderParameter();
                    } else if (paramHolder.isRequiredCookie()) {
                        parameter = new CookieParameter();
                    } else if (paramHolder.isRequiredPath()) {
                        parameter = new PathParameter();
                    } else {
                        QueryParameter queryParameter = new QueryParameter();
                        queryParameter.setType(dataType);

                        if (paramHolder.getAnno() != null) {
                            queryParameter.setFormat(paramHolder.getAnno().format());
                            queryParameter.setDefaultValue(paramHolder.getAnno().defaultValue());
                        }

                        parameter = queryParameter;
                    }
                }
            }

            parameter.setName(paramHolder.getName());
            parameter.setDescription(paramHolder.getDescription());
            parameter.setRequired(paramHolder.isRequired());
            parameter.setReadOnly(paramHolder.isReadOnly());

            if (Utils.isEmpty(parameter.getIn())) {
                parameter.setIn(paramHolder.paramType());
            }

            paramList.add(parameter);
        }

        return paramList;
    }


    private void parseActionParametersByFields(ParamHolder paramHolder, List<Parameter> paramList) {
        //做为 字段
        ClassWrap classWrap = ClassWrap.get(paramHolder.getParam().getType());
        for (FieldWrap fw : classWrap.getFieldAllWraps().values()) {
            if(Modifier.isTransient(fw.field.getModifiers())){
                continue;
            }

            QueryParameter parameter = new QueryParameter();
            parameter.setType(fw.type.getSimpleName());

            ApiModelProperty anno = fw.field.getAnnotation(ApiModelProperty.class);

            if (anno != null) {
                parameter.setName(anno.name());
                parameter.setDescription(anno.value());
                parameter.setRequired(anno.required());
                parameter.setReadOnly(anno.readOnly());

                if (Utils.isNotEmpty(anno.dataType())) {
                    parameter.setType(anno.dataType());
                }
            }

            if (Utils.isEmpty(parameter.getName())) {
                parameter.setName(fw.field.getName());
            }


            paramList.add(parameter);
        }
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
            if (BuilderHelper.isModel(apiResClz)) {
                try {
                    ModelImpl commonResKv = (ModelImpl) this.parseSwaggerModel(apiResClz, method.getGenericReturnType());
                    swaggerModelName = commonResKv.getName();
                    return swaggerModelName;
                } catch (Exception e) {
                    String hint = method.getDeclaringClass().getName() + ":" + method.getName() + "->" + apiResClz.getSimpleName();
                    throw new DocException("Response model parsing failure: " + hint, e);
                }
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
        if (globalResultModel != null) {
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
    private Model parseSwaggerModel(Class<?> clazz, Type type) {
        String modelName = BuilderHelper.getModelName(clazz, type);

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
            if (Modifier.isStatic(field.getModifiers())) {
                //静态的跳过
                continue;
            }

            ApiModelProperty apiField = field.getAnnotation(ApiModelProperty.class);

            Class<?> typeClazz = field.getType();
            Type typeGenericType = field.getGenericType();
            if (typeGenericType instanceof TypeVariable) {
                if (type instanceof ParameterizedType) {
                    Map<String, Type> genericMap = GenericUtil.getGenericInfo(type);
                    Type typeClazz2 = genericMap.get(typeGenericType.getTypeName());
                    if (typeClazz2 instanceof Class) {
                        typeClazz = (Class<?>) typeClazz2;
                    }

                    if (typeClazz2 instanceof ParameterizedType) {
                        ParameterizedType typeGenericType2 = (ParameterizedType) typeClazz2;
                        typeClazz = (Class<?>) typeGenericType2.getRawType();
                        typeGenericType = typeClazz2;
                    }
                }
            }

            // List<Class> 类型
            if (Collection.class.isAssignableFrom(typeClazz)) {
                // 如果是List类型，得到其Generic的类型
                if (typeGenericType == null) {
                    continue;
                }

                // 如果是泛型参数的类型
                if (typeGenericType instanceof ParameterizedType) {
                    ArrayProperty fieldPr = new ArrayProperty();
                    if (apiField != null) {
                        fieldPr.setDescription(apiField.value());
                    }


                    ParameterizedType pt = (ParameterizedType) typeGenericType;
                    //得到泛型里的class类型对象
                    Type itemClazz = pt.getActualTypeArguments()[0];

                    if (itemClazz instanceof ParameterizedType) {
                        itemClazz = ((ParameterizedType) itemClazz).getRawType();
                    }

                    if (itemClazz instanceof Class) {
                        ModelImpl swaggerModel = (ModelImpl) this.parseSwaggerModel((Class<?>) itemClazz, itemClazz);

                        RefProperty itemPr = new RefProperty(swaggerModel.getName(), RefFormat.INTERNAL);
                        fieldPr.setItems(itemPr);
                    }


                    fieldList.put(field.getName(), fieldPr);
                }
                continue;
            }


            if (BuilderHelper.isModel(typeClazz)) {
                ModelImpl swaggerModel = (ModelImpl) this.parseSwaggerModel(typeClazz, typeClazz);

                RefProperty fieldPr = new RefProperty(swaggerModel.getName(), RefFormat.INTERNAL);
                if (apiField != null) {
                    fieldPr.setDescription(apiField.value());
                }

                fieldList.put(field.getName(), fieldPr);
            } else {
                ObjectProperty fieldPr = new ObjectProperty();
                fieldPr.setName(field.getName());

                if (apiField != null) {
                    fieldPr.setDescription(apiField.value());
                    fieldPr.setType(Utils.isBlank(apiField.dataType()) ? typeClazz.getSimpleName().toLowerCase() : apiField.dataType());
                    fieldPr.setExample(apiField.example());
                } else {
                    fieldPr.setType(typeClazz.getSimpleName().toLowerCase());
                }

                fieldList.put(field.getName(), fieldPr);
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
                ModelImpl swaggerModel = (ModelImpl) this.parseSwaggerModel(apiResponse.dataTypeClass(), apiResponse.dataTypeClass());

                if (apiResponse.allowMultiple()) {
                    ArrayProperty fieldPr = new ArrayProperty();
                    fieldPr.setName(swaggerModel.getName());
                    fieldPr.setDescription(apiResponse.value());
                    fieldPr.items(new RefProperty(swaggerModel.getName(), RefFormat.INTERNAL));

                    propertiesList.put(apiResponse.name(), fieldPr);
                } else {
                    RefProperty fieldPr = new RefProperty(swaggerModel.getName(), RefFormat.INTERNAL);
                    fieldPr.setDescription(apiResponse.value());

                    propertiesList.put(apiResponse.name(), fieldPr);
                }

                //fieldPr.set("key", apiResponse.name());
                //fieldPr.set("name", swaggerModel.getStr("name"));
                //fieldPr.set("description", apiResponse.value());
                //fieldPr.set("type", ApiEnum.RES_OBJECT);
                //fieldPr.set("allowMultiple", apiResponse.allowMultiple());


            } else {
                if (apiResponse.allowMultiple()) {
                    ArrayProperty fieldPr = new ArrayProperty();

                    fieldPr.setName(apiResponse.name());
                    fieldPr.setDescription(apiResponse.value());
                    fieldPr.setFormat(Utils.isBlank(apiResponse.format()) ? ApiEnum.FORMAT_STRING : apiResponse.format());
                    fieldPr.setExample(apiResponse.example());

                    UntypedProperty itemsProperty = new UntypedProperty();
                    itemsProperty.setType(Utils.isBlank(apiResponse.dataType()) ? ApiEnum.RES_STRING : apiResponse.dataType());
                    fieldPr.items(itemsProperty);

                    propertiesList.put(apiResponse.name(), fieldPr);
                } else {
                    UntypedProperty fieldPr = new UntypedProperty();

                    fieldPr.setName(apiResponse.name());
                    fieldPr.setDescription(apiResponse.value());
                    fieldPr.setType(Utils.isBlank(apiResponse.dataType()) ? ApiEnum.RES_STRING : apiResponse.dataType());
                    fieldPr.setFormat(Utils.isBlank(apiResponse.format()) ? ApiEnum.FORMAT_STRING : apiResponse.format());
                    fieldPr.setExample(apiResponse.example());

                    propertiesList.put(apiResponse.name(), fieldPr);
                }

                //fieldPr.set("exampleEnum", apiResponse.exampleEnum());
                //fieldPr.set("allowMultiple", apiResponse.allowMultiple());


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
    private String getParameterSchema(ParamHolder paramHolder) {
        if (paramHolder.getAnno() != null) {
            Class<?> dataTypeClass = paramHolder.getAnno().dataTypeClass();

            if (dataTypeClass != Void.class) {
                ModelImpl swaggerModel = (ModelImpl) this.parseSwaggerModel(dataTypeClass, dataTypeClass);

                return swaggerModel.getName();
            }
        }

        if (paramHolder.getParam() != null) {
            Class<?> dataTypeClass = paramHolder.getParam().getType();
            if (dataTypeClass.isPrimitive()) {
                return null;
            }

            if (UploadedFile.class.equals(dataTypeClass)) {
                return null;
            }

            if (dataTypeClass.getName().startsWith("java.lang")) {
                return null;
            }

            Type dataGenericType = paramHolder.getParam().getGenericType();

            if (dataTypeClass != Void.class) {
                ModelImpl swaggerModel = (ModelImpl) this.parseSwaggerModel(dataTypeClass, dataGenericType);

                return swaggerModel.getName();
            }
        }

        return null;
    }
}
