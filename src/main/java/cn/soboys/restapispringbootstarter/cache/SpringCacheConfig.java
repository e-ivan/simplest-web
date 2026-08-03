package cn.soboys.restapispringbootstarter.cache;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * @author E_Ivan
 * @date 2024/10/28 21:46
 */
//@Configuration
@EnableCaching //开启缓存注解驱动，否则后面使用的缓存都是无效的
@ConditionalOnClass(name = "org.springframework.data.redis.core.RedisOperations")
public class SpringCacheConfig {

    @Resource
    private RedisConnectionFactory redisConnectionFactory;

    /**
     * 自定义key生成策略
     */
    @Bean("keyGeneratorStrategy")
    public KeyGenerator keyGeneratorStrategy() {
        return (target, method, params) -> method.getName() + "[" + Arrays.asList(params) + "]";
    }


    @Bean
    public SpringCacheUtil springCacheUtil() {
        return new SpringCacheUtil();
    }


    /**
     * 定义缓存redis json 序列化
     *
     * @return
     */
    @Primary
    @Bean
    public CacheManager cacheManager(ObjectMapper objectMapper) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                // .entryTtl(Duration.ofSeconds(600)) // 设置缓存有效期一小时
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(keySerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer(objectMapper)));

        TtlRedisCacheManager manager = new TtlRedisCacheManager(RedisCacheWriter.lockingRedisCacheWriter(redisConnectionFactory),
                // 默认缓存配置
                config);
        manager.setTransactionAware(true);
        return manager;
    }

    @Bean("localCacheManager")
    public CacheManager localCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        //Caffeine配置
        Caffeine<Object, Object> caffeine = Caffeine.newBuilder()
                //最后一次写入后经过固定时间过期
                .expireAfterWrite(60, TimeUnit.MINUTES)
                //maximumSize=[long]: 缓存的最大条数
                .maximumSize(1000);
        cacheManager.setCaffeine(caffeine);
        return cacheManager;
    }

    private RedisSerializer<String> keySerializer() {
        return new StringRedisSerializer();
    }

    private RedisSerializer<Object> valueSerializer(ObjectMapper objectMapper) {
        // 使用 Jackson 序列化库进行 JSON 序列化
        ObjectMapper om = objectMapper.copy();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);
        // 反序列化字段不存在不报错
        om.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                // null字段不序列化
                .setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL);
        return new GenericJackson2JsonRedisSerializer(om);
    }
}
