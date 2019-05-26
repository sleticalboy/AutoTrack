package com.sleticalboy.plugin.transform

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.AdviceAdapter

import java.util.regex.Pattern

/**
 * Created on 19-5-15.
 *
 * @author leebin
 */
class TrackClassVisitor extends ClassVisitor implements Opcodes {

    ///////////////// sdk api
    private static final String SDK_CLASS = "com/sleticalboy/autotrack/sdk/TrackSdk"
    private static final String SDK_METHOD = "autoTrack"

    ///////////////// android.view.View.OnClickListener
    // void onClick(View view)
    private static final String ON_CLICK_VIEW = 'android/view/View$OnClickListener'
    private static final String ON_CLICK_VIEW_DESC = 'onClick(Landroid/view/View;)V'
    // lambda$onCreate$0(Landroid/view/View;)V
    // 为了对付 lambda 表达式, kotlin 和 java 通用
    private static final Pattern ON_CLICK_VIEW_LAMBDA = Pattern.compile(
            'lambda\\$*..*\\$\\d+\\(Landroid/view/View;\\)V')
    ///////////////// android.view.View.OnClickListener

    ///////////////// android.widget.CompoundButton.OnCheckedChangeListener
    // void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    private static final String ON_CHECKED_CHANGE = 'android/widget/CompoundButton$OnCheckedChangeListener'
    private static final String ON_CHECKED_CHANGE_DESC = 'onCheckedChanged(Landroid/widget/CompoundButton;Z)V'
    private static final Pattern ON_CHECKED_CHANGE_LAMBDA = Pattern.compile(
            'lambda\\$*..*\\$\\d+\\(Landroid/widget/CompoundButton;Z\\)V')
    ///////////////// android.widget.CompoundButton.OnCheckedChangeListener

    /////////////// android.content.DialogInterface.OnClickListener
    // void onClick(DialogInterface dialog, int which)
    private static final String ON_CLICK_DIA = 'android/content/DialogInterface$OnClickListener'
    private static final String ON_CLICK_DIA_DESC = 'onClick(Landroid/content/DialogInterface;I)V'
    private static final Pattern ON_CLICK_DIA_LAMBDA = Pattern.compile(
            'lambda\\$*..*\\$\\d+\\(Landroid/content/DialogInterface;I\\)V')
    /////////////// android.content.DialogInterface.OnClickListener

    /////////////// android.content.DialogInterface.OnMultiChoiceClickListener
    // void onClick(DialogInterface dialog, int which, boolean isChecked)
    private static final String ON_CLICK_DIA_MULTI = 'android/content/DialogInterface$OnMultiChoiceClickListener'
    private static final String ON_CLICK_DIA_MULTI_DESC = 'onClick(Landroid/content/DialogInterface;IZ)V'
    private static final Pattern ON_CLICK_DIA_MULTI_LAMBDA = Pattern.compile(
            'lambda\\$*..*\\$\\d+\\(Landroid/content/DialogInterface;IZ\\)V')
    /////////////// android.content.DialogInterface.OnMultiChoiceClickListener

    ///////////////// android.widget.AdapterView.OnItemClickListener
    // void onItemClick(AdapterView<?> parent, View view, int position, long id)
    private static final String ON_ITEM_CLICK = 'android/widget/AdapterView$OnItemClickListener'
    private static final String ON_ITEM_CLICK_DESC = 'onItemClick(Landroid/widget/AdapterView;Landroid/view/View;IJ)V'
    private static final Pattern ON_ITEM_CLICK_LAMBDA = Pattern.compile(
            'lambda\\$*..*\\$\\d+\\(Landroid/widget/AdapterView;Landroid/view/View;IJ\\)V')
    ///////////////// android.widget.AdapterView.OnItemClickListener

    ///////////////// lambda$dynamicAddView$1(Landroid/widget/Button;Landroid/view/View;)V
    ///////////////// lambda\\$*..*\\$\\d+\\(L*..*;Landroid/view/View;\\)V
    // 动态添加的 View 使用 lambda 表达式设置 OnClickListener
    private static final Pattern DYNAMIC_CLICK_LAMBDA = Pattern.compile(
            'lambda\\$*..*\\$\\d+\\(L*..*;Landroid/view/View;\\)V')

    private String[] interfaces
    private String clsName

    TrackClassVisitor(ClassVisitor cv) {
        // api must be Opcodes.ASM5 or it'll throw IllegalArgumentException.
        // 这里不能使用静态导入，否则会报错
        super(Opcodes.ASM5, cv)
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
    void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.interfaces = interfaces
        this.clsName = name
        super.visit(version, access, name, signature, superName, interfaces)
    }

