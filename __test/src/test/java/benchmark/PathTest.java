package benchmark;

import org.junit.Test;
import org.noear.solon.Utils;

/**
 * @author noear 2022/7/8 created
 */
public class PathTest {
    @Test
    public void test1(){
        long start = System.currentTimeMillis();
        for(int i=0; i<100000; i++) {
            String path = "/a//b/c//d/e";
            if(path.contains("//")) {
                path = Utils.trimDuplicates(path, '/');
            }

            //System.out.println(path);
        }

        long timspan = System.currentTimeMillis() - start;
        System.out.println(timspan);
    }

    @Test
    public void test2(){
        long start = System.currentTimeMillis();
        for(int i=0; i<100000; i++) {
            String path = "/a//b/c//d/e";
            path = path.replaceAll("//","/");

            //System.out.println(path);
        }

        long timspan = System.currentTimeMillis() - start;
        System.out.println(timspan);
    }

    @Test
    public void test3(){
        long start = System.currentTimeMillis();
        for(int i=0; i<100000; i++) {
            String path = "/a//b/c//d/e";
            while (path.contains("//")) {
                path = path.replace("//", "/");
            }

            //System.out.println(path);
        }

        long timspan = System.currentTimeMillis() - start;
        System.out.println(timspan);
    }
}
