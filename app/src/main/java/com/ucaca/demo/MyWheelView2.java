package com.ucaca.demo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by pactera on 2017/4/12.
 */

public class MyWheelView2 extends FrameLayout {

    /**滚轮选择器*/
    private WheelView wheelView;
    /**选择线的高度*/
    private int lineHeight;
    /**选择线的颜色*/
    private int lineColor;

    public MyWheelView2(Context context) {
        super(context);
        init(context, null);
    }

    public MyWheelView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    /**初始化滚轮选择器和上下的选择线并且显示在屏幕上*/
    private void init(Context context, AttributeSet attrs) {

        //初始化滚轮选择器
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        wheelView = new WheelView(context, attrs);
        wheelView.setLayoutParams(params);
        wheelView.setBackgroundColor(Color.TRANSPARENT);
        int height = wheelView.Height;
        Log.i("zhangdi", "height="+height);

        //获取xml里的配置
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MyWheelView);
        lineHeight = (int) array.getDimension(R.styleable.MyWheelView_lineHeight, wheelView.changUnit(TypedValue.COMPLEX_UNIT_DIP, 2));
        lineColor = array.getColor(R.styleable.MyWheelView_lineColor, Color.BLACK);
        array.recycle();

        //计算出上下两条线距顶端的距离
        int lineTopY = height / 2 - wheelView.unitHeight / 2 + lineHeight;
        int lineBottomY = height / 2 + wheelView.unitHeight / 2 - lineHeight;
        //初始化两条线的宽度和高度设置
        LayoutParams lineParams1 = new LayoutParams(LayoutParams.MATCH_PARENT, lineHeight);
        LayoutParams lineParams2 = new LayoutParams(LayoutParams.MATCH_PARENT, lineHeight);
        //创建两条线并且设置距顶部距离以及线的颜色，并且把线添加到屏幕上
        View line1 = new View(context);
        line1.setBackgroundColor(lineColor);
        lineParams1.topMargin = lineTopY;
        line1.setLayoutParams(lineParams1);
        addView(line1);
        View line2 = new View(context);
        line2.setBackgroundColor(Color.BLACK);
        lineParams2.topMargin = lineBottomY;
        line2.setLayoutParams(lineParams2);
        addView(line2);
        addView(wheelView);
    }

    public WheelView getWheelView() {
        return wheelView;
    }

    /**为滚轮选择器设置数据和初始的选中项*/
    public void setData(ArrayList<String> list){
        wheelView.setData(list);
    }

    public class WheelView extends LinearLayout {
        private Context context;
        /**使得控件可以滑动*/
        private Scroller scroller;
        /**控件的高度*/
        private int Height;
        /**控件的宽度*/
        private int Width;
        /**显示的条目个数*/
        private int itemCount;
        /**条目单元格的高度*/
        private int unitHeight;
        /**选中内容的字体大小*/
        private int selectedFontSize = 20;
        /**选中内容的字体颜色*/
        private int selectedFontColor;
        /**未选中内容的字体大小*/
        private int normalFontSize = 16;
        /**未选中内容的字体颜色*/
        private int normalFontColor;
        /**条目集合*/
        private ArrayList<TextView> wheelItems;
        /**每一次操作屏幕时的y值*/
        private int lastY;
        /**每一个条目的布局设置*/
        private LayoutParams params;
        /**滑动速度跟踪器*/
        private VelocityTracker tracker;
        /**当前选中项的索引*/
        private int currentIndex;
        /**最初设置的选中项的索引*/
        private int firstSelectedIndex;
        /**选中监听器*/
        private MyWheelView.OnSelectedListener listener;

        public WheelView(Context context) {
            super(context);
            initView(context, null);
        }

        public WheelView(Context context, AttributeSet attrs) {
            super(context, attrs);
            initView(context, attrs);
        }

        /**初始化数据*/
        private void initView(Context context, AttributeSet attrs) {

            setOrientation(LinearLayout.VERTICAL);
            this.context = context;
            scroller = new Scroller(context);
            tracker = VelocityTracker.obtain();

            itemCount = 7;
            unitHeight = changUnit(TypedValue.COMPLEX_UNIT_DIP, 50);
            selectedFontColor = Color.BLACK;
            normalFontColor = Color.GRAY;

            //获取xml里的配置
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MyWheelView);
            itemCount = array.getInteger(R.styleable.MyWheelView_itemNumber, itemCount);
            unitHeight = (int) array.getDimension(R.styleable.MyWheelView_unitHeight, unitHeight);
            selectedFontSize = array.getInteger(R.styleable.MyWheelView_selectedTextSize, selectedFontSize);
            selectedFontColor = array.getColor(R.styleable.MyWheelView_selectedTextColor, selectedFontColor);
            normalFontSize = array.getInteger(R.styleable.MyWheelView_normalTextSize, normalFontSize);
            normalFontColor = array.getColor(R.styleable.MyWheelView_normalTextColor, normalFontColor);
            firstSelectedIndex = array.getInteger(R.styleable.MyWheelView_selectedDefaultIndex, (itemCount/2));//如果不设置默认选中屏幕中间项
            array.recycle();

            Height = itemCount * unitHeight;
            params = new LayoutParams(LayoutParams.WRAP_CONTENT, unitHeight);
            params.gravity = Gravity.CENTER_HORIZONTAL;

        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            Width = getMeasuredWidth();
            setMeasuredDimension(Width, Height);//根据希望显示的条目个数配置控件的高度
        }

        /**为选择器设置数据*/
        public void setData(ArrayList<String> list) {
            //每次需要先清空所有的数据
            removeAllViews();
            wheelItems = new ArrayList<>();
            currentIndex = firstSelectedIndex;

            //遍历数组生成对应的文本控件并添加到屏幕上
            for (int i=0; i<list.size(); i++) {

                TextView tv = new TextView(context);
                tv.setGravity(Gravity.CENTER);
                tv.setText(list.get(i));
                tv.setLayoutParams(params);
                wheelItems.add(tv);
                setSelectedFont();//设置选中项的字体
                smoothScrollBy((firstSelectedIndex - (itemCount/2))*unitHeight);//根据首选项的索引滑动到首选项的位置
                addView(tv);
            }
            firstSelectedIndex -= (firstSelectedIndex - (itemCount/2));//计算出首选项与中间项的差值，以便后续计算选中项和滑动距离使用
            invalidate();
        }

        /**设置选中监听器*/
        public void setOnSelectedListener(MyWheelView.OnSelectedListener listener) {

            this.listener = listener;
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {

            if (wheelItems == null || wheelItems.size() <= 0)
                return true;

            tracker.addMovement(event);
            int y = (int) event.getY();

            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    break;
                case MotionEvent.ACTION_MOVE:
                    int dexY = y - lastY;
                    actionMove(getScrollY(), dexY);
                    break;
                case MotionEvent.ACTION_UP:
                    actionUp(getScrollY());
                    listener.selected(currentIndex, wheelItems.get(currentIndex).getText().toString());
                    break;
            }
            lastY = y;
            return true;
        }

        /**手指滑动时使用，实时改变选中的条目*/
        private void actionMove(int scrollY, int dexY) {
            getSelectedIndex(scrollY);
            scrollBy(0,-dexY);
        }

        /**手指松开后继续滑动一段距离*/
        private void actionUp(final int scrollY) {

            getSelectedIndex(scrollY);
            int dy = (currentIndex - firstSelectedIndex) * unitHeight - scrollY;
            smoothScrollBy(dy);
        }

        /**根据手指的滑动速度获取到当前选中的条目*/
        private void getSelectedIndex(int scrollY){

            tracker.computeCurrentVelocity(100);//设置时间单位为0.1秒
            float yVelocity = tracker.getYVelocity();//1秒内延y轴运动了多少个像素
            if (Math.abs(yVelocity) >= unitHeight) {//如果像素大于一个单位高度
                //像素是大于0的则是向上滑动，否则是向下
                int count = (int) (yVelocity / changUnit(TypedValue.COMPLEX_UNIT_PX, unitHeight) / 5);
                currentIndex = currentIndex - count;
            } else {
                if (scrollY > 0) {//scrollY大于0表示向itemCount/2的下方项滑动，否则是向上方项滑动
                    currentIndex = (scrollY + unitHeight/2)/unitHeight + firstSelectedIndex;
                }
                else {
                    currentIndex = (scrollY - unitHeight/2)/unitHeight + firstSelectedIndex;
                }
            }
            currentIndex = Math.max(0, Math.min(currentIndex, wheelItems.size()-1));
            setSelectedFont();
            tracker.clear();
        }

        /**设置选中的文本字体，其他未选中项使用默认字体*/
        private void setSelectedFont() {

            for (int i=0; i<wheelItems.size(); i++) {

                TextView tv = wheelItems.get(i);
                tv.setTextSize(normalFontSize);
                tv.setTextColor(normalFontColor);

                int j = Math.abs(i-currentIndex);
                if((normalFontSize - j*2) > 0)
                    tv.setTextSize(normalFontSize - 2*j);

                if (i== currentIndex) {
                    tv.setTextSize(selectedFontSize);
                    tv.setTextColor(selectedFontColor);
                }
            }
        }

        /**让控件在0.1秒内从当前位置滑动到指定位置，必须配合computeScroll使用*/
        private void smoothScrollBy(int dy) {//dy是y轴移动距离
            scroller.startScroll(0, getScrollY(), 0, dy, 100);
            postInvalidate();
        }

        @Override
        public void computeScroll() {
            if (scroller.computeScrollOffset()) {
                scrollTo(scroller.getCurrX(), scroller.getCurrY());
                postInvalidate();
            }
        }

        /**将你传进来得到值变成你期望的单位
         * @param unit TypedValue.COMPLEX_UNIT_DIP就是期望转换成dp类型的值
         * @param value 宽度或者字体大小
         * 例如你传进来的值是TypedValue.COMPLEX_UNIT_DIP ，10，返回值就是10dp对应的值*/
        public int changUnit(int unit, int value) {
            return (int) TypedValue.applyDimension(unit, value, context.getResources().getDisplayMetrics());
        }
    }
}
