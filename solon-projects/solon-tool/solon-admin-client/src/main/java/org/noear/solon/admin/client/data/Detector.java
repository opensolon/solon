package org.noear.solon.admin.client.data;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@AllArgsConstructor
@Data
public class Detector {

    private String name;

    private Map<String, Object> info;

}
