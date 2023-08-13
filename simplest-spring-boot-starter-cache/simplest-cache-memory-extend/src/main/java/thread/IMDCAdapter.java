package thread;

import java.util.Map;

public interface IMDCAdapter {

    void put(String key, Object val);

    Object get(String key);

    void remove(String key);

    void clear();

    Map<String, Object> getCopyOfContextMap();

    void setContextMap(Map<String, Object> contextMap);

}
