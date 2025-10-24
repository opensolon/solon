package features.solon.injectcoll;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.noear.solon.annotation.Managed;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.core.AppContext;

import java.util.List;
import java.util.Map;

/**
 * @author noear 2025/1/27 created
 */
public class AppTest {
    static int count_l;
    static int count_m;

    @Test
    public void case1() throws Exception {
        AppContext appContext = new AppContext();
        appContext.beanScan(AppTest.class);
        appContext.start();

        //校验集合注入时，只运行一次
        Assertions.assertEquals(1, count_l);
        Assertions.assertEquals(1, count_m);
    }

    @Configuration
    public static class Config {
        @Managed("n1")
        public LogAdapter logAdapter() {
            return new LogAdapter() {
                @Override
                public void log(String msg) {
                    System.out.println(msg);
                }
            };
        }

        public LogAdapter logAdapter2() {
            return new LogAdapter() {
                @Override
                public void log(String msg) {
                    System.out.println(msg);
                }
            };
        }

        @Managed
        public void logAdapterAry(List<LogAdapter> adapters) {
            count_l++;
        }

        @Managed
        public void logAdapterMap(Map<String, LogAdapter> adapters) {
            count_m++;
        }
    }

    public interface LogAdapter {
        void log(String msg);
    }
}
