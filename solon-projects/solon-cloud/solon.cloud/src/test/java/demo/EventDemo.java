package demo;

import org.noear.solon.cloud.CloudClient;
import org.noear.solon.cloud.model.Event;
import org.noear.solon.cloud.model.EventTran;
import org.noear.solon.data.annotation.Tran;
import org.noear.solon.data.tran.TranUtils;

/**
 * @author noear 2024/5/14 created
 */
public class EventDemo {
    public void event_only() {
        EventTran eventTran = CloudClient.event().newTransaction();

        try {
            CloudClient.event().publish(new Event("demo.event", "test1").tran(eventTran));
            CloudClient.event().publish(new Event("demo.event", "test2").tran(eventTran));
            CloudClient.event().publish(new Event("demo.event", "test3").tran(eventTran));

            eventTran.commit();
        } catch (Throwable ex) {
            eventTran.rollback();
        }
    }

    @Tran
    public void event_and_jdbc() {
        EventTran eventTran = CloudClient.event().newTransaction();
        TranUtils.listen(eventTran); //通过监听器，交由 @Tran 管理


        CloudClient.event().publish(new Event("demo.event", "test1").tran(eventTran));
        CloudClient.event().publish(new Event("demo.event", "test2").tran(eventTran));
        CloudClient.event().publish(new Event("demo.event", "test3").tran(eventTran));
    }
}