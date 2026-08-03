package cn.soboys.restapispringbootstarter.serializer;

import cn.soboys.restapispringbootstarter.enums.EnumType;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.List;

/**
 * 枚举名序列化
 *
 * @author E_Ivan
 * @date 2024/3/2 13:54
 */
public class ListEnumNameSerializer extends JsonSerializer<List<EnumType>> {

    @Override
    public void serialize(List<EnumType> value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        String[] array = value.stream().map(EnumType::getName).toArray(String[]::new);
        gen.writeArray(array, 0, array.length);
    }
}
