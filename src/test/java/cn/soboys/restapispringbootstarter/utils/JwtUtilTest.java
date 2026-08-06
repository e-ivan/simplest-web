package cn.soboys.restapispringbootstarter.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.MacAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private String subject;
    private String issue;
    private Map<String, Object> userClaim;
    private long ttlMillis;

    @BeforeEach
    void setUp() {
        subject = "user123";
        issue = "test-issuer";
        ttlMillis = 3600;

        userClaim = new HashMap<>();
        userClaim.put("userId", 1001L);
        userClaim.put("username", "zhangsan");
        userClaim.put("role", "ADMIN");
    }

    @Test
    @DisplayName("测试默认密钥和算法创建JWT")
    void testCreateJWT_defaultKeyAndAlgorithm() {
        String jwt = JwtUtil.createJWT(subject, issue, userClaim, ttlMillis);

        assertNotNull(jwt);
        assertFalse(jwt.isEmpty());
        assertTrue(jwt.split("\\.").length == 3);
        System.out.println("生成的JWT: " + jwt);
    }

    @Test
    @DisplayName("测试自定义密钥和算法创建JWT")
    void testCreateJWT_customKeyAndAlgorithm() {
        String customKey = "Y3VzdG9tLXNlY3JldC1rZXktZm9yLXRlc3RzLWhzMjU2LTI1NmJpdHM=";
        MacAlgorithm algorithm = Jwts.SIG.HS256;

        String jwt = JwtUtil.createJWT(subject, issue, userClaim, ttlMillis, algorithm, customKey);

        assertNotNull(jwt);
        assertFalse(jwt.isEmpty());
        assertTrue(jwt.split("\\.").length == 3);
        System.out.println("自定义密钥生成的JWT: " + jwt);
    }

    @Test
    @DisplayName("测试默认密钥解析JWT")
    void testParseJWT_defaultKey() {
        String jwt = JwtUtil.createJWT(subject, issue, userClaim, ttlMillis);

        Jws<Claims> claimsJws = JwtUtil.parseJWT(jwt);

        assertNotNull(claimsJws);
        Claims payload = claimsJws.getPayload();
        assertEquals(subject, payload.getSubject());
        assertEquals(issue, payload.getIssuer());
        assertEquals(issue, payload.getId());

        Object user = payload.get("user");
        assertNotNull(user);
        assertTrue(user instanceof Map);

        @SuppressWarnings("unchecked")
        Map<String, Object> userMap = (Map<String, Object>) user;
        assertEquals(1001L, ((Number) userMap.get("userId")).longValue());
        assertEquals("zhangsan", userMap.get("username"));
        assertEquals("ADMIN", userMap.get("role"));

        System.out.println("解析得到的Subject: " + payload.getSubject());
        System.out.println("解析得到的Issuer: " + payload.getIssuer());
        System.out.println("解析得到的User: " + user);
    }

    @Test
    @DisplayName("测试自定义密钥解析JWT")
    void testParseJWT_customKey() {
        String customKey = "Y3VzdG9tLXNlY3JldC1rZXktZm9yLXRlc3RzLWhzMjU2LTI1NmJpdHM=";
        MacAlgorithm algorithm = Jwts.SIG.HS256;

        String jwt = JwtUtil.createJWT(subject, issue, userClaim, ttlMillis, algorithm, customKey);

        SecretKey secretKey = getSignedKey(customKey);
        Jws<Claims> claimsJws = JwtUtil.parseJWT(jwt, secretKey);

        assertNotNull(claimsJws);
        Claims payload = claimsJws.getPayload();
        assertEquals(subject, payload.getSubject());
        assertEquals(issue, payload.getIssuer());
    }

    @Test
    @DisplayName("测试默认密钥获取Claims")
    void testGetClaims_defaultKey() {
        String jwt = JwtUtil.createJWT(subject, issue, userClaim, ttlMillis);

        Claims claims = JwtUtil.getClaims(jwt);

        assertNotNull(claims);
        assertEquals(subject, claims.getSubject());
        assertEquals(issue, claims.getIssuer());
        assertEquals(issue, claims.getId());
        assertNotNull(claims.getExpiration());
        assertNotNull(claims.get("user"));
    }

    @Test
    @DisplayName("测试自定义密钥获取Claims")
    void testGetClaims_customKey() {
        String customKey = "Y3VzdG9tLXNlY3JldC1rZXktZm9yLXRlc3RzLWhzMjU2LTI1NmJpdHM=";
        MacAlgorithm algorithm = Jwts.SIG.HS256;

        String jwt = JwtUtil.createJWT(subject, issue, userClaim, ttlMillis, algorithm, customKey);

        SecretKey secretKey = getSignedKey(customKey);
        Claims claims = JwtUtil.getClaims(jwt, secretKey);

        assertNotNull(claims);
        assertEquals(subject, claims.getSubject());
        assertEquals(issue, claims.getIssuer());
    }

    @Test
    @DisplayName("测试自定义密钥完整流程：创建+解析")
    public void testToken() throws Exception {
        String customKey = "d3Jvbmctc2VjcmV0LWtleS1mb3ItdGVzdGluZy13cm9uZy0yNTZiaXQ=";
        MacAlgorithm algorithm = Jwts.SIG.HS256;

        String jwt = "eyJ6aXAiOiJERUYiLCJhbGciOiJIUzI1NiJ9.eJyrViouTVKyUnq2tfvF-qnP-9Y_XdSspKOUWVwMFDQEsrJKMqGs1IoCJStDcwszI0MTMxNLHaXS4tQiJatqMO2ZApSDCOUl5qYCtaTGZ5Yl5gH1JabkZuYpWZUUlabqKOUXpOaB1ColmSWaJaZamKYYmRlbWphaoPOVamsBVigx2g.8kp49f8DvPsTRjImlSX_JWxdmmwQKISkrE7ZKNh5vIo";
        SecretKey secretKey = getSignedKey(customKey);
        Claims claims = JwtUtil.getClaims(jwt, secretKey);
        assertNotNull(claims);
        assertEquals(subject, claims.getSubject());
        assertEquals(issue, claims.getIssuer());
    }

    @Test
    @DisplayName("测试过期时间设置正确")
    void testExpirationTime() {
        long ttl = 60;
        long beforeCreate = System.currentTimeMillis();

        String jwt = JwtUtil.createJWT(subject, issue, userClaim, ttl);
        Claims claims = JwtUtil.getClaims(jwt);
        long afterCreate = System.currentTimeMillis();

        long expectedExpMin = beforeCreate + ttl * 1000;
        long expectedExpMax = afterCreate + ttl * 1000;
        long actualExp = claims.getExpiration().getTime();

        assertTrue(actualExp >= expectedExpMin - 1000);
        assertTrue(actualExp <= expectedExpMax + 1000);

        System.out.println("过期时间: " + claims.getExpiration());
    }

    @Test
    @DisplayName("测试使用字符串作为Claim内容")
    void testStringClaim() {
        String stringClaim = "simple-user-data";
        String jwt = JwtUtil.createJWT(subject, issue, stringClaim, ttlMillis);

        Claims claims = JwtUtil.getClaims(jwt);
        assertEquals(stringClaim, claims.get("user"));
    }

    @Test
    @DisplayName("测试使用简单对象作为Claim内容")
    void testSimpleObjectClaim() {
        Long userId = 999L;
        String jwt = JwtUtil.createJWT(subject, issue, userId, ttlMillis);

        Claims claims = JwtUtil.getClaims(jwt);
        Object user = claims.get("user");
        assertEquals(userId, ((Number) user).longValue());
    }

    @Test
    @DisplayName("测试解析篡改的JWT会抛出异常")
    void testParseTamperedJWT_shouldThrowException() {
        String jwt = JwtUtil.createJWT(subject, issue, userClaim, ttlMillis);
        String tamperedJwt = jwt.substring(0, jwt.length() - 5) + "XXXXX";

        assertThrows(Exception.class, () -> JwtUtil.parseJWT(tamperedJwt));
    }

    @Test
    @DisplayName("测试错误密钥解析会抛出异常")
    void testParseWithWrongKey_shouldThrowException() {
        String jwt = JwtUtil.createJWT(subject, issue, userClaim, ttlMillis);

        String wrongKey = "d3Jvbmctc2VjcmV0LWtleS1mb3ItdGVzdGluZy13cm9uZy0yNTZiaXQ=";
        SecretKey wrongSecretKey = getSignedKey(wrongKey);

        assertThrows(Exception.class, () -> JwtUtil.parseJWT(jwt, wrongSecretKey));
    }

    private SecretKey getSignedKey(String key) {
        byte[] keyBytes = Base64.getDecoder().decode(key);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}