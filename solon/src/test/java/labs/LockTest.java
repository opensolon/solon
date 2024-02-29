package labs;

import org.noear.solon.Utils;

/**
 * @author noear 2024/3/1 created
 */
public class LockTest {
    public static void main(String[] args){
        Utils.locker().lock();

        try{
            Utils.locker().lock();

            System.out.println("test");

            initAaa();

        }finally {
            Utils.locker().unlock();
        }
    }

    public static void initAaa(){
        Utils.locker().lock();

        try{
            System.out.println("init");

        }finally {
            Utils.locker().unlock();
        }
    }
}
