package webapp.dso;

import feign.Param;
import feign.RequestLine;
import webapp.model.User;

public interface RemoteService {

    @RequestLine("GET /users/get1?name={name}")
    String getOwner(@Param(value = "name") String name);

    @RequestLine("GET /users/get2?name={name}")
    User getOwner2(@Param(value = "name") String name);
}
