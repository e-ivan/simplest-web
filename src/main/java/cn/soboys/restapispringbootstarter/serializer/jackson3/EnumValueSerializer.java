package cn.soboys.restapispringbootstarter.serializer.jackson3;

import com.baomidou.mybatisplus.annotation.IEnum;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

/**
 * 枚举值序列化
 *
 * @author E_Ivan
 * @date 2024/3/2 13:54
 */
public class EnumValueSerializer extends ValueSerializer<IEnum<Integer>> {

    @Override
    public void serialize(IEnum<Integer> value, JsonGenerator gen, SerializationContext context) {
        gen.writeNumber(value.getValue());
    }
}
