package cn.soboys.restapispringbootstarter.serializer;

import cn.soboys.restapispringbootstarter.config.RestApiProperties;
import cn.soboys.restapispringbootstarter.enums.EnumType;
import cn.soboys.restapispringbootstarter.handler.LocalDateTimeDeserializationProblemHandler;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author 公众号 程序员三时
 * @version 1.0
 * @date 2023/4/29 23:27
 * @webSite https://github.com/coder-amiao
 * 注册自定义json序列化器
 */
@JsonInclude(JsonInclude.Include.ALWAYS)
public class JsonSerializerConfig {

    @Bean
    @ConditionalOnMissingBean
    public RestApiProperties.JsonSerializeProperties jsonSerializeProperties() {
        return new RestApiProperties.JsonSerializeProperties();
    }

    @Bean
    @ConditionalOnMissingBean
    public DoubleValueSerializer doubleValueSerializer() {
        return new DoubleValueSerializer();
    }

    @Bean
    @ConditionalOnMissingBean
    public BigDecimalSerializer bigDecimalSerializer() {
        return new BigDecimalSerializer();
    }

    @Bean
    @ConditionalOnMissingBean
    public DateSerializer dateSerializer() {
        return new DateSerializer();
    }

    @Bean
    @ConditionalOnMissingBean
    public LocalDateTimeSerializer localDateSerializer() {
        return new LocalDateTimeSerializer();
    }

    @Bean
    @ConditionalOnMissingBean
    public LocalDateTimeDeserializer localDateTimeDeserializer(RestApiProperties.JsonSerializeProperties jsonSerializeProperties) {
        return new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(jsonSerializeProperties.getDateForm()));
    }

    @Bean
    @ConditionalOnMissingBean
    public BeanSerializerModifierFactory beanSerializerModifierFactory() {
        return new BeanSerializerModifierFactory();
    }

    @Bean
    @ConditionalOnMissingBean
    public EnumValueSerializer enumValueSerializer() {
        return new EnumValueSerializer();
    }

    @Bean
    @ConditionalOnMissingBean
    public EnumJsonDeserializer enumJsonDeserializer() {
        return new EnumJsonDeserializer();
    }

    @Bean
    public ObjectMapper objectMapper(DoubleValueSerializer doubleValueSerializer, BigDecimalSerializer bigDecimalSerializer,
                                     DateSerializer dateSerializer, EnumValueSerializer enumValueSerializer,
                                     EnumJsonDeserializer enumJsonDeserializer,
                                     BeanSerializerModifierFactory beanSerializerModifierFactory,
                                     LocalDateTimeSerializer localDateTimeSerializer, LocalDateTimeDeserializer localDateTimeDeserializer
    ) {
        ObjectMapper objectMapper = new ObjectMapper();
        //Jackson 当属性null 不会序列化。
//        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        JavaTimeModule module = new JavaTimeModule();
        module.addSerializer(Double.class, doubleValueSerializer);
        module.addSerializer(BigDecimal.class, bigDecimalSerializer);
        module.addSerializer(Date.class, dateSerializer);
        module.addSerializer(LocalDateTime.class, localDateTimeSerializer);
        module.addSerializer(EnumType.class, enumValueSerializer);
        module.addDeserializer(LocalDateTime.class, localDateTimeDeserializer);
        module.addDeserializer(Enum.class, enumJsonDeserializer);
        objectMapper.registerModule(module);
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.disable(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS);

        // 为mapper注册一个带有SerializerModifier的Factory，此modifier主要做的事情为：判断序列化类型，根据类型指定为null时的值
        objectMapper.setSerializerFactory(objectMapper.getSerializerFactory().withSerializerModifier(beanSerializerModifierFactory))
                .addHandler(new LocalDateTimeDeserializationProblemHandler());

        return objectMapper;
    }
}
