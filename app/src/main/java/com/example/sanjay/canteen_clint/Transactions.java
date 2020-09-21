package com.example.sanjay.canteen_clint;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.Inet4Address;

/**
 * Created by sanjay on 15/06/2018.
 */

public class Transactions extends AppCompatActivity {
    RecyclerView recyclerView;
    private adapter_allorders adapter;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_allorders);
        recyclerView = (RecyclerView)  findViewById(R.id.recycler_view);
          RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        progressBar=findViewById(R.id.progressFood);
firebase();
    }

void firebase(){
      DatabaseReference FoodDatabase= FirebaseDatabase.getInstance().getReference();
FoodDatabase=FoodDatabase.child("Transactions");
    final DatabaseReference finalFoodDatabase = FoodDatabase;
    FirebaseRecyclerAdapter<album_allorders, mainactiv_allorders.FoodViewHolder>FBRA=new FirebaseRecyclerAdapter<album_allorders, mainactiv_allorders.FoodViewHolder>(
            album_allorders.class,
            R.layout.payments,
            mainactiv_allorders.FoodViewHolder.class,
            finalFoodDatabase

    ) {

        @Override
        protected void populateViewHolder(final mainactiv_allorders.FoodViewHolder viewHolder, final album_allorders model, int position) {
            progressBar.setVisibility(View.GONE);
             viewHolder.setOrderNo(model.getOrderNo());
             viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                     Intent intent=new Intent(Transactions.this,TransactionView.class);
                     intent.putExtra("OrderNo",model.getOrderNo());
                     intent.putExtra("Uid",model.getUserId());
                     startActivity(intent);
                 }
             });
            finalFoodDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

try {
    viewHolder.setTotalTransactions(String.valueOf(dataSnapshot.child(model.getOrderNo()).getChildrenCount() - 4));
}catch (Exception NumberFormatException){viewHolder.setTotalTransactions("0");}
          //          viewHolder.setOrderNo(model.getOrderNo());
                    viewHolder.setTime(model.getTime());
                   // dataSnapshot.getValue();//.getKey();
                // viewHolder.setOrderNo(String.valueOf(dataSnapshot.child(String.valueOf(dataSnapshot.getChildren()))));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });

        }
    };
    recyclerView.setAdapter(FBRA);
}

}
