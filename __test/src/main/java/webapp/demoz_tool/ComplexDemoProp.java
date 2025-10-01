package webapp.demoz_tool;

import lombok.Data;
import org.noear.solon.annotation.BindProps;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.configurationprocessor.support.NestedConfigurationProperty;

import java.util.List;
import java.util.Map;

@Data
@Configuration
@BindProps(prefix = "cdemo")
public class ComplexDemoProp {

    private Map<String, AConfig> aConfigMap;
    private String name;

    @NestedConfigurationProperty
    private AConfig aConfig;

    private List<BConfig> bConfigs;
}

