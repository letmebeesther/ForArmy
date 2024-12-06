package com.esther.perfect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class signup extends AppCompatActivity {

    private EditText email_join, pwd_join, name_join;
    private Button btn;

    FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        email_join = findViewById(R.id.email);
        pwd_join = findViewById(R.id.pwd);
        name_join = findViewById(R.id.name);
        btn = findViewById(R.id.btn);

        firebaseAuth = FirebaseAuth.getInstance();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = email_join.getText().toString().trim();
                final String pwd = pwd_join.getText().toString().trim();
                final String name = name_join.getText().toString().trim();

                firebaseAuth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(signup.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            String uid = user.getUid();

                            // 사용자 정보를 데이터베이스에 저장
                            UserAccount account = new UserAccount(email, name);
                            databaseReference.child("users").child(uid).setValue(account);

                            Intent it = new Intent(signup.this, login.class);
                            startActivity(it);
                            finish();
                            Toast.makeText(signup.this, "회원가입 완료", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(signup.this, "등록 에러", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    public static class UserAccount {
        public String email;
        public String nickname;

        public UserAccount() {}

        public UserAccount(String email, String nickname) {
            this.email = email;
            this.nickname = nickname;
        }
    }
}
