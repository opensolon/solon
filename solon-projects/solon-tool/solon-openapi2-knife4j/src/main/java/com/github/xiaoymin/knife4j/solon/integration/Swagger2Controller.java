package com.github.xiaoymin.knife4j.solon.integration;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.models.Swagger;
import org.noear.solon.docs.DocDocket;

import org.noear.solon.Utils;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Produces;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.handle.*;
import org.noear.solon.docs.openapi2.BasicAuthUtils;
import org.noear.solon.docs.openapi2.Swagger2Builder;

/**
 * Swagger api Controller
 *
 * @author noear
 * @since 2.3
 */
public class Swagger2Controller {
    ObjectMapper mapper = new ObjectMapper();

    @Inject
    AopContext aopContext;

    public Swagger2Controller(){
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    /**
     * swagger 获取分组信息
     */
    @Produces("application/json; charset=utf-8")
    @Mapping("swagger-resources")
    public String resources() throws IOException{
        List<BeanWrap> list = aopContext.getWrapsOfType(DocDocket.class);

        List<Swagger2Resource> resourceList = list.stream().filter(bw -> Utils.isNotEmpty(bw.name()))
                .map(bw -> new Swagger2Resource(bw.name(), ((DocDocket) bw.raw()).groupName()))
                .collect(Collectors.toList());

        return mapper.writeValueAsString(resourceList);
    }

    /**
     * swagger 获取分组接口数据
     */
    @Produces("application/json; charset=utf-8")
    @Mapping("swagger/v2")
    public String api(Context ctx, String group) throws IOException {
        DocDocket docket = aopContext.getBean(group);

        if (docket == null) {
            return null;
        }

        if (!BasicAuthUtils.basicAuth(ctx, docket)) {
            BasicAuthUtils.response401(ctx);
            return null;
        }

        if (docket.globalResponseCodes().containsKey(200) == false) {
            docket.globalResponseCodes().put(200, "");
        }

        Swagger swagger = new Swagger2Builder(docket).build();
        return mapper.writeValueAsString(swagger);
    }
}