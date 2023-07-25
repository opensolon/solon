package org.noear.solon.admin.server.data;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 监视器数据
 */
@NoArgsConstructor
@Data
public class Detector {

    private String name;

    private Map<String, Object> info;

}
