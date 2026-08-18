package cn.soboys.restapispringbootstarter.utils;



import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 切割工具
 *
 * @author E_Ivan
 * @since 2024/7/19 下午5:49
 */
public class SplitUtil extends cn.hutool.v7.core.text.split.SplitUtil {

    /**
     * 通常的分割，一半的隔开符号都可以分割
     *
     * @param str 原文
     * @return 分割后的集合
     */
    public static List<String> splitGeneral(CharSequence str) {
        return splitByRegex(str, "\\s|,|\t|，|。|\\||、|；|;|/", 0, true, true);
    }

    /**
     * 分割文字转换为枚举集合，这个使用{@link #splitGeneral}进行文本分割
     *
     * @param str   原文
     * @param clazz 需要转换的枚举类型
     * @param <E>   枚举
     * @return 枚举集合
     */
    public static <E extends Enum<E>> List<E> splitToEnumList(CharSequence str, Class<E> clazz) {
        if (StrUtil.isNotBlank(str)) {
            return splitGeneral(str).stream()
                    .map(s -> EnumUtil.likeValueOf(clazz, s))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}