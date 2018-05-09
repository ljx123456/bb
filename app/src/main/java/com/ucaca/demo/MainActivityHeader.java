package com.ucaca.demo;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivityHeader extends Activity {

    private HeaderWheelView headerWheelView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_header);

        initView();
    }

    public void doClick(View v) {

        if (headerWheelView.getVisibility() != View.VISIBLE) {
            headerWheelView.setVisibility(View.VISIBLE);
        } else {
            headerWheelView.setVisibility(View.GONE);
        }
    }

    private void initView() {

        headerWheelView = (HeaderWheelView) findViewById(R.id.header_wheel);

        final MyWheelView wheel = headerWheelView.getWheelView();
        wheel.setLineColor(Color.RED);
        ArrayList<String> list = new ArrayList<>();
        list.add("北京");
        list.add("天津");
        list.add("河北");
        list.add("保定");
        list.add("南京");
        list.add("厦门");
        list.add("三亚");
        list.add("海南");
        list.add("成都");
        list.add("重庆");
        list.add("安徽");
        list.add("上海");
        list.add("新疆");
        list.add("拉萨");
        list.add("青海");
        list.add("东南");
        list.add("西南");
        list.add("东北");
        list.add("东西");
        list.add("东东");
        list.add("南北");
        list.add("南西");
        list.add("西北");
        list.add("西东");
        list.add("北南");
        list.add("北东");
        list.add("北西");
        list.add("哈哈");
        list.add("呵呵");
        list.add("嘿嘿");
        list.add("哈啊哈");

        wheel.addData(list,0);
        wheel.setOnSelectedListener(new MyWheelView.OnSelectedListener() {
            @Override
            public void selected(int id, final String text) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivityHeader.this, "选择了"+text, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });


        headerWheelView.getCancel().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(MainActivityHeader.this, "选择了取消", Toast.LENGTH_LONG).show();
                wheel.resetView();
                headerWheelView.setVisibility(View.GONE);
            }
        });

        headerWheelView.getOk().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(MainActivityHeader.this, "选择了确定", Toast.LENGTH_LONG).show();
                headerWheelView.setVisibility(View.GONE);
            }
        });
    }
}
