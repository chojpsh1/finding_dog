package com.example.minyoung.finding_dog.Fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Switch;
import android.widget.Toast;

import com.example.minyoung.finding_dog.LoginActivity;
import com.example.minyoung.finding_dog.MainActivity;
import com.example.minyoung.finding_dog.R;
import com.google.firebase.auth.FirebaseAuth;
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
    EditText user_edit;
    Button logout_btn;
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_setting, container, false);
        Switch sw = (Switch) view.findViewById(R.id.switch1);
        user_edit = (EditText) view.findViewById(R.id.user_edittext);
        logout_btn= (Button) view.findViewById(R.id.logout_btn);

        logout_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Start your activity here
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(view.getContext(), LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

        DatabaseReference database2 = FirebaseDatabase.getInstance().getReference();
        DatabaseReference mConditionRef2=database2.child("Current_user");
        mConditionRef2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();
                DataSnapshot temp=child.next();
                String key = temp.getKey();
                current_uid = key;
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
                if (isChecked){
                    Map<String, Object> userUpdates = new HashMap<>();
                    userUpdates.put("/User/"+current_uid+"/LoseState", "True");

                    databaseReference.updateChildren(userUpdates);

                }
                else {
                    Map<String, Object> userUpdates = new HashMap<>();
                    userUpdates.put("/User/"+current_uid+"/LoseState", "False");
                    databaseReference.updateChildren(userUpdates);
                }


            }

        });

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference mConditionRef=database.child("User");

        mConditionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();

                while(child.hasNext()) {
                    DataSnapshot temp=child.next();
                    String key=temp.getKey();
                    if(key.equals(current_uid)){
                        String str=temp.child("uid").getValue(String.class);
                        user_edit.setText(str);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return view;
    }

    private void finish() {
        finish();
    }


}
