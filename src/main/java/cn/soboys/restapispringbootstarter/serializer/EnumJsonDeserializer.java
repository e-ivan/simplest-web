package cn.soboys.restapispringbootstarter.serializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.hutool.core.util.EnumUtil;

import java.io.IOException;

/**
 * @author E_Ivan
 * @since 2025/6/8 16:36
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EnumJsonDeserializer extends JsonDeserializer<Enum<?>> implements ContextualDeserializer {
    private Class<?> target;

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
        Class<?> rawCls = ctxt.getContextualType().getRawClass();
        EnumJsonDeserializer enumDeserializer = new EnumJsonDeserializer();
        enumDeserializer.setTarget(rawCls);
        return enumDeserializer;
    }

    @Override
    public Enum<?> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
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
