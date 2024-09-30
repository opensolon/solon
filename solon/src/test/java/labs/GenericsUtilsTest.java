package labs;

import org.noear.solon.core.wrap.ClassWrap;

import java.util.HashMap;

/**
 * @author noear 2024/9/30 created
 */
public class GenericsUtilsTest {
    public static void main(String[] args) {
        try {
            ClassWrap.get(HashMap.class);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
