package cn.soboys.restapispringbootstarter.authorization;


/**
 * @author 公众号 程序员三时
 * @version 1.0
 * @date 2023/7/13 21:38
 * @webSite https://github.com/coder-amiao
 */
public interface UserSign {

    /**
     * 自定义秘钥，使用Base64.encode(Jwts.SIG.HS256.key().build().getEncoded())生成
     *
     * @return
     */
    String AuthKey();

}
