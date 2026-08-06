package cn.soboys.restapispringbootstarter.handler;

import cn.soboys.restapispringbootstarter.utils.TimeUtil;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import lombok.extern.slf4j.Slf4j;
import cn.hutool.v7.core.reflect.ClassUtil;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * LocalDateTime问题处理
 *
 * @author ex_lianghf8
 * @since 2024/8/25 上午10:07
 */
@Slf4j
public class LocalDateTimeDeserializationProblemHandler extends DeserializationProblemHandler {

    @Override
    public Object handleWeirdStringValue(DeserializationContext ctxt, Class<?> targetType, String valueToConvert, String failureMsg) throws IOException {
        if (ClassUtil.isAssignable(targetType, LocalDateTime.class)) {
            LocalDateTime dateTime = TimeUtil.autoParse(valueToConvert);
            if (Objects.nonNull(dateTime)) {
                return dateTime;
            }
        }
        return super.handleWeirdStringValue(ctxt, targetType, valueToConvert, failureMsg);
    }
}
