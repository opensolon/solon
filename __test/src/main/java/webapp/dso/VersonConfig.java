package webapp.dso;

import org.noear.solon.annotation.Managed;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.core.handle.Filter;
import org.noear.solon.web.version.VersionFilter;

/**
 * @author noear 2025/6/25 created
 */
@Configuration
public class VersonConfig {
    @Managed
    public Filter filter() {
        return new VersionFilter().useHeader("Api-Version");
    }
}
