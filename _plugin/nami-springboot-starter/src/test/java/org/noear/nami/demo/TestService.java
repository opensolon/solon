package org.noear.nami.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class TestService {
    @Autowired
    BaiduApi api;
    @PostConstruct
    public void init(){
        System.out.println("测试");
        System.out.println(api.search("测试1"));
        api.doTest();

    }
}
