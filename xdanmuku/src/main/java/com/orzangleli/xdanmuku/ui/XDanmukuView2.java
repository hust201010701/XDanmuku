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
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;

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

public class XDanmukuView2 extends SurfaceView implements SurfaceHolder.Callback, IDanmukuView {

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
        setWillNotCacheDrawing(true);
        setDrawingCacheEnabled(false);

        mHolder = this.getHolder();
        if (mHolder != null) {
            mHolder.addCallback(this);
            mHolder.setFormat(PixelFormat.TRANSPARENT);
        }

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
        // 设置弹幕透明度
//        this.setAlpha(1f);

    }

    private void initPaint() {
        mDanmukuPaint = new Paint();
        mClearPaint = new Paint();
        mClearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    /**
     * 之前出现 闪烁的原因找到了：
     * 1. 之前的做法： 先清屏，再把新的弹幕绘制到屏幕上
     * 2. 现在的做法： 先把弹幕绘制到bakBitmap上，然后再清屏，再把bakBitmap绘制到屏幕上
     * @return
     */
    @Override
    public synchronized long drawDanmukus() {
        long startTime = System.currentTimeMillis();
        mLastDrawTime = startTime;
        if (mWidth <= 0 || mHeight <= 0) {
            return System.currentTimeMillis() - startTime;
        }
        if (mHolder != null) {
            Canvas canvas = mHolder.lockCanvas();
            if (canvas != null) {
                // 清除画布
                clearCanvas(canvas);
                drawDanmukusOnBak(canvas);
//                canvas.drawBitmap(mBakBitmap, 0, 0, mDanmukuPaint);
            }
            mHolder.unlockCanvasAndPost(canvas);
        }
        return System.currentTimeMillis() - startTime;
    }

    public void drawDanmukusOnBak(Canvas canvas) {
        // 清除画布
        clearCanvas(canvas);
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
                    drawDanmukusInternal(canvas, simpleDanmuVo);
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
        int startColor = Color.parseColor("#CCCC00");
        int endColor = Color.parseColor("#0066CC");
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

    // 内置的绘制弹幕的方法
    private void drawDanmukusInternal(Canvas canvas, SimpleDanmuVo simpleDanmuVo) {
        if (canvas == null || simpleDanmuVo == null || simpleDanmuVo.getContent() == null || "".equals(simpleDanmuVo.getContent())) {
            return;
        }
//        Log.i("lxc", "正在画弹幕 ---> " + simpleDanmuVo.getContent());
        int laneHeight = mHeight / DanmuEnqueueThread.MAX_LINE_NUMS;
        canvas.drawText(simpleDanmuVo.getContent(), mWidth - simpleDanmuVo.getPadding(), (0.6f + simpleDanmuVo.getLineNum()) * laneHeight, simpleDanmuVo.getDanmuPaint());
        simpleDanmuVo.setWidth((int) (simpleDanmuVo.getDanmuPaint().measureText(simpleDanmuVo.getContent()) + 0.5f));
    }

    public void enqueue(SimpleDanmuVo vo) {
        mDanmuController.enqueue(vo);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mWidth = holder.getSurfaceFrame().width();
        mHeight = holder.getSurfaceFrame().height();
        mDanmuEnqueueThread.setWidth(mWidth);
        mBakBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        mBakCanvas = new Canvas(mBakBitmap);
        Log.i("lxc", "mWidth ---> " + mWidth + " , mHeight = " + mHeight);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d("lxc", "surfaceChanged() called with: holder = [" + holder + "], format = [" + format + "], width = [" + width + "], height = [" + height + "]");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

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
