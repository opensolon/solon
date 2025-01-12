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
package org.noear.solon.docs.openapi2;

import io.swagger.annotations.*;
import io.swagger.models.Contact;
import io.swagger.models.ExternalDocs;
import io.swagger.models.Info;
import io.swagger.models.License;
import io.swagger.models.Tag;
import io.swagger.models.*;
import io.swagger.models.auth.SecuritySchemeDefinition;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.parameters.*;
import io.swagger.models.properties.*;
import io.swagger.models.refs.RefFormat;
import io.swagger.solon.annotation.ApiNoAuthorize;
import io.swagger.solon.annotation.ApiRes;
import io.swagger.solon.annotation.ApiResProperty;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.handle.*;
import org.noear.solon.core.route.Routing;
import org.noear.solon.core.util.NameUtil;
import org.noear.solon.core.util.PathUtil;
import org.noear.solon.core.wrap.*;
import org.noear.solon.docs.ApiEnum;
import org.noear.solon.docs.DocDocket;
import org.noear.solon.docs.exception.DocException;
import org.noear.solon.docs.models.ApiContact;
import org.noear.solon.docs.models.ApiLicense;
import org.noear.solon.docs.models.ApiScheme;
import org.noear.solon.docs.openapi2.common.Constants;
import org.noear.solon.docs.openapi2.impl.ActionHolder;
import org.noear.solon.docs.openapi2.impl.BuilderHelper;
import org.noear.solon.docs.openapi2.impl.ParamHolder;
import org.noear.solon.docs.openapi2.wrap.ApiImplicitParamImpl;

import java.lang.reflect.*;
import java.text.Collator;
import java.time.LocalDateTime;
import java.util.*;

/**
 * openapi v2 json builder
 *
 * @author noear
 * @since 2.3
 */
public class OpenApi2Builder {
    private final Swagger swagger = new Swagger();
    private final DocDocket docket;

    /**
     * 公共返回模型
     */
    private ModelImpl globalResultModel;

    public OpenApi2Builder(DocDocket docket) {
        this.docket = docket;
    }

    public Swagger build(String description) {
        // 解析通用返回
        if (docket.globalResult() != null) {
            this.globalResultModel = (ModelImpl) this.parseSwaggerModel(docket.globalResult(), docket.globalResult());
        }

        // 解析JSON
        this.parseGroupPackage();

        ApiLicense apiLicense = docket.info().license();
        ApiContact apiContact = docket.info().contact();

        if(Utils.isEmpty(description)){
            description = docket.info().description();
        }

        swagger.setSwagger(docket.version());
        swagger.info(new Info()
                .title(docket.info().title())
                .description(description)
                .termsOfService(docket.info().termsOfService())
                .version(docket.info().version()));

        if (apiLicense != null) {
            License license = new License()
                    .url(apiLicense.url())
                    .name(apiLicense.name());
            license.setVendorExtensions(apiLicense.vendorExtensions());

            swagger.getInfo().setLicense(license);
        }

        if (apiContact != null) {
            Contact contact = new Contact()
                    .email(apiContact.email())
                    .name(apiContact.name())
                    .url(apiContact.url());
            contact.setVendorExtensions(apiContact.vendorExtensions());
            swagger.getInfo().contact(contact);
        }


        swagger.host(BuilderHelper.getHost(docket));
        swagger.basePath(docket.basePath());

        if (docket.schemes() != null) {
            for (ApiScheme scheme : docket.schemes()) {
                swagger.scheme(Scheme.forValue(scheme.toValue()));
            }
        }

        if (docket.externalDocs() != null) {
            swagger.externalDocs(new ExternalDocs(docket.externalDocs().description(), docket.externalDocs().url()));
        }

        swagger.vendorExtensions(docket.vendorExtensions());
        docket.securityExtensions().forEach((key, val) -> {
            if (val instanceof SecuritySchemeDefinition) {
                swagger.addSecurityDefinition(key, (SecuritySchemeDefinition) val);
            }
        });
        //

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
        //获取所有控制器及动作
        Map<Class<?>, List<ActionHolder>> classMap = this.getApiAction();

        for (Map.Entry<Class<?>, List<ActionHolder>> kv : classMap.entrySet()) {
            // 解析controller
            this.parseController(kv.getKey(), kv.getValue());
        }
    }


