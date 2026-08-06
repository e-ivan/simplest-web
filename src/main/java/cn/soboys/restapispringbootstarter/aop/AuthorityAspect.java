package cn.soboys.restapispringbootstarter.aop;

import cn.hutool.v7.core.collection.CollUtil;
import cn.hutool.v7.extra.spring.SpringUtil;
import cn.soboys.restapispringbootstarter.annotation.Authority;
import cn.soboys.restapispringbootstarter.auth.AuthorityHandler;
import cn.soboys.restapispringbootstarter.auth.UserContextSupport;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.*;

/**
 * 权限切面
 *
 * @author E_Ivan
 * @date 2021/7/21 10:59
 */
@Slf4j
@Aspect
@Component
@Order(2)
public class AuthorityAspect extends BaseAspectSupport {

    @Pointcut("@annotation(cn.soboys.restapispringbootstarter.annotation.Authority)||@annotation(cn.soboys.restapispringbootstarter.annotation.Authorities)")
    public void authorityPointcut() {
    }

    @Around("authorityPointcut()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        //得到其方法签名
        Method method = resolveMethod(pjp);
        List<Authority> anns = Optional.of(findRepeatableAnnotations(method, Authority.class))
                .filter(CollUtil::isNotEmpty)
                .orElseGet(() -> findRepeatableAnnotations(method.getDeclaringClass(), Authority.class));
        if (CollUtil.isNotEmpty(anns)) {
            for (Authority ann : anns) {
                AuthorityHandler handler = getHandler(ann.handler());
                if (Objects.nonNull(handler)) {
                    Object parameter = getParameter(method, pjp.getArgs());
                    String userId = UserContextSupport.getInstance().userId(false);
                    if (parameter instanceof Map) {
                        Collection<?> values = ((Map<?, ?>) parameter).values();
                        for (Object param : values) {
                            if (handler.match(param)) {
                                handler.preHandler(ann.permission(), userId, param);
                                return handler.postHandler(ann.permission(), userId, pjp.proceed(), param, ann.resultPath());
                            }
                        }
                    } else {
                        if (handler.match(parameter)) {
                            handler.preHandler(ann.permission(), userId, parameter);
                            return handler.postHandler(ann.permission(), userId, pjp.proceed(), parameter, ann.resultPath());
                        }
                    }
                }
            }
        }
        return pjp.proceed();
    }

    private static AuthorityHandler getHandler(Class<? extends AuthorityHandler> clz) {
        if (!clz.equals(AuthorityHandler.class)) {
            try {
                return SpringUtil.getBean(clz);
            } catch (Exception e) {
                log.warn("获取权限处理器异常:{}", clz, e);
            }
        }
        return null;
    }
}
