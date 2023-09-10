package org.noear.solon.core;

import org.noear.dami.solon.ListenerLifecycleWrap;
import org.noear.dami.solon.annotation.Dami;
import org.noear.solon.Solon;

/**
 * TopicMapping 构建器
 *
 * @author noear
 * @since 2.5
 */
public class DamiTopicMappingBuilder implements BeanBuilder<Dami> {
    @Override
    public void doBuild(Class<?> clz, BeanWrap bw, Dami anno) throws Throwable {
        if (clz.isInterface()) {

            Object raw = org.noear.dami.Dami.api().createSender(anno.topicMapping(), clz);
            bw.rawSet(raw);
            bw.tagSet(anno.topicMapping());

        } else {

            org.noear.dami.Dami.api().registerListener(anno.topicMapping(), bw.raw());
            bw.tagSet(anno.topicMapping());

            lifecycleWrap(bw, anno);
        }
    }

    /**
     * 包装生命周期
     */
    private void lifecycleWrap(BeanWrap bw, Dami anno) {
        if (Solon.context() != bw.context()) {
            //如果不是根容器，则停止时自动注销
            ListenerLifecycleWrap lifecycleWrap = (ListenerLifecycleWrap) bw.context().getAttrs().get(ListenerLifecycleWrap.class);

            if (lifecycleWrap == null) {
                lifecycleWrap = new ListenerLifecycleWrap();
                bw.context().getAttrs().put(ListenerLifecycleWrap.class, lifecycleWrap);
                bw.context().lifecycle(lifecycleWrap);
            }

            lifecycleWrap.add(anno.topicMapping(), bw.raw());
        }
    }
}
