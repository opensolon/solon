package org.noear.solon.data.sqlink.plugin.builder;


import org.noear.solon.data.sqlink.base.toBean.beancreator.AbsBeanCreator;
import org.noear.solon.data.sqlink.base.toBean.beancreator.BeanCreatorFactory;

public class AotBeanCreatorFactory extends BeanCreatorFactory
{
    @Override
    protected <T> AbsBeanCreator<T> create(Class<T> target)
    {
        return new AotFastCreator<>(target);
    }
}
