package org.noear.solon.view.enjoy;

import com.jfinal.template.source.ISource;
import com.jfinal.template.source.ISourceFactory;

/**
 * @author noear 2022/10/8 created
 */
class ClassPathSourceFactory2 implements ISourceFactory {
    private ClassLoader classLoader;

    public ClassPathSourceFactory2(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public ISource getSource(String baseTemplatePath, String fileName, String encoding) {
        return new ClassPathSource2(classLoader, baseTemplatePath, fileName, encoding);
    }
}
