package cn.soboys.restapispringbootstarter.utils;

import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.map.MapUtil;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 集合值填充工具
 *
 * @author E_Ivan
 * @date 2024/3/2 17:07
 */
@Slf4j
public class CollUtil extends org.dromara.hutool.core.collection.CollUtil {

    /**
     * 集合批量查询填充
     *
     * @param coll        集合
     * @param idFunc      集合元素获取id方法
     * @param mapFunction 集合id获取对应值的方法
     * @param fillFun     填充方法
     * @param size        每批大小
     * @param <T>         需要处理的类型
     * @param <R>         id类型
     * @param <E>         id结果类型
     */
    public static <T, R, E> void fill(Collection<T> coll, Function<T, R> idFunc, Function<Collection<R>, Map<R, E>> mapFunction, BiConsumer<T, E> fillFun, int size) {
        if (isNotEmpty(coll)) {
            for (List<T> list : split(coll, size)) {
                List<R> ids = list.stream().map(idFunc).filter(Objects::nonNull).distinct().collect(Collectors.toList());
                if (isNotEmpty(ids)) {
                    Map<R, E> reMap = mapFunction.apply(ids);
                    if (MapUtil.isNotEmpty(reMap)) {
                        for (T t : list) {
                            R id = idFunc.apply(t);
                            if (Objects.nonNull(id)) {
                                fillFun.accept(t, reMap.get(id));
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 集合批量查询填充
     *
     * @param coll        集合
     * @param idFunc      集合元素获取id方法
     * @param mapFunction 集合id获取对应值的方法
     * @param fillFun     填充方法
     * @param <T>         需要处理的类型
     * @param <R>         id类型
     * @param <E>         id结果类型
     */
    public static <T, R, E> void fill(Collection<T> coll, Function<T, R> idFunc, Function<Collection<R>, Map<R, E>> mapFunction, BiConsumer<T, E> fillFun) {
        fill(coll, idFunc, mapFunction, fillFun, Integer.MAX_VALUE);
    }


    /**
     * 切分集合
     *
     * @param collection 集合
     * @param size       每个大小
     * @param <T>        type
     * @return 集合集合
     */
    public static <T> List<List<T>> split(Collection<T> collection, int size) {
        final List<List<T>> result = new ArrayList<>();
        if (isEmpty(collection)) {
            return result;
        }

        final int initSize = Math.min(collection.size(), size);
        List<T> subList = new ArrayList<>(initSize);
        for (T t : collection) {
            if (subList.size() >= size) {
                result.add(subList);
                subList = new ArrayList<>(initSize);
            }
            subList.add(t);
        }
        result.add(subList);
        return result;
    }

    /**
     * 按给定索引集合切分集合，每个集合结束不包含索引结束所在位置
     * <p>
     * 例如集合有10个元素，现需要按索引集合位1,4,5,8切割，那个会分成一下索引
     * 0,1-3,4,5-7,8-9
     * </p>
     *
     * @param collection 集合
     * @param idxList    索引集合，必须是有序
     * @param <T>        type
     * @return 集合集合
     */
    public static <T> List<List<T>> split(Collection<T> collection, List<Integer> idxList) {
        final List<List<T>> result = new ArrayList<>();
        if (isEmpty(collection)) {
            return result;
        }
        //排除0
        idxList.remove((Object) 0);
        int i = 0;
        int initSize = isEmpty(idxList) ? collection.size() : Math.min(collection.size(), idxList.get(i));
        List<T> subList = new ArrayList<>(initSize);
        for (T t : collection) {
            if (subList.size() >= initSize) {
                result.add(subList);
                i++;
                Integer i1 = i >= idxList.size() ? collection.size() : idxList.get(i);
                initSize = i1 - idxList.get(i - 1);
                subList = new ArrayList<>(initSize);
            }
            subList.add(t);
        }
        result.add(subList);
        return result;
    }


    /**
     * 并行转换
     *
     * @param collection      集合
     * @param func            转换方法
     * @param executor        线程池
     * @param exceptionallyFn 异常后执行处理，不指定整个执行异常
     * @param timeout         超时时间
     * @param unit            单位
     */
    public static <T, R> List<R> mapParallel(Iterable<T> collection, Function<? super T, R> func, Function<Throwable, ? extends R> exceptionallyFn, Executor executor, long timeout, TimeUnit unit) {
        final List<R> fieldValueList = new ArrayList<>();
        if (null == collection) {
            return fieldValueList;
        }

        List<CompletableFuture<R>> futures = new ArrayList<>();
        Executor exc = Optional.ofNullable(executor).orElse(ForkJoinPool.commonPool());
        for (T t : collection) {
            Supplier<R> supplier = () -> func.apply(t);
//            try {
//                supplier = SupplierWrapper.of(supplier);
//            } catch (Throwable e) {
//                //兼容包不存在
//            }
            CompletableFuture<R> future;
            if (timeout > 0) {
                future = CompletableFutureExpandUtil.orTimeout(CompletableFuture.supplyAsync(supplier, exc), timeout, unit);
                if (exceptionallyFn != null) {
                    future = future.exceptionally(exceptionallyFn);
                }
            } else {
                future = CompletableFuture.supplyAsync(supplier, exc);
            }
            futures.add(future);
        }
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        CompletableFuture<? extends List<R>> completableFuture = allFutures.thenApply(v -> futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList()));
        return completableFuture.join();
    }

    public static <T, R> List<R> mapParallel(Iterable<T> collection, Function<? super T, R> func, Executor executor, long timeout, TimeUnit unit) {
        return mapParallel(collection, func, e -> {
            log.warn("执行并发异常", e);
            return null;
        }, executor, timeout, unit);
    }

    public static <T, R> List<R> mapParallel(Iterable<T> collection, Function<? super T, R> func, Executor executor) {
        return mapParallel(collection, func, null, executor, 0, null);
    }

    public static <T, R> List<R> mapParallel(Iterable<T> collection, Function<? super T, R> func) {
        return mapParallel(collection, func, null);
    }

    /**
     * 通过func自定义一个规则，此规则将原集合中的元素转换成新的元素，生成新的列表返回<br>
     * 例如提供的是一个Bean列表，通过Function接口实现获取某个字段值，返回这个字段值组成的新列表<br>
     * 默认忽略空值，这里的空值包括函数处理前和处理后的null值
     *
     * @param <T>        集合元素类型
     * @param <R>        返回集合元素类型
     * @param collection 原集合
     * @param func       编辑函数
     * @return 抽取后的新列表
     * @since 5.3.5
     */
    public static <T, R> List<R> map(Iterable<T> collection, Function<? super T, ? extends R> func) {
        return map(collection, func, true);
    }

}
