package com.example.sanjay.canteen_clint.transactions;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.example.sanjay.canteen_clint.R;
import com.example.sanjay.canteen_clint.TransactionView;
import com.example.sanjay.canteen_clint.album_allorders;
import com.example.sanjay.canteen_clint.mainactiv_allorders;
import com.example.sanjay.canteen_clint.payments.filters;
import com.example.sanjay.canteen_clint.payments.pending_from_app;
import com.example.sanjay.canteen_clint.pendingPayments;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class MyAdapter_All_Transaction extends FirebaseRecyclerAdapter<album_allorders,mainactiv_allorders.FoodViewHolder>{
    private static final int LAYOUT_ONE = 1;
    private static final int LAYOUT_TWO = 2;
    private ArrayList<String> months=new ArrayList<>();
    private Context context;
    private RelativeLayout textView;
    private ProgressBar progressBar;
    private Query ref;
    private String canteen;
    public MyAdapter_All_Transaction(Class<album_allorders> modelClass,
                                     int modelLayout,
                                     Class<mainactiv_allorders.FoodViewHolder> viewHolderClass,
                                     Query ref, Context context, ProgressBar progressBar, String address) {

        super(modelClass, modelLayout, viewHolderClass, ref );
        this.context=context;
        this.ref=ref;
        this.progressBar=progressBar;
        canteen=address;
    }


    @Override
    public mainactiv_allorders.FoodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;

        if (viewType == LAYOUT_ONE) {

            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.payments, parent, false);
            return new mainactiv_allorders.FoodViewHolder(view);

        }
        else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_empty, parent, false);
            return new mainactiv_allorders.FoodViewHolder(view);

        }

    }
    private int checkData(album_allorders model){
        Log.e("Canteen","inside check "+canteen);
        if (model.getOrderNo()!=null){
            return 0;
        }else
            return 1;

    }
    @Override
    public int getItemViewType(int position){

        album_allorders model = getItem(position);

        try {
            switch (checkData(model)) {
                case 0:        Log.e("OrderList "," Layout one"+months.size()+" canteen is "+canteen);

                    return LAYOUT_ONE;
                case 1:        Log.e("OrderList "," Layout two"+months.size()+" canteen is "+canteen);

                    return LAYOUT_TWO;


            }
        }catch (Exception ignored){}
        return position;

    }

    @Override
    protected void populateViewHolder(final mainactiv_allorders.FoodViewHolder viewHolder, final album_allorders model, final int position) {
        progressBar.setVisibility(View.GONE);
        Log.e("Canteen",canteen);
        try {

            viewHolder.setOrderNo(model.getOrderNo());
            viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, TransactionView.class);
                    intent.putExtra("OrderNo", model.getOrderNo());
                    intent.putExtra("Outside", true);
                    intent.putExtra("Address", canteen);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    context.  startActivity(intent);
                }
            });
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    try {
                        // viewHolder.setTotalTransactions(String.valueOf(dataSnapshot.child(model.getOrderNo()).getChildrenCount() - 4));
                        viewHolder.setTotalTransactions(String.valueOf(dataSnapshot.child(model.getOrderNo()).child("Transactions").getChildrenCount()));
                    } catch (Exception NumberFormatException) {
                        viewHolder.setTotalTransactions("0");
                    }
                    //          viewHolder.setOrderNo(model.getOrderNo());
                    viewHolder.setTime(model.getTime());
                    // dataSnapshot.getValue();//.getKey();
                    // viewHolder.setOrderNo(String.valueOf(dataSnapshot.child(String.valueOf(dataSnapshot.getChildren()))));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });


         }catch (Exception ignored){}
    }
}





