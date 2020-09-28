package features;

import org.noear.water.utils.HttpUtils;

import java.io.IOException;
import java.util.Map;

public class TestBase {
    protected String get(String path) throws IOException {
        String url = "http://localhost:8080" + path;
        String rst = HttpUtils.http(url).get();

        System.out.println(path + " :: " + rst);

        return rst;
    }

    protected int getStatus(String path) throws IOException {
        String url = "http://localhost:8080" + path;
        int rst = HttpUtils.http(url).exec("GET").code();

        System.out.println("code : " + rst);

        return rst;
    }

    protected int headStatus(String path) throws IOException {
        String url = "http://localhost:8080" + path;
        int rst = HttpUtils.http(url).exec("HEAD").code();

        System.out.println("code : " + rst);

        return rst;
    }

    protected String post(String path, String body) throws IOException {
        String url = "http://localhost:8080" + path;
        String rst = HttpUtils.http(url).bodyTxt(body, "text/plain").post();

        System.out.println(path + " :: " + rst);

        return rst;
    }

    protected String put(String path, String body) throws IOException {
        String url = "http://localhost:8080" + path;
        String rst = HttpUtils.http(url).bodyTxt(body, "text/plain").put();

        System.out.println(path + " :: " + rst);

        return rst;
    }

    protected String post(String path, Map<String,String> data) throws IOException {
        String url = "http://localhost:8080" + path;
        String rst =  HttpUtils.http(url).data(data).post();

        System.out.println(path + " :: " + rst);

        return rst;
    }

    protected String put(String path, Map<String,String> data) throws IOException {
        String url = "http://localhost:8080" + path;
        String rst =  HttpUtils.http(url).data(data).put();

        System.out.println(path + " :: " + rst);

        return rst;
    }
}
