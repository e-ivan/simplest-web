package cn.soboys.restapispringbootstarter.config;


import cn.soboys.restapispringbootstarter.ApplicationRunner;
import cn.soboys.restapispringbootstarter.ExceptionHandler;
import cn.soboys.restapispringbootstarter.ResultHandler;
import cn.soboys.restapispringbootstarter.aop.AuthorityAspect;
import cn.soboys.restapispringbootstarter.aop.EmptyParamReturnAspect;
import cn.soboys.restapispringbootstarter.aop.LimitAspect;
import cn.soboys.restapispringbootstarter.aop.LogAspect;
import cn.soboys.restapispringbootstarter.i18n.I18NMessage;
import cn.soboys.restapispringbootstarter.utils.RestFulTemp;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.hibernate.validator.HibernateValidator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;


/**
 * @author 公众号 程序员三时
 * @version 1.0
 * @date 2023/6/27 11:36
 * @webSite https://github.com/coder-amiao
 */
//@Configuration
//@ConditionalOnProperty(name = "rest-api.enabled", havingValue = "true")
public class BeanAutoConfiguration {


    @Bean
    @ConditionalOnMissingBean
    public I18NMessage i18NMessage() {
        return new I18NMessage();
    }

    @Bean
    @ConditionalOnMissingBean
    public ResultHandler resultHandler() {
        return new ResultHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public ExceptionHandler exceptionHandler() {
        return new ExceptionHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public StartupApplicationListener startupApplicationListener() {
        return new StartupApplicationListener();
    }


    @Bean
    @ConditionalOnMissingBean
    public RestApiProperties restApiProperties() {
        return new RestApiProperties();
    }

    @Bean
    @ConditionalOnMissingBean
    public RestApiProperties.LoggingProperties loggingProperties(RestApiProperties restApiProperties) {
        return new RestApiProperties.LoggingProperties();
    }

    @Bean
    @ConditionalOnMissingBean
    public RestApiProperties.Ip2regionProperties ip2regionProperties(RestApiProperties restApiProperties) {
        return new RestApiProperties.Ip2regionProperties();
    }

    @Bean
    @ConditionalOnMissingBean
    public RestApiProperties.JwtProperties jwtProperties(RestApiProperties restApiProperties) {
        return new RestApiProperties.JwtProperties();
    }

    @Bean
    @ConditionalOnMissingBean
    public RestApiProperties.InvokeTimeProperties invokeTimeProperties(RestApiProperties restApiProperties) {
        return new RestApiProperties.InvokeTimeProperties();
    }

    @Bean
    @ConditionalOnMissingBean
    public ApplicationRunner applicationRunner() {
        return new ApplicationRunner();
    }

    @Bean
    @ConditionalOnMissingBean
    public LogAspect logAspect() {
        return new LogAspect();
    }

    @Bean
    @ConditionalOnMissingBean
    public LimitAspect limitAspect() {
        return new LimitAspect();
    }

    @Bean
    @ConditionalOnMissingBean
    public AuthorityAspect authorityAspect() {
        return new AuthorityAspect();
    }

    @Bean
    @ConditionalOnMissingBean
    public EmptyParamReturnAspect emptyParamReturnAspect() {
        return new EmptyParamReturnAspect();
    }

    /**
     * 参数校验快速失败返回 提升性能
     */
    @Bean
    @ConditionalOnMissingBean
    public Validator validator() {
        ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class)
                .configure()
                // 快速失败模式
                .failFast(true)
                .buildValidatorFactory();
        return validatorFactory.getValidator();
    }

    public class RestTemplateConfig {

        /**
         * 第三方请求要求的默认编码
         */
        private final Charset thirdRequest = StandardCharsets.UTF_8;

        @Bean
        public RestTemplate restTemplate(ClientHttpRequestFactory factory) {
            RestTemplate restTemplate = new RestTemplate(factory);
            // 处理请求中文乱码问题
            List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters();
            for (HttpMessageConverter<?> messageConverter : messageConverters) {
                if (messageConverter instanceof StringHttpMessageConverter) {
                    ((StringHttpMessageConverter) messageConverter).setDefaultCharset(thirdRequest);
                }
                if (messageConverter instanceof JacksonJsonHttpMessageConverter) {
                    ((JacksonJsonHttpMessageConverter) messageConverter).setDefaultCharset(thirdRequest);
                }
                if (messageConverter instanceof AllEncompassingFormHttpMessageConverter) {
                    ((AllEncompassingFormHttpMessageConverter) messageConverter).setCharset(thirdRequest);
                }
            }
            return restTemplate;
        }

        @Bean
        public ClientHttpRequestFactory simpleClientHttpRequestFactory() {
            SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
            factory.setConnectTimeout(15000);
            factory.setReadTimeout(5000);
            return factory;
        }


        @Bean
        public RestFulTemp restFulTemp() {
            return new RestFulTemp();
        }
    }
}