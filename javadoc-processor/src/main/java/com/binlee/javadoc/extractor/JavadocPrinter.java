package com.binlee.javadoc.extractor;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Filer;
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
import javax.tools.StandardLocation;

/**
 * Created on 2022/5/18
 *
 * @author binlee
 */
final class JavadocPrinter extends SimpleElementVisitor8<JavadocPrinter, Object> {

  private static boolean sProcessing = false;

  private final String mTag;
  private final Elements mUtils;
  private final JSONObject mJson;

  public static void print(ProcessingEnvironment env, Set<? extends Element> elements, String tag) {
    if (sProcessing) return;
    sProcessing = true;

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
      if (e.getKind() == ElementKind.ANNOTATION_TYPE || Util.shouldSkip(e, regexes)) {
        Log.w(tag, "visitType() skip element: " + e);
        continue;
      }
      Log.w("JavadocPrinter", "visit " + e + " ---> start --->");
      final JSONObject json = new JSONObject();
      new JavadocPrinter(tag, env.getElementUtils(), json)
        .visit(e)
        .format()
        .flush(env.getFiler());
    }
  }

  private JavadocPrinter(String tag, Elements utils, JSONObject json) {
    mTag = tag;
    mUtils = utils;
    mJson = json;
  }

  private JavadocPrinter format() {
    Util.formatFields(mJson, mTag);
    return this;
  }

  private void flush(Filer filer) {
    FileObject fileObject = null;
    try {
      fileObject = filer.createResource(StandardLocation.SOURCE_OUTPUT, "",
        // 类名中的泛型处理掉：com.quvideo.engine.layers.export._BaseExportManager<T,Q>
        mJson.get("class").toString().replaceFirst("<.*>", "") + ".json");
      final Writer writer = fileObject.openWriter();
      writer.write(mJson.toString());
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
      Log.w("JavadocPrinter", "visit " + mJson.get("class") + " ---> end ---> " + fileObject.toUri());
    }
  }

  @Override public JavadocPrinter visitType(TypeElement e, Object o) {
    // Log.w(mTag, "visitType() -> " + e + ", kind: " + e.getNestingKind());
    switch (e.getNestingKind()) {
      case TOP_LEVEL:
        break;
      case MEMBER:
        final Element enclosing = e.getEnclosingElement();
        // 解决递归无法退出最终导致栈溢出问题
        if (e.getEnclosingElement().equals(mJson.get("enclosing"))) break;

        JSONArray nested = mJson.getJSONArray("nested");
        if (nested == null) {
          mJson.put("nested", nested = new JSONArray());
        }
        JSONObject json = new JSONObject();
        json.put("enclosing", enclosing);
        nested.add(json);
        new JavadocPrinter(mTag, mUtils, json).visit(e).format();
        json.remove("enclosing");
        return this;
      default:
        return this;
    }
    // 类注释
    final CharSequence javadoc = Util.javadoc(mUtils, e, mTag);
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
    JSONArray interfaceList = new JSONArray();
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
      //   ENUM, 枚举
      //   CLASS, 内部类
      //   ANNOTATION_TYPE, 匿名内部类
      //   INTERFACE, 接口
      //   ENUM_CONSTANT, 枚举常量
      //   FIELD, 字段
      //   PARAMETER,
      //   LOCAL_VARIABLE,
      //   EXCEPTION_PARAMETER,
      //   METHOD, 方法
      //   CONSTRUCTOR, 构造器
      //   STATIC_INIT, 静态代码块
      //   INSTANCE_INIT, 构造快
      //   TYPE_PARAMETER,
      //   OTHER,
      //   RESOURCE_VARIABLE,
      //   MODULE;
      switch (element.getKind()) {
        case ENUM:
          // break;
        case CLASS:
          // break;
        case INTERFACE:
          // break;
        case ENUM_CONSTANT:
          visitUnknown(element, o);
          break;
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
    final CharSequence javadoc = Util.javadoc(mUtils, e, mTag);
    JSONArray fields = mJson.getJSONArray("fields");
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
    JSONArray methods = mJson.getJSONArray("methods");
    if (methods == null) mJson.put("methods", methods = new JSONArray());
    final JSONObject method = new JSONObject();
    final CharSequence javadoc = Util.javadoc(mUtils, e, mTag);
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
    JSONObject params = new JSONObject();
    buf.append('(');
    final List<? extends VariableElement> parameters = e.getParameters();
    for (int i = 0, size = parameters.size(); i < size; i++) {
      final VariableElement element = parameters.get(i);
      buf.append(element.asType()).append(' ').append(element.getSimpleName());
      if (i != size - 1) buf.append(", ");
      params.put(element.asType() + " " + element.getSimpleName(), "");
    }
    buf.append(')');
    method.put("params", params);
    // 异常表
    JSONArray throwsList = new JSONArray();
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

  @Override public JavadocPrinter visitUnknown(Element e, Object o) {
    // Log.e(mTag, "visitUnknown() " + e + " " + e.getClass());
    switch (e.getKind()) {
      case ENUM:
      case CLASS:
      case INTERFACE:
        visitType((TypeElement) e, o);
        break;
      case ENUM_CONSTANT:
        visitVariable((VariableElement) e, o);
        break;
      default:
        break;
    }
    return this;
  }
}
