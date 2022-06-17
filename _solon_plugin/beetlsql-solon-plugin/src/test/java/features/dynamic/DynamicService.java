package features.dynamic;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.ext.solon.Db;
import org.noear.solon.data.annotation.Tran;
import org.noear.solon.aspect.annotation.Service;

/**
 * Solon 的事务，只支持 Controller, Service, Dao ，且只支持注在函数上（算是较为克制）
 * */
@Service
public class DynamicService {
    @Db
    SQLManager sqlManager;

    @Db
    DynamicUserInfoMapper mapper;

    @Tran
    public void test(){
        mapper.deleteById(19999);
        sqlManager.single(UserInfoInDs1.class,1);
        sqlManager.single(UserInfoInDs2.class,1);
        mapper.single(1);
        mapper.queryById(1);
    }
}