    @Override
    MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        final MethodVisitor visitor = super.visitMethod(access, name, desc, signature, exceptions)
        final String methodDesc = name + desc
        return new AdviceAdapter(Opcodes.ASM5, visitor, access, name, desc) {

            @Override
            protected void onMethodExit(int opcode) {
                if (methodDesc == 'onContextItemSelected(Landroid/view/MenuItem;)Z'
                        || methodDesc == 'onOptionsItemSelected(Landroid/view/MenuItem;)Z') {
                    TrackUtils.log('TrackClassVisitor cls = ' + clsName + ' method = ' + methodDesc)
                    // Activity.onContextItemSelected(MenuItem item)
                    // Activity.onOptionsItemSelected(MenuItem item)
                    // 0 表示 `调用者` // 1/2/3... 表示当前方法 `参数` 的索引 (从 1 开始)
                    // 引用数据类型用 ALOAD 基本数据类型用其他(比如 int 用 ILOAD, float 用 FLOAD)
                    visitor.visitVarInsn(ALOAD, 0) /*this Context*/
                    visitor.visitVarInsn(ALOAD, 1) /*item MenuItem*/
                    visitor.visitMethodInsn(INVOKESTATIC, SDK_CLASS, SDK_METHOD,
                            '(Ljava/lang/Object;Landroid/view/MenuItem;)V', false)
                } else if (hasInterface(ON_CLICK_VIEW) && ON_CLICK_VIEW_DESC == methodDesc
                        || ON_CLICK_VIEW_LAMBDA.matcher(methodDesc)
                        || DYNAMIC_CLICK_LAMBDA.matcher(methodDesc)) {
                    TrackUtils.log('TrackClassVisitor cls = ' + clsName + ' method = ' + methodDesc)
                    // View.OnClickListener
                    // onClick(View view)
                    visitor.visitVarInsn(ALOAD, 1) /*view View*/
                    visitor.visitMethodInsn(INVOKESTATIC, SDK_CLASS, SDK_METHOD,
                            '(Ljava/lang/Object;)V', false)
                } else if (hasInterface(ON_CHECKED_CHANGE) && ON_CHECKED_CHANGE_DESC == methodDesc
                        || ON_CHECKED_CHANGE_LAMBDA.matcher(methodDesc)) {
                    TrackUtils.log('TrackClassVisitor cls = ' + clsName + ' method = ' + methodDesc)
                    // CompoundButton.OnCheckedChangedListener
                    // onCheckedChanged(CompoundButton button, boolean isChecked)
                    visitor.visitVarInsn(ALOAD, 1) /*button CompoundButton*/
                    visitor.visitMethodInsn(INVOKESTATIC, SDK_CLASS, SDK_METHOD,
                            '(Ljava/lang/Object;)V', false)
                } else if (hasInterface(ON_CLICK_DIA) && ON_CLICK_DIA_DESC == methodDesc
                        || ON_CLICK_DIA_LAMBDA.matcher(methodDesc)) {
                    // android.content.DialogInterface.OnClickListener
                    // void onClick(DialogInterface dialog, int which)
                    visitor.visitVarInsn(ALOAD, 1) /*dialog DialogInterface*/
                    visitor.visitVarInsn(ILOAD, 2) /*which int*/
                    visitor.visitMethodInsn(INVOKESTATIC, SDK_CLASS, SDK_METHOD,
                            '(Landroid/content/DialogInterface;I)V', false)
                } else if (hasInterface(ON_CLICK_DIA_MULTI) && ON_CLICK_DIA_MULTI_DESC == methodDesc
                        || ON_CLICK_DIA_MULTI_LAMBDA.matcher(methodDesc)) {
                    // android.content.DialogInterface.OnMultiChoiceClickListener
                    // void onClick(DialogInterface dialog, int which, boolean isChecked)
                    visitor.visitVarInsn(ALOAD, 1) /*dialog DialogInterface*/
                    visitor.visitVarInsn(ILOAD, 2) /*which int*/
                    visitor.visitVarInsn(ILOAD, 3) /*isChecked boolean*/
                    visitor.visitMethodInsn(INVOKESTATIC, SDK_CLASS, SDK_METHOD,
                            '(Landroid/content/DialogInterface;IZ)V', false)
                } else if (hasInterface(ON_ITEM_CLICK) && ON_ITEM_CLICK_DESC == methodDesc
                        || ON_ITEM_CLICK_LAMBDA.matcher(methodDesc)) {
                    TrackUtils.log('TrackClassVisitor cls = ' + clsName + ' method = ' + methodDesc)
                    // android.widget.AdapterView.OnItemClickListener
                    // void onItemClick(AdapterView<?> parent, View view, int position, long id)
                    if (ON_ITEM_CLICK_LAMBDA.matcher(methodDesc)) {
                        // AdapterView 通过 lambda 表达式设置 OnItemClickListener
                        visitor.visitVarInsn(ALOAD, 0) /*this AdapterView*/
                        visitor.visitVarInsn(ALOAD, 1) /*view View*/
                        visitor.visitVarInsn(ILOAD, 2) /*position int*/
                    } else {
                        visitor.visitVarInsn(ALOAD, 1) /*parent AdapterView*/
                        visitor.visitVarInsn(ALOAD, 2) /*view View*/
                        visitor.visitVarInsn(ILOAD, 3) /*position int*/
                    }
                    visitor.visitMethodInsn(INVOKESTATIC, SDK_CLASS, SDK_METHOD,
                            '(Landroid/widget/AdapterView;Landroid/view/View;I)V', false)
                } else {
                    // empty implementation
                }
            }
        }
    }

    private boolean hasInterface(String listener) {
        if (interfaces == null || interfaces.length == 0) {
            return false
        }
        for (String inter : interfaces) {
            if (listener == inter) {
                return true
            }
        }
        return false
    }
}
