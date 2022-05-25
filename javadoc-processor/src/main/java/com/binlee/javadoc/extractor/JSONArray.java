package com.binlee.javadoc.extractor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created on 2022/5/25
 *
 * @author binlee
 */
public final class JSONArray extends ArrayList<Object> {

  public JSONArray() {
  }

  public JSONArray(List<?> copyFrom) {
    if (copyFrom == null || copyFrom.size() == 0) return;
    addAll(copyFrom);
  }

  @Override public String toString() {
    // ['value1', 'value2']
    // [{}, {}, {}]
    // [[], [], []]
    Iterator<Object> it = this.iterator();
    if (!it.hasNext()) return "[]";

    StringBuilder buf = new StringBuilder();
    buf.append('[');

    while(true) {
      Object e = it.next();
      if (e instanceof List) {
        buf.append(new JSONArray((List<?>) e));
      } else if (e instanceof Map) {
        buf.append(new JSONObject(((Map<?, ?>) e)));
      } else {
        buf.append('"').append(e == null ? "" : e).append('"');
      }
      if (!it.hasNext()) {
        return buf.append(']').toString();
      }

      buf.append(',');
    }
  }
}
