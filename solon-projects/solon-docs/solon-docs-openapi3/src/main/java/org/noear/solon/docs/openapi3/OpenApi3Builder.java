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
package org.noear.solon.docs.openapi3;


import io.swagger.solon.annotation.ApiNoAuthorize;
import io.swagger.solon.annotation.ApiRes;
import io.swagger.solon.annotation.ApiResProperty;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.noear.eggg.TypeEggg;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.handle.Action;
import org.noear.solon.core.handle.Gateway;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.core.handle.UploadedFile;
import org.noear.solon.core.route.Routing;
import org.noear.solon.core.route.VersionedTarget;
import org.noear.solon.core.util.EgggUtil;
import org.noear.solon.core.util.GenericUtil;
import org.noear.solon.core.wrap.ParamWrap;
import org.noear.solon.docs.DocDocket;
import org.noear.solon.docs.exception.DocException;
import org.noear.solon.docs.models.ApiContact;
import org.noear.solon.docs.models.ApiLicense;
import org.noear.solon.docs.openapi3.impl.ActionHolder;
import org.noear.solon.docs.openapi3.impl.BuilderHelper;
import org.noear.solon.docs.openapi3.impl.ParamHolder;
import org.noear.solon.lang.NonNull;
import org.noear.solon.validation.annotation.NotBlank;
import org.noear.solon.validation.annotation.NotEmpty;
import org.noear.solon.validation.annotation.NotNull;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * openapi v3 json builder
 *
 * @author ingrun
 * @author noear
 * @since 3.8.1
 */
public class OpenApi3Builder {
    private final OpenAPI openAPI = new OpenAPI();
    private final DocDocket docket;

    public OpenApi3Builder(DocDocket docket) {
        this.docket = docket;
    }

    public OpenAPI build() {
        // 初始化 components
        openAPI.setComponents(new Components().schemas(new LinkedHashMap<>()));
        openAPI.setPaths(new Paths());

        // 解析通用返回
        if (docket.globalResult() != null) {
            // OpenAPI 3 中处理全局返回模型
            this.parseSchema(docket.globalResult(), docket.globalResult());
        }

        // 解析API
        this.parseGroupPackage();

        ApiLicense apiLicense = docket.info().license();
        ApiContact apiContact = docket.info().contact();

        openAPI.info(new Info()
                .title(docket.info().title())
                .description(docket.info().description())
                .termsOfService(docket.info().termsOfService())
                .version(docket.info().version()));

        if (apiLicense != null) {
            License license = new License()
                    .url(apiLicense.url())
                    .name(apiLicense.name());
            license.setExtensions(apiLicense.vendorExtensions());

            openAPI.getInfo().setLicense(license);
        }

        if (apiContact != null) {
            Contact contact = new Contact()
                    .email(apiContact.email())
                    .name(apiContact.name())
                    .url(apiContact.url());
            contact.setExtensions(apiContact.vendorExtensions());
            openAPI.getInfo().setContact(contact);
        }

        // 添加服务器配置
        Server server = new Server();
        server.url(BuilderHelper.getHost(docket));
        openAPI.addServersItem(server);

        if (docket.externalDocs() != null) {
            ExternalDocumentation externalDocumentation = new ExternalDocumentation();
            externalDocumentation.setDescription(docket.externalDocs().description());
            externalDocumentation.setUrl(docket.externalDocs().url());
            openAPI.setExternalDocs(externalDocumentation);
        }

        // 添加安全定义
        if (docket.securityExtensions() != null) {
            for (Map.Entry<String, Object> entry : docket.securityExtensions().entrySet()) {
                if (entry.getValue() instanceof SecurityScheme) {
                    SecurityScheme securityScheme = (SecurityScheme) entry.getValue();
                    if (openAPI.getComponents().getSecuritySchemes() == null) {
                        openAPI.getComponents().setSecuritySchemes(new LinkedHashMap<>());
                    }
                    openAPI.getComponents().addSecuritySchemes(entry.getKey(), securityScheme);
                }
            }
        }

        // 按tag name去重
        List<io.swagger.v3.oas.models.tags.Tag> tags = openAPI.getTags();
        if (tags != null) {
            openAPI.setTags(tags.stream().collect(Collectors.groupingBy(io.swagger.v3.oas.models.tags.Tag::getName)).
                    values().stream().map(i -> i.get(0)).collect(Collectors.toList()));
        }

        return openAPI;
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

        Collection<Routing<Handler>> routingCollection = Solon.app().router().findAll();
        for (Routing<Handler> routing : routingCollection) {
            for (VersionedTarget<Handler> vt : routing.targets()) {
                if (vt.getTarget() instanceof Action) {
                    //如果是 Action
                    resolveAction(apiMap, routing, (Action) vt.getTarget());
                }

                if (vt.getTarget() instanceof Gateway) {
                    //如果是 Gateway (网关)
                    for (Routing<Handler> routing2 : ((Gateway) vt.getTarget()).getMainRouting().getAll()) {
                        for (VersionedTarget<Handler> vt2 : routing2.targets()) {
                            if (vt2.getTarget() instanceof Action) {
                                resolveAction(apiMap, routing2, (Action) vt2.getTarget());
                            }
                        }
                    }
                }
            }
        }

        return apiMap;
    }

