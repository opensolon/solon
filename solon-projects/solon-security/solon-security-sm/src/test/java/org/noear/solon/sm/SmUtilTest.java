package org.noear.solon.sm;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * 国密工具类单元测试
 */
class SmUtilTest {

    private static String plainText = "hello world";
    private static String privateKey;
    private static String publicKey;
    @BeforeEach
    void setUp() {
        //生成密钥对
        AsymmetricCipherKeyPair keyPair = SmUtil.sm2GenerateKeyPair();
        privateKey = SmUtil.getPrivateKeyStr(keyPair);
        publicKey = SmUtil.getPublicKeyStr(keyPair);
    }
    /**
     * 测试SM2加解密、签名、验签
     */
    @Test
    public void case1() throws Exception {
        //加密
        String sm2Encrypt = SmUtil.sm2Encrypt(publicKey, plainText);
        //解密
        String sm2Decrypt = SmUtil.sm2Decrypt(privateKey, sm2Encrypt);
        //断言解密内容与加密内容一致
        Assertions.assertEquals(plainText, sm2Decrypt);
        //签名
        String sign = SmUtil.sm2Sign(privateKey, plainText);
        //验签
        boolean verify = SmUtil.sm2Verify(publicKey, plainText, sign);
        Assertions.assertTrue(verify);

    }
    /**
     * 测试SM3
     */
    @Test
    public void case2()  {
        String sm3 = SmUtil.calculateSM3(plainText);
        boolean verifySM3 = SmUtil.verifySM3(plainText, sm3);
        Assertions.assertTrue(verifySM3);
        String salt = SmUtil.generateRandomSalt();
        String sm3WithSalt = SmUtil.calculateSM3WithSalt(plainText, salt);
        boolean verifySM3WithSalt = SmUtil.verifySM3WithSalt(plainText, salt, sm3WithSalt);
        Assertions.assertTrue(verifySM3WithSalt);
    }
    /**
     * 测试SM4
     */
    @Test
    public void case3() throws InvalidCipherTextException {
        String generateRandomKey = SmUtil.sm4GenerateRandomKey();
        String generateRandomIV = SmUtil.sm4GenerateRandomIV();
        String sm4EcbEncrypt = SmUtil.sm4EcbEncrypt(plainText, generateRandomKey);
        String sm4EcbDecrypt = SmUtil.sm4EcbDecrypt(sm4EcbEncrypt, generateRandomKey);
        Assertions.assertEquals(plainText, sm4EcbDecrypt);
        String sm4CbcEncrypt = SmUtil.sm4CbcEncrypt(plainText, generateRandomKey, generateRandomIV);
        String sm4CbcDecrypt = SmUtil.sm4CbcDecrypt(sm4CbcEncrypt, generateRandomKey, generateRandomIV);
        Assertions.assertEquals(plainText, sm4CbcDecrypt);
    }
}