package com.binlee.javadoc.extractor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.SimpleElementVisitor8;
import jdk.nashorn.internal.objects.NativeJSON;

/**
 * Created on 2022/5/18
 *
 * @author binlee
 */
final class JavadocPrinter extends SimpleElementVisitor8<JavadocPrinter, Object> {

  private final Elements mUtils;
  private final String mTag;
  private final Map<String, Object> mJson;

  public static void print(Set<? extends Element> elements, Elements utils, String tag) {
    Log.e("JavadocPrinter", "print() elements: " + elements);
    if (elements == null || elements.size() == 0) return;
    for (Element e : elements) {
      Log.i("JavadocPrinter", "visit " + e + " ---> start --->");
      Log.i("JavadocPrinter", "visit " + e + " ---> start --->");
      // class info:
      //{
      //  'super_class': 'java.lang.Object',
      //  'interfaces': [
      //    'java.util.concurrent.Callable<java.util.List<java.lang.String>>',
      //    'java.util.concurrent.Future<com.sleticalboy.transform.bean.Image>',
      //    'java.lang.Runnable',
      //  ]
      //  'constructors': [
      //    '', ...
      //  ]
      //  methods: [
      //    {
      //      '创建时被调用': '//'
      //      'name': 'onCreate',
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
      Map<String, Object> json = new HashMap<>();
      new JavadocPrinter(tag, utils, json).visit(e);
      Log.i("JavadocPrinter", "visit " + e + " ---> end --->");
      Log.i("JavadocPrinter", "visit " + e + " ---> end --->");
    }
  }

  private JavadocPrinter(String tag, Elements utils, Map<String, Object> json) {
    mTag = tag;
    mUtils = utils;
    mJson = json;
  }

  // 打印文档信息
  @Override protected JavadocPrinter defaultAction(Element e, Object o) {
    // Log.w(TAG, "defaultAction() -> " + e + ", kind: " + e.getKind());
    final String comment = mUtils.getDocComment(e);
    if (comment != null) {
      final StringTokenizer tokenizer = new StringTokenizer(comment, "\n\r");
      final StringBuilder buf = new StringBuilder();
      buf.append("/**\n");
      while (tokenizer.hasMoreTokens()) {
        buf.append(" *");
        buf.append(tokenizer.nextToken());
        buf.append('\n');
      }
      buf.append(" */");
      Log.i(mTag, buf.toString());
    }
    return this;
  }

  @Override public JavadocPrinter visitType(TypeElement e, Object o) {
    // Log.w(TAG, "visitType() -> " + e + ", kind: " + e.getKind());
    // 类注释
    defaultAction(e, o);
    final StringBuilder buf = new StringBuilder();
    // 类详细信息
    // 修饰符
    final Set<Modifier> modifiers = e.getModifiers();
    for (Modifier modifier : modifiers) {
      buf.append(modifier).append(' ');
    }
    buf.append(e.asType());
    // 父类及接口
    final TypeMirror superclass = e.getSuperclass();
    mJson.put("super_class", superclass.toString());
    buf.append(" extends ").append(superclass);
    final List<? extends TypeMirror> interfaces = e.getInterfaces();
    if (interfaces.size() != 0) {
      buf.append(" implements ");
      List<String> interfaceList = new ArrayList<>();
      for (int i = 0, size = interfaces.size(); i < size; i++) {
        buf.append(interfaces.get(i));
        if (i != size - 1) buf.append(", ");
      }
      mJson.put("interfaces", interfaceList);
    }
    buf.append(" {}");
    Log.w(mTag, buf.toString());
    // 类中的方法、内部类等信息
    final List<? extends Element> elements = e.getEnclosedElements();
    for (Element element : elements) {
      switch (element.getKind()) {
        // field/constructor
        case CONSTRUCTOR:
        case METHOD:
          visitExecutable((ExecutableElement) element, o);
        default:
          break;
      }
    }
    return this;
  }

  @Override public JavadocPrinter visitExecutable(ExecutableElement e, Object o) {
    defaultAction(e, o);
    // 打印方法详细信息
    final StringBuilder buf = new StringBuilder();
    // 修饰符
    final Set<Modifier> modifiers = e.getModifiers();
    for (Modifier modifier : modifiers) {
      buf.append(modifier).append(' ');
    }
    // 返回值
    if (e.getKind() == ElementKind.CONSTRUCTOR) {
      // 构造器要单独处理
      buf.append(e.getEnclosingElement().asType());
    } else {
      final TypeMirror returnType = e.getReturnType();
      buf.append(returnType.toString()).append(' ');
      buf.append(e.getSimpleName());
    }
    // 方法参数
    buf.append('(');
    final List<? extends VariableElement> parameters = e.getParameters();
    for (int i = 0, size = parameters.size(); i < size; i++) {
      final VariableElement element = parameters.get(i);
      buf.append(element.asType()).append(' ').append(element.getSimpleName());
      if (i != size - 1) buf.append(", ");
    }
    buf.append(')');
    // 异常表
    final List<? extends TypeMirror> types = e.getThrownTypes();
    if (types.size() != 0) {
      buf.append(" throws ");
      for (int i = 0, size = types.size(); i < size; i++) {
        final TypeMirror mirror = types.get(i);
        buf.append(mirror);
        if (i != size - 1) buf.append(", ");
      }
    }
    // 方法体
    buf.append(" {}");
    Log.w(mTag, buf.toString());
    // Log.w(TAG, "visitExecutable() -> " + e + ", kind: " + e.getKind());
    return this;
  }
}
