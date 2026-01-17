package org.noear.solon.sm;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.noear.solon.sm.SM2KeyHexStorage.*;

class SM2KeyHexStorageTest {

    @Test
    public void case1() {
        // 1. 生成SM2密钥对
        AsymmetricCipherKeyPair keyPair = SM2Utils.generateSM2KeyPair(); // 复用之前的SM2Utils.generateSM2KeyPair()
        ECPrivateKeyParameters privateKey = (ECPrivateKeyParameters) keyPair.getPrivate();
        ECPublicKeyParameters publicKey = (ECPublicKeyParameters) keyPair.getPublic();

        // 2. 序列化（密钥 → 十六进制字符串）
        String hexPriv = privateKeyToHex(privateKey);
        String hexPub = publicKeyToHex(publicKey);
        System.out.println("私钥（十六进制）：" + hexPriv);
        System.out.println("公钥（十六进制）：" + hexPub);

        // 3. 反序列化（十六进制字符串 → 密钥对象）
        ECPrivateKeyParameters restorePriv = hexToPrivateKey(hexPriv);
        ECPublicKeyParameters restorePub = hexToPublicKey(hexPub);

        // 4. 验证一致性（私钥D值对比，公钥Q点对比）
        boolean privEqual = privateKey.getD().equals(restorePriv.getD());
        boolean pubEqual = publicKey.getQ().equals(restorePub.getQ());
        System.out.println("私钥恢复一致性：" + privEqual);
        System.out.println("公钥恢复一致性：" + pubEqual);
        Assertions.assertTrue(privEqual);
        Assertions.assertTrue(pubEqual);

    }
}