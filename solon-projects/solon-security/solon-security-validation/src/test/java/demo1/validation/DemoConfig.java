package demo1.validation;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.validation.ValidatorFailureHandler;
import org.noear.solon.validation.ValidatorFailureHandlerI18n;

@Configuration
public class DemoConfig {
    /**
     * 支持：message="{aaa} bb {ccc.ddd}"
     * */
    @Bean
    public ValidatorFailureHandler validatorFailureHandler() {
        return new ValidatorFailureHandlerI18n(2048);
    }
}
