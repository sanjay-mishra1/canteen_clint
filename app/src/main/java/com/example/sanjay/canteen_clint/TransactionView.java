package com.example.sanjay.canteen_clint;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.example.sanjay.canteen_clint.extra_classes.Constants.Canteen;

/**
 * Created by sanjay on 16/06/2018.
 */

public class TransactionView extends AppCompatActivity {

        RecyclerView recyclerView;
        private adapter_allorders adapter;
        ProgressBar progressBar;
        String orderNo;
    private String uid;
    private String canteen;

    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.transactions);
        boolean outside=true;
            try {
                Intent intent = getIntent();
                orderNo = intent.getStringExtra("OrderNo");
                // if (Canteen.equalsIgnoreCase("admin"))
                    canteen= intent.getStringExtra("Address");

              //  else
                   // canteen=Canteen;
                 outside = intent.getBooleanExtra("Outside",true);
            } catch (Exception e) {
                e.printStackTrace();
            }
            recyclerView = (RecyclerView) findViewById(R.id.transactions);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setAdapter(adapter);
            progressBar = findViewById(R.id.progressFood);
            firebase(outside);

        }
    private void firebase(boolean outside) {
        DatabaseReference dat = FirebaseDatabase.getInstance().getReference().child("Orders/"+canteen+"/All Orders/" + orderNo);
          TextView text = findViewById(R.id.orderno1);
        dat.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                album_allorders t = dataSnapshot.getValue(album_allorders.class);
                TextView textv=findViewById(R.id.Orderstatus1);
                try {
                    if (t != null) {
                        uid=t.getUserId();
                    }
                    textv.setText("Status              " + t.getStatus());
                }catch (Exception NullPointerException){
                 }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(TransactionView.this,editOrder.class);
                intent.putExtra("OrderNo",orderNo);
                intent.putExtra("Address",canteen);
                intent.putExtra("Uid",uid);
                startActivity(intent);


            }
        });
        DatabaseReference databaseReference;
       if (outside)
           databaseReference= FirebaseDatabase.getInstance().getReference().
                child("Transactions/"+canteen+"/" + orderNo);
       else
           databaseReference= FirebaseDatabase.getInstance().getReference().
                   child("Transactions/"+canteen+"/"+"Payments/" + orderNo);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                album_allorders trans = dataSnapshot.getValue(album_allorders.class);
                TextView textView = findViewById(R.id.orderno1);
               try {
               //    uid=trans.getUId();
               }catch (Exception ignored){}
                textView.setText("OrderNo          "+orderNo);
                textView = findViewById(R.id.deliverytime);
try {
    textView.setText("Time            "+trans.getTime());
}catch (Exception ignored){}
                textView = findViewById(R.id.total_final2);
try {
    textView.setText("Total Amount     "+getString(R.string.rupeesSymbol)+trans.getTotalAmount());
}catch (Exception ignored){}
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

databaseReference=databaseReference.child("Transactions");
        FirebaseRecyclerAdapter<album_allorders, mainactiv_allorders.FoodViewHolder> FBRA = new FirebaseRecyclerAdapter<album_allorders, mainactiv_allorders.FoodViewHolder>(
                album_allorders.class,
                R.layout.layout_trasactions,
                mainactiv_allorders.FoodViewHolder.class,
                databaseReference

        ) {
            @Override
            protected void populateViewHolder(mainactiv_allorders.FoodViewHolder viewHolder, album_allorders model, int position) {
                viewHolder.setTotalTransactions(model.getTransactionNumber());
                viewHolder.setTotal_Amount(model.getPayment());
                viewHolder.setTime(model.getTime());
                viewHolder.setMode(model.getMode());
           try {
               viewHolder.setPayment2(model.getMode(),getApplicationContext());
           }catch (Exception ignored){}

            }
        };
        recyclerView.setAdapter(FBRA);
    }    }



