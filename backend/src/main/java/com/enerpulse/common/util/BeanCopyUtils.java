package com.enerpulse.common.util;

import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

public class BeanCopyUtils {

    public static <S, T> T copy(S source, Class<T> targetClass) {
        if (source == null) return null;
        try {
            T target = targetClass.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(source, target);
            return target;
        } catch (Exception e) {
            throw new RuntimeException("Bean copy failed: " + e.getMessage(), e);
        }
    }

    public static <S, T> List<T> copyList(List<S> sourceList, Class<T> targetClass) {
        if (sourceList == null) return List.of();
        return sourceList.stream()
                .map(s -> copy(s, targetClass))
                .collect(Collectors.toList());
    }
}
