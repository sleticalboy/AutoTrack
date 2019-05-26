package com.sleticalboy.transform;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Created on 19-5-8.
 *
 * @author leebin
 */
public class TrackActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);
        // 普通点击事件 测试通过
        findViewById(R.id.normalClick).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toast("normal click");
            }
        });
        // lambda 表达式添加点击事件 测试通过
        findViewById(R.id.lambdaClick).setOnClickListener(v -> toast("lambda click"));
        // CheckBox 点击事件 测试通过
        ((CheckBox) findViewById(R.id.cbNormal)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                toast("cb normal checked");
            }
        });
        // 测试通过
        ((CheckBox) findViewById(R.id.cbLambda)).setOnCheckedChangeListener((view, isChecked) -> {
            toast("cb lambda checked");
        });
        // 测试通过
        findViewById(R.id.showDialog).setOnClickListener(v -> createDialog());

        // 动态添加 View
        findViewById(R.id.dynamicAdd).setOnClickListener(v -> dynamicAddView());
    }

    private void dynamicAddView() {
        final LinearLayout ll = findViewById(R.id.dynamicContainer);
        final ListView listView = new ListView(this);
        listView.setOnItemClickListener((parent, view, position, id) -> {
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //
            }
        });
        ll.removeAllViews();
        final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.weight = 1;
        params.gravity = Gravity.CENTER_VERTICAL;
        for (int i = 0; i < 3; i++) {
            final Button child = new Button(this);
            child.setAllCaps(false);
            child.setText("Button " + i);
            // 普通匿名内部类可以添加埋点代码，测试通过
            if (i % 2 == 0) {
                child.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toast("this is " + child.getText());
                    }
                });
            } else {
                // lambda 表达式可以添加埋点代码，测试通过
                child.setOnClickListener(v -> {
                    toast("this is " + child.getText());
                });
            }
            ll.addView(child, i, params);
        }
    }

    private void createDialog() {
        // showMultiChoiceDialog(this);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Auto Track Dialog");
        // multiChoiceItems 和 message 不能同时存在
        // builder.setMessage("track the click event automatically");
        final CharSequence[] items = {"Activity/Fragment", "Widget", "Dialog", "Menu"};
        final boolean[] checkedItems = {false, false, false, false};
        // 测试通过
        builder.setMultiChoiceItems(items, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if (isChecked) {
                    toast(items[which]);
                }
            }
        });
        // 测试通过
        builder.setNegativeButton("Normal Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                toast("Normal Cancel");
            }
        });
        builder.setPositiveButton("Lambda Ok", ((dialog, which) -> toast("Lambda Ok")));
        final AlertDialog dialog = builder.create();
        dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        dialog.show();
    }

    private void toast(CharSequence msg) {
        ToastUtils.toast(this, msg);
    }

    // 不需要特殊处理，因为 View 在初始化的过程中会通过反射得到声明在布局文件中的 android:onClick="xxx" 方法
    // 并设置给 View， 详情参见源码 androidx.appcompat.app.AppCompatViewInflater.DeclaredOnClickListener
    // 或者 android.support.v7.app.AppCompatViewInflater.DeclaredOnClickListener
    // 测试通过
    public void xmlClick(View view) {
        toast("xml click");
    }
}