    private void resolveAction(Map<Class<?>, List<ActionHolder>> apiMap, Routing<Handler> routing, Action action) {
        Class<?> controller = action.controller().clz();

        boolean matched = docket.apis().stream().anyMatch(res -> res.test(action));
        if (!matched) {
            return;
        }

        ActionHolder actionHolder = new ActionHolder(routing, action);

        if (apiMap.containsKey(controller)) {
            if (action.method().isAnnotationPresent(Operation.class)) {
                List<ActionHolder> actionHolders = apiMap.get(controller);
                if (!actionHolders.contains(actionHolder)) {
                    actionHolders.add(actionHolder);
                    apiMap.put(controller, actionHolders);
                }
            }
        } else {
            if (controller.isAnnotationPresent(Tag.class)) {
                if (action.method().isAnnotationPresent(Operation.class)) {
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
        Tag api = clazz.getAnnotation(Tag.class);

        if (api != null && Utils.isNotEmpty(api.name())) {
            io.swagger.v3.oas.models.tags.Tag tag = new io.swagger.v3.oas.models.tags.Tag();
            tag.setName(api.name());
            tag.setDescription(api.description());
            openAPI.addTagsItem(tag);
        }

        // 解析action
        this.parseAction(actionHolders);
    }

    /**
     * 解析action
     */
    private void parseAction(List<ActionHolder> actionHolders) {
        for (ActionHolder actionHolder : actionHolders) {

            Operation apiAction = actionHolder.getAnnotation(Operation.class);

            if (apiAction.hidden()) {
                continue;
            }

//            String controllerKey = BuilderHelper.getControllerKey(actionHolder.controllerClz());
//            String actionName = actionHolder.action().name();
//            Method actionMethod = actionHolder.action().method().getMethod();

            Set<String> actionTags = actionHolder.getTags(apiAction);


            String pathKey = actionHolder.routing().path();

            PathItem path = openAPI.getPaths().get(pathKey);
            if (path == null) {
                //path 要重复可用
                path = new PathItem();
                openAPI.path(pathKey, path);
            }

            io.swagger.v3.oas.models.Operation operation = new io.swagger.v3.oas.models.Operation();

            operation.setTags(new ArrayList<>(actionTags));
            operation.setSummary(apiAction.summary());
            operation.setDescription(apiAction.description());
            operation.setDeprecated(actionHolder.isAnnotationPresent(Deprecated.class));

            String operationMethod = BuilderHelper.getHttpMethod(actionHolder, apiAction);

            // 设置请求体
            List<Parameter> parameters = this.parseActionParameters(actionHolder);
            operation.setParameters(parameters);

            // 设置响应
//            operation.setResponses(this.parseActionResponse(controllerKey, actionName, actionMethod));
            operation.setResponses(this.parseActionResponse(actionHolder));

            if (Utils.isBlank(apiAction.operationId())) {
                operation.setOperationId(operationMethod + "_" + pathKey.replace("/", "_"));
            } else {
                operation.setOperationId(apiAction.operationId());
            }

            // 设置安全需求
            if (!(actionHolder.isAnnotationPresent(ApiNoAuthorize.class) ||
                    actionHolder.controllerClz().isAnnotationPresent(ApiNoAuthorize.class))) {
                // 如果没有ApiNoAuthorize注解，则添加安全需求
                if (docket.securityExtensions() != null && !docket.securityExtensions().isEmpty()) {
                    for (String securityName : docket.securityExtensions().keySet()) {
                        SecurityRequirement securityRequirement = new SecurityRequirement();
                        securityRequirement.addList(securityName);
                        operation.addSecurityItem(securityRequirement);
                    }
                }
            }

            // 反射
            Class<PathItem> pathItemClass = PathItem.class;
            try {
                pathItemClass.getDeclaredMethod(operationMethod.toLowerCase(), io.swagger.v3.oas.models.Operation.class).invoke(path, operation);
            } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException ignore) {
                path.addExtension("x-" + operationMethod.toLowerCase(), operation);
            }

        }
    }

    /**
     * 解析action 参数文档
     */
    private List<Parameter> parseActionParameters(ActionHolder actionHolder) {
        Map<String, ParamHolder> actionParamMap = new LinkedHashMap<>();
        for (ParamWrap p1 : actionHolder.action().method().getParamWraps()) {
            ParamHolder paramHolder = new ParamHolder(p1);
            paramHolder.binding(paramHolder.getParam().getParameter().getAnnotation(io.swagger.v3.oas.annotations.Parameter.class));
            actionParamMap.put(p1.getName(), paramHolder);
        }

        // 获取参数注解信息
        List<io.swagger.v3.oas.annotations.Parameter> apiParams = new ArrayList<>();
        if (actionHolder.isAnnotationPresent(io.swagger.v3.oas.annotations.Parameter.class)) {
            apiParams.add(actionHolder.getAnnotation(io.swagger.v3.oas.annotations.Parameter.class));
        }

        if (actionHolder.isAnnotationPresent(Parameters.class)) {
            io.swagger.v3.oas.annotations.Parameter[] paramArray = actionHolder.getAnnotationsByType(io.swagger.v3.oas.annotations.Parameter.class);
            apiParams.addAll(Arrays.asList(paramArray));
        }

        for (io.swagger.v3.oas.annotations.Parameter a1 : apiParams) {
            ParamHolder paramHolder = actionParamMap.computeIfAbsent(a1.name(), k -> new ParamHolder(null));
            paramHolder.binding(a1);
        }

        // 构建参数列表
        List<Parameter> paramList = new ArrayList<>();

        for (ParamHolder paramHolder : actionParamMap.values()) {
            if (paramHolder.isIgnore()) {
                continue;
            }

            String paramSchema = this.getParameterSchema(paramHolder);
            String dataType = paramHolder.dataType();

            Parameter parameter = new Parameter();

            parameter.setName(paramHolder.getName());
            parameter.setRequired(paramHolder.isRequired());
            parameter.setIn(paramHolder.paramType());

            // 获取参数描述信息，优先使用ParamHolder中的注解信息，否则尝试从参数本身获取
            if (paramHolder.getAnno() != null) {
                parameter.setDescription(paramHolder.getDescription());
            } else {
                io.swagger.v3.oas.annotations.Parameter annotation = paramHolder.getParam().getParameter().getAnnotation(io.swagger.v3.oas.annotations.Parameter.class);
                if (null != annotation) {
                    parameter.setDescription(annotation.description());
                } else {
                    // 从 schema 中获取参数描述信息
                    Class<?> dataTypeClass = paramHolder.getParam().getTypeEggg().getType();
                    Type dataGenericType = paramHolder.getParam().getTypeEggg().getGenericType();
                    String schemaName = this.getSchemaName(dataTypeClass, dataGenericType);
                    Schema<?> schema = openAPI.getComponents().getSchemas().get(schemaName);
                    if (schema != null) {
                        parameter.setDescription(schema.getTitle());
                    }
                }
            }

            if (paramHolder.allowMultiple()) {
                ArraySchema arraySchema = new ArraySchema();
                if (Utils.isNotEmpty(paramSchema)) {
                    Schema itemSchema = new Schema<>();
                    itemSchema.set$ref("#/components/schemas/" + paramSchema);
                    arraySchema.setItems(itemSchema);
                } else {
                    Schema itemSchema = getSchemaByType(dataType);
                    arraySchema.setItems(itemSchema);
                }
                parameter.setSchema(arraySchema);
            } else {
                if (Utils.isNotEmpty(paramSchema)) {
                    Schema schema = new Schema<>();
                    schema.set$ref("#/components/schemas/" + paramSchema);
                    parameter.setSchema(schema);
                } else {
                    Schema schema = getSchemaByType(dataType);
                    if ("file".equals(dataType)) {
                        schema = new FileSchema();
                    }
                    parameter.setSchema(schema);
                }
            }

            paramList.add(parameter);
        }

        return paramList;
    }

    /**
     * 根据数据类型获取Schema
     */
    private Schema<?> getSchemaByType(String dataType) {
        if (dataType == null) {
            return new StringSchema();
        }

        switch (dataType.toLowerCase()) {
            case "integer":
                return new IntegerSchema();
            case "number":
                return new NumberSchema();
            case "boolean":
                return new BooleanSchema();
            case "string":
                return new StringSchema();
            case "array":
            case "list":
                return new ArraySchema();
            case "object":
                return new ObjectSchema();
            case "file":
                return new FileSchema();
            case "date":
                return new DateSchema();
            case "date-time":
                return new DateTimeSchema();
            default:
                return new StringSchema();
        }
    }


    /**
     * 解析action 返回文档
     */
    private ApiResponses parseActionResponse(String controllerKey, String actionName, Method method) {
        ApiResponses responses = new ApiResponses();


        docket.globalResponseCodes().forEach((key, value) -> {
            ApiResponse response = new ApiResponse().description(value);

            if (key == 200) {
                String schema = this.parseResponse(controllerKey, actionName, method);
                if (schema != null) {
                    response.content(new Content()
                            .addMediaType("application/json",
                                    new MediaType()
                                            .schema(new Schema<>().$ref("#/components/schemas/" + schema))));
                    response.description("OK");
                }
            }

            responses.addApiResponse(String.valueOf(key), response);
        });

        // 如果没有定义200响应，至少添加一个基本响应
        if (!responses.containsKey("200")) {
            responses.addApiResponse("200", new ApiResponse().description("OK"));
        }

        return responses;

    }

    private ApiResponses parseActionResponse(ActionHolder actionHolder) {
        ApiResponses responses = new ApiResponses();

        Class<?> returnType = actionHolder.action().method().getReturnType();
        Type type = actionHolder.action().method().getGenericReturnType();

        String produces = StringUtils.getOrDefault(actionHolder.action().produces(), "application/json");
        String schema = this.parseSchema(returnType, type);

        if (schema != null) {
            ApiResponse okResponse = new ApiResponse()
                    .description("OK")
                    .content(new Content().addMediaType(produces,
                            new MediaType()
                                    .schema(this.generSchema(actionHolder.action().method().getReturnTypeEggg())
                                    )
                    ));
            responses.addApiResponse("200", okResponse);
        }

        // 如果没有定义200响应，至少添加一个基本响应
        if (!responses.containsKey("200")) {
            responses.addApiResponse("200", new ApiResponse().description("OK"));
        }

        return responses;
    }


    private Schema<?> generSchema(TypeEggg typeEggg) {
        Class<?> returnType = typeEggg.getType();
        String schemaName = null;
        if (!BuilderHelper.isModel(returnType) && typeEggg.getGenericType() instanceof ParameterizedType) {
            Schema<?> schemaByType = this.getSchemaByType(typeEggg.getType().getSimpleName());
            Map<String, Type> genericInfo = typeEggg.getGenericInfo();
            schemaByType.items(this.generSchema(EgggUtil.getTypeEggg(genericInfo.values().stream().findFirst().orElse(null))));
            return schemaByType;
        } else if (typeEggg.getGenericType() instanceof ParameterizedType) {
            schemaName = parseSchema(returnType, typeEggg.getGenericType());
        } else {
            schemaName = parseSchema(returnType, typeEggg.getType());
        }

        Schema<Object> schemaByType = new Schema<>();
        schemaByType.$ref("#/components/schemas/" + schemaName);
        return schemaByType;
    }


    /**
     * 解析返回值
     */
    private String parseResponse(String controllerKey, String actionName, Method method) {
        // schema 引用
        String schemaName = null;

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
        if (apiResClz != Void.class && apiResClz != void.class) {
            if (BuilderHelper.isModel(apiResClz)) {
                try {
                    return this.parseSchema(apiResClz, method.getGenericReturnType());
                } catch (Exception e) {
                    String hint = method.getDeclaringClass().getName() + ":" + method.getName() + "->" + apiResClz.getSimpleName();
                    throw new DocException("Response model parsing failure: " + hint, e);
                }
            }
        }

        if (responses.isEmpty() && docket.globalResult() != null) {
            schemaName = this.parseSchema(docket.globalResult(), docket.globalResult());
        } else {
            // 将参数放入commonRes中,作为新的schema引用
            schemaName = this.parseSchema(controllerKey, actionName, responses);
        }

        return schemaName;
    }

    private String getSchemaName(Class<?> clazz, Type type) {
        String schemaName = BuilderHelper.getModelName(clazz, type);
        io.swagger.v3.oas.annotations.media.Schema apiModel = clazz.getAnnotation(io.swagger.v3.oas.annotations.media.Schema.class);
        if (apiModel != null && Utils.isNotEmpty(apiModel.name())) {

            if (schemaName.contains("«")) {
                String baseName = BuilderHelper.getModelName(clazz, clazz);
                schemaName = schemaName.replace(baseName, apiModel.name());
            } else {
                schemaName = apiModel.name();
            }

        }

        return schemaName;
    }

    /**
     * 将class解析为schema
     */
    private String parseSchema(Class<?> clazz, Type type) {

        io.swagger.v3.oas.annotations.media.Schema apiModel = clazz.getAnnotation(io.swagger.v3.oas.annotations.media.Schema.class);
        String schemaName = this.getSchemaName(clazz, type);

        // 1.已存在,不重复解析
        if (openAPI.getComponents().getSchemas().containsKey(schemaName)) {
            return schemaName;
        }

        // 2.创建模型
        String title;
        if (apiModel != null && Utils.isNotEmpty(apiModel.description())) {
            title = apiModel.description();
        } else {
            title = schemaName;
        }

        Schema<Object> schema = new Schema<>();
        schema.setName(schemaName);
        schema.setTitle(title);
        schema.setType("object");

        if (!Collection.class.isAssignableFrom(clazz)) {
            openAPI.getComponents().addSchemas(schemaName, schema);
        }

        if (clazz.isEnum()) {
            schema.setType("string");
            // 设置枚举值
            Object[] enumConstants = clazz.getEnumConstants();
            if (enumConstants != null) {
                List<Object> enumValues = new ArrayList<>();
                for (Object enumConstant : enumConstants) {
                    enumValues.add(enumConstant.toString());
                }
                schema.setEnum(enumValues);
            }
            return schemaName;
        }

        // 3.完成模型解析 - 获取所有字段并添加属性
        List<Field> fields = new ArrayList<>();
        Class<?> currentClass = clazz;

        // 收集当前类及其所有父类的字段
        while (currentClass != null && currentClass != Object.class) {
            fields.addAll(Arrays.asList(currentClass.getDeclaredFields()));
            currentClass = currentClass.getSuperclass();
        }


        Map<String, Schema> properties = new LinkedHashMap<>();
        List<String> required = new ArrayList<>();
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())) {
                //静态的跳过
                continue;
            }


            // model 的字段注解  Schema
            io.swagger.v3.oas.annotations.media.Schema apiField = field.getAnnotation(io.swagger.v3.oas.annotations.media.Schema.class);

            // 添加字段必填项
            this.handSchemaRequiredField(required, field, apiField);


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
                if (typeGenericType instanceof ParameterizedType) {
                    ArraySchema fieldSchema = new ArraySchema();
                    if (apiField != null) {
                        fieldSchema.setDescription(apiField.description());
                    }

                    ParameterizedType pt = (ParameterizedType) typeGenericType;
                    //得到泛型里的class类型对象
                    Type itemClazz = pt.getActualTypeArguments()[0];

                    if (itemClazz instanceof ParameterizedType) {
                        itemClazz = ((ParameterizedType) itemClazz).getRawType();
                    }

                    if (itemClazz instanceof TypeVariable) {
                        Map<String, Type> genericMap = GenericUtil.getGenericInfo(type);
                        Type itemClazz2 = genericMap.get(itemClazz.getTypeName());
                        if (itemClazz2 instanceof Class) {
                            itemClazz = itemClazz2;
                        }
                    }

                    if (itemClazz instanceof Class) {
                        Schema<?> itemSchema = new Schema<>();
                        if (itemClazz.equals(type)) {
                            //避免出现循环依赖，然后 oom
                            itemSchema.set$ref("#/components/schemas/" + schemaName);
                            itemSchema.setDescription(title);
                            fieldSchema.setItems(itemSchema);
                        } else {
                            String itemSchemaName = this.parseSchema((Class<?>) itemClazz, itemClazz);
                            itemSchema = openAPI.getComponents().getSchemas().get(itemSchemaName);
                            fieldSchema.setItems(itemSchema);
                            fieldSchema.description(StringUtils.getOrDefault(itemSchema.getDescription(), itemSchema.getTitle()));
                        }
                        if (fieldSchema.getDescription() == null) {
                            fieldSchema.setDescription(itemSchema.getDescription());
                        }
                    }

                    properties.put(field.getName(), fieldSchema);
                }
                continue;
            }

