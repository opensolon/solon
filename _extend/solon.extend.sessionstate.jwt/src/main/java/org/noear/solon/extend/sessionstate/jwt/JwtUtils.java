package org.noear.solon.extend.sessionstate.jwt;

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
    public static String buildJwt(Claims claims, long expire, Key secret) {
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
            return SessionProp.session_jwt_prefix + " " + builder.signWith(secret).compact();
        } else {
            return builder.signWith(secret).compact();
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

        } catch (Exception ex) {
            EventBus.push(ex);
        }

        return null;
    }
}
