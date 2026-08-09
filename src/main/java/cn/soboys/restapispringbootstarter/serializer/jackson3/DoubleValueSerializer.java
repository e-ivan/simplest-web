package cn.soboys.restapispringbootstarter.serializer.jackson3;

/**
 * @author 公众号 程序员三时
 * @version 1.0
 * @date 2023/4/29 23:19
 * @webSite https://github.com/coder-amiao
 * 自定义对Double 类型json数据序列化返回
 */

import cn.hutool.v7.core.math.NumberUtil;
import cn.soboys.restapispringbootstarter.config.RestApiProperties;
import jakarta.annotation.Resource;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

public class DoubleValueSerializer extends ValueSerializer<Double> {

    @Resource
    private RestApiProperties.JsonSerializeProperties jsonSerializeProperties;

    @Override
    public void serialize(Double value, JsonGenerator jgen, SerializationContext context) {
        if (value != null) {
            jgen.writeString(NumberUtil.format(jsonSerializeProperties.getNumberForm(), value));
        } else {
            jgen.writeString("0.00");
        }
    }
}
