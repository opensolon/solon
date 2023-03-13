package ma.glasnost.orika.solon;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.Mapper;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.ClassMapBuilder;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;

/**
 * @author: aoshiguchen
 * @since 2.2
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(AopContext context) throws Throwable {
        DefaultMapperFactory factory = new DefaultMapperFactory.Builder().build();

        context.subBeansOfType(CustomConverter.class, bean -> {
            factory.getConverterFactory().registerConverter(bean);
        });

        context.subBeansOfType(Mapper.class, bean -> {
            factory.registerMapper(bean);
        });

        context.subBeansOfType(ClassMapBuilder.class, bean -> {
            factory.registerClassMap((ClassMapBuilder<? extends Object, ? extends Object>) bean);
        });

        context.wrapAndPut(MapperFactory.class, factory);
        context.wrapAndPut(MapperFacade.class, factory.getMapperFacade());
    }
}
