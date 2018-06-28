package com.example.minyoung.finding_dog;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class SignupActivity extends AppCompatActivity {
    private EditText email;
    private EditText name;
    private EditText password;
    private Button signup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        email=(EditText)findViewById(R.id.signupActivity_edittext_email);
        name=(EditText)findViewById(R.id.signupActivity_edittext_name);
        password=(EditText)findViewById(R.id.signupActivity_edittext_password);
        signup=(Button)findViewById(R.id.signupActivity_button_signup);

    }
}
