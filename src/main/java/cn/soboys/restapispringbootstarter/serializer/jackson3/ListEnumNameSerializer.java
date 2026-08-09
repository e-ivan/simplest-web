package cn.soboys.restapispringbootstarter.serializer.jackson3;

import cn.soboys.restapispringbootstarter.enums.EnumType;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

import java.util.List;

/**
 * 枚举名序列化
 *
 * @author E_Ivan
 * @date 2024/3/2 13:54
 */
public class ListEnumNameSerializer extends ValueSerializer<List<EnumType>> {

    @Override
    public void serialize(List<EnumType> value, JsonGenerator gen, SerializationContext context) {
        String[] array = value.stream().map(EnumType::getName).toArray(String[]::new);
        gen.writeArray(array, 0, array.length);
    }
}
