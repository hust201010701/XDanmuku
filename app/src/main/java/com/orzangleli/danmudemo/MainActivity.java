package com.orzangleli.danmudemo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.orzangleli.xdanmuku.DanmuContainerView;
import com.orzangleli.xdanmuku.DanmuConverter;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    DanmuContainerView danmuContainerView;
    Button button;
    public String SEED = "桃树、杏树、梨树，你不让我，我不让你，都开满了花赶趟儿。红的像火，粉的像霞，白的像雪。花里带着甜味儿，闭了眼，树上仿佛已经满是桃儿、杏儿、梨儿!花下成千成百的蜜蜂嗡嗡地闹着，大小的蝴蝶飞来飞去。野花遍地是：杂样儿，有名字的，没名字的，散在草丛里像眼睛，像星星，还眨呀眨的。";
    public String doubleSeed = SEED + SEED;
    Random random;
    final int ICON_RESOURCES[] = {R.drawable.icon1, R.drawable.icon2, R.drawable.icon3, R.drawable.icon4, R.drawable.icon5};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        random = new Random();
        danmuContainerView = bindView(R.id.danmuContainerView);
        button = bindView(R.id.button);

        DanmuConverter danmuConverter = new DanmuConverter<DanmuEntity>() {
            @Override
            public int getSingleLineHeight() {
                //将所有类型弹幕的布局拿出来，找到高度最大值，作为弹道高度
                View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.item_danmu, null);
                //指定行高
                view.measure(0, 0);

                View view2 = LayoutInflater.from(MainActivity.this).inflate(R.layout.item_super_danmu, null);
                //指定行高
                view2.measure(0, 0);

                return Math.max(view.getMeasuredHeight(),view2.getMeasuredHeight());
            }

            @Override
            public View convert(DanmuEntity model) {
                View view = null;
                if(model.getType() == 0) {
                    view = LayoutInflater.from(MainActivity.this).inflate(R.layout.item_danmu, null);
                    TextView content = (TextView) view.findViewById(R.id.content);
                    ImageView image = (ImageView) view.findViewById(R.id.image);
                    image.setImageResource(ICON_RESOURCES[random.nextInt(5)]);
                    content.setText(model.content);
                    content.setTextColor(Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
                }
                else if(model.getType() == 1) {
                    view = LayoutInflater.from(MainActivity.this).inflate(R.layout.item_super_danmu, null);
                    TextView content = (TextView) view.findViewById(R.id.content);
                    content.setText(model.content);
                    TextView time = (TextView) view.findViewById(R.id.time);
                    time.setText(model.getTime());
                }
                return view;
            }
        };

        danmuContainerView.setConverter(danmuConverter);
        danmuContainerView.setSpeed(DanmuContainerView.HIGH_SPEED);

        danmuContainerView.setGravity(DanmuContainerView.GRAVITY_TOP | DanmuContainerView.GRAVITY_CENTER | DanmuContainerView.GRAVITY_BOTTOM);


        //弹幕点击事件
        danmuContainerView.setOnItemClickListener(new DanmuContainerView.OnItemClickListener<DanmuEntity>() {
            @Override
            public void onItemClick(DanmuEntity danmuEntity) {
                Toast.makeText(MainActivity.this,danmuEntity.content,Toast.LENGTH_SHORT).show();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int index = random.nextInt(SEED.length());
                DanmuEntity danmuEntity = new DanmuEntity();
                danmuEntity.setContent(doubleSeed.substring(index, index + 2 + random.nextInt(20)));
                danmuEntity.setType(random.nextInt(2));
                danmuEntity.setTime("23:20:11");
                try {
                    danmuContainerView.addDanmu(danmuEntity);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }


    public <T extends View> T bindView(int id) {
        return (T) this.findViewById(id);
    }


}
