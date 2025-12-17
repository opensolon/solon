package demo.openapi2.javadoc;

import io.swagger.models.Scheme;
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
                .schemes(Scheme.HTTP.toValue())
                .apis("demo.openapi2.javadoc");

    }
}
