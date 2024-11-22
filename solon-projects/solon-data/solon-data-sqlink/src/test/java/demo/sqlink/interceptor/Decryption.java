package demo.sqlink.interceptor;

import org.noear.solon.data.sqlink.base.SqLinkConfig;
import org.noear.solon.data.sqlink.base.intercept.Interceptor;

import java.util.Base64;

public class Decryption extends Interceptor<String> {
    @Override
    public String doIntercept(String value, SqLinkConfig config) {
        return decrypt(value);
    }

    private String decrypt(String value) {
        // 解密逻辑
        return new String(Base64.getDecoder().decode(value));
    }
}
