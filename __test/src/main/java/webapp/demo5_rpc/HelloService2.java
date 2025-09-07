package webapp.demo5_rpc;

import org.noear.solon.annotation.*;
import org.noear.solon.core.handle.UploadedFile;

import java.util.List;

/**
 * 简化模式
 * */
//@NamiClient
public interface HelloService2 {
    @Mapping("hello")
//    @Post
    String hello(String name, @Header("H1") String h1, @Cookie("C1") String c1);

    @Mapping("/test01")
//    @Post
    String test01(@Param("ids") List<String> ids);

    @Mapping("/test02")
//    @Post
    String test02(@Param("file") UploadedFile file);

    @Mapping("/test03")
    @Post
    String test03();

    @Mapping("/test04/{name}")
//    @Get
    String test04(String name);

    @Mapping("/test05?type={type}")
//    @Post
    String test05(int type, @Body String body);
}
