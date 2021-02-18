package org.noear.solon.extend.sessionstate.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.noear.solon.core.event.EventBus;

import java.security.Key;
import java.util.Date;

/**
 * @author noear
 * @since 1.3
 */
public class JwtUtils {
    private static Key key = null;

    private static Key key() {
        if (key == null) {
            synchronized (JwtSessionStateFactory.getInstance()) {
                if (key == null) {
                    String signKey0 = JwtSessionStateFactory.getInstance().signKey();
                    key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(signKey0));
                }
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
        return buildJwt(claims, expire, key());
    }

    /**
     * 构建令牌
     */
    public static String buildJwt(Claims claims, long expire, Key signKey) {
        if (expire > 0) {
            return Jwts.builder()
                    .setClaims(claims)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + expire))
                    .signWith(signKey).compact();
        } else {
            return Jwts.builder()
                    .setClaims(claims)
                    .setIssuedAt(new Date())
                    .signWith(signKey).compact();
        }
    }

    /**
     * 解析令牌
     */
    public static Claims parseJwt(String token) {
        return parseJwt(token, key());
    }

    /**
     * 解析令牌
     */
    public static Claims parseJwt(String token, Key signKey) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(signKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            EventBus.push(e);
        }

        return null;
    }
}
