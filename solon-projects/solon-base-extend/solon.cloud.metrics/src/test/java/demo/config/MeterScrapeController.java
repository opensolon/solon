package demo.config;

import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.prometheus.client.exporter.common.TextFormat;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;

/**
 * micrometer 接收站点
 *
 * @author bai
 * @since 2.4
 */
@Controller
public class MeterScrapeController {

    @Inject
    PrometheusMeterRegistry registry;

    @Mapping("/actuator/prometheus")
    public String handle(){
        //todo:这个是不是可以用默衣格式 registry.scrape()
        return registry.scrape(TextFormat.CONTENT_TYPE_OPENMETRICS_100);
    }
}