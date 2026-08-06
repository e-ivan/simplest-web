package cn.soboys.restapispringbootstarter.aop;

import cn.hutool.v7.core.array.ArrayUtil;
import cn.hutool.v7.core.reflect.TypeUtil;
import cn.soboys.restapispringbootstarter.annotation.EmptyParamReturn;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 空值参数返回值切面
 *
 * @author E_Ivan
 * @date 2021/7/21 10:59
 */
@Slf4j
@Aspect
@Component
@Order(2)
public class EmptyParamReturnAspect extends BaseAspectSupport {
    private static final Object NULL = new Object();

    @Pointcut("@annotation(cn.soboys.restapispringbootstarter.annotation.EmptyParamReturn)")
    public void emptyPointcut() {
    }

    @Around("emptyPointcut()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        //得到其方法签名
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        Method method = methodSignature.getMethod();
        EmptyParamReturn ann = AnnotatedElementUtils.findMergedAnnotation(method, EmptyParamReturn.class);
        if (ann != null) {
            Class<?> returnClass = TypeUtil.getReturnClass(method);
            Object[] objects = queryParam(ann.param(), pjp);
            if (ArrayUtil.isNotEmpty(objects)) {
                Object emptyValue = buildEmptyValue(objects, ann, returnClass);
                if (emptyValue != null) {
                    return NULL.equals(emptyValue) ? null : emptyValue;
                }
            }
        }
        return pjp.proceed();
    }

    private static Object buildEmptyValue(Object[] values, EmptyParamReturn annotation, Class<?> returnClass) {
        if (returnClass.isAssignableFrom(Void.class)) {
            return null;
        }
        if (ArrayUtil.isNotEmpty(values) || annotation.includeNull()) {
            if (annotation.fullMatch() != ObjectUtils.isNull(values)) {
                //参数为空，判断返回值是不是数组或集合，map，如果是就返回空
                if (returnClass.isAssignableFrom(List.class)) {
                    return Collections.emptyList();
                } else if (returnClass.isAssignableFrom(Set.class)) {
                    return Collections.emptySet();
                } else if (returnClass.isAssignableFrom(Map.class)) {
                    return Collections.emptyMap();
                } else if (returnClass.isAssignableFrom(IPage.class)) {
                    return new Page<>();
                } else if (returnClass.isArray()) {
                    return Array.newInstance(returnClass.getComponentType(), 0);
                } else {
                    return NULL;
                }
            }
        }
        return null;
    }
}
