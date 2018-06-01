package com.orzangleli.douyu;

import android.support.annotation.Nullable;

import com.orzangleli.douyu.client.Client;
import com.orzangleli.douyu.client.GetDmThread;
import com.orzangleli.douyu.client.HeartBeatThread;
import com.orzangleli.douyu.client.IReceiveDanmu;

import java.io.IOException;

/**
 * <p>description：
 * <p>===============================
 * <p>creator：lixiancheng
 * <p>create time：2018/6/1 下午1:44
 * <p>===============================
 * <p>reasons for modification：
 * <p>Modifier：
 * <p>Modify time：
 * <p>@version
 */

public class DyDanmuManager {
    private IReceiveDanmu iReceiveDanmu;
    private HeartBeatThread heartBeatThread;
    private GetDmThread getDmThread;

    private DyDanmuManager() {

    }

   private static class Holder {
       static DyDanmuManager dyDanmuManager = new DyDanmuManager();
   }

   public static DyDanmuManager getInstance() {
       return Holder.dyDanmuManager;
   }

    public void init(int roomId,@Nullable IReceiveDanmu iReceiveDanmu) {
        // 海量弹幕模式
        init(roomId, -9999, iReceiveDanmu);
    }

    public void init(final int roomId, final int groupId, @Nullable final IReceiveDanmu iReceiveDanmu) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Client client = Client.getInstance();
                    client.init(roomId, groupId, iReceiveDanmu);
                    if (heartBeatThread != null && !heartBeatThread.isInterrupted()) {
                        heartBeatThread.interrupt();
                    }
                    heartBeatThread = new HeartBeatThread();
                    heartBeatThread.start();

                    if (getDmThread != null && !getDmThread.isInterrupted()) {
                        getDmThread.interrupt();
                    }
                    getDmThread = new GetDmThread();
                    getDmThread.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }



}
