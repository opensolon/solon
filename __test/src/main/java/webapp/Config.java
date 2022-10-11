package webapp;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.data.cache.CacheServiceSupplier;
import org.noear.solon.extend.async.AsyncManager;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author noear 2021/9/9 created
 */
@Configuration
public class Config {

//    @Bean
//    public void error(@Inject("${xxxyyyzzz}") String tmp){
//
//    }

    @Bean(value = "map1", typed = true)
    public Map<String, Object> map() {
        Map<String, Object> map = new HashMap<>();
        map.put("1", 1);
        map.put("2", 2);

        return map;
    }

    @Bean
    public void test1(@Inject("map1") BeanWrap bw) {
        Map map = bw.get();
        System.out.println("map::" + map.toString());
    }

    @Bean
    public void test2(@Inject("${username}") String name) {
        System.out.println("cfg::" + name);
    }

    @Bean
    public void test3(@Inject("${cache1}") CacheServiceSupplier supplier) {
        supplier.get();
        System.out.println("cache::");
    }

    @Bean
    public void test3() {
        ExecutorService pools = Executors.newCachedThreadPool();
        AsyncManager.setExecutor(cmd -> pools.submit(cmd));
    }


//    @Bean
//    public Filter test4() {
//        return new SaTokenPathFilter()
//                // 指定 [拦截路由] 与 [放行路由]
//                .addInclude("/demo3/upload/**")
//
//                // 认证函数: 每次请求执行
//                .setAuth(r -> SaRouter.match("/**", StpUtil::checkLogin))
//
//                // 异常处理函数：每次认证函数发生异常时执行此函数
//                .setError(e -> {
//                    System.out.println("---------- sa全局异常 ");
//                    System.out.println(e.getMessage());
//                    StpUtil.login(123);
//                    return e.getMessage();
//                });
//    }

}
