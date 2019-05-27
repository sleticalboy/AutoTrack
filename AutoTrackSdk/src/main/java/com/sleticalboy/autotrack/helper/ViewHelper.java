package com.sleticalboy.autotrack.helper;

import android.app.Activity;
import android.content.res.Resources;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.sleticalboy.autotrack.data.ViewNode;
import com.sleticalboy.autotrack.data.ViewStack;

/**
 * Created on 19-5-11.
 *
 * @author leebin
 */
public final class ViewHelper {

    private static final char SPACE = ' ';

    private ViewHelper() {
        throw new AssertionError("Utility class can not be initialized");
    }

    public static String findViewPath(@NonNull View view) {
        final long start = SystemClock.uptimeMillis();
        final StringBuilder sb = new StringBuilder();
        final Activity page = ActivityHelper.findActivity(view);
        if (page != null) {
            sb.append(page.getClass().getSimpleName());
        }
        // 逆向遍历 View tree 直到 DecorView 得到一条完整路径
        // 用栈存储节点信息，栈底是当前 View，栈顶是 DecorView，出栈顺序即为完整路径
        // 用一个简单的单链表来实现栈结构
        // ViewNode: cls declaredId index next
        // VewStack: head
        // DecorView->FrameLayout->ListView$3$lv_movie->TextView$5$tv_title
        final ViewStack viewStack = new ViewStack();
        collectViews(view, viewStack, WindowHelper.findDecorView(view));
        sb.append("$$").append(viewStack.toString()).append("$$");
        sb.append("search_time: ").append(SystemClock.uptimeMillis() - start).append(" ms");
        // HomeActivity$$FrameLayout$0->RecycleView$2$rv_movies->ConstraintLayout$20$movie_item_constraint->
        // ImageView$0$move_icon$$search_time: xx ms
        // 分析数据时，先用 $$ 切割，再用 -> 切割

        // MainActivity$$DecorView->LinearLayout$0->FrameLayout$1->FitWindowsLinearLayout$0$app:id/action_bar_root->
        // ContentFrameLayout$1$android:id/content->CoordinatorLayout$0->FrameLayout$1$app:id/flContainer->
        // RecyclerView$0->LinearLayout$0 $$ search_time: 7 ms

        // 实际结果
        // [DecorView->LinearLayout$0->FrameLayout$1->FitWindowsLinearLayout$0$app:id/action_bar_root->
        // ContentFrameLayout$1$android:id/content->CoordinatorLayout$0->FloatingActionButton$2$app:id/fab]
        return sb.toString();
    }

    private static void collectViews(View target, ViewStack stack, View decorView) {
        stack.push(createNode(target));
        if (target == decorView) {
            return;
        }
        if (target.getParent() instanceof ViewGroup) {
            // 递归查找
            collectViews((View) target.getParent(), stack, decorView);
        }
    }

    private static ViewNode createNode(View view) {
        int index = View.NO_ID;
        if (view.getParent() instanceof ViewGroup) {
            // view 在 parent 中的索引
            index = ((ViewGroup) view.getParent()).indexOfChild(view);
        }
        return new ViewNode(view.getClass().getSimpleName(), index, getDeclaredId(view));
    }

    public static String findViewId(View view) {
        final int id = view.getId();
        final StringBuilder out = new StringBuilder();
        if (id != View.NO_ID) {
            final Resources r = view.getResources();
            if (id > 0 && /*Resources.resourceHasPackage(id) &&*/ r != null) {
                try {
                    final String pkgName;
                    switch (id & 0xff000000) {
                        case 0x7f000000:
                            pkgName = "app";
                            break;
                        case 0x01000000:
                            pkgName = "android";
                            break;
                        default:
                            pkgName = r.getResourcePackageName(id);
                            break;
                    }
                    out.append(" ").append(pkgName);
                    out.append(":").append(r.getResourceTypeName(id));
                    out.append("/").append(r.getResourceEntryName(id));
                } catch (Resources.NotFoundException ignored) {
                }
            }
        }
        return null;
    }

    /**
     * 获取 View 在 xml 文件中声明的 id
     *
     * @param target 目标 View
     * @return namespace:id/name
     * @see View#toString()
     */
    public static String getDeclaredId(@NonNull View target) {
        // {2d821303 VFED..CL ......I. 0,0-0,0 #7f090552 app:id/message_text}
        if (target.getId() != View.NO_ID && !isViewIdGenerated(target.getId())) {
            final String s = target.toString();
            final int index;
            if ((index = s.lastIndexOf(SPACE)) != -1) {
                return s.substring(index + 1, s.length() - 1);
            }
        }
        return null;
    }

    /**
     * copy form View#isViewIdGenerated()
     */
    private static boolean isViewIdGenerated(int id) {
        return (id & 0xFF000000) == 0 && (id & 0x00FFFFFF) != 0;
    }

    public static CharSequence getWidgetDesc(@NonNull View target) {
        final String prefix = target.getClass().getSimpleName();
        CharSequence desc = "";
        if (target instanceof CompoundButton) {
            desc = ((CompoundButton) target).getText() + ", isChecked: " + ((CompoundButton) target).isChecked();
        } else if (target instanceof TextView) {
            desc = ((TextView) target).getText();
        } else if (target instanceof ImageView) {
            desc = target.getContentDescription();
        }
        return prefix + " " + desc;
    }
}
