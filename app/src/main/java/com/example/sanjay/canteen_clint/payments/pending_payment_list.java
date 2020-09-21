package com.example.sanjay.canteen_clint.payments;

import android.graphics.Color;
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
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import static com.example.sanjay.canteen_clint.payments.pending_from_app.canteen;

public class pending_payment_list extends Fragment {
    private adapter_allorders adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.recyclerlayout, container, false);
        RecyclerView recyclerView=view.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
         recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(adapter);
        loadfirebase(recyclerView,view);

return view;
    }
    void loadfirebase(RecyclerView recyclerView, final View view){
        final DatabaseReference databaseReference= FirebaseDatabase.getInstance().
                getReference("Transactions/"+canteen+"/Pending_Payments");
          FirebaseRecyclerAdapter<album_allorders, mainactiv_allorders.FoodViewHolder> FBRA=new FirebaseRecyclerAdapter<album_allorders, mainactiv_allorders.FoodViewHolder>(
                album_allorders.class,
                R. layout.payments,
                mainactiv_allorders.FoodViewHolder.class,
                databaseReference

        ) {
            @Override
            protected void populateViewHolder(final mainactiv_allorders.FoodViewHolder viewHolder, final album_allorders model, int position) {
                view.findViewById(R.id.progressbar).setVisibility(View.GONE);
                viewHolder.setOrderNo(model.getOrderNo());
                viewHolder.setTime(model.getTime());
                TextView textView=   viewHolder.mView.findViewById(R.id.textView5);
                textView.setText(String.format("Amount            %s ", getString(R.string.rupeesSymbol)));
                try {textView.setTextColor(Color.RED);
                 //   textView.append( model.getPayment());
                    String tax=model.getOtherPayment();
                    if (tax==null)
                    setTotalPayment(textView,databaseReference.child(model.getOrderNo()),"0");
                    else
                        setTotalPayment(textView,databaseReference.child(model.getOrderNo()),tax);
                }catch (Exception e){
                    e.printStackTrace();
                }

                //viewHolder.mView.findViewById(R.id.t3).setVisibility(View.GONE);

            }
        };
      recyclerView.setAdapter(FBRA);
    }
    void setTotalPayment(final TextView textView, final DatabaseReference databaseReference, final String tax){
        databaseReference.child("Transactions").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long total=0;
              for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                try {
                    total+=Long.valueOf( (String)dataSnapshot1.child("Payment").getValue());
                }catch (Exception ignored){}
                  Log.e("PendingList","Key is "+dataSnapshot1.getKey()+" total is "+total+" tax is "+tax);

                 /* databaseReference.child((String) Objects.requireNonNull(dataSnapshot1.child("TransactionNumber").getValue())).addListenerForSingleValueEvent(new ValueEventListener() {
                     @Override
                     public void onDataChange(DataSnapshot dataSnapshot) {
                        try {
                            total[0] +=Long.valueOf( (String)dataSnapshot.child("Payment").getValue());
                        }catch (Exception ignored){}
                     }

                     @Override
                     public void onCancelled(DatabaseError databaseError) {

                     }
                 });*/
             }
try {
                  textView.append(String.valueOf( total-Long.valueOf(tax)));
}catch (Exception ignored){}
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
