package webapp.dso;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.core.handle.Filter;
import org.noear.solon.web.version.VersionFilter;

/**
 * @author noear 2025/6/25 created
 */
@Configuration
public class VersonConfig {
    @Bean
    public Filter filter() {
        return new VersionFilter().useHeader("Api-Version");
    }
}
