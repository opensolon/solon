package org.noear.solon.extend.activerecord;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.noear.solon.extend.activerecord.annotation.Table;

import com.jfinal.plugin.activerecord.Model;

/**
 * Model 管理器
 *
 * @author 胡高 (https://gitee.com/gollyhu)
 * @date 2022/08/16
 */
public class ModelManager {
    static Map<Class<? extends Model<?>>, Model<?>> modelMap = new HashMap<>();
    static Map<Table, Class<? extends Model<?>>> tableMap = new LinkedHashMap<>();

    @SuppressWarnings("unchecked")
    public static void addModel(Table table, Model<?> model) {
        modelMap.put((Class<? extends Model<?>>)model.getClass(), model);
        tableMap.put(table, (Class<? extends Model<?>>)model.getClass());
    }

    public static Model<?> getModel(Class<? extends Model<?>> clz) {
        return modelMap.get(clz);
    }

    public static Map<Table, Class<? extends Model<?>>> getModelClassMap() {
        return tableMap;
    }
}
