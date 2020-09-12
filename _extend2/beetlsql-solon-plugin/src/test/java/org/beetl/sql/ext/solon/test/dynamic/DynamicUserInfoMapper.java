package org.beetl.sql.ext.solon.test.dynamic;

import org.beetl.sql.mapper.BaseMapper;
import org.beetl.sql.mapper.annotation.Sql;

public interface DynamicUserInfoMapper extends BaseMapper<UserInfoInDs1> {
     /*另外一个SQLManager*/
     @Sql("select * from user where id=?")
     UserInfoInDs1 queryById(Integer id);
}
