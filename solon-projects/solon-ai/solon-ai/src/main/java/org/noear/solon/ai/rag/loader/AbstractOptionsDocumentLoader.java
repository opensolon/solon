package org.noear.solon.ai.rag.loader;

import java.util.function.Consumer;

/**
 * 虚拟带选项的文档加载器
 *
 * @author noear
 * @since 3.1
 */
public abstract class AbstractOptionsDocumentLoader<Opt,Slf extends AbstractOptionsDocumentLoader> extends AbstractDocumentLoader {
    protected Opt options;

    /**
     * 配置选项
     */
    public Slf options(Opt options) {
        if (options != null) {
            this.options = options;
        }
        return (Slf) this;
    }

    /**
     * 配置选项
     */
    public Slf options(Consumer<Opt> optionsBuilder) {
        optionsBuilder.accept(this.options);
        return (Slf) this;
    }
}
