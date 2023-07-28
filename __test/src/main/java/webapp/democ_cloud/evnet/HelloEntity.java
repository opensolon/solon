package webapp.democ_cloud.evnet;

import org.noear.solon.cloud.annotation.CloudEvent;
import org.noear.solon.cloud.eventplus.CloudEventEntity;

/**
 * @author noear 2021/11/5 created
 */
@CloudEvent("hello.demo.e")
public class HelloEntity implements CloudEventEntity {
    public String name;

    public HelloEntity(){

    }

    public HelloEntity(String name){
        this.name = name;
    }
}
