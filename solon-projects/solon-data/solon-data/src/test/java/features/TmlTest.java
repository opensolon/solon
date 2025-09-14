package features;

import org.junit.jupiter.api.Test;
import org.noear.solon.core.util.SnelUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author noear 2024/10/8 created
 */
public class TmlTest {
    @Test
    public void case1() {
        Map<String, Object> model = new HashMap<>();
        model.put("datasourceCode", null);

        String temp = SnelUtil.evalTmpl("${datasourceCode}", model);
        System.out.println(temp);

        assert temp != null;
        assert temp.length() == 0;
    }

    @Test
    public void case2() {
        Map<String, Object> model = new HashMap<>();
        model.put("datasourceCode", "a");

        String temp = SnelUtil.evalTmpl("${datasourceCode}", model);
        System.out.println(temp);

        assert temp != null;
        assert "a".equals(temp);
    }
}
