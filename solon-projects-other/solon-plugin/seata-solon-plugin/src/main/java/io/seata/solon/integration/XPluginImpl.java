package io.seata.solon.integration;

import io.seata.rm.GlobalLockTemplate;
import io.seata.solon.annotation.SeataLock;
import io.seata.solon.annotation.SeataLockInterceptor;
import io.seata.solon.annotation.SeataTran;
import io.seata.solon.annotation.SeataTranInterceptor;
import io.seata.tm.api.TransactionalTemplate;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;

/**
 * @author noear
 * @since 2.4
 */
public class XPluginImpl implements Plugin {
    @Override
    public void start(AopContext context) throws Throwable {
        GlobalLockTemplate globalLockTemplate = new GlobalLockTemplate();
        TransactionalTemplate transactionalTemplate = new TransactionalTemplate();

        context.wrapAndPut(GlobalLockTemplate.class, globalLockTemplate);
        context.wrapAndPut(TransactionalTemplate.class, transactionalTemplate);

        context.beanInterceptorAdd(SeataLock.class, new SeataLockInterceptor(globalLockTemplate));
        context.beanInterceptorAdd(SeataTran.class, new SeataTranInterceptor(transactionalTemplate));
    }
}
