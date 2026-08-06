package cn.soboys.restapispringbootstarter.auth;

import cn.hutool.v7.extra.spring.SpringUtil;
import cn.soboys.restapispringbootstarter.HttpStatus;
import cn.soboys.restapispringbootstarter.exception.BusinessException;
import cn.soboys.restapispringbootstarter.utils.CollUtil;
import cn.soboys.restapispringbootstarter.utils.StrUtil;

import java.util.Map;
import java.util.Optional;

/**
 * @author E_Ivan
 * @since 2026/8/2 17:56
 */
public interface UserContextSupport {

    default String userId(boolean required) {
        String userId = getUserId();
        if (StrUtil.isBlank(userId) && required) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED);
        }
        return userId;
    }

    /**
     * 获取当前用户id，如果未登录，则抛异常
     *
     * @return 当前用户id
     */
    default String userId() {
        return userId(true);
    }

    String getUserId();

    UserContextSupport EMPTY = () -> null;

    static UserContextSupport getInstance() {
        return Optional.ofNullable(SpringUtil.getBeansOfType(UserContextSupport.class))
                .map(Map::values).map(CollUtil::getFirst).orElse(EMPTY);
    }
}
