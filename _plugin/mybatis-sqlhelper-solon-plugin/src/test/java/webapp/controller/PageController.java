package webapp.controller;

import com.jn.sqlhelper.dialect.pagination.SqlPaginations;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;
import webapp.dso.mapper.AppxMapper;

/**
 * 分面演示（使用sqlhelper组件）
 *
 * */
@Mapping("/page/")
@Controller
public class PageController {
    @Inject
    AppxMapper appxMapper;

    @Mapping("test")
    public Object test() throws Throwable{
        SqlPaginations.preparePagination(2,2);

       return appxMapper.appx_get_page();

    }
}
