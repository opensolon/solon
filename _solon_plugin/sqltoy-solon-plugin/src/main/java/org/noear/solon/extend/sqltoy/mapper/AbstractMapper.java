package org.noear.solon.extend.sqltoy.mapper;

import org.noear.solon.core.Aop;
import org.noear.solon.core.util.ConvertUtil;
import org.noear.solon.extend.sqltoy.DbManager;
import org.sagacity.sqltoy.dao.SqlToyLazyDao;
import org.sagacity.sqltoy.model.Page;

import javax.sql.DataSource;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * @author 夜の孤城
 * @since 1.5
 * */
public abstract class AbstractMapper {
    protected SqlToyLazyDao dao;
    public void setDao(SqlToyLazyDao dao) {
        this.dao = dao;
    }
    protected <T> T _cast(Object val,Class<T> type){
        if(val==null){
            if(type.isPrimitive()){
               return (T)ConvertUtil.to(type,"0");
            }
            return null;
        }
        if(type.isAssignableFrom(val.getClass())){
            return (T)val;
        }

        return (T)ConvertUtil.to(type,val.toString());
    }
    protected SqlToyLazyDao _getDao(String dataSource){
        if(dataSource==null){
            return dao;
        }
        Object ds= Aop.get(dataSource);
        if(ds==null||!(ds instanceof DataSource)){
            return dao;
        }
        return DbManager.getDao((DataSource) ds);
    }
}
