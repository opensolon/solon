package org.noear.solonfox.swagger2;

import io.swagger.models.Swagger;
import org.noear.snack.ONode;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;

@Controller
public class SwaggerController {
    @Mapping(value = "/swagger-resources", produces = "application/json")
    public String resources() {
        return new ONode().asArray().build(n -> {
            n.addNew().set("name", "应用接口")
                    .set("location", "/v2/api-docs")
                    .set("swaggerVersion", "2.0");
        }).toJson();
    }

    @Mapping("/v2/api-docs")
    public String api_docs(){
        return ONode.stringify(XPluginImp.swagger);
    }

    @Mapping("/v2/swagger.json")
    public String xx(){
        return ONode.stringify(XPluginImp.swagger);
    }
}
