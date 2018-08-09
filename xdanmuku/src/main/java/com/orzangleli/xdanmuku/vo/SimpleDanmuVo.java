package com.orzangleli.xdanmuku.vo;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.NonNull;

import com.orzangleli.xdanmuku.controller.DanmuEnqueueThread;
import com.orzangleli.xdanmuku.util.XUtils;

import java.util.ArrayList;
import java.util.List;

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
    private static final int LOW_SPEED = 2;
    private static final int NORMAL_SPEED = 4;
    private static final int HIGH_SPEED = 8;
    private static final int SMALL_TEXT_SIZE = 30;
    private static final int PRIORITY_LOW = 1;
    private static final int PRIORITY_NORMAL = 2;
    private static final int PRIORITY_HIGH = 3;

    private final int DEFAULT_PADDING = 10;

    // 弹幕优先级
    private int mPriority;
    // 弹幕所在的航道
    private int mLineNum = -1;
    // 弹幕距离右边屏幕的距离
    private int mPadding = Integer.MIN_VALUE;
    // 弹幕长度
    private int mWidth = 0;
    // 弹幕高度
    private int mHeight = 0;

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
    // 边框颜色 如果为-1则没有边框
    private int mBorderColor;
    // 边框宽度
    private int BORDER_WIDTH = 5;
    // 边框画笔
    private Paint mBorderPaint;
    // 弹幕行为, 默认从右到左
    private Behavior mBehavior = Behavior.RIGHT2LEFT;

    private Paint.FontMetricsInt mDefaultFontMetricsInt;

    private Path mPath;

    private Bitmap mCacheBitmap;

    private Bitmap mFirstShowBitmap;

    /**
     * 弹幕行为 支持从右到左，从左到右，顶部悬停，中间悬停，底部悬停
     */
    public enum Behavior {
        RIGHT2LEFT,
        LEFT2RIGHT,
        TOP,
        BOTTOM,
        CENTER,
        CUSTOM
    }


    private static final Object sPoolSync = new Object();
    private final static int MAX_POOL_SIZE = 100;
    private static List<SimpleDanmuVo> sPool = new ArrayList<>(MAX_POOL_SIZE);

    private SimpleDanmuVo() {

    }

    public static SimpleDanmuVo obtain(String content) {
        return obtain(content, HIGH_SPEED, Color.RED, SMALL_TEXT_SIZE, null, null, PRIORITY_NORMAL, -1);
    }

    public static SimpleDanmuVo obtain(String content, int borderColor) {
        return obtain(content, HIGH_SPEED, Color.RED, SMALL_TEXT_SIZE, null, null, PRIORITY_NORMAL, borderColor);
    }

    public static SimpleDanmuVo obtain(String content, int speed, int danmuColor, int danmuTextSize, Paint danmuPaint, Object data, int priority, int borderColor) {
        SimpleDanmuVo simpleDanmuVo = null;
        synchronized (sPoolSync) {
            if (sPool != null && sPool.size() > 0) {
                simpleDanmuVo = sPool.remove(0);
            }
        }
        if (simpleDanmuVo == null) {
            simpleDanmuVo = new SimpleDanmuVo();
        }
        simpleDanmuVo.mContent = content;
        simpleDanmuVo.mSpeed = speed;
        simpleDanmuVo.mDanmuColor = danmuColor;
        simpleDanmuVo.mDanmuTextSize = danmuTextSize;
        simpleDanmuVo.mDanmuPaint = danmuPaint;
        simpleDanmuVo.mData = data;
        simpleDanmuVo.mPriority = priority;
        simpleDanmuVo.mPadding = Integer.MIN_VALUE;
        simpleDanmuVo.mLineNum = -1;
        simpleDanmuVo.mWidth = 0;
        simpleDanmuVo.mBorderColor = borderColor;
        simpleDanmuVo.mBehavior = Behavior.RIGHT2LEFT;
        simpleDanmuVo.mPath = new Path();
        simpleDanmuVo.mCacheBitmap = simpleDanmuVo.mFirstShowBitmap;
        simpleDanmuVo.mFirstShowBitmap = null;
        XUtils.clearBitmap(simpleDanmuVo.mCacheBitmap);
        return simpleDanmuVo;
    }

    public void recycle() {
        synchronized (sPoolSync) {
            if (sPool != null) {
                if (sPool.size() >= MAX_POOL_SIZE) {
                    sPool.remove(0);
                }
                sPool.add(this);
            }
        }
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

    public synchronized void setPadding(int mPadding) {
        this.mPadding = mPadding;
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

    public int getBorderColor() {
        return mBorderColor;
    }

    public void setBorderColor(int borderColor) {
        this.mBorderColor = borderColor;
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
            }
            mDefaultPaint.setAntiAlias(true);
            mDefaultPaint.setTextSize(mDanmuTextSize);
            mDefaultPaint.setColor(mDanmuColor);
            return mDefaultPaint;
        }
        return mDanmuPaint;
    }

    public Paint.FontMetricsInt getFontMetrics() {
        if (mDefaultFontMetricsInt == null) {
            Paint danmuPaint = getDanmuPaint();
            if (danmuPaint != null) {
                mDefaultFontMetricsInt = danmuPaint.getFontMetricsInt();
                return mDefaultFontMetricsInt;
            }
        }
        return mDefaultFontMetricsInt;
    }

    public Paint getBorderPaint() {
        if (mBorderPaint == null) {
            mBorderPaint = new Paint();
        }
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth(BORDER_WIDTH);
        if (mBorderColor != -1) {
            mBorderPaint.setColor(mBorderColor);
        }
        return mBorderPaint;
    }

    public int getBorderStrokeWidth() {
        return (int) getBorderPaint().getStrokeWidth();
    }

    public void setDanmuPaint(Paint mDanmuPaint) {
        this.mDanmuPaint = mDanmuPaint;
    }

    public Behavior getBehavior() {
        return mBehavior;
    }

    public void setBehavior(Behavior behavior) {
        this.mBehavior = behavior;
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

    // 内置的绘制弹幕的方法

    /**
     * 绘制弹幕的终极技能：
     * 第一次使用drawText绘制这个文本，并保存在一个bitmap中
     * 之后的移动都只需要绘制这个bitmap即可。
     *
     * @param canvas
     * @param width
     * @param height
     */
    public void drawDanmukusInternal(Canvas canvas, int width, int height) {
        if (canvas == null || this.getContent() == null || "".equals(this.getContent())) {
            return;
        }

        int laneHeight = height / DanmuEnqueueThread.MAX_LINE_NUMS;

        if (mWidth == 0 && mHeight == 0) {
            Rect bounds = new Rect();
            getDanmuPaint().getTextBounds(this.getContent(), 0, this.getContent().length(), bounds);
            mWidth = bounds.width();
        }

        float x = 0, y = 0;
        if (mBehavior == Behavior.RIGHT2LEFT) {
            x = this.getPadding();
        } else if (mBehavior == Behavior.LEFT2RIGHT) {
            x = this.getPadding() - mWidth;
        }
        y = laneHeight * getLineNum();

        if (mFirstShowBitmap == null) {
            Paint.FontMetricsInt fontMetrics = getFontMetrics();
            if (fontMetrics == null) {
                return;
            }
            mHeight = Math.abs(fontMetrics.ascent + fontMetrics.leading) + fontMetrics.descent;
            int bitmapHeight = 0;
            boolean isOverLane;
            if (mHeight > laneHeight) {
                bitmapHeight = mHeight;
                isOverLane = true;
            } else {
                bitmapHeight = laneHeight;
                isOverLane = false;
            }
            // 如果上一个缓存的bitmap为空，那么重新创建一个bitmap
            if (mCacheBitmap == null) {
                mFirstShowBitmap = Bitmap.createBitmap(mWidth + getBorderStrokeWidth() * 2 + DEFAULT_PADDING * 2, bitmapHeight + getBorderStrokeWidth() * 2, Bitmap.Config.ARGB_8888);
            } else {
                mFirstShowBitmap = createMatrixBitmap(mCacheBitmap, mWidth + getBorderStrokeWidth() * 2 + DEFAULT_PADDING * 2, bitmapHeight + getBorderStrokeWidth() * 2);
                mCacheBitmap.recycle();
            }

            Canvas innerCanvas = new Canvas(mFirstShowBitmap);
            // 绘制弹幕
            int yPos = 0;
            if (isOverLane) {
                yPos = Math.abs(fontMetrics.ascent + fontMetrics.leading);
                // 绘制线框
                if (this.getBorderColor() != -1) {
                    int top = Math.max(getBorderStrokeWidth(), (laneHeight - mHeight) / 2 - getBorderStrokeWidth());
                    int bottom = Math.min(bitmapHeight + getBorderStrokeWidth() , (laneHeight + mHeight) / 2 + getBorderStrokeWidth());
                    int left = getBorderStrokeWidth();
                    int right = mWidth + getBorderStrokeWidth() * 2 + DEFAULT_PADDING * 2 - getBorderStrokeWidth();

                    mPath.reset();
                    mPath.moveTo(left, top);
                    mPath.lineTo(right, top);
                    mPath.lineTo(right, bottom);
                    mPath.lineTo(left, bottom);
                    mPath.lineTo(left, top);
                }
            } else {
                yPos = (laneHeight - mHeight) / 2 + Math.abs(fontMetrics.ascent + fontMetrics.leading);
                // 绘制线框
                if (this.getBorderColor() != -1) {
                    int top = Math.max(getBorderStrokeWidth(), (laneHeight - mHeight) / 2 - getBorderStrokeWidth());
                    int bottom = Math.min(bitmapHeight + getBorderStrokeWidth() , (laneHeight + mHeight) / 2 + getBorderStrokeWidth());
                    int left = getBorderStrokeWidth();
                    int right = mWidth + getBorderStrokeWidth() * 2 + DEFAULT_PADDING * 2 - getBorderStrokeWidth();
                    mPath.reset();
                    mPath.moveTo(left, top);
                    mPath.lineTo(right, top);
                    mPath.lineTo(right, bottom);
                    mPath.lineTo(left, bottom);
                    mPath.lineTo(left, top);
                }
            }
            innerCanvas.drawText(this.getContent(), DEFAULT_PADDING, yPos, this.getDanmuPaint());
            // 绘制线框
            if (this.getBorderColor() != -1) {
                innerCanvas.drawPath(mPath, getBorderPaint());
            }
        }

        canvas.drawBitmap(mFirstShowBitmap, x, y, this.getDanmuPaint());
    }

    // TODO: 2018/8/8 调用此方法防止内存泄漏
    public void destroy() {
        if (mCacheBitmap != null && !mCacheBitmap.isRecycled()) {
            mCacheBitmap.recycle();
        }
        if (mFirstShowBitmap != null && !mFirstShowBitmap.isRecycled()) {
            mFirstShowBitmap.recycle();
        }
        mCacheBitmap = null;
        mFirstShowBitmap = null;
    }

    public Bitmap createMatrixBitmap(Bitmap bm, int newWidth, int newHeight) {
        // 获得图片的宽高.
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例.
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数.
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片.
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }

}
