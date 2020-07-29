package server.dso;

import java.io.IOException;

public interface IGreetingService {
    public String greeting(String name) throws IOException;
}
