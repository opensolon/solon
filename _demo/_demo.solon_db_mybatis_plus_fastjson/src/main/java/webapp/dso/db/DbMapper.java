package webapp.dso.db;

import org.apache.ibatis.annotations.Select;

import java.util.Map;

public interface DbMapper {
    @Select("select * from appx limit 1;")
    Map<String,Object> appx_get();

    @Select("select * from appx where app_id=#{app_id} limit 1;")
    Map<String,Object> appx_get2(int app_id);
}
