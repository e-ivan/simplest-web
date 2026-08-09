package cn.soboys.restapispringbootstarter.serializer.jackson3;

import cn.soboys.restapispringbootstarter.enums.EnumType;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

/**
 * 枚举名序列化
 *
 * @author E_Ivan
 * @date 2024/3/2 13:54
 */
public class EnumNameKeySerializer extends ValueSerializer<EnumType> {

    @Override
    public void serialize(EnumType value, JsonGenerator gen, SerializationContext context) {
        gen.writeName(value.getName());
    }
}
