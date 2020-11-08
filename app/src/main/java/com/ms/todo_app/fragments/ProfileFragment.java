package com.ms.todo_app.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ms.todo_app.R;
import com.ms.todo_app.interfaces.EditClickListener;
import com.squareup.picasso.Picasso;


public class ProfileFragment extends Fragment {
    //Declaring Views
    TextView email, username, gender, birth;
    Button update;
    de.hdodenhof.circleimageview.CircleImageView photo;
    //Declaring The Name of the Document Which Save the User Data
    String UserID;
    //Declaring Firebase Variables
    FirebaseAuth fauth;
    FirebaseFirestore db;
    //Declaring Object from EditClickListener InterFace
    private EditClickListener editClickListener;
    //Get the Context of the Activity Which will Attach to ProfileFragment
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        editClickListener = (EditClickListener) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        //Initialize the Views
        email = view.findViewById(R.id.email);
        username = view.findViewById(R.id.username);
        birth = view.findViewById(R.id.date);
        gender = view.findViewById(R.id.gender);
        photo = view.findViewById(R.id.photo);
        update = view.findViewById(R.id.btn_update);
        //Initialize Firebase Variables
        fauth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        //Get The Id of the User To Get his Data from
        UserID = fauth.getCurrentUser().getUid();
        //Get The Data From FireStore And Set the Views text to the User Data
        db.collection("Users").document(UserID).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                email.setText((String) document.get("Email"));
                                username.setText((String) document.get("User Name"));
                                birth.setText((String) document.get("Date of Birth"));
                                gender.setText((String) document.get("Gender"));
                                String image = (String) document.get("Image Profile");
                                if (image.equals("")){
                                    //If there is no Image Put the Default Image
                                    Picasso.with(getContext()).load(R.drawable.ic_avatar_icon).fit().into(photo);
                                }else{
                                    //If there is Image Load it
                                    Picasso.with(getContext()).load((String) document.get("Image Profile")).fit().centerInside().into(photo);
                                }

                            }
                        }
                    }
                });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editClickListener.onEditClick(username.getText().toString());
            }
        });


        return view;
    }
}