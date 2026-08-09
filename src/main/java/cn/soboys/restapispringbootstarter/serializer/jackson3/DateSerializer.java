package cn.soboys.restapispringbootstarter.serializer.jackson3;

import cn.hutool.v7.core.date.DateUtil;
import cn.soboys.restapispringbootstarter.config.RestApiProperties;
import jakarta.annotation.Resource;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

import java.util.Date;

/**
 * @author 公众号 程序员三时
 * @version 1.0
 * @date 2023/7/21 23:03
 * @webSite https://github.com/coder-amiao
 */
public class DateSerializer extends ValueSerializer<Date> {

    @Resource
    private RestApiProperties.JsonSerializeProperties jsonSerializeProperties;

    @Override
    public void serialize(Date value, JsonGenerator jgen, SerializationContext ctxt) {
        if (value != null) {
            //DateUtil.format(value, DatePattern.NORM_DATETIME_MS_PATTERN);
            if (jsonSerializeProperties.getDateForm().equals("timestamp")) {
                jgen.writeString(String.valueOf(value.getTime()));
            } else {
                jgen.writeString(DateUtil.format(value, jsonSerializeProperties.getDateForm()));
            }

        }
    }
}
