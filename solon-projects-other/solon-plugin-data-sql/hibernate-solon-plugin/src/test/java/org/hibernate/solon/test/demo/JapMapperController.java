package org.hibernate.solon.test.demo;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.solon.annotation.Db;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.data.annotation.Tran;

import java.util.List;

@Controller
public class JapMapperController {
    @Db
    private SessionFactory sessionFactory;

    @Db
    HttpEntityDao httpEntityDao;

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    @Tran// 任何数据库操作都要开启事务
    @Mapping("/t")
    public void t1() {
        HttpEntity entity = new HttpEntity();
        entity.setId(System.currentTimeMillis() + "");
        httpEntityDao.saveOrUpdate(entity);
        System.out.println(entity);
        /*int update = getSession().createQuery(
                "update HttpEntity set updateTime=?1 where id='current'"
        ).setParameter(1, new Date()).executeUpdate();
        System.out.println(update);*/
    }

    @Tran// 任何数据库操作都要开启事务
    @Mapping("/t2")
    public Object t2() {
        HttpEntity entity = new HttpEntity();
        entity.setId(System.currentTimeMillis() + "");
        httpEntityDao.saveOrUpdate(entity);
        List list = getSession()
                .createQuery("select e from HttpEntity e where e.id=?1")
                .setParameter(1,entity.getId())
                .setMaxResults(1).list();
        return list;
    }

    @Tran// 任何数据库操作都要开启事务
    @Mapping("/t3")
    public Object t3() {
        List list = getSession()
                .createQuery("select e from HttpEntity e")
                .setMaxResults(1).list();
        return list;
    }
}