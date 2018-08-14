package com.example.minyoung.finding_dog;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SingerItemView extends LinearLayout {
    TextView textView;
    TextView textView2;
    TextView textView3;
    ImageView imageView;

    public SingerItemView(Context context){
        super(context);
        init(context);
    }

    public SingerItemView(Context context, AttributeSet attrs){
        super(context, attrs);
        init(context);
    }

    public void init(Context context){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.item, this, true);

        textView = (TextView) findViewById(R.id.manufacturer);
        textView2 = (TextView) findViewById(R.id.product);
        textView3 = (TextView) findViewById(R.id.price);
        imageView = (ImageView) findViewById(R.id.imageView);

    }

    public void setmanufacturer(String manufacturer){textView.setText("["+manufacturer+"]");}
    public void setproduct(String product){textView2.setText(product);}
    public void setprice(int price){textView3.setText("가격   "+ String.valueOf(price)+"point");}
    public void setImage(int resId){imageView.setImageResource(resId);}
}