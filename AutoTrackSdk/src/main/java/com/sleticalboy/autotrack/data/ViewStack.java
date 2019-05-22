package com.sleticalboy.autotrack.data;

import androidx.annotation.NonNull;

/**
 * Created on 19-5-11.
 *
 * @author leebin
 */
public final class ViewStack {

    private final Object mLock = new Object();
    /**
     * 指向栈顶元素
     */
    private ViewNode head;
    private int depth;

    public boolean push(ViewNode node) {
        if (node == null) {
            return false;
        }
        if (head == null) {
            head = node;
            head.next = null;
            increase();
            return true;
        }
        int size = depth;
        ViewNode oldHead = head;
        head = node;
        head.next = oldHead;
        increase();
        return depth != size;
    }

    public ViewNode pop() {
        if (head == null) {
            depth = 0;
            return null;
        }
        ViewNode oldHead = head;
        head = oldHead.next;
        oldHead.next = null;
        decrease();
        return oldHead;
    }

    public boolean clear() {
        ViewNode node;
        while ((node = pop()) != null) {
            node.next = null;
        }
        head = null;
        return pop() == null;
    }

    private void increase() {
        synchronized (mLock) {
            depth++;
        }
    }

    private void decrease() {
        synchronized (mLock) {
            depth--;
        }
    }

    public int depth() {
        return depth;
    }

    public boolean isEmpty() {
        return depth() == 0;
    }

    @NonNull
    @Override
    public String toString() {
        return toFormatString(this);
    }

    private static String toFormatString(ViewStack stack) {
        // DecorView$0$android.internal.R.id.content
        // FrameLayout$1$fl_content
        // ListView$3$lv_movies
        // DecorView$0$android.internal.R.id.content->FrameLayout$1$fl_content->ListView$3$lv_movies
        if (stack == null || stack.head == null) {
            return "Empty Stack";
        }
        ViewNode node = stack.head;
        final StringBuilder sb = new StringBuilder();
        sb.append(node.toString());
        while ((node = node.next) != null) {
            sb.append("->").append(node.toString());
        }
        return sb.toString();
    }
}
