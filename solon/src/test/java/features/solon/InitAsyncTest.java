package features.solon;

import org.junit.jupiter.api.Test;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Init;
import org.noear.solon.core.AppContext;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 用于测试@Init注解异步执行功能的测试类
 */
public class InitAsyncTest {
    @Test
    public void testInitAsyncExecution() {
        System.out.println("[InitAsyncTest] 测试开始");
        
        // 创建AppContext并注册测试Bean
        AppContext appContext = new AppContext();
        appContext.beanMake(ClassA.class);
        appContext.beanMake(ClassB.class);
        appContext.beanMake(ClassC.class);

        // 记录开始时间
        long startTime = System.currentTimeMillis();
        
        // 启动AppContext
        appContext.start();

        // 记录结束时间
        long endTime = System.currentTimeMillis();

        // 计算总执行时间
        long totalTime = endTime - startTime;

        System.out.printf("[InitAsyncTest] 测试完成，总初始化时间:%d ms%n", totalTime);

        // 验证异步执行是否生效 1秒同步方法 + 5秒异步方法，总时间应该在6秒左右
        assertTrue(totalTime >= 6000 && totalTime < 7000, 
            "异步执行功能未按预期工作，总时间: " + totalTime + " ms");
    }

    @Component
    public static class ClassA {
        @Init
        public void syncInitMethod() {
            System.out.println("[syncInitMethod] 开始执行");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println("[syncInitMethod] 执行完毕");
        }
    }
    
    @Component
    public static class ClassB {
        @Init(async = true)
        public void asyncInitMethod1() {
            System.out.println("[asyncInitMethod1] 开始执行");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println("[asyncInitMethod1] 执行完毕");
        }
    }
    
    @Component
    public static class ClassC {
        @Init(async = true)
        public void asyncInitMethod2() {
            System.out.println("[asyncInitMethod2] 开始执行");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println("[asyncInitMethod2] 执行完毕");
        }
    }
}