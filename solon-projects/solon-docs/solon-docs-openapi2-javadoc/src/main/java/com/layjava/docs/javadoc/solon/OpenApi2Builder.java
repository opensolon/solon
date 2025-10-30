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
package com.layjava.docs.javadoc.solon;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import com.github.therapi.runtimejavadoc.*;
import com.layjava.docs.javadoc.solon.common.Constants;
import com.layjava.docs.javadoc.solon.impl.ActionHolder;
import com.layjava.docs.javadoc.solon.impl.BuilderHelper;
import com.layjava.docs.javadoc.solon.impl.ParamHolder;
import com.layjava.docs.javadoc.solon.wrap.ApiImplicitParamImpl;
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
import org.noear.eggg.ClassEggg;
import org.noear.eggg.FieldEggg;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.handle.*;
import org.noear.solon.core.route.Routing;
import org.noear.solon.core.util.EgggUtil;
import org.noear.solon.core.util.GenericUtil;
import org.noear.solon.core.util.PathUtil;
import org.noear.solon.core.wrap.MethodWrap;
import org.noear.solon.core.wrap.ParamWrap;
import org.noear.solon.docs.DocDocket;
import org.noear.solon.docs.exception.DocException;
import org.noear.solon.docs.models.ApiContact;
import org.noear.solon.docs.ApiEnum;
import org.noear.solon.docs.models.ApiLicense;
import org.noear.solon.docs.models.ApiScheme;

