package demo1;

import org.dromara.sms4j.comm.enumerate.SupplierType;
import org.dromara.sms4j.core.factory.SmsFactory;
import org.noear.solon.annotation.Component;

@Component
public class DemoHuaweiSMSService implements DemoService{

	@Override
	public void handle() {
		//华为短信向此手机号发送短信
        SmsFactory.createSmsBlend(SupplierType.HUAWEI).sendMessage("16666666666","000000");
	}

}
