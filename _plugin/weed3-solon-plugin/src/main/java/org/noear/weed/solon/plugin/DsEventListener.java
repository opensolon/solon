package org.noear.weed.solon.plugin;

import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.event.EventListener;

import javax.sql.DataSource;

/**
 * @author noear
 * @since 1.3
 */
class DsEventListener implements EventListener<BeanWrap> {

    @Override
    public void onEvent(BeanWrap bw) {
        if (bw.raw() instanceof DataSource) {
            DbManager.global().reg(bw);
        }
    }
}
