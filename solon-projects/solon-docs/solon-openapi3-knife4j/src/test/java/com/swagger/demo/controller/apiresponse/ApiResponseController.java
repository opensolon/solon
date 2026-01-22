package com.swagger.demo.controller.apiresponse;

import com.swagger.demo.model.ApplicationQueryPo;
import com.swagger.demo.model.ApplicationVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.noear.solon.annotation.Body;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Post;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "ApiResponseController(JAVA)")
@Controller
public class ApiResponseController {

    @Operation(summary = "apiResponse", description = "查询条件：apiResponse")
    @Mapping("/apiResponse")
    @Post
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = List.class, subTypes = ApplicationVo.class)
                    )
            )
    })
    public List<ApplicationVo> list(@Body ApplicationQueryPo applicationQueryPo) {
        List<ApplicationVo> list = new ArrayList<>();
        list.add(new ApplicationVo());
        return list;
    }

}
