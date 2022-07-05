package org.noear.solon.vault.coder;

import org.noear.solon.Solon;
import org.noear.solon.vault.VaultCoder;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * @author noear
 * @since 1.9
 */
public class AesVaultCoder implements VaultCoder {

    private final String charset = "UTF-8";

    private final String algorithm = "AES/ECB/PKCS5Padding";
    private final String password;

    public AesVaultCoder() {
        this.password = Solon.cfg().get("solon.guard.password");
    }

    /**
     * 加密
     */
    @Override
    public String encrypt(String str) throws Exception {
        byte[] passwordBytes = password.getBytes(charset);
        SecretKeySpec secretKey = new SecretKeySpec(passwordBytes, "AES");

        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        byte[] encrypted = cipher.doFinal(str.getBytes(charset));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    /**
     * 解密
     */
    @Override
    public String decrypt(String str) throws Exception {
        byte[] passwordBytes = password.getBytes(charset);
        SecretKey secretKey = new SecretKeySpec(passwordBytes, "AES");

        //密码
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        byte[] encrypted1 = Base64.getDecoder().decode(str);
        byte[] original = cipher.doFinal(encrypted1);

        return new String(original, charset);
    }
}
