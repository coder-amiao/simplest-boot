package cn.soboys.simplest.core.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;

@Slf4j
public class ClassUtil extends org.dromara.hutool.core.reflect.ClassUtil {

    public static Class<?> getRawType(Type type) {
        if (type instanceof Class) {
            return (Class) type;
        } else if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type rawType = parameterizedType.getRawType();
            return (Class) rawType;
        } else if (type instanceof GenericArrayType) {
            Type componentType = ((GenericArrayType) type).getGenericComponentType();
            return Array.newInstance(getRawType(componentType), 0).getClass();
        } else if (type instanceof TypeVariable) {
            return Object.class;
        } else if (type instanceof WildcardType) {
            return getRawType(((WildcardType) type).getUpperBounds()[0]);
        } else {
            String className = type == null ? "null" : type.getClass().getName();
            throw new IllegalArgumentException("Expected a Class, ParameterizedType, or GenericArrayType, but <" + type + "> is of type " + className);
        }
    }

    public static boolean isPrimitive(Object obj) {
        try {
            return ((Class) obj.getClass().getField("TYPE").get(null)).isPrimitive();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 读取一个类中属性或方法的标签，该方法首先从属性访问方法中查找标签定义，若找不到，则进一步通过对象的Field属性查找标签定义
     *
     * @param <A>
     * @param annotationClass 指定的标签类型
     * @param obj             目标对象
     * @param property        具体的属性
     * @return 如果找到指定的标签，则直接返回，若没找到，则返回null
     */
    public static <A extends Annotation> A findAnnotation(Class<A> annotationClass, Object obj, PropertyDescriptor property) {
        A ret = null;
        if (property.getWriteMethod() != null) {
            ret = property.getWriteMethod().getAnnotation(annotationClass);// 查找setter方法
        }
        if (ret == null && property.getReadMethod() != null) {
            ret = property.getReadMethod().getAnnotation(annotationClass);// 查找getter方法
        }
        if (ret == null) {
            try {// 查找属性定义
                Class clz = null;
                if (property.getWriteMethod() != null) {
                    clz = property.getWriteMethod().getDeclaringClass();
                } else if (property.getReadMethod() != null) {
                    clz = property.getReadMethod().getDeclaringClass();
                }
                if (clz != null) {
                    Field field = clz.getDeclaredField(property.getName());
                    ret = field.getAnnotation(annotationClass);
                }
            } catch (NoSuchFieldException e) {

            }
        }
        return ret;
    }

    public static <T> T newInstance(String classStr) {
        return newInstance(classStr, null, null);
    }

    public static <T> T newInstance(String classStr, Class<?>[] parameterTypes, Object[] initargs) {
        Class<?> clz = null;
        T instance = null;
        if(StringUtils.isNotBlank(classStr)) {
            try {
                clz = Class.forName(classStr);
                instance = (T) clz.getDeclaredConstructor(parameterTypes).newInstance(initargs);
            } catch (Exception e) {
                log.error("Get Instance from class name:" + classStr + " Instance exception:" + e);
            }
        }
        return instance;
    }

    public static <T> T newInstance(Class<T> clz) {
        return newInstance(clz, null, null);
    }

    public static <T> T newInstance(Class<T> clz, Class<?>[] parameterTypes, Object[] initargs) {
        T instance = null;
        try {
            instance = (T) clz.getDeclaredConstructor(parameterTypes).newInstance(initargs);
        } catch (Exception e) {
            log.error("Get Instance from class name:" + clz.getCanonicalName() + " Instance exception:" + e);
        }
        return instance;
    }

    public static Object getFieldValue(Object obj, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field field = getField(obj.getClass(), fieldName);
        field.setAccessible(true);//覆盖私有属性的访问控制权限
        Object res = field.get(obj);
        return res;
    }

    public static void setFieldValue(Object obj, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field = getField(obj.getClass(), fieldName);//获取属性
        field.setAccessible(true);//覆盖私有属性的访问控制权限
        field.set(obj, value);
    }

    /**
     * 利用递归找一个类的指定属性，如果找不到，去父亲里面找直到最上层Object对象为止。
     */
    public static Field getField(Class clazz, String fieldName) {
        Field field = null;
        try {
            field = clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            try {
                field = clazz.getField(fieldName);
            } catch (NoSuchFieldException ex) {
                if (clazz.getSuperclass() == null) {
                    return field;
                } else {
                    field = getField(clazz.getSuperclass(), fieldName);
                }
            }
        }
        return field;
    }

    /**
     * 利用递归找一个类的指定方法，如果找不到，去父亲里面找直到最上层Object对象为止。
     *
     * @param clazz 目标类
     * @param methodName 方法名
     * @param classes 方法参数类型数组
     * @return 方法对象
     * @throws Exception
     */
    public static Method getMethod(Class clazz, String methodName,
                                   final Class[] classes) throws Exception {
        Method method = null;
        try {
            method = clazz.getDeclaredMethod(methodName, classes);
        } catch (NoSuchMethodException e) {
            try {
                method = clazz.getMethod(methodName, classes);
            } catch (NoSuchMethodException ex) {
                if (clazz.getSuperclass() == null) {
                    return method;
                } else {
                    method = getMethod(clazz.getSuperclass(), methodName,
                            classes);
                }
            }
        }
        return method;
    }

    /**
     * @param obj 调整方法的对象
     * @param methodName 方法名
     * @param classes 参数类型数组
     * @param objects 参数数组
     * @return 方法的返回值
     */
    public static Object invoke(final Object obj, final String methodName,
                                final Class[] classes, final Object[] objects) {
        try {
            Method method = getMethod(obj.getClass(), methodName, classes);
            method.setAccessible(true);// 调用private方法的关键一句话
            return method.invoke(obj, objects);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
