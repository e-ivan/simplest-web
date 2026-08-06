package cn.soboys.restapispringbootstarter.utils;

import org.springframework.util.AntPathMatcher;

import java.util.List;

/**
 * @author E_Ivan
 * @since 2024/8/12 下午4:02
 */
public class StrUtil extends cn.hutool.v7.core.text.StrUtil {
    private static final String[] GENERAL_BLANK = {"-", "#N,A", "--", "—"};

    public static boolean isBlankGeneral(String str) {
        return isBlank(str) || equalsAny(cleanBlank(str), GENERAL_BLANK);
    }

    public static boolean isNotBlankGeneral(String str) {
        return !isBlankGeneral(str);
    }

    public static String defaultIfBlankGeneral(String str, String defaultStr) {
        return isBlankGeneral(str) ? defaultStr : str;
    }

    public static String defaultNullIfBlankGeneral(String str) {
        return defaultIfBlankGeneral(str, null);
    }

    public static String defaultEmptyIfBlankGeneral(String str) {
        return defaultIfBlankGeneral(str, "");
    }


    /**
     * 保留所有数字字符
     */
    public static String extractNumbers(String input) {
        if (isBlank(input)) {
            return input;
        }
        return input.replaceAll("[^0-9]", "");
    }

    /**
     * 字符串是否符合当前条件。条件文字使用运算符&|来分开，取反使用!
     * <p>
     *  学分互认&与韩国庆南大学
     * </p>
     *
     * @param condition 需要满足条件
     * @param value     判断的字符
     * @return 是否匹配
     */
    public static boolean condition(String condition, String value) {
        List<String> orCondition = SplitUtil.split(condition, Strings.PIPE);
        return orCondition.stream().anyMatch(or -> {
            List<String> andCondition = SplitUtil.split(or, Strings.AMPERSAND);
            return andCondition.stream().allMatch(n -> {
                String trim = StrUtil.trim(n);
                boolean neg = StrUtil.startWith(trim, Strings.EXCLAMATION_MARK);
                String s = StrUtil.removePrefix(trim, Strings.EXCLAMATION_MARK);
                return neg != StrUtil.containsIgnoreCase(value, s);
            });
        });
    }

    /**
     * 查找指定字符串是否匹配指定字符串列表中的任意一个字符串
     *
     * @param str  指定字符串
     * @param strs 需要检查的字符串数组
     * @return 是否匹配
     */
    public static boolean matches(String str, List<String> strs) {
        if (isEmpty(str) || CollUtil.isEmpty(strs)) {
            return false;
        }
        for (String pattern : strs) {
            if (isPatchMatch(pattern, str)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 判断url是否与规则配置:
     * ? 表示单个字符;
     * * 表示一层路径内的任意字符串，不可跨层级;
     * ** 表示任意层路径;
     *
     * @param pattern 匹配规则
     * @param url     需要匹配的url
     */
    public static boolean isPatchMatch(String pattern, String url) {
        AntPathMatcher matcher = new AntPathMatcher();
        return matcher.match(pattern, url);
    }
}
