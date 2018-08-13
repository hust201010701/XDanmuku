package com.orzangleli.xdanmuku.controller;

import android.util.SparseArray;

import com.orzangleli.xdanmuku.ui.IDanmukuView;
import com.orzangleli.xdanmuku.vo.SimpleDanmuVo;

import java.util.List;
import java.util.Queue;

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

public class DanmuEnqueueThread extends Thread {
    private DanmuController mDanmuController;
    private IDanmukuView mXDanmukuView;
    private int ENQUEUE_INTERVAL_TIME_MILLS = 100;
    private int mWidth = -1;
    private boolean mIsDestory = false;
    public static final int MAX_LINE_NUMS = 15;

    public void setDanmuController(IDanmukuView xDanmukuView, DanmuController mDanmuController) {
        this.mDanmuController = mDanmuController;
        this.mXDanmukuView = xDanmukuView;
    }

    public void setWidth(int width) {
        mWidth = width;
    }

    @Override
    public void run() {
        super.run();

        while (true) {
            long time1 = System.currentTimeMillis();
            List<SimpleDanmuVo> workingList = mDanmuController.getWorkingList();
            Queue<SimpleDanmuVo> waitingQueue = mDanmuController.getWaitingQueue();
            trimWorkingList(workingList);
            long time2 = System.currentTimeMillis();
//            Log.i("lxc", "1 ---> 2:" + (time2-time1));

            long time3 = System.currentTimeMillis();
//            Log.i("lxc", "2 ---> 3:" + (time3-time2));
            // 如果bestLine 为 -1，表示没有足够的空间放下这条弹幕，那么直接进入丢弃此弹幕下一帧
            // TODO: 2018/6/5 对高优先级特殊处理，高优先级优先显示
            int times = 0;
            while (times < MAX_LINE_NUMS) {
                // 从等待列表中移除一个添加到工作列表
                SimpleDanmuVo danmuVo = waitingQueue.poll();
                if (danmuVo != null) {
                    int bestLine = getBestLine(danmuVo, mDanmuController.getLeftLineLastDanmuVoArray(),
                            mDanmuController.getRightLineLastDanmuVoArray(), mDanmuController.getTopLineLastDanmuVoArray(),
                            mDanmuController.getCenterLineLastDanmuVoArray(), mDanmuController.getBottomLineLastDanmuVoArray());

                    if (bestLine >= 0 && bestLine < MAX_LINE_NUMS) {
                        danmuVo.setLineNum(bestLine);
                        mDanmuController.addWorkingItem(danmuVo);
                        SparseArray lastDanmuVoArrayByVo = mDanmuController.getLastDanmuVoArrayByVo(danmuVo);
                        if (lastDanmuVoArrayByVo != null) {
                            lastDanmuVoArrayByVo.put(bestLine, danmuVo);
                        }
                    }
                    times++;
                }
            }
            try {
                Thread.sleep(ENQUEUE_INTERVAL_TIME_MILLS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 移除不在屏幕中的弹幕
     *
     * @param workingList
     */
    private void trimWorkingList(List<SimpleDanmuVo> workingList) {
        if (workingList == null) {
            return;
        }
        for (int i = 0; i < workingList.size(); i++) {
            SimpleDanmuVo simpleDanmuVo = workingList.get(i);
            if (simpleDanmuVo != null) {
                boolean shouldRecycle = false;
                switch (simpleDanmuVo.getBehavior()) {
                    case RIGHT2LEFT:
                        int right = simpleDanmuVo.getLeftPadding() + simpleDanmuVo.getWidth();
                        shouldRecycle = right < 0;
                        break;
                    case LEFT2RIGHT:
                        shouldRecycle = (mWidth + simpleDanmuVo.getWidth() - simpleDanmuVo.getLeftPadding()) < 0;
                        break;
                }
                if (shouldRecycle) {
                    mDanmuController.removeWorkingItem(i);
                    SparseArray lastDanmuVoArrayByVo = mDanmuController.getLastDanmuVoArrayByVo(simpleDanmuVo);
                    if (lastDanmuVoArrayByVo != null) {
                        int index = lastDanmuVoArrayByVo.indexOfValue(simpleDanmuVo);
                        if (index != -1) {
                            lastDanmuVoArrayByVo.removeAt(index);
                        }
                        simpleDanmuVo.recycle();
                    }
                }
            }
        }
    }

    public int getBestLine(SimpleDanmuVo simpleDanmuVo,
                           SparseArray<SimpleDanmuVo> leftLineLastDanmuVoArray,
                           SparseArray<SimpleDanmuVo> rightLineLastDanmuVoArray,
                           SparseArray<SimpleDanmuVo> topLineLastDanmuVoArray,
                           SparseArray<SimpleDanmuVo> centerLineLastDanmuVoArray,
                           SparseArray<SimpleDanmuVo> bottomLineLastDanmuVoArray) {
        if (simpleDanmuVo != null && simpleDanmuVo.getBehavior() == SimpleDanmuVo.Behavior.RIGHT2LEFT
                && (rightLineLastDanmuVoArray == null || rightLineLastDanmuVoArray.size() == 0)) {
            return 0;
        }
        if (simpleDanmuVo != null && simpleDanmuVo.getBehavior() == SimpleDanmuVo.Behavior.LEFT2RIGHT
                && (leftLineLastDanmuVoArray == null || leftLineLastDanmuVoArray.size() == 0)) {
            return 0;
        }
        if (simpleDanmuVo.getBehavior() == SimpleDanmuVo.Behavior.RIGHT2LEFT) {
            for (int i = 0; i < MAX_LINE_NUMS; i++) {
                SimpleDanmuVo temp = rightLineLastDanmuVoArray.get(i);
                // 没有宽度的弹幕是刚刚插入的，还没有绘制所有没有长度，代表这个航道已有弹幕占领，需要看下一行的
                if (temp != null && temp.getWidth() <= 0) {
                    continue;
                }
                if (temp == null || mWidth - temp.getLeftPadding() > temp.getWidth()) {
                    return i;
                }
            }
        } else if (simpleDanmuVo.getBehavior() == SimpleDanmuVo.Behavior.LEFT2RIGHT) {
            for (int i = 0; i < MAX_LINE_NUMS; i++) {
                SimpleDanmuVo temp = leftLineLastDanmuVoArray.get(i);
                // 没有宽度的弹幕是刚刚插入的，还没有绘制所有没有长度，代表这个航道已有弹幕占领，需要看下一行的
                if (temp != null && temp.getWidth() <= 0) {
                    continue;
                }
                if (temp == null || temp.getLeftPadding() > temp.getWidth()) {
                    return i;
                }
            }
        } else if (simpleDanmuVo.getBehavior() == SimpleDanmuVo.Behavior.TOP){
            for (int i = 0; i < MAX_LINE_NUMS / 3; i++) {
                SimpleDanmuVo temp = topLineLastDanmuVoArray.get(i);
                if (temp == null) {
                    return i;
                }
            }
        } else if (simpleDanmuVo.getBehavior() == SimpleDanmuVo.Behavior.CENTER){
            for (int i = MAX_LINE_NUMS / 3; i < 2 * MAX_LINE_NUMS / 3; i++) {
                SimpleDanmuVo temp = centerLineLastDanmuVoArray.get(i);
                if (temp == null) {
                    return i;
                }
            }
        } else if (simpleDanmuVo.getBehavior() == SimpleDanmuVo.Behavior.BOTTOM){
            for (int i = 2 * MAX_LINE_NUMS / 3; i < MAX_LINE_NUMS; i++) {
                SimpleDanmuVo temp = bottomLineLastDanmuVoArray.get(i);
                if (temp == null) {
                    return i;
                }
            }
        }
        return -1;
    }


    public void destory() {
        mIsDestory = true;
    }

}
