package com.sleticalboy.transform;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

/**
 * Created on 19-5-19.
 *
 * @author leebin
 */
public class TestAdapter extends BaseAdapter {

    private final Context mContext;

    public TestAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new ImageView(mContext);
        }
        if (convertView instanceof ImageView) {
            ((ImageView) convertView).setImageResource(R.mipmap.ic_launcher);
        }
        return convertView;
    }
}
