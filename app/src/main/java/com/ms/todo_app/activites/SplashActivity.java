package com.ms.todo_app.activites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ms.todo_app.R;

public class SplashActivity extends AppCompatActivity {
    //Declaring FireBase Variables
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //Make Object from MyThread Class
        MyThread myThread = new MyThread();
        myThread.start();
        //Initialize Firebase Variables
        mAuth = FirebaseAuth.getInstance();
        //Make Listener to know if there is User Signed-in
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null){
                    //If there is User Go to Todo page
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                }else {
                    //If not Go to Sign-in page
                    startActivity(new Intent(SplashActivity.this, SignInActivity.class));
                }
                //Finish the Activity
                SplashActivity.this.finish();
            }
        };
    }

    private class MyThread extends Thread{
        @Override
        public void run() {
            super.run();
            try {
                //Make the User See The Splash Scree For 3 Sec And then Check if There is User or Not
                sleep(3000);
                mAuth.addAuthStateListener(mAuthStateListener);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }


}