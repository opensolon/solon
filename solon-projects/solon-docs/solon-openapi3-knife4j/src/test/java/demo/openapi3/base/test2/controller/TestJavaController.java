package demo.openapi3.base.test2.controller;

import com.swagger.demo.model.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.noear.solon.annotation.*;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.core.handle.UploadedFile;
import org.noear.solon.core.util.MimeType;

import java.util.List;

@Tag(name = "test")
@Controller
public class TestJavaController {

    @Operation(summary = "分页查询信息列表", description = "查询条件：应用名称")
    @Mapping(
            method = MethodType.POST,
            value = "/appPage",
            produces = MimeType.APPLICATION_FORM_URLENCODED_VALUE
    )
    public CustomerQueryPageVo<Page<ApplicationVo>> query(@Parameter(description = "Parameter 缺参数说明", required = true) CustomerQueryPageVo<ApplicationVo> queryParam) {
        CustomerQueryPageVo<Page<ApplicationVo>> page = new CustomerQueryPageVo<>();
        page.setCurrPage(1);
        page.setPageSize(10);
        page.setTotalElements(1);

        PageImpl<ApplicationVo> applicationVoPage = new PageImpl<>(1, 10);
        page.getContent().add(applicationVoPage);
        return page;
    }

    @Operation(summary = "测试请求类型注解")
    @Mapping("/testRequestType/{appid}")
    @Post
    public R<String> list(@Parameter(description = "用户授权", required = true) @Header String token,
                          @Parameter(description = "微信openId") String openId,
                          @Parameter(description = "上传附件", required = false) UploadedFile file,
                          @Path String appId,
                          @Body List<ApplicationQueryPo> applicationQueryPo) {
        return R.ok(null);
    }

}

