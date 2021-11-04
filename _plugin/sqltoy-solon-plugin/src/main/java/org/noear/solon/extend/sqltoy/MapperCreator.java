package org.noear.solon.extend.sqltoy;

import org.noear.solon.Utils;
import org.noear.solon.core.Aop;
import org.noear.solon.core.BeanBuilder;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.extend.sqltoy.annotation.Mapper;
import org.sagacity.sqltoy.SqlToyContext;
import org.sagacity.sqltoy.dao.SqlToyLazyDao;
import org.sagacity.sqltoy.dao.impl.SqlToyLazyDaoImpl;

import javax.sql.DataSource;

/**
 * @author 夜の孤城
 * @since 1.5
 * */
class MapperCreator implements BeanBuilder<Mapper> {
    @Override
    public void doBuild(Class<?> clz, BeanWrap bw, Mapper anno) throws Exception {
        if (clz.isInterface() == false) {
            return;
        }
        if (Utils.isEmpty(anno.value())) {
            if(Aop.has(SqlToyLazyDao.class)){
                Aop.getAsyn(SqlToyLazyDao.class, (dsBw) -> {
                    create(clz, dsBw.get());
                });
            }else if(Aop.has(DataSource.class)){
                SqlToyLazyDaoImpl sqlToyLazyDao=new SqlToyLazyDaoImpl();
                SqlToyContext sqlToyContext = Aop.get(SqlToyContext.class);
                sqlToyLazyDao.setSqlToyContext(sqlToyContext);
                create(clz, sqlToyLazyDao);
            }

        } else {
            Aop.getAsyn(anno.value(), (dsBw) -> {
                if (dsBw.raw() instanceof SqlToyLazyDao) {
                    create(clz, dsBw.get());
                } else if(dsBw.raw() instanceof DataSource){
                    SqlToyLazyDaoImpl sqlToyLazyDao=new SqlToyLazyDaoImpl();
                    SqlToyContext sqlToyContext = Aop.get(SqlToyContext.class);
                    sqlToyLazyDao.setSqlToyContext(sqlToyContext);
                    sqlToyLazyDao.setDataSource(dsBw.raw());
                    create(clz, sqlToyLazyDao);
                }
            });
        }
    }
    private void create(Class<?> clz,SqlToyLazyDao dao){
       // dsBw.get();
       Object proxy= MapperUtil.proxy(clz,dao);
       Aop.wrapAndPut(clz,proxy);
    }
}
