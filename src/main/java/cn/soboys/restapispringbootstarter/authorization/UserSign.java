package cn.soboys.restapispringbootstarter.authorization;


import io.jsonwebtoken.security.MacAlgorithm;

/**
 * @author 公众号 程序员三时
 * @version 1.0
 * @date 2023/7/13 21:38
 * @webSite https://github.com/coder-amiao
 */
public interface UserSign {

    /**
     * 自定义签名
     *
     * @return
     */
    MacAlgorithm sign();

    /**
     * 自定义秘钥
     *
     * @return
     */
    String AuthKey();

}
