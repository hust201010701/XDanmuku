package com.orzangleli.xdanmuku;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/30.
 */

public class DanmuContainerView extends ViewGroup {

    public final static int LOW_SPEED = 1;
    public final static int NORMAL_SPEED = 3;
    public final static int HIGH_SPEED = 5;

    public final static int GRAVITY_TOP = 1 ;    //001
    public final static int GRAVITY_CENTER = 2 ;  //010
    public final static int GRAVITY_BOTTOM = 4 ;  //100
    public final static int GRAVITY_FULL = 7 ;   //111

    private int gravity = 7;


    private int spanCount = 8;

    private int WIDTH, HEIGHT;

    public List<View> spanList;

    private int singleLineHeight;
    DanmuConverter danmuConverter = null;
    int speed = NORMAL_SPEED;

    public DanmuContainerView(Context context) {
        this(context, null, 0);
    }

    public DanmuContainerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DanmuContainerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        spanList = new ArrayList<View>();

    }



    public void setGravity(int gravity) {
        this.gravity = gravity;
    }

    OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener){
        onItemClickListener = listener;
    };

    //单项点击监听器
    public interface OnItemClickListener<M>{
        void onItemClick(M model);
    }



    public void setSpeed(int s) {
        speed = s;
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        WIDTH = width;
        HEIGHT = height;

        spanCount = HEIGHT / singleLineHeight;
        for (int i = 0; i < this.spanCount; i++) {
            if (spanList.size() <= spanCount)
                spanList.add(i, null);
        }
    }

    public void setConverter(DanmuConverter converter) {
        danmuConverter = converter;
        singleLineHeight = converter.getSingleLineHeight();

    }

    public <M> void addDanmu(final M model) throws Error {
        if (danmuConverter == null) {
            throw new Error("DanmuConverter can't be null,you should call setConverter firstly");
        }
        View danmuView = danmuConverter.convert(model);
        addView(danmuView);
        //添加监听
        danmuView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListener != null)
                    onItemClickListener.onItemClick(model);
            }
        });

    }

    @Override
    public void addView(View child) {
        super.addView(child);
        child.measure(0, 0);
        //把宽高拿到，宽高都是包含ItemDecorate的尺寸
        int width = child.getMeasuredWidth();
        int height = child.getMeasuredHeight();
        //获取最佳行数
        int bestLine = getBestLine();

        child.layout(WIDTH, singleLineHeight * bestLine, WIDTH + width, singleLineHeight * bestLine + height);
        child.setTag(bestLine); //将行数保存在tag中
        spanList.set(bestLine, child);
        new Thread(new MyRunnable(child)).start();
    }



    private int getBestLine() {
        //转换成2进制
        int gewei = gravity % 2;   //个位是
        int temp = gravity / 2;
        int shiwei = temp % 2;
        temp = temp / 2;
        int baiwei = temp % 2;

        //将所有的行分为三份,前两份行数相同,将第一份的行数四舍五入
        int firstPart = (int)(spanCount / 3.0f + 0.5f);

        //构造允许输入行的列表
        List<Integer> legalLines = new ArrayList<>();
        if(gewei == 1){
            for(int i=0;i<firstPart;i++)
                legalLines.add(i);
        }
        if(shiwei == 1){
            for(int i=firstPart;i<2*firstPart;i++)
                legalLines.add(i);
        }
        if(baiwei == 1){
            for(int i=2*firstPart;i<spanCount;i++)
                legalLines.add(i);
        }


        int bestLine = 0;
        //如果有空行直接结束
        for (int i = 0; i < spanCount; i++) {
            if (spanList.get(i) == null) {
                bestLine = i;
                if(legalLines.contains(bestLine))
                    return bestLine;
            }
        }
        float minSpace = Integer.MAX_VALUE;
        //没有空行，就找最大空间的
        for (int i = spanCount - 1; i >= 0; i--) {
            if(legalLines.contains(i)) {
                if (spanList.get(i).getX() + spanList.get(i).getWidth() <= minSpace) {
                    minSpace = spanList.get(i).getX() + spanList.get(i).getWidth();
                    bestLine = i;
                }
            }
        }
        return bestLine;
    }


    private class MyRunnable implements Runnable {
        View view;

        public MyRunnable(View v) {
            view = v;
        }

        @Override
        public void run() {
            while (view.getX() + view.getWidth() >= 0) {

                Message msg = new Message();
                msg.obj = view;
                msg.what = 1; //移动view
                handler.sendMessage(msg);

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

            if (spanList.get((int) view.getTag()) == view) {
                spanList.set((int) view.getTag(), null);
            }

            Message msg = new Message();
            msg.obj = view;
            msg.what = 2; //删除view
            handler.sendMessage(msg);

        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                View view = (View) msg.obj;
//                view.setX(view.getX()-speed);
                view.offsetLeftAndRight(0 - speed);
            } else if (msg.what == 2) {
                View view = (View) msg.obj;
                DanmuContainerView.this.removeView(view);
            }

        }
    };

}
