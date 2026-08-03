package cn.soboys.restapispringbootstarter.serializer;

import cn.soboys.restapispringbootstarter.enums.EnumType;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * 枚举名序列化
 *
 * @author E_Ivan
 * @date 2024/3/2 13:54
 */
public class EnumNameSerializer extends JsonSerializer<EnumType> {

    @Override
    public void serialize(EnumType value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(value.getName());
    }
}
