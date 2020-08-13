package benchmark.demo1;

import org.junit.Test;

public class ProxyTest {
    @Test
    public void test1(){
        UserServiceImpl service = new UserServiceImpl();
        service.hello();

        long start = System.currentTimeMillis();

        for (int i=0; i< 100000000; i++){
            service.hello();
        }

        long end = System.currentTimeMillis();

        System.out.println("times: " + (end - start));
    }

    @Test
    public void test2() throws Exception{
        UserService service = BeanProxy.get(UserService.class, new UserServiceImpl());
        service.hello();

        long start = System.currentTimeMillis();

        for (int i=0; i< 100000000; i++){
            service.hello();
        }

        long end = System.currentTimeMillis();

        System.out.println("times: " + (end - start));
    }
}
