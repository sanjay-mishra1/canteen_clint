package com.example.sanjay.canteen_clint.extra_classes;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sanjay.canteen_clint.MainActivity;
import com.example.sanjay.canteen_clint.NewFood;
import com.example.sanjay.canteen_clint.R;
import com.example.sanjay.canteen_clint.album_allorders;
import com.example.sanjay.canteen_clint.cakeActivity;
import com.example.sanjay.canteen_clint.editOrder;
import com.example.sanjay.canteen_clint.editcake;
import com.example.sanjay.canteen_clint.info.all_users;
import com.example.sanjay.canteen_clint.info.user_info;
import com.example.sanjay.canteen_clint.mainactiv_allorders;
import com.example.sanjay.canteen_clint.newChatScreen.chat_app_Activity;
import com.example.sanjay.canteen_clint.payments.payment_admin;
import com.example.sanjay.canteen_clint.payments.pending_from_app;
import com.example.sanjay.canteen_clint.pendingPayments;
import com.example.sanjay.canteen_clint.search.allOrders;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Loader extends AppCompatActivity{
    private RecyclerView recyclerView;
    private String addr;
    private ProgressBar progressBar;
    private Intent intent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recyclerlayout);
        progressBar=findViewById(R.id.progressbar);
         recyclerView = (RecyclerView)  findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        receive();
        switchIntent();
        firebase("");
    }

    private void receive() {
         Intent intent=getIntent();
         addr=intent.getStringExtra("Address");
    }
    void switchIntent(){
        switch (addr){
            case "1":intent = new Intent(Loader.this, NewFood.class);
                        break;
            case "2":intent = new Intent(Loader.this, MainActivity.class);
                break;
            case "3":intent = new Intent(Loader.this, allOrders.class);
                     intent.putExtra("OrderAddress","New Orders");
                break;
            case "4":intent = new Intent(Loader.this, allOrders.class);
                intent.putExtra("OrderAddress","All Orders");

                break;
            case "5":intent = new Intent(Loader.this, pendingPayments.class);
                break;
            case "6":intent = new Intent(Loader.this, pending_from_app.class);
                break;
            case "7":intent = new Intent(Loader.this, cakeActivity.class);
                break;
            case "8":intent = new Intent(Loader.this, editcake.class);
                break;
            case "9":intent = new Intent(Loader.this, chat_app_Activity.class);
                break;
            case "11":intent = new Intent(Loader.this, MainActivity.class);
                intent.putExtra("INFO","INFO");

                break;
           // case "11":intent = new Intent(Loader.this, all_users.class);

            //    break;
            case "14":intent = new Intent(Loader.this, payment_admin.class);
                break;

        }
    }
    void firebase(String keys){
        progressBar.setVisibility(View.VISIBLE);
       // query=mDatabase.orderByChild("OrderNo").startAt(keys).endAt(keys+"\uf8ff");
          DatabaseReference mDatabase=FirebaseDatabase.getInstance().getReference("Canteen");

        FirebaseRecyclerAdapter<album_allorders, mainactiv_allorders.FoodViewHolder> FBRA=new FirebaseRecyclerAdapter<album_allorders, mainactiv_allorders.FoodViewHolder>(
                album_allorders.class,
                R.layout.all_users,
                mainactiv_allorders.FoodViewHolder.class,
                mDatabase
        )
        {

            @Override
            protected void populateViewHolder(mainactiv_allorders.FoodViewHolder viewHolder, final album_allorders model, int position) {
                progressBar.setVisibility(View.GONE);
                 viewHolder.setUserImage(model.getCanteenImage());
                viewHolder.setUser_name(model.getCanteen());
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                      //  Intent intent = new Intent(Loader.this, user_info.class);
                        if (model.getCanteen() != null) {
                            intent.putExtra("Address",   model.getCanteen());

                            startActivity(intent);

                        } else {
                            Toast.makeText(Loader.this, "Failed to load user info,might be an old account", Toast.LENGTH_SHORT).show();
                        }


                    }


                });
            }
        };
        recyclerView.setAdapter(FBRA);

    }

}
