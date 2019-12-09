package webapp.dso;

import org.noear.weed.annotation.Sql;
import webapp.model.AppxModel;

import java.util.List;
import java.util.Map;

//
// 注解 sql
//
public interface SqlAnnotation {
    @Sql("select app_id from appx limit 1")
    int appx_get() throws Exception;

    //添加了缓存处理
    @Sql(value = "select * from appx where app_id = @{app_id} limit 1", caching = "test", cacheTag = "app_${app_id}")
    AppxModel appx_get2(int app_id) throws Exception;

    @Sql(value = "select * from ${tb} where app_id = @{app_id} limit 1" , cacheClear = "test")
    Map<String,Object> appx_get3(String tb, int app_id) throws Exception;

    @Sql("select * from appx where app_id>@{app_id} order by app_id asc limit 4")
    List<AppxModel> appx_getlist(int app_id) throws Exception;

    @Sql("select app_id from appx limit 4")
    List<Integer> appx_getids() throws Exception;
}
