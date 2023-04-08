package com.drools.solon.config;



import static com.drools.solon.common.Constants.LISTENER_CLOSE;

import java.util.Objects;

import org.noear.solon.Utils;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Condition;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

import com.drools.solon.KieSchedule;
import com.drools.solon.KieTemplate;

/**
 * @author <a href="mailto:hongwen0928@outlook.com">Karas</a>
 * @date 2019/9/26
 * @since 1.0.0
 */
@Configuration
public class DroolsAutoConfiguration {

    @Bean
    @Condition(onMissingBean = KieTemplate.class)
    public KieTemplate kieTemplate(@Inject("${spring.drools}") DroolsProperties droolsProperties) {
        String path = droolsProperties.getPath();
        if(Utils.isBlank(path)){
            throw new IllegalArgumentException("Please set base path(spring.drools.path = xxx).");
        }
        KieTemplate kieTemplate = new KieTemplate();
        kieTemplate.setPath(droolsProperties.getPath());
        kieTemplate.setMode(droolsProperties.getMode());
        String autoUpdate = droolsProperties.getAutoUpdate();
        if (Objects.equals(LISTENER_CLOSE, autoUpdate)) {
            // 关闭自动更新
            kieTemplate.setUpdate(999999L);
        } else {
            // 启用自动更新
            kieTemplate.setUpdate(droolsProperties.getUpdate());
        }
        kieTemplate.setListener(droolsProperties.getListener());
        kieTemplate.setVerify(droolsProperties.getVerify());
        String charset = droolsProperties.getCharset();
        if (Utils.isNotBlank(charset)) {
            kieTemplate.setCharset(charset);
        }
        return kieTemplate;
    }

    @Bean
    @Condition(onMissingBean = KieSchedule.class)
    public KieSchedule kieSchedule(KieTemplate kieTemplate) {
        KieSchedule kieSchedule = new KieSchedule(kieTemplate);
        kieSchedule.execute();
        return kieSchedule;
    }

}
