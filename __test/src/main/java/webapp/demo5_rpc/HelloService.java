package webapp.demo5_rpc;

import org.noear.nami.common.ContentTypes;
import org.noear.solon.annotation.*;
import org.noear.solon.core.handle.UploadedFile;

import java.util.List;

//@NamiClient
public interface HelloService {
    @Mapping("hello")
    @Post
    String hello(String name, @Header("H1") String h1, @Cookie("C1") String c1);

    @Mapping("/test01")
    @Post
    String test01(@Param("ids") List<String> ids);

    @Mapping("/test02")
    @Post
    String test02(@Param("file") UploadedFile file);

    @Mapping("/test03")
    @Post
    String test03();

    @Mapping("/test04/{name}")
    @Get
    String test04(String name);

    @Mapping("/test05?type={type}")
    @Post
    String test05(int type, @Body String body);

    @Mapping("/test06")
    @Post
    public String test06(String name);

    @Consumes(ContentTypes.FORM_URLENCODED_VALUE)
    @Mapping("/test07")
    @Post
    String test07(@Body Namiform namiform);
}