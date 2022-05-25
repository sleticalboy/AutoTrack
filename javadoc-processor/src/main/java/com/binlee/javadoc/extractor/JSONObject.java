package com.binlee.javadoc.extractor;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created on 2022/5/25
 *
 * @author binlee
 */
public final class JSONObject extends LinkedHashMap<String, Object> {

  public JSONObject() {
  }

  public JSONObject(Map<?, ?> copyFrom) {
    if (copyFrom == null || copyFrom.size() == 0) return;
    for (Map.Entry<?, ?> entry : copyFrom.entrySet()) {
      put(String.valueOf(entry.getKey()), entry.getValue());
    }
  }

  @Override public String toString() {
    // {'key': 'value', 'key2': 'value2'}
    // {'key': {}, 'key2': []}
    Iterator<Map.Entry<String, Object>> i = this.entrySet().iterator();
    if (!i.hasNext()) return "{}";

    final StringBuilder buf = new StringBuilder();
    buf.append('{');

    while (true) {
      Map.Entry<String, Object> e = (Map.Entry<String, Object>) i.next();
      // 'key': 'value',
      String key = e.getKey();
      Object value = e.getValue();
      buf.append('"').append(key).append('"').append(':');
      if (value instanceof List) {
        buf.append(new JSONArray(((List<?>) value)));
      } else if (value instanceof Map) {
        buf.append(new JSONObject(((Map<?, ?>) value)));
      } else {
        buf.append('"').append(value == null ? "" : value).append('"');
      }
      if (!i.hasNext()) {
        return buf.append('}').toString();
      }
      buf.append(',');
    }
  }
}
