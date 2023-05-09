package webapp.dso;

import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

/**
 * @author noear 2023/4/12 created
 */
@Inject(value = "${autorefresh}", autoRefreshed = true)
@Configuration
public class AutoConfigTest {
    private String username;

    public void setUsername(String username) {
        this.username = username;
        System.out.println("))))) " + username);
    }

    public String getUsername() {
        return username;
    }
}
