package cn.soboys.restapispringbootstarter.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 公众号 程序员三时
 * @version 1.0
 * @date 2023/6/27 22:55
 * @webSite https://github.com/coder-amiao
 */
@Configuration
@ConfigurationProperties(prefix = "rest-api")
@Data
public class RestApiProperties {


    private boolean enabled = Boolean.FALSE;


    private String success = "success";

    private String code = "code";

    private String codeSuccessValue = "OK";

    private String msg = "msg";

    private String timestamp = "timestamp";

    private String data = "data";

    /**
     * 当前页
     */
    private String previousPage = "previousPage";
    /**
     * 下一页
     */
    private String nextPage = "nextPage";

    /**
     * 总页数
     */
    private String pageSize = "pageSize";

    private String totalPageSize = "totalPageSize";

    /**
     * 是否有下一页
     */
    private String hasNext = "hasNext";

    /**
     * 是否包装分页结果到data
     */
    private Boolean pageWrap = Boolean.TRUE;

    private String pageData = "pageData";

    /**
     * 排除不需要统一返回的restFull
     */
    private String[] excludePackages;
    /**
     * 添加需要统一返回的restFull
     */
    private String[] includePackages;

    @NestedConfigurationProperty
    private Ip2regionProperties ip2region = new Ip2regionProperties();

    @NestedConfigurationProperty
    private JwtProperties jwt = new JwtProperties();

    @NestedConfigurationProperty
    private LoggingProperties logging = new LoggingProperties();

    @NestedConfigurationProperty
    private RedisProperties redis = new RedisProperties();

    @NestedConfigurationProperty
    private InvokeTimeProperties invokeTime = new InvokeTimeProperties();

    @NestedConfigurationProperty
    private JsonSerializeProperties json = new JsonSerializeProperties();


    @Configuration
    @ConfigurationProperties(prefix = "rest-api.ip2region")
    @Data
    public static class Ip2regionProperties {
        /**
         * 是否使用外部的IP数据文件.
         */
        private boolean external = false;
        /**
         * ip2region.db 文件路径，默认： classpath:ip2region/ip2region.db
         */
        private String location = "classpath:ip2region/ip2region_v4.xdb";

        private String locationV6 = "classpath:ip2region/ip2region_v6.xdb";

    }


    @Configuration
    @ConfigurationProperties(prefix = "rest-api.jwt")
    @Data
    public static class JwtProperties {

        /**
         * 过期时间秒1天后过期=86400  (单位秒)
         */
        private Long expiration = 86400L;

        /**
         * 记住我过期时间 7天后过期=604800（单位秒）
         */
        private Long rememberMeExpiration = 604800L;

        /**
         * 配置用户自定义签名
         */
        private Boolean userSign = Boolean.FALSE;
        /**
         * Header Key
         */
        private String tokenHeader = "Token";

        /**
         * # 密匙KEY
         */
        private String secret = "2af57b969bac152d";


        private Authorization authorization = new Authorization();
    }

    @Data
    public static class Authorization {

        private Boolean hasAuthorization = Boolean.FALSE;

        /**
         * 需要认证的url
         */
        private String includesUrl;

        /**
         * 不需要认证的url
         */
        private String excludesUrl;
    }


    @Configuration
    @ConfigurationProperties(prefix = "rest-api.logging")
    @Data
    public static class LoggingProperties {
        private String path;
        private String maxHistory;
        private String maxFileSize;
        private String maxTotalSizeCap;
        private String levelRoot;
        private String logDataSourceClass = "cn.soboys.restapispringbootstarter.log.LogFileDefaultDataSource";
    }

    @Configuration
    @ConfigurationProperties(prefix = "rest-api.redis")
    @Data
    public static class RedisProperties {
        /**
         * 全局注册key
         */
        private String keyPrefix;
        /**
         * redis 缓存的默认超时时间(s) 1天超时
         */
        private Long expireTime;
    }

    @Data
    public static class Contact {

        /**
         * 联系人
         **/
        private String name = "公众号程序员三时";

        /**
         * 联系人url
         **/
        private String url = "https://github.com/coder-amiao/rest-api-spring-boot-starter";

        /**
         * 联系人email
         **/
        private String email = "xymarcus@163.com";

    }

    @Configuration
    @ConfigurationProperties(prefix = "rest-api.invoke-time")
    @Data
    public static class InvokeTimeProperties {

        /**
         * 是否开启请求耗时统计拦截器
         */
        private Boolean enabled = Boolean.FALSE;

        /**
         * 需要拦截的路径
         */
        private List<String> includePath = List.of("/**");

        /**
         * 不需要拦截的路径（默认排除：错误页、监控端点、静态资源、接口文档）
         */
        private List<String> excludePath = List.of("/error", "/actuator/**", "/v3/api-docs");
    }

    @Configuration
    @ConfigurationProperties(prefix = "rest-api.json")
    @Data
    public static class JsonSerializeProperties {
        /**
         * 是否使用 Jackson3 序列化器，现阶段很多框架不支持，默认关闭
         */
        private Boolean useJackson3 = Boolean.FALSE;
        /**
         * 序列化类型
         */
        private List<String> serializableType = new ArrayList<>();
        /**
         * 时间类型序列化返回 默认时间戳格式
         * yyyy-MM-dd HH:mm:ss.SSS
         */
        private String dateForm = "timestamp";

        /**
         * 浮点数序列化 BigDecimal 保留完整精度 科学计数法
         * 四舍五入
         */
        private String numberForm = ",###.##";


        /**
         * 对空 返回处理
         */
        private NullAble nullAble = new NullAble();

    }

    /**
     * JSON 序列化对空返回处理
     * 空集合 返回[],Double 返回 0.00 Number 返回0 字符串返回""
     */
    @Data
    public static class NullAble {
        /**
         * 是否开启对空值处理
         */
        private Boolean hasNullAble = Boolean.FALSE;

        /**
         * 当 int和long 类型为空默认处理返回0
         * original 不处理| number 0|string ""|
         */
        private String NumberType = "number";

        /**
         * 当 集合类型 空处理默认返会 []
         * original 不处理 |array []
         */
        private String ArrayType = "array";

        /**
         * 浮点类型 空处理 默认返回 0.00
         * original 不处理 |double 0.00
         */
        private String DoubleType = "double";

        /**
         * 对象 空处理 包括null空字符
         * original 不处理 |string ""
         */
        private String ObjectType = "string";


    }

}