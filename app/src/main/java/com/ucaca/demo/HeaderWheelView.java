package com.ucaca.demo;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by pactera on 2017/4/11.
 */

public class HeaderWheelView extends LinearLayout {

    /**取消按钮*/
    private TextView cancel;
    /**确定按钮*/
    private TextView ok;
    /**滚轮选择器*/
    private MyWheelView wheelView;
    /**当前的view*/
    private View view;

    public HeaderWheelView(Context context) {
        super(context);
        initView(context);
    }

    public HeaderWheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {

        //定义出顶部的确认、取消
        RelativeLayout.LayoutParams wrapcontentParams1 = new RelativeLayout.LayoutParams
                (RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams wrapcontentParams2 = new RelativeLayout.LayoutParams
                (RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        LayoutParams matchparentParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        RelativeLayout layout = new RelativeLayout(context);
        layout.setLayoutParams(matchparentParams);
        int padding1 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, context.getResources().getDisplayMetrics());
        int padding2 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, context.getResources().getDisplayMetrics());
        layout.setPadding(padding2, padding1, padding2, padding1);
        cancel = new TextView(context);
        cancel.setText("取消");
        cancel.setTextSize(16);
        cancel.setLayoutParams(wrapcontentParams1);
        layout.addView(cancel);
        ok = new TextView(context);
        ok.setText("确定");
        ok.setTextSize(16);
        wrapcontentParams2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        ok.setLayoutParams(wrapcontentParams2);
        layout.addView(ok);

        setOrientation(LinearLayout.VERTICAL);
        addView(layout);
        //设置出滚轮选择器
        wheelView = new MyWheelView(context);
        addView(wheelView);
    }


    /**获取当前的view*/
    public View getView() {
        return view;
    }

    /**获取取消按钮*/
    public TextView getCancel() {
        return cancel;
    }

    /**获取确定按钮*/
    public TextView getOk() {
        return ok;
    }

    /**获取滚轮选择器*/
    public MyWheelView getWheelView() {
        return wheelView;
    }
}
