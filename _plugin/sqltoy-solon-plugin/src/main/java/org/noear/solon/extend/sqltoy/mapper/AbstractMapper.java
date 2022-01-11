package org.noear.solon.extend.sqltoy.mapper;

import org.sagacity.sqltoy.dao.SqlToyLazyDao;
/**
 * @author 夜の孤城
 * @since 1.5
 * */
public abstract class AbstractMapper {
    protected SqlToyLazyDao dao;
    public void setDao(SqlToyLazyDao dao) {
        this.dao = dao;
    }
}
