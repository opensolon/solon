/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.sessionstate.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Key;
import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author noear
 * @since 1.3
 */
public class JwtUtils {
    static final Logger log = LoggerFactory.getLogger(JwtUtils.class);

    private static String TOKEN_HEADER = "Bearer ";
    private static Key key = null;
    private static final ReentrantLock SYNC_LOCK = new ReentrantLock();

    /**
     * 根据配置获取签名密钥
     */
    private static Key getKey() {
        if (key == null) {
            SYNC_LOCK.lock();
            try {
                if (key == null) {
                    String signKey0 = JwtSessionStateFactory.getInstance().signKey();
                    key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(signKey0));
                }
            } finally {
                SYNC_LOCK.unlock();
            }
        }

        return key;
    }

    /**
     * 生成密钥
     */
    public static String createKey() {
        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

        return Encoders.BASE64.encode(key.getEncoded());
    }

    public static String buildJwt(Claims claims, long expire) {
        return buildJwt(claims, expire, getKey());
    }

    /**
     * 构建令牌
     *
     * @param claims  数据
     * @param expire  超时（单位：毫秒）
     * @param signKey 签名密钥
     */
    public static String buildJwt(Claims claims, long expire, Key signKey) {
        JwtBuilder builder;
        if (expire > 0) {
            builder = Jwts.builder()
                    .setClaims(claims)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + expire));
        } else {
            builder = Jwts.builder()
                    .setClaims(claims)
                    .setIssuedAt(new Date());
        }

        if (Utils.isNotEmpty(Solon.cfg().appName())) {
            builder.setIssuer(Solon.cfg().appName());
        }

        if (Utils.isNotEmpty(JwtSessionProps.getInstance().prefix)) {
            return JwtSessionProps.getInstance().prefix + " " + builder.signWith(signKey).compact();
        } else {
            return builder.signWith(signKey).compact();
        }
    }

    /**
     * 解析令牌
     *
     * @param token 令牌
     */
    public static Claims parseJwt(String token) {
        return parseJwt(token, getKey());
    }

    /**
     * 解析令牌
     *
     * @param token   令牌
     * @param signKey 签名密钥
     */
    public static Claims parseJwt(String token, Key signKey) {
        if (token.startsWith(TOKEN_HEADER)) {
            token = token.substring(TOKEN_HEADER.length()).trim();
        }

        if (Utils.isNotEmpty(JwtSessionProps.getInstance().prefix) && token.startsWith(JwtSessionProps.getInstance().prefix)) {
            token = token.substring(JwtSessionProps.getInstance().prefix.length()).trim();
        }

        try {
            return Jwts.parserBuilder()
                    .setSigningKey(signKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException ex) {

        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }

        return null;
    }
}
