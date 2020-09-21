package com.example.sanjay.canteen_clint.message;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.sanjay.canteen_clint.R;
import com.example.sanjay.canteen_clint.album_allorders;
import com.example.sanjay.canteen_clint.mainactiv_allorders;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import static com.example.sanjay.canteen_clint.extra_classes.Constants.Canteen;

public class chat_app_Activity extends AppCompatActivity {
    RecyclerView recyclerView;
    private MyAdapter adapter;
    private String canteen;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity_layout);
        receive();
       recyclerView=findViewById(R.id.recycler_Chat);
       LinearLayoutManager mLayoutManager = new LinearLayoutManager( this, LinearLayoutManager.VERTICAL, false);

          mLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        ONStart();
    }
    void receive(){
        Intent intent=getIntent();
          if (Canteen.equalsIgnoreCase("admin"))
            canteen= intent.getStringExtra("Address");

        else
            canteen=Canteen;
    }
     protected void ONStart() {
         final DatabaseReference databaseReference=  FirebaseDatabase.getInstance().getReference().
                child("Messages").child("Help").child(canteen);
        Query query=databaseReference.orderByChild("Server_Time");
        FirebaseRecyclerAdapter<album_allorders, mainactiv_allorders.FoodViewHolder>firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<album_allorders, mainactiv_allorders.FoodViewHolder>(
                album_allorders.class,
                R.layout.chat_layout,
                mainactiv_allorders.FoodViewHolder.class,
                query

        ) {
            @Override
            protected void populateViewHolder(final mainactiv_allorders.FoodViewHolder viewHolder, final album_allorders model, int position) {
                findViewById(R.id.progressbar).setVisibility(View.GONE);
                viewHolder.setImage(model.getUser_Img(),getApplicationContext());
                //viewHolder.setUser_name(model.getUser_Name());
                viewHolder.setlast_message(model.getUser_last_Message());
                viewHolder.setTime1(model.getTime());
                Load_User_info(model.getUId(), viewHolder.setImage(model.getUser_Img(),getApplicationContext()),
                        viewHolder.setUser_name(model.getUser_Name()),viewHolder.mView);
               try {
                   viewHolder.setMessage_counter(model.getLast_message_counter());
               }catch (DatabaseException e){
                   Log.e("Database Exception",e.toString());
               }


                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(chat_app_Activity.this,chat_window.class);
                        intent.putExtra("UID",model.getUId());
                        intent.putExtra("UserName",model.getUser_Name());
                        intent.putExtra("Address",canteen);
                        TextView t=viewHolder.mView.findViewById(R.id.imglink);
                        intent.putExtra("img",t.getText().toString().trim());
                        databaseReference.child(model.getUId()).child("Last_message_counter").setValue("0");
                         startActivity(intent);
                    }
                });
            }

        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }
    void Load_User_info(final String Uid, final ImageView imageview, final TextView User_name, final View view){
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("User Informations"+"/"+Uid);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User_name.setText((String) dataSnapshot.child("Name").getValue());
              try {
                  Log.e("Message added",Uid);
              }catch (Exception e){
                  e.printStackTrace();
              }
              TextView textView=view.findViewById(R.id.imglink);
              textView.setText((String) dataSnapshot.child("User_Img").getValue());
                Glide.with(getApplicationContext()).load(dataSnapshot.child("User_Img").getValue()).into(imageview);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
