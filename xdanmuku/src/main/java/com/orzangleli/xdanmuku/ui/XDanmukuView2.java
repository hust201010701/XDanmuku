package com.orzangleli.xdanmuku.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.widget.TextView;

import com.orzangleli.xdanmuku.controller.DanmuController;
import com.orzangleli.xdanmuku.controller.DanmuControllerImpl;
import com.orzangleli.xdanmuku.controller.DanmuEnqueueThread;
import com.orzangleli.xdanmuku.controller.DanmuMoveThread;
import com.orzangleli.xdanmuku.vo.SimpleDanmuVo;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>description：
 * <p>===============================
 * <p>creator：lixiancheng
 * <p>create time：2018/6/1 下午3:06
 * <p>===============================
 * <p>reasons for modification：
 * <p>Modifier：
 * <p>Modify time：
 * <p>@version
 */

public class XDanmukuView2 extends View implements IDanmukuView {

    // 字幕画笔
    private Paint mDanmukuPaint, mClearPaint;
    private DanmuController<SimpleDanmuVo> mDanmuController;
    private List<DanmuDrawer> mDanmuDrawerList;
    private DanmuEnqueueThread mDanmuEnqueueThread;
    private DanmuMoveThread mDanmuMoveThread;
    private boolean mIsDebug = false;
    private long mDrawCostTime = 0;

    private long mLastDrawTime = 0;

    private int mWidth = -1, mHeight = -1;

    private SurfaceHolder mHolder;

    private Bitmap mBakBitmap;
    private Canvas mBakCanvas;

    public static final int MSG_UPDATE = 1;

    public XDanmukuView2(Context context) {
        super(context);
        init(context);
    }

    public XDanmukuView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public XDanmukuView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {
        initPaint();

        mDanmuController = new DanmuControllerImpl();
        mDanmuDrawerList = new ArrayList<>();

        mDanmuEnqueueThread = new DanmuEnqueueThread();
        mDanmuEnqueueThread.setName("DanmuEnqueueThread");
        mDanmuEnqueueThread.setDanmuController(this, mDanmuController);
        mDanmuEnqueueThread.start();

        mDanmuMoveThread= new DanmuMoveThread();
        mDanmuMoveThread.setName("DanmuMoveThread");
        mDanmuMoveThread.setDanmuController(this, mDanmuController);
        mDanmuMoveThread.start();
    }

    private void initPaint() {
        mDanmukuPaint = new Paint();
        mClearPaint = new Paint();
        mClearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (canvas != null) {
            // 清除画布
            drawDanmukusOnBak(canvas);
        }
    }

    /**
     * 之前出现 闪烁的原因找到了：
     * 1. 之前的做法： 先清屏，再把新的弹幕绘制到屏幕上
     * 2. 现在的做法： 先把弹幕绘制到bakBitmap上，然后再清屏，再把bakBitmap绘制到屏幕上
     * @return
     */
    @Override
    public synchronized long drawDanmukus() {
        mHandler.obtainMessage(MSG_UPDATE).sendToTarget();
        return 0;
    }

    public void drawDanmukusOnBak(Canvas canvas) {
        // 绘制航道
        if (mIsDebug) {
            drawLane(canvas);
        }
        List<SimpleDanmuVo> workingList = mDanmuController.getWorkingList();
        if (workingList != null) {
            for (int i = 0; i < workingList.size(); i++) {
                SimpleDanmuVo simpleDanmuVo = workingList.get(i);
                if (simpleDanmuVo == null) {
                    continue;
                }
                if (mDanmuDrawerList.size() == 0) {
                    simpleDanmuVo.drawDanmukusInternal(canvas, mWidth, mHeight);
                } else {
                    for (int j = 0; j < mDanmuDrawerList.size(); j++) {
                        DanmuDrawer danmuDrawer = mDanmuDrawerList.get(j);
                        if (danmuDrawer != null) {
                            int width = danmuDrawer.drawDanmu(canvas, simpleDanmuVo);
                            simpleDanmuVo.setWidth(width);
                        }
                    }
                }
            }
        }
    }

    // 绘制弹幕航道
    private void drawLane(Canvas canvas) {
        int laneHeight = mHeight / DanmuEnqueueThread.MAX_LINE_NUMS;
        int startColor = Color.parseColor("#80CCCC00");
        int endColor = Color.parseColor("#800066CC");
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        for (int i = 0; i < DanmuEnqueueThread.MAX_LINE_NUMS; i++) {
            int color = (endColor - startColor) * i / (DanmuEnqueueThread.MAX_LINE_NUMS - 1) + startColor;
            paint.setColor(color);
            canvas.drawRect(new Rect(0, i * laneHeight, mWidth, (i + 1) * laneHeight), paint);
        }
    }

    private void clearCanvas(Canvas canvas) {
        if (canvas != null) {
            canvas.drawPaint(mClearPaint);
        }
    }

    public void enqueue(SimpleDanmuVo vo) {
        mDanmuController.enqueue(vo);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
        mDanmuEnqueueThread.setWidth(mWidth);
        mDanmuMoveThread.setWidth(mWidth);
        if (mBakBitmap != null) {
            mBakBitmap.recycle();
        }
        mBakBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        if (mBakCanvas != null) {
            mBakCanvas.setBitmap(mBakBitmap);
        } else {
            mBakCanvas = new Canvas(mBakBitmap);
        }
    }

    public Handler mHandler = new Handler (){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_UPDATE:
                    invalidate();
                    break;
            }
        }
    };

    interface DanmuDrawer {
        /**
         * 返回值为弹幕的长度
         *
         * @param canvas
         * @param simpleDanmuVo
         *
         * @return
         */
        int drawDanmu(Canvas canvas, SimpleDanmuVo simpleDanmuVo);
    }

    public void addDanmuDrawer(DanmuDrawer danmuDrawer) {
        mDanmuDrawerList.add(danmuDrawer);
    }

    public void setDebug(boolean isDebug) {
        this.mIsDebug = isDebug;
    }

    public long getDrawCostTime() {
        return mDrawCostTime;
    }
}
