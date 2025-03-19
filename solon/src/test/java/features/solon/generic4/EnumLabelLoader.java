package features.solon.generic4;

import java.util.Collections;
import java.util.List;

/**
 * @author noear 2025/3/19 created
 */
public class EnumLabelLoader implements  LabelLoader<Enum<?>>{
    @Override
    public boolean supports(String key) {
        return false;
    }

    @Override
    public List<Label<Enum<?>>> load(String key, List<Enum<?>> enums) {
        return Collections.emptyList();
    }
}
