package benchmark;

import org.noear.solon.core.util.TmlUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author noear 2024/5/10 created
 */
public class TmlTest {
    public static void main(String[] args) {
        String view = "user=${user}";
        Map<String, Object> model = new HashMap<>();
        model.put("user", "noear");
        model.put("label", 1);
        model.put("",model);

        System.out.println(TmlUtil.parse(view, model));


        long timeStart = System.currentTimeMillis();


        for (int i = 0; i < 100_000; i++) {
            TmlUtil.parse(view, model);
        }

        System.out.println(System.currentTimeMillis() - timeStart);
    }
}