import java.io.StringReader;
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
    private static final CommentFormatter formatter = new CommentFormatter();

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
        ctlList.sort(Comparator.comparingInt(clazz -> {
            Api annotation = clazz.getAnnotation(Api.class);
            if (annotation != null) {
                return annotation.position();
            }
            return 0;
        }));

        Map<Class<?>, List<ActionHolder>> result = new LinkedHashMap<>();
        ctlList.forEach(i -> {
            List<ActionHolder> actionHolders = apiMap.get(i);
            actionHolders.sort(Comparator.comparingInt(ah -> {
                Api annotation = ah.getAnnotation(Api.class);
                if (annotation != null) {
                    return annotation.position();
                }
                return 0;
            }));
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
            List<ActionHolder> actionHolders = apiMap.get(controller);
            if (!actionHolders.contains(actionHolder)) {
                actionHolders.add(actionHolder);
                apiMap.put(controller, actionHolders);
            }
        } else {
            List<ActionHolder> actionHolders = new ArrayList<>();
            actionHolders.add(actionHolder);
            apiMap.put(controller, actionHolders);
        }
    }

    /**
     * 解析controller
     */
    private void parseController(Class<?> clazz, List<ActionHolder> actionHolders) {
        // controller 信息
        Api api = clazz.getAnnotation(Api.class);
        if (api != null && api.hidden()) {
            return;
        }

        String controllerKey = BuilderHelper.getControllerKey(clazz);
        Set<String> apiTags = new LinkedHashSet<>();
        String description = "";
        if (api == null) {
            ClassJavadoc javadoc = RuntimeJavadoc.getJavadoc(clazz);
            description = format(javadoc.getComment());
            List<String> list = IoUtil.readLines(new StringReader(description), new ArrayList<>());
            if (CollUtil.isNotEmpty(list)) {
                apiTags.add(list.get(0));
            }
        } else {
            apiTags.add(api.value());
            apiTags.addAll(Arrays.asList(api.tags()));
        }

        apiTags.remove("");

        for (String tagName : apiTags) {
            Tag tag = new Tag();
            tag.setName(tagName);
            tag.setDescription(StrUtil.isBlank(description) ? controllerKey + " (" + clazz.getSimpleName() + ")" : description);

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

            if (apiAction != null && apiAction.hidden()) {
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
            // 方法说明
            if (apiAction != null) {
                operation.setSummary(apiAction.value());
            } else {
                ClassJavadoc javadoc = RuntimeJavadoc.getJavadoc(actionHolder.controllerClz().getName());
                String comment = format(javadoc.getComment());

                List<String> list = IoUtil.readLines(new StringReader(comment), new ArrayList<>());
                if (CollUtil.isNotEmpty(list)) {
                    operation.setTags(Arrays.asList(list.get(0)));
                }
                javadoc.getMethods().stream().filter(m -> m.getName().equals(actionMethod.getName())).findFirst().ifPresent(methodJavadoc -> {
                    operation.setSummary(format(methodJavadoc.getComment()));
                });
            }
            // operation.setDescription(apiAction.notes());
            operation.setDeprecated(actionHolder.isAnnotationPresent(Deprecated.class));

            if ((actionHolder.isAnnotationPresent(ApiNoAuthorize.class) ||
                    actionHolder.controllerClz().isAnnotationPresent(ApiNoAuthorize.class)) == false) {
                for (String securityName : docket.securityExtensions().keySet()) {
                    operation.security(new SecurityRequirement(securityName).scope("global"));
                }
            }


            String operationMethod = BuilderHelper.getHttpMethod(actionHolder, apiAction);


            operation.setParameters(this.parseActionParameters(actionHolder));
            operation.setResponses(this.parseActionResponse(controllerKey, actionName, actionMethod));
            operation.setVendorExtension("controllerKey", controllerKey);
            operation.setVendorExtension("actionName", actionName);

            //添加全局参数
            for (Object p1 : docket.globalParams()) {
                if (p1 instanceof Parameter) {
                    operation.addParameter((Parameter) p1);
                }
            }

            if (apiAction == null || Utils.isBlank(apiAction.consumes())) {
                if (operationMethod.equals(ApiEnum.METHOD_GET)) {
                    operation.consumes(ApiEnum.CONSUMES_URLENCODED); //如果是 get ，则没有 content-type
                } else {
                    //operation.consumes(ApiEnum.CONSUMES_URLENCODED);
                    if (operation.getParameters().stream().anyMatch(parameter -> parameter instanceof BodyParameter)) {
                        operation.consumes(ApiEnum.CONSUMES_JSON);
                    } else {
                        operation.consumes(ApiEnum.CONSUMES_URLENCODED);
                    }
                }
            } else {
                operation.consumes(apiAction.consumes());
            }

            operation.produces(apiAction == null || Utils.isBlank(apiAction.produces()) ? ApiEnum.PRODUCES_DEFAULT : apiAction.produces());

            operation.setOperationId(operationMethod + "_" + pathKey.replace("/", "_"));

            path.set(operationMethod, operation);
        }
    }

    /**
     * 解析action 参数文档
     */
    private List<Parameter> parseActionParameters(ActionHolder actionHolder) {
        // 获取参数信息
        Map<String, ParamHolder> actionParamMap = new LinkedHashMap<>();

        MethodWrap method = actionHolder.action().method();
        MethodJavadoc methodJavadoc = RuntimeJavadoc.getJavadoc(method.getMethod());
        // 将方法参数存储到map

        for (ParamWrap p1 : method.getParamWraps()) {
            actionParamMap.put(p1.spec().getName(), new ParamHolder(p1));
        }

        // 获取参数注解信息
        {
            // 将参数注解信息绑定到参数
            List<ApiImplicitParam> apiParams = new ArrayList<>();
            if (actionHolder.isAnnotationPresent(ApiImplicitParams.class)) {
                apiParams.addAll(Arrays.asList(actionHolder.getAnnotation(ApiImplicitParams.class).value()));
            }

            if (actionHolder.isAnnotationPresent(ApiImplicitParams.class)) {
                ApiImplicitParam[] paramArray = actionHolder.getAnnotationsByType(ApiImplicitParam.class);
                apiParams.addAll(Arrays.asList(paramArray));
            }

            // 循环处理参数注解
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

            // 多个参数
            if (paramHolder.allowMultiple()) {
                System.out.println("paramSchema" + paramSchema);
                if (Utils.isNotEmpty(paramSchema)) {
                    //array model
                    BodyParameter modelParameter = new BodyParameter();
                    modelParameter.setSchema(new ArrayModel().items(new RefProperty(paramSchema)));
                    if (paramHolder.getParam() != null && paramHolder.getParam().spec().isRequiredBody() == false) {
                        modelParameter.setIn(ApiEnum.PARAM_TYPE_QUERY);
                    }
                    modelParameter.setDescription("我试试");
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
                    } else if (paramHolder.isRequiredBody()) {
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
                    // model
                    if (paramHolder.isRequiredBody() || paramHolder.getParam() == null) {
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
                    // query参数
                    if (paramHolder.isRequiredHeader()) {
                        parameter = new HeaderParameter();
                    } else if (paramHolder.isRequiredCookie()) {
                        parameter = new CookieParameter();
                    } else if (paramHolder.isRequiredPath()) {
                        parameter = new PathParameter();
                    } else if (paramHolder.isRequiredBody()) {
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

        return mergeBodyParamList(actionHolder, paramList);
    }

    private List<Parameter> mergeBodyParamList(ActionHolder actionHolder, List<Parameter> paramList) {
        ArrayList<Parameter> parameters = new ArrayList<>();
        if (Constants.CONTENT_TYPE_JSON_TYPE.equals(actionHolder.action().consumes()) || paramList.stream().map(Parameter::getIn).anyMatch(in -> in.equals(ApiEnum.PARAM_TYPE_BODY))) {
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


    /**
     * 按字段解析操作参数
     *
     * @param paramHolder 参数保持器
     * @param paramList   参数列表
     */
    private void parseActionParametersByFields(ParamHolder paramHolder, List<Parameter> paramList) {

        //做为 字段
        ClassEggg ce = paramHolder.getParam().getParamEggg().getTypeEggg().getClassEggg();
        for (FieldEggg fe : ce.getAllFieldEgggs()) {
            if (fe.isTransient()) {
                continue;
            }

            QueryParameter parameter = new QueryParameter();

            if (Collection.class.isAssignableFrom(fe.getType())) {
                parameter.setType(ApiEnum.RES_ARRAY);
            } else if (Map.class.isAssignableFrom(fe.getType())) {
                parameter.setType(ApiEnum.RES_OBJECT);
            } else {
                parameter.setType(fe.getType().getSimpleName());
            }

            ApiModelProperty anno = fe.getField().getAnnotation(ApiModelProperty.class);
            FieldJavadoc fieldJavadoc = RuntimeJavadoc.getJavadoc(fe.getField());

            String description = format(fieldJavadoc.getComment());
            parameter.setDescription(description);
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
                parameter.setName(fe.getName());
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

        ClassJavadoc classJavadoc = RuntimeJavadoc.getJavadoc(clazz);
        String comment = format(classJavadoc.getComment());

        // 2.创建模型
        ApiModel apiModel = clazz.getAnnotation(ApiModel.class);
        String title;
        if (apiModel != null) {
            title = apiModel.description();
        } else {
            title = StrUtil.isBlank(comment) ? modelName : comment;
        }

        Map<String, Property> fieldList = new LinkedHashMap<>();


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
        ClassEggg ce = EgggUtil.getClassEggg(clazz);
        for (FieldEggg fe : ce.getAllFieldEgggs()) {
            if (Modifier.isStatic(fe.getField().getModifiers())) {
                //静态的跳过
                continue;
            }

            FieldJavadoc fieldJavadoc = RuntimeJavadoc.getJavadoc(fe.getField());
            ApiModelProperty apiField = fe.getField().getAnnotation(ApiModelProperty.class);

            // 隐藏的跳过
            if (apiField != null && apiField.hidden()) {
                continue;
            }

            // 类字段注释
            String fieldComment = format(fieldJavadoc.getComment());
            // 第一行注释作为标题
            String fieldTitle = StrUtil.isBlank(fieldComment) ? "" : IoUtil.readLines(new StringReader(fieldComment), new ArrayList<>()).get(0);

            Class<?> typeClazz = fe.getType();
            Type typeGenericType = fe.getGenericType();


            // List<Class> 类型
            if (Collection.class.isAssignableFrom(typeClazz)) {
                // 如果是List类型，得到其Generic的类型
                if (typeGenericType == null) {
                    continue;
                }

                // 如果是泛型参数的类型
                if (typeGenericType instanceof ParameterizedType) {
                    ArrayProperty fieldPr = new ArrayProperty();
                    fieldPr.setTitle(fieldTitle);
                    fieldPr.setDescription(fieldComment);

                    if (apiField != null) {
                        fieldPr.setDescription(apiField.value());
                        fieldPr.setRequired(apiField.required());
                        // 如果是泛型参数的类型 加上 示例，在knife4j下将无法正确解析，所以将其注释
                        // fieldPr.setExample(apiField.example());
                    }

                    ParameterizedType pt = (ParameterizedType) typeGenericType;
                    //得到泛型里的class类型对象
                    Type itemClazz = pt.getActualTypeArguments()[0];

                    if (itemClazz instanceof ParameterizedType) {
                        itemClazz = ((ParameterizedType) itemClazz).getRawType();
                    }

                    if (itemClazz instanceof Class) {
                        if (itemClazz.equals(type)) {
                            //避免出现循环依赖，然后 oom
                            RefProperty itemPr = new RefProperty(modelName, RefFormat.INTERNAL);
                            itemPr.setTitle(fieldTitle);
                            fieldPr.setDescription(fieldComment);
                            fieldPr.setItems(itemPr);
                        } else {
                            Property itemPr = getPrimitiveProperty((Class<?>) itemClazz);
                            if (itemPr != null) {
                                itemPr.setTitle(fieldTitle);
                                itemPr.setDescription(fieldComment);
                                fieldPr.setItems(itemPr);
                            } else {
                                ModelImpl swaggerModel = (ModelImpl) this.parseSwaggerModel((Class<?>) itemClazz, itemClazz);

                                itemPr = new RefProperty(swaggerModel.getName(), RefFormat.INTERNAL);
                                fieldPr.setItems(itemPr);
                            }
                        }
                    }

                    fieldList.put(fe.getName(), fieldPr);
                }
                continue;
            }


            if (BuilderHelper.isModel(typeClazz)) {
                if (typeClazz.equals(type)) {
                    //避免出现循环依赖，然后 oom
                    RefProperty fieldPr = new RefProperty(modelName, RefFormat.INTERNAL);
                    fieldPr.setTitle(fieldTitle);
                    fieldPr.setDescription(fieldComment);
                    if (apiField != null) {
                        fieldPr.setDescription(apiField.value());
                        fieldPr.setRequired(apiField.required());
                        fieldPr.setExample(apiField.example());
                    }


                    fieldList.put(fe.getName(), fieldPr);
                } else {
                    ModelImpl swaggerModel = (ModelImpl) this.parseSwaggerModel(typeClazz, typeGenericType);

                    RefProperty fieldPr = new RefProperty(swaggerModel.getName(), RefFormat.INTERNAL);
                    fieldPr.setTitle(fieldTitle);
                    fieldPr.setDescription(fieldComment);
                    if (apiField != null) {
                        fieldPr.setDescription(apiField.value());
                        fieldPr.setRequired(apiField.required());
                        fieldPr.setExample(apiField.example());
                    }

                    fieldList.put(fe.getName(), fieldPr);
                }
            } else {
                ObjectProperty fieldPr = new ObjectProperty();
                fieldPr.setName(fe.getName());
                fieldPr.setTitle(fieldTitle);
                fieldPr.setDescription(fieldComment);
                if (apiField != null) {
                    fieldPr.setDescription(apiField.value());
                    fieldPr.setRequired(apiField.required());
                    fieldPr.setExample(apiField.example());
                    fieldPr.setType(Utils.isBlank(apiField.dataType()) ? typeClazz.getSimpleName().toLowerCase() : apiField.dataType());
                } else {
                    fieldPr.setType(typeClazz.getSimpleName().toLowerCase());
                }

                fieldList.put(fe.getName(), fieldPr);
            }
        }

        model.setProperties(fieldList);
        return model;
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

            // 泛型参数的类型
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

    private static String format(Comment c) {
        return formatter.format(c);
    }
}
