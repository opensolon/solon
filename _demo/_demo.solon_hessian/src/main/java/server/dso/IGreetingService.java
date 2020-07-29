package server.dso;

import org.noear.solonclient.annotation.XClient;

import java.io.IOException;

@XClient("test:/GreetingService/")
public interface IGreetingService {
    public String greeting(String name) throws IOException;
}
