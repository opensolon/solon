package demo.sqlink.interceptor;

import org.noear.solon.data.sqlink.base.SqLinkConfig;
import org.noear.solon.data.sqlink.base.intercept.Interceptor;

import java.util.Base64;

public class Encryption extends Interceptor<String> {
    @Override
    public String doIntercept(String value, SqLinkConfig config) {
        return encrypt(value);
    }

    private String encrypt(String password) {
        // 加密逻辑
        return Base64.getEncoder().encodeToString(password.getBytes());
    }
}
