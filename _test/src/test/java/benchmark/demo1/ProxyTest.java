package benchmark.demo1;

import org.junit.Test;

public class ProxyTest {
    private static long count = 100000000;
    @Test
    public void test1(){
        UserServiceImpl service = new UserServiceImpl();
        service.hello();

        long start = System.currentTimeMillis();

        for (int i=0; i< count; i++){
            service.hello();
        }

        long end = System.currentTimeMillis();

        System.out.println("times: " + (end - start));
    }

    @Test
    public void test2() throws Exception{
        UserService service = JdkProxy.get(UserService.class, new UserServiceImpl());
        service.hello();

        long start = System.currentTimeMillis();

        for (int i=0; i< count; i++){
            service.hello();
        }

        long end = System.currentTimeMillis();

        System.out.println("times: " + (end - start));
    }

    @Test
    public void test3() throws Exception{
        UserService service = AsmProxy.get(UserServiceImpl.class, new UserServiceImpl());
        service.hello();

        long start = System.currentTimeMillis();

        for (int i=0; i< count; i++){
            service.hello();
        }

        long end = System.currentTimeMillis();

        System.out.println("times: " + (end - start));
    }
}
