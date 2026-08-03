package cn.soboys.restapispringbootstarter.enums;

/**
 * @author E_Ivan
 * @since 2025/2/16 17:01
 */
public enum LimitType {
    /**
     * 传统类型
     */
    CUSTOMER,
    /**
     * 根据 IP地址限制
     */
    IP,
    /**
     * 根据用户名限制
     */
    USER,
    ;

}
