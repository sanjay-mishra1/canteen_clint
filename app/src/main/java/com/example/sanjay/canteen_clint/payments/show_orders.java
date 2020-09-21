package com.example.sanjay.canteen_clint.payments;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.example.sanjay.canteen_clint.R;
import com.example.sanjay.canteen_clint.TransactionView;
import com.example.sanjay.canteen_clint.adapter_allorders;
import com.example.sanjay.canteen_clint.album_allorders;
import com.example.sanjay.canteen_clint.mainactiv_allorders;
import com.example.sanjay.canteen_clint.pendingPayments;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class show_orders extends AppCompatActivity {
    private adapter_allorders adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recyclerlayout);
        Intent intent=getIntent();
        RecyclerView recyclerView=findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(adapter);
      try {String node=intent.getStringExtra("Date_Node");
          setTitle(node);
           loadfirebase(recyclerView,node,intent.getStringExtra("Address"));
      }catch (Exception e){
          finish();
          Toast.makeText(this,"Failed to load the list.Please Contact the administrator",Toast.LENGTH_SHORT).show();
      }

    }
    void loadfirebase(RecyclerView recyclerView, String node, final String canteen){
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().
                getReference("Transactions/"+canteen+"/Payments");
        FirebaseRecyclerAdapter<album_allorders, mainactiv_allorders.FoodViewHolder> FBRA=new FirebaseRecyclerAdapter<album_allorders, mainactiv_allorders.FoodViewHolder>(
                album_allorders.class,
                R. layout.payments,
                mainactiv_allorders.FoodViewHolder.class,
                databaseReference.orderByChild("APP_PAYMENT_RECEIVED")
                        .equalTo(node)

        ) {
            @Override
            protected void populateViewHolder(final mainactiv_allorders.FoodViewHolder viewHolder, final album_allorders model, int position) {
                findViewById(R.id.progressbar).setVisibility(View.GONE);
               viewHolder.mView. findViewById(R.id.t3).setVisibility(View.GONE);
                viewHolder.setOrderNo(model.getOrderNo());
                viewHolder.setTime(model.getTime());
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(show_orders. this, TransactionView.class);
                        intent.putExtra("OrderNo", model.getOrderNo());
                        intent.putExtra("Address", canteen);
                        intent.putExtra("Outside", false);

                        startActivity(intent);
                    }
                });
            }
        };
        recyclerView.setAdapter(FBRA);
    }

}
