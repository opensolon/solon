package features.solon.generic4;

import java.util.List;

/**
 * @author noear 2025/3/19 created
 */
public class BeanReflectorImpl implements BeanReflector {
    private List<FieldConvertor.BFieldConvertor> convertors;

    public BeanReflectorImpl(List<FieldConvertor.BFieldConvertor> convertors) {
        this.convertors = convertors;
    }

    @Override
    public List<FieldConvertor.BFieldConvertor> getConvertors() {
        return this.convertors;
    }
}