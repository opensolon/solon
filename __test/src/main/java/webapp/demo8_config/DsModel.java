package webapp.demo8_config;

import org.noear.solon.annotation.BindProps;
import org.noear.solon.annotation.Configuration;

/**
 * @author noear 2025/1/24 created
 */
@BindProps(prefix = "demo8.test")
@Configuration
public class DsModel {
    private String url;
    private String username;
    private String password;

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
