package demo.openapi3.base;

import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Managed;
import org.noear.solon.docs.DocDocket;

@Configuration
public class Config {
    /**
     * 简单点的
     */
    @Managed("appApi")
    public DocDocket appApi() {
        return new DocDocket()
                .groupName("app端接口")
                .apis("demo.openapi3.base");

    }
}
