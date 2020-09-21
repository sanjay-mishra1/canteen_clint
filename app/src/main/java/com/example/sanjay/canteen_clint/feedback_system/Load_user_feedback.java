package com.example.sanjay.canteen_clint.feedback_system;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.sanjay.canteen_clint.R;
import com.example.sanjay.canteen_clint.adapter_allorders;
import com.example.sanjay.canteen_clint.album_allorders;
import com.example.sanjay.canteen_clint.mainactiv_allorders;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Load_user_feedback extends AppCompatActivity {
    private adapter_allorders adapter;
    private String name;
     @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_all_feedbacks);
        Intent intent=getIntent();
        setTitle("");
        String uid=intent.getStringExtra("Uid");
        RecyclerView   recyclerView = (RecyclerView)  findViewById(R.id.recycler_view);
        //   total_food = view.findViewById(R.id.total_foodItem);
        //mainLinear=view.findViewById(R.id.main_relative_orders);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(adapter);
        load_User_name(uid,(TextView) findViewById(R.id.username));
         loadfirebase(recyclerView,uid  );

    }
    private void load_User_name(final String uId, final TextView textView) {
         textView.setText(name);
        FirebaseDatabase.getInstance().getReference("User Informations/"+uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                textView.setText((String)dataSnapshot.child("Name").getValue());
                 setTitle((String)dataSnapshot.child("Name").getValue());
             try {
                 Glide.with(getApplicationContext()).applyDefaultRequestOptions(RequestOptions.circleCropTransform())
                         .load((String)dataSnapshot.child("User_Img").getValue()).into((ImageView)findViewById(R.id.userimage));



             }catch (Exception e){
                 e.printStackTrace();
             }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void loadfirebase(RecyclerView recyclerView, String uid ){
        /*
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().
                getReference("Messages/Feedbacks");
        FirebaseRecyclerAdapter<album_allorders, mainactiv_allorders.FoodViewHolder> FBRA=new FirebaseRecyclerAdapter<album_allorders, mainactiv_allorders.FoodViewHolder>(
                album_allorders.class,
                R.layout.feedback_ui,
                mainactiv_allorders.FoodViewHolder.class,
                databaseReference.startAt("UId",uid)

        ) {
            @Override
            protected void populateViewHolder(mainactiv_allorders.FoodViewHolder viewHolder, final album_allorders model, int position) {
                findViewById(R.id.progressbar).setVisibility(View.GONE);
                viewHolder.setrating(Double.valueOf(model.getRating()));
             //   load_User_name(model.getUId(),(TextView) viewHolder.mView.findViewById(R.id.UserName));
                viewHolder.setMessage(model.getFeedback());
            }

        };*/
    //    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Orders/New Orders");
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().
                getReference().child("Messages/Feedbacks");
        //Query q=mDatabase.startAt("UId",uid);
       // Query q=mDatabase.child("UId").startAt(uid);
        Query q=mDatabase.orderByChild("UId").equalTo(uid);
        FirebaseRecyclerAdapter<album_allorders, mainactiv_allorders.FoodViewHolder> FBRA=new FirebaseRecyclerAdapter<album_allorders, mainactiv_allorders.FoodViewHolder>(
                album_allorders.class,
                R.layout.feedback_ui,
                mainactiv_allorders.FoodViewHolder.class,
                q)
        {
            @Override
            protected void populateViewHolder(mainactiv_allorders.FoodViewHolder viewHolder, final album_allorders model, int position) {
                //progressBar.setVisibility(View.GONE);
                findViewById(R.id.progressbar).setVisibility(View.GONE);
               try {
                   viewHolder.setrating(Double.valueOf(model.getRating()));
                }catch (Exception e){
                  viewHolder.setrating(0.0);
              }
                   //load_User_name(model.getUId(),(TextView) viewHolder.mView.findViewById(R.id.UserName));
                load_User_name(model.getUId(),(TextView) viewHolder.mView.findViewById(R.id.UserName),(ImageView) viewHolder.mView.findViewById(R.id.img2));

                viewHolder.setMessage(model.getFeedback());

            }

        };
        recyclerView.setAdapter(FBRA);
    }
    private void load_User_name(String uId, final TextView textView,final ImageView imageView) {

        FirebaseDatabase.getInstance().getReference("User Informations/"+uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {     textView.setText((String)dataSnapshot.child("Name").getValue());
                   String img=    (String)dataSnapshot.child("User_Img").getValue();
                    Glide.with(getApplicationContext()).applyDefaultRequestOptions(RequestOptions.circleCropTransform())
                            .load(img).into(imageView);

                }
                else textView.setText(R.string.deleted_account);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
