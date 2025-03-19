package features.solon.generic4;

import java.util.List;

/**
 * @author noear 2025/3/19 created
 */
public interface BeanReflector {
    List<FieldConvertor.BFieldConvertor> getConvertors();
}
