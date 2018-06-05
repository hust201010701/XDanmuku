package com.orzangleli.danmudemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.orzangleli.douyu.DyDanmuManager;
import com.orzangleli.douyu.client.IReceiveDanmu;
import com.orzangleli.xdanmuku.ui.XDanmukuView;
import com.orzangleli.xdanmuku.vo.SimpleDanmuVo;

public class DanmuActivity extends AppCompatActivity {

    XDanmukuView mXDanmukuView;

    private final int ROOM_ID = 2132902;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_danmu);

        mXDanmukuView = this.findViewById(R.id.xdanmukuView);

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (true) {
//                    SimpleDanmuVo simpleDanmuVo = SimpleDanmuVo.obtain("time = "+System.currentTimeMillis());
//                    mXDanmukuView.enqueue(simpleDanmuVo);
//                    try {
//                        Thread.sleep(10);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }).start();

        DyDanmuManager.getInstance().init(ROOM_ID, new IReceiveDanmu() {
            @Override
            public void receive(String name, String content) {
                Log.i("lxc", name + " : " +content);
                SimpleDanmuVo simpleDanmuVo = SimpleDanmuVo.obtain(name + " : " +content);
                mXDanmukuView.enqueue(simpleDanmuVo);
            }
        });


    }
}
