package features;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.vault.annotation.VaultInject;

import java.util.Properties;

/**
 * @author noear 2022/7/5 created
 */
@Configuration
public class TestConfig {
    @Inject("${test.password1}")
    private String password1;

    @VaultInject("${test.password2}")
    private String password2;

    @Bean("db1")
    private Properties db1(@Inject("${test.db1}") Properties props){
        System.out.println(password1);
        System.out.println(password2);
        System.out.println(props);

        return props;
    }

    @Bean("db2")
    private Properties db2(@VaultInject("${test.db2}") Properties props){
        System.out.println(password1);
        System.out.println(password2);
        System.out.println(props);

        return props;
    }
}
