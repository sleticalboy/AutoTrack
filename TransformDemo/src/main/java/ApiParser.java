import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2022/4/29
 *
 * @author binlee
 */
public final class ApiParser implements IParser {

  private final List<String> mList = new ArrayList<>();

  public static void main(String[] args) throws IOException {
    // final List<String> list = new ApiParser().parse(args[0]);
    // System.out.println("ApiParser.main() " + list);
    try {
      // 在 java 层通过这种方式可以找到类
      System.out.println("(\"[I\") = " + Class.forName("[I"));
      System.out.println("(\"[B\") = " + Class.forName("[B"));
      System.out.println("(\"[Ljava.lang.String;\") = " + Class.forName("[Ljava.lang.String;"));
      System.out.println(String[].class);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

  public List<String> parse(String path) throws IOException {
    // 解析文件，逐行读取
    final BufferedReader reader = new BufferedReader(new FileReader(path));
    String line;
    // 9#com/sleticalboy/transform/ToastUtils#toast(Landroid/content/Context;Ljava/lang/CharSequence;)V
    while ((line = reader.readLine()) != null && (line = line.trim()).length() != 0) {
      int index, start;
      // 访问修饰符
      index = line.indexOf('#');
      if (index != 1) continue;
      final int access = line.charAt(0) - '0';
      if ((access & (ACC_NATIVE | ACC_ABSTRACT)) != 0) continue;
      // 类名
      start = index + 1;
      index = line.indexOf('#', index + 1);
      final String cls = line.substring(start, index).replace('/', '.');
      // 方法名
      start = index + 1;
      index = line.indexOf('(', start);
      final String method = line.substring(start, index);
      final List<String> paramList = new ArrayList<>();
      // 方法参数列表：Landroid/content/Context;Ljava/lang/CharSequence;
      // void foo(char c, int i, short s, byte b, boolean bool, float f, double d, long l) {}
      // 10#com/sleticalboy/transform/ToastUtils#foo4()Ljava/lang/Void;
      // 10#com/sleticalboy/transform/ToastUtils#foo3(Ljava/lang/Void;)[I
      // 10#com/sleticalboy/transform/ToastUtils#foo2()[Ljava/util/List;
      // 10#com/sleticalboy/transform/ToastUtils#foo(CISBZFDJ[Ljava/lang/String;[I)Ljava/util/List;
      start = index + 1;
      index = line.indexOf(')', start);
      if (start != index) {
        final String params = line.substring(start, index);
        int prev = 0;
        boolean isArray;
        for (int i = 0; i < params.length(); i++) {
          isArray = false;
          final char c = params.charAt(i);
          switch (c) {
            case 'L':
              // 引用类型
              prev = i;
              break;
            case '[':
              // 数组类型
              isArray = true;
              break;
            case ';':
              String clazz = params.substring(prev + 1, i).replace('/', '.');
              paramList.add(clazz);
              break;
            default:
              // 基本数据类型
              Class<?> type = getPrimitive(params.charAt(0));
              paramList.add(type.getName());
              break;
          }
        }
      } else {
        // 无参函数
      }
      // 方法返回值类型
      final String returns = line.substring(index + 1);
      for (int i = 0; i < returns.length(); i++) {
        final char c = returns.charAt(i);
        switch (c) {
          case 'V':
            // void 类型
            break;
          case 'L':
            // 引用类型
            break;
          case '[':
            // 数组类型
            break;
          default:
            getPrimitive(c);
            break;
        }
      }
    }
    return mList;
  }

  private static Class<?> getPrimitive(char signature) {
    switch (signature) {
      case 'C':
        return char.class;
      case 'I':
        return int.class;
      case 'S':
        return short.class;
      case 'B':
        return byte.class;
      case 'Z':
        return boolean.class;
      case 'F':
        return float.class;
      case 'd':
        return double.class;
      case 'J':
        return long.class;
      default:
        throw new IllegalArgumentException("" + signature);
    }
  }

}
