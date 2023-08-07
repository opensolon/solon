package org.noear.solon.extend.activerecord.proxy;

import java.util.HashMap;
import java.util.Map;

import org.noear.solon.core.util.ConvertUtil;
import org.noear.solon.extend.activerecord.ModelManager;

import com.jfinal.kit.TypeKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Record;

/**
 * Mapper 方法执行器
 *
 * @author 胡高 (https://gitee.com/gollyhu)
 * @since 1.10
 */
@SuppressWarnings("unchecked")
public final class MapperMethodInvoker {

    static String PAGE_NUMBER_NAME = "pageNumber";
    static String PAGE_SIZE_NAME = "pageSize";

    private static Map<Object, Object> buildArgsMap(MapperMethodContext context, Object[] args) {
        Map<Object, Object> argsMap = new HashMap<>();

        for (int i = 0; i < context.getParameters().length; i++) {
            String name = context.getParameters()[i].getName();
            Object para = args[i];
            if (null != para && para instanceof Map) {
                // 如果参数为Map，则Map中所有的参数都为直接参数
                argsMap.putAll((Map<Object, Object>)para);
            } else if (null != para && para instanceof Model) {
                // 如果参数为Model，则Model中所有的属性都为直接参数
                argsMap.putAll(((Model<?>)para).toMap());
            } else {
                argsMap.put(name, para);
            }
        }

        return argsMap;
    }

    private static int getPageNumber(Map<Object, Object> argsMap) {
        Integer pageIndex = TypeKit.toInt(argsMap.get(PAGE_NUMBER_NAME));
        return pageIndex == null ? 1 : pageIndex;
    }

    private static int getPageSiz(Map<Object, Object> argsMap) {
        Integer pageSize = TypeKit.toInt(argsMap.get(PAGE_SIZE_NAME));
        return pageSize == null ? 10 : pageSize;
    }

    public static Object invoke(MapperMethodContext context, String db, Object[] args) {
        Map<Object, Object> argsMap = buildArgsMap(context, args);

        /*
         * 执行List返回
         */
        if (context.isReturnList()) {
            return invokeList(context, db, argsMap);
        }

        /*
         * 执行Page返回
         */
        if (context.isReturnPage()) {
            return invokePage(context, db, argsMap);
        }

        /*
         * 执行单对象返回
         */
        return invokeOne(context, db, argsMap);
    }

    private static Object invokeList(MapperMethodContext context, String db, Map<Object, Object> argsMap) {
        if (Model.class.isAssignableFrom(context.getReturnClz())) {
            // 处理List<Model>
            Model<?> m = ModelManager.getModel((Class<? extends Model<?>>)context.getReturnClz());

            if (context.isSqlStatement()) {
                return m.use(db).templateByString(context.getSql(), argsMap).find();
            } else {
                return m.use(db).template(context.getSql(), argsMap).find();
            }
        }

        if (Record.class.isAssignableFrom(context.getReturnClz())) {
            // 处理List<Record>
            if (context.isSqlStatement()) {
                return Db.use(db).templateByString(context.getSql(), argsMap).find();
            } else {
                return Db.use(db).template(context.getSql(), argsMap).find();
            }
        }

        // 处理List<Object>
        if (context.isSqlStatement()) {
            return Db.use(db).templateByString(context.getSql(), argsMap).query();
        } else {
            return Db.use(db).template(context.getSql(), argsMap).query();
        }
    }

    private static Object invokeOne(MapperMethodContext context, String db, Map<Object, Object> argsMap) {
        /*
         * 处理Void
         */
        if (null == context.getReturnClz() || void.class == context.getReturnClz()) {
            if (context.isSqlStatement()) {
                return Db.use(db).templateByString(context.getSql(), argsMap).update();
            } else {
                return Db.use(db).template(context.getSql(), argsMap).update();
            }
        }

        /*
         * 处理Model
         */
        if (Model.class.isAssignableFrom(context.getReturnClz())) {
            Model<?> m = ModelManager.getModel((Class<? extends Model<?>>)context.getReturnClz());
            if (context.isSqlStatement()) {
                return m.use(db).templateByString(context.getSql(), argsMap).findFirst();
            } else {
                return m.use(db).template(context.getSql(), argsMap).findFirst();
            }
        }

        /*
         * 处理Record
         */
        if (Record.class.isAssignableFrom(context.getReturnClz())) {
            if (context.isSqlStatement()) {
                return Db.use(db).templateByString(context.getSql(), argsMap).findFirst();
            } else {
                return Db.use(db).template(context.getSql(), argsMap).findFirst();
            }
        }

        /*
         * 处理Object
         */
        if (context.isUpdate()) {
            // 执行Update
            Object ret = null;
            if (context.isSqlStatement()) {
                ret = Db.use(db).templateByString(context.getSql(), argsMap).update();
            } else {
                ret = Db.use(db).template(context.getSql(), argsMap).update();
            }
            return ConvertUtil.to(context.getReturnClz(), String.valueOf(ret));
        } else {
            if (context.isSqlStatement()) {
                return Db.use(db).templateByString(context.getSql(), argsMap).queryFirst();
            } else {
                return Db.use(db).template(context.getSql(), argsMap).queryFirst();
            }
        }
    }

    private static Object invokePage(MapperMethodContext context, String db, Map<Object, Object> argsMap) {
        int pageNumber = getPageNumber(argsMap);
        int pageSize = getPageSiz(argsMap);

        if (Model.class.isAssignableFrom(context.getReturnClz())) {
            // 处理List<Model>
            Model<?> m = ModelManager.getModel((Class<? extends Model<?>>)context.getReturnClz());

            if (context.isSqlStatement()) {
                return m.use(db).templateByString(context.getSql(), argsMap).paginate(pageNumber, pageSize);
            } else {
                return m.use(db).template(context.getSql(), argsMap).paginate(pageNumber, pageSize);
            }
        }

        if (Record.class.isAssignableFrom(context.getReturnClz())) {
            // 处理List<Record>
            if (context.isSqlStatement()) {
                return Db.use(db).templateByString(context.getSql(), argsMap).paginate(pageNumber, pageSize);
            } else {
                return Db.use(db).template(context.getSql(), argsMap).paginate(pageNumber, pageSize);
            }
        }

        return null;
    }

}
