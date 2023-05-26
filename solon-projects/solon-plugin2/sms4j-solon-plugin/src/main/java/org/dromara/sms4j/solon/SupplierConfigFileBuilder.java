package org.dromara.sms4j.solon;

import org.dromara.sms4j.aliyun.config.AlibabaConfig;
import org.dromara.sms4j.cloopen.config.CloopenConfig;
import org.dromara.sms4j.comm.config.SmsConfig;
import org.dromara.sms4j.comm.utils.SmsUtil;
import org.dromara.sms4j.core.config.SupplierFactory;
import org.dromara.sms4j.emay.config.EmayConfig;
import org.dromara.sms4j.huawei.config.HuaweiConfig;
import org.dromara.sms4j.jdcloud.config.JdCloudConfig;
import org.dromara.sms4j.tencent.config.TencentConfig;
import org.dromara.sms4j.unisms.config.UniConfig;
import org.dromara.sms4j.yunpian.config.YunpianConfig;
import org.noear.solon.core.Props;

class SupplierConfigFileBuilder {

	private final Props props;

//	private final AopContext context;

	SupplierConfigFileBuilder(Props props
//			, AopContext context
			) {
		this.props = props;
//		this.context = context;
	}

	/**
	 * @param smsConfig 尝鲜版，先备用
	 */
	void init(SmsConfig smsConfig) {
		alibaba();
        huawei();
        jingdong();
        tencent();
        uniSms();
        yunPian();
        cloopen();
        emay();
	}

	/**
	 * alibaba
	 * 
	 * @author :Wind
	 */
	void alibaba() {
		try {
			AlibabaConfig ac = props.getBean("sms.alibaba", AlibabaConfig.class);
			AlibabaConfig sfg = SupplierFactory.getAlibabaConfig();
			SmsUtil.copyBean(ac, sfg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * huawei
	 * 
	 * @author :Wind
	 */
	void huawei() {
		try {
			HuaweiConfig ac = props.getBean("sms.huawei", HuaweiConfig.class);
			HuaweiConfig sfg = SupplierFactory.getHuaweiConfig();
			SmsUtil.copyBean(ac, sfg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * jingdong
	 * 
	 * @author :Wind
	 */
	void jingdong() {
		try {
			YunpianConfig ac = props.getBean("sms.yunpian", YunpianConfig.class);
			YunpianConfig sfg = SupplierFactory.getYunpianConfig();
			SmsUtil.copyBean(ac, sfg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * tencent
	 * 
	 * @author :Wind
	 */
	void tencent() {
		try {
			UniConfig ac = props.getBean("sms.uni", UniConfig.class);
			UniConfig sfg = SupplierFactory.getUniConfig();
			SmsUtil.copyBean(ac, sfg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * uniSms
	 * <p>
	 * 数据库读取并设置合一短信配置
	 * 
	 * @author :Wind
	 */
	void uniSms() {
		try {
			TencentConfig ac = props.getBean("sms.tencent", TencentConfig.class);
			TencentConfig sfg = SupplierFactory.getTencentConfig();
			SmsUtil.copyBean(ac, sfg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * yunPian
	 * <p>
	 * 数据库读取并设置云片短信
	 * 
	 * @author :Wind
	 */
	void yunPian() {
		try {
			JdCloudConfig ac = props.getBean("sms.jdcloud", JdCloudConfig.class);
			JdCloudConfig sfg = SupplierFactory.getJdCloudConfig();
			SmsUtil.copyBean(ac, sfg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * cloopen
	 * 
	 * @author :Wind
	 */
	void cloopen() {
		try {
			CloopenConfig ac = props.getBean("sms.cloopen", CloopenConfig.class);
			CloopenConfig sfg = SupplierFactory.getCloopenConfig();
			SmsUtil.copyBean(ac, sfg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * emay
	 * 
	 * @author :Wind
	 */
	void emay() {
		try {
			EmayConfig ac = props.getBean("sms.emay", EmayConfig.class);
			EmayConfig sfg = SupplierFactory.getEmayConfig();
			SmsUtil.copyBean(ac, sfg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
