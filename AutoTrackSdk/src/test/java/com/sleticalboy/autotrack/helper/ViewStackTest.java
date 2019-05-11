package com.sleticalboy.autotrack.helper;

import com.sleticalboy.autotrack.data.ViewNode;
import com.sleticalboy.autotrack.data.ViewStack;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created on 19-5-11.
 *
 * @author leebin
 */
public class ViewStackTest {

    private ViewStack mStack;

    @Before
    public void setup() {
        mStack = new ViewStack();
        mStack.push(new ViewNode("ListView", 3, "lv_movies"));
        mStack.push(new ViewNode("FrameLayout", 1, "fl_content"));
        mStack.push(new ViewNode("DecorView", 0, "android.internal.R.id.content"));
    }

    @After
    public void tearDown() {
        mStack = null;
    }

    @Test
    public void testClear() {
        assertTrue(mStack.clear());
        assertEquals(mStack.depth(), 0);
        assertTrue(mStack.isEmpty());
    }

    @Test
    public void testPushAndPop() {
        ViewNode node = new ViewNode("TextView", 999, "tv_test");
        assertTrue(mStack.push(node));
        assertEquals(mStack.pop(), node);
        assertEquals(node.toString(), "TextView$999$tv_test");
    }

    @Test
    public void testToPath() {
        assertEquals(mStack.toString(), "DecorView$0$android.internal.R.id.content->FrameLayout$1$fl_content->" +
                "ListView$3$lv_movies");
    }

    @Test
    public void testSize() {
        assertEquals(mStack.depth(), 3);
    }
}