package org.noear.solon.extend.springboot;

import org.noear.solon.Solon;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.context.ApplicationListener;

/**
 * @author noear 2021/1/2 created
 * @since 1.2
 */
public class ApplicationStartingSolon implements ApplicationListener<ApplicationStartingEvent> {
    @Override
    public void onApplicationEvent(ApplicationStartingEvent event) {
        Solon.start(event.getSource().getClass(), event.getArgs());
    }
}
