package webapp.democ_cloud.evnet;

import org.noear.solon.annotation.Component;
import org.noear.solon.cloud.eventplus.CloudEventSubscribe;

/**
 * @author noear 2021/11/5 created
 */
@Component
public class HelloEntitySubscribe {
    @CloudEventSubscribe
    public boolean hello(HelloEntity event){
        System.out.println("HelloEntitySubscribe....:: " + event.name);
        return true;
    }
}
