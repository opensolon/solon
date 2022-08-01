package benchmark;

import org.junit.Test;

/**
 * @author noear 2021/5/18 created
 */
public class AnnoexistsTest {
    @Test
    public void test() {
        long start = System.currentTimeMillis();

        for (int i = 0; i < 100000; i++) {
            if (AnnoexistsTest.class.getAnnotations().length > 0) {

            }
        }
        System.out.println((System.currentTimeMillis() - start) + "ms");
    }

    @Test
    public void test2() {
        long start = System.currentTimeMillis();

        for (int i = 0; i < 100000; i++) {

        }
        System.out.println((System.currentTimeMillis() - start) + "ms");
    }
}
