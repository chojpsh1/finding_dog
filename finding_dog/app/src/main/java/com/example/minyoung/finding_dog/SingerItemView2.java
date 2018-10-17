package com.example.minyoung.finding_dog;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by user on 2017-10-26.
 */

public class SingerItemView2 extends LinearLayout {
    TextView textView;
    TextView textView2;
    TextView textView3;
    ImageView imageView;

    public SingerItemView2(Context context) {
        super(context);
        init(context);
    }

    public SingerItemView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.singer_item, this, true);

        textView = (TextView) findViewById(R.id.dog_species);
        textView2 = (TextView) findViewById(R.id.dog_location);
        textView3 = (TextView) findViewById(R.id.dog_feature);
        imageView = (ImageView) findViewById(R.id.imageView);
    }

    public void setName(String name) { textView.setText("종 : "+name); }
    public void setLocation(String location) { textView2.setText("지역 : "+location); }
    public void setFeature(String feature) { textView3.setText("특징 : "+feature); }
    public void setImage(int resId) { imageView.setImageResource(resId); }

}