    /**
     * 获取全部Action
     */
    private Map<Class<?>, List<ActionHolder>> getApiAction() {
        Map<Class<?>, List<ActionHolder>> apiMap = new HashMap<>(16);

        //@since 3.0
        Collection<Routing<Handler>> routingCollection = Solon.app().router().getAll();
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

    private void resolveAction(Map<Class<?>, List<ActionHolder>> apiMap, Routing<Handler> routing) {
        Action action = (Action) routing.target();
        Class<?> controller = action.controller().clz();

        boolean matched = docket.apis().stream().anyMatch(res -> res.test(action));
        if (!matched) {
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
        Set<String> apiTags = new LinkedHashSet<>();
        apiTags.add(api.value());
        apiTags.addAll(Arrays.asList(api.tags()));
        apiTags.remove("");

        for (String tagName : apiTags) {
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

            Set<String> actionTags = actionHolder.getTags(apiAction);


            String pathKey = actionHolder.routing().path(); //PathUtil.mergePath(controllerKey, actionName);
            //支持 context-path
            pathKey = PathUtil.mergePath(Solon.cfg().serverContextPath(), pathKey);


            Path path = swagger.getPath(pathKey);
            if (path == null) {
                //path 要重复可用
                path = new Path();
                swagger.path(pathKey, path);
            }


            Operation operation = new Operation();

            operation.setTags(new ArrayList<>(actionTags));
            operation.setSummary(apiAction.value());
            operation.setDescription(apiAction.notes());
            operation.setDeprecated(actionHolder.isAnnotationPresent(Deprecated.class));

            if ((actionHolder.isAnnotationPresent(ApiNoAuthorize.class) ||
                    actionHolder.controllerClz().isAnnotationPresent(ApiNoAuthorize.class)) == false) {
                for (String securityName : docket.securityExtensions().keySet()) {
                    operation.security(new SecurityRequirement(securityName).scope("global"));
                }
            }

            String operationMethod = BuilderHelper.getHttpMethod(actionHolder, apiAction);

            String operationConsumes = Utils.annoAlias(apiAction.consumes(), actionHolder.action().consumes());
            String operationProduces = Utils.annoAlias(apiAction.produces(), actionHolder.action().produces());
            boolean isRequiredBody = false;
            if (operationConsumes != null && operationConsumes.contains("json") && operationMethod.equals(ApiEnum.METHOD_GET) == false) {
                isRequiredBody = true;
            }


            operation.setParameters(this.parseActionParameters(actionHolder, isRequiredBody));
            operation.setResponses(this.parseActionResponse(controllerKey, actionName, actionMethod));
            operation.setVendorExtension("controllerKey", controllerKey);
            operation.setVendorExtension("actionName", actionName);

            //添加全局参数
            for (Object p1 : docket.globalParams()) {
                if (p1 instanceof Parameter) {
                    operation.addParameter((Parameter) p1);
                }
            }

            if (Utils.isEmpty(operationConsumes)) {
                if (operationMethod.equals(ApiEnum.METHOD_GET)) {
                    operation.consumes(ApiEnum.CONSUMES_URLENCODED); //如果是 get ，则没有 content-type //ApiEnum.CONSUMES_URLENCODED
                } else {
                    if (operation.getParameters().stream().anyMatch(parameter -> parameter instanceof BodyParameter)) {
                        operation.consumes(ApiEnum.CONSUMES_JSON);
                    } else {
                        operation.consumes(ApiEnum.CONSUMES_URLENCODED);
                    }
                }
            } else {
                operation.consumes(operationConsumes);
            }

            if (Utils.isEmpty(operationProduces)) {
                operation.produces(ApiEnum.PRODUCES_DEFAULT);
            } else {
                operation.produces(operationProduces);
            }


            operation.setOperationId(operationMethod + "_" + pathKey.replace("/", "_"));

            path.set(operationMethod, operation);
        }
    }

    /**
     * 解析action 参数文档
     */
    private List<Parameter> parseActionParameters(ActionHolder actionHolder, boolean isRequiredBody) {
        Map<String, ParamHolder> actionParamMap = new LinkedHashMap<>();
        for (ParamWrap p1 : actionHolder.action().method().getParamWraps()) {
            actionParamMap.put(p1.spec().getName(), new ParamHolder(p1));
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
                paramHolder.binding(new ApiImplicitParamImpl(a1));
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
                    if (isRequiredBody == false && paramHolder.getParam() != null && paramHolder.getParam().spec().isRequiredBody() == false) {
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

                    if (paramHolder.isRequiredHeader()) {
                        parameter = new HeaderParameter().type(ApiEnum.RES_ARRAY);
                    } else if (paramHolder.isRequiredCookie()) {
                        parameter = new CookieParameter().type(ApiEnum.RES_ARRAY);
                    } else if (paramHolder.isRequiredPath()) {
                        parameter = new PathParameter().type(ApiEnum.RES_ARRAY);
                    } else if (isRequiredBody  || paramHolder.isRequiredBody()) {
                        BodyParameter bodyParameter = new BodyParameter();
                        if (Utils.isNotEmpty(dataType)) {
                            bodyParameter.setSchema(new ArrayModel().items(objectProperty));
                        }
                        parameter = bodyParameter;
                    } else {
                        parameter = new QueryParameter().type(ApiEnum.RES_ARRAY).items(objectProperty);
                    }
                }
            } else {
                if (Utils.isNotEmpty(paramSchema)) {
                    //model
                    if (isRequiredBody  || paramHolder.isRequiredBody() || paramHolder.getParam() == null) {
                        //做为 body
                        BodyParameter modelParameter = new BodyParameter();

                        if (paramHolder.isMap()) {
                            modelParameter.setSchema(new ModelImpl().type("object"));
                        } else {
                            modelParameter.setSchema(new RefModel(paramSchema));
                        }

                        if (paramHolder.getParam() != null && paramHolder.getParam().spec().isRequiredBody() == false) {
                            modelParameter.setIn(ApiEnum.PARAM_TYPE_QUERY);
                        }

                        parameter = modelParameter;
                    } else {
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
                    } else if (isRequiredBody  || paramHolder.isRequiredBody()) {
                        BodyParameter bodyParameter = new BodyParameter();
                        if (Utils.isNotEmpty(dataType)) {
                            bodyParameter.setSchema(new ModelImpl().type(dataType));
                        }
                        parameter = bodyParameter;
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

        //return paramList;
        return mergeBodyParamList(actionHolder, paramList);
    }

    private List<Parameter> mergeBodyParamList(ActionHolder actionHolder, List<Parameter> paramList) {
        ArrayList<Parameter> parameters = new ArrayList<>();
        if (Constants.CONTENT_TYPE_JSON_TYPE.equals(actionHolder.action().consumes())
                || paramList.stream().map(Parameter::getIn).anyMatch(in -> in.equals(ApiEnum.PARAM_TYPE_BODY))) {
            BodyParameter finalBodyParameter = new BodyParameter();
            finalBodyParameter.setIn(Constants.BODY_TYPE);
            finalBodyParameter.name(Constants.DATA);
            ModelImpl model = new ModelImpl();
            model.setDescription(Constants.DEFAULT_BODY_NAME);
            model.setName(Constants.DEFAULT_BODY_NAME);

            for (Parameter parameter : paramList) {
                if (parameter instanceof BodyParameter) {
                    BodyParameter bodyParameter = ((BodyParameter) parameter);
                    Model schema = bodyParameter.getSchema();
                    if (schema instanceof RefModel) {
                        RefModel refModel = (RefModel) schema;
                        model.setProperties(this.swagger.getDefinitions().get(refModel.getSimpleRef()).getProperties());
                    } else {
                        if (schema instanceof ModelImpl) {
                            ModelImpl schemaModelImpl = (ModelImpl) schema;
                            ObjectProperty property = new ObjectProperty();
                            property.setType(schemaModelImpl.getType());
                            property.setDescription(schemaModelImpl.getDescription());
                            model.setProperties(Collections.singletonMap(bodyParameter.getName(), property));
                        } else {
                            parameters.add(parameter);
                        }
                    }
                } else {
                    parameters.add(parameter);
                }
            }
            //String key = "Map[" + actionHolder.action().fullName() + "]";
            // 避免其他平台数据导入错误
            String key = String.format("Map[%s]", actionHolder.action().fullName().replace("/", "_").replace("${", "").replace("}", ""));
            this.swagger.addDefinition(key, model);
            finalBodyParameter.setSchema(new RefModel(key));
            parameters.add(finalBodyParameter);
        } else {
            parameters.addAll(paramList);
        }
        return parameters;
    }


    private void parseActionParametersByFields(ParamHolder paramHolder, List<Parameter> paramList) {
        //做为 字段
        ClassWrap classWrap = ClassWrap.get(paramHolder.getParam().getType());
        for (FieldWrap fw : classWrap.getAllFieldWraps()) {
            if (Modifier.isTransient(fw.getField().getModifiers())) {
                continue;
            }

            QueryParameter parameter = new QueryParameter();

            if (Collection.class.isAssignableFrom(fw.getType())) {
                parameter.setType(ApiEnum.RES_ARRAY);
            } else if (Map.class.isAssignableFrom(fw.getType())) {
                parameter.setType(ApiEnum.RES_OBJECT);
            } else {
                parameter.setType(fw.getType().getSimpleName());
            }

            ApiModelProperty anno = fw.getField().getAnnotation(ApiModelProperty.class);

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
                parameter.setName(fw.getName());
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

        // 实验性质 自定义返回值
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
        final String modelName = BuilderHelper.getModelName(clazz, type);

        // 1.已存在,不重复解析
        if (swagger.getDefinitions() != null) {
            Model model = swagger.getDefinitions().get(modelName);

            if (null != model) {
                return model;
            }
        }


        // 2.创建模型
        ApiModel apiModel = clazz.getAnnotation(ApiModel.class);
        String title;
        if (apiModel != null) {
            title = apiModel.description();
        } else {
            title = modelName;
        }

        Map<String, Property> propertyList = new LinkedHashMap<>();


        ModelImpl model = new ModelImpl();
        model.setName(modelName);
        model.setTitle(title);
        model.setType(ApiEnum.RES_OBJECT);

        swagger.addDefinition(modelName, model);

        if (clazz.isEnum()) {
            model.setType(ApiEnum.RES_STRING);
            return model;
        }


        // 3.完成模型解析
        ClassWrap classWrap = ClassWrap.get(clazz);
        if (clazz.isInterface()) {
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.getParameterTypes().length == 0
                        && method.getName().startsWith("get")) {
                    if (Modifier.isStatic(method.getModifiers())) {
                        //静态的跳过
                        continue;
                    }

                    String propName = NameUtil.getFieldName(method.getName());

                    ApiModelProperty propAnno = method.getAnnotation(ApiModelProperty.class);
                    TypeWrap propType = new TypeWrap(clazz, method.getReturnType(), method.getGenericReturnType());

                    parseSwaggerModelProperty(type, modelName, propertyList, propName, propType.getType(), propType.getGenericType(), propAnno);
                }
            }

            for (Method method : clazz.getMethods()) {
                if (method.getParameterTypes().length == 0
                        && method.getName().startsWith("get")) {
                    if (Modifier.isStatic(method.getModifiers())) {
                        //静态的跳过
                        continue;
                    }
                    String propName = NameUtil.getFieldName(method.getName());

                    if (propertyList.containsKey(propName)) {
                        continue;
                    }

                    ApiModelProperty propAnno = method.getAnnotation(ApiModelProperty.class);
                    TypeWrap propType = new TypeWrap(clazz, method.getReturnType(), method.getGenericReturnType());

                    parseSwaggerModelProperty(type, modelName, propertyList, propName, propType.getType(), propType.getGenericType(), propAnno);
                }
            }
        } else {
            for (FieldWrap fw : classWrap.getAllFieldWraps()) {
                if (Modifier.isStatic(fw.getField().getModifiers())) {
                    //静态的跳过
                    continue;
                }

                String propName = fw.getName();

                ApiModelProperty propAnno = fw.getField().getAnnotation(ApiModelProperty.class);
                TypeWrap fwTypeWrap = fw.typeWrapOf(type); //如果是返回类型，它的泛型需要借签 type

                parseSwaggerModelProperty(type, modelName, propertyList, propName, fwTypeWrap.getType(), fwTypeWrap.getGenericType(), propAnno);
            }
        }

        model.setProperties(propertyList);
        return model;
    }

    private void parseSwaggerModelProperty(Type type, String modelName, Map<String, Property> propertyList, String propName, Class<?> propType, ParameterizedType propGenericType, ApiModelProperty propAnno) {
        // 隐藏的跳过
        if (propAnno != null && propAnno.hidden()) {
            return;
        }

        // List<Class> 类型
        if (Collection.class.isAssignableFrom(propType)) {
            // 如果是List类型，得到其Generic的类型
            if (propGenericType == null) {
                return;
            }

            // 如果是泛型参数的类型
            ArrayProperty fieldPr = new ArrayProperty();
            if (propAnno != null) {
                fieldPr.setDescription(propAnno.value());
                fieldPr.setRequired(propAnno.required());
                // 如果是泛型参数的类型 加上 示例，在knife4j下将无法正确解析，所以将其注释
                // fieldPr.setExample(propAnno.example());
            }

            //得到泛型里的class类型对象
            Type itemClazz = propGenericType.getActualTypeArguments()[0];

            if (itemClazz instanceof Class) {
                if (itemClazz.equals(type)) {
                    //避免出现循环依赖，然后 oom
                    RefProperty itemPr = new RefProperty(modelName, RefFormat.INTERNAL);
                    fieldPr.setItems(itemPr);
                } else {
                    Property itemPr = getPrimitiveProperty((Class<?>) itemClazz);

                    if (itemPr != null) {
                        fieldPr.setItems(itemPr);
                    } else {
                        ModelImpl swaggerModel = (ModelImpl) this.parseSwaggerModel((Class<?>) itemClazz, itemClazz);

                        itemPr = new RefProperty(swaggerModel.getName(), RefFormat.INTERNAL);
                        fieldPr.setItems(itemPr);
                    }
                }
            }


            propertyList.put(propName, fieldPr);
            return;
        }


        if (BuilderHelper.isModel(propType)) {
            if (propType.equals(type)) {
                //避免出现循环依赖，然后 oom
                RefProperty propPr = new RefProperty(modelName, RefFormat.INTERNAL);
                if (propAnno != null) {
                    propPr.setDescription(propAnno.value());
                    propPr.setRequired(propAnno.required());
                    propPr.setExample(propAnno.example());
                }

                propertyList.put(propName, propPr);
            } else {
                ModelImpl swaggerModel = (ModelImpl) this.parseSwaggerModel(propType, propGenericType);

                RefProperty propPr = new RefProperty(swaggerModel.getName(), RefFormat.INTERNAL);
                if (propAnno != null) {
                    propPr.setDescription(propAnno.value());
                    propPr.setRequired(propAnno.required());
                    propPr.setExample(propAnno.example());
                }

                propertyList.put(propName, propPr);
            }
        } else {
            ObjectProperty propPr = new ObjectProperty();
            propPr.setName(propName);

            if (propAnno != null) {
                propPr.setDescription(propAnno.value());
                propPr.setRequired(propAnno.required());
                propPr.setExample(propAnno.example());
                propPr.setType(Utils.isBlank(propAnno.dataType()) ? propType.getSimpleName().toLowerCase() : propAnno.dataType());
            } else {
                propPr.setType(propType.getSimpleName().toLowerCase());
            }

            propertyList.put(propName, propPr);
        }
    }

    /**
     * 将action response解析为swagger model
     */
    private Model parseSwaggerModel(String controllerKey, String actionName, List<ApiResProperty> responses) {
        final String modelName = controllerKey + "_" + actionName;

        Map<String, Property> propertiesList = new LinkedHashMap<>();

        ModelImpl model = new ModelImpl();
        model.setName(modelName);

        swagger.addDefinition(modelName, model);


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
                    ArrayProperty propPr = new ArrayProperty();
                    propPr.setName(swaggerModel.getName());
                    propPr.setDescription(apiResponse.value());
                    propPr.items(new RefProperty(swaggerModel.getName(), RefFormat.INTERNAL));

                    propertiesList.put(apiResponse.name(), propPr);
                } else {
                    RefProperty propPr = new RefProperty(swaggerModel.getName(), RefFormat.INTERNAL);
                    propPr.setDescription(apiResponse.value());

                    propertiesList.put(apiResponse.name(), propPr);
                }
            } else {
                if (apiResponse.allowMultiple()) {
                    ArrayProperty propPr = new ArrayProperty();

                    propPr.setName(apiResponse.name());
                    propPr.setDescription(apiResponse.value());
                    propPr.setFormat(Utils.isBlank(apiResponse.format()) ? ApiEnum.FORMAT_STRING : apiResponse.format());
                    propPr.setExample(apiResponse.example());

                    UntypedProperty itemsProperty = new UntypedProperty();
                    itemsProperty.setType(Utils.isBlank(apiResponse.dataType()) ? ApiEnum.RES_STRING : apiResponse.dataType());
                    propPr.items(itemsProperty);

                    propertiesList.put(apiResponse.name(), propPr);
                } else {
                    UntypedProperty propPr = new UntypedProperty();

                    propPr.setName(apiResponse.name());
                    propPr.setDescription(apiResponse.value());
                    propPr.setType(Utils.isBlank(apiResponse.dataType()) ? ApiEnum.RES_STRING : apiResponse.dataType());
                    propPr.setFormat(Utils.isBlank(apiResponse.format()) ? ApiEnum.FORMAT_STRING : apiResponse.format());
                    propPr.setExample(apiResponse.example());

                    propertiesList.put(apiResponse.name(), propPr);
                }
            }
        }

        model.setProperties(propertiesList);
        return model;
    }


    /**
     * 解析对象参数
     */
    private String getParameterSchema(ParamHolder paramHolder) {
        Class<?> dataTypeClass = paramHolder.dataTypeClass();

        if (dataTypeClass != null) {
            if (dataTypeClass.isPrimitive()) {
                return null;
            }

            if (UploadedFile.class.equals(dataTypeClass)) {
                return null;
            }

            if (dataTypeClass.getName().startsWith("java.lang")) {
                return null;
            }

            Type dataGenericType = paramHolder.dataGenericType();

            if (dataTypeClass != Void.class) {
                if (Collection.class.isAssignableFrom(dataTypeClass) && dataGenericType instanceof ParameterizedType) {
                    Type itemType = ((ParameterizedType) dataGenericType).getActualTypeArguments()[0];

                    if (itemType instanceof Class) {
                        ModelImpl swaggerModel = (ModelImpl) this.parseSwaggerModel((Class<?>) itemType, itemType);
                        return swaggerModel.getName();
                    }
                }

                ModelImpl swaggerModel = (ModelImpl) this.parseSwaggerModel(dataTypeClass, dataGenericType);
                return swaggerModel.getName();
            }
        }

        return null;
    }

    private Property getPrimitiveProperty(Class<?> clz) {
        if (clz == Integer.class || clz == int.class) {
            return new IntegerProperty();
        }

        if (clz == Long.class || clz == long.class) {
            return new LongProperty();
        }

        if (clz == Float.class || clz == float.class) {
            return new FloatProperty();
        }

        if (clz == Double.class || clz == double.class) {
            return new DoubleProperty();
        }

        if (clz == Boolean.class || clz == boolean.class) {
            return new BooleanProperty();
        }

        if (clz == Date.class) {
            return new DateProperty();
        }

        if (clz == LocalDateTime.class) {
            return new DateTimeProperty();
        }

        if (clz == String.class || clz.isEnum()) {
            return new StringProperty();
        }


        return null;
    }
}