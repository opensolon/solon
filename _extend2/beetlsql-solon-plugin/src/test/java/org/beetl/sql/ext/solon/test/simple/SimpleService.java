package org.beetl.sql.ext.solon.test.simple;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.ext.solon.test.UserInfo;
import org.junit.Test;
import org.noear.solon.annotation.XTran;
import org.noear.solon.extend.aspect.annotation.XService;
import org.noear.solon.extend.beetlsql.Db;

/**
 * Solon 的事务，只支持 XController, XService, XDao ，且只支持注在函数上（算是较为克制）
 * */
@XService
public class SimpleService {
    @Db
    SQLManager sqlManager;

    @Db
    SimpleUserInfoMapper userInfoMapper;

    @XTran
    public void test(){
        sqlManager.single(UserInfo.class,1);
        userInfoMapper.single(1);
    }
}
