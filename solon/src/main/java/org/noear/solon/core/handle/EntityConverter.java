package org.noear.solon.core.handle;

import org.noear.solon.core.wrap.MethodWrap;
import org.noear.solon.lang.Preview;

/**
 * 实体转换器（预览，未启用）
 *
 * @author noear
 * @since 3.2
 */
@Preview("3.2")
public interface EntityConverter {
    /**
     * 名字
     */
    default String name() {
        return this.getClass().getSimpleName();
    }

    /**
     * 映射
     */
    default String[] mappings() {
        return null;
    }


    /**
     * 是否可写
     */
    boolean canWrite(String mime, Context ctx);

    /**
     * 写入并返回（渲染并返回（默认不实现））
     */
    String writeAndReturn(Object data, Context ctx) throws Throwable;

    /**
     * 写入（渲染）
     *
     * @param data 数据
     * @param ctx  上下文
     */
    void write(Object data, Context ctx) throws Throwable;


    /**
     * 是否可读
     */
    boolean canRead(Context ctx, String mime);

    /**
     * 读取（参数分析）
     *
     * @param ctx    上下文
     * @param target 控制器
     * @param mWrap  函数包装器
     */
    Object[] read(Context ctx, Object target, MethodWrap mWrap) throws Throwable;
}