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
    private volatile SparseArray<SimpleDanmuVo> mLeftLineLastDanmuVoArray, mRightLineLastDanmuVoArray;

    private volatile SparseArray<SimpleDanmuVo> mTopLineLastDanmuVoArray, mCenterLineLastDanmuVoArray, mBottomLineLastDanmuVoArray;


    public DanmuControllerImpl() {
        init();
    }

    public void init() {
        mWaitingDanmuList = new PriorityBlockingQueue<>();
        mWorkingDanmuList = new LinkedList<>();
        mLeftLineLastDanmuVoArray = new SparseArray<>();
        mRightLineLastDanmuVoArray = new SparseArray<>();
        mTopLineLastDanmuVoArray = new SparseArray<>();
        mCenterLineLastDanmuVoArray = new SparseArray<>();
        mBottomLineLastDanmuVoArray = new SparseArray<>();
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
    public synchronized void addWorkingItem(SimpleDanmuVo simpleDanmuVo) {
        mWorkingDanmuList.add(simpleDanmuVo);
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
    public SparseArray getRightLineLastDanmuVoArray() {
        return mRightLineLastDanmuVoArray;
    }

    @Override
    public SparseArray getLeftLineLastDanmuVoArray() {
        return mLeftLineLastDanmuVoArray;
    }

    @Override
    public SparseArray getTopLineLastDanmuVoArray() {
        return mTopLineLastDanmuVoArray;
    }

    @Override
    public SparseArray getCenterLineLastDanmuVoArray() {
        return mCenterLineLastDanmuVoArray;
    }

    @Override
    public SparseArray getBottomLineLastDanmuVoArray() {
        return mBottomLineLastDanmuVoArray;
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

    @Override
    public SparseArray getLastDanmuVoArrayByVo(SimpleDanmuVo simpleDanmuVo) {
        if (simpleDanmuVo == null) {
            return null;
        }
        SparseArray sparseArray = null;
        if (simpleDanmuVo.getBehavior() == SimpleDanmuVo.Behavior.RIGHT2LEFT) {
            sparseArray = getRightLineLastDanmuVoArray();
        } else if (simpleDanmuVo.getBehavior() == SimpleDanmuVo.Behavior.LEFT2RIGHT) {
            sparseArray = getLeftLineLastDanmuVoArray();
        } else if (simpleDanmuVo.getBehavior() == SimpleDanmuVo.Behavior.BOTTOM) {
            sparseArray = getBottomLineLastDanmuVoArray();
        } else if (simpleDanmuVo.getBehavior() == SimpleDanmuVo.Behavior.TOP) {
            sparseArray = getTopLineLastDanmuVoArray();
        } else if (simpleDanmuVo.getBehavior() == SimpleDanmuVo.Behavior.CENTER) {
            sparseArray = getCenterLineLastDanmuVoArray();
        }
        return sparseArray;
    }

}
