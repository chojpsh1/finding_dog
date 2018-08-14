package com.example.minyoung.finding_dog.Fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.Switch;
import android.widget.Toast;

import com.example.minyoung.finding_dog.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by minyoung on 2018-06-28.
 */

public class setting_fragment extends Fragment{
    DatabaseReference databaseReference;
    String current_uid;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        Switch sw = (Switch)view.findViewById(R.id.switch1);

        DatabaseReference database2 = FirebaseDatabase.getInstance().getReference();
        DatabaseReference mConditionRef2=database2.child("Current_user");
        mConditionRef2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();
                DataSnapshot temp=child.next();
                String key=temp.getKey();
                current_uid=key;

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
//        DatabaseReference usersRef = ref.child("users");
        databaseReference = FirebaseDatabase.getInstance().getReference();
        //스위치의 체크 이벤트를 위한 리스너 등록
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                if(isChecked){
                    Map<String, Object> userUpdates = new HashMap<>();
                    userUpdates.put("/User/"+current_uid+"/LoseState", "True");

                    databaseReference.updateChildren(userUpdates);

                }
                else{
                    Map<String, Object> userUpdates = new HashMap<>();
                    userUpdates.put("/User/"+current_uid+"/LoseState", "False");
                    databaseReference.updateChildren(userUpdates);
                }


            }

        });
        return view;
    }



}
