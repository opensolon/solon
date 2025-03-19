package features.solon.generic4;

import java.util.List;

public class ResultFilter {
    private List<LabelLoader<?>> labelLoaders;

    public ResultFilter(List<LabelLoader<?>> labelLoaders) {
        this.labelLoaders = labelLoaders;
    }

    public List<LabelLoader<?>> getLabelLoaders() {
        return labelLoaders;
    }
}
