package com.binlee.plugin.extractor

import org.objectweb.asm.*

/**
 * Created on 19-5-15.
 *
 * @author leebin
 */
class MethodExtractClassVisitor extends ClassVisitor {

  private static final int ASM_VERSION = Opcodes.ASM6

  private String[] interfaces
  private String clsName

  MethodExtractClassVisitor(ClassVisitor cv) {
    // api must be Opcodes.ASM6 or it'll throw IllegalArgumentException.
    super(ASM_VERSION, cv)
  }

  @Override
  AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
    // Utils.log("visitAnnotation() [class] " + descriptor + ", visible: " + visible)
    return super.visitAnnotation(descriptor, visible)
  }

  @Override
  AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor,
    boolean visible) {
    // Utils.log("visitTypeAnnotation() " + descriptor + ", typePath: " + typePath)
    return super.visitTypeAnnotation(typeRef, typePath, descriptor, visible)
  }

  /**
   * 访问 .class 的头文件 使用 `javap -v xxx.class` 可以查看类文件的详细信息
   *
   * @param version jdk 版本(不是常说的jdk6/7/8, 使用 `javap -v xxx.class` 可以查看)
   * @param access 类修饰符public/private/protect or default
   * @param name 类签名 Ljava/lang/String;
   * @param signature 泛型
   * @param superName 父类名，当是 `Object` 时为 null
   * @param interfaces 类所实现所有的接口
   */
  @Override
  void visit(int version, int access, String name, String signature, String superName,
    String[] interfaces) {
    this.interfaces = interfaces
    this.clsName = name
    super.visit(version, access, name, signature, superName, interfaces)
  }

  @Override
  void visitEnd() {
    super.visitEnd()
    MethodRecorder.get().flush()
  }

  @Override
  MethodVisitor visitMethod(int access, String name, String desc, String signature,
    String[] exceptions) {
    // Utils.log("visitMethod() $clsName#$name$desc")
    MethodRecorder.get().record(access + "#" + clsName + "#" + name + desc)
    return super.visitMethod(access, name, desc, signature, exceptions)
  }
}
