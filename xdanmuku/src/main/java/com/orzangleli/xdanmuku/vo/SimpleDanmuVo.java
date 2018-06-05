package com.orzangleli.xdanmuku.vo;

import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;

/**
 * <p>description：
 * <p>===============================
 * <p>creator：lixiancheng
 * <p>create time：2018/6/1 下午3:09
 * <p>===============================
 * <p>reasons for modification：
 * <p>Modifier：
 * <p>Modify time：
 * <p>@version
 */

public class SimpleDanmuVo<T> implements Comparable<SimpleDanmuVo> {
    private static final int LOW_SPEED = 8;
    private static final int SMALL_TEXT_SIZE = 30;
    private static final int PRIORITY_LOW = 1;
    private static final int PRIORITY_NORMAL = 2;
    private static final int PRIORITY_HIGH = 3;


    // 弹幕优先级
    private int mPriority;
    // 弹幕所在的航道
    private int mLineNum;
    // 弹幕距离右边屏幕的距离
    private int mPadding;
    // 弹幕长度
    private int mWidth;
    // 弹幕高度
    private int mLineHeight = 100;
    // 弹幕速度
    private int mSpeed;
    // 业务弹幕数据类型
    private T mData;
    // 弹幕内容
    private String mContent;
    // 弹幕颜色
    private int mDanmuColor;
    // 弹幕字体大小
    private int mDanmuTextSize;
    // 画笔
    private Paint mDanmuPaint, mDefaultPaint;

    private SimpleDanmuVo() {

    }

    public static SimpleDanmuVo obtain(String content) {
        return obtain(content, LOW_SPEED, Color.RED, SMALL_TEXT_SIZE, null, null, PRIORITY_NORMAL);
    }

    public static SimpleDanmuVo obtain(String content, int speed, int danmuColor, int danmuTextSize, Paint danmuPaint, Object data, int priority) {
        SimpleDanmuVo simpleDanmuVo = new SimpleDanmuVo();
        simpleDanmuVo.mContent = content;
        simpleDanmuVo.mSpeed = speed;
        simpleDanmuVo.mDanmuColor = danmuColor;
        simpleDanmuVo.mDanmuTextSize = danmuTextSize;
        simpleDanmuVo.mDanmuPaint = danmuPaint;
        simpleDanmuVo.mData = data;
        simpleDanmuVo.mPriority = priority;
        return simpleDanmuVo;
    }



    public int getLineNum() {
        return mLineNum;
    }

    public void setLineNum(int mLineNum) {
        this.mLineNum = mLineNum;
    }

    public int getPadding() {
        return mPadding;
    }

    public void setPadding(int mPadding) {
        this.mPadding = mPadding;
    }

    public int getLineHeight() {
        return mLineHeight;
    }

    public void setLineHeight(int mLineHeight) {
        this.mLineHeight = mLineHeight;
    }

    public int getSpeed() {
        return mSpeed;
    }

    public void setSpeed(int mSpeed) {
        this.mSpeed = mSpeed;
    }

    public T getData() {
        return mData;
    }

    public void setData(T mData) {
        this.mData = mData;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String mContent) {
        this.mContent = mContent;
    }

    public int getDanmuColor() {
        return mDanmuColor;
    }

    public void setDanmuColor(int mDanmuColor) {
        this.mDanmuColor = mDanmuColor;
        if (mDanmuPaint != null) {
            mDanmuPaint.setColor(mDanmuColor);
        }
    }

    public int getDanmuTextSize() {
        return mDanmuTextSize;
    }

    public void setDanmuTextSize(int mDanmuTextSize) {
        this.mDanmuTextSize = mDanmuTextSize;
        if (mDanmuPaint != null) {
            mDanmuPaint.setTextSize(mDanmuTextSize);
        }
    }

    public Paint getDanmuPaint() {
        if (mDanmuPaint == null) {
            if (mDefaultPaint == null) {
                mDefaultPaint = new Paint();
                mDefaultPaint.setAntiAlias(true);
                mDefaultPaint.setTextSize(mDanmuTextSize);
                mDefaultPaint.setColor(mDanmuColor);
            }
            return mDefaultPaint;
        }
        return mDefaultPaint;
    }

    public void setDanmuPaint(Paint mDanmuPaint) {
        this.mDanmuPaint = mDanmuPaint;
    }

    public int getWidth() {
        return mWidth;
    }

    public void setWidth(int mWidth) {
        this.mWidth = mWidth;
    }

    public int getPriority() {
        return mPriority;
    }

    public void setPriority(int mPriority) {
        this.mPriority = mPriority;
    }

    @Override
    public int compareTo(@NonNull SimpleDanmuVo o) {
        return this.getPriority() - o.getPriority();
    }


}
