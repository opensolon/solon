package com.myapp.demo.controller;

import com.gitee.fastmybatis.core.query.Query;
import com.myapp.demo.dao.TUserMapper;
import com.myapp.demo.model.TUser;
import org.apache.ibatis.solon.annotation.Db;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;

import java.util.Arrays;
import java.util.List;

@Controller
public class DemoController {

    @Db("db1")
    TUserMapper mapper;

    /**
     * http://localhost:6041/index
     *
     * @return
     */
    @Mapping("index")
    public TUser index() {
        return mapper.getById(6);
    }

    /**
     * http://localhost:6041/index2
     *
     * @return
     */
    @Mapping("index2")
    public List<TUser> index2() {
        Query query = new Query()
                .in("id", Arrays.asList(4, 5, 6));
        return mapper.list(query);
    }
}