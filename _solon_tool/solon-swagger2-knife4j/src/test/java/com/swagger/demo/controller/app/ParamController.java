package com.swagger.demo.controller.app;

import com.swagger.demo.model.bean.DeviceParamBean;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.noear.solon.annotation.Body;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;
import org.noear.solon.core.handle.UploadedFile;

import java.util.Map;

/**
 * @author noear 2023/5/25 created
 */
@Mapping("/param")
@Api(description = "参数测试", tags = "示例")
@Controller
public class ParamController {
    @ApiOperation("框架类参数")
    @Mapping("demo1")
    public Result demo1(Context ctx, UploadedFile file) {
        return null;
    }

    @ApiOperation("字符串参数")
    @Mapping("demo2")
    public void demo2(String name) {

    }

    @ApiOperation("字符串参数")
    @Mapping("demo3")
    public void demo3(@Body String body) {

    }

    @ApiOperation("Map 参数")
    @Mapping("demo4")
    public void demo4(@Body Map<String, String> body) {

    }

    @ApiOperation("实体参数")
    @Mapping("demo10")
    public void demo10(DeviceParamBean bean) {

    }
}
