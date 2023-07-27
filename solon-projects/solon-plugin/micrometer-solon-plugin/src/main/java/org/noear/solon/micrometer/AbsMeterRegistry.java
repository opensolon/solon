package org.noear.solon.micrometer;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;

import java.util.ArrayList;
import java.util.List;

/**
 * abs计注册表
 *
 * @author bai
 * @date 2023/07/27
 */
public abstract class AbsMeterRegistry<T extends MeterRegistry> implements CommonMeterRegistry<T> {

    /**
     * 注册表
     */
    private final T meterRegistry;

    /**
     * 注册表列表
     */
    public static final List<AbsMeterRegistry<? extends MeterRegistry>> meterRegistryList = new ArrayList<>();

    /**
     * abs计注册表
     *
     * @param meterRegistry 计注册表
     */
    public AbsMeterRegistry(T meterRegistry){
        this.meterRegistry = meterRegistry;
        this.registry(meterRegistry);
        meterRegistryList.add(this);
    }

    /**
     * 刮
     * 注册器消息体内容
     *
     * @param t t
     * @return {@link String}
     */
    public abstract String scrape(T t);

    /**
     * 刮
     *
     * @return {@link String}
     */
    public String scrape(){
        return scrape(meterRegistry);
    }


    /**
     * 注册表
     *
     * @param meterRegistry 计注册表
     */
    @Override
    public void registry(MeterRegistry meterRegistry){
        // 全局注册
        Metrics.globalRegistry.add(meterRegistry);
    }

    /**
     * 得到计注册表
     *
     * @return {@link T}
     */
    public T getMeterRegistry(){
        return meterRegistry;
    }

}
