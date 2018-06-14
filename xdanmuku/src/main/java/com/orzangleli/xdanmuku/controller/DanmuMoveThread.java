package com.orzangleli.xdanmuku.controller;

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

    public void setDanmuController(IDanmukuView xDanmukuView, DanmuController mDanmuController) {
        this.mDanmuController = mDanmuController;
        this.mXDanmukuView = xDanmukuView;
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
            simpleDanmuVo.setPadding(simpleDanmuVo.getPadding() + simpleDanmuVo.getSpeed());
        }
    }

}
