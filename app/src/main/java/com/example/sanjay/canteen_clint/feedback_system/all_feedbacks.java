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

public class all_feedbacks extends AppCompatActivity{
        private adapter_allorders adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback_layout);
     RecyclerView   recyclerView = (RecyclerView)  findViewById(R.id.recycler_view);
        //   total_food = view.findViewById(R.id.total_foodItem);
        //mainLinear=view.findViewById(R.id.main_relative_orders);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        loadfirebase(recyclerView);
        recyclerView.setNestedScrollingEnabled(false);
        calculateRating();

    }

    void loadfirebase(RecyclerView recyclerView){
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().
                getReference("Messages/Feedbacks");
        FirebaseRecyclerAdapter<album_allorders, mainactiv_allorders.FoodViewHolder> FBRA=new FirebaseRecyclerAdapter<album_allorders, mainactiv_allorders.FoodViewHolder>(
                album_allorders.class,
                R.layout.feedback_ui,
                mainactiv_allorders.FoodViewHolder.class,
                databaseReference

        ) {
            @Override
            protected void populateViewHolder(final mainactiv_allorders.FoodViewHolder viewHolder, final album_allorders model, int position) {
                findViewById(R.id.progressFood).setVisibility(View.GONE);
try {
    viewHolder.setrating(Double.valueOf(model.getRating()));
}catch (Exception e){
    viewHolder.setrating(0.0);
}
              load_User_name(model.getUId(),(TextView) viewHolder.mView.findViewById(R.id.UserName),(ImageView) viewHolder.mView.findViewById(R.id.img2));
              viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View view) {
                      Intent intent;

                      intent =new Intent(all_feedbacks.this,issues.class);


                      intent.putExtra("Uid",model.getUId());
                       ActivityOptionsCompat optionsCompat = ActivityOptionsCompat
                              .makeSceneTransitionAnimation
                                      (all_feedbacks.this, viewHolder.getImageView1(), "user_image");
                      startActivity(intent, optionsCompat.toBundle());

                   }
              });
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
                  String  img=    (String)dataSnapshot.child("User_Img").getValue();
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
    private void calculateRating() {
        FirebaseDatabase.getInstance().getReference("Messages/Feedbacks").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    long total=dataSnapshot.getChildrenCount();
                     double rating=0.0;

                TextView textView=findViewById(R.id.avgrating);
                    for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    try {
                        rating+=Double.valueOf( (String)dataSnapshot1.child("Rating").getValue());
                    }catch (Exception e){
                        rating+=0.0;
                    }

                    }
               // textView.setText(String.format("%.1f", rating/total));
                textView.setText( String.format("%.1f",rating/total)) ;
                textView=findViewById(R.id.totalrating);
                textView.setText(String.valueOf(total));
                RatingBar ratingBar=findViewById(R.id.rating);
                ratingBar.setRating((float) rating/total);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
