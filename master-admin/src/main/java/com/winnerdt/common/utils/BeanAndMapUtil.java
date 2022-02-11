package com.winnerdt.common.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * @author:zsk
 * @CreateTime:2018/7/5 10:46
 */
public class BeanAndMapUtil {
        /**
         * 实体对象转成Map
         * @param obj 实体对象
         * @return
         */
        public static Map<String, Object> objectToMap(Object obj) {
            Map<String, Object> map = new HashMap<>();
            if (obj == null) {
                return map;
            }
            Class clazz = obj.getClass();
            Field[] fields = clazz.getDeclaredFields();
            try {
                for (Field field : fields) {
                    field.setAccessible(true);
                    map.put(field.getName(), field.get(obj));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return map;
        }

        /**
         * Map转成实体对象
         * @param map map实体对象包含属性
         * @param clazz 实体对象类型
         * @return
         */
        public static Object mapToObject(Map<String, Object> map, Class<?> clazz) {
            if (map == null) {
                return null;
            }
            Object obj = null;
            try {
                obj = clazz.newInstance();


                Field[] fields = obj.getClass().getDeclaredFields();
                for (Field field : fields) {
                    int mod = field.getModifiers();
                    if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
                        continue;
                    }
                    field.setAccessible(true);
                    if(field.getType() == int.class || field.getType() == Integer.class){
                        field.set(obj, Integer.valueOf(map.get(field.getName()).toString()));
                    }else {
                        if(field.getType() == double.class || field.getType() == Double.class){
                            field.set(obj, Double.valueOf(map.get(field.getName()).toString()));
                        }else {
                            field.set(obj, map.get(field.getName()));
                        }
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return obj;
        }
}