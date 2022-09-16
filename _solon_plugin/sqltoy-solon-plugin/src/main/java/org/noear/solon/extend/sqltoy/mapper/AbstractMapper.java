package org.noear.solon.extend.sqltoy.mapper;

import org.noear.solon.core.util.ConvertUtil;
import org.sagacity.sqltoy.dao.SqlToyLazyDao;

import javax.sql.DataSource;

/**
 * @author 夜の孤城
 * @since 1.5
 * */
public abstract class AbstractMapper {
    protected SqlToyLazyDao dao;

    public void setDao(SqlToyLazyDao dao) {
        this.dao = dao;
    }

    protected <T> T _cast(Object val, Class<T> type) {
        if (val == null) {
            if (type.isPrimitive()) {
                return (T) ConvertUtil.to(type, "0");
            }
            return null;
        }
        if (type.isAssignableFrom(val.getClass())) {
            return (T) val;
        }

        return (T) ConvertUtil.to(type, val.toString());
    }
}
