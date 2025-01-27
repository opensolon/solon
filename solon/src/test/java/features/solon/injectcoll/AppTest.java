package features.solon.injectcoll;

import org.junit.jupiter.api.Test;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;

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
        Solon.start(AppTest.class, new String[0]);

        //校验集合注入时，只运行一次
        assert count_l == 1;
        assert count_m == 1;
    }

    @Configuration
    public static class Config {
        @Bean("n1")
        public LogAdapter logAdapter() {
            return new LogAdapter() {
                @Override
                public void log(String msg) {
                    System.out.println(msg);
                }
            };
        }

        @Bean
        public void logAdapterAry(List<LogAdapter> adapters) {
            count_l++;
        }

        @Bean
        public void logAdapterMap(Map<String, LogAdapter> adapters) {
            count_m++;
        }
    }

    public interface LogAdapter {
        void log(String msg);
    }
}
