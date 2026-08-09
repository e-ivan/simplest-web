package cn.soboys.restapispringbootstarter.serializer.jackson3;

import cn.hutool.v7.core.math.NumberUtil;
import cn.soboys.restapispringbootstarter.config.RestApiProperties;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

import java.math.BigDecimal;

/**
 * @author: siyulong
 * @date: 2021/10/31 01:49
 **/
@Slf4j
public class BigDecimalSerializer extends ValueSerializer<BigDecimal> {

    @Resource
    private RestApiProperties.JsonSerializeProperties jsonSerializeProperties;


    @Override
    public void serialize(BigDecimal value, JsonGenerator jgen, SerializationContext ctxt) {
        if (value != null) {
            jgen.writeString(NumberUtil.format(jsonSerializeProperties.getNumberForm(), value));
        } else {
            jgen.writeString("0.00");
        }
    }
}