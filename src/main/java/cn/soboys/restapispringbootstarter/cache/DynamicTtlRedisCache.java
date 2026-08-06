package cn.soboys.restapispringbootstarter.cache;

import cn.soboys.restapispringbootstarter.utils.NumberUtil;
import cn.soboys.restapispringbootstarter.utils.SplitUtil;
import cn.soboys.restapispringbootstarter.utils.StrUtil;
import cn.soboys.restapispringbootstarter.utils.Strings;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.lang.Nullable;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

/**
 * 动态过期redis缓存，主要用于统一过期时间的处理
 *
 * @author ex_lianghf8
 * @since 2024/10/28 14:48
 */
public class DynamicTtlRedisCache extends RedisCache {

    /**
     * 13@12:00:00
     */
    protected Integer expireDay;

    protected LocalTime expireTime = LocalTime.of(0, 0);

    /**
     * Create new {@link RedisCache}.
     *
     * @param name        must not be {@literal null}.
     * @param cacheWriter must not be {@literal null}.
     * @param cacheConfig must not be {@literal null}.
     */
    protected DynamicTtlRedisCache(String name, RedisCacheWriter cacheWriter, RedisCacheConfiguration cacheConfig, String expireDayStr) {
        super(name, cacheWriter, cacheConfig);
        List<String> split = SplitUtil.split(expireDayStr, Strings.AT);
        String time = null;
        if (split.size() > 1) {
            time = split.get(1);
        }
        this.expireDay = NumberUtil.parseIntDefaultNull(split.get(0));
        if (StrUtil.isNotBlank(time)) {
            this.expireTime = LocalTime.parse(time);
        }
    }

    protected Duration getExpireDuration() {
        LocalDateTime expireDateTime = expireTime.atDate(LocalDate.now()).plusDays(expireDay - 1);
        LocalDateTime now = LocalDateTime.now();
        if (expireDateTime.isBefore(now)) {
            expireDateTime = expireDateTime.plusDays(1);
        }
        return Duration.between(now, expireDateTime);
    }

    @Override
    public void put(Object key, @Nullable Object value) {
        if (Objects.nonNull(expireDay) && expireDay > 0) {
            // 计算当前时间距离过期日的时间
            Duration duration = getExpireDuration();
            Object cacheValue = preProcessCacheValue(value);
            String name = getName();
            if (!isAllowNullValues() && cacheValue == null) {
                throw new IllegalArgumentException(String.format(
                        "Cache '%s' does not allow 'null' values. Avoid storing null via '@Cacheable(unless=\"#result == null\")' or configure RedisCache to allow 'null' via RedisCacheConfiguration.",
                        name));
            }
            getNativeCache().put(name, createAndConvertCacheKey(key), serializeCacheValue(cacheValue), duration);
        } else {
            super.put(key, value);
        }
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        if (Objects.nonNull(expireDay)) {
            Object cacheValue = preProcessCacheValue(value);

            if (!isAllowNullValues() && cacheValue == null) {
                return get(key);
            }

            byte[] result = getNativeCache().putIfAbsent(getName(), createAndConvertCacheKey(key), serializeCacheValue(cacheValue),
                    getExpireDuration());

            if (result == null) {
                return null;
            }

            return new SimpleValueWrapper(fromStoreValue(deserializeCacheValue(result)));
        }
        return super.putIfAbsent(key, value);
    }


    private byte[] createAndConvertCacheKey(Object key) {
        return serializeCacheKey(createCacheKey(key));
    }
}
