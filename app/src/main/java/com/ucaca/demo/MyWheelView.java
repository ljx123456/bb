package com.ucaca.demo;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by pactera on 2017/4/5.
 */

public class MyWheelView extends View {

    /**控件高度*/
    private int Height;
    /**控件宽度*/
    private int Width;
    /**可以显示的条目个数*/
    private int itemCount = 7;
    /**数据列表*/
    private ArrayList<WheelItem> data;
    /**每个条目的高度*/
    private int unitHeight = 50;
    /**线高*/
    private int lineHeight = 2;
    /**线的颜色*/
    private int lineColor = Color.BLACK;
    /**选中条目的顶端y坐标*/
    private int topY;
    /**选中条目的底端y坐标*/
    private int bottomY;
    /**画文字的画笔*/
    private Paint textPaint;
    /**被选中的条目的字体大小*/
    private int selectedSize = 22;
    /**被选中的条目的字体颜色*/
    private int selectedColor = Color.BLACK;
    /**未被选中的条目的字体大小*/
    private int normalSize = 18;
    /**未被选中的条目的字体颜色*/
    private int normalColor = Color.GRAY;
    /**记录每次按下时的y坐标*/
    private int downY;
    private Context context;
    /**用来监听当前选择的内容*/
    private OnSelectedListener listener;
    /**记录按下的时间*/
    private long downTime;
    /**用来刷新界面的handler*/
    private Handler refreshHandler;
    /**设置的初始选中项索引，默认选中中间项*/
    private int firstSelectedIndex = -1;

    public MyWheelView(Context context) {
        super(context);
        init(context, null);
    }

