package org.dromara.sms4j.solon;

import org.dromara.sms4j.comm.config.SmsSqlConfig;
import org.dromara.sms4j.comm.factory.BeanFactory;
import org.dromara.sms4j.comm.utils.JDBCTool;
import org.dromara.sms4j.comm.utils.SmsUtil;
import org.dromara.sms4j.core.SupplierSqlConfig;
import org.noear.solon.core.Props;

class SupplierSqlBuilder {
	
	private final Props props;
	
	SupplierSqlBuilder(Props props){
		this.props = props;
	}
	
	/**
	 * 
	 * @param smsSqlConfig 尝鲜版，先备用
	 */
	void init(SmsSqlConfig smsSqlConfig) {
		JDBCTool jdbc = BeanFactory.getJDBCTool();
		SmsUtil.copyBean(new JDBCTool(smsSqlConfig), jdbc);
		
		new SupplierSqlConfig();
	}

}
