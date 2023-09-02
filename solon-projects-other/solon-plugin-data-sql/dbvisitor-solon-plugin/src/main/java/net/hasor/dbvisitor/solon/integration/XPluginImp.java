package net.hasor.dbvisitor.solon.integration;

import net.hasor.dbvisitor.dal.mapper.BaseMapper;
import net.hasor.dbvisitor.dal.repository.DalRegistry;
import net.hasor.dbvisitor.dal.repository.RefMapper;
import net.hasor.dbvisitor.dal.session.DalSession;
import net.hasor.dbvisitor.jdbc.core.JdbcTemplate;
import net.hasor.dbvisitor.lambda.LambdaTemplate;
import net.hasor.dbvisitor.solon.Db;
import org.noear.solon.Utils;
import org.noear.solon.core.*;

import javax.sql.DataSource;

/**
 * @author noear
 * @since 1.8
 */
public class XPluginImp implements Plugin {
    DalRegistry dalRegistry = new DalRegistry();

    @Override
    public void start(AppContext context) {
        context.beanInjectorAdd(Db.class, (varH, anno) -> {
            if (Utils.isEmpty(anno.value())) {
                varH.context().getWrapAsync(DataSource.class, (dsBw) -> {
                    inject0(varH, dsBw);
                });
            } else {
                varH.context().getWrapAsync(anno.value(), (dsBw) -> {
                    if (dsBw.raw() instanceof DataSource) {
                        inject0(varH, dsBw);
                    }
                });
            }
        });

        context.beanBuilderAdd(RefMapper.class, (clz, bw, anno) -> {
            dalRegistry.loadMapper(clz);
        });
    }

    private void inject0(VarHolder varH, BeanWrap dsBw) {
        try {
            inject1(varH, dsBw);
        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private void inject1(VarHolder varH, BeanWrap dsBw) throws Exception {
        DataSource ds = dsBw.get();
        Class<?> clz = varH.getType();

        //@Db("db1") LambdaTemplate ; //顺序别乱变
        if (LambdaTemplate.class.isAssignableFrom(varH.getType())) {
            LambdaTemplate accessor = new LambdaTemplate(new DynamicConnectionImpl(ds));

            varH.setValue(accessor);
            return;
        }

        //@Db("db1") JdbcTemplate ;
        if (JdbcTemplate.class.isAssignableFrom(varH.getType())) {
            JdbcTemplate accessor = new JdbcTemplate(new DynamicConnectionImpl(ds));

            varH.setValue(accessor);
            return;
        }

        //@Db("db1") DalSession ;
        if (DalSession.class.isAssignableFrom(varH.getType())) {
            DalSession accessor = new DalSession(new DynamicConnectionImpl(ds));

            varH.setValue(accessor);
            return;
        }

        //@Db("db1") UserMapper ;
        if (varH.getType().isInterface()) {
            DalSession accessor = new DalSession(new DynamicConnectionImpl(ds), dalRegistry);

            if (clz == BaseMapper.class) {
                Object obj = accessor.createBaseMapper((Class<?>) varH.getGenericType().getActualTypeArguments()[0]);
                varH.setValue(obj);
            } else {
                Object mapper = accessor.createMapper(varH.getType());
                varH.setValue(mapper);
            }
            return;
        }
    }
}
