package cn.soboys.restapispringbootstarter.utils;

import cn.hutool.v7.core.reflect.FieldUtil;
import cn.hutool.v7.core.util.ObjUtil;

import java.lang.reflect.Field;

/**
 * 枚举转换工具类，兼容 Java 17+ 模块系统
 * 替代 Hutool 的 EnumUtil.likeValueOf，避免反射访问 Enum 私有字段
 *
 * @author E_Ivan
 */
public class EnumUtil extends cn.hutool.v7.core.util.EnumUtil {
    /**
     * 模糊匹配转换为枚举，给定一个值，匹配枚举中定义的所有字段名（包括name属性），一旦匹配到返回这个枚举对象，否则返回null
     *
     * @param <E>       枚举类型
     * @param enumClass 枚举类
     * @param value     值
     * @return 匹配到的枚举对象，未匹配到返回null
     */
    public static <E extends Enum<E>> E likeValueOf(final Class<E> enumClass, Object value) {
        if (null == enumClass || null == value) {
            return null;
        }
        if (value instanceof CharSequence) {
            value = value.toString().trim();
        }

        final Field[] fields = FieldUtil.getFields(enumClass);
        final E[] enums = getEnums(enumClass);
        String fieldName;
        for (final Field field : fields) {
            fieldName = field.getName();
            if (field.getType().isEnum() || fieldName.contains("$VALUES") || "ordinal".equals(fieldName) || field.getDeclaringClass().equals(Enum.class) && "name".equals(fieldName)) {
                // 跳过一些特殊字段
                continue;
            }
            for (final E enumObj : enums) {
                if (ObjUtil.equals(value, FieldUtil.getFieldValue(enumObj, field))) {
                    return enumObj;
                }
            }
        }
        try {
            return fromString(enumClass, value.toString());
        } catch (Exception ignored) {
        }
        return null;
    }
}