package server.dso;


import org.noear.fairy.annotation.FairyClient;

import java.io.IOException;

@FairyClient("test:/GreetingService/")
public interface IGreetingService {
     String greeting(String name) throws IOException;
}
