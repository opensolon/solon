package org.noear.solon.sm;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;

/**
 * SM国密工具类
 * 包含SM2(非对称加密)、SM3(摘要)、SM4(对称加密)
 * 需要自行依赖org.bouncycastle:bcprov-jdk18on
 * @author 五月
 * @since 1.0
 */
public class SmUtil {

    /**
     * 生成SM2密钥对
     * @return
     * @throws Exception
     */
    public static AsymmetricCipherKeyPair sm2GenerateKeyPair(){
        return SM2Utils.generateSM2KeyPair();
    }
    /**
     * 获取SM2密钥对中的私钥
     * @param keyPair
     * @return
     */
    public static ECPrivateKeyParameters getPrivateKey(AsymmetricCipherKeyPair keyPair){
        return (ECPrivateKeyParameters) keyPair.getPrivate();
    }
    /**
     * 获取SM2密钥对中的公钥
     * @param keyPair
     * @return
     */
    public static ECPublicKeyParameters getPublicKey(AsymmetricCipherKeyPair keyPair){
        return (ECPublicKeyParameters) keyPair.getPublic();
    }
    /**
     * 获取SM2密钥对中的私钥字符串
     * @param keyPair
     * @return
     */
    public static String getPrivateKeyStr(AsymmetricCipherKeyPair keyPair){
        return SM2KeyHexStorage.privateKeyToHex((ECPrivateKeyParameters) keyPair.getPrivate());
    }
    /**
     * 获取SM2密钥对中的公钥字符串
     * @param keyPair
     * @return
     */
    public static String getPublicKeyStr(AsymmetricCipherKeyPair keyPair){
        return SM2KeyHexStorage.publicKeyToHex((ECPublicKeyParameters) keyPair.getPublic());
    }

    /**
     * 使用SM2算法对明文进行加密
     *
     * @param publicKey 公钥字符串，用于加密操作的公钥
     * @param plainText 待加密的明文字符串
     * @return 加密后的密文字符串
     * @throws InvalidCipherTextException 当加密过程中出现无效密文时抛出此异常
     */
    public static String sm2Encrypt(String publicKey, String plainText) throws InvalidCipherTextException {
        return SM2Utils.sm2EncryptStr(publicKey, plainText);
    }

    /**
     * 使用SM2算法解密Base64编码的密文
     *
     * @param privateKey SM2私钥，用于解密操作
     * @param base64Text 经过Base64编码的密文字符串
     * @return 解密后的明文字符串
     * @throws InvalidCipherTextException 当密文格式错误或解密失败时抛出此异常
     */
    public static String sm2Decrypt(String privateKey, String base64Text) throws InvalidCipherTextException {
        return SM2Utils.sm2DecryptToString(privateKey, base64Text);
    }
    /**
     * 使用SM2算法对内容进行签名
     *
     * @param privateKey 私钥，用于签名操作的私钥字符串
     * @param content 待签名的内容，需要进行SM2签名的原始数据
     * @return 返回签名结果字符串
     * @throws Exception 签名过程中可能出现的异常
     */
    public static String sm2Sign(String privateKey, String content) throws Exception{
        return SM2Utils.sm2SignToBase64(privateKey, content);
    }
    /**
     * SM2数字签名验证方法
     * 使用SM2算法对给定的内容和签名进行验证
     *
     * @param publicKey SM2公钥字符串
     * @param content 待验证的原始内容字符串
     * @param signBase64Text Base64编码的签名数据字符串
     * @return 验证结果，true表示签名有效，false表示签名无效
     */
    public static boolean sm2Verify(String publicKey, String content, String signBase64Text){
        return SM2Utils.sm2Verify(publicKey, content, signBase64Text);
    }
    /**
     * 生成随机盐值（生产环境首选，每个数据对应唯一盐值）
     * @return base64字符串的随机盐值
     */
    public static String generateRandomSalt() {
        return SM3Utils.generateRandomSalt();
    }
    /**
     * 计算字符串的SM3哈希值
     *
     * @param content 待计算哈希值的字符串内容
     * @return 返回计算得到的SM3哈希值字符串
     */
    public static String calculateSM3(String content){
        return SM3Utils.calculateSM3(content);
    }

