package webapp.controller.test;

import feign.Feign;
import feign.gson.GsonDecoder;

import java.util.List;

/**
 * @author noear 2021/1/1 created
 */
public class MainTest {
    public static void main(String... args) {
        GitHub github = Feign.builder()
                .decoder(new GsonDecoder())
                .target(GitHub.class, "https://api.github.com");

        // Fetch and print a list of the contributors to this library.
        List<Contributor> contributors = github.contributors("OpenFeign", "feign");
        for (Contributor contributor : contributors) {
            System.out.println(contributor.login + " (" + contributor.contributions + ")");
        }
    }
}
