package cn.soboys.restapispringbootstarter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限
 *
 * @author E_Iva
 * @date 2024/2/22 16:07
 */
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Authorities {
    Authority[] value();
}
