package org.noear.solon.sessionstate.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.event.EventBus;

import java.security.Key;
import java.util.Date;

/**
 * @author noear
 * @since 1.3
 */
public class JwtUtils {
    private static String TOKEN_HEADER = "Bearer ";
    private static Key key = null;

    /**
     * 根据配置获取签名密钥
     * */
    private static Key getKey() {
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

        if (Utils.isNotEmpty(SessionProp.session_jwt_prefix)) {
            return SessionProp.session_jwt_prefix + " " + builder.signWith(signKey).compact();
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

        if (Utils.isNotEmpty(SessionProp.session_jwt_prefix) && token.startsWith(SessionProp.session_jwt_prefix)) {
            token = token.substring(SessionProp.session_jwt_prefix.length()).trim();
        }

        try {
            return Jwts.parserBuilder()
                    .setSigningKey(signKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException ex) {

        } catch (Throwable e) {
            EventBus.pushTry(e);
        }

        return null;
    }
}
