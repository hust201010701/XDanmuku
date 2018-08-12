package com.orzangleli.xdanmuku.ui;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.orzangleli.xdanmuku.controller.DanmuController;
import com.orzangleli.xdanmuku.vo.SimpleDanmuVo;

import java.util.List;

/**
 * <p>description：
 * <p>===============================
 * <p>creator：lixiancheng
 * <p>create time：2018/8/10 下午6:23
 * <p>===============================
 * <p>reasons for modification：
 * <p>Modifier：
 * <p>Modify time：
 * <p>@version
 */

public class TouchHelper {
    private IDanmukuView mIDanmukuView;
    private Context mContext;
    private OnClickDanmuListener mOnClickDanmuListener;

    private GestureDetector mGestureDetector;

    public TouchHelper(IDanmukuView iDanmukuView, Context context) {
        this.mIDanmukuView = iDanmukuView;
        this.mContext = context;
        mGestureDetector = new GestureDetector(mContext, gestureListener);
    }

    public boolean onTouchEvent(MotionEvent e) {
        return mGestureDetector.onTouchEvent(e);
    }

    public void setOnClickDanmuListener(OnClickDanmuListener onClickDanmuListener) {
        this.mOnClickDanmuListener = onClickDanmuListener;
    }

    private GestureDetector.OnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
            if (mIDanmukuView != null) {
                DanmuController danmuController = mIDanmukuView.getDanmuController();
                if (danmuController != null) {
                    List workingList = danmuController.getWorkingList();
                    SimpleDanmuVo touchItemVo = getTouchItemVo(workingList, e.getX(), e.getY());
                    if (mOnClickDanmuListener != null) {
                        mOnClickDanmuListener.onLongClickDanmu(touchItemVo);
                    }
                }
            }
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (mIDanmukuView != null) {
                DanmuController danmuController = mIDanmukuView.getDanmuController();
                if (danmuController != null) {
                    List workingList = danmuController.getWorkingList();
                    SimpleDanmuVo touchItemVo = getTouchItemVo(workingList, e.getX(), e.getY());
                    if (mOnClickDanmuListener != null) {
                        mOnClickDanmuListener.onClickDanmu(touchItemVo);
                        if (touchItemVo != null) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
            }
            return super.onSingleTapConfirmed(e);
        }
    };

    /**
     * 获取触摸的坐标位置处弹幕
     * @param workingList
     * @param touchX
     * @param touchY
     * @return
     */
    public SimpleDanmuVo getTouchItemVo(List<SimpleDanmuVo> workingList, float touchX, float touchY) {
        if (workingList == null || workingList.size() == 0) {
            return null;
        }
        for (int i=0;i<workingList.size();i++) {
            SimpleDanmuVo simpleDanmuVo = workingList.get(i);
            if (simpleDanmuVo != null) {
                float left = simpleDanmuVo.getX();
                float right = left + simpleDanmuVo.getWidth();
                float top = simpleDanmuVo.getY();
                float bottom = top + simpleDanmuVo.getHeight();

                if (left <= touchX && touchX <= right && top <= touchY && touchY <= bottom) {
                    return simpleDanmuVo;
                }
            }
        }
        return null;
    }


    public interface OnClickDanmuListener {
        void onClickDanmu(@Nullable SimpleDanmuVo simpleDanmuVo);
        void onLongClickDanmu(@Nullable SimpleDanmuVo simpleDanmuVo);
    }


}
