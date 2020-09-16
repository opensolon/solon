package org.noear.solon.extend.beetlsql;

import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.XEventListener;
import org.noear.solon.extend.beetlsql.DbManager;

import javax.sql.DataSource;

class DsEventListener implements XEventListener<BeanWrap> {

    @Override
    public void onEvent(BeanWrap bw) {
        if (bw.raw() instanceof DataSource) {
            DbManager.global().get(bw.name(), bw);
        }
    }
}
