package org.noear.nami.demo;

import org.noear.nami.Result;
import org.noear.nami.annotation.Mapping;
import org.noear.nami.annotation.NamiClient;

@NamiClient(name = "baidu")
public interface BaiduApi {
    @Mapping("GET s")
    String search(String w);
    @Mapping("GET s")
    Result test2(String w);
    default void doTest(){
        for(int i=0;i<10;i++){
          Result r=  this.test2("测试");
            System.out.println(r.code());
        }
    }
}
