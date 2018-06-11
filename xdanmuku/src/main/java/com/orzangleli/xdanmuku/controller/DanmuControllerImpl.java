package com.orzangleli.xdanmuku.controller;

import android.support.annotation.NonNull;
import android.util.SparseArray;

import com.orzangleli.xdanmuku.vo.SimpleDanmuVo;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * <p>description：
 * <p>===============================
 * <p>creator：lixiancheng
 * <p>create time：2018/6/2 下午6:08
 * <p>===============================
 * <p>reasons for modification：
 * <p>Modifier：
 * <p>Modify time：
 * <p>@version
 */

public class DanmuControllerImpl implements DanmuController<SimpleDanmuVo>{
    // 排队的具有优先级弹幕池列表
    private volatile PriorityBlockingQueue<SimpleDanmuVo> mWaitingDanmuList;
    // 工作中的弹幕
    private volatile LinkedList<SimpleDanmuVo> mWorkingDanmuList;
    // 每行航道的最后一个弹幕
    private volatile SparseArray<SimpleDanmuVo> mLineLastDanmuVoArray;

    public DanmuControllerImpl() {
        init();
    }

    public void init() {
        mWaitingDanmuList = new PriorityBlockingQueue<>();
        mWorkingDanmuList = new LinkedList<>();
        mLineLastDanmuVoArray = new SparseArray<>();
    }

    @Override
    public synchronized void enqueue(@NonNull SimpleDanmuVo vo) {
        if (mWorkingDanmuList.size() > 1000) {
            mWaitingDanmuList.poll();
        }
        mWaitingDanmuList.add(vo);
    }

    @Override
    public synchronized void removeDanmuVo(@NonNull SimpleDanmuVo vo) {
        // 如果不包含的话，直接return
        if (!mWaitingDanmuList.contains(vo)) {
            return ;
        }
        mWaitingDanmuList.remove(vo);
    }

    @Override
    public void removeAllWaitingDanmuVo() {
        mWaitingDanmuList.clear();
    }

    @Override
    public synchronized void removeAllWorkingDanmuVo() {
        mWorkingDanmuList.clear();
    }

    @Override
    public synchronized void removeAllDanmuVo() {
        mWaitingDanmuList.clear();
        mWorkingDanmuList.clear();
    }

    @Override
    public synchronized void removeWorkingItem(int pos) {
        mWorkingDanmuList.remove(pos);
    }

    @Override
    public synchronized void removeLastItem(int pos) {
        mLineLastDanmuVoArray.removeAt(pos);
    }

    @Override
    public synchronized void addWorkingItem(SimpleDanmuVo simpleDanmuVo) {
        mWorkingDanmuList.add(simpleDanmuVo);
    }

    @Override
    public synchronized void putLastItem(int key, SimpleDanmuVo value) {
        mLineLastDanmuVoArray.put(key, value);
    }

    @Override
    public List<SimpleDanmuVo> getWorkingList() {
        return mWorkingDanmuList;
    }

    @Override
    public Queue<SimpleDanmuVo> getWaitingQueue() {
        return mWaitingDanmuList;
    }

    @Override
    public void updateLineLastDanmuVo(SimpleDanmuVo simpleDanmuVo, int width) {
        mLineLastDanmuVoArray.put(simpleDanmuVo.getLineNum(), getTheMoreRightDanmuVo(mLineLastDanmuVoArray.get(simpleDanmuVo.getLineNum()), simpleDanmuVo, width));
    }

    @Override
    public SparseArray getLineLastDanmuVoArray() {
        return mLineLastDanmuVoArray;
    }

    /**
     * 获取更加靠右的弹幕
     * @param simpleDanmuVo1
     * @param simpleDanmuVo2
     */
    public SimpleDanmuVo getTheMoreRightDanmuVo(SimpleDanmuVo simpleDanmuVo1, SimpleDanmuVo simpleDanmuVo2, int width) {
        if (width <= 0) {
            return null;
        }
        if (simpleDanmuVo1 == null) {
            return simpleDanmuVo2;
        } else if (simpleDanmuVo1 != null && simpleDanmuVo2 == null) {
            return simpleDanmuVo1;
        } else {
            int padding1 = simpleDanmuVo1.getPadding();
            int padding2 = simpleDanmuVo2.getPadding();

            int width1 = simpleDanmuVo1.getWidth();
            int width2 = simpleDanmuVo2.getWidth();

            if (width - padding1 + width1 >= width - padding2 + width2) {
                return simpleDanmuVo1;
            } else {
                return simpleDanmuVo2;
            }
        }
    }

}
