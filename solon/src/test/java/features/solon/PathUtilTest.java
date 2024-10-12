package features.solon;

import org.junit.jupiter.api.Test;
import org.noear.solon.core.util.PathUtil;

/**
 * @author noear 2024/10/12 created
 */
public class PathUtilTest {
    @Test
    public void case1() {
        assert "localhost:8080/user/get".equals(PathUtil.joinUri("localhost:8080", "/user/get"));
        assert "localhost:8080/user/get".equals(PathUtil.joinUri("localhost:8080/", "/user/get"));
        assert "localhost:8080/user/get".equals(PathUtil.joinUri("localhost:8080", "user/get"));
    }

    @Test
    public void case2() {
        assert "localhost:8080/".equals(PathUtil.joinUri("localhost:8080", "/"));
        assert "localhost:8080/".equals(PathUtil.joinUri("localhost:8080/", "/"));
        assert "localhost:8080/".equals(PathUtil.joinUri("localhost:8080", ""));
    }

    @Test
    public void case3() {
        assert "http://localhost:8080/".equals(PathUtil.joinUri("http://localhost:8080", "/"));
        assert "http://localhost:8080/".equals(PathUtil.joinUri("http://localhost:8080/", "/"));
        assert "http://localhost:8080/".equals(PathUtil.joinUri("http://localhost:8080", ""));
    }
}
