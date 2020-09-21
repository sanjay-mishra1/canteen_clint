package com.example.sanjay.canteen_clint.payments;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.sanjay.canteen_clint.R;
import com.example.sanjay.canteen_clint.album_allorders;
import com.example.sanjay.canteen_clint.mainactiv_allorders;
 import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.Arrays;


public class MyAdapter extends FirebaseRecyclerAdapter<album_allorders,mainactiv_allorders.FoodViewHolder>{
    private static final int LAYOUT_ONE = 1;
    private static final int LAYOUT_TWO = 2;
    private ArrayList<String> months=new ArrayList<>();
    private Context context;
    private RelativeLayout textView;
    private ProgressBar progressBar;
     MyAdapter(Class<album_allorders> modelClass,
               int modelLayout,
               Class<mainactiv_allorders.FoodViewHolder> viewHolderClass,
               Query ref, Context context, ProgressBar progressBar,RelativeLayout textView) {

        super(modelClass, modelLayout, viewHolderClass, ref );
         this.context=context;
        this. textView=textView;
         this.progressBar=progressBar;
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

    @Override
    public int getItemViewType(int position){

        album_allorders model = getItem(position);

        try {
            switch (hasDate(model.getKey())) {
                case 0:        Log.e("Date change "," Layout one"+months.size());

                    return LAYOUT_ONE;
                case 1:        Log.e("Date change "," Layout two"+months.size());

                    return LAYOUT_ONE;


            }
        }catch (Exception ignored){}
        return position;

    }

    static String Changedate(String date){

        String month=date.substring(0,3);
        String year=date.substring(date.indexOf(","),date.length());
        Log.e("Date change ","D -"+date+"m -"+month+" y"+year);
        year=year.substring(0,year.indexOf("20")+4);
        Log.e("Date change "," y"+year);
        return month+" "+year;
    }

    private int hasDate(String date){
         for (String e:months){
             if (e.equals(date))
                 return 1;

         }

        return 0;
    }

    @Override
    protected void populateViewHolder(final mainactiv_allorders.FoodViewHolder viewHolder, final album_allorders model, final int position) {
    progressBar.setVisibility(View.GONE);
    textView.setVisibility(View.VISIBLE);
         if (hasDate(model.getKey())==0){
             String time=Changedate(model.getTime());
         viewHolder.setTime(time);
       viewHolder. mView.findViewById(R.id.t1).setVisibility(View.GONE);
       viewHolder. mView.findViewById(R.id.t3).setVisibility(View.GONE);
        months.add(model.getKey());


      viewHolder.mView.findViewById(R.id.totalTrasaction).setVisibility(View.GONE);
       viewHolder.mView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               pending_from_app.filter=model.getKey();
               ((filters)context).finish();
           }
       });

   }else{
            viewHolder.mView.findViewById(R.id.relative).setVisibility(View.GONE);
         }
    }

    public Context getContext() {
        return context;

    }
}
