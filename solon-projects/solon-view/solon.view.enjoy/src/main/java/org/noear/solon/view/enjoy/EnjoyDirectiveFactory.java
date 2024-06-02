package org.noear.solon.view.enjoy;

import com.jfinal.template.Directive;
import com.jfinal.template.DirectiveFactory;
import org.noear.solon.core.BeanWrap;

/**
 * @author noear
 * @since 2.8
 */
public class EnjoyDirectiveFactory implements DirectiveFactory {
    private BeanWrap bw;

    public EnjoyDirectiveFactory(BeanWrap bw) {
        this.bw = bw;
    }

    @Override
    public Directive createDirective() throws Exception {
        return bw.get();
    }
}
