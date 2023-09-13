package org.hibernate.solon;

import java.io.Serializable;

public interface Dao<E> {
    void saveOrUpdate(E entity);

    void deleteById(Serializable id);
}
