package cn.soboys.restapispringbootstarter.serializer;

import cn.hutool.v7.core.date.DateFormatPool;
import cn.hutool.v7.json.engine.jackson.HutoolModule;
import cn.soboys.restapispringbootstarter.config.RestApiProperties;
import cn.soboys.restapispringbootstarter.enums.EnumType;
import cn.soboys.restapispringbootstarter.handler.LocalDateTimeDeserializationProblemHandler;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.StreamReadConstraints;
import com.fasterxml.jackson.core.StreamWriteConstraints;
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
    public RestApiProperties.JsonSerializeProperties jsonSerializeProperties(RestApiProperties restApiProperties) {
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
        // 1. 先配置 JsonFactory 的读取约束
        StreamReadConstraints readConstraints = StreamReadConstraints.builder()
                .maxStringLength(100_000_000)      // 字符串最大长度，默认 20,000,000
                .maxNestingDepth(2000)              // 最大嵌套深度，默认 500
                .maxNumberLength(1000)              // 数字最大长度，默认 1,000
                .maxNameLength(50_000)              // 属性名最大长度，默认 50,000
                .maxDocumentLength(-1)              // 文档最大长度，-1 表示无限制，默认 -1
                .maxTokenCount(-1)                  // 最大 token 数量，-1 表示无限制，默认 -1
                .build();

        // 2. 配置写入约束（可选）
        StreamWriteConstraints writeConstraints = StreamWriteConstraints.builder()
                .maxNestingDepth(2000)              // 写入时最大嵌套深度，默认 500
                .build();

        // 3. 创建 JsonFactory 并设置约束
        JsonFactory jsonFactory = JsonFactory.builder()
                .streamReadConstraints(readConstraints)
                .streamWriteConstraints(writeConstraints)
                .build();
        ObjectMapper objectMapper = new ObjectMapper(jsonFactory);
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
        objectMapper.registerModule(new HutoolModule(DateFormatPool.NORM_DATETIME_PATTERN));
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.disable(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS);

        // 为mapper注册一个带有SerializerModifier的Factory，此modifier主要做的事情为：判断序列化类型，根据类型指定为null时的值
        objectMapper.setSerializerFactory(objectMapper.getSerializerFactory().withSerializerModifier(beanSerializerModifierFactory))
                .addHandler(new LocalDateTimeDeserializationProblemHandler());
        return objectMapper;
    }
}
