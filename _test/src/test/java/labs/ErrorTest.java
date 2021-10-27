package labs;

import org.junit.Test;

/**
 * @author noear 2021/10/27 created
 */
public class ErrorTest {
    @Test
    public void test(){
        try{
            test0();
        }catch (Throwable e){
            e.printStackTrace();
            System.out.println("4");
        }
    }

    private void test0(){
        try{
            System.out.println("1");
            throw new RuntimeException("xxx");
        }catch (Throwable e){
            System.out.println("2");
            throw e;
        }finally {
            System.out.println("3");
        }
    }
}
