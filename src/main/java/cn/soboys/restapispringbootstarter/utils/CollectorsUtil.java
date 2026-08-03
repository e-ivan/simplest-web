package cn.soboys.restapispringbootstarter.utils;


import org.dromara.hutool.core.stream.CollectorUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * @author E_Ivan
 * @date 2023/2/15 18:54
 */
public class CollectorsUtil extends CollectorUtil {

    /**
     * 根据key去重过滤
     */
    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        ConcurrentHashMap<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    /**
     * 支持key为null的分组
     */
    public static <T, A> Collector<T, ?, Map<A, List<T>>> groupByWithNullKeys(Function<? super T, ? extends A> classifier) {
        return Collectors.toMap(
                classifier,
                Collections::singletonList,
                (List<T> oldList, List<T> newEl) -> {
                    List<T> newList = new ArrayList<>(oldList.size() + 1);
                    newList.addAll(oldList);
                    newList.addAll(newEl);
                    return newList;
                }
        );
    }
}
