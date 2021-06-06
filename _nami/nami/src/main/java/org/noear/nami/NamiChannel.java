package org.noear.nami;

/**
 * Nami 执行通道
 *
 * @author noear
 * @since 1.0
 * */
public interface NamiChannel{
    Result call(Context ctx) throws Throwable;
}
