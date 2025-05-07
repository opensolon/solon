package webapp.demo5_rpc;

import org.noear.solon.annotation.*;
import org.noear.solon.core.handle.UploadedFile;

import java.util.List;

@Mapping("/demo5/hello/")
@Controller
public class HelloServiceImpl implements HelloService {
    @Override
    @Post
    @Mapping("hello")
    public String hello(String name, @Header("H1") String h1) {
        return name + ":" + h1;
    }

    @Override
    @Mapping("/test01")
    @Post
    public String test01(@Param("ids") List<String> ids) {
        ids.forEach(System.out::println);

        return String.join(",", ids);
    }

    @Override
    @Mapping("/test02")
    @Post
    public String test02(@Param("file") UploadedFile file) {

        return file.getName();
    }

    @Override
    @Mapping("/test03")
    @Post
    public String test03() {
        return "test03";
    }
}