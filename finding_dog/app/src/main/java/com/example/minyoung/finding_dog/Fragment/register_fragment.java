package com.example.minyoung.finding_dog.Fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
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

    public register_fragment(){
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabaseRef = firebaseDatabase.getReference();
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
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously


        String species = editTextSpecies.getText().toString();
        String location = editTextLocation.getText().toString();
        String feature = editTextFeature.getText().toString();
        Dog dog = new Dog(species, location, feature);
        Map<String, Object> postValues = dog.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/UID/", postValues);
        firebaseDatabaseRef.updateChildren(childUpdates);

        Toast.makeText(mContext,"저장 완료",Toast.LENGTH_LONG).show();
        editTextSpecies.setText("");
        editTextLocation.setText("");
        editTextFeature.setText("");
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
            StorageReference storageRef = firebaseStorage.getReferenceFromUrl("gs://chatting-ed067.appspot.com").child(filename);

            //올라가거라...
            storageRef.putFile(filePath)
                    //성공시
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss(); //업로드 진행 Dialog 상자 닫기
                            Toast.makeText(mContext, "업로드 완료!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    //실패시
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(mContext, "업로드 실패!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    //진행중
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            @SuppressWarnings("VisibleForTests") //이걸 넣어 줘야 아랫줄에 에러가 사라진다. 넌 누구냐?
                                    double progress = (100 * taskSnapshot.getBytesTransferred()) /  taskSnapshot.getTotalByteCount();
                            //dialog에 진행률을 퍼센트로 출력해 준다
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "% ...");
                        }
                    });
        } else {
            Toast.makeText(mContext, "파일을 먼저 선택하세요.", Toast.LENGTH_SHORT).show();
        }
    }
}
