package cn.soboys.restapispringbootstarter.authorization;

import cn.soboys.restapispringbootstarter.HttpStatus;
import cn.soboys.restapispringbootstarter.annotation.Authority;
import cn.soboys.restapispringbootstarter.exception.BusinessException;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.dromara.hutool.core.text.StrUtil;
import org.springframework.web.method.HandlerMethod;

import java.util.Objects;
import java.util.Optional;

/**
 * @author 公众号 程序员三时
 * @version 1.0
 * @date 2023/7/16 12:12
 * @webSite https://github.com/coder-amiao
 */
public class LoginAuthorizationSubject implements LoginAuthorization {
    private static final String BEARER = "Bearer ";

    @Resource
    protected UserJwtToken userJwtToken;

    protected String getToken(HttpServletRequest request) {
        String token = request.getHeader(userJwtToken.getJwtProperties().getTokenHeader());
        if (StrUtil.startWith(token, BEARER)) {
            return StrUtil.removePrefix(token, BEARER);
        }
        return null;
    }

    @Override
    public Boolean authorization(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = getToken(request);
        Authority authority = findAuthority(handler);
        //定义不需要登录，则跳过没有登录的情况
        if (StrUtil.isNotEmpty(token)) {
            //验证token有效合法性。
            Claims claims = userJwtToken.getClaims(token);
            dispose(claims);
        } else if (Objects.isNull(authority) || authority.requireLogin()) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED);
        }
        return true;
    }

    /**
     * 处理jwt信息
     *
     * @param claims A JWT Claims set
     */
    protected void dispose(Claims claims) {
        //其他数据库 或者业务操作
        Object user = claims.get("user");
        // 业务自行处理
    }

    /**
     * 获取权限注解
     */
    private static Authority findAuthority(Object handler) {
        if (handler instanceof HandlerMethod h) {
            //先取方法上的，方法上没有再取类上的
            return Optional.ofNullable(h.getMethodAnnotation(Authority.class))
                    .orElseGet(() -> h.getBeanType().getAnnotation(Authority.class));
        }
        return null;
    }
}
