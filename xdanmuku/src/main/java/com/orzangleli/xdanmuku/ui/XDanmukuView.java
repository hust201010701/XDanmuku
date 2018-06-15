package com.orzangleli.xdanmuku.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.util.Log;
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

public class XDanmukuView extends TextureView implements TextureView.SurfaceTextureListener, IDanmukuView {

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

    public XDanmukuView(Context context) {
        super(context);
        init(context);
    }

    public XDanmukuView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public XDanmukuView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {
        initPaint();
        setOpaque(false);
        setWillNotCacheDrawing(true);
        setDrawingCacheEnabled(false);
        this.setSurfaceTextureListener(this);

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

    @Override
    public synchronized long drawDanmukus() {
        long startTime = System.currentTimeMillis();
        mLastDrawTime = startTime;
        if (mWidth <= 0 || mHeight <= 0) {
            return System.currentTimeMillis() - startTime;
        }
        Canvas canvas = this.lockCanvas();
        if (canvas != null) {
            // 清除画布
            clearCanvas(canvas);
            drawDanmukusDirectly(canvas);
        }
        unlockCanvasAndPost(canvas);
        return System.currentTimeMillis() - startTime;
    }

    public void drawDanmukusDirectly(Canvas canvas) {
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

    public void enqueue(SimpleDanmuVo vo) {
        mDanmuController.enqueue(vo);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        Log.d("lxc", "onSurfaceTextureAvailable() called with: surface = [" + surface + "], width = [" + width + "], height = [" + height + "]");
        mWidth = width;
        mHeight = height;
        mDanmuEnqueueThread.setWidth(mWidth);
        Log.i("lxc", "mWidth ---> " + mWidth);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        Log.d("lxc", "onSurfaceTextureSizeChanged() called with: surface = [" + surface + "], width = [" + width + "], height = [" + height + "]");
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        Log.d("lxc", "onSurfaceTextureDestroyed() called with: surface = [" + surface + "]");
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
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
