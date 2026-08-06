package cn.soboys.restapispringbootstarter.aop;

import cn.hutool.v7.core.array.ArrayUtil;
import cn.hutool.v7.core.reflect.method.MethodUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.StandardReflectionParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.annotation.Annotation;
import java.lang.annotation.Repeatable;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * @author 公众号 程序员三时
 * @version 1.0
 * @date 2023/7/2 11:28
 * @webSite https://github.com/coder-amiao
 */
@Slf4j
public class BaseAspectSupport {

    public Method resolveMethod(JoinPoint point) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Class<?> targetClass = point.getTarget().getClass();

        Method method = getDeclaredMethod(targetClass, signature.getName(),
                signature.getMethod().getParameterTypes());
        if (method == null) {
            throw new IllegalStateException("无法解析目标方法: " + signature.getMethod().getName());
        }
        return method;
    }


    public static <A extends Annotation> List<A> findRepeatableAnnotations(AnnotatedElement element, Class<A> annotationClass) {
        List<A> annList = new ArrayList<>();
        Repeatable repeatable = AnnotatedElementUtils.findMergedAnnotation(annotationClass, Repeatable.class);
        if (Objects.nonNull(repeatable)) {
            Object aa = AnnotatedElementUtils.findMergedAnnotation(element, repeatable.value());
            if (Objects.nonNull(aa)) {
                Object value = MethodUtil.invoke(aa, "value");
                annList.addAll(Arrays.asList((A[]) value));
            }
        }
        A ann = AnnotatedElementUtils.findMergedAnnotation(element, annotationClass);
        if (Objects.nonNull(ann)) {
            annList.add(ann);
        }
        return annList;
    }


    protected Object[] queryParam(String[] expressions, JoinPoint pjp) {
        List<Object> params = new ArrayList<>();
        Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        Object[] args = pjp.getArgs();
        if (ArrayUtil.isEmpty(expressions)) {
            if (ArrayUtil.isNotEmpty(args)) {
                //默认取第一个值
                params.add(args[0]);
            }
        } else {
            for (String expression : expressions) {
                try {
                    params.add(parseExpression(expression, method, args));
                } catch (Exception e) {
                    log.warn("获取不到表达式：{}的值", expression);
                }
            }
        }
        return params.toArray();
    }

    /**
     * 根据方法和传入的参数获取请求参数
     */
    public Object getParameter(Method method, Object[] args) {
        List<Object> argList = new ArrayList<>();
        Parameter[] parameters = method.getParameters();
        Map<String, Object> map = new HashMap<>();
        for (int i = 0; i < parameters.length; i++) {
            //将RequestBody注解修饰的参数作为请求参数
            RequestBody requestBody = parameters[i].getAnnotation(RequestBody.class);
            //将RequestParam注解修饰的参数作为请求参数
            RequestParam requestParam = parameters[i].getAnnotation(RequestParam.class);
            String key = parameters[i].getName();
            if (requestBody != null) {
                argList.add(args[i]);
            } else if (requestParam != null) {
                map.put(key, args[i]);
            } else {
                map.put(key, args[i]);
            }
        }
        if (!map.isEmpty()) {
            argList.add(map);
        }
        if (argList.isEmpty()) {
            return null;
        } else if (argList.size() == 1) {
            return argList.get(0);
        } else {
            return argList;
        }
    }

    public Method getDeclaredMethod(Class<?> clazz, String name, Class<?>... parameterTypes) {
        try {
            return clazz.getDeclaredMethod(name, parameterTypes);
        } catch (NoSuchMethodException e) {
            Class<?> superClass = clazz.getSuperclass();
            if (superClass != null) {
                return getDeclaredMethod(superClass, name, parameterTypes);
            }
        }
        return null;
    }


    /**
     * 使用SPEL进行key的解析
     *
     * @param expressionString 表达式字符串
     * @param method           方法对象，用于获取参数名
     * @param args             方法的参数值
     * @return
     */
    public String parseExpression(String expressionString, Method method, Object[] args) {
        if (!StringUtils.hasText(expressionString)) {
            return expressionString;
        }
        StandardReflectionParameterNameDiscoverer discoverer = new StandardReflectionParameterNameDiscoverer();
        String[] paramNameArr = discoverer.getParameterNames(method);
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();
        if (paramNameArr != null && args != null) {
            for (int i = 0; i < paramNameArr.length && i < args.length; i++) {
                context.setVariable(paramNameArr[i], args[i]);
            }
        }
        return parser.parseExpression(expressionString).getValue(context, String.class);
    }
}