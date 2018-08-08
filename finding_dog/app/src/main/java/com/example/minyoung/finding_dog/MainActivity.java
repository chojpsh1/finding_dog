package com.example.minyoung.finding_dog;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.MenuItem;
import com.example.minyoung.finding_dog.Fragment.chatting_fragment;
import com.example.minyoung.finding_dog.Fragment.register_fragment;
import com.example.minyoung.finding_dog.Fragment.search_fragment;
import com.example.minyoung.finding_dog.Fragment.setting_fragment;
import com.example.minyoung.finding_dog.Fragment.shop_fragment;


public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNavigationView=(BottomNavigationView)findViewById(R.id.mainactivity_bottomnavigationview);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@Nullable MenuItem item){
                switch (item.getItemId()) {
                    case R.id.pet_register:
                        getFragmentManager().beginTransaction().replace(R.id.mainactivity_framelayout,new register_fragment()).commit();
                        break;
                    case R.id.search:
                        getFragmentManager().beginTransaction().replace(R.id.mainactivity_framelayout,new search_fragment()).commit();
                        break;
                    case R.id.chatting:
                        getFragmentManager().beginTransaction().replace(R.id.mainactivity_framelayout,new chatting_fragment()).commit();
                        break;
                    case R.id.shop:
                        getFragmentManager().beginTransaction().replace(R.id.mainactivity_framelayout,new shop_fragment()).commit();
                        break;
                    case R.id.setting:
                        getFragmentManager().beginTransaction().replace(R.id.mainactivity_framelayout,new setting_fragment()).commit();
                        break;
                }
                return true;
            }
        });
    }
}
