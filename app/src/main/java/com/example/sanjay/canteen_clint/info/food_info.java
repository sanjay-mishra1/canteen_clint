package com.example.sanjay.canteen_clint.info;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.sanjay.canteen_clint.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class food_info extends AppCompatActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_info_layout);
        settypeimg();
        setInfo();
    }
    void settypeimg(){
        Intent intent=getIntent();
        TextView foodtype=  findViewById(R.id.usermessage);

        //foodtype.setText(intent.getStringExtra("Type"));

        switch (intent.getStringExtra("Type").toUpperCase()) {
            case "SNAKS":foodtype.setText("Snacks");
//                Picasso.get().load((R.drawable.snaksvector)).into((ImageView) findViewById(R.id.img));
                Glide.with(getApplicationContext()).load( (R.drawable.snaksvector)).into((ImageView) findViewById(R.id.img));

                break;
            case "BEVERAGES":foodtype.setText("Beverages");
                //Picasso.get().load((R.drawable.drinksvector)).into((ImageView) findViewById(R.id.img));
                Glide.with(getApplicationContext()).load( (R.drawable.drinksvector)).into((ImageView) findViewById(R.id.img));

                break;
            case "MEAL":foodtype.setText("Meal");
               // Picasso.get().load((R.drawable.mealvector)).into((ImageView) findViewById(R.id.img));
                Glide.with(getApplicationContext()).load( (R.drawable.mealvector)).into((ImageView) findViewById(R.id.img));

                break;
            case "SPECIAL":foodtype.setText("Special");
               // Picasso.get().load((R.drawable.cake)).into((ImageView) findViewById(R.id.img));
                Glide.with(getApplicationContext()).load( (R.drawable.cake)).into((ImageView) findViewById(R.id.img));

                break;
         default: Glide.with(getApplicationContext()).load( (R.drawable.disable_food)).into((ImageView) findViewById(R.id.img));

        }
    }
    void setInfo(){
        Intent intent=getIntent();
        FirebaseDatabase.getInstance().getReference("Food Items/"+intent.getStringExtra("Type")+"/"+intent.getStringExtra("Name")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TextView name=findViewById(R.id.userphone);
                TextView price=findViewById(R.id.useremail);
                TextView discount=findViewById(R.id.usercollege);
                TextView orders=findViewById(R.id.order);
                   TextView favorite=findViewById(R.id.userfav);
              try {
                  name.setText((String)dataSnapshot.child("Food_name").getValue());
                  setTitle((String)dataSnapshot.child("Food_name").getValue());
              }catch (Exception ignored){}
                try {
                    discount.setText(String.format("%s %s", getString(R.string.rupeesSymbol), dataSnapshot.child("discount").getValue()));
                }catch (Exception ignored){}
                try {
                    price.setPaintFlags(price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                    price.setText(String.format("%s %s", getString(R.string.rupeesSymbol), dataSnapshot.child("price").getValue()));
                }catch (Exception e){e.printStackTrace();}
                try {
                    orders.setText(String.valueOf( (long)dataSnapshot.child("Total_orders").getValue()));
                }catch (Exception e){e.printStackTrace();
              orders.setText("0");
              }
                 try {
                    favorite.setText(String.valueOf( (long)dataSnapshot.child("Favorite").getValue()));
                }catch (Exception e){e.printStackTrace();
              favorite.setText("0");
              }TextView description=findViewById(R.id.description);
                try {
                    description.setText((String)dataSnapshot.child("Description").getValue());
                }catch (Exception e){
                    description.setText("no description");
                }
                TextView rating ;
                rating=findViewById(R.id.rating);
              try {

                    rating.setText("Total Ratings "+String.valueOf( (long)dataSnapshot.child("Total_NoOfTime_Rated").getValue()));
                }catch (Exception e){e.printStackTrace();
                rating.setText(R.string.notrating);
              }
                TextView ratingavg=findViewById(R.id.totalratingss);

                try { float rate= (float) ( (long)dataSnapshot.child("Sum_of_Ratings").getValue())/
                            (float) ( (long)dataSnapshot.child("Total_NoOfTime_Rated").getValue());
                    RatingBar ratingBar=findViewById(R.id.ratingbar);
                    ratingBar.setRating(rate);
                    ratingavg.setText(String.format("%.1f",rate));
                }catch (Exception e){e.printStackTrace();
                ratingavg.setText("no rating");
              }
                try {
                    Glide.with(getApplicationContext()).load( dataSnapshot.child("Food_Image").getValue()).into((ImageView) findViewById(R.id.foodimage));
                    Glide.with(getApplicationContext()).applyDefaultRequestOptions(RequestOptions.circleCropTransform()).load( dataSnapshot.child("Food_Image").getValue()).into((ImageView) findViewById(R.id.userimage));
                }catch (Exception ignored){}



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
