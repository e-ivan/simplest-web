package cn.soboys.restapispringbootstarter.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.MacAlgorithm;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

/**
 * @author 公众号 程序员三时
 * @version 1.0
 * @date 2023/7/13 20:09
 * @webSite https://github.com/coder-amiao
 */
public class JwtUtil {

    //创建jwt
    public static String createJWT(String subject, String issue, Object claim,
                                   long ttlMillis) {


        //过期时间
        Date now = new Date();
        Date exp = new Date(now.getTime() + ttlMillis * 1000);


        //设置主题
        //发行者
        //jwtID
        //设置过期日期
        //主题，可以包含用户信息
        //加密算法
        //对载荷进行压缩
        return Jwts.builder()
                .subject(subject) //设置主题
                .issuer(issue) //发行者
                .id(issue)//jwtID
                .expiration(exp)//设置过期日期
                .claim("user", claim)//主题，可以包含用户信息
                .signWith(getSignedKey())//加密算法
                .compressWith(Jwts.ZIP.DEF).compact();
    }


    public static String createJWT(String subject, String issue, Object claim,
                                   long ttlMillis, String key) {

        //过期时间
        Date now = new Date();
        Date exp = new Date(now.getTime() + ttlMillis * 1000);

        String result = Jwts.builder()
                .subject(subject) //设置主题
                .issuer(issue) //发行者
                .id(issue)//jwtID
                .expiration(exp)//设置过期日期
                .claim("user", claim)//主题，可以包含用户信息
                .signWith(getSignedKey(key))//加密算法
                .compressWith(Jwts.ZIP.DEF).compact();//对载荷进行压缩

        return result;
    }


    // 解析jwt
    public static Jws<Claims> parseJWT(String jwt) {
        return parseJWT(jwt, getSignedKey());
    }

    public static Jws<Claims> parseJWT(String jwt, SecretKey key) {
        return Jwts.parser().verifyWith(key)
                .build().parseSignedClaims(jwt);
    }

    public static Jws<Claims> parseJWT(String jwt, String key) {
        return parseJWT(jwt, getSignedKey(key));
    }


    //获取主题信息
    public static Claims getClaims(String jwt) {
        return Jwts.parser().verifyWith(getSignedKey())
                .build().parseSignedClaims(jwt).getPayload();
    }


    public static Claims getClaims(String jwt, SecretKey key) {
        return Jwts.parser().verifyWith(key)
                .build().parseSignedClaims(jwt).getPayload();
    }

    public static Claims getClaims(String jwt, String key) {
        return getClaims(jwt, getSignedKey(key));
    }


    /**
     * 获取密钥
     *
     * @return Key
     */
    private static SecretKey getSignedKey() {
        return getSignedKey(getAuthKey());
    }

    private static SecretKey getSignedKey(String key) {
        byte[] keyBytes = Base64.getDecoder().decode(key);
        return Keys.hmacShaKeyFor(keyBytes);
    }


    /**
     * 自定义秘钥
     * 生成方式：SecretKey key = Keys.secretKeyFor(Jwts.SIG.HS256);
     * String encoded = Base64.getEncoder().encodeToString(key.getEncoded());
     *
     * @return Base64编码的32字节安全密钥
     */
    public static String getAuthKey() {
        return "dGhpcy1pcy1hLXNlY3JldC1rZXktZm9yLWhzMjU2LWFsZ29yaXRobQ==";
    }

}