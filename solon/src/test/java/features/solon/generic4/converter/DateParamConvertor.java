package features.solon.generic4.converter;

import features.solon.generic4.FieldConvertor;
import features.solon.generic4.FieldMeta;

/**
 * @author noear 2025/3/19 created
 */
public class DateParamConvertor implements FieldConvertor.BFieldConvertor{
    @Override
    public boolean supports(FieldMeta meta, Class<?> valueType) {
        return false;
    }
}
