package com.sleticalboy.autotrack.data;

/**
 * Created on 19-5-11.
 *
 * @author leebin
 */
public final class ViewNode {

    // 如果里的信息不够用可以接着扩展
    private final String type;
    private final String declaredId;
    // index = -1 表示是 DecorView
    private final int index;
    // private final String desc;
    /**
     * 指向下一个元素
     */
    /*package*/ ViewNode next;

    public ViewNode(String type, int index, String declaredId) {
        if (type == null) {
            throw new NullPointerException();
        }
        this.type = type;
        this.index = index;
        this.declaredId = declaredId == null || declaredId.isEmpty() ? null : declaredId;
    }

    @Override
    public String toString() {
        return toFormatString();
    }

    private String toFormatString() {
        // type$index$declaredId
        return type + (index == -1 ? "" : "$" + index) + (declaredId == null ? "" : "$" + declaredId);
    }
}
