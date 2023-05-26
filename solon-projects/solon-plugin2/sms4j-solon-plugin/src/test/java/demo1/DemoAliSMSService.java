package demo1;

import org.dromara.sms4j.aliyun.config.AlibabaConfig;
import org.dromara.sms4j.comm.enumerate.SupplierType;
import org.dromara.sms4j.core.config.SupplierFactory;
import org.dromara.sms4j.core.factory.SmsFactory;
import org.noear.solon.annotation.Component;

@Component
public class DemoAliSMSService implements DemoService{

	public void handle() {
		AlibabaConfig sfg = SupplierFactory.getAlibabaConfig();
		//阿里云向此手机号发送短信
        SmsFactory.createSmsBlend(SupplierType.ALIBABA).sendMessage("18888888888","123456");
	}
	
}
