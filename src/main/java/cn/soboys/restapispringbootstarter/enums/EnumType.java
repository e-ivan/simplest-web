package cn.soboys.restapispringbootstarter.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author E_Ivan
 * @date 2024/3/2 14:30
 */
public interface EnumType extends IEnum<Integer> {
    @JsonValue
    Integer getValue();

    String getName();
}
