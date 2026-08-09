package cn.soboys.restapispringbootstarter.serializer.jackson3;

import cn.soboys.restapispringbootstarter.exception.JsonSerializableException;
import cn.soboys.restapispringbootstarter.serializer.SerializableType;
import cn.soboys.restapispringbootstarter.utils.StrUtil;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

/**
 * @author 公众号 程序员三时
 * @version 1.0
 * @date 2023/7/21 23:03
 * @webSite https://github.com/coder-amiao
 */
public class NullAbleSerializer extends ValueSerializer<Object> {

    private SerializableType serializableType;


    public void setSerializableType(SerializableType serializableType) {
        this.serializableType = serializableType;
    }

    @Override
    public void serialize(Object value, JsonGenerator jgen, SerializationContext context) {
        if (value == null && this.serializableType != null) {

            String t = this.serializableType.getType(); //original
            if (StrUtil.isNotEmpty(t)) {
                if (t.equals("array")) {
                    jgen.writeStartArray();
                    jgen.writeEndArray();
                } else if (t.equals("string")) {
                    jgen.writeString("");
                } else if (t.equals("double")) {
                    jgen.writeString("0.00");
                } else if (t.equals("number")) {
                    jgen.writeString("0");
                    //jgen.writeNumber(0);
                } else {
                    throw new JsonSerializableException("找不到对应 nullAble 类型处理");
                }
            }
        }
    }


}
