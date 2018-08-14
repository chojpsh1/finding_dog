package com.example.minyoung.finding_dog;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.kakao.auth.ErrorCode;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

import org.json.JSONObject;

import java.util.Iterator;

public class LoginActivity extends AppCompatActivity {
    private Button login;
    private Button singup;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    //현재 접속중인 id 저장하기
    private DatabaseReference databaseReference2;
    private EditText loginActivity_edittext_id;
    String user_email[];

    //페이스북 로그인을 위한 변수
    private static final String TAG = "LoginActivity";
    private CallbackManager callbackManager;
    private com.facebook.login.widget.LoginButton btn_facebook_login;

    //카카오톡 로그인을 위한 변수
//    private Button btn_custom_login;
    private com.kakao.usermgmt.LoginButton btn_kakao;
    private SessionCallback callback;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mFirebaseRemoteConfig=FirebaseRemoteConfig.getInstance();
        loginActivity_edittext_id=(EditText)findViewById(R.id.loginActivity_edittext_id);

        String splash_background=mFirebaseRemoteConfig.getString("splash_background");


        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            getWindow().setStatusBarColor(Color.parseColor(splash_background));
        }
        //카카오톡 연동
        callback = new SessionCallback(); //세션콜백을 부르고
        Session.getCurrentSession().addCallback(callback); // 추가시키면 끝입니다!!
        btn_kakao =(com.kakao.usermgmt.LoginButton)findViewById(R.id.btn_kakao_login);
//        btn_custom_login.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                btn_kakao_login.performClick();
//            }
//        });
//        btn_kakao_login = (com.kakao.usermgmt.LoginButton) findViewById(R.id.btn_kakao_login);


        firebaseAuth = firebaseAuth.getInstance();

        login = (Button) findViewById(R.id.loginActivity_button_login);
        singup = (Button) findViewById(R.id.loginActivity_button_signup);
        login.setBackgroundColor(Color.parseColor(splash_background));
        singup.setBackgroundColor(Color.parseColor(splash_background));

        login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                databaseReference2 = FirebaseDatabase.getInstance().getReference("Current_user");
                databaseReference2.setValue(null);
                user_email=loginActivity_edittext_id.getText().toString().split("@");
                databaseReference2.child(user_email[0]).child("uid").setValue(loginActivity_edittext_id.getText().toString());
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        singup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                startActivity(new Intent(LoginActivity.this,SignupActivity.class));
            }
        });

        //페이스북 로그인 연동
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();
        btn_facebook_login = findViewById(R.id.btn_facebook_login);

        btn_facebook_login.setReadPermissions("email");

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                databaseReference = FirebaseDatabase.getInstance().getReference("User");
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {

                        try {
                            final String facebook_email=object.getString("email");
                            user_email=facebook_email.split("@");
                            //아이디 등록 영역
                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    int a=0;
                                    Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();

                                    while(child.hasNext()) {
                                        DataSnapshot temp=child.next();
                                        String key=temp.getKey();
                                        if(key.equals(user_email[0])){
                                            a=1;

                                        }
                                    }

                                    if(a==0){
                                        //email저장
                                        databaseReference.child(user_email[0]).child("uid").setValue(facebook_email);
                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
                            //현재 접속중인 사용자 id 저장하기
                            databaseReference = FirebaseDatabase.getInstance().getReference("Current_user");
                            databaseReference.setValue(null);
                            databaseReference2 = FirebaseDatabase.getInstance().getReference("Current_user");
                            databaseReference2.child(user_email[0]).child("uid").setValue(facebook_email);

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);

                        }catch (Exception e){

                        }
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "email");
                request.setParameters(parameters);
                request.executeAsync();

            }
            @Override
            public void onCancel() {
                // App code
                Log.d(TAG, "onCancel");
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Log.d(TAG, "onError");
            }
        });

    }


    private void loginEmail(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("", "signInWithEmail:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            // updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("", "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            // updateUI(null);
                        }

                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
    //카카오톡 로그인 연동
    private class SessionCallback implements ISessionCallback {
        @Override
        public void onSessionOpened() {
            UserManagement.getInstance().requestMe(new MeResponseCallback() {
                @Override
                public void onFailure(ErrorResult errorResult) {
                    String message = "failed to get user info. msg=" + errorResult;
                    Logger.d(message);

                    ErrorCode result = ErrorCode.valueOf(errorResult.getErrorCode());
                    if (result == ErrorCode.CLIENT_ERROR_CODE) {
                        finish();
                    } else {
                        //redirectMainActivity();
                    }
                }

                @Override
                public void onSessionClosed(ErrorResult errorResult) {
                }

                @Override
                public void onNotSignedUp() {
                }
                @Override
                public void onSuccess(UserProfile userProfile) {
                    //로그인에 성공하면 로그인한 사용자의 일련번호, 닉네임, 이미지url등을 리턴합니다.
                    //사용자 ID는 보안상의 문제로 제공하지 않고 일련번호는 제공합니다.
                    Log.e("UserProfile", userProfile.toString());
                    String kakao_email = userProfile.getEmail();
                    user_email=kakao_email.split("@");
                    databaseReference = FirebaseDatabase.getInstance().getReference("User");
                    databaseReference.child(user_email[0]).child("uid").setValue(kakao_email);

                    databaseReference2 = FirebaseDatabase.getInstance().getReference("Current_user");
                    databaseReference2.setValue(null);
                    databaseReference2.child(user_email[0]).child("uid").setValue(user_email);
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                    intent.putExtra("uid", kakao_email);
                    startActivity(intent);
                    finish();
                }
            });

        }
        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            // 세션 연결이 실패했을때
            // 어쩔때 실패되는지는 테스트를 안해보았음 ㅜㅜ
        }
    }

}