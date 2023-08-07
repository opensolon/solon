package org.noear.solon.admin.client.data;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

/**
 * 监视器数据
 *
 * @author shaokeyibb
 * @since 2.3
 */
@AllArgsConstructor
@Data
public class Detector {

    private String name;

    private Map<String, Object> info;

}
