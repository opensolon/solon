package org.noear.solon.sm;

import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Base64;

/**
 * SM3 哈希算法 Java实现（基础场景：字符串/字节数组）
 */
class SM3Utils {
    // 静态代码块：注册Bouncy Castle安全提供者
    static {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }
    // 缓冲区大小（1024*4=4KB，可根据文件大小调整，推荐4KB-8KB）
    private static final int BUFFER_SIZE = 4 * 1024;
    /**
     * 计算字符串的SM3摘要（返回base64字符串，编码默认UTF-8）
     * @param content 待计算摘要的字符串
     * @return base64 SM3摘要结果
     */
    public static String calculateSM3(String content) {
        // 1. 字符串转字节数组（指定UTF-8编码，避免中文乱码导致摘要不一致）
        byte[] contentBytes = content.getBytes(StandardCharsets.UTF_8);
        // 2. 调用字节数组SM3计算方法
        return calculateSM3(contentBytes);
    }

    /**
     * 计算字节数组的SM3摘要（返回base64字符串）
     * @param data 待计算摘要的字节数组
     * @return base64 SM3摘要结果
     */
    public static String calculateSM3(byte[] data) {
        // 1. 初始化SM3摘要算法对象
        SM3Digest sm3Digest = new SM3Digest();

        // 2. 传入待计算数据（更新摘要）
        // 支持分段传入：多次调用update()，适合大字节数组分批处理
        sm3Digest.update(data, 0, data.length);

        // 3. 完成哈希计算，获取结果字节数组（32字节=256位）
        byte[] resultBytes = new byte[sm3Digest.getDigestSize()];
        sm3Digest.doFinal(resultBytes, 0);

        // 4. 二进制结果转十六进制字符串（便于存储和展示）
        return Base64.getEncoder().encodeToString(resultBytes);
    }

    // 盐值长度（推荐16字节=32位十六进制，可根据安全需求调整为32/64字节）
    private static final int SALT_LENGTH = 16;

    /**
     * 生成随机盐值（生产环境首选，每个数据对应唯一盐值）
     * @return base64字符串的随机盐值
     */
    public static String generateRandomSalt() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] saltBytes = new byte[SALT_LENGTH];
        secureRandom.nextBytes(saltBytes);
        // 转十六进制字符串，便于存储和传输
        return Base64.getEncoder().encodeToString(saltBytes);
    }

    /**
     * SM3加盐哈希计算（核心方法：盐值+原始数据拼接后计算摘要）
     * @param content 原始数据（字符串）
     * @param salt 盐值（base64字符串）
     * @return 拼接后的数据的SM3摘要（base64字符串）
     */
    public static String calculateSM3WithSalt(String content, String salt) {
        // 1. 校验参数
        if (content == null) content = "";
        if (salt == null || salt.trim().isEmpty()) {
            throw new IllegalArgumentException("盐值不能为空");
        }

        // 2. 拼接规则：盐值（二进制） + 原始数据（二进制）【固定规则，验签时需保持一致】
        byte[] saltBytes = Base64.getDecoder().decode(salt); // base64盐值转二进制
        byte[] contentBytes = content.getBytes(StandardCharsets.UTF_8); // 原始数据转二进制
        byte[] combinedBytes = new byte[saltBytes.length + contentBytes.length];

        // 复制盐值到拼接数组
        System.arraycopy(saltBytes, 0, combinedBytes, 0, saltBytes.length);
        // 复制原始数据到拼接数组
        System.arraycopy(contentBytes, 0, combinedBytes, saltBytes.length, contentBytes.length);

        // 3. 计算SM3摘要（同基础SM3计算逻辑）
        SM3Digest sm3Digest = new SM3Digest();
        sm3Digest.update(combinedBytes, 0, combinedBytes.length);

        byte[] resultBytes = new byte[sm3Digest.getDigestSize()];
        sm3Digest.doFinal(resultBytes, 0);

        // 4. 转base64返回
        return Base64.getEncoder().encodeToString(resultBytes);
    }

    /**
     * 验证SM3加盐哈希（验证原始数据+盐值，是否与目标摘要一致）
     * @param content 待验证的原始数据
     * @param salt 对应的盐值
     * @param targetSm3Hash 目标SM3加盐哈希摘要
     * @return true=验证通过（数据未篡改，盐值匹配），false=验证失败
     */
    public static boolean verifySM3WithSalt(String content, String salt, String targetSm3Hash) {
        if (targetSm3Hash == null || targetSm3Hash.trim().isEmpty()) {
            return false;
        }
        // 重新计算加盐哈希，与目标摘要对比
        String currentSm3Hash = calculateSM3WithSalt(content, salt);
        return currentSm3Hash.equals(targetSm3Hash);
    }
    /**
     * 验证SM3哈希（验证原始数据，是否与目标摘要一致）
     * @param content 待验证的原始数据
     * @param targetSm3Hash 目标SM3哈希摘要
     * @return true=验证通过（数据未篡改），false=验证失败
     */
    public static boolean verifySM3(String content, String targetSm3Hash) {
        if (targetSm3Hash == null || targetSm3Hash.trim().isEmpty()) {
            return false;
        }
        // 重新计算哈希，与目标摘要对比
        String currentSm3Hash = calculateSM3(content);
        return currentSm3Hash.equals(targetSm3Hash);
    }
}
