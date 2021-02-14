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
    private static Key signKey = null;

    private static Key key() {
        if (signKey == null) {
            synchronized (JwtSessionStateFactory.getInstance()) {
                if (signKey == null) {
                    String signKey0 = JwtSessionStateFactory.getInstance().signKey();
                    signKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(signKey0));
                }
            }
        }

        return signKey;
    }

    /**
     * 生成密钥
     * */
    public static String createKey() {
        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

        return Encoders.BASE64.encode(key.getEncoded());
    }

    /**
     * 构建令牌
     * */
    public static String buildJwt(Claims claims, long expire) {
        if (expire > 0) {
            return Jwts.builder()
                    .setClaims(claims)
                    .setExpiration(new Date(System.currentTimeMillis() + expire))
                    .signWith(key()).compact();
        } else {
            return Jwts.builder()
                    .setClaims(claims)
                    .signWith(key()).compact();
        }
    }

    /**
     * 解析令牌
     * */
    public static Claims parseJwt(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            EventBus.push(e);
        }

        return null;
    }
}
