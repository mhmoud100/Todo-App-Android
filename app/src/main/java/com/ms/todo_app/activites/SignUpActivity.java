package com.ms.todo_app.activites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ms.todo_app.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    EditText email, pass, passconfirm, username;
    TextView birthday;
    Button register;
    RadioGroup radioGroup;
    String Gender;
    String myFormat;
    SimpleDateFormat sdf;
    Boolean isC;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    String UserID;
    Calendar myCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        pass = findViewById(R.id.password);
        passconfirm = findViewById(R.id.password_confirm);
        register = findViewById(R.id.btn_register);
        radioGroup = findViewById(R.id.RadioGroup);
        birthday = findViewById(R.id.Birthday);
        isC = false;

        myFormat = "dd/MM/yy";
        sdf = new SimpleDateFormat(myFormat, Locale.US);

        mAuth = FirebaseAuth.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();


        myCalendar = Calendar.getInstance();

       final  DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };


        birthday.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(SignUpActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String Email = email.getText().toString().trim();
                String password = pass.getText().toString();
                String passwordConfirm = passconfirm.getText().toString();
                final String userName = username.getText().toString().trim();
                if (Email.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please Put Your Email", Toast.LENGTH_LONG).show();
                    email.requestFocus();
                } else if (password.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please Put Your Password", Toast.LENGTH_LONG).show();
                    pass.requestFocus();
                } else if (Email.isEmpty() && password.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Fill The Form Please", Toast.LENGTH_LONG).show();
                } else if (!password.equals(passwordConfirm)){
                    Toast.makeText(SignUpActivity.this, "The Passwords Doesn't Match", Toast.LENGTH_LONG).show();
                    passconfirm.requestFocus();
                } else if (username.getText().toString().equals("")){
                    Toast.makeText(SignUpActivity.this, "Please put Username", Toast.LENGTH_LONG).show();
                    username.requestFocus();
                } else if (password.length() < 6){
                    Toast.makeText(SignUpActivity.this, "Password must be at least 6 Charachters", Toast.LENGTH_LONG).show();
                } else if (!isC){
                    Toast.makeText(SignUpActivity.this, "Please Choose your Gender", Toast.LENGTH_LONG).show();

                } else if (birthday.getText().toString().equals("Date of Birth")){
                    Toast.makeText(SignUpActivity.this, "Please put Your Birthday", Toast.LENGTH_LONG).show();

                } else if (!Email.isEmpty() && !password.isEmpty()) {
                    mAuth.createUserWithEmailAndPassword(Email, password)
                            .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        UserID = firebaseAuth.getCurrentUser().getUid();
                                        Map<String,Object> user = new HashMap<>();
                                        user.put("User Name", userName);
                                        user.put("Email", Email);
                                        user.put("Gender", Gender);
                                        user.put("Date of Birth", sdf.format(myCalendar.getTime()));
                                        user.put("Image Profile", "");

                                        firestore.collection("Users").document(UserID).set(user)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(SignUpActivity.this, "done", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(SignUpActivity.this, "Fail", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                        startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                                        Toast.makeText(SignUpActivity.this, "SignedUp Successfully", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(SignUpActivity.this, task.getException().toString(), Toast.LENGTH_LONG).show();

                                    }
                                }
                            });
                } else {
                    Toast.makeText(SignUpActivity.this, "Something Went Wrong", Toast.LENGTH_LONG).show();
                }
            }
        });
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.male:
                        Gender = "male";
                        isC = true;
                        break;
                    case R.id.female:
                        Gender = "female";
                        isC = true;
                        break;
                    default: Gender = "";
                }
            }
        });

    }
    private void updateLabel() {

        birthday.setText(sdf.format(myCalendar.getTime()));
    }

    public void onClickLogin(View view) {
        startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
    }


}