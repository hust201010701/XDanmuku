package com.orzangleli.danmudemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.orzangleli.douyu.DyDanmuManager;
import com.orzangleli.douyu.client.IReceiveDanmu;
import com.orzangleli.xdanmuku.DanmuContainerView;
import com.orzangleli.xdanmuku.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements IReceiveDanmu{

    private final int ROOM_ID = 4809;

    DanmuContainerView danmuContainerView;
    Button button;
    public String SEED[] = {"桃树、杏树、梨树，你不让我", "，都开满了花赶趟儿。红的像火，", "花里带着甜味儿，闭了眼，树上", "满是桃儿、杏儿、梨儿!花下成", "嗡地闹着，大小的蝴蝶"};
    Random random;
    final int ICON_RESOURCES[] = {R.drawable.icon1, R.drawable.icon2, R.drawable.icon3, R.drawable.icon4, R.drawable.icon5};

    // 模拟视频时长为100秒
    private final int VIDEO_DURATION = 100 * 1000;

    private SeekBar mSeekBar;

    private TextView mProgressTv;

    private int mCurrentProgress;

    private PlayThread mPlayThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        random = new Random();
        danmuContainerView = bindView(R.id.danmuContainerView);
        button = bindView(R.id.button);
        mSeekBar = bindView(R.id.progress);
        mProgressTv = bindView(R.id.progressTv);

        mSeekBar.setMax(VIDEO_DURATION);
        mProgressTv.setText("0/" + VIDEO_DURATION);


        DanmuAdapter danmuAdapter = new DanmuAdapter(MainActivity.this);
        danmuContainerView.setAdapter(danmuAdapter);

        danmuContainerView.setSpeed(DanmuContainerView.NORMAL_SPEED);

        danmuContainerView.setGravity(DanmuContainerView.GRAVITY_FULL);

        //弹幕点击事件
        danmuContainerView.setOnItemClickListener(new DanmuContainerView.OnItemClickListener() {
            @Override
            public void onItemClick(Model model) {
                DanmuEntity danmuEntity = (DanmuEntity) model;
                Toast.makeText(MainActivity.this, danmuEntity.content, Toast.LENGTH_SHORT).show();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Model> danmuEntities = new ArrayList<>();
                for (int i = 0; i < 200; i++) {
                    DanmuEntity danmuEntity = new DanmuEntity();
                    danmuEntity.setContent("弹幕"+i);
                    danmuEntity.setType(0);
                    danmuEntity.setTime("23:20:11");
                    danmuEntity.setShowTime((long) (VIDEO_DURATION * random.nextFloat()));
                    danmuEntities.add(danmuEntity);
//                    danmuContainerView.addDanmu(danmuEntity);
                }
                danmuContainerView.addDanmuIntoCachePool(danmuEntities);
                simulatePlayVideo();
                button.setEnabled(false);
            }
        });


        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    danmuContainerView.resetDanmuProgress();
                } else {
                    danmuContainerView.onProgress(progress);
                }
                mProgressTv.setText(progress + "/" + VIDEO_DURATION);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mPlayThread.setPause(true);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mPlayThread.setPause(false);
            }
        });


        DyDanmuManager.getInstance().init(ROOM_ID, this);

    }

    public void simulatePlayVideo() {
        mPlayThread = new PlayThread();
        mPlayThread.start();
    }

    @Override
    public void receive(String name, String content) {
        Log.d("lxc", "receive() called with: name = [" + name + "], content = [" + content + "]");
    }

    private class PlayThread extends Thread {
        private boolean pause;

        public void setPause(boolean pause) {
            this.pause = pause;
        }

        @Override
        public void run() {
            super.run();
            while ((mCurrentProgress = mSeekBar.getProgress()) < VIDEO_DURATION) {
                if (!pause) {
                    mCurrentProgress += 10;
                    mCurrentProgress = mCurrentProgress > VIDEO_DURATION ? VIDEO_DURATION : mCurrentProgress;
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mSeekBar.setProgress(mCurrentProgress);
                        }
                    });
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }



    public <T extends View> T bindView(int id) {
        return (T) this.findViewById(id);
    }


}
