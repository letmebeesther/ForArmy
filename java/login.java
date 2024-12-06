package com.esther.perfect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class login extends AppCompatActivity {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private Button login;
    private EditText email_login;
    private EditText pwd_login;

    private TextView su, findpw;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        su = (TextView) findViewById(R.id.su);
        findpw = (TextView) findViewById(R.id.findpw);
        login = (Button)findViewById(R.id.login);

        su.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(getApplicationContext(), signup.class);
                startActivity(it);
            }
        });

        findpw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(getApplicationContext(), FindPwActivity.class);
                startActivity(it);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(getApplicationContext(), home.class);
                startActivity(it);
            }
        });

        login = (Button) findViewById(R.id.login);
        email_login = (EditText) findViewById(R.id.email);
        pwd_login = (EditText) findViewById(R.id.pwd);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = email_login.getText().toString().trim();
                String pwd = pwd_login.getText().toString().trim();
                mAuth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(com.esther.perfect.login.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Intent it = new Intent(com.esther.perfect.login.this, home.class);
                            startActivity(it);

                        } else {
                            Toast.makeText(com.esther.perfect.login.this, "로그인 오류", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }//onCreate


}//MainActivity
