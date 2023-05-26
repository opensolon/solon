package org.dromara.sms4j.solon;

import java.util.concurrent.Executor;

import org.dromara.sms4j.comm.annotation.Restricted;
import org.dromara.sms4j.comm.config.SmsBanner;
import org.dromara.sms4j.comm.config.SmsConfig;
import org.dromara.sms4j.comm.config.SmsSqlConfig;
import org.dromara.sms4j.comm.delayedTime.DelayedTime;
import org.dromara.sms4j.comm.enumerate.ConfigType;
import org.dromara.sms4j.comm.factory.BeanFactory;
import org.dromara.sms4j.comm.utils.SmsUtil;
import org.dromara.sms4j.solon.integration.RestrictedAround;
import org.noear.redisx.RedisClient;
import org.noear.redisx.plus.RedisBucket;
import org.noear.solon.Solon;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.Props;
import org.noear.solon.core.event.AppLoadEndEvent;
import org.noear.solon.core.util.LogUtil;

public class XPluginImp implements Plugin {

	@Override
	public void start(AopContext context) throws Throwable {
		Props props = context.cfg();
		SmsSqlConfig smsSqlConfig = BeanFactory.getSmsSqlConfig();
		SmsConfig smsConfig = BeanFactory.getSmsConfig();

		SmsUtil.copyBean(props.getBean("sms.sql", SmsSqlConfig.class), smsSqlConfig);
		SmsUtil.copyBean(props.getBean("sms", SmsConfig.class), smsConfig);

		context.wrapAndPut(SmsSqlConfig.class, smsSqlConfig); // TODO 是否有必要注入进来？
		context.wrapAndPut(SmsConfig.class, smsConfig); // TODO 是否有必要注入进来？

		context.wrapAndPut(DelayedTime.class, BeanFactory.getDelayedTime());/** 注入一个定时器 */

		Executor taskExecutor = BeanFactory.setExecutor(smsConfig);/** 注入线程池 */
		BeanWrap bw = context.wrap(Executor.class, taskExecutor);
		context.putWrap("smsExecutor", bw);

		String configType = props.getProperty("sms.config-type", ConfigType.CONFIG_FILE.getName());
		if (configType.equals(ConfigType.CONFIG_FILE.getName()) || configType.equals("config-file")
				|| configType.equals("config_file")) {
			SupplierConfigFileBuilder sf = new SupplierConfigFileBuilder(props);
			sf.init(smsConfig);
		} else if (configType.equals(ConfigType.SQL_CONFIG.getName()) || configType.equals("sql-config")
				|| configType.equals("sql_config")) {
			SupplierSqlBuilder ss = new SupplierSqlBuilder(props);
			ss.init(smsSqlConfig);
		}
		boolean useRedis = smsConfig.getRedisCache();
		boolean aopRestricted = smsConfig.getRestricted();
		/* 如果配置中启用了redis，则注入redis工具 */
		if (useRedis) {
			LogUtil.global().debug("The redis cache is enabled for sms-aggregation");
		}
//		if (aopRestricted) {
//			LogUtil.global().debug("SMS restriction is enabled");
//			if(useRedis) {
//				context.subBeansOfType(RedisClient.class, rc->{
//					RestrictedAround ra = new RestrictedAround(smsConfig,rc.getBucket());
//					context.beanAroundAdd(Restricted.class, ra);
//				});
//			}else {
//				RedisBucket rb = null;
//				RestrictedAround ra = new RestrictedAround(smsConfig,rb);
//				context.beanAroundAdd(Restricted.class, ra);
//			}
//		}

		if (smsConfig.getIsPrint()) {
			SmsBanner.PrintBanner("V 1.0.5");
		}

		// 应用加载完之后
		Solon.app().onEvent(AppLoadEndEvent.class, e -> {
			if (aopRestricted) {
				LogUtil.global().debug("SMS restriction is enabled");
				if (useRedis) {
					e.context().subBeansOfType(RedisClient.class, rc -> {
						RestrictedAround ra = new RestrictedAround(smsConfig, rc.getBucket());
						e.context().beanAroundAdd(Restricted.class, ra);
					});
				} else {
					RedisBucket rb = null;
					RestrictedAround ra = new RestrictedAround(smsConfig, rb);
					e.context().beanAroundAdd(Restricted.class, ra);
				}
			}
		});

	}
}
