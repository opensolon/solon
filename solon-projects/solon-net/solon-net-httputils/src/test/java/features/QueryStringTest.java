package features;

import org.junit.jupiter.api.Test;
import org.noear.solon.net.http.HttpUtils;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author noear 2024/10/13 created
 */
public class QueryStringTest {
    @Test
    public void case1() throws IOException {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("key1", "a");
        map.put("key2", "b");
        map.put("key3", "c");
        CharSequence str = HttpUtils.toQueryString(map);

        System.out.println(str);

        assert "key1=a&key2=b&key3=c".equals(str);
    }
}
