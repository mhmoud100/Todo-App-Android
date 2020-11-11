package com.ms.todo_app.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ms.todo_app.R;
import com.ms.todo_app.model.TodoItems;
import com.ms.todo_app.fragments.TodoFragment;

import java.util.ArrayList;
import java.util.Objects;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private ArrayList<TodoItems> todoItems;
    private Context context;
    FirebaseUser user;
    FirebaseFirestore db;


    public RecyclerViewAdapter(Context context, ArrayList<TodoItems> todoItems) {
        this.context = context;
        this.todoItems = todoItems;
    }
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView = LayoutInflater.from(context).inflate(R.layout.list_view_item, parent, false);
        return new ViewHolder(itemView);

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public int getItemCount(){
        return todoItems.size();
    }

    public void onBindViewHolder (ViewHolder holder, final int position){
        final TodoItems item = todoItems.get(position);
        holder.textView.setText(item.getTodoitems());

        if (item.getCompleted()){
            holder.textView.setPaintFlags(holder.textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.checkBox.setChecked(true);
        }else {
            holder.textView.setPaintFlags(0);
            holder.checkBox.setChecked(false);
        }
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
           @Override
           public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
               db = FirebaseFirestore.getInstance();
               user = FirebaseAuth.getInstance().getCurrentUser();
               assert user != null;
               db.collection(Objects.requireNonNull(user.getEmail())).document(TodoFragment.id.get(position))
                       .update("isCompleted",!item.getCompleted())
                       .addOnSuccessListener(new OnSuccessListener<Void>() {
                           @Override
                           public void onSuccess(Void aVid) {

                               item.setCompleted(isChecked);

                               notifyDataSetChanged();
                           }
                       }).addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {
                       Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                   }
               });
           }
       });
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        CheckBox checkBox;
        ViewHolder(View view){
            super(view);
            textView = view.findViewById(R.id.listview_text);
            checkBox = view.findViewById(R.id.check);
        }

    }
}
