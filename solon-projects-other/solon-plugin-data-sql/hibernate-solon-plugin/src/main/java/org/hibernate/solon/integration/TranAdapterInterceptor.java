package org.hibernate.solon.integration;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.solon.annotation.HibernateTran;
import org.noear.solon.core.aspect.Interceptor;
import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.data.tran.TranListener;
import org.noear.solon.data.tran.TranUtils;

/**
 * HibernateTran 拦截器
 * @author bai
 * @date 2023/10/18
 */
public class TranAdapterInterceptor implements Interceptor {


    @Override
    public Object doIntercept(Invocation inv) throws Throwable {
        HibernateTran hibernateTran = inv.getMethodAnnotation(HibernateTran.class);
        if (hibernateTran == null){
            return inv.invoke();
        }
        HibernateAdapter db = HibernateAdapterManager.getOnly(hibernateTran.name());

        final SessionFactory sessionFactory = db.getSessionFactory();
        Session session = sessionFactory.getCurrentSession();
        Transaction transaction = session.getTransaction();
        transaction.begin();

        TranUtils.listen(new TranListener() {
            @Override
            public void afterCommit() {
                Session session = sessionFactory.getCurrentSession();
                Transaction transaction = session.getTransaction();
                TranListener.super.afterCommit();
                transaction.commit();
            }
        });
        return inv.invoke();
    }
}
