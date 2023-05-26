package demo3;

import com.baomidou.mybatisplus.solon.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AppxMapper {
    AppxModel appx_get();
    Page<AppxModel> appx_get_page(Page<AppxModel> page);
    AppxModel appx_get2(int app_id);
    void appx_add();
    Integer appx_add2(int v1);
}

