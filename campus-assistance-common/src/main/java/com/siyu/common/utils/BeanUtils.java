package com.siyu.common.utils;

import cn.hutool.core.bean.BeanUtil;

import java.util.Map;

public class BeanUtils {
    public static Map<String, Object> obj2Map(Object object) {
        return BeanUtil.beanToMap(object);
    }

    public static Map<String, Object> obj2Map(Object object, boolean ignoreNullValue) {
        return BeanUtil.beanToMap(object, false, ignoreNullValue);
    }

    public static <T> T map2Obj(Map<String, Object> map, Class<T> clazz) {
        return BeanUtil.toBean(map, clazz);
    }

    public static <T> T copyProperties(Object source, T target) {
        BeanUtil.copyProperties(source, target);
        return target;
    }

    public static <T> T copyProperties(Object source, T target, String ...ignoreProperties) {
        BeanUtil.copyProperties(source, target, ignoreProperties);
        return target;
    }
}
