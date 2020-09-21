package com.example.sanjay.canteen_clint.info;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sanjay.canteen_clint.R;
import com.example.sanjay.canteen_clint.adapter_allorders;
import com.example.sanjay.canteen_clint.album_allorders;
import com.example.sanjay.canteen_clint.editOrder;
import com.example.sanjay.canteen_clint.mainactiv_allorders;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import static com.example.sanjay.canteen_clint.info.user_info.canteen;

public class orders extends Fragment{
        private adapter_allorders adapter;

        @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycler_allorders, container, false);
            view.findViewById(R.id.progressFood).setVisibility(View.GONE);
    view.        findViewById(R.id.appbar).setVisibility(View.GONE);

            firebase_for_order(FirebaseDatabase.getInstance().getReference().
             child("User Informations/"+user_info.uid+"/Orders/All orders"),view);
        return view;
    }
    public void firebase_for_order(DatabaseReference child ,View view) {
     RecyclerView recyclerView=view. findViewById(R.id.recycler_view);
        recyclerView.setVisibility(View.VISIBLE);
        // findViewById(R.id.recycler_view_order).setVisibility(View.GONE);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(adapter);

        //         child("Orders/New Orders");
        FirebaseRecyclerAdapter<album_allorders, com.example.sanjay.canteen_clint.mainactiv_allorders.FoodViewHolder> FBRA=new FirebaseRecyclerAdapter<album_allorders, com.example.sanjay.canteen_clint.mainactiv_allorders.FoodViewHolder>(
                album_allorders.class,
                R.layout.new_orders_layout,
                com.example.sanjay.canteen_clint.mainactiv_allorders.FoodViewHolder.class,
                child)
        {
            @Override
            protected void populateViewHolder(mainactiv_allorders.FoodViewHolder viewHolder, final album_allorders model, int position) {
                //   progressBar.setVisibility(View.GONE);
                Log.e("User info","Inside order "+model.getOrderNo() );
                viewHolder.mView.findViewById(R.id.user_cred).setVisibility(View.GONE);
                viewHolder.setOrderNo(model.getOrderNo());
                viewHolder.setTime(model.getDelivery());

                viewHolder.setStatus(model.getStatus());

                viewHolder.setPayment(model.getPayment());
                try {
                    viewHolder.setPayment2(String.valueOf((Integer.parseInt(model.getTotalAmount()) - Integer.parseInt(model.getPayment()))),getContext());

                    viewHolder.setMore(String.valueOf((Integer.parseInt(model.getTotalFood()) - 4)));
                }catch (Exception ignored){}
                // viewHolder.setMore(String.valueOf(model.getTotalFood()));
                viewHolder.setImage0(model.getImage0(),getContext());
                viewHolder.setImage1(model.getImage1(),getContext());
                viewHolder.setImage2(model.getImage2(),getContext());
                viewHolder.setImage3(model.getImage3(),getContext());

                viewHolder.Relative().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(getContext(),editOrder.class);
                        intent.putExtra("OrderNo",model.getOrderNo());
                        intent.putExtra("Uid",model.getUserId());
                        intent.putExtra("Address",canteen);
                        startActivity(intent);
                    }
                });


            }


        };
        recyclerView.setAdapter(FBRA);

    }
}
