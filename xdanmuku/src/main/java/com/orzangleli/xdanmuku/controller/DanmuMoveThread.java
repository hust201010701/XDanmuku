package com.orzangleli.xdanmuku.controller;

import android.util.Log;

import com.orzangleli.xdanmuku.ui.IDanmukuView;
import com.orzangleli.xdanmuku.ui.XDanmukuView;
import com.orzangleli.xdanmuku.ui.XDanmukuView2;
import com.orzangleli.xdanmuku.vo.SimpleDanmuVo;

import java.util.List;

/**
 * <p>description：
 * <p>===============================
 * <p>creator：lixiancheng
 * <p>create time：2018/6/4 下午11:08
 * <p>===============================
 * <p>reasons for modification：
 * <p>Modifier：
 * <p>Modify time：
 * <p>@version
 */

public class DanmuMoveThread extends Thread {
    private DanmuController mDanmuController;
    private IDanmukuView mXDanmukuView;
    private int MOVE_INTERVAL_TIME_MILLS = 16;
    private int mWidth = -1;


    public void setDanmuController(IDanmukuView xDanmukuView, DanmuController mDanmuController) {
        this.mDanmuController = mDanmuController;
        this.mXDanmukuView = xDanmukuView;
    }

    public void setWidth(int width) {
        this.mWidth = width;
    }

    @Override
    public void run() {
        super.run();

        if (mDanmuController == null) {
            return;
        }

        while (true) {
            List<SimpleDanmuVo> workingList = mDanmuController.getWorkingList();
            moveAllWorkingDanmu(workingList);
            long costTime = 0;
            if (mXDanmukuView != null) {
                costTime = mXDanmukuView.drawDanmukus();
            }
            try {
                Thread.sleep(MOVE_INTERVAL_TIME_MILLS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private void moveAllWorkingDanmu(List<SimpleDanmuVo> workingList) {
        if (workingList == null) {
            return;
        }
        for (int i = 0; i < workingList.size(); i++) {
            SimpleDanmuVo simpleDanmuVo = workingList.get(i);
            if (simpleDanmuVo != null) {
                switch (simpleDanmuVo.getBehavior()) {
                    case RIGHT2LEFT:
                        if (simpleDanmuVo.getPadding() == Integer.MIN_VALUE) {
                            simpleDanmuVo.setPadding(mWidth);
                        }
                        simpleDanmuVo.setPadding(simpleDanmuVo.getPadding() - simpleDanmuVo.getSpeed());
                        break;
                    case LEFT2RIGHT:
                        // padding 应该减去自身的宽度，但是此时宽度未知，所以这个padding不是真实的padding
                        if (simpleDanmuVo.getPadding() == Integer.MIN_VALUE) {
                            simpleDanmuVo.setPadding(0);
                        }
                        simpleDanmuVo.setPadding(simpleDanmuVo.getPadding() + simpleDanmuVo.getSpeed());
                        Log.i("lxc", "simpleDanmuVo.getPadding() ---> " + simpleDanmuVo.getPadding());
                        break;
                    case TOP:
                        simpleDanmuVo.setPadding(mWidth/2);
                        break;
                    case CENTER:
                        simpleDanmuVo.setPadding(mWidth/2);
                        break;
                    case BOTTOM:
                        simpleDanmuVo.setPadding(mWidth/2);
                        break;
                    case CUSTOM:
//                        simpleDanmuVo.locate();
                        break;
                }
            }

        }
    }

}