    public MyWheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MyWheelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    /**做一些初始化操作，以及单位转换操作*/
    private void init(Context context, AttributeSet attrs) {

        this.context = context;
        refreshHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                invalidate();
            }
        };

        //将默认的初始值转换成对应单位
        unitHeight = translatDimensionUnit(TypedValue.COMPLEX_UNIT_DIP,unitHeight,context);
        lineHeight = translatDimensionUnit(TypedValue.COMPLEX_UNIT_DIP,lineHeight,context);
        selectedSize = translatDimensionUnit(TypedValue.COMPLEX_UNIT_SP,selectedSize,context);
        normalSize = translatDimensionUnit(TypedValue.COMPLEX_UNIT_SP,normalSize,context);

        //获取xml里设置的希望值
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MyWheelView);
        unitHeight = (int) array.getDimension(R.styleable.MyWheelView_unitHeight, unitHeight);
        lineHeight = (int) array.getDimension(R.styleable.MyWheelView_lineHeight, lineHeight);
        selectedSize = (int) array.getDimension(R.styleable.MyWheelView_selectedTextSize, selectedSize);
        normalSize = (int) array.getDimension(R.styleable.MyWheelView_normalTextSize, normalSize);
        selectedColor = array.getColor(R.styleable.MyWheelView_selectedTextColor, selectedColor);
        normalColor = array.getColor(R.styleable.MyWheelView_normalTextColor, normalColor);
        itemCount = array.getInteger(R.styleable.MyWheelView_itemNumber, itemCount);
        lineColor = array.getColor(R.styleable.MyWheelView_lineColor, lineColor);
        firstSelectedIndex = array.getInteger(R.styleable.MyWheelView_selectedDefaultIndex, firstSelectedIndex);
        array.recycle();


        Height = unitHeight * itemCount;
        topY = Height / 2 - unitHeight / 2;//上线的y坐标
        bottomY = Height / 2 + unitHeight / 2;//下线的y坐标
    }

    /**为数据列表添加数据*/
    public void addData(ArrayList<String> list) {

        data = new ArrayList<>();
        for (int i=0; i<list.size(); i++) {

            WheelItem item = new WheelItem();
            item.setContent(list.get(i));//设置条目内容
            item.setY(i * unitHeight);//设置条目初始y坐标
            item.setId(i);//设置条目的索引
           data.add(item);
        }
        refreshHandler.sendEmptyMessage(0);
    }

    /**为数据列表添加数据，并指定首选项，firstSelectedIndex的取值要大于等于0*/
    public void addData(ArrayList<String> list, int firstSelectedIndex) {

        this.firstSelectedIndex = firstSelectedIndex;
        data = new ArrayList<>();
        for (int i=0; i<list.size(); i++) {

            WheelItem item = new WheelItem();
            item.setContent(list.get(i));
            item.setY(topY - (firstSelectedIndex - i) * unitHeight);
            item.setId(i);
            data.add(item);
        }
        refreshHandler.sendEmptyMessage(0);
    }

    /**不改变选择器内的数据，重置选择器内容的位置，如需重置选择器内容请使用addData方法*/
    public void resetView() {

        if (firstSelectedIndex != -1) {

            for (int i=0; i<data.size(); i++) {

                data.get(i).setY(topY - (firstSelectedIndex - i) * unitHeight);
                data.get(i).setOffsetY(0);
            }
        } else {

            for (int i = 0; i < data.size(); i++) {

                data.get(i).setY(i * unitHeight);
                data.get(i).setOffsetY(0);
            }
        }
        refreshHandler.sendEmptyMessage(0);
    }

    /**获取屏幕可以显示的条目个数*/
    public int getItemCount() {
        return itemCount;
    }

    /**设置屏幕可以显示的条目个数*/
    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
        invalidate();
    }

    /**获取条目的高度单位为dp*/
    public int getUnitHeight() {
        return unitHeight;
    }

    /**
     * 设置条目的高度，会自动转换单位为dp
     * @param unitHeight 例如你传值是10，即为10dp
     */
    public void setUnitHeight(int unitHeight) {
        this.unitHeight = translatDimensionUnit(TypedValue.COMPLEX_UNIT_DIP, unitHeight, context);
        invalidate();
    }

    /**获取线的高度，单位为dp*/
    public int getLineHeight() {
        return lineHeight;
    }

    /**设置线的高度*/
    public void setLineHeight(int lineHeight) {
        this.lineHeight = translatDimensionUnit(TypedValue.COMPLEX_UNIT_DIP, lineHeight, context);
        invalidate();
    }

    /**获取线的颜色*/
    public int getLineColor() {
        return lineColor;
    }

    /**设置线的颜色*/
    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
        invalidate();
    }

    /**获取选中条目的字体大小，单位是sp*/
    public int getSelectedSize() {
        return selectedSize;
    }

    /**设置选中条目的字体大小，单位是sp*/
    public void setSelectedSize(int selectedSize) {
        this.selectedSize = translatDimensionUnit(TypedValue.COMPLEX_UNIT_SP, selectedSize, context);
        invalidate();
    }

    /**获取选中条目的字体颜色*/
    public int getSelectedColor() {
        return selectedColor;
    }

    /**设置选中条目的字体颜色*/
    public void setSelectedColor(int selectedColor) {
        this.selectedColor = selectedColor;
        invalidate();
    }

    /**获取默认字体的大小，单位是sp*/
    public int getNormalSize() {
        return normalSize;
    }

    /**设置默认字体的大小，单位是sp*/
    public void setNormalSize(int normalSize) {
        this.normalSize = translatDimensionUnit(TypedValue.COMPLEX_UNIT_SP, normalSize, context);
        invalidate();
    }

    /**获取默认字体的颜色*/
    public int getNormalColor() {
        return normalColor;
    }

    /**设置默认字体的颜色*/
    public void setNormalColor(int normalColor) {
        this.normalColor = normalColor;
        invalidate();
    }

    /**为滚轮选择器设置选择监听*/
    public void setOnSelectedListener(OnSelectedListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Width = getMeasuredWidth();
        //Log.i("zhangdi","onMeasure-->width="+Width+", height="+Height);
        setMeasuredDimension(Width, Height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawLine(canvas);
        drawData(canvas);
    }

    /**画线*/
    private void drawLine(Canvas canvas) {

        Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(lineColor);
        canvas.drawLine(0, topY + lineHeight, Width, topY + lineHeight, linePaint);
        canvas.drawLine(0, bottomY - lineHeight, Width, bottomY - lineHeight, linePaint);
    }

    /**画数据*/
    private void drawData(Canvas canvas) {

        for (int i=0; i<data.size(); i++) {

            data.get(i).drawText(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        /*获取每次不论是按下，滑动，抬起的y坐标*/
        int y = (int) ev.getY();

        switch (ev.getAction()) {

            case MotionEvent.ACTION_DOWN :
                downY = (int) ev.getY();
                downTime = System.currentTimeMillis();
                break;

            case MotionEvent.ACTION_MOVE:
                actionMove(y - downY);
                break;

            case MotionEvent.ACTION_UP:
                //短时间用力滑动的时候，会产生一个较大的偏移量，需要分解偏移量并延长时间，让空间产生缓慢滑动的效果
                if (System.currentTimeMillis() - downTime < 200 && Math.abs(y - downY) > 100) {
                    slowMove(y - downY);
                } else {
                    actionUp(y - downY);
                }
                break;
        }

        return true;
    }

    /**将y轴偏移量分解，每隔5毫秒移动分解后的一个单位的距离*/
    private synchronized void slowMove(final int move) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                int height = Math.abs(move);
                int distance = 0;
                while (distance < height) {
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    actionMove(move > 0 ? distance : distance * (-1));
                    distance += 5;//移动单位为5，如果想移动快些就增加，移动慢性减少，但是移动太快会没有那种慢慢滚动的感觉

                }
                //Log.i("zhangdi","move="+move+", distance="+distance);
                actionUp(move);
            }
        }).start();
    }

    /**每次松手后重置条目的y轴坐标以及偏移量，判断被选中的条目是否在屏幕中心，如果不在调整到中心*/
    private void actionUp(int i) {

        i = translatDimensionUnit(TypedValue.COMPLEX_UNIT_DIP,i,context);
        /*排除边界条件，当第一个条目的y==topY时证明滑动到了最顶端，不允许再继续上滑
        当最后一个条目的y==topY时证明滑动到了最底端，不允许再继续下滑 其他情况返回值为0*/
        if (!isEdge(i)) {
            return;
        }

        //完成滑动后重置条目的y轴偏移量，并调整条目的y轴坐标
        offsetYReset(i);

        for (WheelItem item: data) {

            if (item.isSelected()) {

                //Log.i("zhangdi","actionUp-->selectItemId="+item.getId());
                if (listener != null) {
                    listener.selected(item.getId(), item.getContent());
                }

                int y = topY - item.getY();//计算出当前选中的条目与上线的y轴坐标差
                if (y != 0) {//文本不是正好被滑动到正中间
                    actionMove(y);//根据y轴坐标差继续滑动一段距离

                    offsetYReset(y);
                }
                break;
            }
        }
        refreshHandler.sendEmptyMessage(0);
    }

    /**完成滑动后重置条目的y轴偏移量，并调整条目的y轴坐标*/
    private void offsetYReset(int i) {

        for (WheelItem item : data) {

            item.setOffsetY(0);//把y轴偏移量置0
            item.setY(item.getY() + i);//每次滑动结束后根据y轴偏移量调整条目的y坐标
        }
    }

    /**每次滑动的时候修改条目的offsetY值，然后重绘界面来实现滑动效果*/
    private void actionMove(int i) {

        i = translatDimensionUnit(TypedValue.COMPLEX_UNIT_DIP,i,context);
        //Log.i("zhangdi","actionMove--> move="+i);
        for (WheelItem item : data) {

            item.setOffsetY(i);
        }
        //别忘了重绘界面
        refreshHandler.sendEmptyMessage(0);
    }

    /**当第一个条目的y==topY时证明滑动到了最顶端，不允许再继续上滑
     * 当最后一个条目的y==topY时证明滑动到了最底端，不允许再继续下滑
     * 其他情况返回值为0*/
    public boolean isEdge(int move) {

        //Log.i("zhangdi","isEdge()-->y+move="+(data.get(0).getY()+move));
        if (data.get(0).getY()+move > topY && move >= 0) {
            //Log.i("zhangdi","isEdge()-->不能继续上滑");
            for (int i=0; i<data.size(); i++) {
                data.get(i).setY(i*unitHeight+topY);
                data.get(i).setOffsetY(0);
            }
            if (listener != null) {
                listener.selected(data.get(0).getId(), data.get(0).getContent());
            }
            refreshHandler.sendEmptyMessage(0);
            return false;
        } else if (data.get(data.size()-1).getY()+move < topY && move <= 0) {
            //Log.i("zhangdi","isEdge()-->不能继续下滑");
            for (int i=data.size()-1,j=0; i>=0; i--,j++) {
                data.get(i).setY(-j*unitHeight+topY);
                data.get(i).setOffsetY(0);
            }
            if (listener != null) {
                listener.selected(data.get(data.size()-1).getId(), data.get(data.size()-1).getContent());
            }
            refreshHandler.sendEmptyMessage(0);
            return false;
        }
        return true;
    }

    private class WheelItem {

        private int id;
        /**条目所在高度*/
        private int y;
        /**移动距离*/
        private int offsetY;
        /**文字内容*/
        private String content;
        /**文字大小*/
        private int size = 0;

        /**判断是否在选中区域内,
         * 移动距离distance+条目高度height+半个单元格高度+字体高度/4这个高度是眼睛看到的文本内容的开始高度
         * 也就是移动距离distance+条目高度height+半个单元格高度+字体高度/4 >= 上线y坐标
         * 并且移动距离distance+条目高度height+半个单元格高度+字体高度/4 < 下线y坐标*/
        private boolean isSelected() {

            return ( (offsetY + y + unitHeight/2 - selectedSize/2 >= topY)  &&
                    (offsetY + y + unitHeight/2 - selectedSize/2 < bottomY) );
        }

        /**判断是否在可显示区域内,即y+offsetY不小于0，也不大于控件高度*/
        private boolean isVisible() {

            return  (y+offsetY >= 0 && (y+offsetY < Height));
        }

        /**把文字画出来*/
        private void drawText(Canvas canvas) {

            //Log.i("zhangdi","isSelected()-->itemId="+id+", height="+Height+", offsetY="+offsetY+", y="+y+", topY="+topY+", bottomY="+bottomY);
            if (!isVisible()) {//先判断y坐标是否在显示范围内，不在就直接返回了
                return;
            }

            if (textPaint == null) {//初始化画笔

                textPaint = new Paint();
                textPaint.setAntiAlias(true);
            }

            if (isSelected()) {//判断y坐标是否在两条线的中间，决定是否使用选中字体

                textPaint.setColor(selectedColor);
                textPaint.setTextSize(selectedSize);
            } else {

                textPaint.setColor(normalColor);
                //iOS的滚轮选择器有一种向内倾斜的感觉，但是我没找到实现办法，也懒得去找了，就让字体距离被选中的条目
                //越远越小，让字体有一种自中间向两边逐渐变小的线性感觉，视觉上也会产生一点向内弯曲的感觉

                if (y + offsetY > topY) {
                    size = normalSize - 2 * translatDimensionUnit(TypedValue.COMPLEX_UNIT_SP, (y + offsetY - topY) / unitHeight, context);
                } else {
                    size = normalSize - 2 * translatDimensionUnit(TypedValue.COMPLEX_UNIT_SP, (topY - y - offsetY) / unitHeight, context);
                }

                textPaint.setTextSize(size);
            }

            Rect bounds = new Rect();
            textPaint.getTextBounds(content,0,content.length(),bounds);//获取内容边界值
            int start = Width / 2 - bounds.width() / 2;//计算出内容的x坐标
            int top = y + offsetY + unitHeight / 2 + bounds.height() / 2;//内容的y坐标

            canvas.drawText(content,start,top,textPaint);//把文字写在正中间
        }

        /**获取条目id*/
        private int getId() {
            return id;
        }

        /**设置条目id*/
        private void setId(int id) {
            this.id = id;
        }

        /**获取条目所在高度*/
        private int getY() {
            return y;
        }

        /**设置条目所在高度*/
        private void setY(int y) {
            this.y = y;
        }

        /**获取条目y轴偏移量*/
        public int getOffsetY() {
            return offsetY;
        }

        /**设置条目y轴偏移量*/
        private void setOffsetY(int offsetY) {
            this.offsetY = offsetY;
        }

        /**获取条目文本内容*/
        private String getContent() {
            return content;
        }

        /**设置条目文本内容*/
        private void setContent(String content) {
            this.content = content;
        }
    }

    /**单位转换工具，可以把数值转成dp,sp等类型*/
    public int translatDimensionUnit(int unit, float value, Context context) {
        return (int) TypedValue.applyDimension(unit,value,context.getResources().getDisplayMetrics());
    }

    public interface OnSelectedListener {
        void selected(int id, String text);
    }
}
