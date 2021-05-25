package org.noear.solon.extend.springboot;

import org.noear.solon.Solon;
import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;

/**
 * @author noear
 * @since 1.2
 */
public class ApplicationStartingSolon implements ApplicationListener<ApplicationStartingEvent>, ApplicationContextAware {
    static ApplicationContext context;

    @Override
    public void onApplicationEvent(ApplicationStartingEvent event) {
        if (event.getSource() instanceof SpringApplication) {
            SpringApplication sa = (SpringApplication) event.getSource();

            Solon.start(sa.getMainApplicationClass(), event.getArgs());
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        ApplicationStartingSolon.context = context;
    }

    public static ApplicationContext getContext(){
        return context;
    }
}
