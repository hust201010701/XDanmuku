package com.orzangleli.xdanmuku.controller;

import com.orzangleli.xdanmuku.ui.IDanmukuView;
import com.orzangleli.xdanmuku.vo.SimpleDanmuVo;

import java.util.List;

/**
 * <p>description：
 * <p>===============================
 * <p>creator：lixiancheng
 * <p>create time：2018/11/27 上午12:39
 * <p>===============================
 * <p>reasons for modification：
 * <p>Modifier：
 * <p>Modify time：
 * <p>@version
 */

public class DanmuDrawThread extends Thread {
    private DanmuController mDanmuController;
    private IDanmukuView mXDanmukuView;
    private final int DRAW_INTERVAL_TIME_MILLS = 8;
    private boolean paused = false;

    public void setDanmuController(IDanmukuView xDanmukuView, DanmuController mDanmuController) {
        this.mDanmuController = mDanmuController;
        this.mXDanmukuView = xDanmukuView;
    }

    @Override
    public void run() {
        super.run();

        while (true) {
            if (!paused && mXDanmukuView != null) {
                mXDanmukuView.drawDanmukus();
            }
            try {
                Thread.sleep(DRAW_INTERVAL_TIME_MILLS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
