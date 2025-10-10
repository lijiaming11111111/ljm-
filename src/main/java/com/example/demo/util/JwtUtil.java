package com.example.demo.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

/**
 * @author Refjttria
 *
 * JWT令牌工具
 */
public class JwtUtil {
    /**
     * 生成jwt
     * @param secretKey jwt秘钥
     * @param ttlMillis jwt过期时间(毫秒)
     * @param claims    设置的信息
     */
    public static String createJWT(String secretKey, long ttlMillis, Map<String, Object> claims) {
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Date exp = new Date(nowMillis + ttlMillis);
        JwtBuilder jwtBuilder = Jwts.builder()
                .claims(claims) // 设置载荷信息
                .issuedAt(now) // 签发时间
                .expiration(exp) // 过期时间
                .signWith(key,Jwts.SIG.HS256); // 签名算法以及秘钥
        return jwtBuilder.compact();
    }

    /**
     * Token解密
     *
     * @param secretKey jwt秘钥
     * @param token     加密后的token
     * @return  解密后的信息
     */
    public static Claims parseJWT(String secretKey, String token) {
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        Claims claims = Jwts.parser()   // 创建解析器
                .verifyWith(key)    // 验证秘钥
                .build()    // 构建
                .parseSignedClaims(token)   // 解析token
                .getPayload();  // 获取载荷信息
        return claims;
    }

}
