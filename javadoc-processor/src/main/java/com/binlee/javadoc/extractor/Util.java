package com.binlee.javadoc.extractor;

import java.util.StringTokenizer;
import javax.lang.model.element.Element;
import javax.lang.model.util.Elements;

/**
 * Created on 2022-05-27.
 *
 * @author binlee
 */
public final class Util {

  private Util() {
    //no instance
  }

  static boolean shouldSkip(Element e, String[] regexes) {
    final String s = e.asType().toString();
    for (String regex : regexes) {
      if (s.matches(regex)) return true;
    }
    return false;
  }

  static void formatFields(JSONObject json, String tag) {
    final JSONArray fields = json.getJSONArray("fields");
    if (fields != null && fields.size() != 0) {
      final JSONObject formattedFields = new JSONObject();
      Object value;
      String name;
      for (int i = 0; i < fields.size(); i++) {
        value = "";
        name = null;
        final JSONObject field = fields.getJSONObject(i);
        for (String key : field.keySet()) {
          if ("comment".equals(key) || "modifiers".equals(key)) continue;
          if ("value".equals(key)) {
            value = field.get(key);
          } else {
            name = key;
          }
        }
        if (name != null) formattedFields.put(name, value);
      }
      Log.w(tag, "simple fields: \n" + formattedFields);
      json.put("simple_fields", formattedFields);
    }
  }

  /**
   * 打印文档信息
   *
   * @param utils 工具
   * @param e {@link Element} 元素
   * @param tag 日志标签
   * @return {@link CharSequence}
   */
  static CharSequence javadoc(Elements utils, Element e, String tag) {
    // Log.w(TAG, "defaultAction() -> " + e + ", kind: " + e.getKind());
    final String comment = utils.getDocComment(e);
    if (comment != null) {
      final StringTokenizer tokenizer = new StringTokenizer(comment, "\n\r");
      final StringBuilder buf = new StringBuilder();
      final StringBuilder doc = new StringBuilder();
      buf.append("\n/**\n");
      while (tokenizer.hasMoreTokens()) {
        final String token = tokenizer.nextToken();
        buf.append(" *").append(token).append('\n');
        doc.append(token.trim()).append('/');
      }
      buf.append(" */");
      Log.i(tag, buf.toString());
      return doc.toString();
    }
    return null;
  }
}
