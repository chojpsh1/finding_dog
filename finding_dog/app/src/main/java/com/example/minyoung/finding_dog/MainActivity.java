package com.example.minyoung.finding_dog;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.MenuItem;
import com.example.minyoung.finding_dog.Fragment.chatting_fragment;
import com.example.minyoung.finding_dog.Fragment.register_fragment;
import com.example.minyoung.finding_dog.Fragment.search_fragment;
import com.example.minyoung.finding_dog.Fragment.setting_fragment;
import com.example.minyoung.finding_dog.Fragment.shop_fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity{
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        getFragmentManager().beginTransaction().replace(R.id.mainactivity_framelayout,new register_fragment()).commit();
//        Intent intent = getIntent();
//        String uid = intent.getExtras().getString("uid");
        Log.e("TAG", mAuth.getUid());
        Log.e("TAG", user.getEmail());

        FirebaseUser a = mAuth.getCurrentUser();
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
