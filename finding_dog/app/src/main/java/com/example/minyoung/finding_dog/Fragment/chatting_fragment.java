package com.example.minyoung.finding_dog.Fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.minyoung.finding_dog.R;

/**
 * Created by minyoung on 2018-06-28.
 */


public class chatting_fragment extends Fragment{
    private Button start_chat_btn;
    private EditText chat_my_id;
    private EditText chat_your_id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chatting, container, false);
        start_chat_btn = (Button) view.findViewById(R.id.start_chatting_btn);
        chat_my_id = (EditText) view.findViewById(R.id.chat_my_id);
        chat_your_id = (EditText) view.findViewById(R.id.chat_your_id);

        chat_my_id.setInputType(0);
        chat_your_id.setInputType(0);

        start_chat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFragment();
            }
        });
        return view;
    }

    public void switchFragment() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();

        Fragment chatroom = new chatroom_fragment();
        Bundle bundle = new Bundle(2);
        bundle.putString("myID", chat_my_id.getText().toString());
        bundle.putString("yourID", chat_your_id.getText().toString());
        chatroom.setArguments(bundle);

        fragmentTransaction.replace(R.id.mainactivity_framelayout, chatroom);
        fragmentTransaction.commit();
    }
}
