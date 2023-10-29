package org.noear.solon.test.aot;

import org.noear.solon.aot.RuntimeNativeMetadata;
import org.noear.solon.aot.SolonAotProcessor;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.BeanWrap;

/**
 * @author songyinyin
 * @since 2023/10/23 16:20
 */
public class SolonAotTestProcessor extends SolonAotProcessor {
    public SolonAotTestProcessor(Class<?> applicationClass) {
        super(null, null, applicationClass);
    }

    public void process(AppContext appContext) {
        RuntimeNativeMetadata metadata = genRuntimeNativeMetadata(appContext);

        // 注册到 bean 容器，方便后续断言使用
        BeanWrap beanWrap = new BeanWrap(appContext, metadata.getClass(), metadata);
        appContext.beanRegister(beanWrap, RuntimeNativeMetadata.class.getName(), true);
    }
}
