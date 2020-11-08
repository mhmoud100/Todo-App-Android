package com.ms.todo_app.activites;

import androidx.annotation.NonNull;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ms.todo_app.interfaces.EditClickListener;
import com.ms.todo_app.fragments.ProfileFragment;
import com.ms.todo_app.R;
import com.ms.todo_app.fragments.TodoFragment;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity implements EditClickListener {
    FirebaseUser user;
    Toolbar toolbar;

    FirebaseFirestore db;
private DrawerLayout drawer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                InputMethodManager imm = (InputMethodManager) MainActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(drawerView.getWindowToken(), 0);
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        toggle.syncState();

        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        final NavigationView navigationView = findViewById(R.id.nav_view);
        View view = navigationView.getHeaderView(0);
        final TextView textView = view.findViewById(R.id.textView);
        final ImageView imageView = view.findViewById(R.id.imageView);


        db.collection("Users").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        textView.setText("Hello "+ document.get("User Name"));
                        if ((document.get("Image Profile")).equals("")){

                            imageView.setImageDrawable(getDrawable(R.drawable.ic_avatar_icon));
                        }else Picasso.with(MainActivity.this).load((String) document.get("Image Profile")).fit().centerInside().into(imageView);
                    }
                }
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawer.closeDrawer(GravityCompat.START);

                if(item.getItemId() == R.id.profile){
                    setFragment(new ProfileFragment(), "Profile");
                } else if (item.getItemId() == R.id.signout) {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                    intent.putExtra("isHome",true);
                    startActivity(intent);
                }
                else setFragment(new TodoFragment(), "Todo");

                return true;
            }
        });
        Boolean isEdited = getIntent().getBooleanExtra("isEdited", false);
        if (isEdited){
            setFragment(new ProfileFragment(), "Profile");
            navigationView.getMenu().getItem(1).setChecked(true);
        }else {
            setFragment(new TodoFragment(), "Todo");
            navigationView.getMenu().getItem(0).setChecked(true);

        }

    }
    private void setFragment(Fragment fragment, String title){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.coordinator_layout, fragment);
        getSupportActionBar().setTitle(title);
        fragmentTransaction.commit();
    }

    @Override
    public void onEditClick(String Username) {
        Intent intent = new Intent(MainActivity.this, EditProfileActivity.class);
        intent.putExtra("User", Username);
        startActivity(intent);
    }


    @Override
    public void onBackPressed() {
        Boolean isAuth = getIntent().getBooleanExtra("isAuth",false);
        Boolean isEdited = getIntent().getBooleanExtra("isEdited", false);
        if(isAuth || isEdited){
            return;
        }else super.onBackPressed();
    }
}
