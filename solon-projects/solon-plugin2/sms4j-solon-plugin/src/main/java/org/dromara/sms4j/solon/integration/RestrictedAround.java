package org.dromara.sms4j.solon.integration;


import java.util.ArrayList;
import java.util.List;

import org.dromara.sms4j.comm.annotation.Restricted;
import org.dromara.sms4j.comm.config.SmsConfig;
import org.dromara.sms4j.comm.exception.SmsBlendException;
import org.dromara.sms4j.comm.utils.SmsUtil;
import org.dromara.sms4j.comm.utils.TimeExpiredPoolCache;
import org.noear.redisx.plus.RedisBucket;
import org.noear.solon.core.aspect.Interceptor;
import org.noear.solon.core.aspect.Invocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 针对注解 @see org.dromara.sms4j.comm.annotation.Restricted 的环绕处理
 * @author Administrator
 * 如果启用了短信限制，则注入AOP组件
 *
 */
public class RestrictedAround implements Interceptor{
	
	private static final Logger log = LoggerFactory.getLogger(RestrictedAround.class);

	private static final int minTimer = 60 * 1000;
    private static final int accTimer = 24 * 60 * 60 * 1000;
    private static final String REDIS_KEY = "sms:restricted:";
    
    private final SmsConfig config;
    private final RedisBucket redisBucket;
	
    public RestrictedAround(SmsConfig config,RedisBucket redisBucket) {
    	this.config = config;
    	this.redisBucket = redisBucket;
    }
    
	@Override
	public Object doIntercept(Invocation inv) throws Throwable {
		Restricted anno = inv.method().getAnnotation(Restricted.class);
		if(anno == null) {
			return inv.invoke();
		}
		String args = "";
        List<String> argsList = new ArrayList<>();
        try {
            args = (String) inv.args()[0];
        } catch (Exception e) {
        	log.error("sms4j aspect get args error",e);
            for (Object o : (ArrayList<?>) inv.args()[0]) {
                argsList.add((String) o);
            }
        }
        SmsBlendException process = redisProcess(args);
        if (process != null) {
            throw process;
        }
        argsList.forEach(f -> {
            SmsBlendException proce = null;
            try {
                proce = redisProcess(f);
            } catch (Exception e) {
                log.error(e.getMessage());
                throw new RuntimeException(e);
            }
            if (proce != null) {
                throw proce;
            }
        });
        return inv.invoke();
	}
	
	private SmsBlendException process(String args) throws Exception {
        TimeExpiredPoolCache instance = TimeExpiredPoolCache.getInstance();//缓存实例
        Integer accountMax = config.getAccountMax();//每日最大发送量
        Integer minuteMax = config.getMinuteMax();//每分钟最大发送量
        if (SmsUtil.isNotEmpty(accountMax)) {   //是否配置了每日限制
            Integer i = instance.get(args + "max");
            if (SmsUtil.isEmpty(i)) {
                instance.put(args + "max", 1, accTimer);
            } else if (i > accountMax) {
                log.info("The phone:"+args +",number of short messages reached the maximum today");
                return new SmsBlendException("The phone:"+args +",number of short messages reached the maximum today");
            } else {
                instance.put(args + "max", i + 1, accTimer);
            }
        }
        if (SmsUtil.isNotEmpty(minuteMax)) {  //是否配置了每分钟最大限制
            Integer o = instance.get(args);
            if (SmsUtil.isNotEmpty(o)) {
                if (o < minuteMax) {
                    instance.put(args, o + 1, minTimer);
                } else {
                    log.info("The phone:"+args +",number of short messages reached the maximum today");
                    return new SmsBlendException("The phone:", args + " Text messages are sent too often！");
                }
            } else {
                instance.put(args, 1, minTimer);
            }
        }
        return null;
    }

    private SmsBlendException redisProcess(String args) throws Exception{
        if (config.getRedisCache()==false || redisBucket == null){
            return process(args);
        }

        Integer accountMax = config.getAccountMax();//每日最大发送量
        Integer minuteMax = config.getMinuteMax();//每分钟最大发送量
        if (SmsUtil.isNotEmpty(accountMax)) {   //是否配置了每日限制
        	String rkey = REDIS_KEY+args + "max";
        	String amax = redisBucket.get(rkey);
            if (SmsUtil.isEmpty(amax)) {
            	redisBucket.store(rkey, "1", accTimer/1000);
            } else {
            	Integer i = Integer.parseInt(amax);
            	if (i > accountMax) {
                    log.info("The phone:"+args +",number of short messages reached the maximum today");
                    return new SmsBlendException("The phone:"+args +",number of short messages reached the maximum today");
                }
            	redisBucket.store(rkey, (i+1)+"", accTimer/1000);
            }
        }
        if (SmsUtil.isNotEmpty(minuteMax)) {  //是否配置了每分钟最大限制
        	String rkey = REDIS_KEY+args;
        	String rval = redisBucket.get(rkey);
            if (SmsUtil.isNotEmpty(rval)) {
            	Integer o = Integer.parseInt(rval);
                if (o < minuteMax) {
                	redisBucket.store(rkey, (o+1)+"", minTimer/1000);
                } else {
                    log.info("The phone:"+args +",number of short messages reached the maximum today");
                    return new SmsBlendException("The phone:", args + " Text messages are sent too often！");
                }
            } else {
            	redisBucket.store(rkey, "1", minTimer/1000);
            }
        }
        return null;
    }

}
