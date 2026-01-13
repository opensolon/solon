package com.swagger.demo.controller.issues;

import com.swagger.demo.model.ApplicationQueryPo;
import com.swagger.demo.model.ApplicationVo;
import com.swagger.demo.model.CustomerQueryPageVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.noear.solon.annotation.Body;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.core.util.MimeType;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "demo控制器(JAVA)")
@Controller
public class TestJavaController {
    @Operation(summary = "查询信息列表", description = "查询条件：应用名称")
    @Mapping(
            method = MethodType.POST,
            value = "/appList",
            produces = MimeType.APPLICATION_JSON_VALUE
    )
    public List<ApplicationVo> list(@Body ApplicationQueryPo applicationQueryPo) {
        List<ApplicationVo> list = new ArrayList<>();
        list.add(new ApplicationVo());
        return list;
    }

    @Operation(summary = "分页查询信息列表", description = "查询条件：应用名称")
    @Mapping(
            method = MethodType.POST,
            value = "/appPage",
            produces = MimeType.APPLICATION_FORM_URLENCODED_VALUE
    )
    public CustomerQueryPageVo<ApplicationVo> query(@Body List<ApplicationQueryPo> applicationQueryPo) {
        CustomerQueryPageVo<ApplicationVo> page = new CustomerQueryPageVo<>();
        page.setCurrPage(1);
        page.setPageSize(10);
        page.setTotalElements(1);
        page.getContent().add(new ApplicationVo());
        return page;
    }
}