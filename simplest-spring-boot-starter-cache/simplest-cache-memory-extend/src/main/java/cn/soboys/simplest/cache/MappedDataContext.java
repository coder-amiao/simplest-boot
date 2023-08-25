package cn.soboys.simplest.cache;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 */
public class MappedDataContext implements IMDCAdapter{

    final ThreadLocal<Map<String, Object>> copyOnThreadLocal = new ThreadLocal<Map<String, Object>>();

    private static final int WRITE_OPERATION = 1;
    private static final int MAP_COPY_OPERATION = 2;

    final ThreadLocal<Integer> lastOperation = new ThreadLocal<Integer>();

    private Integer getAndSetLastOperation(int op) {
        Integer lastOp = lastOperation.get();
        lastOperation.set(op);
        return lastOp;
    }

    private boolean wasLastOpReadOrNull(Integer lastOp) {
        return lastOp == null || lastOp.intValue() == MAP_COPY_OPERATION;
    }

    private Map<String, Object> duplicateAndInsertNewMap(Map<String, Object> oldMap) {
        Map<String, Object> newMap = Collections.synchronizedMap(new HashMap<String, Object>());
        if (oldMap != null) {
            // we don't want the parent thread modifying oldMap while we are
            // iterating over it
            synchronized (oldMap) {
                newMap.putAll(oldMap);
            }
        }

        copyOnThreadLocal.set(newMap);
        return newMap;
    }

    @Override
    public void put(String key, Object val) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("key cannot be null");
        }

        Map<String, Object> oldMap = copyOnThreadLocal.get();
        Integer lastOp = getAndSetLastOperation(WRITE_OPERATION);

        if (wasLastOpReadOrNull(lastOp) || oldMap == null) {
            Map<String, Object> newMap = duplicateAndInsertNewMap(oldMap);
            newMap.put(key, val);
        } else {
            oldMap.put(key, val);
        }
    }

    @Override
    public void remove(String key) {
        if (key == null) {
            return;
        }
        Map<String, Object> oldMap = copyOnThreadLocal.get();
        if (oldMap == null){
            return;
        }

        Integer lastOp = getAndSetLastOperation(WRITE_OPERATION);

        if (wasLastOpReadOrNull(lastOp)) {
            Map<String, Object> newMap = duplicateAndInsertNewMap(oldMap);
            newMap.remove(key);
        } else {
            oldMap.remove(key);
        }
    }

    @Override
    public void clear() {
        lastOperation.set(WRITE_OPERATION);
        copyOnThreadLocal.remove();
    }

    @Override
    public Object get(String key) {
        final Map<String, Object> map = copyOnThreadLocal.get();
        if ((map != null) && (key != null)) {
            return map.get(key);
        } else {
            return null;
        }
    }

    public Map<String, Object> getPropertyMap() {
        lastOperation.set(MAP_COPY_OPERATION);
        return copyOnThreadLocal.get();
    }

    public Set<String> getKeys() {
        Map<String, Object> map = getPropertyMap();

        if (map != null) {
            return map.keySet();
        } else {
            return null;
        }
    }

    @Override
    public Map<String, Object> getCopyOfContextMap() {
        Map<String, Object> hashMap = copyOnThreadLocal.get();
        if (hashMap == null) {
            return null;
        } else {
            return new HashMap<String, Object>(hashMap);
        }
    }

    @Override
    public void setContextMap(Map<String, Object> contextMap) {
        lastOperation.set(WRITE_OPERATION);

        Map<String, Object> newMap = Collections.synchronizedMap(new HashMap<String, Object>());
        newMap.putAll(contextMap);

        // the newMap replaces the old one for serialisation's sake
        copyOnThreadLocal.set(newMap);
    }
}

