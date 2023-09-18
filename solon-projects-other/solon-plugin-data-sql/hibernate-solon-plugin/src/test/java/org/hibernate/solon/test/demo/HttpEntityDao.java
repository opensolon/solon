package org.hibernate.solon.test.demo;

import org.noear.data.jap.JpaRepository;
import org.noear.solon.annotation.Component;

@Component
public interface HttpEntityDao extends JpaRepository<HttpEntity, String> {
    void saveOrUpdate(HttpEntity entity);
}
