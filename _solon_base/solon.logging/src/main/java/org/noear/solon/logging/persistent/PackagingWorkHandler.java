package org.noear.solon.logging.persistent;

import java.util.List;

/**
 * 打包工作处理
 *
 * @author noear
 * @since 2.2
 */
public interface PackagingWorkHandler<Event> {
    void onEvents(List<Event> list) throws Exception;
}
