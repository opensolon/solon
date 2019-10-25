package webapp.demo3_water;

import org.noear.water.WaterClient;
import org.noear.solon.annotation.XBean;
import org.noear.solon.extend.wateradpter.XMessageHandler;

@XBean("msg:T.T.2")
public class Msg_t_t_2 implements XMessageHandler {
    @Override
    public boolean handler(WaterClient.MessageModel msg) throws Exception {
        return false;
    }
}
