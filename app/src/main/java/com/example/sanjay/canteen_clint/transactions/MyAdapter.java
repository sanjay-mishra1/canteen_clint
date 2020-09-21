package com.example.sanjay.canteen_clint.transactions;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.sanjay.canteen_clint.R;
import com.example.sanjay.canteen_clint.album_allorders;
import com.example.sanjay.canteen_clint.mainactiv_allorders;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;

import java.util.ArrayList;


public class MyAdapter extends FirebaseRecyclerAdapter<album_allorders,mainactiv_allorders.FoodViewHolder>{
    private static final int LAYOUT_ONE = 1;
    private static final int LAYOUT_TWO = 2;
    private ArrayList<String> months=new ArrayList<>();
    private Context context;
     private ProgressBar progressBar;
     private String canteen;
     public MyAdapter(Class<album_allorders> modelClass,
                      int modelLayout,
                      Class<mainactiv_allorders.FoodViewHolder> viewHolderClass,
                      Query ref, Context context, ProgressBar progressBar, String Canteen) {

        super(modelClass, modelLayout, viewHolderClass, ref );
         this.context=context;
          this.progressBar=progressBar;
          canteen=Canteen;
      }


    @Override
    public mainactiv_allorders.FoodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;

        if (viewType == LAYOUT_ONE) {

            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pendingpayments, parent, false);
            return new mainactiv_allorders.FoodViewHolder(view);

        }
         else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_empty, parent, false);
            return new mainactiv_allorders.FoodViewHolder(view);

        }

    }
    private int checkData(album_allorders model){
         if (model.getStatus().toUpperCase().contains("CANCEL")&&
                 model.getPay_Return_OnCancel().toUpperCase().contains("NO")){
            return 0;
         }else
    if (Long.parseLong(model.getTotalAmount())<Long.parseLong(model.getPayment())){
             return 0;
    }else
       return 1;
    }
    @Override
    public int getItemViewType(int position){

        album_allorders model = getItem(position);

        try {
            switch (checkData(model)) {
                case 0:        Log.e("Date change "," Layout one"+months.size());

                    return LAYOUT_ONE;
                case 1:        Log.e("Date change "," Layout two"+months.size());

                    return LAYOUT_TWO;


            }
        }catch (Exception ignored){}
        return position;

    }

    @Override
    protected void populateViewHolder(final mainactiv_allorders.FoodViewHolder viewHolder, final album_allorders model, final int position) {
        progressBar.setVisibility(View.GONE);

        try {
        viewHolder.setOrderNo(model.getOrderNo());
        viewHolder.setStatus(model.getStatus());

      if (!model.getStatus().toUpperCase().contains("CANCEL"))
          viewHolder.setprice("Return "+context.getString(R.string.rupeesSymbol)+" "+String.valueOf( Long.valueOf(model.getPayment())-Long.valueOf(model.getTotalAmount()))+" to the user");
      else viewHolder.setprice("Return "+context.getString(R.string.rupeesSymbol)+" "+model.getPayment());

        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context,pay_extra.class);
                intent.putExtra("UID",model.getUserId());
                intent.putExtra("ORDERNO",model.getOrderNo());
                intent.putExtra("Address",canteen);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
        /*    extractRemainingPayments(model.getUId(), Address, model.getStatus(),
                    model.getOrderNo(), viewHolder.returnTextview(), Integer.parseInt(model.getPayment()),
                    Integer.parseInt(model.getTotalAmount()), model.getExtra_Payment_Return(),
                    model.getPay_Return_OnCancel(), viewHolder.relativeLayoutReturn());*/
        }catch (Exception ignored){}
    }
}





