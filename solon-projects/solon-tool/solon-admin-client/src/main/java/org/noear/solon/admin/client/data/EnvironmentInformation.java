package org.noear.solon.admin.client.data;

import lombok.Data;
import lombok.Value;
import lombok.val;
import org.noear.solon.Solon;

import java.util.HashMap;
import java.util.Map;

@Data
@Value
public class EnvironmentInformation {

    Map<String, String> systemEnvironment;

    Map<String, String> systemProperties;

    Map<String, String> applicationProperties;

    public static EnvironmentInformation create() {
        return create(Solon.cfg().getBool("solon.admin.client.showSecretInformation", false));
    }

    public static EnvironmentInformation create(boolean showSecretInformation) {
        val systemEnvironment = new HashMap<String, String>();
        val systemProperties = new HashMap<String, String>();
        val applicationProperties = new HashMap<String, String>();

        System.getenv().forEach((key, value) -> systemEnvironment.put(key, showSecretInformation ? value : "******"));
        System.getProperties().forEach((key, value) -> systemProperties.put(key.toString(), showSecretInformation ? value.toString() : "******"));
        Solon.cfg().forEach((key, value) -> applicationProperties.put(key.toString(), showSecretInformation ? value.toString() : "******"));

        return new EnvironmentInformation(systemEnvironment, systemProperties, applicationProperties);
    }

}
