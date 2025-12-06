#!/bin/bash

# =========================================================
# 脚本名称: generate_wss_keystore.sh
# 功能: 为 WSS/SSL 服务器生成 keystore.jks，并包含 SAN 扩展以解决主机名验证错误。
# =========================================================

# --- 密钥库和密钥密码 ---
KEY_PASS="123456"
STORE_PASS="123456"
ALIAS_NAME="certificateKey"
KEYSTORE_FILE="keystore.jks"
TRUSTSTORE_FILE="trustKeystore.jks"
CERT_FILE="public.crt"

echo "--- 准备生成证书文件 ---"

# 1. 检查并清理旧文件
rm -f "$KEYSTORE_FILE" "$TRUSTSTORE_FILE" "$CERT_FILE"

# 2. 生成密钥对 (KeyPair) 并包含 SAN 扩展
# -dname 用于定义证书主体信息，CN=localhost 用于通用名称
# -ext SAN=... 用于添加 Subject Alternative Name，这是解决 "No name matching localhost found" 的关键
keytool -genkeypair \
    -alias "$ALIAS_NAME" \
    -dname "CN=localhost, OU=Dev, O=SocketD, C=CN" \
    -keypass "$KEY_PASS" \
    -storepass "$STORE_PASS" \
    -keyalg RSA \
    -keysize 2048 \
    -validity 36500 \
    -keystore "$KEYSTORE_FILE" \
    -ext "SAN=DNS:localhost,IP:127.0.0.1"

echo "✅ [1/4] 成功生成密钥库: $KEYSTORE_FILE"

# 3. 导出公共证书 (Public Certificate)
keytool -export \
    -alias "$ALIAS_NAME" \
    -storepass "$STORE_PASS" \
    -keystore "$KEYSTORE_FILE" \
    -rfc -file "$CERT_FILE"

echo "✅ [2/4] 成功导出公共证书: $CERT_FILE"

# 4. 生成信任库 (Truststore)，并导入公共证书
# 客户端需要这个 Truststore 来信任服务器的自签名证书
keytool -import \
    -alias "$ALIAS_NAME" \
    -storepass "$STORE_PASS" \
    -noprompt \
    -file "$CERT_FILE" \
    -keystore "$TRUSTSTORE_FILE"

echo "✅ [3/4] 成功生成信任库: $TRUSTSTORE_FILE"

# 5. 打印证书信息进行验证（确认 SAN 已添加）
echo "--- 验证证书详情 (查找 SAN 字段) ---"
keytool -list -v -storepass "$STORE_PASS" -keystore "$KEYSTORE_FILE" | grep -A 5 "SubjectAlternativeName"

echo "--- 证书生成完成 ---"