package webapp.demo0_bean.condition2;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Condition;
import org.noear.solon.annotation.Configuration;

/**
 * @author noear 2024/8/7 created
 */
@Configuration
public class ConditionConfig {
    @Condition(onBean = Condition2A.class)
    @Bean
    public ConditionBean11A ConditionBean11A() {
        return new ConditionBean11A();
    }

    @Condition(onBeanName = "Condition2A")
    @Bean
    public ConditionBean12A ConditionBean12A() {
        return new ConditionBean12A();
    }


    @Condition(onBean = Condition2B.class)
    @Bean
    public ConditionBean11B ConditionBean11B() {
        return new ConditionBean11B();
    }

    @Condition(onBeanName = "Condition2B")
    @Bean
    public ConditionBean12B ConditionBean12B() {
        return new ConditionBean12B();
    }
}
