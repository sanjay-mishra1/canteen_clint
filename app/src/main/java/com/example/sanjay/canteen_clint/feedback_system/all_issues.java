package com.example.sanjay.canteen_clint.feedback_system;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.sanjay.canteen_clint.R;
import com.example.sanjay.canteen_clint.adapter_allorders;
import com.example.sanjay.canteen_clint.album_allorders;
import com.example.sanjay.canteen_clint.info.all_users;
import com.example.sanjay.canteen_clint.mainactiv_allorders;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class all_issues extends AppCompatActivity{
    private adapter_allorders adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback_layout);
        findViewById(R.id.card_view).setVisibility(View.GONE);
        RecyclerView   recyclerView = (RecyclerView)  findViewById(R.id.recycler_view);
        //   total_food = view.findViewById(R.id.total_foodItem);
        //mainLinear=view.findViewById(R.id.main_relative_orders);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        loadfirebase(recyclerView);
        recyclerView.setNestedScrollingEnabled(false);

    }

    void loadfirebase(RecyclerView recyclerView){
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().
                getReference("Messages/Issues");
        FirebaseRecyclerAdapter<album_allorders, mainactiv_allorders.FoodViewHolder> FBRA=new FirebaseRecyclerAdapter<album_allorders, mainactiv_allorders.FoodViewHolder>(
                album_allorders.class,
                R.layout.all_users,
                mainactiv_allorders.FoodViewHolder.class,
                databaseReference

        ) {
            @Override
            protected void populateViewHolder(final mainactiv_allorders.FoodViewHolder viewHolder, final album_allorders model, int position) {
                findViewById(R.id.progressFood).setVisibility(View.GONE);
                viewHolder.setMessage(model.getMessage());
               viewHolder.setUser_name(model.getCanteen());
                load_User_name(model.getCanteen(),(TextView) viewHolder.mView.findViewById(R.id.username),(ImageView) viewHolder.mView.findViewById(R.id.image_message_profile));
                viewHolder.mView.findViewById(R.id.text_message_body).setVisibility(View.VISIBLE);
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent;

                            intent =new Intent(all_issues.this,issues.class);


                        intent.putExtra("Canteen",model.getCanteen());
                         ActivityOptionsCompat optionsCompat = ActivityOptionsCompat
                                .makeSceneTransitionAnimation
                                        (all_issues.this, viewHolder.mView.findViewById(R.id.image_message_profile), "user_image");
                        startActivity(intent, optionsCompat.toBundle());

                    }
                });
            }


        };
        recyclerView.setAdapter(FBRA);
    }
    private void load_User_name(final String uId, final TextView textView, final ImageView imageView) {
        FirebaseDatabase.getInstance().getReference("Canteen/"+uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                 {      textView.setText(uId);
                    String  img=    (String)dataSnapshot.child("CanteenImage").getValue();
                    Glide.with(getApplicationContext()).applyDefaultRequestOptions(RequestOptions.circleCropTransform())
                            .load(img).into(imageView);

                }
              //  else textView.setText(R.string.deleted_account);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
 }
