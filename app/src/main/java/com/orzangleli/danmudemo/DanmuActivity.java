package com.orzangleli.danmudemo;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.orzangleli.douyu.DyDanmuManager;
import com.orzangleli.douyu.client.IReceiveDanmu;
import com.orzangleli.xdanmuku.ui.XDanmukuView;
import com.orzangleli.xdanmuku.ui.XDanmukuView2;
import com.orzangleli.xdanmuku.vo.SimpleDanmuVo;

import java.util.Random;

public class DanmuActivity extends AppCompatActivity {

    XDanmukuView2 mXDanmukuView;

    private static final int CHEN_XIANG = 2132902;
    private static final int FENG_TI_MO = 71017;
    private static final int CHEN_YI_FA = 67373;
    private static final int AN_CHUN = 96291;
    private static final int SANSANJIU = 96291;

    public static final int ROOM_ID = FENG_TI_MO;
    public static String SEED[] = {"桃树、杏树、梨树，你不让我", "都开满了花赶趟儿。红的像火，", "花里带着甜味儿，闭了眼，树上", "满是桃儿、杏儿、梨儿!花下成", "嗡地闹着，大小的蝴蝶"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_danmu);

        mXDanmukuView = this.findViewById(R.id.xdanmukuView);
//        mXDanmukuView.setDebug(true);

        startFakeDanmu();

        DyDanmuManager.getInstance().init(ROOM_ID, new IReceiveDanmu() {
            @Override
            public void receive(String name, String content) {
//                Log.i("lxc", name + " : " +content);
                SimpleDanmuVo simpleDanmuVo = SimpleDanmuVo.obtain(name + " : " + content);
                simpleDanmuVo.setSpeed(new Random().nextInt(2) + 3);
                simpleDanmuVo.setDanmuColor(getRandomColor());
                simpleDanmuVo.setDanmuTextSize(new Random().nextInt(20) + 30);
                mXDanmukuView.enqueue(simpleDanmuVo);
            }
        });


    }

    private void startFakeDanmu() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    SimpleDanmuVo simpleDanmuVo = SimpleDanmuVo.obtain(getRandomDanmu());
                    simpleDanmuVo.setSpeed(new Random().nextInt(2) + 3);
                    simpleDanmuVo.setDanmuColor(getRandomColor());
                    simpleDanmuVo.setDanmuTextSize(new Random().nextInt(20) + 30);
                    mXDanmukuView.enqueue(simpleDanmuVo);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public static int getRandomColor() {
        int startColor = Color.parseColor("#000000");
        int endColor = Color.parseColor("#ffffff");
        return (int) (new Random().nextFloat() * (endColor - startColor) + startColor);
    }

    public static String getRandomDanmu() {
        return SEED[new Random().nextInt(SEED.length)];
    }
}
