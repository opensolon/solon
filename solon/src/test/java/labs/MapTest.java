package labs;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author noear 2023/9/13 created
 */
public class MapTest {
    public static void main(String[] args) {
        Map<String, String> loadKeyMap = new TreeMap<>();
        loadKeyMap.put("3", "d");
        loadKeyMap.put("0", "a");
        loadKeyMap.put("2", "c");
        loadKeyMap.put("1", "b");
        loadKeyMap.put("","x");

        for (String v : loadKeyMap.values()) {
            System.out.println(v);
        }
    }
}
