package com.example.minyoung.finding_dog.Fragment;

import android.app.Fragment;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.minyoung.finding_dog.ChatMessage;
import com.example.minyoung.finding_dog.ChatArrayAdapter;
import com.example.minyoung.finding_dog.DbOpenHelper;
import com.example.minyoung.finding_dog.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class chatroom_fragment extends Fragment {
    private TextView textView;
    private EditText chatText;
    private Button buttonSend;
    private ListView listView;
    private ChatArrayAdapter chatArrayAdapter;

    private String myID;
    private String yourID;

    private DatabaseReference databaseReference;
    private ChildEventListener childEventListener;

    private DbOpenHelper mDbOpenHelper;
    private Cursor mCursor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chatroom, container, false);

        listView = (ListView) view.findViewById(R.id.msg_container);
        chatText = (EditText) view.findViewById(R.id.chat_content);
        chatText.setInputType(0);
        buttonSend = (Button) view.findViewById(R.id.chat_confirm);
        textView = (TextView) view.findViewById(R.id.check);
        myID = getArguments().getString("myID");
        yourID = getArguments().getString("yourID");

        chatArrayAdapter = new ChatArrayAdapter(view.getContext(), R.layout.chatmessage);
        listView.setAdapter(chatArrayAdapter);

        textView.setText(myID + "와 " + yourID + "의 채팅방");

        // sqlite DB Create and Open
        mDbOpenHelper = new DbOpenHelper(view.getContext());
        mDbOpenHelper.open();
        mDbOpenHelper.create(yourID);

        updateChatMessage();

        //파이어 베이스 권한
        databaseReference = FirebaseDatabase.getInstance().getReference();
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                receiveChatMessage(dataSnapshot.getValue().toString());
                databaseReference.child("chat").child(myID).child(yourID).child(dataSnapshot.getKey()).setValue(null);
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        //디비에 올라가면 채팅 갱신
        databaseReference.child("chat").child(myID).child(yourID).addChildEventListener(childEventListener);

        //엔터 눌렀을 때 전송
        chatText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    return sendChatMessage();
                }
                return false;
            }
        });

        //버튼 눌렀을 때 전송
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                sendChatMessage();
            }
        });

        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setAdapter(chatArrayAdapter);

        //to scroll the list view to bottom on data change
        chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(chatArrayAdapter.getCount() - 1);
            }
        });

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        databaseReference.child("chat").child(myID).child(yourID).removeEventListener(childEventListener);

        mDbOpenHelper.close();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private boolean sendChatMessage(){
        chatArrayAdapter.add(new ChatMessage(false, chatText.getText().toString()));

        // firebase에 저장
        databaseReference.child("chat").child(yourID).child(myID).push().setValue(chatText.getText().toString());

        // local db에 저장
        mDbOpenHelper.insertColumn(yourID, 0, 0, chatText.getText().toString());
        Log.i("input : msg", chatText.getText().toString());
        chatText.setText("");
        return true;
    }

    private boolean receiveChatMessage(String msg){
        chatArrayAdapter.add(new ChatMessage(true, msg));

        // local db에 저장
        mDbOpenHelper.insertColumn(yourID, 1, 0, msg);
        Log.i("input : msg", msg);

        return true;
    }

    private void updateChatMessage(){
        mCursor = null;
        if((mCursor = mDbOpenHelper.selectColumns(yourID)) != null){
            ChatMessage chatMessage;
            while (mCursor.moveToNext()) {
                boolean side = mCursor.getInt(mCursor.getColumnIndex("side")) == 1;
                chatMessage = new ChatMessage(side, mCursor.getString(mCursor.getColumnIndex("msg")));
                chatArrayAdapter.add(chatMessage);

                Log.i("M : "," 뭐가 문제냐");
            }

            mCursor.close();
        }
    }
}
