package org.noear.weed.solon.plugin;

import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.XEventListener;

import javax.sql.DataSource;

 class DsEventListener implements XEventListener<BeanWrap> {

    @Override
    public void onEvent(BeanWrap bw) {
        if (bw.raw() instanceof DataSource) {
            DbManager.global().reg(bw);
        }
    }
}
