package features.solon.generic7;

import org.noear.solon.annotation.Managed;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

/**
 * @author noear 2025/6/3 created
 */
@Configuration
public class Config {
    @Managed(name = "loginKit", typed = true)
    public LoginKit<LoginUser> loginKit(@Inject ILoginUserFactory<LoginUser> loginUserFactory) {
        return new LoginKit<>(loginUserFactory);
    }

    @Managed
    public ILoginUserFactory<LoginUser> loginUserFactory() {
        return new ILoginUserFactory<LoginUser>() {
            @Override
            public LoginUser create() {
                return null;
            }
        };
    }
}
