package com.orzangleli.danmudemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.orzangleli.xdanmuku.DanmuContainerView;
import com.orzangleli.xdanmuku.Model;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    DanmuContainerView danmuContainerView;
    Button button;
    public String SEED[] = {"桃树、杏树、梨树，你不让我","，都开满了花赶趟儿。红的像火，","花里带着甜味儿，闭了眼，树上","满是桃儿、杏儿、梨儿!花下成","嗡地闹着，大小的蝴蝶"};
    Random random;
    final int ICON_RESOURCES[] = {R.drawable.icon1, R.drawable.icon2, R.drawable.icon3, R.drawable.icon4, R.drawable.icon5};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        random = new Random();
        danmuContainerView = bindView(R.id.danmuContainerView);
        button = bindView(R.id.button);

        DanmuAdapter danmuAdapter = new DanmuAdapter(MainActivity.this);
        danmuContainerView.setAdapter(danmuAdapter);

        danmuContainerView.setSpeed(DanmuContainerView.HIGH_SPEED);

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
                for (int i = 0; i < 1; i++) {
                    DanmuEntity danmuEntity = new DanmuEntity();
                    danmuEntity.setContent(SEED[random.nextInt(5)]);
                    danmuEntity.setType(0);
                    danmuEntity.setTime("23:20:11");
                    danmuContainerView.addDanmu(danmuEntity);
                }
            }
        });
    }


    public <T extends View> T bindView(int id) {
        return (T) this.findViewById(id);
    }


}
