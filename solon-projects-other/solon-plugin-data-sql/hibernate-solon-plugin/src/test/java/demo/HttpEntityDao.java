package demo;

import org.noear.solon.annotation.Component;

/**
 * @author noear 2023/9/13 created
 */
@Component
public interface HttpEntityDao {
    void saveOrUpdate(HttpEntity entity);
}
