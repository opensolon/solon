package webapp.demo3_water;

import org.noear.solon.extend.wateradpter.XWaterAdapter;


public class WaterAdpter extends XWaterAdapter {
    public WaterAdpter() {
        super();
    }

    @Override
    public String msg_subscriber_id() {
        return "e02d4b3ec5014a6bb7956d56acc4acf0";
    }

    @Override
    public String msg_signature_key() {
        return "x5NJ3jop1XZBDrZv";
    }

    @Override
    public String msg_receiver_url() {
        return "http://localhost:8080/msg/receive";
    }

    @Override
    public String alarm_mobile() {
        return "18658857337";
    }

    @Override
    public String service_name() {
        return "demo_water";
    }
}
