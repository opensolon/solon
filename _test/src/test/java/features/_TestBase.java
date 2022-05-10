package features;

import org.noear.solon.test.HttpTestBase;

import java.io.IOException;
import java.util.Map;

public class _TestBase extends HttpTestBase {
    protected String get(String path) throws IOException {
        String rst = path(path).get();

        System.out.println(path + " :: " + rst);

        return rst;
    }

    protected String post(String path, Map<String, String> data) throws IOException {
        String rst = path(path).data(data).post();

        System.out.println(path + " :: " + rst);

        return rst;
    }

    protected String put(String path, Map<String, String> data) throws IOException {
        String rst = path(path).data(data).put();

        System.out.println(path + " :: " + rst);

        return rst;
    }
}
