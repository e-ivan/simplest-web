package cn.soboys.restapispringbootstarter.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.math.NumberUtil;
import org.dromara.hutool.core.util.EnumUtil;
import org.dromara.hutool.extra.spring.SpringUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.format.FormatterRegistry;
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


    /**
     * 授权登录拦截器
     *
     * @return
     */
    @Bean
    public JwtTokenInterceptor jwtTokenInterceptor() {
        return new JwtTokenInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(SpringUtil.getBean(JwtTokenInterceptor.class));
        registry.addInterceptor(new WebInvokeTimeInterceptor()).order(0);
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
