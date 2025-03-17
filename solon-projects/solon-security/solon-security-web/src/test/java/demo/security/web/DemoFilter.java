package demo.security.web;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.security.web.SecurityFilter;
import org.noear.solon.security.web.header.XContentTypeOptionsHeaderHandler;
import org.noear.solon.security.web.header.XXssProtectionHeaderHandler;


@Configuration
public class DemoFilter {
    @Bean(index = -99)
    public SecurityFilter securityFilter() {
        return new SecurityFilter(
                new XContentTypeOptionsHeaderHandler(),
                new XXssProtectionHeaderHandler()
        );
    }
}
