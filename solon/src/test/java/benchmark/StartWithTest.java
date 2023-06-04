package benchmark;

import java.util.Properties;

/**
 * @author noear 2023/6/5 created
 */
public class StartWithTest {
    public static void main(String[] args){
        Properties properties = System.getProperties();
        long timeStart = System.currentTimeMillis();

        for(int i=0; i<1000000; i++){
            properties.contains("cc_d");
            //"cc_d".startsWith("${");
        }

        System.out.println(System.currentTimeMillis() - timeStart);
    }
}
