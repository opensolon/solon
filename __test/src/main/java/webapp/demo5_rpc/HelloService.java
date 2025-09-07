package webapp.demo5_rpc;

import org.noear.nami.annotation.NamiClient;
import org.noear.solon.annotation.*;
import org.noear.solon.core.handle.UploadedFile;

import java.util.List;

//@NamiClient
public interface HelloService {
    @Post
    @Mapping("hello")
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

    @Mapping("/test04?type={type}")
    @Post
    String test04(int type, @Body String body);
}
