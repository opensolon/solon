package org.noear.solon.extend.sessionstate.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.noear.solon.Utils;

import java.security.Key;
import java.util.Date;

public class JwtUtils {
    public static final String SUBJECT = "RookieLi";

    public static final long EXPIRE = 1000 * 60 * 60 * 24 * 7;  //过期时间，毫秒，一周

    public static String getJwt(Claims claims, Key key) {


        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE))
                .setId(Utils.guid())
                .signWith(key).compact();

        return token;
    }
    public static Claims chekcJwt(String token, Key key) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
