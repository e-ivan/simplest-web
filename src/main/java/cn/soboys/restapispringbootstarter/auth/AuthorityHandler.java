package cn.soboys.restapispringbootstarter.auth;

/**
 * 权限处理者
 *
 * @author E_Ivan
 * @date 2024/3/18 11:06
 */
public interface AuthorityHandler {
    /**
     * 是否匹配
     *
     * @param param 入参
     * @return 是否命中
     */
    boolean match(Object param);

    /**
     * 前置处理，允许修改参数
     *
     * @param permission 权限信息
     * @param userId     当前用户
     * @param param      命中的参数
     */
    void preHandler(String permission, String userId, Object param);

    /**
     * 结果处理
     *
     * @param permission 权限信息
     * @param userId     当前用户
     * @param result     结果
     * @param param      命中的参数
     * @param resultPath 需要处理的结果路径
     * @return 修改后的值
     */
    Object postHandler(String permission, String userId, Object result, Object param, String[] resultPath);
}
