package demo;

import org.noear.solon.annotation.Component;

@Component
public interface HttpEntityDao {
    void saveOrUpdate(HttpEntity entity);
}
