package cn.soboys.simplest.jpa.jpalazy;

import cn.soboys.simplest.core.utils.EntityUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Hibernate;
import org.hibernate.collection.internal.AbstractPersistentCollection;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Component;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.*;


/**
 * @version: 2.2
 * @author: siyulong
 * @description: JsonBody注解工具类
 * @date: 2019/12/28
 */
@Component
@Slf4j
public class JsonBodyUtil {

    public static final String ROOT = "root";
    private static final String HIDE = "hide";
    private static final String NEST_SPLIT_CHAR = ":";
    private static final String ENCRYPT = "encrypt";
    private static final String SPLIT_CHAR = "/";
    private static final String DOT_CHAR = ".";
    private static final String ALL_CHAR = "*";
    private static final String LIST_FLAG = "[]";

    private static Set<String> ignorePropertyNames;

    private static Map<String, Map<Integer, Map<String, Set<String>>>> includeMaps;
    private static Map<String, Map<Integer, Map<String, Set<String>>>> excludeMaps;
    private static Map<String, Map<Integer, Map<String, Set<String>>>> encryptMaps;
    private static Map<String, Map<String, Map<String, Set<String>>>> exclNestMaps;

    private static boolean isPrintStackTrace = false;

    static {
        includeMaps = new HashMap<>();
        excludeMaps = new HashMap<>();
        encryptMaps = new HashMap<>();
        exclNestMaps = new HashMap<>();
        ignorePropertyNames = EntityUtil.ignorePropertyNames;
    }

    private static ThreadLocal<JsonBody> jsonBodyThreadLocal = new ThreadLocal<JsonBody>();
    private static ThreadLocal<String> genericMethodNameThreadLocal = new ThreadLocal<String>();

    public static void storeJsonBody(JsonBody jsonBody, String genericMethodName) {
        jsonBodyThreadLocal.set(jsonBody);
        genericMethodNameThreadLocal.set(genericMethodName);
    }

    private static JsonBody getJsonBody() {
        return jsonBodyThreadLocal.get();
    }

    private static String getGenericMethodName() {
        return genericMethodNameThreadLocal.get();
    }

    private static void clearThreadLocal() {
        jsonBodyThreadLocal.remove();
        genericMethodNameThreadLocal.remove();
    }

    public static Object parse(Object object) {
        JsonBodyVo jsonBody = getJsonBodyVo(getJsonBody());
        if (jsonBody != null) {
            try {
                boolean isCollection = (object instanceof Collection);
                //Map<层级(1~n),Map<父属性串(obj.xxx),Set<父属性串下的子属性名>>>
                Map<Integer, Map<String, Set<String>>> includeMap = null;
                Map<Integer, Map<String, Set<String>>> excludeMap = null;
                Map<Integer, Map<String, Set<String>>> encryptMap = null;
                //Map<父属性串(obj.xxx),Map<嵌套属性名,Set<嵌套属性下的子属性名>>>
                Map<String, Map<String, Set<String>>> exclNestMap = null;
                if (isExpressionParsed()) {
                    includeMap = includeMaps.get(getGenericMethodName());
                    excludeMap = excludeMaps.get(getGenericMethodName());
                    encryptMap = encryptMaps.get(getGenericMethodName());
                    exclNestMap = exclNestMaps.get(getGenericMethodName());
                } else {
                    includeMap = new HashMap<>();
                    excludeMap = new HashMap<>();
                    encryptMap = new HashMap<>();
                    exclNestMap = new HashMap<>();
                    //解析表达式
                    parseExpression(isCollection, jsonBody, includeMap, excludeMap, encryptMap, exclNestMap);
                    includeMaps.put(getGenericMethodName(), includeMap);
                    excludeMaps.put(getGenericMethodName(), excludeMap);
                    encryptMaps.put(getGenericMethodName(), encryptMap);
                    exclNestMaps.put(getGenericMethodName(), exclNestMap);
                }

                //转换为自定义的Map对象，替换原来的对象返回
                if (ClassUtils.isPrimitiveOrWrapper(object.getClass())) {
                    throw new RuntimeException("返回值是基本类型的方法，请不要使用JsonBody注解！");
                } else if (isCollection) {
                    List result = parseList((Collection) object, 1, ROOT + LIST_FLAG, ROOT + LIST_FLAG, jsonBody, includeMap, excludeMap, encryptMap, exclNestMap);
                    return result;
                } else {
                    Object result = parseObject(object, 1, ROOT, ROOT, jsonBody, includeMap, excludeMap, encryptMap, exclNestMap);
                    return result;
                }
            } finally {
                clearThreadLocal();
            }
        }

        return object;
    }

