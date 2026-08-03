package cn.soboys.restapispringbootstarter.serializer;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * 枚举值序列化
 *
 * @author E_Ivan
 * @date 2024/3/2 13:54
 */
public class EnumValueSerializer extends JsonSerializer<IEnum<Integer>> {

    @Override
    public void serialize(IEnum<Integer> value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeNumber(value.getValue());
    }
}
