package com.hyd.appserver.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 将两个数组的元素合并，并以 List 的方式返回；对于两个数组中都存在的“重复”元素，取 subArr 的。
 *
 * @author yiding.he
 */
public abstract class ArrayMerger<E> {

    /**
     * 将两个数组的元素合并，并以 List 的方式返回；对于两个数组中都存在的“重复”元素，取 subArr 的。
     *
     * @param superArr “父”数组（优先级较低）
     * @param subArr   “子”数组（优先级较高）
     *
     * @return 合并后的列表
     */
    public List<E> merge(E[] superArr, E[] subArr) {
        List<E> result = new ArrayList<E>();

        for (E sup : superArr) {
            if (!isPropertyInArray(sup, subArr)) {
                result.add(sup);
            }
        }

        result.addAll(Arrays.asList(subArr));
        return result;
    }

    private boolean isPropertyInArray(E element, E[] arr) {
        for (E e : arr) {
            if (isEqual(e, element)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断两个元素是否“重复”
     *
     * @param element1 元素1
     * @param element2 元素2
     *
     * @return 如果相同则返回 true
     */
    protected abstract boolean isEqual(E element1, E element2);
}
