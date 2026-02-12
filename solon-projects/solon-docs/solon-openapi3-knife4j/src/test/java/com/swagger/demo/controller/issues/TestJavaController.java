package com.swagger.demo.controller.issues;

import com.swagger.demo.model.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.noear.solon.annotation.*;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.core.handle.UploadedFile;
import org.noear.solon.core.util.MimeType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "demo控制器(JAVA)")
@Controller
public class TestJavaController {
    @Operation(summary = "查询信息列表", description = "查询条件：查询信息列表")
    @Mapping(
            method = MethodType.POST,
            value = "/appList/{appid}",
            produces = MimeType.APPLICATION_JSON_VALUE
    )
    public R<Page<List<ApplicationVo>>> list(@Parameter(description = "用户授权") @Header(description = "用户授权", required = true) String token,
                                             @Parameter(description = "param") String openId,
                                             @Parameter(description = "上传附件") UploadedFile file,
                                             @Path String appId,
                                             @Body List<ApplicationQueryPo> applicationQueryPo) {
//        List<Map<String, ApplicationVo>> list = new ArrayList<>();
//        HashMap<String, ApplicationVo> stringApplicationVoHashMap = new HashMap<>();
//        stringApplicationVoHashMap.put("1", new ApplicationVo());
//        list.add(stringApplicationVoHashMap);
        return R.ok(null);
    }

    @Operation(summary = "分页查询信息列表", description = "查询条件：应用名称")
    @Mapping(
            method = MethodType.POST,
            value = "/appPage",
            produces = MimeType.APPLICATION_JSON_VALUE
    )
    public CustomerQueryPageVo<Page<ApplicationVo>> query(@Parameter(description = "issues 缺参数说明", required = true) CustomerQueryPageVo<ApplicationVo> queryParam) {
        CustomerQueryPageVo<Page<ApplicationVo>> page = new CustomerQueryPageVo<>();
        page.setCurrPage(1);
        page.setPageSize(10);
        page.setTotalElements(1);

        PageImpl<ApplicationVo> applicationVoPage = new PageImpl<>(1, 10);
        page.getContent().add(applicationVoPage);
        return page;
    }
}