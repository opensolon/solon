package webapp.dso.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import webapp.model.AppxModel;
import webapp.model.DbTable;

import java.util.List;

@Mapper
public interface AppxMapper {
    AppxModel appx_get();
    List<AppxModel> appx_get_page();
    AppxModel appx_get2(int app_id);
    void appx_add();
    Integer appx_add2(int v1);

    @Select("SELECT * FROM INFORMATION_SCHEMA.TABLES")
    List<DbTable> listTables();
}

