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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.sanjay.canteen_clint.R;
import com.example.sanjay.canteen_clint.adapter_allorders;
import com.example.sanjay.canteen_clint.album_allorders;
import com.example.sanjay.canteen_clint.info.UserImg;
import com.example.sanjay.canteen_clint.mainactiv_allorders;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class issues extends AppCompatActivity {
    private adapter_allorders adapter;
     @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_all_feedbacks);
        Intent intent=getIntent();
        setTitle("");
        String Canteen=intent.getStringExtra("Canteen");
         RecyclerView   recyclerView = (RecyclerView)  findViewById(R.id.recycler_view);
        //   total_food = view.findViewById(R.id.total_foodItem);
        //mainLinear=view.findViewById(R.id.main_relative_orders);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(adapter);
        TextView textView=findViewById(R.id.username);
        textView.setText(Canteen);
        load_User_name(Canteen,(TextView) findViewById(R.id.username));
        loadfirebase(recyclerView,Canteen );

    }

    private void load_User_name(final String canteen, final TextView textView) {
         FirebaseDatabase.getInstance().getReference("Canteen/"+canteen).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               // textView.setText((String)dataSnapshot.child("Name").getValue());
                setTitle(canteen);
                try {
                    Glide.with(getApplicationContext()).applyDefaultRequestOptions(RequestOptions.circleCropTransform())
                            .load((String)dataSnapshot.child("CanteenImage").getValue()).into((ImageView)findViewById(R.id.userimage));



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

         DatabaseReference mDatabase = FirebaseDatabase.getInstance().
                getReference().child("Messages/Issues");

        Query q=mDatabase.orderByChild("Canteen").equalTo(uid);
        FirebaseRecyclerAdapter<album_allorders, mainactiv_allorders.FoodViewHolder> FBRA=new FirebaseRecyclerAdapter<album_allorders, mainactiv_allorders.FoodViewHolder>(
                album_allorders.class,
                R.layout.issue_layout,
                mainactiv_allorders.FoodViewHolder.class,
                q)
        {
            @Override
            protected void populateViewHolder(mainactiv_allorders.FoodViewHolder viewHolder, final album_allorders model, int position) {
                //progressBar.setVisibility(View.GONE);
                findViewById(R.id.progressbar).setVisibility(View.GONE);

                //load_User_name(model.getUId(),(TextView) viewHolder.mView.findViewById(R.id.UserName));
                viewHolder.setIssueImage(model.getUser_Img());
                viewHolder.setMessage(model.getMessage());
                viewHolder.setDate2(model.getDate());
                viewHolder.setIssueID(model.getUId());
               viewHolder.mView.findViewById(R.id.image_message_profile).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent1=new Intent(issues.this,UserImg.class);
                        intent1.putExtra("USERIMG",model.getUser_Img());
                        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat
                                .makeSceneTransitionAnimation
                                        (issues.this,findViewById(R.id.userimage) , "user_image");
                        startActivity(intent1, optionsCompat.toBundle());


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
