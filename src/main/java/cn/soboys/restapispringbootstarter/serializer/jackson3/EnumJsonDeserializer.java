package cn.soboys.restapispringbootstarter.serializer.jackson3;

import cn.soboys.restapispringbootstarter.utils.EnumUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

/**
 * @author E_Ivan
 * @since 2025/6/8 16:36
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EnumJsonDeserializer extends ValueDeserializer<Enum<?>> {
    private Class<?> target;

    @Override
    public ValueDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) {
        Class<?> rawCls = ctxt.getContextualType().getRawClass();
        EnumJsonDeserializer enumDeserializer = new EnumJsonDeserializer();
        enumDeserializer.setTarget(rawCls);
        return enumDeserializer;
    }

    @Override
    public Enum<?> deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException {
        if (Enum.class.isAssignableFrom(target)) {
            if (p.hasToken(JsonToken.VALUE_STRING)) {
                return EnumUtil.likeValueOf((Class) target, p.getText());
            }

            if (p.hasToken(JsonToken.VALUE_NUMBER_INT)) {
                return EnumUtil.likeValueOf((Class) target, p.getIntValue());
            }
        }
        return null;
    }
}