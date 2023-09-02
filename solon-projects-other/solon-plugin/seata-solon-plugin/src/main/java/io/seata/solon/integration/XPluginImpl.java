package io.seata.solon.integration;

import io.seata.rm.GlobalLockTemplate;
import io.seata.solon.annotation.GlobalLock;
import io.seata.solon.impl.GlobalLockInterceptor;
import io.seata.solon.annotation.GlobalTransactional;
import io.seata.solon.impl.GlobalTransactionalInterceptor;
import io.seata.tm.api.TransactionalTemplate;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;

/**
 * @author noear
 * @since 2.4
 */
public class XPluginImpl implements Plugin {
    @Override
    public void start(AppContext context) throws Throwable {
        GlobalLockTemplate globalLockTemplate = new GlobalLockTemplate();
        TransactionalTemplate transactionalTemplate = new TransactionalTemplate();

        context.wrapAndPut(GlobalLockTemplate.class, globalLockTemplate);
        context.wrapAndPut(TransactionalTemplate.class, transactionalTemplate);

        context.beanInterceptorAdd(GlobalLock.class, new GlobalLockInterceptor(globalLockTemplate));
        context.beanInterceptorAdd(GlobalTransactional.class, new GlobalTransactionalInterceptor(transactionalTemplate));
    }
}
