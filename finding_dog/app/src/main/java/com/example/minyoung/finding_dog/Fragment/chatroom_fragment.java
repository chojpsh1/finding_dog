package com.example.minyoung.finding_dog.Fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.minyoung.finding_dog.ChatMessage;
import com.example.minyoung.finding_dog.ChatArrayAdapter;
import com.example.minyoung.finding_dog.DbOpenHelper;
import com.example.minyoung.finding_dog.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

import static android.app.Activity.RESULT_OK;


public class chatroom_fragment extends Fragment {
    private TextView textView;
    private EditText chatText;
    private Button buttonSend;
    private Button buttonSendPicture;
    private TextView back_fragment;

    private Button buttonPicture;
    private Button buttonPictureCancel;

    private ListView listView;
    private ChatArrayAdapter chatArrayAdapter;

    private String myID;
    private String yourID;

    private DatabaseReference databaseReference;
    private ChildEventListener childEventListener;

    private DbOpenHelper mDbOpenHelper;
    private Cursor mCursor;

    //사진 경로
    private Uri filePath;
    private ImageView chatImg;

    //Storage
    private FirebaseStorage storage;

    public String filename;

    public StorageReference storageRef;

    public LinkedList receive_msg;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chatroom, container, false);

        setHasOptionsMenu(true);

        listView = (ListView) view.findViewById(R.id.msg_container);
        chatText = (EditText) view.findViewById(R.id.chat_content);

        buttonSend = (Button) view.findViewById(R.id.chat_confirm);
        buttonSendPicture = view.findViewById(R.id.chat_confirm_picture);

        buttonPicture = (Button) view.findViewById(R.id.chat_picture);
        buttonPictureCancel = view.findViewById(R.id.chat_picture_cancel);

        chatImg = view.findViewById(R.id.chat_img);

        textView = (TextView) view.findViewById(R.id.chat_partner);
        myID = getArguments().getString("myID").replace("@","").replace(".","");
        yourID = getArguments().getString("yourID").replace("@","").replace(".","");

        chatArrayAdapter = new ChatArrayAdapter(view.getContext(), R.layout.chatmessage);
        listView.setAdapter(chatArrayAdapter);

        textView.setText("To " + yourID);

        back_fragment = view.findViewById(R.id.back_fragment);
        back_fragment.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                Fragment chat = new chatting_fragment();

                fragmentTransaction.replace(R.id.mainactivity_framelayout, chat);
                fragmentTransaction.commit();
            }
        });

        // sqlite DB Create and Open
        mDbOpenHelper = new DbOpenHelper(view.getContext());
        mDbOpenHelper.open();
        mDbOpenHelper.create(yourID);

        updateChatMessage();
        receive_msg = new LinkedList();

        //파이어 베이스 권한
        databaseReference = FirebaseDatabase.getInstance().getReference();
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.hasChild("image")){
                    Log.i("@@@@@@@@@@@@@ image : ", dataSnapshot.child("image").getValue().toString());
                    receiveChatImage(dataSnapshot.child("image").getValue().toString());
                    databaseReference.child("chat").child(myID).child(yourID).child(dataSnapshot.getKey()).setValue(null);
                }
                else {
                    Log.i("@@@@@@@@@@@@@ chat : ", dataSnapshot.getValue().toString());
                    receiveChatMessage(dataSnapshot.getValue().toString());
                    databaseReference.child("chat").child(myID).child(yourID).child(dataSnapshot.getKey()).setValue(null);
                }
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

        // firebase storage
        storage = FirebaseStorage.getInstance();

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

        buttonSendPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                sendChatImage();
            }
        });

        buttonPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "이미지를 선택하세요."), 0);
            }
        });

        buttonPictureCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                buttonPicture.setVisibility(View.VISIBLE);
                buttonPictureCancel.setVisibility(View.GONE);
                chatImg.setVisibility(View.GONE);
                chatText.setVisibility(View.VISIBLE);

                buttonSend.setVisibility(View.VISIBLE);
                buttonSendPicture.setVisibility(View.GONE);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && resultCode == RESULT_OK){
            filePath = data.getData();
            try {
                //Uri 파일을 Bitmap으로 만들어서 ImageView에 집어 넣는다.
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getActivity().getContentResolver(), filePath);
                chatImg.setImageBitmap(bitmap);
                chatImg.setVisibility(View.VISIBLE);
                chatText.setVisibility(View.INVISIBLE);
                buttonPictureCancel.setVisibility(View.VISIBLE);
                buttonPicture.setVisibility(View.GONE);
                buttonSend.setVisibility(View.GONE);
                buttonSendPicture.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void reconnectingFirebase(){
        databaseReference.child("chat").child(myID).child(yourID).removeEventListener(childEventListener);

        //파이어 베이스 권한
        databaseReference = FirebaseDatabase.getInstance().getReference();
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.hasChild("image")){
                    Log.i("@@@@@@@@@@@@@ image : ", dataSnapshot.child("image").getValue().toString());
                    receiveChatImage(dataSnapshot.child("image").getValue().toString());
                    databaseReference.child("chat").child(myID).child(yourID).child(dataSnapshot.getKey()).setValue(null);
                }
                else {
                    Log.i("@@@@@@@@@@@@@ chat : ", dataSnapshot.getValue().toString());
                    receiveChatMessage(dataSnapshot.getValue().toString());
                    databaseReference.child("chat").child(myID).child(yourID).child(dataSnapshot.getKey()).setValue(null);
                }
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
    }

    @Override
    public void onPause() {
        super.onPause();

        mDbOpenHelper.close();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private boolean sendChatMessage(){
        chatArrayAdapter.add(new ChatMessage(false, chatText.getText().toString()));
        Toast.makeText(this.getActivity(), chatText.getText().toString(), Toast.LENGTH_SHORT).show();
        Log.i("@@@@@@@@@@@@@@@@@@@@@", chatText.getText().toString());

        // firebase에 저장
        databaseReference.child("chat").child(yourID).child(myID).push().setValue(chatText.getText().toString());

        // local db에 저장
        mDbOpenHelper.open();
        mDbOpenHelper.insertColumn(yourID, 0, 0, "msg",chatText.getText().toString(), null);
        Log.i("input : msg", chatText.getText().toString());
        chatText.setText("");
        return true;
    }

    private boolean receiveChatMessage(String msg){
        chatArrayAdapter.add(new ChatMessage(true, msg));

        // local db에 저장
        mDbOpenHelper.open();
        mDbOpenHelper.insertColumn(yourID, 1, 0, "msg", msg, null);
        Log.i("input : msg", msg);

        return true;
    }

    private boolean sendChatImage(){
        Drawable d = chatImg.getDrawable();
        Bitmap bitmap = ((BitmapDrawable)d).getBitmap();

        chatArrayAdapter.add(new ChatMessage(false, bitmap));

        //Unique한 파일명을 만들자.
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMHH_mmss");
        Date now = new Date();
        filename = myID + formatter.format(now)  + yourID + ".png";
        //storage 주소와 폴더 파일명을 지정해 준다.
        StorageReference upload_storageRef = storage.getReferenceFromUrl("gs://chatting-ed067.appspot.com").child("images").child(filename);

        final Context mContext = this.getActivity();

        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setTitle("전송중...");
        progressDialog.show();

        upload_storageRef.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.dismiss(); //업로드 진행 Dialog 상자 닫기
                Toast.makeText(mContext, "전송 완료!", Toast.LENGTH_LONG).show();
                databaseReference.child("chat").child(yourID).child(myID).push().child("image").setValue(filename);

                reconnectingFirebase();
            }
        })
                //실패시
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(mContext, "전송 실패!", Toast.LENGTH_LONG).show();
                    }
                })
                //진행중
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        @SuppressWarnings("VisibleForTests")
                        double progress = (100 * taskSnapshot.getBytesTransferred()) /  taskSnapshot.getTotalByteCount();
                        //dialog에 진행률을 퍼센트로 출력해 준다
                        progressDialog.setMessage("전송중 " + ((int) progress) + "% ...");
                    }
                });

        mDbOpenHelper.open();
        mDbOpenHelper.insertColumn(yourID, 0, 0, "picture", "", bitmap);

        buttonPicture.setVisibility(View.VISIBLE);
        buttonPictureCancel.setVisibility(View.GONE);
        chatImg.setVisibility(View.GONE);
        chatText.setVisibility(View.VISIBLE);

        buttonSend.setVisibility(View.VISIBLE);
        buttonSendPicture.setVisibility(View.GONE);


        return true;
    }

    private boolean receiveChatImage(String img){
        storageRef = storage.getReferenceFromUrl("gs://chatting-ed067.appspot.com");
        StorageReference download_storageRef = storageRef.child("images/" + img);

        final long ONE_MEGABYTE = 1024 * 1024;
        download_storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length) ;
                chatArrayAdapter.add(new ChatMessage(true, bitmap));
                mDbOpenHelper.insertColumn(yourID, 1, 0, "picture", null, bitmap);

                reconnectingFirebase();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

        download_storageRef.delete();

        return true;
    }
    private void updateChatMessage(){
        mCursor = null;
        if((mCursor = mDbOpenHelper.selectColumns(yourID)) != null){
            ChatMessage chatMessage;
            while (mCursor.moveToNext()) {
                boolean side = mCursor.getInt(mCursor.getColumnIndex("side")) == 1;
                boolean type = mCursor.getString(mCursor.getColumnIndex("type")).contains("msg");
                Log.i("@@@@@@@@@@@@@@@@@@@@@", mCursor.getString(mCursor.getColumnIndex("type")) + type);
                if(type){
                    chatMessage = new ChatMessage(side, mCursor.getString(mCursor.getColumnIndex("msg")));
                    chatArrayAdapter.add(chatMessage);
                }
                else{
                    byte[] byteArray = mCursor.getBlob(mCursor.getColumnIndex("img"));
                    Bitmap bitmap = BitmapFactory.decodeByteArray( byteArray, 0, byteArray.length ) ;

                    chatMessage = new ChatMessage(side, bitmap);
                    chatArrayAdapter.add(chatMessage);
                }
            }

            mCursor.close();
        }
    }
}
