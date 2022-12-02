package benchmark;

import org.noear.solon.core.handle.MethodType;

/**
 * @author noear 2022/12/2 created
 */
public class MethodTest {
    public static void main(String[] args){
        long timeStart = System.currentTimeMillis();

        for(int i=0; i<100000; i++){
            if("GET".equals("GET")){

            }
        }

        System.out.println(System.currentTimeMillis() - timeStart);
    }
}
