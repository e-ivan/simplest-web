package cn.soboys.restapispringbootstarter.annotation;


import cn.soboys.restapispringbootstarter.auth.AuthorityHandler;

import java.lang.annotation.*;

/**
 * 权限
 *
 * @author E_Iva
 * @date 2024/2/22 16:07
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Authorities.class)
public @interface Authority {
    /**
     * 是否必须登录
     */
    boolean requireLogin() default true;

    Class<? extends AuthorityHandler> handler() default AuthorityHandler.class;

    String permission() default "";

    String[] resultPath() default "";
}