    /**
     * 使用SM3加盐哈希计算
     *
     * @param content 原始数据
     * @param salt 盐值
     * @return 拼接后的数据的SM3摘要（base64字符串）
     */
    public static String calculateSM3WithSalt(String content, String salt){
        return SM3Utils.calculateSM3WithSalt(content,salt);
    }
    /**
     * 验证SM3哈希（验证原始数据，是否与目标摘要一致）
     * @param content 待验证的原始数据
     * @param targetSm3Hash 目标SM3哈希摘要
     * @return true=验证通过（数据未篡改），false=验证失败
     */
    public static boolean verifySM3(String content, String targetSm3Hash){
        return SM3Utils.verifySM3(content, targetSm3Hash);
    }
    /**
     * 验证SM3加盐哈希（验证原始数据+盐值，是否与目标摘要一致）
     * @param content 待验证的原始数据
     * @param salt 对应的盐值
     * @param targetSm3Hash 目标SM3加盐哈希摘要
     * @return true=验证通过（数据未篡改，盐值匹配），false=验证失败
     */
    public static boolean verifySM3WithSalt(String content, String salt, String targetSm3Hash) {
        return SM3Utils.verifySM3WithSalt(content, salt, targetSm3Hash);
    }
    /**
     * 使用SM4算法进行ECB模式加密
     *
     * @param plainText 待加密的明文字符串
     * @param key 加密密钥
     * @return 经过Base64编码的加密结果字符串
     * @throws InvalidCipherTextException 当加密过程中出现无效密文时抛出此异常
     */
    public static String sm4EcbEncrypt(String plainText, String key) throws InvalidCipherTextException {
        return SM4Utils.sm4EcbEncryptToBase64(plainText, key);
    }
    /**
     * SM4算法ECB模式解密方法
     * 使用提供的密钥对Base64编码的密文进行SM4 ECB模式解密
     *
     * @param cipherTextBase64 Base64编码的密文字符串
     * @param key 解密密钥
     * @return 解密后的明文字符串
     * @throws InvalidCipherTextException 当密文格式错误或解密失败时抛出此异常
     */
    public static String sm4EcbDecrypt(String cipherTextBase64, String key) throws InvalidCipherTextException {
        return SM4Utils.sm4EcbDecryptFromBase64(cipherTextBase64, key);
    }

    /**
     * SM4 CBC模式加密函数
     * 使用SM4算法对明文进行CBC模式加密，并将结果转换为Base64编码字符串
     *
     * @param plainText 待加密的明文字符串
     * @param key 加密密钥字符串
     * @param iv 初始化向量字符串
     * @return 经过SM4 CBC加密并Base64编码后的密文字符串
     * @throws InvalidCipherTextException 当加密过程中出现错误时抛出此异常
     */
    public static String sm4CbcEncrypt(String plainText, String key, String iv) throws InvalidCipherTextException {
        return SM4Utils.sm4CbcEncryptToBase64(plainText, key, iv);
    }
    /**
     * SM4 CBC模式解密方法
     * 使用SM4算法对经过Base64编码的密文进行CBC模式解密
     *
     * @param cipherTextBase64 经过Base64编码的密文字符串
     * @param key 解密密钥
     * @param iv 初始化向量
     * @return 解密后的明文字符串
     * @throws InvalidCipherTextException 当密文格式无效或解密失败时抛出此异常
     */
    public static String sm4CbcDecrypt(String cipherTextBase64, String key, String iv) throws InvalidCipherTextException{
        return SM4Utils.sm4CbcDecryptFromBase64(cipherTextBase64, key, iv);
    }

    /**
     * 生成SM4随机密钥
     *
     * @return 返回生成的SM4随机密钥字符串
     */
    public static String sm4GenerateRandomKey(){
        return SM4Utils.generateSM4RandomKey();
    }

    /**
     * 生成SM4算法的随机初始化向量(IV)
     *
     * @return 返回生成的SM4随机初始化向量字符串
     */
    public static String sm4GenerateRandomIV(){
        return SM4Utils.generateSM4RandomIV();
    }

}
