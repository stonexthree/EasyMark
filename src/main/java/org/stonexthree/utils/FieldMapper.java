package org.stonexthree.utils;

import java.util.AbstractMap;
import java.util.Map;

/**
 * 用于把对象中的某个域映射成另一个值
 */
public class FieldMapper {

    /**
     * 把Map.Entry[] 中的key映射成另一个值,字典中没有找到对应的则不做映射
     *
     * @param target       目标数组
     * @param dictionary 映射字典
     * @param <K>
     * @param <V>
     * @return 返回一个新的Map.Entry[]
     */
    public static <K, V> Map.Entry<K, V>[] entryArrayKeyMapper(Map.Entry<K, V>[] target, Map<K, K> dictionary) {
        Map.Entry<K, V>[] result = new Map.Entry[target.length];
        for (int i = 0; i < target.length; i++) {
            K key = dictionary.get(target[i].getKey());
            key = key == null ? target[i].getKey() : key;
            result[i] = new AbstractMap.SimpleEntry<>(key, target[i].getValue());
        }
        return result;
    }
}
