package com.example.minyoung.finding_dog;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class FirstActivity extends AppCompatActivity {

    ImageView foot;
    Button register;
    Button search;
    Button shopping;
    Button chatting;
    Button setting;
    int height=0;
    int width=0;

    RelativeLayout layout1;
    LinearLayout layout2;
    LinearLayout layout3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        layout1=findViewById(R.id.first_layout_1);
        layout2=findViewById(R.id.first_layout_2);
        layout3=findViewById(R.id.first_layout_3);

        foot=findViewById(R.id.foot);
        register=findViewById(R.id.register_btn);
        search=findViewById(R.id.btn_search);
        shopping=findViewById(R.id.shopping_btn);
        chatting=findViewById(R.id.chatting_btn);
        setting=findViewById(R.id.setting_btn);



        register.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FirstActivity.this, MainActivity.class);
                intent.putExtra("next","register_fragment");
                startActivity(intent);

            }
        }) ;
        search.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FirstActivity.this, MainActivity.class);
                intent.putExtra("next","search_fragment");
                startActivity(intent);

            }
        }) ;
        shopping.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FirstActivity.this, MainActivity.class);
                intent.putExtra("next","shopping_fragment");
                startActivity(intent);

            }
        }) ;
        chatting.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FirstActivity.this, MainActivity.class);
                intent.putExtra("next","chatting_fragment");
                startActivity(intent);

            }
        }) ;
        setting.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FirstActivity.this, MainActivity.class);
                intent.putExtra("next","setting_fragment");
                startActivity(intent);
            }
        }) ;


        for (int i = 0; i < 560; i+=20) {
            foot.animate().translationX(i).setDuration(3000).withLayer();
        }



    }


}
