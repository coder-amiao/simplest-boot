package cn.soboys.simplest.core.utils;

import cn.soboys.simplest.core.domain.BaseObj;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
public class EntityUtil {

    public static Set<String> ignorePropertyNames = new HashSet<>();

    static {
        ignorePropertyNames.add("class");
        ignorePropertyNames.add("handler");
        ignorePropertyNames.add("hibernateLazyInitializer");
        ignorePropertyNames.add("fieldHandler");
    }

    private static Integer IGNORE_NULL = 1;
    private static Integer IGNORE_EMPTY = 2;


    /**
     * 是否需要进入该对象中，进行下一步遍历操作，当该对象继承BaseObj或者是Collection，Map，IPageList，返回ture
     *
     * @param object
     * @return
     */
    public static boolean needIn(Object object) {
        if (object != null) {
            Class clz = object.getClass();
            return
                    BaseObj.class.isAssignableFrom(clz) ||
                    Collection.class.isAssignableFrom(clz) ||
                    Map.class.isAssignableFrom(clz)

        }
        return false;
    }
}
