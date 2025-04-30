package org.noear.solon.core.handle;

import org.noear.solon.core.wrap.MethodWrap;
import org.noear.solon.lang.Preview;

/**
 * 实体转换器（预览，未启用）
 *
 * @author noear
 * @since 3.1
 */
@Deprecated
@Preview("3.1")
public interface EntityConverter {
    /**
     * 是否可读
     */
    boolean canRead(String mime, Context ctx);

    /**
     * 是否可写
     */
    boolean canWrite(String mime, Context ctx);

    /**
     * 写入并返回（渲染并返回（默认不实现））
     */
    default String writeAndReturn(Object data, Context ctx) throws Throwable {
        return null;
    }

    /**
     * 写入（渲染）
     *
     * @param data 数据
     * @param ctx  上下文
     */
    void write(Object data, Context ctx) throws Throwable;


    /**
     * 读取（参数分析）
     *
     * @param target 控制器
     * @param mWrap  函数包装器
     * @param ctx    上下文
     */
    Object[] read(Object target, MethodWrap mWrap, Context ctx) throws Throwable;
}