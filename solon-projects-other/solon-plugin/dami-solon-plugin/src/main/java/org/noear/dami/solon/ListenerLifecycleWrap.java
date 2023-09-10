package org.noear.dami.solon;

import org.noear.dami.Dami;
import org.noear.solon.core.bean.LifecycleBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 监听器的生命周期包装
 *
 * @author noear
 * @since 2.5
 */
public class ListenerLifecycleWrap implements LifecycleBean {
    List<ListenerRecord> listenerRecords = new ArrayList<>();

    public void add(String topicMapping, Object listener) {
        listenerRecords.add(new ListenerRecord(topicMapping, listener));
    }

    @Override
    public void start() throws Throwable {

    }

    @Override
    public void stop() throws Throwable {
        //停止时自动注销
        for (ListenerRecord r1 : listenerRecords) {
            Dami.api().unregisterListener(r1.getTopicMapping(), r1.getListenerObj());
        }
    }
}
