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

  // class info:
  //{
  //  'class': 'com.sleticalboy.transform.ToastUtils',
  //  'super_class': 'java.lang.Object',
  //  'interfaces': [
  //    'java.util.concurrent.Callable<java.util.List<java.lang.String>>',
  //    'java.util.concurrent.Future<com.sleticalboy.transform.bean.Image>',
  //    'java.lang.Runnable',
  //  ],
  //  'fields': {
  //    'comment': '节点的层级 id',
  //    'layerId': 'float'
  //  },
  //  'constructors': [
  //    '', ...
  //  ],
  //  'methods': [
  //    {
  //      'comment': '创建时被调用/'
  //      'name': 'onCreate',
  //      'returns': 'java.util.List<java.lang.String>[]',
  //      'params': {
  //        'android.os.Bundle savedInstanceState': '',
  //        'long timeout': 50,
  //        'java.util.concurrent.TimeUnit unit': '',
  //      },
  //      'throws': [
  //        'java.util.concurrent.ExecutionException',
  //        'java.lang.InterruptedException'
  //      ]
  //    }
  //  ],
  //  'simple_fields': {},
  //  'nested': [
  //    {},
  //    {}
  //  ],
  //}

  public JSONObject() {
  }

  public JSONObject(Map<?, ?> copyFrom) {
    if (copyFrom == null || copyFrom.size() == 0) return;
    for (Map.Entry<?, ?> entry : copyFrom.entrySet()) {
      put(String.valueOf(entry.getKey()), entry.getValue());
    }
  }

  public JSONObject getJSONObject(Object key) {
    return (JSONObject) super.get(key);
  }

  public JSONArray getJSONArray(Object key) {
    return (JSONArray) super.get(key);
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
