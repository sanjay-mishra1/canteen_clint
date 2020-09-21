package com.example.sanjay.canteen_clint.payments;

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
import android.widget.TextView;

import com.example.sanjay.canteen_clint.R;
import com.example.sanjay.canteen_clint.adapter_allorders;
import com.example.sanjay.canteen_clint.album_allorders;
import com.example.sanjay.canteen_clint.mainactiv_allorders;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import static com.example.sanjay.canteen_clint.payments.pending_from_app.canteen;

public class payment_completed_list extends Fragment {
    private adapter_allorders adapter;
    RecyclerView recyclerView;
    View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
          view= inflater.inflate(R.layout.recyclerlayout, container, false);
          recyclerView=view.findViewById(R.id.recycler_view);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setAdapter(adapter);
        loadfirebase(recyclerView,view);

            return view;
    }
    static String Changedate(String date){

        String month=date.substring(0,3);
        String year=date.substring(date.indexOf(","),date.length());
        Log.e("Date change ","D -"+date+"m -"+month+" y"+year);
        year=year.substring(0,year.indexOf("20")+4);
        Log.e("Date change "," y"+year);
        return month+" "+year;
    }
String charat(){
        StringBuilder a= new StringBuilder();
        char at;
        for (int i=0;i<4;i++) {
         at=   pending_from_app.filter.charAt(i);
         a.append(at);
        }
        return a.toString();
}
    void loadfirebase(RecyclerView recyclerView, final View  view){
         DatabaseReference databaseReference= FirebaseDatabase.getInstance().
                getReference("Transactions/"+canteen+"/Payment_Received");
            Query query;
         if (pending_from_app.filter==null)
             query=databaseReference;
             else
                 {
try {
    query = databaseReference.
            orderByChild("key").
            equalTo(charat())
             ;

    Log.e("Check date load ",charat());

}catch (Exception e){          query=databaseReference;}
                 }

        FirebaseRecyclerAdapter<album_allorders, mainactiv_allorders.FoodViewHolder> FBRA=new FirebaseRecyclerAdapter<album_allorders, mainactiv_allorders.FoodViewHolder>(
                album_allorders.class,
                R. layout.payments,
                mainactiv_allorders.FoodViewHolder.class,
              query

        ) {
            @Override
            protected void populateViewHolder(final mainactiv_allorders.FoodViewHolder viewHolder, final album_allorders model, int position) {
                view.findViewById(R.id.progressbar).setVisibility(View.GONE);
                viewHolder. mView.findViewById(R.id.t1).setVisibility(View.GONE);

                viewHolder.setTime(model.getTime());
                TextView textView=   viewHolder.mView.findViewById(R.id.textView5);
                textView.setText(String.format("Amount            %s ", getString(R.string.rupeesSymbol)));
             try {
                 textView.append( model.getTotalAmount());
             }catch (Exception e){}

                viewHolder.mView.findViewById(R.id.totalTrasaction).setVisibility(View.GONE);
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(getContext(),show_orders.class);
                        intent.putExtra("Date_Node",model.getTime());
                        intent.putExtra("Address",canteen);

                        startActivity(intent);
                    }
                });
            }
        };
        recyclerView.setAdapter(FBRA);
    }

    @Override
    public void onResume() {
        super.onResume();
        try {try {


            TextView textView = getActivity().findViewById(R.id.monthfilter);
            if (!textView.getText().toString().trim().toUpperCase().contains("PAY")) {


                if (pending_from_app.filter == null)
                    textView.setText(R.string.all);
                else textView.setText(dayName(pending_from_app.filter, "yyMM"));
            }
            }catch(Exception ignored){
            }

            loadfirebase(recyclerView,view);

            Log.e("Complete list ","Resumed filter is "+pending_from_app.filter);
        }catch (Exception e){
            Log.e("Complete list ","Resumed filter is exception");
        }

    }
    public String dayName(String inputDate, String format) {
        Date date = null;
        try {
            date = new SimpleDateFormat(format,Locale.UK).parse(inputDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
         return new SimpleDateFormat("MMM yyyy",Locale.UK).format(date);
    }
}
