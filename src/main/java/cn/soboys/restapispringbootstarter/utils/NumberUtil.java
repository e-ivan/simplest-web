package cn.soboys.restapispringbootstarter.utils;


import cn.hutool.v7.core.lang.tuple.Pair;
import cn.hutool.v7.core.util.ObjUtil;

import java.util.Arrays;

/**
 * 数字工具
 *
 * @author ex_lianghf8
 * @since 2024/7/25 下午5:01
 */
public class NumberUtil extends cn.hutool.v7.core.math.NumberUtil {

    /**
     * 查找数字相邻的数字
     *
     * @param array  有序的数字集合
     * @param target 目标数
     * @param before 是否找前面的值，否则后面的值
     * @return 等于或前后相邻的数字
     */
    public static int findNearestValue(Integer[] array, int target, boolean before) {
        int index = Arrays.binarySearch(array, target);
        if (index >= 0) {
            return array[index];
        } else {
            index = -(index + 1);
            if (index == 0) {
                return array[0];
            } else if (index == array.length) {
                return array[array.length - 1];
            } else {
                return before ? array[index - 1] : array[index];
            }
        }
    }

    /**
     * 查找数字相邻的后面的数
     *
     * @param array  有序的数字集合
     * @param target 目标数
     * @return 等于或后面相邻的数字
     */
    public static int findAfterNearestValue(Integer[] array, int target) {
        return findNearestValue(array, target, false);
    }

    /**
     * 查找数字相邻的前面的数
     *
     * @param array  有序的数字集合
     * @param target 目标数
     * @return 等于或前面相邻的数字
     */
    public static int findBeforeNearestValue(Integer[] array, int target) {
        return findNearestValue(array, target, true);
    }


    /**
     * 查找数字相邻的两个数
     *
     * @param array  有序的集合
     * @param target 目标值
     * @return 相邻的两个值
     */
    public static Pair<Integer, Integer> findNeighbors(Integer[] array, int target) {
        int index = Arrays.binarySearch(array, target);
        int lowerNeighborIndex = index;
        int upperNeighborIndex = index;

        // 如果目标值在数组中
        if (index >= 0) {
            // 如果不是最后一个元素，那么后一个元素是上侧数
            if (index < array.length - 1) {
                upperNeighborIndex = index + 1;
            }
        } else {
            // 如果目标值不在数组中，计算插入点
            index = -(index + 1);
            // 下侧数是插入点之前的元素
            lowerNeighborIndex = index - 1;
            // 上侧数是插入点处的元素
            upperNeighborIndex = index;
        }

        // 确保索引不会越界
        if (lowerNeighborIndex < 0) {
            lowerNeighborIndex = 0;
        }
        if (upperNeighborIndex >= array.length) {
            upperNeighborIndex = array.length - 1;
        }

        return Pair.of(array[lowerNeighborIndex], array[upperNeighborIndex]);
    }

    /**
     * 寻找临界值，在给定开始和结束的临界点，如果数值在范围内，则返回原值；如果超出范围，在范围左边拿最开始节点，在右边拿最后节点
     *
     * @param start 开始值
     * @param end   结束值
     * @param point 寻找的点
     * @return 临界值或本身
     */
    public static int findCriticalValue(int start, int end, int point) {
        if (point > end) {
            return end;
        } else if (point < start) {
            return start;
        }
        return point;
    }

    public static Integer parseIntDefaultNull(String numberStr) {
        return parseInt(numberStr, null);
    }

    public static Integer parseIntDefaultZero(String numberStr) {
        return parseInt(numberStr, 0);
    }

    public static int negativeOrNull2Zero(Integer number) {
        Integer i = ObjUtil.defaultIfNull(number, 0);
        if (i < 0) {
            i = 0;
        }
        return i;
    }

    public static String negative2Str(Integer num, String value) {
        if (ObjUtil.defaultIfNull(num, -1) < 0) {
            return value;
        }
        return String.valueOf(num);
    }

    /**
     * 查找给定的数在指定范围内按数量分段后的哪一段
     *
     * @param start   范围开始
     * @param end     范围结束
     * @param section 需要分多少段
     * @param value   查找的数
     * @return 所在的位置
     */
    public static int findNumSegmentInRange(Integer start, Integer end, Integer section, Integer value) {
        int segment = (int) Math.floor((double) (value - start) / ((double) (end - start) / section)) + 1;
        segment = Math.max(1, segment);
        segment = Math.min(segment, section);
        return segment;
    }

}
