package org.noear.solon.extend.mybatis;

import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.core.*;
import org.apache.ibatis.ext.solon.Db;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {

        loadPlugins();

        app.onEvent(BeanWrap.class, new DsEventListener());

        Aop.context().beanBuilderAdd(Db.class, (clz, wrap, anno) -> {
            if (clz.isInterface() == false) {
                return;
            }

            if (Utils.isEmpty(anno.value())) {
                Aop.getAsyn(DataSource.class, (dsBw) -> {
                    create0(clz, dsBw);
                });
            } else {
                Aop.getAsyn(anno.value(), (dsBw) -> {
                    if (dsBw.raw() instanceof DataSource) {
                        create0(clz, dsBw);
                    }
                });
            }
        });

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

    protected static List<Interceptor> pluginList = new ArrayList<>();
    private void loadPlugins(){
        int index = 0;
        while (true) {
            Props props = Solon.cfg().getProp("mybatis.plugin["+index+"]");
            if(props.size() == 0){
                break;
            }else{
                index++;

                String name = null;
                for(Map.Entry kv : props.entrySet()){
                    if(kv.getKey() instanceof String) {
                        String key = (String) kv.getKey();
                        if (key.endsWith(".class")) {
                            name = key.split("\\.")[0];
                        }
                    }
                }

                if(name != null){
                    props = props.getProp(name);
                    Interceptor plugin = Utils.newInstance(props.get("class"));
                    if(plugin == null){
                        throw new IllegalArgumentException("Mybatis.plugin["+name+"].class load failed");
                    }
                    props.remove("class");

                    plugin.setProperties(props);
                    pluginList.add(plugin);
                }
            }
        }
    }

    private void create0(Class<?> clz, BeanWrap dsBw) {
        SqlSessionProxy holder = SqlSessionManager.global().get(dsBw);

        Object raw = holder.getMapper(clz);
        Aop.wrapAndPut(clz,raw);
    }

    private void inject0(VarHolder varH, BeanWrap dsBw) {
        SqlSessionProxy holder = SqlSessionManager.global().get(dsBw);

        if (SqlSession.class.isAssignableFrom(varH.getType())) {
            varH.setValue(holder);
            return;
        }

        if (SqlSessionFactory.class.isAssignableFrom(varH.getType())) {
            varH.setValue(holder.getFactory());
            return;
        }

        if (varH.getType().isInterface()) {
            Object mapper = holder.getMapper(varH.getType());

            varH.setValue(mapper);
            return;
        }
    }
}
