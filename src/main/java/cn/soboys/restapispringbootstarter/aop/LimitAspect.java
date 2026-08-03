package cn.soboys.restapispringbootstarter.aop;

import cn.soboys.restapispringbootstarter.annotation.Limit;
import cn.soboys.restapispringbootstarter.auth.UserContextSupport;
import cn.soboys.restapispringbootstarter.config.RestApiProperties;
import cn.soboys.restapispringbootstarter.enums.LimitType;
import cn.soboys.restapispringbootstarter.exception.LimitAccessException;
import cn.soboys.restapispringbootstarter.utils.HttpUserAgent;
import cn.soboys.restapispringbootstarter.utils.Strings;
import com.google.common.collect.ImmutableList;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;

/**
 * @author E_Ivan
 * @since 2025/2/16 17:02
 */
@Aspect
@Component
@Slf4j
public class LimitAspect extends BaseAspectSupport {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private RestApiProperties.RedisProperties redisProperties;

    public LimitAspect() {
    }

    @Pointcut("@annotation(cn.soboys.restapispringbootstarter.annotation.Limit)")
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        Method method = resolveMethod(point);
        Limit limitAnnotation = method.getAnnotation(Limit.class);
        LimitType limitType = limitAnnotation.limitType();
        String name = limitAnnotation.name();
        String key;
        String ip = HttpUserAgent.getIpAddr();
        int limitPeriod = limitAnnotation.period();
        int limitCount = limitAnnotation.count();
        String userId = null;
        key = switch (limitType) {
            case IP -> ip;
            case CUSTOMER -> limitAnnotation.key();
            case USER -> {
                userId = UserContextSupport.getInstance().userId(false);
                yield Optional.ofNullable(userId).map(id -> "USER_LIMIT:" + id).orElse(ip);
            }
        };
        if (redisProperties != null && StringUtils.isNotBlank(redisProperties.getKeyPrefix())) {
            key = redisProperties.getKeyPrefix() + Strings.COLON + key;
        }
        ImmutableList<String> keys = ImmutableList.of(StringUtils.join(limitAnnotation.prefix() + Strings.UNDER_LINE, key));
        String luaScript = buildLuaScript();
        RedisScript<Long> redisScript = new DefaultRedisScript<>(luaScript, Long.class);
        Long count = redisTemplate.execute(redisScript, keys, limitCount, limitPeriod);
        if (Objects.nonNull(count) && count.intValue() <= limitCount) {
            log.info("IP:{} user:{} 第 {} 次访问key为 {}，描述为 [{}] 的接口", ip, userId, count, keys, name);
            return point.proceed();
        } else {
            log.error("key为 {}，描述为 [{}] 的接口访问超出频率限制", keys, name);
            throw new LimitAccessException("访问频率过快请稍后再试");
        }
    }

    /**
     * 限流脚本
     * 调用的时候不超过阈值，则直接返回并执行计算器自加。
     *
     * @return lua脚本
     */
    private String buildLuaScript() {
        return "local c" +
                "\nc = redis.call('get',KEYS[1])" +
                "\nif c and tonumber(c) > tonumber(ARGV[1]) then" +
                "\nreturn c;" +
                "\nend" +
                "\nc = redis.call('incr',KEYS[1])" +
                "\nif tonumber(c) == 1 then" +
                "\nredis.call('expire',KEYS[1],ARGV[2])" +
                "\nend" +
                "\nreturn c;";
    }
}
