package org.noear.solon.aot.hint;

import lombok.Data;

/**
 * 序列化
 *
 * @author songyinyin
 * @since 2023/4/7 14:51
 */
@Data
public class SerializationHint {

    private String name;

    private String reachableType;
}