    private static List parseList(Collection collection, int level, String parentPropSerial, String rootHeader, JsonBodyVo jsonBody,
                                  Map<Integer, Map<String, Set<String>>> includeMap,
                                  Map<Integer, Map<String, Set<String>>> excludeMap,
                                  Map<Integer, Map<String, Set<String>>> encryptMap,
                                  Map<String, Map<String, Set<String>>> exclNestMap) {
        List list = new ArrayList<>();

        if (jsonBody.getIgnoreLazy() && collection != null && collection instanceof AbstractPersistentCollection && !((AbstractPersistentCollection)collection).wasInitialized()) {
            return list;
        }

        Iterator iterator = collection.iterator();
        while (iterator.hasNext()) {
            Object object = iterator.next();
            if (needParse(object)) {
                Object subMap = parseObject(object, level, parentPropSerial, rootHeader, jsonBody, includeMap, excludeMap, encryptMap, exclNestMap);
                list.add(subMap);
            } else {
                list.add(object);
            }
        }
        return list;
    }

    private static Object parseObject(Object object, int level, String parentPropSerial, String rootHeader, JsonBodyVo jsonBody,
                                      Map<Integer, Map<String, Set<String>>> includeMap,
                                      Map<Integer, Map<String, Set<String>>> excludeMap,
                                      Map<Integer, Map<String, Set<String>>> encryptMap,
                                      Map<String, Map<String, Set<String>>> exclNestMap) {
        //userCustom=true时调用对象的toJSonObject方法
        if (jsonBody.getUseCustom() && hasToJsonObjectApi(object)) {
            return ((IJsonObject) object).toJSonObject();
        }

        //忽略懒加载
        if (jsonBody.getIgnoreLazy() && object != null  && !Hibernate.isInitialized(object)) {
            return null;
        }

        //需要包含的属性
        Set<String> includes = new HashSet<>();
        boolean includeAll = false;
        if (includeMap.containsKey(level) && includeMap.get(level).containsKey(parentPropSerial)) {
            includes = includeMap.get(level).get(parentPropSerial);
            if (includes.contains(ALL_CHAR)) {
                includeAll = true;
            }
        }
        //需要排除的属性
        Set<String> excludes = new HashSet<>();
        if (excludeMap.containsKey(level) && excludeMap.get(level).containsKey(parentPropSerial)) {
            excludes = excludeMap.get(level).get(parentPropSerial);
        }
        //需要加密的属性
        Set<String> encrypts = new HashSet<>();
        if (encryptMap.containsKey(level) && encryptMap.get(level).containsKey(parentPropSerial)) {
            encrypts = encryptMap.get(level).get(parentPropSerial);
        }
        //需要递归排除的属性
        for (String key : exclNestMap.keySet()) {
            if (parentPropSerial.startsWith(key)) {
                String parentPropName = parentPropSerial.contains(DOT_CHAR) ? parentPropSerial.substring(parentPropSerial.lastIndexOf(DOT_CHAR) + 1) : parentPropSerial;
                if (exclNestMap.containsKey(key)) {
                    if (exclNestMap.get(key).containsKey(ALL_CHAR)){
                        excludes.addAll(exclNestMap.get(key).get(ALL_CHAR));
                    }
                    if (exclNestMap.get(key).containsKey(parentPropName)){
                        excludes.addAll(exclNestMap.get(key).get(parentPropName));
                    }
                }
            }
        }

        //读取对象属性放到map中
        Map<String, Object> result = new HashMap<>();
        if (object instanceof Map) {
            Map<String, Object> originMap = (Map) object;
            for (Object propName : originMap.keySet().toArray()) {
                Object propVal = originMap.get(propName);
                boolean needParse = needParse(propVal);
                boolean hasSubPropSerial = hasSubPropSerial(level, parentPropSerial, propName, propVal, includeMap);
                if (canRead(level, includes, includeAll, excludes, (String) propName, needParse, hasSubPropSerial, parentPropSerial, rootHeader, jsonBody, result)) {
                    loadPropVal(level, parentPropSerial, rootHeader, jsonBody, includeMap, excludeMap, encryptMap, exclNestMap, result, (String) propName, propVal, needParse);
                }
            }
        } else {
            BeanWrapper wrapper = new BeanWrapperImpl(object);
            PropertyDescriptor[] propertys = wrapper.getPropertyDescriptors();
            for (int i = 0; i < propertys.length; i++) {
                JsonIgnore jsonIgnore = cn.soboys.simplest.core.utils.ClassUtil.findAnnotation(JsonIgnore.class, object,propertys[i]);
                String propName = propertys[i].getName();
                if (!wrapper.isReadableProperty(propName) || propertys[i].getReadMethod() == null || ignorePropertyNames.contains(propName) || jsonIgnore != null) {
                    continue;
                }
                Method readMethod = propertys[i].getReadMethod();
                try {
                    Object propVal = readMethod.invoke(object);
                    boolean needParse = needParse(propVal);
                    boolean hasSubPropSerial = hasSubPropSerial(level, parentPropSerial, propName, propVal, includeMap);
                    if (canRead(level, includes, includeAll, excludes, propName, needParse, hasSubPropSerial, parentPropSerial, rootHeader, jsonBody, result)) {
                        loadPropVal(level, parentPropSerial, rootHeader, jsonBody, includeMap, excludeMap, encryptMap, exclNestMap, result, propName, propVal, needParse);
                    }
                } catch (Exception e) {
                    if (isPrintStackTrace) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return result;
    }



    private static boolean canRead(int level, Set<String> includes, boolean includeAll, Set<String> excludes, String propName, boolean needParse, boolean hasSubPropSerial, String parentPropSerial, String rootHeader, JsonBodyVo jsonBody, Map<String, Object> result) {
        boolean isRead = false;
        if (jsonBody.getRoot().equals("") || parentPropSerial.startsWith(rootHeader + DOT_CHAR + jsonBody.getRoot())) {
            if (jsonBody.getStrategy().equals(JsonBody.Strategy.DEFAULT_NONE)) {
                if (includeAll || includes.contains(propName)) {
                    if (!excludes.contains(propName)) {
                        if (!needParse || hasSubPropSerial) {
                            isRead = true;
                        } else {
                            result.put(propName, null);
                        }
                    }
                }
            } else if (jsonBody.getStrategy().equals(JsonBody.Strategy.DEFAULT_SHOW)) {
                if (includeAll || includes.isEmpty() || includes.contains(propName)) {
                    if (!excludes.contains(propName)) {
                        isRead = checkLevel(level, propName, needParse, jsonBody, result);
                    }
                }
            }
        } else {
            isRead = checkLevel(level, propName, needParse, jsonBody, result);
        }

        return isRead;
    }

    private static boolean checkLevel(int level, String propName, boolean needParse, JsonBodyVo jsonBody, Map<String, Object> result) {
        boolean isRead = false;
        if (level < jsonBody.getMaxLevel()) {
            isRead = true;
        } else if (level == jsonBody.getMaxLevel() && !needParse) {
            isRead = true;
        } else {
            result.put(propName, null);
        }
        return isRead;
    }

    private static boolean hasSubPropSerial(int level, String parentPropSerial, Object propName, Object propVal, Map<Integer, Map<String, Set<String>>> includeMap) {
        String subPropSerial = (propVal instanceof Collection) ? parentPropSerial + DOT_CHAR + propName + LIST_FLAG : parentPropSerial + DOT_CHAR + propName;
        int subLevel = level + 1;
        if (includeMap.get(subLevel) != null && includeMap.get(subLevel).get(subPropSerial) != null) {
            return true;
        } else {
            return false;
        }
    }

    private static void loadPropVal(int level, String parentPropSerial, String rootHeader, JsonBodyVo jsonBody,
                                    Map<Integer, Map<String, Set<String>>> includeMap,
                                    Map<Integer, Map<String, Set<String>>> excludeMap,
                                    Map<Integer, Map<String, Set<String>>> encryptMap,
                                    Map<String, Map<String, Set<String>>> exclNestMap,
                                    Map<String, Object> result, String propName, Object propVal, boolean needParse) {

        if (needParse) {
            Object subObj = null;
            if (propVal != null) {
                if (propVal instanceof Collection) {
                    List list = parseList((Collection) propVal, level + 1, parentPropSerial + DOT_CHAR + propName + LIST_FLAG, rootHeader, jsonBody, includeMap, excludeMap, encryptMap, exclNestMap);
                    result.put(propName, list);
                } else {
                    subObj = parseObject(propVal, level + 1, parentPropSerial + DOT_CHAR + propName, rootHeader, jsonBody, includeMap, excludeMap, encryptMap, exclNestMap);
                    result.put(propName, subObj);
                }
            }
        } else {
            result.put(propName, propVal);
        }
    }


    private static void parseExpression(boolean isCollection, JsonBodyVo jsonBody,
                                        Map<Integer, Map<String, Set<String>>> includeMap,
                                        Map<Integer, Map<String, Set<String>>> excludeMap,
                                        Map<Integer, Map<String, Set<String>>> encryptMap,
                                        Map<String, Map<String, Set<String>>> exclNestMap) {
        //解析表达式,全表达式由多个属性表达式用逗号连接而成
        String expression = jsonBody.getExpression();
        String rootPath = isCollection ? ROOT + LIST_FLAG : ROOT;
        rootPath = jsonBody.getRoot().trim().equals("") ? rootPath : rootPath + DOT_CHAR + jsonBody.getRoot();
        int minLevel = rootPath.split("[.]").length;
        int maxLevel = 1;
        if (StringUtils.isNotBlank(expression)) {
            String[] prop_exps = expression.split(",");
            for (String prop_exp : prop_exps) {
                //获取父属性串,统一从root根开始
                String parentPropSerial = prop_exp.contains(DOT_CHAR) ? rootPath + DOT_CHAR + prop_exp.substring(0, prop_exp.lastIndexOf(DOT_CHAR)) : rootPath;

                //root下的属性是第一级，多一个点加一级
                Integer level = parentPropSerial.split("[.]").length;
                if (level > maxLevel) {
                    maxLevel = level;
                }
                Map<String, Set<String>> includeInnerMap = getInnerMap(includeMap, level);
                Map<String, Set<String>> excludeInnerMap = getInnerMap(excludeMap, level);
                Map<String, Set<String>> encryptInnerMap = getInnerMap(encryptMap, level);
                Map<String, Set<String>> exclNestInnerMap = getInnerMap(exclNestMap, parentPropSerial);

                Set<String> includePropSet = getInnerSet(includeInnerMap, parentPropSerial);
                Set<String> excludePropSet = getInnerSet(excludeInnerMap, parentPropSerial);
                Set<String> encryptPropSet = getInnerSet(encryptInnerMap, parentPropSerial);

                //获取属性，截取最后一个点之后字符串
                String propName = prop_exp.contains(DOT_CHAR) ? prop_exp.substring(prop_exp.lastIndexOf(DOT_CHAR) + 1) : prop_exp;
                //装载属性
                loadProps(propName, includePropSet, excludePropSet, encryptPropSet, exclNestInnerMap);
            }
        }

        if (jsonBody.getStrategy().equals(JsonBody.Strategy.DEFAULT_NONE)) {
            loadParentProps(maxLevel, includeMap);
        }

    }

    private static void loadParentProps(int level, Map<Integer, Map<String, Set<String>>> includeMap) {
        if (level > 1) {
            if (includeMap.containsKey(level)) {
                for (String propSerial : includeMap.get(level).keySet()) {
                    String parentPropSerial = propSerial.substring(0, propSerial.lastIndexOf(DOT_CHAR));
                    String propName = propSerial.substring(propSerial.lastIndexOf(DOT_CHAR) + 1);
                    Set<String> includePropSet = getInnerSet(getInnerMap(includeMap, level - 1), parentPropSerial);
                    checkAndAdd(includePropSet, propName);
                }
            }
            loadParentProps(level - 1, includeMap);
        }
    }

    private static void loadProps(String propName, Set<String> includePropSet, Set<String> excludePropSet, Set<String> encryptPropSet,
                                  Map<String, Set<String>> exclNestInnerMap) {
        if (propName.endsWith(")")) {
            propName = propName.replace(")", "");
            if (propName.startsWith(HIDE + "(")) {
                propName = propName.replace(HIDE + "(", "");
                String[] propNames = propName.split(SPLIT_CHAR);
                for (String _propName : propNames) {
                    int idx = _propName.indexOf(":");
                    if (idx == 0) {
                        Set<String> exclNestPropSet = getInnerSet(exclNestInnerMap, ALL_CHAR);
                        checkAndAdd(exclNestPropSet, _propName.substring(idx + 1));
                    } else if (idx > 0) {
                        String[] _propNames = _propName.split(NEST_SPLIT_CHAR);
                        Set<String> exclNestPropSet = getInnerSet(exclNestInnerMap, _propNames[0]);
                        checkAndAdd(exclNestPropSet, _propNames[1]);
                    } else {
                        checkAndAdd(excludePropSet, _propName);
                    }
                }
                //排除的默认隐含前提是Include All Prop
                checkAndAdd(includePropSet, ALL_CHAR);
            } else if (propName.startsWith(ENCRYPT + "(")) {
                propName = propName.replace(ENCRYPT + "(", "");
                String[] propNames = propName.split(SPLIT_CHAR);
                for (String _propName : propNames) {
                    checkAndAdd(encryptPropSet, _propName);
                }
            } else if (propName.startsWith("(")) {
                propName = propName.replace("(", "");
                String[] propNames = propName.split(SPLIT_CHAR);
                for (String _propName : propNames) {
                    checkAndAdd(includePropSet, _propName);
                }
            }
        } else {
            checkAndAdd(includePropSet, propName);
        }
    }

    private static void checkAndAdd(Set<String> propSet, String propName) {
        if (!propSet.contains(ALL_CHAR)) {
            if (ALL_CHAR.equals(propName)) {
                propSet.clear();
                propSet.add(ALL_CHAR);
            } else {
                propSet.add(propName.replace(LIST_FLAG, ""));
            }
        }
    }

    private static Set<String> getInnerSet(Map<String, Set<String>> innerMap, String key) {
        Set<String> set = innerMap.get(key);
        if (set == null) {
            set = new HashSet<>();
            innerMap.put(key, set);
        }
        return set;
    }

    private static <T> Map<String, Set<String>> getInnerMap(Map<T, Map<String, Set<String>>> map, T key) {
        Map<String, Set<String>> innerMap = map.get(key);
        if (innerMap == null) {
            innerMap = new HashMap<>();
            map.put(key, innerMap);
        }
        return innerMap;
    }

    private static boolean hasToJsonObjectApi(Object object) {

        try {
            Method jsonMethod = object.getClass().getDeclaredMethod("toJSonObject");
            if (jsonMethod != null && object instanceof IJsonObject) {
                return true;
            }
        } catch (NoSuchMethodException e) {
            if (isPrintStackTrace) {
                e.printStackTrace();
            }
        }

        return false;
    }

    private static boolean needParse(Object object) {
        return EntityUtil.needIn(object);
    }

    private static boolean isExpressionParsed() {
        if (true) {
            return false;
        }
        if (includeMaps.get(getGenericMethodName()) != null && excludeMaps.get(getGenericMethodName()) != null) {
            return true;
        } else {
            return false;
        }
    }

    private static JsonBodyVo getJsonBodyVo(JsonBody jsonBody) {
        JsonBodyVo jsonBodyVo = null;
        if (jsonBodyVo == null && jsonBody != null) {
            jsonBodyVo = new JsonBodyVo();
            jsonBodyVo.setRoot(jsonBody.root());
            jsonBodyVo.setUseCustom(jsonBody.useCustom());
            jsonBodyVo.setExpression(jsonBody.expression());
            jsonBodyVo.setMaxLevel(jsonBody.maxLevel());
            jsonBodyVo.setStrategy(jsonBody.strategy());
            jsonBodyVo.setIgnoreLazy(jsonBody.ignoreLazy());
        }
        return jsonBodyVo;
    }

}


