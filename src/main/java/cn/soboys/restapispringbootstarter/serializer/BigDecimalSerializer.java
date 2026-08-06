package cn.soboys.restapispringbootstarter.serializer;

import cn.hutool.v7.core.math.NumberUtil;
import cn.soboys.restapispringbootstarter.config.RestApiProperties;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * @author: siyulong
 * @date: 2021/10/31 01:49
 **/
@Slf4j
public class BigDecimalSerializer extends JsonSerializer<BigDecimal> {

    @Resource
    private RestApiProperties.JsonSerializeProperties jsonSerializeProperties;


    @Override
    public void serialize(BigDecimal value, JsonGenerator jgen, SerializerProvider serializerProvider) throws IOException {
        if (value != null) {
            jgen.writeString(NumberUtil.format(jsonSerializeProperties.getNumberForm(), value));
        } else {
            jgen.writeString("0.00");
        }
    }
}