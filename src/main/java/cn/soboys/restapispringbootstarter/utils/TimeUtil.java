package cn.soboys.restapispringbootstarter.utils;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author E_Ivan
 * @since 2026/5/22 20:10
 */
@Slf4j
public class TimeUtil extends org.dromara.hutool.core.date.TimeUtil{
    private static final DateTimeFormatter[] FORMATTERS = new DateTimeFormatter[]{
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("yyyy-M-d H:m:s"),
            DateTimeFormatter.ofPattern("yyyy-M-d H:m"),
            DateTimeFormatter.ofPattern("yyyy-M-d"),
    };

    public static LocalDateTime autoParse(String text) {
        for (DateTimeFormatter formatter : FORMATTERS) {
            try {
                return parse(text, formatter);
            } catch (Exception e) {
                log.debug("尝试解析日期格式失败:{}", text);
                // 再降级使用日期方式
            }
        }
        return null;
    }

}
