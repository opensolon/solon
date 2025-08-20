package features.solon.inject3;

import org.noear.solon.annotation.Managed;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

import java.util.List;

/**
 * @author noear 2025/4/10 created
 */
@Configuration
public class DemoConfig {
    private DemoCon con;
    private List<Demo> demos;

    public List<Demo> getDemos() {
        return demos;
    }

    public DemoCon getCon() {
        return con;
    }

    public DemoConfig(@Inject(required = false) DemoCon con){
        this.con = con;
    }

    @Managed(allowInject = true)
    public void setDemo(List<Demo> demos) {
        this.demos = demos;
    }
}