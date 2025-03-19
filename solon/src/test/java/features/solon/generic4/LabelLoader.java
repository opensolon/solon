package features.solon.generic4;

import java.util.List;

public interface LabelLoader<ID> {

    /**
     * @param key 标签KEY
     * @return 是否支持
     */
    boolean supports(String key);

    /**
     * 加载标签
     * @param key 标签KEY
     * @param ids 标签ID列表
     * @return 标签列表
     */
    List<Label<ID>> load(String key, List<ID> ids);

}
