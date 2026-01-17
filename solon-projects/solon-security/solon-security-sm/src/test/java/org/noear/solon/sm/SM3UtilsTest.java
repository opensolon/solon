package org.noear.solon.sm;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.noear.solon.sm.SM3Utils.*;

class SM3UtilsTest {

    @Test
    public void case1() {
        // 测试场景1：普通字符串
        String testStr1 = "这是一段测试SM3哈希算法的普通文本";
        String sm3Result1 = calculateSM3(testStr1);
        System.out.println("普通字符串SM3摘要：" + sm3Result1);
        System.out.println("摘要长度（字符数）：" + sm3Result1.length()); // 64位（对应32字节）

        // 测试场景2：空字符串
        String testStr2 = "";
        String sm3Result2 = calculateSM3(testStr2);
        System.out.println("空字符串SM3摘要：" + sm3Result2);

        // 测试场景3：微小差异字符串（验证雪崩效应）
        String testStr3 = "这是一段测试SM3哈希算法的普通文本_微小差异";
        String sm3Result3 = calculateSM3(testStr3);
        System.out.println("差异字符串SM3摘要：" + sm3Result3);

        // 验证：相同输入是否生成相同摘要（多次计算一致性）
        String sm3Result1Repeat = calculateSM3(testStr1);
        System.out.println("普通字符串重复计算SM3摘要：" + sm3Result1Repeat);
        System.out.println("两次计算结果是否一致：" + sm3Result1.equals(sm3Result1Repeat));

    }
    @Test
    public void case2() {
        // 原始测试数据（如用户密码、敏感文本）
        String originalContent = "123456Abc!";
        System.out.println("原始数据：" + originalContent);

        // 场景1：固定盐值（测试/调试场景，可复现结果）
        String fixedSalt = "A1B2C3D4E5F60102030405060708090A"; // 16字节十六进制盐值
        String sm3HashWithFixedSalt = calculateSM3WithSalt(originalContent, fixedSalt);
        System.out.println("\n=== 固定盐值场景 ===");
        System.out.println("固定盐值：" + fixedSalt);
        System.out.println("SM3加盐哈希：" + sm3HashWithFixedSalt);
        System.out.println("验证结果（正确数据+正确盐值）：" + verifySM3WithSalt(originalContent, fixedSalt, sm3HashWithFixedSalt));
        System.out.println("验证结果（错误数据+正确盐值）：" + verifySM3WithSalt("123456Abc", fixedSalt, sm3HashWithFixedSalt));

        // 场景2：随机盐值（生产环境场景，每个数据唯一盐值）
        String randomSalt = generateRandomSalt();
        String sm3HashWithRandomSalt = calculateSM3WithSalt(originalContent, randomSalt);
        System.out.println("\n=== 随机盐值场景 ===");
        System.out.println("随机盐值：" + randomSalt);
        System.out.println("SM3加盐哈希：" + sm3HashWithRandomSalt);
        System.out.println("验证结果（正确数据+正确随机盐值）：" + verifySM3WithSalt(originalContent, randomSalt, sm3HashWithRandomSalt));
        System.out.println("验证结果（正确数据+错误随机盐值）：" + verifySM3WithSalt(originalContent, generateRandomSalt(), sm3HashWithRandomSalt));

    }
}