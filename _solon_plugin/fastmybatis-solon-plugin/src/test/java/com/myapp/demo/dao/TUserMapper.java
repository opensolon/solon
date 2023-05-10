package com.myapp.demo.dao;

import com.gitee.fastmybatis.core.mapper.CrudMapper;
import com.gitee.fastmybatis.core.query.Query;
import com.myapp.demo.model.TUser;
import com.myapp.demo.model.UserInfoDO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

public interface TUserMapper extends CrudMapper<TUser, Integer> {

    // 自定义sql
    @Update("update t_user set username = #{username} where id = #{id}")
    int updateById(@Param("id") int id, @Param("username") String username);


    TUser selectByName(@Param("username") String username);

    List<TUser> findByMap(@Param("map") Map<String, Object> map);

    TUser getByMap(@Param("map") Map<String, Object> map);

    List<UserInfoDO> findJoin(@Param("query") Query query);

    List<UserInfoDO> listUserInfo(@Param("query") Query query);

    Long getUserInfoCount(@Param("query") Query query);

    UserInfoDO selectById(int id);
}