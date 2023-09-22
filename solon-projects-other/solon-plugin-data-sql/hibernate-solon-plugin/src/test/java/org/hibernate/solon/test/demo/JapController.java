package org.hibernate.solon.test.demo;

import org.hibernate.solon.annotation.Db;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.data.annotation.Tran;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

@Mapping("jpa")
@Controller
public class JapController {
    @Db
    private EntityManagerFactory entityManagerFactory;

    private EntityManager getEntityManager() {
        return entityManagerFactory.createEntityManager();
    }

    @Tran// 任何数据库操作都要开启事务
    @Mapping("/t")
    public void t1() {
        HttpEntity entity = new HttpEntity();
        entity.setId(System.currentTimeMillis() + "");

        getEntityManager().persist(entity);

    }

    @Tran// 任何数据库操作都要开启事务
    @Mapping("/t2")
    public Object t2() {
        HttpEntity entity = new HttpEntity();
        entity.setId(System.currentTimeMillis() + "");

        return getEntityManager().find(HttpEntity.class, "1");
    }

    @Tran// 任何数据库操作都要开启事务
    @Mapping("/t3")
    public Object t3() {
        return getEntityManager()
                .createQuery("select e from HttpEntity e")
                .setMaxResults(1)
                .getResultList();
    }
}