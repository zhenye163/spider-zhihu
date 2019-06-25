package com.netopstec.spiderzhihu.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * 某些字符串类型的字段值中可能会有双引号。这样会影响解析，需要预先删除这些会影响解析的双引号。
     * @param originJsonStr 原始的JSON串
     * @param isLastField 这个不标准的字段是否是某个对象的最后一个属性
     * @param notStandardField 这个不标准的字段的名称
     * @param nextField 如果不标准的字段不是最后一个属性，需要传入下一个字段名称
     * @return
     */
    public static String removeDoubleQuotationMark(String originJsonStr, Boolean isLastField, String notStandardField, String nextField){
        if (StringUtils.isBlank(originJsonStr) || StringUtils.isBlank(notStandardField)) {
            return originJsonStr;
        }
        char[] charArray = originJsonStr.toCharArray();
        notStandardField = "\"" + notStandardField + "\"";
        // 如果这个字段是某个对象的最后一个字段,结尾是}；否则需要传入其下一个字段
        nextField = isLastField ? "}" : ("\"" + nextField + "\"");
        int i = 0;
        do {
            // 找到这个字段的值的位置
            int index = originJsonStr.indexOf(notStandardField, i);
            if (index == -1) {
                break;
            }
            int start = index + notStandardField.length();
            int end = originJsonStr.indexOf(nextField, start) - 1;
            // 然后替换它内部的双引号
            replaceDoubleQuotationMark(charArray, start, end);
            i = end + nextField.length();
        } while (i < charArray.length);
        return new String(charArray);
    }

    /**
     * 将字符数组从start到end位置的字符中，除第一次和最后一次以外出现的双引号替换为空格
     */
    private static void replaceDoubleQuotationMark(char[] charArray, int start, int end) {
        int times = 0;
        Map<Integer, Integer> timesIndexMap = new HashMap<>();
        for (int i = start; i <= end; i++) {
            if (charArray[i] == '"') {
                timesIndexMap.put(++times, i);
            }
        }
        for (int thisTime = 2; thisTime < timesIndexMap.size(); thisTime++) {
            int i = timesIndexMap.get(thisTime);
            charArray[i] = ' ';
        }
    }

    /**
     * 某些字符串类型的字段值中可能会有特殊字符，需要删除其值，便于JSON正确解析。
     * @param originJsonStr 原始的JSON串
     * @param isLastField 这个不标准的字段是否是某个对象的最后一个属性
     * @param thisField 这个字段的名称
     * @param nextField 如果不标准的字段不是最后一个属性，需要传入下一个字段名称
     * @return
     */
    public static String removeTheStringFieldValue(String originJsonStr, Boolean isLastField, String thisField, String nextField) {
        if (StringUtils.isBlank(originJsonStr) || StringUtils.isBlank(thisField)) {
            return originJsonStr;
        }
        char[] charArray = originJsonStr.toCharArray();
        thisField = "\"" + thisField + "\"";
        // 如果这个字段是某个对象的最后一个字段,结尾是}；否则需要传入其下一个字段
        nextField = isLastField ? "}" : ("\"" + nextField + "\"");
        int i = 0;
        do {
            // 找到这个字段的值的位置
            int index = originJsonStr.indexOf(thisField, i);
            if (index == -1) {
                break;
            }
            int start = index + thisField.length();
            int end = originJsonStr.indexOf(nextField, start) - 1;
            // 删除双引号内部的所有内容
            removeTextBetweenDoubleQuotationMark(charArray, start, end);
            i = end + nextField.length();
        } while (i < charArray.length);
        return new String(charArray);
    }

    /**
     * 将字符串类型值删除（双引号内部的字符全改为空格）
     */
    private static void removeTextBetweenDoubleQuotationMark(char[] charArray, int start, int end) {
        int times = 0;
        Map<Integer, Integer> timesIndexMap = new HashMap<>();
        for (int i = start; i <= end; i++) {
            if (charArray[i] == '"') {
                timesIndexMap.put(++times, i);
            }
        }
        int quotStartIndex = timesIndexMap.get(1);
        int quotEndIndex = timesIndexMap.get(times);
        for (int i = quotStartIndex + 1; i < quotEndIndex; i++) {
            charArray[i] = ' ';
        }
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
