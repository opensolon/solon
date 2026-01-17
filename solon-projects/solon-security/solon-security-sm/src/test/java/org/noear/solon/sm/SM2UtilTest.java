package org.noear.solon.sm;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.noear.solon.sm.SM2Util.*;
class SM2UtilTest {
    @Test
    public void case1() throws CryptoException {
        // 步骤1：生成SM2密钥对
        AsymmetricCipherKeyPair keyPair = generateSM2KeyPair();
        ECPrivateKeyParameters privateKey = (ECPrivateKeyParameters) keyPair.getPrivate();
        ECPublicKeyParameters publicKey = (ECPublicKeyParameters) keyPair.getPublic();
        System.out.println("SM2密钥对生成成功");
        String privateKeyToHex = SM2KeyHexStorage.privateKeyToHex(privateKey);
        String publicKeyToHex = SM2KeyHexStorage.publicKeyToHex(publicKey);
        System.out.println("私钥（十六进制）：" + privateKeyToHex);
        System.out.println("公钥（十六进制）：" + publicKeyToHex);
        // 步骤2：待加密/签名的原始数据
        String originalText = "这是一段测试SM2算法的明文数据";
        System.out.println("原始明文：" + originalText);

        // 步骤3：SM2加密
        String cipherText = sm2EncryptToBase64(publicKey, originalText);
        System.out.println("加密后密文base64格式：" + cipherText);

        // 步骤4：SM2解密
        String decryptText = sm2DecryptToString(privateKey, cipherText);
        System.out.println("解密后明文：" + decryptText);

        // 步骤5：SM2签名
        String base64SignText = sm2SignToBase64(privateKey, originalText);
        System.out.println("签名结果（base64）：" + base64SignText);

        // 步骤6：SM2验签
        boolean verifyResult = sm2Verify(publicKey, originalText, base64SignText);
        System.out.println("验签结果：" + (verifyResult ? "通过" : "失败"));
        Assertions.assertTrue(verifyResult);


    }
}