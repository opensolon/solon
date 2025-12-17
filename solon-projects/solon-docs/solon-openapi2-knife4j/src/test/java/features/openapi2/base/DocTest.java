package features.openapi2.base;

import demo.openapi2.base.App;
import io.swagger.models.Scheme;
import org.junit.jupiter.api.Test;
import org.noear.solon.docs.DocDocket;
import org.noear.solon.docs.openapi2.OpenApi2Utils;
import org.noear.solon.test.SolonTest;

/**
 *
 * @author noear 2025/12/17 created
 *
 */
@SolonTest(App.class)
public class DocTest {
    @Test
    public void test() throws Exception {
        DocDocket docDocket = new DocDocket()
                .groupName("app端接口")
                .schemes(Scheme.HTTP.toValue())
                .apis("demo.openapi2.base");

        String json = OpenApi2Utils.getSwaggerJson(docDocket, "app端接口");

        System.out.println(json);
    }
}
