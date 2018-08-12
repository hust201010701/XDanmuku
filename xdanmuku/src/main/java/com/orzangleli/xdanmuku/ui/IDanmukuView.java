package com.orzangleli.xdanmuku.ui;

import com.orzangleli.xdanmuku.controller.DanmuController;

/**
 * <p>description：
 * <p>===============================
 * <p>creator：lixiancheng
 * <p>create time：2018/6/13 上午11:15
 * <p>===============================
 * <p>reasons for modification：
 * <p>Modifier：
 * <p>Modify time：
 * <p>@version
 */

public interface IDanmukuView {
    long drawDanmukus();
    void onDestroy();
    DanmuController getDanmuController();
}
