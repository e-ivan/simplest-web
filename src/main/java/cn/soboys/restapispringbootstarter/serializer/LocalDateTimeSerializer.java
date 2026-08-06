package cn.soboys.restapispringbootstarter.serializer;

import cn.hutool.v7.core.date.DateUtil;
import cn.soboys.restapispringbootstarter.config.RestApiProperties;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import jakarta.annotation.Resource;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * @author 公众号 程序员三时
 * @version 1.0
 * @date 2023/7/21 23:03
 * @webSite https://github.com/coder-amiao
 */
public class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {

    @Resource
    private RestApiProperties.JsonSerializeProperties jsonSerializeProperties;

    @Override
    public void serialize(LocalDateTime value, JsonGenerator jgen, SerializerProvider serializerProvider) throws IOException {
        if (jsonSerializeProperties.getDateForm().equals("timestamp")) {
            jgen.writeString(String.valueOf(value.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));
        } else {
            jgen.writeString(DateUtil.format(value, jsonSerializeProperties.getDateForm()));
        }
    }
}
