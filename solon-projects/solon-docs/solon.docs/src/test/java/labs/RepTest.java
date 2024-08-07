package labs;

/**
 * @author noear 2024/8/7 created
 */
public class RepTest {
    public static void main(String[] args) {
        String tmp = "{service}/demo?group={service}".replace("{service}","app-api");
        System.out.println(tmp);
    }
}
