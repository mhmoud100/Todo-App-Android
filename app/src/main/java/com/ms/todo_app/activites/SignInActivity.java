package com.ms.todo_app.activites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.ms.todo_app.R;

public class SignInActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    EditText email;
    EditText password;
    Button Login;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = findViewById(R.id.username);
        password = findViewById(R.id.password);
        Login = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.progress);
        mAuth = FirebaseAuth.getInstance();


        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                Login.setEnabled(false);
                String Email = email.getText().toString().trim();
                String Password = password.getText().toString();
                if (Email.isEmpty()) {
                    progressBar.setVisibility(View.GONE);
                    Login.setEnabled(true);
                    Toast.makeText(SignInActivity.this, "Please Put Your Email", Toast.LENGTH_LONG).show();
                    email.requestFocus();
                } else if (Password.isEmpty()) {
                    progressBar.setVisibility(View.GONE);
                    Login.setEnabled(true);
                    Toast.makeText(SignInActivity.this, "Please Put Your Password", Toast.LENGTH_LONG).show();
                } else if (Email.isEmpty() && Password.isEmpty()) {
                    progressBar.setVisibility(View.GONE);
                    Login.setEnabled(true);
                    Toast.makeText(SignInActivity.this, "Fill The Form Please", Toast.LENGTH_LONG).show();
                } else if (!Email.isEmpty() && !Password.isEmpty()) {

                    mAuth.signInWithEmailAndPassword(Email, Password)
                            .addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                                        intent.putExtra("isAuth",true);
                                        startActivity(intent);
                                        progressBar.setVisibility(View.GONE);
                                        Login.setEnabled(true);
                                        Toast.makeText(SignInActivity.this, "SignedIn Successfully", Toast.LENGTH_SHORT).show();

                                    } else {
                                        progressBar.setVisibility(View.GONE);
                                        Login.setEnabled(true);
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(SignInActivity.this, task.getException().toString(),Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });

                } else {
                    Toast.makeText(SignInActivity.this, "Something Went Wrong", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    @Override
    public void onBackPressed() {
        Boolean isHome = getIntent().getBooleanExtra("isHome",false);
        if(isHome){
            return;
        }else super.onBackPressed();
    }

    public void onClickRegister(View view) {
        startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
    }


}