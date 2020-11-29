package server.dso;


import org.noear.nami.annotation.NamiClient;

import java.io.IOException;

@NamiClient("test:/GreetingService/")
public interface IGreetingService {
     String greeting(String name) throws IOException;
}
