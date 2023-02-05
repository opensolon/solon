package webapp.dso;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Condition;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

/**
 * @author noear 2023/2/5 created
 */
@Configuration
public class ConditionConfig {
    String username;
    String username2;
    String username3;

    public String getUsername() {
        return username;
    }

    public String getUsername2() {
        return username2;
    }

    public String getUsername3() {
        return username3;
    }

    @Condition(hasProperty = "${username}")
    @Bean
    public void setUsername(@Inject("${username}") String u1){
        username = u1;
    }

    @Condition(hasProperty = "${username2}")
    @Bean
    public void setUsername2(@Inject("${username2}") String u2){
        username2 = u2;
    }

    @Condition(hasProperty = "${username} = noear")
    @Bean
    public void setUsername3(@Inject("${username}") String u1){
        username3 = u1;
    }
}
