package demo;

import org.noear.solon.extend.hotdev.Hotdev;

public class DemoApp {
    public static void main(String[] args) {
        //开发时可用这个启动,编译代码后会自动重启
        //IDEA下需要设置自动编译，否则需要手动编译下更改的文件
        Hotdev.start(DemoApp.class,args);
    }
}
