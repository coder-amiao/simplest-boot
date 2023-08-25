package cn.soboys.simplest.cache;

import java.io.Closeable;
import java.util.Map;

/**
 * 线程数据通用缓存类
 * ThreadCache存储的数据会自动将数据透传至@Ansyc注解开启的子线程
 *
 * @see org.slf4j.MDC
 */
public class ThreadCache {

    static MappedDataContext mdcAdapter = new MappedDataContext();

    /**
     * An adapter to remove the key when done.
     */
    public static class MDCCloseable implements Closeable {
        private final String key;

        private MDCCloseable(String key) {
            this.key = key;
        }

        @Override
        public void close() {
            ThreadCache.remove(this.key);
        }
    }

    private ThreadCache() {
    }

    public static void put(String key, Object val) {
        if (key == null) {
            throw new IllegalArgumentException("key parameter cannot be null");
        }
        mdcAdapter.put(key, val);
    }

    public static MDCCloseable putCloseable(String key, Object val) {
        put(key, val);
        return new MDCCloseable(key);
    }

    public static <T> T get(String key, Class<T> returnClass, T defaultValue) {
        T t = (T) get(key);
        if (t == null) {
            t = defaultValue;
            put(key, t);
        }
        return t;
    }

    public static <T> T get(String key, Class<T> returnClass) {
        return (T) get(key);
    }

    public static Object get(String key) {
        if (key == null) {
            throw new IllegalArgumentException("key parameter cannot be null");
        }
        return mdcAdapter.get(key);
    }

    public static void remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException("key parameter cannot be null");
        }
        mdcAdapter.remove(key);
    }

    public static void clear() {
        mdcAdapter.clear();
    }

    public static Map<String, Object> getCopyOfContextMap() {
        return mdcAdapter.getCopyOfContextMap();
    }

    public static void setContextMap(Map<String, Object> contextMap) {
        if (contextMap != null) {
            mdcAdapter.setContextMap(contextMap);
        }
    }

    public static MappedDataContext getMDCAdapter() {
        return mdcAdapter;
    }

}