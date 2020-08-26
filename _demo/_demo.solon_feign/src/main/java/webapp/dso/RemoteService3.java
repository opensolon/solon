package webapp.dso;

import feign.Param;
import feign.RequestLine;
import org.noear.solon.extend.feign.FeignClient;
import webapp.model.User;

@FeignClient(name = "user-service", path = "/users/", configuration = JacksonConfig.class)
public interface RemoteService3 {

    @RequestLine("GET get1?name={name}")
    String getOwner(@Param(value = "name") String name);

    @RequestLine("GET get2?name={name}")
    User get2(String name);
}
