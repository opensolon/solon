package org.noear.solon.sm;

import org.bouncycastle.asn1.gm.GMNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.encoders.Hex;

import java.math.BigInteger;

/**
 * SM2密钥 十六进制格式存储工具（基础方案）
 */
class SM2KeyHexStorage {
    // SM2标准曲线
    private static final X9ECParameters SM2_CURVE_PARAMS = GMNamedCurves.getByName("sm2p256v1");

    /**
     * 私钥序列化（BigInteger → 十六进制字符串）
     */
    public static String privateKeyToHex(ECPrivateKeyParameters privateKey) {
        BigInteger d = privateKey.getD();
        return Hex.toHexString(d.toByteArray());
    }

    /**
     * 公钥序列化（ECPoint → 十六进制字符串）
     */
    public static String publicKeyToHex(ECPublicKeyParameters publicKey) {
        ECPoint q = publicKey.getQ();
        // 压缩格式或非压缩格式，此处选用非压缩格式（兼容性更好）
        byte[] pubBytes = q.getEncoded(false);
        return Hex.toHexString(pubBytes);
    }

    /**
     * 私钥反序列化（十六进制字符串 → ECPrivateKeyParameters）
     */
    public static ECPrivateKeyParameters hexToPrivateKey(String hexPrivateKey) {
        byte[] privBytes = Hex.decode(hexPrivateKey);
        BigInteger d = new BigInteger(1, privBytes);
        return new ECPrivateKeyParameters(d, new ECDomainParameters(SM2_CURVE_PARAMS));
    }

    /**
     * 公钥反序列化（十六进制字符串 → ECPublicKeyParameters）
     */
    public static ECPublicKeyParameters hexToPublicKey(String hexPublicKey) {
        byte[] pubBytes = Hex.decode(hexPublicKey);
        ECPoint q = SM2_CURVE_PARAMS.getCurve().decodePoint(pubBytes);
        return new ECPublicKeyParameters(q, new ECDomainParameters(SM2_CURVE_PARAMS));
    }
}
