package demo.openapi2;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Produces;
import org.noear.solon.core.handle.Context;
import org.noear.solon.docs.openapi2.OpenApi2Utils;

import java.io.IOException;

@Controller
public class OpenApi2Controller {
    /**
     * swagger 获取分组信息
     */
    @Produces("application/json; charset=utf-8")
    @Mapping("swagger-resources")
    public String resources() throws IOException {
        return OpenApi2Utils.getApiGroupResourceJson();
    }

    /**
     * swagger 获取分组接口数据
     */
    @Produces("application/json; charset=utf-8")
    @Mapping("swagger/v2")
    public String api(Context ctx, String group) throws IOException {
        return OpenApi2Utils.getApiJson(ctx, group);
    }
}
