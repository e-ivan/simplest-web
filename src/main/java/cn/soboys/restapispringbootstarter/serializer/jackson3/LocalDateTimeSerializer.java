package cn.soboys.restapispringbootstarter.serializer.jackson3;

import cn.hutool.v7.core.date.DateUtil;
import cn.soboys.restapispringbootstarter.config.RestApiProperties;
import jakarta.annotation.Resource;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * @author 公众号 程序员三时
 * @version 1.0
 * @date 2023/7/21 23:03
 * @webSite https://github.com/coder-amiao
 */
public class LocalDateTimeSerializer extends ValueSerializer<LocalDateTime> {

    @Resource
    private RestApiProperties.JsonSerializeProperties jsonSerializeProperties;

    @Override
    public void serialize(LocalDateTime value, JsonGenerator jgen, SerializationContext context) {
        if (jsonSerializeProperties.getDateForm().equals("timestamp")) {
            jgen.writeString(String.valueOf(value.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));
        } else {
            jgen.writeString(DateUtil.format(value, jsonSerializeProperties.getDateForm()));
        }
    }
}
