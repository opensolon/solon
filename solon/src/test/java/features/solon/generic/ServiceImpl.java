package features.solon.generic;

import org.noear.solon.annotation.Inject;

/**
 * @author noear 2024/10/29 created
 */
public class ServiceImpl<M extends BaseMapper<T>, T> {
    @Inject
    protected M baseMapper;

    public M getBaseMapper() {
        assert baseMapper != null;
        return baseMapper;
    }
}