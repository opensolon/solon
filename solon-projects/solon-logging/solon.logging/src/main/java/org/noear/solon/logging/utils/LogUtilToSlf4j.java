package org.noear.solon.logging.utils;

import org.noear.solon.annotation.Note;
import org.noear.solon.extend.impl.LogUtilExt;

/**
 * 把内核日志转到 Slf4j 接口（不再需要手动转了）
 *
 * @author noear
 * @since 1.10
 * @deprecated 2.3
 * @removal true
 */
@Note("不再需要手动转了")
@Deprecated
public class LogUtilToSlf4j extends LogUtilExt {

}
