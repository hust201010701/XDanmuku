package com.orzangleli.xdanmuku.controller;

import android.util.SparseArray;

import com.orzangleli.xdanmuku.ui.XDanmukuView;
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
    private XDanmukuView mXDanmukuView;
    private int ENQUEUE_INTERVAL_TIME_MILLS = 100;
    private int mWidth = -1;
    private boolean mIsDestory = false;
    public static final int MAX_LINE_NUMS = 10;

    public void setDanmuController(XDanmukuView xDanmukuView, DanmuController mDanmuController) {
        this.mDanmuController = mDanmuController;
        this.mXDanmukuView = xDanmukuView;
    }

    public void setWidth(int width) {
        mWidth = width;
    }

    @Override
    public void run() {
        super.run();

        if (mDanmuController == null) {
            return;
        }

        while (true) {
            long time1 = System.currentTimeMillis();
            List<SimpleDanmuVo> workingList = mDanmuController.getWorkingList();
            Queue<SimpleDanmuVo> waitingQueue = mDanmuController.getWaitingQueue();

            trimWorkingList(workingList);
            long time2 = System.currentTimeMillis();
//            Log.i("lxc", "1 ---> 2:" + (time2-time1));

            int bestLine = getBestLine(mDanmuController.getLineLastDanmuVoArray());
//            Log.i("lxc", "bestLine ---> " + bestLine);
            long time3 = System.currentTimeMillis();
//            Log.i("lxc", "2 ---> 3:" + (time3-time2));
            // 如果bestLine 为 -1，表示没有足够的空间放下这条弹幕，那么直接进入丢弃此弹幕下一帧
            // TODO: 2018/6/5 对高优先级特殊处理，高优先级优先显示
            int times = 0;
            while (bestLine != -1 && times < MAX_LINE_NUMS) {
                // 从等待列表中移除一个添加到工作列表
                SimpleDanmuVo danmuVo = waitingQueue.poll();
                if (danmuVo != null) {
                    danmuVo.setLineNum(bestLine);
                    mDanmuController.addWorkingItem(danmuVo);
                    mDanmuController.putLastItem(bestLine, danmuVo);
                    bestLine = getBestLine(mDanmuController.getLineLastDanmuVoArray());
//                    if (bestLine != -1) {
//                        Log.i("lxc", "bestLine ---> " + bestLine);
//                    }
                    times++;
                } else {
                    bestLine = -1;
                }
            }
            long time4 = System.currentTimeMillis();
//            Log.i("lxc", "3 ---> 4:" + (time4-time3));
            long time5 = System.currentTimeMillis();
//            Log.i("lxc", "4 ---> 5:" + (time5-time4));
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
                int right = mWidth - simpleDanmuVo.getPadding() + simpleDanmuVo.getWidth();
                if (right <= 0) {
                    mDanmuController.removeWorkingItem(i);
                    int index = mDanmuController.getLineLastDanmuVoArray().indexOfValue(simpleDanmuVo);
                    if (index != -1) {
                        mDanmuController.removeLastItem(index);
                    }
                    simpleDanmuVo = null;
                }
            }
        }
    }

    public int getBestLine(SparseArray<SimpleDanmuVo> lineLastDanmuVoArray) {
        if (lineLastDanmuVoArray == null || lineLastDanmuVoArray.size() == 0) {
            return 0;
        }
        for (int i = 0; i < MAX_LINE_NUMS; i++) {
            SimpleDanmuVo temp = lineLastDanmuVoArray.get(i);
            // 没有宽度的弹幕是刚刚插入的，还没有绘制所有没有长度，代表这个航道已有弹幕占领，需要看下一行的
            if (temp != null && temp.getWidth() <= 0) {
                continue;
            }
            if (temp == null || temp.getPadding() > temp.getWidth()) {
                return i;
            }
        }
        return -1;
    }


    public void destory() {
        mIsDestory = true;
    }

}
