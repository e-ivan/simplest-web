package cn.soboys.restapispringbootstarter.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * <pre>
 * 空参数返回空结果
 * 这个避免真实调用，没判空的情况
 * 如果是字符串类型，空字符串也算为空
 * 如果返回值类型是void则都会执行原方法
 * </pre>
 *
 * @author E_Ivan
 * @date 2021/7/21 11:20
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface EmptyParamReturn {

    /**
     * 参数，使用SpEL表达式，如果获取失败，则继续执行原方法。不指定默认第一个参数
     */
    @AliasFor("param")
    String[] value() default {};

    /**
     * 参数，使用SpEL表达式，如果获取失败，则继续执行原方法。不指定默认第一个参数
     */
    @AliasFor("value")
    String[] param() default {};

    /**
     * 是否全匹配，默认只要匹配中一个就算为空。当指定多个字段时是或还是且关系
     */
    boolean fullMatch() default false;

    /**
     * 是否包含null的情况
     */
    boolean includeNull() default true;
}
