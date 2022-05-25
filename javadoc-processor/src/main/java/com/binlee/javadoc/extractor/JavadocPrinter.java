package com.binlee.javadoc.extractor;

import java.io.IOException;
import java.io.Writer;
import java.lang.management.BufferPoolMXBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.SimpleElementVisitor8;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;

/**
 * Created on 2022/5/18
 *
 * @author binlee
 */
final class JavadocPrinter extends SimpleElementVisitor8<JavadocPrinter, Object> {

  private final Elements mUtils;
  private final String mTag;
  private final Map<String, Object> mJson;

  public static void print(ProcessingEnvironment env, Set<? extends Element> elements, String tag) {
    final Map<String, String> options = env.getOptions();
    String[] regexes = {};
    if (options != null) {
      Log.setDebug(Boolean.parseBoolean(options.get("debug")));
      final String skipClassesRegex = options.get("skip_class_regex");
      if (skipClassesRegex != null) regexes = skipClassesRegex.split(",");
    }
    Log.e("JavadocPrinter", "print() elements: " + elements);
    if (elements == null || elements.size() == 0) return;
    for (Element e : elements) {
      if (e.getKind() == ElementKind.ANNOTATION_TYPE || shouldSkip(e, regexes)) {
        Log.w(tag, "visitType() skip element: " + e);
        continue;
      }
      Log.i("JavadocPrinter", "visit " + e + " ---> start --->");
      // class info:
      //{
      //  'class': 'com.sleticalboy.transform.ToastUtils',
      //  'super_class': 'java.lang.Object',
      //  'interfaces': [
      //    'java.util.concurrent.Callable<java.util.List<java.lang.String>>',
      //    'java.util.concurrent.Future<com.sleticalboy.transform.bean.Image>',
      //    'java.lang.Runnable',
      //  ],
      //  'fields': [
      //    {
      //      'comment': '节点的层级 id',
      //      'layerId': 'float'
      //    },
      //  ],
      //  'constructors': [
      //    '', ...
      //  ],
      //  'methods': [
      //    {
      //      '创建时被调用': '//'
      //      'name': 'onCreate',
      //      'returns': 'java.util.List<java.lang.String>[]',
      //      'params': [
      //        {
      //          '状态': '//',
      //          'savedInstanceState': 'android.os.Bundle',
      //        },
      //        {
      //          '超时时长': '//',
      //          'timeout': 'L'
      //        },
      //        {
      //          '时间单位': '//',
      //          'unit': 'java.util.concurrent.TimeUnit'
      //        }
      //      ],
      //      'throws': [
      //        'java.util.concurrent.ExecutionException',
      //        'java.lang.InterruptedException'
      //      ]
      //    }
      //  ]
      //}
      final JSONObject json = new JSONObject();
      new JavadocPrinter(tag, env.getElementUtils(), json).visit(e);
      final JSONArray fields = (JSONArray) json.get("fields");
      if (fields != null && fields.size() != 0) {
        final JSONObject formattedFields = new JSONObject();
        Object value;
        String name;
        for (int i = 0; i < fields.size(); i++) {
          value = "";
          name = null;
          final JSONObject field = (JSONObject) fields.get(i);
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
        Log.w(tag, "formatted fields: \n" + formattedFields);
        json.put("formatted_fields", formattedFields);
      }
      FileObject fileObject = null;
      try {
        fileObject = env.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, "",
          // 类名中的泛型处理掉：com.quvideo.engine.layers.export._BaseExportManager<T,Q>
          json.get("class").toString().replaceFirst("<.*>", "") + ".json");
        final Writer writer = fileObject.openWriter();
        writer.write(json.toString());
        writer.close();
      } catch (IOException ex) {
        if (fileObject != null) {
          try {
            fileObject.delete();
          } catch (Exception ignored) {
          }
        }
        ex.printStackTrace();
      }
      if (fileObject != null) {
        Log.w("JavadocPrinter", "visit " + e + " ---> end ---> " + fileObject.toUri());
      }
    }
  }

  private static boolean shouldSkip(Element e, String[] regexes) {
    final String s = e.asType().toString();
    for (String regex : regexes) {
      if (s.matches(regex)) return true;
    }
    return false;
  }

  private JavadocPrinter(String tag, Elements utils, Map<String, Object> json) {
    mTag = tag;
    mUtils = utils;
    mJson = json;
  }

