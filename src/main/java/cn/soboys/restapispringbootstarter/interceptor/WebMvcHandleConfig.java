package cn.soboys.restapispringbootstarter.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import cn.hutool.v7.core.math.NumberUtil;
import cn.hutool.v7.extra.spring.SpringUtil;
import cn.soboys.restapispringbootstarter.config.RestApiProperties;
import cn.soboys.restapispringbootstarter.utils.EnumUtil;
import jakarta.annotation.Resource;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverters;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author 公众号 程序员三时
 * @version 1.0
 * @date 2023/6/28 00:21
 * @webSite https://github.com/coder-amiao
 * 封装全局的 资源配置。路由配置 拦截处理
 */
@Slf4j
public class WebMvcHandleConfig implements WebMvcConfigurer {

    @Resource
    private RestApiProperties.InvokeTimeProperties invokeTimeProperties;
    @Resource
    private RestApiProperties.JsonSerializeProperties jsonSerializeProperties;

    @Autowired(required = false)
    private ObjectMapper objectMapper;

    @Override
    public void configureMessageConverters(@NotNull HttpMessageConverters.ServerBuilder builder) {
        if (jsonSerializeProperties.getUseJackson3() || Objects.isNull(objectMapper)) {
            log.info("开启 Jackson3 序列化器");
            return;
        }
        MappingJackson2HttpMessageConverter jackson2Converter = new MappingJackson2HttpMessageConverter();
        jackson2Converter.setObjectMapper(objectMapper);
        builder.withJsonConverter(jackson2Converter);
    }

    /**
     * 授权登录拦截器
     *
     * @return
     */
    @Bean
    public JwtTokenInterceptor jwtTokenInterceptor() {
        return new JwtTokenInterceptor();
    }

    /**
     * 请求耗时统计拦截器
     *
     * @return
     */
    @Bean
    public WebInvokeTimeInterceptor webInvokeTimeInterceptor() {
        return new WebInvokeTimeInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(SpringUtil.getBean(JwtTokenInterceptor.class));

        if (invokeTimeProperties != null && Boolean.TRUE.equals(invokeTimeProperties.getEnabled())) {
            log.info("开启请求耗时统计拦截器");
            registry.addInterceptor(SpringUtil.getBean(WebInvokeTimeInterceptor.class))
                    .addPathPatterns(invokeTimeProperties.getIncludePath())
                    .excludePathPatterns(invokeTimeProperties.getExcludePath());
        }

        WebMvcConfigurer.super.addInterceptors(registry);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
        WebMvcConfigurer.super.addResourceHandlers(registry);
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(new EnumConverterFactory());
        registry.addConverter(new StringToLocalDateTimeConverter());
        registry.addConverter(new StringToLocalDateConverter());
    }


    /**
     * String 转 LocalDateTime 全局转换器（支持多种格式自动降级）
     */
    static class StringToLocalDateTimeConverter implements Converter<String, LocalDateTime> {

        private static final List<DateTimeFormatter> FORMATTERS = Arrays.asList(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
        );

        @Override
        public LocalDateTime convert(String source) {
            if (source == null || source.trim().isEmpty()) {
                return null;
            }
            source = source.trim();

            // 尝试各种格式
            for (DateTimeFormatter formatter : FORMATTERS) {
                try {
                    return LocalDateTime.parse(source, formatter);
                } catch (DateTimeParseException e) {
                    log.debug("尝试解析日期格式失败: {} 使用格式: {}", source, formatter);
                }
            }

            // 降级处理：如果是纯日期格式，自动补充时间
            try {
                LocalDate date = LocalDate.parse(source, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                return date.atStartOfDay();
            } catch (DateTimeParseException e) {
                log.debug("尝试解析为日期格式失败: {}", source);
            }

            throw new IllegalArgumentException("无法解析日期时间: " + source + "，支持的格式包括: yyyy-MM-dd HH:mm:ss, yyyy-MM-dd'T'HH:mm:ss, yyyy-MM-dd");
        }
    }

    /**
     * String 转 LocalDate 全局转换器
     */
    static class StringToLocalDateConverter implements Converter<String, LocalDate> {

        private static final List<DateTimeFormatter> FORMATTERS = Arrays.asList(
                DateTimeFormatter.ofPattern("yyyy-MM-dd"),
                DateTimeFormatter.ofPattern("yyyy/MM/dd"),
                DateTimeFormatter.ofPattern("yyyyMMdd")
        );

        @Override
        public LocalDate convert(String source) {
            if (source == null || source.trim().isEmpty()) {
                return null;
            }
            source = source.trim();

            for (DateTimeFormatter formatter : FORMATTERS) {
                try {
                    return LocalDate.parse(source, formatter);
                } catch (DateTimeParseException e) {
                    log.debug("尝试解析日期格式失败: {} 使用格式: {}", source, formatter);
                }
            }

            throw new IllegalArgumentException("无法解析日期: " + source + "，支持的格式包括: yyyy-MM-dd, yyyy/MM/dd, yyyyMMdd");
        }
    }

    /**
     * 非json参数枚举转换
     */
    static class EnumConverterFactory implements ConverterFactory<String, Enum> {
        @Override
        public <T extends Enum> Converter<String, T> getConverter(Class<T> targetType) {
            // 在导出一对一时候办学层次枚举有数字
            return source -> {
                T t = (T) EnumUtil.likeValueOf(targetType, source);
                if (Objects.isNull(t) && NumberUtil.isInteger(source)) {
                    return (T) EnumUtil.likeValueOf(targetType, NumberUtil.parseInt(source));
                }
                return t;
            };
        }
    }
}