            if (BuilderHelper.isModel(typeClazz)) {
                if (typeClazz.equals(type)) {
                    //避免出现循环依赖，然后 oom
                    Schema fieldSchema = new Schema<>();
                    fieldSchema.set$ref("#/components/schemas/" + schemaName);
                    if (apiField != null) {
                        fieldSchema.setDescription(apiField.description());
                    }

                    properties.put(field.getName(), fieldSchema);
                } else {
                    String refSchemaName = this.parseSchema(typeClazz, typeGenericType);
                    Schema fieldSchema = new Schema<>();
                    fieldSchema.set$ref("#/components/schemas/" + refSchemaName);
                    if (apiField != null) {
                        fieldSchema.setDescription(apiField.description());
                    }

                    properties.put(field.getName(), fieldSchema);

                }
            } else {
                Schema fieldSchema = getSchemaByType(typeClazz.getSimpleName());
                fieldSchema.setName(field.getName());

                if (apiField != null) {
                    fieldSchema.setDescription(apiField.description());
                    fieldSchema.setType(Utils.isBlank(apiField.type()) ? typeClazz.getSimpleName().toLowerCase() : apiField.type());
                    fieldSchema.setExample(apiField.example());
                } else {
                    fieldSchema.setType(typeClazz.getSimpleName().toLowerCase());
                }

                properties.put(field.getName(), fieldSchema);
            }
        }

        schema.setProperties(properties);
        schema.setRequired(required);
        return schemaName;
    }

    private void handSchemaRequiredField(List<String> required, Field field, io.swagger.v3.oas.annotations.media.Schema apiField) {
        if (apiField == null) return;
        io.swagger.v3.oas.annotations.media.Schema.RequiredMode requiredMode = apiField.requiredMode();

        if (io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED.equals(requiredMode)) {
            required.add(field.getName());
        } else if (io.swagger.v3.oas.annotations.media.Schema.RequiredMode.AUTO.equals(requiredMode)) {
            //  RequiredMode.AUTO： @NotNull、@NonNull、@NotBlank、@NotEmpty）→ 必填
            NotNull notNull = field.getAnnotation(NotNull.class);
            NotBlank notBlank = field.getAnnotation(NotBlank.class);
            NotEmpty notEmpty = field.getAnnotation(NotEmpty.class);
            NonNull nonNull = field.getAnnotation(NonNull.class);
            if (notNull != null || notBlank != null || notEmpty != null || nonNull != null) {
                required.add(field.getName());
            }
        }
    }

    /**
     * 将action response解析为schema
     */
    private String parseSchema(String controllerKey, String actionName, List<ApiResProperty> responses) {
        final String schemaName = controllerKey + "_" + actionName;


        Map<String, Schema> propertiesList = new LinkedHashMap<>();

        Schema schema = new Schema<>();
        schema.setName(schemaName);

        // 添加到components
        if (openAPI.getComponents() == null) {
            openAPI.setComponents(new Components());
        }
        if (openAPI.getComponents().getSchemas() == null) {
            openAPI.getComponents().setSchemas(new LinkedHashMap<>());
        }
        openAPI.getComponents().addSchemas(schemaName, schema);

        for (ApiResProperty apiResponse : responses) {
            if (apiResponse.dataTypeClass() != Void.class) {
                String schemaRef = this.parseSchema(apiResponse.dataTypeClass(), apiResponse.dataTypeClass());

                if (apiResponse.allowMultiple()) {
                    ArraySchema propSchema = new ArraySchema();
                    propSchema.setName(schemaRef);
                    propSchema.setDescription(apiResponse.value());

                    Schema itemSchema = new Schema<>();
                    itemSchema.set$ref("#/components/schemas/" + schemaRef);
                    propSchema.setItems(itemSchema);

                    propertiesList.put(apiResponse.name(), propSchema);
                } else {
                    Schema propSchema = new Schema<>();
                    propSchema.set$ref("#/components/schemas/" + schemaRef);
                    propSchema.setDescription(apiResponse.value());

                    propertiesList.put(apiResponse.name(), propSchema);
                }
            } else {
                if (apiResponse.allowMultiple()) {
                    ArraySchema propSchema = new ArraySchema();

                    propSchema.setName(apiResponse.name());
                    propSchema.setDescription(apiResponse.value());

                    Schema itemsSchema = new StringSchema();
                    itemsSchema.setType(Utils.isBlank(apiResponse.dataType()) ? "string" : apiResponse.dataType());
                    propSchema.setItems(itemsSchema);

                    propertiesList.put(apiResponse.name(), propSchema);
                } else {
                    Schema propSchema = new StringSchema();

                    propSchema.setName(apiResponse.name());
                    propSchema.setDescription(apiResponse.value());
                    propSchema.setType(Utils.isBlank(apiResponse.dataType()) ? "string" : apiResponse.dataType());
                    propSchema.setExample(apiResponse.example());

                    propertiesList.put(apiResponse.name(), propSchema);
                }
            }
        }

        schema.setProperties(propertiesList);
        return schemaName;
    }

    /**
     * 解析对象参数
     */
    private String getParameterSchema(ParamHolder paramHolder) {
//        if (paramHolder.getAnno() != null) {
//            Class<?> dataTypeClass = null;
//            if (paramHolder.getParam() != null) {
//                dataTypeClass = paramHolder.getParam().getTypeEggg().getType();
//            }
//
//            if (null != dataTypeClass && dataTypeClass != Void.class) {
//                return this.parseSchema(dataTypeClass, dataTypeClass);
//            }
//        }

        if (paramHolder.getParam() != null) {
            Class<?> dataTypeClass = paramHolder.getParam().getTypeEggg().getType();
            if (dataTypeClass.isPrimitive()) {
                return null;
            }

            if (UploadedFile.class.equals(dataTypeClass)) {
                return null;
            }

            if (dataTypeClass.getName().startsWith("java.lang")) {
                return null;
            }

            Type dataGenericType = paramHolder.getParam().getTypeEggg().getGenericType();

            if (Collection.class.isAssignableFrom(dataTypeClass) && dataGenericType instanceof ParameterizedType) {
                Type itemType = ((ParameterizedType) dataGenericType).getActualTypeArguments()[0];

                if (itemType instanceof Class) {
                    return this.parseSchema((Class<?>) itemType, itemType);
                }
            }

            return this.parseSchema(dataTypeClass, dataGenericType);

        }

        return null;
    }
}