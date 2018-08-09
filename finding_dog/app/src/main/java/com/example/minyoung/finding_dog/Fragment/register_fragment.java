package com.example.minyoung.finding_dog.Fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.minyoung.finding_dog.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getApplicationContext;
import static junit.framework.Assert.assertEquals;

/**
 * Created by minyoung on 2018-06-28.
 */

public class register_fragment extends Fragment {
    Context mContext;
    EditText editTextSpecies;
    EditText editTextLocation;
    EditText editTextFeature;
    Button buttonSaveDog;
    Button buttonChoose;
    Button buttonUpload;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference firebaseDatabaseRef;
    Uri filePath;
    ImageView imageViewUpload;
    FirebaseStorage firebaseStorage;
    StorageReference firebaseStorageRef;
    String current_uid;

    public register_fragment(){
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 데이터베이스 Instance 생성
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabaseRef = firebaseDatabase.getReference();

        // 스토리지 Instance 생성
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseStorageRef = firebaseStorage.getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        mContext = container.getContext();

        // EditText 생성
        editTextSpecies = view.findViewById(R.id.editTextSpecies);
        editTextLocation = view.findViewById(R.id.editTextLocation);;
        editTextFeature = view.findViewById(R.id.editTextFeature);;

        //Button 생성
        buttonSaveDog = view.findViewById(R.id.buttonSaveDog);
        buttonChoose = view.findViewById(R.id.buttonChoose);
        buttonUpload = view.findViewById(R.id.buttonUpload);

        imageViewUpload = view.findViewById(R.id.imageViewUpload);
        //Button 클릭 기능 생성
        buttonSaveDog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDog();
            }
        });
        buttonChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Select Image
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "이미지를 선택하세요."), 0);
            }
        });
        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFile();
            }
        });
        return view;
    }

    private void saveDog() {
       //현재 사용자를 불러옴
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

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference mConditionRef=database.child("User");
        mConditionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int a=0;
                Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();

                while(child.hasNext()) {
                    DataSnapshot temp=child.next();
                    String key=temp.getKey();
                    Toast.makeText(mContext,current_uid,Toast.LENGTH_LONG).show();
                    if(key.equals(current_uid)){
                        // editText의 내용을 읽어옴
                        String species = editTextSpecies.getText().toString();
                        String location = editTextLocation.getText().toString();
                        String feature = editTextFeature.getText().toString();
                        // Dog 클래스 객체생성
                        Dog dog = new Dog(species, location, feature);
                        Map<String, Object> postValues = dog.toMap();
                        Toast.makeText(mContext,"객체 생성",Toast.LENGTH_LONG).show();
                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put("/User/"+current_uid, postValues);
                        firebaseDatabaseRef.updateChildren(childUpdates);

                        Toast.makeText(mContext,"저장 완료",Toast.LENGTH_LONG).show();

                        // 저장한 후 editText 초기화
                        editTextSpecies.setText("");
                        editTextLocation.setText("");
                        editTextFeature.setText("");
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    //결과 처리
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //request코드가 0이고 OK를 선택했고 data에 뭔가가 들어 있다면
        if(requestCode == 0 && resultCode == RESULT_OK){
            filePath = data.getData();
            try {
                //Uri 파일을 Bitmap으로 만들어서 ImageView에 집어 넣는다.
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getApplicationContext().getContentResolver(), filePath);
                // 배경이미지 삭제 후 imageView를 통해 사진 확인
                imageViewUpload.setBackgroundColor(0xFFFFFFFF);
                imageViewUpload.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //upload the file
    private void uploadFile() {
        //업로드할 파일이 있으면 수행
        if (filePath != null) {
            //업로드 진행 Dialog 보이기
            final ProgressDialog progressDialog = new ProgressDialog(mContext);
            progressDialog.setTitle("업로드중...");
            progressDialog.show();

            //Unique한 파일명을 만들자.
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMHH_mmss");
            Date now = new Date();
            String filename = formatter.format(now) + ".jpg";
            //storage 주소와 폴더 파일명을 지정해 준다.
            StorageReference storageRef = firebaseStorage.getReferenceFromUrl("gs://chatting-ed067.appspot.com").child("UID").child(filename);

            storageRef.putFile(filePath)
                    //성공시
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss(); //업로드 진행 Dialog 상자 닫기
                            Toast.makeText(mContext, "업로드 완료!", Toast.LENGTH_LONG).show();
                        }
                    })
                    //실패시
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(mContext, "업로드 실패!", Toast.LENGTH_LONG).show();
                        }
                    })
                    //진행중
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            @SuppressWarnings("VisibleForTests")
                                    double progress = (100 * taskSnapshot.getBytesTransferred()) /  taskSnapshot.getTotalByteCount();
                            //dialog에 진행률을 퍼센트로 출력해 준다
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "% ...");
                        }
                    });
        } else {
            Toast.makeText(mContext, "선택된 사진 파일이 없습니다.", Toast.LENGTH_LONG).show();
        }
    }
}
