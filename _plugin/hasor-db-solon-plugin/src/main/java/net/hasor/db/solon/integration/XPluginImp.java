package net.hasor.db.solon.integration;

import net.hasor.db.dal.session.BaseMapper;
import net.hasor.db.dal.session.DalSession;
import net.hasor.db.lambda.core.LambdaTemplate;
import net.hasor.db.solon.Db;
import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.core.Aop;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.VarHolder;

import net.hasor.db.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * @author noear
 * @since 1.6
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {

        Aop.context().beanInjectorAdd(Db.class, (varH, anno) -> {
            if (Utils.isEmpty(anno.value())) {
                Aop.getAsyn(DataSource.class, (dsBw) -> {
                    inject0(varH, dsBw);
                });
            } else {
                Aop.getAsyn(anno.value(), (dsBw) -> {
                    if (dsBw.raw() instanceof DataSource) {
                        inject0(varH, dsBw);
                    }
                });
            }
        });
    }

    private void inject0(VarHolder varH, BeanWrap dsBw) {
        try {
            inject1(varH, dsBw);
        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable e) {
            throw new IllegalStateException(e);
        }
    }

    private void inject1(VarHolder varH, BeanWrap dsBw) throws SQLException {
        DataSource ds = dsBw.get();
        Class<?> clz = varH.getType();

        //@Db("db1") JdbcTemplate ;
        if(JdbcTemplate.class.isAssignableFrom(varH.getType())){
            varH.setValue(new JdbcTemplate(ds));
            return;
        }

        //@Db("db1") LambdaTemplate ;
        if(LambdaTemplate.class.isAssignableFrom(varH.getType())){
            varH.setValue(new LambdaTemplate(ds));
            return;
        }

        //@Db("db1") DalSession ;
        if(DalSession.class.isAssignableFrom(varH.getType())){
            varH.setValue(new DalSession(ds));
            return;
        }

        //@Db("db1") UserMapper ;
        if (varH.getType().isInterface()) {
            DalSession session = new DalSession(ds);
            if (clz == BaseMapper.class) {
                Object obj = session.createBaseMapper((Class<?>) varH.getGenericType().getActualTypeArguments()[0]);
                varH.setValue(obj);
            }else {
                Object mapper = session.createMapper(varH.getType());
                varH.setValue(mapper);
            }
            return;
        }
    }
}
