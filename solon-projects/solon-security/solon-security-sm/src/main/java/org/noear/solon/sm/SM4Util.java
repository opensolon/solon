package org.noear.solon.sm;

import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.SM4Engine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Base64;

/**
 * SM4 对称加密算法 Java实现（输出格式：Base64，支持ECB/CBC模式）
 */
public class SM4Util {
    // 静态代码块：注册Bouncy Castle安全提供者
    static {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    // SM4 固定配置：密钥长度16字节（128位），IV长度16字节（仅CBC模式需要）
    private static final int SM4_KEY_LENGTH = 16;
    private static final int SM4_IV_LENGTH = 16;

    // 原生Base64编码器/解码器（推荐：URL安全可选，此处使用标准Base64）
    private static final Base64.Encoder BASE64_ENCODER = Base64.getEncoder();
    private static final Base64.Decoder BASE64_DECODER = Base64.getDecoder();

    /**
     * 生成SM4随机密钥（16字节=32位十六进制，加密/解密需使用同一密钥）
     * @return 十六进制格式的SM4密钥（便于存储密钥，密文使用Base64）
     */
    public static String generateSM4RandomKey() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] keyBytes = new byte[SM4_KEY_LENGTH];
        secureRandom.nextBytes(keyBytes);
        // 密钥仍用十六进制存储（便于手动核对，密文用Base64）
        return bytesToHex(keyBytes).toUpperCase();
    }

    /**
     * 生成CBC模式随机IV（16字节=32位十六进制）
     * @return 十六进制格式的IV（加密/解密需使用同一IV）
     */
    public static String generateSM4RandomIV() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] ivBytes = new byte[SM4_IV_LENGTH];
        secureRandom.nextBytes(ivBytes);
        return bytesToHex(ivBytes).toUpperCase();
    }

    // ==================== SM4 ECB 模式（无IV，Base64输出/输入） ====================
    /**
     * SM4 ECB 加密（输出Base64格式密文）
     * @param plainText 待加密明文
     * @param key 十六进制格式SM4密钥（32位，16字节）
     * @return Base64格式密文
     * @throws Exception 加密失败异常（密钥非法、数据格式错误等）
     */
    public static String sm4EcbEncryptToBase64(String plainText, String key) throws InvalidCipherTextException {
        // 1. 校验参数
        if (plainText == null) plainText = "";
        if (key == null || key.length() != 32) {
            throw new IllegalArgumentException("SM4 ECB密钥必须是32位十六进制字符串（16字节）");
        }

        // 2. 转换数据格式：密钥→二进制，明文→二进制
        byte[] keyBytes = hexToBytes(key);
        byte[] plainBytes = plainText.getBytes(StandardCharsets.UTF_8);

        // 3. 初始化SM4 ECB加密器（PKCS7填充）
        BufferedBlockCipher cipher = new PaddedBufferedBlockCipher(
                new SM4Engine(), // SM4 核心引擎
                new PKCS7Padding() // 标准填充方式
        );
        cipher.init(true, new KeyParameter(keyBytes)); // true=加密模式，传入密钥参数

        // 4. 执行加密，获取二进制密文
        byte[] cipherBytes = processBlockToBytes(cipher, plainBytes);

        // 5. 二进制密文→Base64字符串（核心：替换十六进制转换为Base64）
        return BASE64_ENCODER.encodeToString(cipherBytes);
    }

    /**
     * SM4 ECB 解密（输入Base64格式密文）
     * @param cipherTextBase64 Base64格式密文
     * @param key 十六进制格式SM4密钥（32位，16字节）
     * @return 解密后的明文
     * @throws Exception 解密失败异常（密钥错误、密文篡改、格式错误等）
     */
    public static String sm4EcbDecryptFromBase64(String cipherTextBase64, String key) throws InvalidCipherTextException {
        // 1. 校验参数
        if (cipherTextBase64 == null || cipherTextBase64.trim().isEmpty()) {
            throw new IllegalArgumentException("Base64密文不能为空");
        }
        if (key == null || key.length() != 32) {
            throw new IllegalArgumentException("SM4 ECB密钥必须是32位十六进制字符串（16字节）");
        }

        // 2. 转换数据格式：密钥→二进制，Base64密文→二进制
        byte[] keyBytes = hexToBytes(key);
        byte[] cipherBytes = BASE64_DECODER.decode(cipherTextBase64); // 核心：Base64→二进制

        // 3. 初始化SM4 ECB解密器（PKCS7填充）
        BufferedBlockCipher cipher = new PaddedBufferedBlockCipher(
                new SM4Engine(),
                new PKCS7Padding()
        );
        cipher.init(false, new KeyParameter(keyBytes)); // false=解密模式，传入密钥参数

        // 4. 执行解密，获取二进制明文
        byte[] plainBytes = processBlockToBytes(cipher, cipherBytes);

        // 5. 二进制明文→明文字符串
        return new String(plainBytes, StandardCharsets.UTF_8);
    }

    // ==================== SM4 CBC 模式（需IV，Base64输出/输入，生产环境首选） ====================
    /**
     * SM4 CBC 加密（输出Base64格式密文）
     * @param plainText 待加密明文
     * @param key 十六进制格式SM4密钥（32位，16字节）
     * @param iv 十六进制格式IV（32位，16字节）
     * @return Base64格式密文
     * @throws Exception 加密失败异常
     */
    public static String sm4CbcEncryptToBase64(String plainText, String key, String iv) throws InvalidCipherTextException {
        // 1. 校验参数
        if (plainText == null) plainText = "";
        if (key == null || key.length() != 32) {
            throw new IllegalArgumentException("SM4 CBC密钥必须是32位十六进制字符串（16字节）");
        }
        if (iv == null || iv.length() != 32) {
            throw new IllegalArgumentException("SM4 CBC IV必须是32位十六进制字符串（16字节）");
        }

        // 2. 转换数据格式：密钥→二进制，IV→二进制，明文→二进制
        byte[] keyBytes = hexToBytes(key);
        byte[] ivBytes = hexToBytes(iv);
        byte[] plainBytes = plainText.getBytes(StandardCharsets.UTF_8);

        // 3. 初始化SM4 CBC加密器（PKCS7填充）
        BufferedBlockCipher cipher = new PaddedBufferedBlockCipher(
                new CBCBlockCipher(new SM4Engine()), // CBC 分组模式包装SM4引擎
                new PKCS7Padding()
        );
        cipher.init(true, new ParametersWithIV(new KeyParameter(keyBytes), ivBytes)); // 传入密钥+IV参数

        // 4. 执行加密，获取二进制密文
        byte[] cipherBytes = processBlockToBytes(cipher, plainBytes);

        // 5. 二进制密文→Base64字符串
        return BASE64_ENCODER.encodeToString(cipherBytes);
    }

    /**
     * SM4 CBC 解密（输入Base64格式密文）
     * @param cipherTextBase64 Base64格式密文
     * @param key 十六进制格式SM4密钥（32位，16字节）
     * @param iv 十六进制格式IV（32位，16字节，必须与加密时一致）
     * @return 解密后的明文
     * @throws Exception 解密失败异常（密钥/IV错误、密文篡改等）
     */
    public static String sm4CbcDecryptFromBase64(String cipherTextBase64, String key, String iv) throws InvalidCipherTextException {
        // 1. 校验参数
        if (cipherTextBase64 == null || cipherTextBase64.trim().isEmpty()) {
            throw new IllegalArgumentException("Base64密文不能为空");
        }
        if (key == null || key.length() != 32) {
            throw new IllegalArgumentException("SM4 CBC密钥必须是32位十六进制字符串（16字节）");
        }
        if (iv == null || iv.length() != 32) {
            throw new IllegalArgumentException("SM4 CBC IV必须是32位十六进制字符串（16字节）");
        }

        // 2. 转换数据格式：密钥→二进制，IV→二进制，Base64密文→二进制
        byte[] keyBytes = hexToBytes(key);
        byte[] ivBytes = hexToBytes(iv);
        byte[] cipherBytes = BASE64_DECODER.decode(cipherTextBase64);

        // 3. 初始化SM4 CBC解密器（PKCS7填充）
        BufferedBlockCipher cipher = new PaddedBufferedBlockCipher(
                new CBCBlockCipher(new SM4Engine()),
                new PKCS7Padding()
        );
        cipher.init(false, new ParametersWithIV(new KeyParameter(keyBytes), ivBytes)); // 传入密钥+IV参数

        // 4. 执行解密，获取二进制明文
        byte[] plainBytes = processBlockToBytes(cipher, cipherBytes);

        // 5. 二进制明文→明文字符串
        return new String(plainBytes, StandardCharsets.UTF_8);
    }

    // ==================== 通用辅助方法 ====================
    /**
     * 通用块加密/解密处理方法（返回二进制数据，适配Base64转换）
     * @param cipher 初始化后的BufferedBlockCipher
     * @param data 待处理的二进制数据（明文/密文）
     * @return 处理后的二进制结果
     * @throws Exception 处理失败异常
     */
    private static byte[] processBlockToBytes(BufferedBlockCipher cipher, byte[] data) throws InvalidCipherTextException {
        int inputLength = data.length;
        // 1. 计算输出缓冲区长度（加密后长度可能增加，预留足够空间）
        int outputLength = cipher.getOutputSize(inputLength);
        byte[] outputBytes = new byte[outputLength];

        // 2. 处理数据
        int processedLength = cipher.processBytes(data, 0, inputLength, outputBytes, 0);
        // 3. 完成最后一块数据处理（处理填充数据）
        processedLength += cipher.doFinal(outputBytes, processedLength);

        // 4. 截取有效数据（去除多余的预留空间）
        byte[] resultBytes = new byte[processedLength];
        System.arraycopy(outputBytes, 0, resultBytes, 0, processedLength);

        return resultBytes;
    }

    /**
     * 二进制字节数组→十六进制字符串（用于密钥/IV的存储和展示）
     * @param bytes 二进制字节数组
     * @return 十六进制字符串
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xFF & b);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    /**
     * 十六进制字符串→二进制字节数组（用于密钥/IV的解析）
     * @param hex 十六进制字符串
     * @return 二进制字节数组
     */
    private static byte[] hexToBytes(String hex) {
        int length = hex.length();
        byte[] bytes = new byte[length / 2];
        for (int i = 0; i < length; i += 2) {
            bytes[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return bytes;
    }
}
