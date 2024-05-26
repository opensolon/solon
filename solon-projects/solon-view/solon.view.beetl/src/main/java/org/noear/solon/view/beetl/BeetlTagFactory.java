package org.noear.solon.view.beetl;

import org.beetl.core.tag.Tag;
import org.beetl.core.tag.TagFactory;
import org.noear.solon.core.BeanWrap;

/**
 * @author noear
 * @since 2.8
 */
public class BeetlTagFactory implements TagFactory {
    private final BeanWrap bw;

    public BeetlTagFactory(BeanWrap bw) {
        this.bw = bw;
    }

    @Override
    public Tag createTag() {
        return bw.get();
    }
}
