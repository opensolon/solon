package features.dynamic;

import org.beetl.sql.mapper.BaseMapper;
import org.beetl.sql.mapper.annotation.Sql;

public interface DynamicUserInfoMapper extends BaseMapper<UserInfoInDs1> {
     /*另外一个SQLManager*/
     @Sql("select * from user where id=?")
     UserInfoInDs2 queryById(Integer id);
}
