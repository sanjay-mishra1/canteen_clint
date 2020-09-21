package com.example.sanjay.canteen_clint.payments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.sanjay.canteen_clint.R;
import com.example.sanjay.canteen_clint.album_allorders;
import com.example.sanjay.canteen_clint.mainactiv_allorders;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import static com.example.sanjay.canteen_clint.payments.pending_from_app.canteen;

public class filters extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recyclerlayout);

        DatabaseReference databaseReference= FirebaseDatabase.getInstance().
                getReference("Transactions/"+canteen+"/Payment_Received");
        RecyclerView recyclerView= findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new
                LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
        findViewById(R.id.t2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pending_from_app.filter=null;
                finish();
            }
        });
        findViewById(R.id.appbar).setVisibility(View.VISIBLE);

        recyclerView.setAdapter(    new MyAdapter(album_allorders.class, R.layout.payments,
        mainactiv_allorders.FoodViewHolder.class, databaseReference,this,(ProgressBar)findViewById(R.id.progressbar),(RelativeLayout) findViewById(R.id.t2)));

    }

    public void endActivity(View view) {
        finish();
    }
}
