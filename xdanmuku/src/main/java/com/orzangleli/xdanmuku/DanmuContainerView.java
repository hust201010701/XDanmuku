package com.orzangleli.xdanmuku;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/30.
 */

public class DanmuContainerView extends ViewGroup {

    public final static float LOW_SPEED = 0.25f;
    public final static float NORMAL_SPEED = 0.6f;
    public final static float HIGH_SPEED = 1f;

    public final static int GRAVITY_TOP = 1 ;    //001
    public final static int GRAVITY_CENTER = 2 ;  //010
    public final static int GRAVITY_BOTTOM = 4 ;  //100
    public final static int GRAVITY_FULL = 7 ;   //111

    private int gravity = 7;


    private int spanCount = 8;

    private int WIDTH, HEIGHT;

    public List<View> spanList;

    private int singleLineHeight;

    XAdapter xAdapter;

    float speed = NORMAL_SPEED;


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

    public void setAdapter(XAdapter danmuAdapter) {
        xAdapter = danmuAdapter;
        singleLineHeight = danmuAdapter.getSingleLineHeight();
        new Thread(new MyRunnable()).start();
    }

    //单项点击监听器
    public interface OnItemClickListener{
        void onItemClick(Model model);
    }



    public void setSpeed(float s) {
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


    public void addDanmu(final Model model){
        if (xAdapter == null) {
            throw new Error("XAdapter(an interface need to be implemented) can't be null,you should call setAdapter firstly");
        }

        View danmuView = null;
        if(xAdapter.getCacheSize() >= 1){
            danmuView = xAdapter.getView(model,xAdapter.removeFromCacheViews(model.getType()));
            if(danmuView == null)
                addTypeView(model,danmuView,false);
            else
                addTypeView(model,danmuView,true);
        }
        else {
            danmuView = xAdapter.getView(model,null);
            addTypeView(model,danmuView,false);
        }

        //添加监听
        danmuView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListener != null)
                    onItemClickListener.onItemClick(model);
            }
        });

    }


    public void addTypeView(Model model,View child,boolean isReused) {
        super.addView(child);
        child.measure(0, 0);
        //把宽高拿到，宽高都是包含ItemDecorate的尺寸
        int width = child.getMeasuredWidth();
        int height = child.getMeasuredHeight();
        //获取最佳行数
        int bestLine = getBestLine();
        child.layout(WIDTH, singleLineHeight * bestLine, WIDTH + width, singleLineHeight * bestLine + height);

        InnerEntity innerEntity = null;
        innerEntity = (InnerEntity) child.getTag(R.id.tag_inner_entity);
        if(!isReused || innerEntity==null){
            innerEntity = new InnerEntity();
        }
        innerEntity.model = model;
        innerEntity.bestLine = bestLine;
        child.setTag(R.id.tag_inner_entity,innerEntity);

        spanList.set(bestLine, child);

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

    class InnerEntity{
        public int bestLine;
        public Model model;
    }


    private class MyRunnable implements Runnable {
        @Override
        public void run() {
            int count = 0;
            Message msg = null;
            while(true){
                if(count < 7500){
                    count ++;
                }
                else{
                    count = 0;
                    if(DanmuContainerView.this.getChildCount() < xAdapter.getCacheSize() / 2){
                        xAdapter.shrinkCacheSize();
                        System.gc();
                    }
                }
                if(DanmuContainerView.this.getChildCount() >= 0){
                    msg = new Message();
                    msg.what = 1; //移动view
                    handler.sendMessage(msg);
                }

                try {
                    Thread.sleep(4);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                for(int i=0;i<DanmuContainerView.this.getChildCount();i++){
                    View view = DanmuContainerView.this.getChildAt(i);
                    if(view.getX()+view.getWidth() >= 0)
                        view.offsetLeftAndRight((int)(0 - speed));
                    else{
                        //添加到缓存中
                        int type = ((InnerEntity)view.getTag(R.id.tag_inner_entity)).model.getType();
                        xAdapter.addToCacheViews(type,view);
                        DanmuContainerView.this.removeView(view);

                    }
                }
            }

        }
    };

}
