package org.noear.solon.extend.sessionstate.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.noear.solon.Utils;
import org.noear.solon.core.event.EventBus;

import java.security.Key;
import java.util.Date;

public class JwtUtils {
    private static String encrypt_str = "DHPjbM5QczZ2cysd4gpDbG/4SnuwzWX3sA1i6AXiAbo=";
    private static Key encrypt_key = null;

    private static Key key() {
        if (encrypt_key == null) {
            synchronized (encrypt_str.intern()) {
                if (encrypt_key == null) {
                    encrypt_key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(encrypt_str));
                }
            }
        }

        return encrypt_key;
    }

    public static String buildJwt(Claims claims, long expire) {
        if (expire > 0) {
            return Jwts.builder()
                    .setClaims(claims)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + expire))
                    .signWith(key()).compact();
        } else {
            return Jwts.builder()
                    .setClaims(claims)
                    .signWith(key()).compact();
        }
    }

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