  // 打印文档信息
  private CharSequence javadoc(Element e) {
    // Log.w(TAG, "defaultAction() -> " + e + ", kind: " + e.getKind());
    final String comment = mUtils.getDocComment(e);
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
      Log.i(mTag, buf.toString());
      return doc.toString();
    }
    return null;
  }

  @Override public JavadocPrinter visitType(TypeElement e, Object o) {
    // Log.w(TAG, "visitType() -> " + e + ", kind: " + e.getKind());
    // 类注释
    final CharSequence javadoc = javadoc(e);
    mJson.put("comment", javadoc);
    final StringBuilder buf = new StringBuilder();
    // 类详细信息
    // 修饰符
    final Set<Modifier> modifiers = e.getModifiers();
    for (Modifier modifier : modifiers) {
      buf.append(modifier).append(' ');
    }
    buf.append(e.asType());
    mJson.put("class", e.asType());
    // 父类及接口
    final TypeMirror superclass = e.getSuperclass();
    if (superclass.getKind() != TypeKind.NONE) {
      mJson.put("super_class", superclass.toString());
      buf.append(" extends ").append(superclass);
    } else {
      mJson.put("super_class", "");
    }
    // 接口
    List<String> interfaceList = new ArrayList<>();
    final List<? extends TypeMirror> interfaces = e.getInterfaces();
    if (interfaces.size() != 0) {
      buf.append(" implements ");
      for (int i = 0, size = interfaces.size(); i < size; i++) {
        buf.append(interfaces.get(i));
        if (i != size - 1) buf.append(", ");
        interfaceList.add(interfaces.get(i).toString());
      }
    }
    mJson.put("interfaces", interfaceList);
    buf.append(" {}");
    Log.w(mTag, buf.toString());
    // 类中的方法、内部类等信息
    final List<? extends Element> elements = e.getEnclosedElements();
    for (Element element : elements) {
      switch (element.getKind()) {
        // field
        case FIELD:
          visitVariable((VariableElement) element, o);
          break;
        // constructor/method
        case CONSTRUCTOR:
        case METHOD:
          visitExecutable((ExecutableElement) element, o);
        default:
          break;
      }
    }
    return this;
  }

  @Override public JavadocPrinter visitVariable(VariableElement e, Object o) {
    final CharSequence javadoc = javadoc(e);
    JSONArray fields = (JSONArray) mJson.get("fields");
    if (fields == null) mJson.put("fields", fields = new JSONArray());
    final JSONObject field = new JSONObject();
    fields.add(field);
    field.put("comment", javadoc);
    StringBuilder buf = new StringBuilder();
    // 修饰符
    final Set<Modifier> modifiers = e.getModifiers();
    boolean isConst = false;
    if (modifiers != null) {
      isConst = modifiers.contains(Modifier.STATIC) && modifiers.contains(Modifier.FINAL);
      for (Modifier modifier : modifiers) {
        buf.append(modifier).append(' ');
      }
    }
    field.put("modifiers", buf.toString());
    // 描述
    buf.append(e.asType()).append(' ').append(e.getSimpleName());
    if (isConst) buf.append(" = '").append(e.getConstantValue()).append('\'');
    buf.append(';');
    field.put(e.getSimpleName().toString(), e.asType());
    field.put("value", isConst ? e.getConstantValue() : null);
    Log.w(mTag, buf.toString());
    return this;
  }

  @Override public JavadocPrinter visitExecutable(ExecutableElement e, Object o) {
    JSONArray methods = (JSONArray) mJson.get("methods");
    if (methods == null) mJson.put("methods", methods = new JSONArray());
    final JSONObject method = new JSONObject();
    final CharSequence javadoc = javadoc(e);
    method.put("comment", javadoc);
    // 打印方法详细信息
    final StringBuilder buf = new StringBuilder();
    // 修饰符
    final Set<Modifier> modifiers = e.getModifiers();
    for (Modifier modifier : modifiers) {
      buf.append(modifier).append(' ');
    }
    method.put("modifiers", buf.toString());
    // 返回值
    if (e.getKind() == ElementKind.CONSTRUCTOR) {
      // 构造器要单独处理
      buf.append(e.getEnclosingElement().asType());
      method.put("name", "<init>");
    } else {
      final TypeMirror returnType = e.getReturnType();
      buf.append(returnType.toString()).append(' ');
      // 方法名
      buf.append(e.getSimpleName());
      method.put("returns", returnType.toString());
      method.put("name", e.getSimpleName());
    }
    // 方法参数
    // List<Map<String, Object>> paramList = new ArrayList<>();
    JSONObject params = new JSONObject();
    buf.append('(');
    final List<? extends VariableElement> parameters = e.getParameters();
    for (int i = 0, size = parameters.size(); i < size; i++) {
      // Map<String, Object> param = new LinkedHashMap<>();
      final VariableElement element = parameters.get(i);
      buf.append(element.asType()).append(' ').append(element.getSimpleName());
      if (i != size - 1) buf.append(", ");
      // param.put(element.getSimpleName().toString(), element.asType());
      // paramList.add(param);
      params.put(element.getSimpleName().toString(), element.asType());
    }
    buf.append(')');
    // method.put("params", paramList);
    method.put("params", params);
    // 异常表
    List<String> throwsList = new ArrayList<>();
    final List<? extends TypeMirror> types = e.getThrownTypes();
    if (types.size() != 0) {
      buf.append(" throws ");
      for (int i = 0, size = types.size(); i < size; i++) {
        buf.append(types.get(i));
        if (i != size - 1) buf.append(", ");
        throwsList.add(types.get(i).toString());
      }
    }
    method.put("throws", throwsList);
    // 方法体
    buf.append(" {}");
    Log.w(mTag, buf.toString());
    // Log.w(TAG, "visitExecutable() -> " + e + ", kind: " + e.getKind());
    methods.add(method);
    return this;
  }
}
