package com.orzangleli.danmudemo;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.orzangleli.xdanmuku.XAdapter;

import java.util.Random;

/**
 * Created by Administrator on 2017/4/17.
 */

public class DanmuAdapter extends XAdapter<DanmuEntity> {

    final int ICON_RESOURCES[] = {R.drawable.icon1, R.drawable.icon2, R.drawable.icon3, R.drawable.icon4, R.drawable.icon5};
    Random random;


    private Context context;
    DanmuAdapter(Context c){
        super();
        context = c;
        random = new Random();
    }

    @Override
    public View getView(DanmuEntity danmuEntity, View convertView) {

        ViewHolder1 holder1 = null;
        ViewHolder2 holder2 = null;

        if(convertView == null){
            switch (danmuEntity.getType()) {
                case 0:
                    convertView = LayoutInflater.from(context).inflate(R.layout.item_danmu, null);
                    holder1 = new ViewHolder1();
                    holder1.content = (TextView) convertView.findViewById(R.id.content);
                    holder1.image = (ImageView) convertView.findViewById(R.id.image);
                    convertView.setTag(holder1);
                    break;
                case 1:
                    convertView = LayoutInflater.from(context).inflate(R.layout.item_super_danmu, null);
                    holder2 = new ViewHolder2();
                    holder2.content = (TextView) convertView.findViewById(R.id.content);
                    holder2.time = (TextView) convertView.findViewById(R.id.time);
                    convertView.setTag(holder2);
                    break;
            }
        }
        else{
            switch (danmuEntity.getType()) {
                case 0:
                    holder1 = (ViewHolder1)convertView.getTag();
                    break;
                case 1:
                    holder2 = (ViewHolder2)convertView.getTag();
                    break;
            }
        }

        switch (danmuEntity.getType()) {
            case 0:
                Glide.with(context).load(ICON_RESOURCES[random.nextInt(5)]).into(holder1.image);
                holder1.content.setText(danmuEntity.content);
                holder1.content.setTextColor(Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
                break;
            case 1:
                holder2.content.setText(danmuEntity.content);
                holder2.time.setText(danmuEntity.getTime());
                break;
        }

        return convertView;
    }

    @Override
    public int[] getViewTypeArray() {
        int type[] = {0,1};
        return type;
    }

    @Override
    public int getSingleLineHeight() {
        //将所有类型弹幕的布局拿出来，找到高度最大值，作为弹道高度
        View view = LayoutInflater.from(context).inflate(R.layout.item_danmu, null);
        //指定行高
        view.measure(0, 0);

        View view2 = LayoutInflater.from(context).inflate(R.layout.item_super_danmu, null);
        //指定行高
        view2.measure(0, 0);

//        return Math.max(view.getMeasuredHeight(),view2.getMeasuredHeight());
        return view.getMeasuredHeight();
    }


    class ViewHolder1{
        public TextView content;
        public ImageView image;
    }

    class ViewHolder2{
        public TextView content;
        public TextView time;
    }


}
