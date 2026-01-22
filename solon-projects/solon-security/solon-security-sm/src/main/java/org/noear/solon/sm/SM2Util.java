package org.noear.solon.sm;

import org.bouncycastle.asn1.gm.GMNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.SM2Engine;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.*;
import org.bouncycastle.crypto.signers.SM2Signer;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Base64;

/**
 * SM2椭圆曲线公钥算法 Java实现（基于Bouncy Castle）
 */
public class SM2Util {
    // 静态代码块：注册Bouncy Castle安全提供者
    static {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    // 选择SM2标准曲线（国密推荐：sm2p256v1）
    private static final X9ECParameters SM2_CURVE_PARAMS = GMNamedCurves.getByName("sm2p256v1");
    private static final ECDomainParameters SM2_DOMAIN_PARAMS = new ECDomainParameters(
            SM2_CURVE_PARAMS.getCurve(),
            SM2_CURVE_PARAMS.getG(),
            SM2_CURVE_PARAMS.getN(),
            SM2_CURVE_PARAMS.getH()
    );
    /**
     * 默认签名ID
     */
    private static final byte[] SM2_ID = "1234567812345678".getBytes();
    /**
     * 生成SM2密钥对（私钥+公钥）
     * @return 包含私钥和公钥的密钥对对象
     */
    public static AsymmetricCipherKeyPair generateSM2KeyPair() {
        // 初始化密钥对生成器
        ECKeyPairGenerator keyPairGenerator = new ECKeyPairGenerator();
        ECKeyGenerationParameters keyGenParams = new ECKeyGenerationParameters(
                SM2_DOMAIN_PARAMS,
                new SecureRandom() // 安全随机数生成器
        );
        keyPairGenerator.init(keyGenParams);

        // 生成密钥对并返回
        return keyPairGenerator.generateKeyPair();
    }
    /**
     * 获取SM2加密引擎实例 使用新模式
     * @return SM2加密引擎实例
     */
    private static SM2Engine getSM2Engine() {
        SM2Engine sm2Engine = new SM2Engine(SM2Engine.Mode.C1C3C2);
        return sm2Engine;
    }
    /**
     * 适配其他入参的最终调用
     * SM2加密（公钥加密，私钥解密）
     * @param publicKey  SM2公钥
     * @param plainBytes  待加密明文字节数组
     * @return  加密后的结果（byte数组）
     * @throws InvalidCipherTextException 加密失败异常
     */
    public static byte[] sm2Encrypt(ECPublicKeyParameters publicKey, byte[] plainBytes) throws InvalidCipherTextException {
        // 初始化SM2加密引擎（使用默认加密模式）
        SM2Engine sm2Engine = getSM2Engine();
        sm2Engine.init(true, new ParametersWithRandom(publicKey, new SecureRandom()));
        // 执行加密
        byte[] cipherBytes = sm2Engine.processBlock(plainBytes, 0, plainBytes.length);
        return cipherBytes;
    }
    /**
     * SM2加密（公钥加密，私钥解密）
     * @param publicKey  SM2公钥hex字符串
     * @param plainText  待加密明文
     * @return  加密后的结果（byte数组）
     * @throws InvalidCipherTextException 加密失败异常
     */
    public static byte[] sm2Encrypt(String publicKey, String plainText) throws InvalidCipherTextException {
        return sm2Encrypt(SM2KeyHexStorage.hexToPublicKey(publicKey),plainText);
    }
    /**
     * SM2加密（公钥加密，私钥解密）
     * @param publicKey  SM2公钥hex字符串
     * @param plainText  待加密明文
     * @return  加密后的结果（byte数组）
     * @throws InvalidCipherTextException 加密失败异常
     */
    public static String sm2EncryptStr(String publicKey, String plainText) throws InvalidCipherTextException {
        byte[] bytes = sm2Encrypt(SM2KeyHexStorage.hexToPublicKey(publicKey), plainText);
        return Base64.getEncoder().encodeToString(bytes);
    }
    /**
     * SM2加密（公钥加密，私钥解密）
     * @param publicKey  SM2公钥
     * @param plainText  待加密明文
     * @return  加密后的结果（byte数组）
     * @throws InvalidCipherTextException 加密失败异常
     */
    public static byte[] sm2Encrypt(ECPublicKeyParameters publicKey, String plainText) throws InvalidCipherTextException {
        // 明文转字节数组
        byte[] plainBytes = plainText.getBytes(StandardCharsets.UTF_8);
        // 执行加密
        return sm2Encrypt(publicKey,plainBytes);
    }
    /**
     * 2. SM2加密（公钥加密，私钥解密）
     * @param publicKey  SM2公钥
     * @param plainText  待加密明文
     * @return  加密后的结果（base64字符串）
     * @throws InvalidCipherTextException 加密失败异常
     */
    public static String sm2EncryptToBase64(ECPublicKeyParameters publicKey, String plainText) throws InvalidCipherTextException {
        return new String(Base64.getEncoder().encode(sm2Encrypt(publicKey,plainText)));
    }
    /**
     * SM2解密（私钥解密，公钥加密）
     * @param privateKey  SM2私钥
     * @param base64Text  加密后的base64字符串
     * @return  解密后的明文byte数组
     * @throws InvalidCipherTextException 解密失败异常（密钥错误、密文篡改等）
     */
    public static byte[] sm2Decrypt(ECPrivateKeyParameters privateKey, String base64Text) throws InvalidCipherTextException {
        // 初始化SM2解密引擎
        SM2Engine sm2Engine = getSM2Engine();
        sm2Engine.init(false, privateKey);

        // 十六进制密文转字节数组
        byte[] cipherBytes = Base64.getDecoder().decode(base64Text);
        // 执行解密
        return sm2Engine.processBlock(cipherBytes, 0, cipherBytes.length);
    }
    /**
     * SM2解密（私钥解密，公钥加密）
     * @param privateKey  SM2私钥hex字符串
     * @param base64Text  加密后的base64字符串
     * @return  解密后的明文byte数组
     * @throws InvalidCipherTextException 解密失败异常（密钥错误、密文篡改等）
     */
    public static byte[] sm2Decrypt(String privateKey, String base64Text) throws InvalidCipherTextException {
        return sm2Decrypt(SM2KeyHexStorage.hexToPrivateKey(privateKey),base64Text);
    }
    /**
     * SM2解密（私钥解密，公钥加密）
     * @param privateKey  SM2私钥
     * @param base64Text  加密后的base64字符串
     * @return  解密后的明文
     * @throws InvalidCipherTextException 解密失败异常（密钥错误、密文篡改等）
     */
    public static String sm2DecryptToString(ECPrivateKeyParameters privateKey, String base64Text) throws InvalidCipherTextException {
        return new String(sm2Decrypt(privateKey,base64Text), StandardCharsets.UTF_8);
    }
    /**
     * SM2解密（私钥解密，公钥加密）
     * @param privateKey  SM2私钥字符串
     * @param base64Text  加密后的base64字符串
     * @return  解密后的明文
     * @throws InvalidCipherTextException 解密失败异常（密钥错误、密文篡改等）
     */
    public static String sm2DecryptToString(String privateKey, String base64Text) throws InvalidCipherTextException {
        return new String(sm2Decrypt(privateKey,base64Text), StandardCharsets.UTF_8);
    }
    /**
     * SM2签名（私钥签名，用于确认数据完整性和发送方身份）
     * @param privateKey  SM2私钥
     * @param content     待签名的数据
     * @return  签名结果（byte数组）
     */
    public static byte[] sm2Sign(ECPrivateKeyParameters privateKey, String content)throws CryptoException {
        // 初始化SM2签名器
        SM2Signer sm2Signer = new SM2Signer();
        ParametersWithID parametersWithID = new ParametersWithID(
                new ECPrivateKeyParameters(privateKey.getD(), SM2_DOMAIN_PARAMS),
                SM2_ID // SM2签名需要指定ID，默认推荐：1234567812345678
        );
        sm2Signer.init(true, parametersWithID);

        // 待签名数据转字节数组，更新签名器
        byte[] contentBytes = content.getBytes(StandardCharsets.UTF_8);
        sm2Signer.update(contentBytes, 0, contentBytes.length);

        // 执行签名
        byte[] signBytes = null;
        try {
            signBytes = sm2Signer.generateSignature();
        } catch (CryptoException e) {
            throw e;
        }
        return signBytes;
    }
    /**
     * SM2签名（私钥签名，用于确认数据完整性和发送方身份）
     * @param privateKey  SM2私钥hex字符串
     * @param content     待签名的数据
     * @return  签名结果（十六进制字符串）
     */
    public static String sm2SignToBase64(String privateKey, String content)throws CryptoException {
        return new String(Base64.getEncoder().encode(sm2Sign(SM2KeyHexStorage.hexToPrivateKey(privateKey),content)));
    }
    /**
     * SM2签名（私钥签名，用于确认数据完整性和发送方身份）
     * @param privateKey  SM2私钥
     * @param content     待签名的数据
     * @return  签名结果（十六进制字符串）
     */
    public static String sm2SignToBase64(ECPrivateKeyParameters privateKey, String content)throws CryptoException {
        return new String(Base64.getEncoder().encode(sm2Sign(privateKey,content)));
    }
    /**
     * SM2验签（公钥验签，用于验证签名的有效性和数据未被篡改）
     * @param publicKey  SM2公钥
     * @param content    原始待验证数据
     * @param signBase64Text   签名结果（base64字符串）
     * @return  true=验签通过，false=验签失败
     */
    public static boolean sm2Verify(ECPublicKeyParameters publicKey, String content, String signBase64Text) {
        // 初始化SM2签名器（验签模式）
        SM2Signer sm2Signer = new SM2Signer();
        ParametersWithID parametersWithID = new ParametersWithID(
                publicKey,
                SM2_ID // 验签ID必须与签名ID一致
        );
        sm2Signer.init(false, parametersWithID);

        // 原始数据转字节数组，更新签名器
        byte[] contentBytes = content.getBytes(StandardCharsets.UTF_8);
        sm2Signer.update(contentBytes, 0, contentBytes.length);

        // 签名数据转字节数组，执行验签
        byte[] signBytes = Base64.getDecoder().decode(signBase64Text);
        return sm2Signer.verifySignature(signBytes);
    }
    /**
     * SM2验签（公钥验签，用于验证签名的有效性和数据未被篡改）
     * @param publicKey  SM2公钥字符串
     * @param content    原始待验证数据
     * @param signBase64Text   签名结果（base64字符串）
     * @return  true=验签通过，false=验签失败
     */
    public static boolean sm2Verify(String publicKey, String content, String signBase64Text) {
        return sm2Verify(SM2KeyHexStorage.hexToPublicKey(publicKey),content,signBase64Text);
    }
}
