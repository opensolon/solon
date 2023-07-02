package org.noear.solon.admin.server.data;

import lombok.Data;
import lombok.Value;

import java.util.Map;

@Data
@Value
public class EnvironmentInformation {

    Map<String, String> systemEnvironment;

    Map<String, String> systemProperties;

    Map<String, String> applicationProperties;

}
