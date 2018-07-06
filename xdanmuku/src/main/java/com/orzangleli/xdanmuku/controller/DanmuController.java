package com.orzangleli.xdanmuku.controller;

import android.util.SparseArray;

import com.orzangleli.xdanmuku.vo.SimpleDanmuVo;

import java.util.List;
import java.util.Queue;

/**
 * <p>description：
 * <p>===============================
 * <p>creator：lixiancheng
 * <p>create time：2018/6/2 下午6:26
 * <p>===============================
 * <p>reasons for modification：
 * <p>Modifier：
 * <p>Modify time：
 * <p>@version
 */

public interface DanmuController<T> {
    // 添加弹幕
    void enqueue(T vo);
    // 移除弹幕
    void removeDanmuVo(T vo);
    // 移除等待队列中弹幕
    void removeAllWaitingDanmuVo();
    // 移除工作队列中的弹幕
    void removeAllWorkingDanmuVo();
    // 移除所有弹幕
    void removeAllDanmuVo();

    void removeWorkingItem(int pos);

    void addWorkingItem(SimpleDanmuVo simpleDanmuVo);

    // 获取需要展示的弹幕列表
    List<T> getWorkingList();
    // 获取排队的弹幕队列
    Queue<T> getWaitingQueue();

    SparseArray getLastDanmuVoArrayByVo(SimpleDanmuVo simpleDanmuVo);

    SparseArray getRightLineLastDanmuVoArray();

    SparseArray getLeftLineLastDanmuVoArray();

    SparseArray getTopLineLastDanmuVoArray();

    SparseArray getCenterLineLastDanmuVoArray();

    SparseArray getBottomLineLastDanmuVoArray();

    SimpleDanmuVo getTheMoreRightDanmuVo(SimpleDanmuVo simpleDanmuVo1, SimpleDanmuVo simpleDanmuVo2, int width);
}
