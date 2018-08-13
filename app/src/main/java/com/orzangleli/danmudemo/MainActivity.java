package com.orzangleli.danmudemo;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
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

public class MainActivity extends AppCompatActivity {

    Paint mPaint;
    String text = "大家好";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPaint = new Paint();
        mPaint.setColor(Color.RED);

        for (int i=20;i<80;i++) {
            mPaint.setTextSize(i);

            Path textPath = new Path();
            mPaint.getTextPath(text, 0, text.length(), 0.0f, 0.0f, textPath);
            RectF boundsPath = new RectF();
            textPath.computeBounds(boundsPath, true);

            Log.i("lxc", "text 大小为 " + i + ", boundsPath ---> " + boundsPath.height());

            Rect rect = new Rect();
            mPaint.getTextBounds(text, 0, text.length(), rect);
            Log.i("lxc", "text 大小为 " + i + ", getTextBounds ---> " + rect.height());

        }

    }

}
