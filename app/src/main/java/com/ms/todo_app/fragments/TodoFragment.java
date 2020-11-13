package com.ms.todo_app.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ms.todo_app.R;
import com.ms.todo_app.adapters.RecyclerViewAdapter;
import com.ms.todo_app.model.TodoItems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class TodoFragment extends Fragment {
    //Declaring Views
    EditText addText;
    Button add;
    ProgressBar progressBar;
    RecyclerView recyclerView;
    //Declaring Collection name from firebase
    String col;
    //Declaring ArrayLists
    ArrayList<TodoItems> todoItems;
    public static ArrayList<String> id;
    //Declaring Adapter for RecyclerView
    RecyclerViewAdapter todoAdapter;
    //Declaring Firebase Variables
    FirebaseUser user;
    FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_todo, container, false);

        //Initialize the Views
        recyclerView = view.findViewById(R.id.listView);
        addText = view.findViewById(R.id.add_text);
        add = view.findViewById(R.id.add_Button);
        progressBar = view.findViewById(R.id.progress_bar);
        //Initialize ArrayList which holds the Todos
        todoItems = new ArrayList<>();
        //Initialize Firebase Variables
        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        //Initialize the String which holds the user Email (which is the name of the Collection in fireStore)
        col = user.getEmail();
        //Make ProgressBar Visible to show loading to get the data
        progressBar.setVisibility(View.VISIBLE);
        //Setting the RecyclerView LayoutManager to MainActivity which Contain the todoFragment
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // Display the Todo List
        display();
        //Setting onClickListener to Add Todo Button
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                //Make the Button unClickable
                add.setEnabled(false);
                if (addText.getText().toString().trim().equals("")) {
                    add.setEnabled(true);
                    Toast.makeText(getContext(), "Enter What You Want To-Do", Toast.LENGTH_SHORT).show();
                } else if (addText.getText().toString().trim().length() < 3){
                    add.setEnabled(true);
                    Toast.makeText(getContext(), "The To-Do must be at least 3 characters ", Toast.LENGTH_SHORT).show();
                } else {
                    //Hold the data the user typed in todo HashMap
                    Map<String, Object> todo = new HashMap<>();
                    todo.put("todo", addText.getText().toString().trim());
                    todo.put("isCompleted", false);
                    todo.put("uid", user.getUid());
                    //Add the Data to fireStore
                    db.collection("Todos")
                            .add(todo)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    //Display the data after Adding the Todo
                                    display();
                                    //Setting the EditText to Empty String
                                    addText.setText("");
                                    //Hide The Keyboard
                                    hideKeyboardFrom(getContext(), v);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //InCaseOf Failure Make the button Clickable and Show Message
                                    add.setEnabled(true);
                                    Toast.makeText(getContext(), "Something Went Wrong", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

        //Declaring and Initialize the ItemTouchHelper to SimpleCallback
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        //Attach it to RecyclerView
        itemTouchHelper.attachToRecyclerView(recyclerView);
        return view;
    }
    //Declaring and Initialize SimpleCallback to Swipe Left and Right Listener
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, final int direction) {
            //Get Position the todo Swiped
            final int position = viewHolder.getAdapterPosition();
            switch (direction){
                //The Todo Swiped left Lisetener (Delete)
                case ItemTouchHelper.LEFT:
                    //Deleting The Todo From FireStore
                    db.collection("Todos").document(TodoFragment.id.get(position))
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    //Desplay the Data after the Deleting and Show Message
                                    display();
                                    Toast.makeText(getContext(), "Todo Successfully deleted", Toast.LENGTH_SHORT).show();
                                    //Remove the Todo id, todo item from ArrayLists
                                    id.remove(position);
                                    todoItems.remove(position);
                                    //Notify the Adapter
                                    todoAdapter.notifyDataSetChanged();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //InCaseOf Failure Desplay the Data and Show Message
                                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                    display();
                                }
                            });


                    break;
                //The Todo Swiped Right Lisetener (Edit)
                case ItemTouchHelper.RIGHT:
                    //If the Todo is Completed it can't be edited
                    if (!todoItems.get(position).isCompleted ) {
                        // Get the Layout of AlertDialog
                        final View alertDialogView = LayoutInflater.from(getContext()).inflate(R.layout.update_text_dialog, null);
                        final AlertDialog alertDialog = new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme).create();
                        alertDialog.setTitle("Update Todo");
                        alertDialog.setCancelable(false);


                        //Declaring and Initialize the TextView in the AlertDialog
                        final EditText etComments =alertDialogView.findViewById(R.id.etComments);
                        //Set the TextView text to the todo item
                        etComments.setText(todoItems.get(position).getTodoitems());
                        //Set onClickListener to Update Button
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Update", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Update the todo in FireStore
                                db.collection("Todos").document(TodoFragment.id.get(position))
                                        .update("todo",etComments.getText().toString())
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                display();
                                                Toast.makeText(getContext(), "Todo Updated Successfully", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                                    }
                                });

                            }
                        });

                        //Set onClickListener to Cancel Button
                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                display();
                                //Close the AlertDialog
                                alertDialog.dismiss();
                            }
                        });

                        alertDialog.setView(alertDialogView);
                        alertDialog.show();

                    }else {
                        display();
                        Toast.makeText(getContext(), "You Can't Edit A Completed Task", Toast.LENGTH_SHORT).show();
                    }

                    break;
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                                float dX, float dY, int actionState, boolean isCurrentlyActive)
        {
            //Make Animation when Swiping
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(getContext(), R.color.red))
                    .addSwipeLeftActionIcon(R.drawable.ic_delete_icon)
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(getContext(), R.color.dark_blue))
                    .addSwipeRightActionIcon(R.drawable.ic_edit_icon)
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };
    //Hiding the Keyboard Function
    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    //Display Function
    private void display(){
        //Get the Todo from FireStore
        db.collection("Todos").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                //Initialize todoItems ArrayList (Which Holds Todo items)
                //Initialize id (Which Holds Todo id's)
                todoItems = new ArrayList<>();
                id = new ArrayList<>();
                //The Data Came Successfully from FireStore
                if(task.isSuccessful()){
                    //Make ProgressBar Disappear
                    progressBar.setVisibility(View.GONE);
                    for(QueryDocumentSnapshot document : task.getResult()) {
                        if ((document.get("uid")).equals(user.getUid())) {
                            //Add the Todo id in id ArrayList
                            id.add(0, document.getId());
                            //Add The Data to todoItems(ArrayList)
                            todoItems.add(0, new TodoItems((String) document.get("todo"), (Boolean) document.get("isCompleted")));
                            //Make the Add Button Clickable
                            add.setEnabled(true);
                        }
                    }
                    //Initialize the Adapter
                    todoAdapter = new RecyclerViewAdapter(getContext(), todoItems);
                    recyclerView.setItemViewCacheSize(todoItems.size());
                    //Setting Adapter to RecyclerView
                    recyclerView.setAdapter(todoAdapter);

                } else {
                    //The Data didn't Come Successfully
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}