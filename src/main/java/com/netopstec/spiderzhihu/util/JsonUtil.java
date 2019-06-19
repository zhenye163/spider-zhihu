package com.netopstec.spiderzhihu.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Json工具类
 * @author zhenye 2018/9/11
 */
@Slf4j
public class JsonUtil {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();


    /**
     * 将对象转化成Json字符串
     * @param obj 对象
     * @return Json字符串
     */
    public static String obj2String(Object obj){
        if (obj != null){
            try {
                return OBJECT_MAPPER.writeValueAsString(obj);
            } catch (JsonProcessingException e) {
                log.error("error occurred in JsonUtil.obj2String(Object obj)--->",e);
            }
        }
        return null;
    }

    /**
     * 将Json字符串转化成对象
     * @param str Json字符串
     * @param clazz 对象的类对象
     * @return 对象
     */
    public static <T> T string2Obj(String str, Class<T> clazz){
        if (str != null && !"".equals(str.trim())){
            try {
                return OBJECT_MAPPER.readValue(str,clazz);
            } catch (IOException e) {
                log.error("error occurred in JsonUtil.string2Obj(String str, Class<T> clazz)--->",e);
            }
        }
        return null;
    }

    /**
     * 将Json字符串转化成List集合
     * @param str Json字符串
     * @param clazz 对象的类对象
     * @return List集合
     */
    public static <T> List<T> string2List(String str,Class<T> clazz){
        if (str != null && !"".equals(str.trim())){
            JavaType javaType = getDefaultCollectType(clazz);
            try {
                return OBJECT_MAPPER.readValue(str, javaType);
            } catch (IOException e) {
                log.error("error occurred in JsonUtil.string2List(String str,Class<T> clazz)--->",e);
            }
        }
        return null;
    }

    private static <T> JavaType getDefaultCollectType(Class<T> clazz) {
        return OBJECT_MAPPER.getTypeFactory().constructCollectionLikeType(ArrayList.class,clazz);
    }
}
