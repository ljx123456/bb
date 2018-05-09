package com.ucaca.demo;

/**
 * Created by alu on 2017/12/1.
 */

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity1 extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);
        initView();
    }

    private void initView() {

        MyWheelView wheel = (MyWheelView) findViewById(R.id.wheel1);
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

        wheel.addData(list);
        wheel.setOnSelectedListener(new MyWheelView.OnSelectedListener() {
            @Override
            public void selected(int id, final String text) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity1.this, "选择了"+text, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}
