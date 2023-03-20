package org.noear.solon.cloud.impl;

import org.noear.solon.cloud.CloudJobHandler;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.handle.MethodHandler;

import java.lang.reflect.Method;

/**
 * CloubJob 方法运行器（支持非单例）
 *
 * @author noear
 * @since 2.2
 */
public class CloudJobMethod extends MethodHandler implements CloudJobHandler {
    /**
     * @param beanWrap Bean包装器
     * @param method   函数（外部要控制访问权限）
     */
    public CloudJobMethod(BeanWrap beanWrap, Method method) {
        super(beanWrap, method, true);
    }
}
