package com.orzangleli.xdanmuku.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.SystemClock;
import android.util.AttributeSet;

import com.chillingvan.canvasgl.ICanvasGL;
import com.chillingvan.canvasgl.glcanvas.GLPaint;
import com.chillingvan.canvasgl.glview.GLView;
import com.chillingvan.canvasgl.glview.texture.GLContinuousTextureView;
import com.chillingvan.canvasgl.glview.texture.GLTextureView;
import com.orzangleli.xdanmuku.controller.DanmuController;
import com.orzangleli.xdanmuku.controller.DanmuControllerImpl;
import com.orzangleli.xdanmuku.controller.DanmuEnqueueThread;
import com.orzangleli.xdanmuku.controller.DanmuMoveThread;
import com.orzangleli.xdanmuku.util.XUtils;
import com.orzangleli.xdanmuku.vo.SimpleDanmuVo;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>description：
 * <p>===============================
 * <p>creator：lixiancheng
 * <p>create time：2018/12/7 下午9:33
 * <p>===============================
 * <p>reasons for modification：
 * <p>Modifier：
 * <p>Modify time：
 * <p>@version
 */

public class XDanmakuView2 extends GLContinuousTextureView implements IDanmukuView {
    // 字幕画笔
    private Paint mDanmukuPaint, mClearPaint;
    private DanmuController<SimpleDanmuVo> mDanmuController;
    private List<XDanmukuView.DanmuDrawer> mDanmuDrawerList;
    private DanmuEnqueueThread mDanmuEnqueueThread;
    private DanmuMoveThread mDanmuMoveThread;
    private boolean mIsDebug = false;
    private long mDrawCostTime = 0;
    private TouchHelper mTouchHelper;
    private TouchHelper.OnClickDanmuListener mOnClickDanmuListener;
    private int mWidth = -1, mHeight = -1;

    public XDanmakuView2(Context context) {
        super(context);
        init(context);
    }

    public XDanmakuView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void init(Context context) {
        initPaint();

        mTouchHelper = new TouchHelper(this, context);

        mDanmuController = new DanmuControllerImpl();
        mDanmuDrawerList = new ArrayList<>();

        mDanmuEnqueueThread = new DanmuEnqueueThread();
        mDanmuEnqueueThread.setName("DanmuEnqueueThread");
        mDanmuEnqueueThread.setDanmuController(this, mDanmuController);
        mDanmuEnqueueThread.start();

        mDanmuMoveThread = new DanmuMoveThread();
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
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
        mDanmuEnqueueThread.setWidth(mWidth);
        mDanmuMoveThread.setWidth(mWidth);
    }


    @Override
    protected void onGLDraw(ICanvasGL canvas) {
        long startTime = SystemClock.elapsedRealtime();
        XUtils.clearCanvas(canvas);
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
//                    for (int j = 0; j < mDanmuDrawerList.size(); j++) {
//                        XDanmukuView.DanmuDrawer danmuDrawer = mDanmuDrawerList.get(j);
//                        if (danmuDrawer != null) {
//                            int width = danmuDrawer.drawDanmu(canvas, simpleDanmuVo);
//                            simpleDanmuVo.setWidth(width);
//                        }
//                    }
                }
            }
        }
    }

    public void enqueue(SimpleDanmuVo vo) {
        mDanmuController.enqueue(vo);
    }

    // 绘制弹幕航道
    private void drawLane(ICanvasGL canvas) {
        int laneHeight = mHeight / DanmuEnqueueThread.MAX_LINE_NUMS;
        int startColor = Color.parseColor("#80FFFF00");
        int endColor = Color.parseColor("#80009966");
        GLPaint paint = new GLPaint();
        for (int i = 0; i < DanmuEnqueueThread.MAX_LINE_NUMS; i++) {
            int color = (endColor - startColor) * i / (DanmuEnqueueThread.MAX_LINE_NUMS - 1) + startColor;
            paint.setColor(color);
            canvas.drawRect(new Rect(0, i * laneHeight, mWidth, (i + 1) * laneHeight), paint);
        }
    }

    @Override
    public long drawDanmukus() {
        return 0;
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public DanmuController getDanmuController() {
        return mDanmuController;
    }

    public void setDebug(boolean isDebug) {
        this.mIsDebug = isDebug;
    }


}
