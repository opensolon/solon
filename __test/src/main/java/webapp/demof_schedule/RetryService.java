package webapp.demof_schedule;

import org.noear.solon.annotation.ProxyComponent;
import org.noear.solon.scheduling.annotation.Retry;
import org.noear.solon.scheduling.retry.DefaultRecover;

import java.util.concurrent.TimeUnit;

@ProxyComponent
public class RetryService {

    @Retry(
            maxAttempts = 5,
            recover = DefaultRecover.class,
            interval = 1,
            unit = TimeUnit.SECONDS,
            exs = {ArithmeticException.class}
    )
    public String m1(String aa) {
        System.out.println("123 = " + aa);
        int a = 1 / 0;
        return "ok";
    }

    @Retry(
            maxAttempts = 5,
            recover = DefaultRecover.class,
            interval = 1,
            unit = TimeUnit.SECONDS,
            exs = {NullPointerException.class}
    )
    public String m2(String aa) {
        System.out.println("123 = " + aa);
        int a = 1 / 0;
        return "ok";
    }

}
