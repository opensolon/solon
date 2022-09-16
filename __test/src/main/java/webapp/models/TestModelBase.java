package webapp.models;

import org.noear.solon.annotation.Inject;

/**
 * @author noear 2022/7/8 created
 */
public class TestModelBase {
    @Inject("${testname}")
    String testname;

    public String getTestname() {
        return testname;
    }
}
