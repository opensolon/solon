package webapp.models;

import org.noear.solon.annotation.Component;
import org.noear.solon.core.convert.Converter;
import org.noear.solon.core.exception.ConvertException;

/**
 * @author noear 2023/7/17 created
 */
@Component
public class CatTypeConverter implements Converter<String, CatType> {
    @Override
    public CatType convert(String value) throws ConvertException {
        return "2".equals(value) ? CatType.Demo2 : CatType.Demo1;
    }
}
