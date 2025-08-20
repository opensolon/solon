/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package webapp;

import org.noear.solon.annotation.Managed;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.data.cache.CacheServiceSupplier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author noear 2021/9/9 created
 */
@Configuration
public class Config {
    @Inject(value = "${username}", autoRefreshed = true)
    String username;


    @Inject("${formattest.text}")
    String formattest;

    @Inject("${formattest.text2}")
    String formattest2;

    @Inject("${formattest.text3}")
    String formattest3;

    @Inject("${formattest.text10}")
    String formattest10;

    @Inject("${formattest.text11}")
    String formattest11;


    @Inject("${inject.set1:1,2,3}")
    Set<String> injectSet1;

    @Inject("${inject.set2:1,2,3}")
    List<Integer> injectSet2;

    @Inject("${inject.set3}")
    Set<Integer> injectSet3;

    @Inject("${inject.set3}")
    Integer[] injectSet3_2;

    @Inject("${inject.set4}")
    Set<Integer> injectSet4;

    @Inject("${inject.set4}")
    Integer[] injectSet4_2;

    @Inject("${load2.title}")
    String load2Title;

    @Inject("${load1.title}")
    String load1Title;

//    @Managed
//    public void error(@Inject("${xxxyyyzzz}") String tmp){
//
//    }

    @Managed(value = "map1", typed = true)
    public Map<String, Object> map() {
        Map<String, Object> map = new HashMap<>();
        map.put("1", 1);
        map.put("2", 2);

        return map;
    }

    @Managed
    public void test1(@Inject("map1") BeanWrap bw) {
        Map map = bw.get();
        System.out.println("map::" + map.toString());
    }

    @Managed
    public void test2(@Inject("${username}") String name) {
        System.out.println("cfg::" + name);
    }

    @Managed
    public void test3(@Inject("${cache1}") CacheServiceSupplier supplier) {
        supplier.get();
        System.out.println("cache::");
    }



//    @Managed
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
