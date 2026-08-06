package cn.soboys.restapispringbootstarter.cache;

import cn.hutool.v7.core.reflect.FieldUtil;
import cn.soboys.restapispringbootstarter.utils.StrUtil;
import cn.soboys.restapispringbootstarter.utils.Strings;
import org.springframework.boot.convert.DurationStyle;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;

import java.time.Duration;

/**
 * @author ex_lianghf8
 * @since 2024/10/28 14:25
 */
public class TtlRedisCacheManager extends RedisCacheManager {
    public static final String TTL_DELAY = Strings.HASH;
    public static final String TTL_EQ = Strings.EQUALS;

    public TtlRedisCacheManager(RedisCacheWriter cacheWriter, RedisCacheConfiguration defaultCacheConfiguration) {
        super(cacheWriter, defaultCacheConfiguration);
    }

    /**
     * @param name        解析name两个规则，如果是#，则过期时间以当前时间以后到指定时长。如果是=，则过期时间在指定时间，且格式为day(@time),time可选，默认为0点.如一天，时间是12点，1@12:00
     * @param cacheConfig can be {@literal null}.
     * @return
     */
    @Override
    protected RedisCache createRedisCache(String name, RedisCacheConfiguration cacheConfig) {
        if (StrUtil.containsAny(name, TTL_DELAY, TTL_EQ)) {
            String eq = StrUtil.subAfter(name, TTL_EQ, true);
            if (StrUtil.isNotBlank(eq)) {
                return new DynamicTtlRedisCache(StrUtil.subBefore(name, TTL_EQ, true), (RedisCacheWriter) FieldUtil.getFieldValue(this, "cacheWriter"), cacheConfig, eq);
            }
            String delay = StrUtil.subAfter(name, TTL_DELAY, true);
            if (StrUtil.isNotBlank(delay)) {
                final Duration duration = DurationStyle.detectAndParse(delay);
                return super.createRedisCache(StrUtil.subBefore(name, TTL_DELAY, true), cacheConfig.entryTtl(duration));
            }
        }
        return super.createRedisCache(name, cacheConfig);
    }
}
