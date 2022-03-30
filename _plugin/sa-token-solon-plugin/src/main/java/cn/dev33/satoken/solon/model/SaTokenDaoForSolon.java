package cn.dev33.satoken.solon.model;

import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.util.SaFoxUtil;
import org.noear.solon.core.handle.Context;

import java.util.List;

/**
 * @author noear
 * @since 1.6
 */
public class SaTokenDaoForSolon implements SaTokenDao {
    /**
     * 获取Value，如无返空
     */
    @Override
    public String get(String key) {
        return Context.current().session(key, "");
    }

    /**
     * 写入Value，并设定存活时间 (单位: 秒)
     */
    @Override
    public void set(String key, String value, long timeout) {
        if (timeout == 0 || timeout <= SaTokenDao.NOT_VALUE_EXPIRE) {
            return;
        }

        Context.current().sessionSet(key, value);
    }

    /**
     * 修改指定key-value键值对 (过期时间不变)
     */
    @Override
    public void update(String key, String value) {
        Context.current().sessionSet(key, value);
    }

    /**
     * 删除Value
     */
    @Override
    public void delete(String key) {
        Context.current().sessionRemove(key);
    }

    /**
     * 获取Value的剩余存活时间 (单位: 秒)
     */
    @Override
    public long getTimeout(String key) {
        return 100L; //一直会有时间（把它交给）
    }

    /**
     * 修改Value的剩余存活时间 (单位: 秒)
     */
    @Override
    public void updateTimeout(String key, long timeout) {

    }


    /**
     * 获取Object，如无返空
     */
    @Override
    public Object getObject(String key) {
        return Context.current().session(key);
    }

    /**
     * 写入Object，并设定存活时间 (单位: 秒)
     */
    @Override
    public void setObject(String key, Object object, long timeout) {
        if (timeout == 0 || timeout <= SaTokenDao.NOT_VALUE_EXPIRE) {
            return;
        }

        Context.current().sessionSet(key, object);
    }

    /**
     * 更新Object (过期时间不变)
     */
    @Override
    public void updateObject(String key, Object object) {
        Context.current().sessionSet(key, object);
    }

    /**
     * 删除Object
     */
    @Override
    public void deleteObject(String key) {
        Context.current().sessionRemove(key);
    }

    /**
     * 获取Object的剩余存活时间 (单位: 秒)
     */
    @Override
    public long getObjectTimeout(String key) {
        return 100L;
    }

    /**
     * 修改Object的剩余存活时间 (单位: 秒)
     */
    @Override
    public void updateObjectTimeout(String key, long timeout) {

    }


    /**
     * 搜索数据
     */
    @Override
    public List<String> searchData(String prefix, String keyword, int start, int size) {
        return SaFoxUtil.searchList(Context.current().sessionState().sessionKeys(), prefix, keyword, start, size);
    }
}
