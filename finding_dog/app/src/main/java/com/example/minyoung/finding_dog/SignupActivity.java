package com.example.minyoung.finding_dog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity{
    final String TAG = "SignupActivity";
    String user_email[];
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference databaseReference;
    Context mContext;
    private EditText editTextEmail;
    private EditText editTextName;
    private EditText editTextPassword;
    private Button buttonSignUp;

    //String current_uid;
    private FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference firebaseDatabaseRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mContext = this;

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextName = findViewById(R.id.editTextName);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonSignUp = findViewById(R.id.buttonSignUp);

        mAuth = FirebaseAuth.getInstance();
        buttonSignUp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString();
                String pass = editTextPassword.getText().toString();
                createUser(email, pass);
            }
        });
    }

    public void createUser(final String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(mContext, "Authentication successed.",
                                    Toast.LENGTH_SHORT).show();


                            // 데이터베이스 Instance 생성
                            firebaseDatabase = FirebaseDatabase.getInstance();
                            firebaseDatabaseRef = firebaseDatabase.getReference();
                            // 로그인 계정정보
                            mAuth = FirebaseAuth.getInstance();
                            user = mAuth.getCurrentUser();
                            Map<String, Object> childUpdates = new HashMap<>();
                            final String current_uid = user.getEmail().split("@")[0];


                            childUpdates.put("/User/" +current_uid+"/species", "");
                            childUpdates.put("/User/"+current_uid+"/location", "");
                            childUpdates.put("/User/"+current_uid+"/feature", "");
                            childUpdates.put("/User/"+current_uid+"/LoseState", "False");
                            childUpdates.put("/User/"+current_uid+"/uid", email);

                            firebaseDatabaseRef.updateChildren(childUpdates);

                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(mContext, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
