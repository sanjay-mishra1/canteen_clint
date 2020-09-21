package com.example.sanjay.canteen_clint.payments;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sanjay.canteen_clint.R;
import com.example.sanjay.canteen_clint.TransactionView;
import com.example.sanjay.canteen_clint.adapter_allorders;
import com.example.sanjay.canteen_clint.album_allorders;
import com.example.sanjay.canteen_clint.mainactiv_allorders;
import com.example.sanjay.canteen_clint.pendingPayments;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import static com.example.sanjay.canteen_clint.payments.pending_from_app.canteen;

 import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
 import java.util.Calendar;
 import java.util.Date;
import java.util.Locale;

public class show_order_admin extends AppCompatActivity {
    private adapter_allorders adapter;
    private ArrayList<String> ordernos;
    private ArrayList<Long> Price;
    private ArrayList<Long> Tax;
    private String SomeAmount="0";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ordernos=new ArrayList<>();
        Price=new ArrayList<>();
        Tax=new ArrayList<>();
        setContentView(R.layout.admin_order_view);
        RecyclerView recyclerView=findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(adapter);
        TextView textView=findViewById(R.id.TotalAmount);
        textView.setText(String.valueOf(getFormatedAmount(payment_admin.totalAmount)));
        try {
            loadfirebase(recyclerView);
        }catch (Exception e){
            finish();
            Toast.makeText(this,"Failed to load the list.Please Contact the administrator",Toast.LENGTH_SHORT).show();
        }

    }
    void loadfirebase(RecyclerView recyclerView){
        final DatabaseReference databaseReference= FirebaseDatabase.getInstance().
                getReference("Transactions/"+canteen+"/"+"Pending_Payments");
        FirebaseRecyclerAdapter<album_allorders, mainactiv_allorders.FoodViewHolder> FBRA=new FirebaseRecyclerAdapter<album_allorders, mainactiv_allorders.FoodViewHolder>(
                album_allorders.class,
                R. layout.payments,
                mainactiv_allorders.FoodViewHolder.class,
                databaseReference

        ) {
            @Override
            protected void populateViewHolder(final mainactiv_allorders.FoodViewHolder viewHolder, final album_allorders model, int position) {
                findViewById(R.id.progressbar).setVisibility(View.GONE);
                //viewHolder.mView. findViewById(R.id.t3).setVisibility(View.GONE);
                viewHolder.setOrderNo(model.getOrderNo());
                viewHolder.setTime(model.getTime());
                if (model.getOrderNo() != null) {
                    if (returnIndex(model.getOrderNo()) == -1) {
                        viewHolder.mView.findViewById(R.id.relative).setBackgroundColor(Color.TRANSPARENT);
                    } else
                        viewHolder.mView.findViewById(R.id.relative).setBackgroundColor(getResources().getColor(R.color.colorAccent));
                }
                final TextView textView = viewHolder.mView.findViewById(R.id.textView5);
                textView.setText(String.format("Amount            %s ", getString(R.string.rupeesSymbol)));
                textView.setTextColor(Color.RED);

                try {
                    setTotalPayment(textView, databaseReference.child(model.getOrderNo()), "0", false);
                } catch (Exception ignored) {
                }

                if (model.getOrderNo() != null)
                {
                    viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int index = returnIndex(model.getOrderNo());

                            if (index != -1) {    //int index=Collections.singletonList(ordernos).toString().indexOf(model.getOrderNo());
                                //    int index=  Arrays.asList(ordernos).indexOf(model.getOrderNo());
                                Log.e("Order clicked ", "remove Index is " + index);

                                ordernos.remove(index);
                                Price.remove(index);
                                Tax.remove(index);
                                viewHolder.mView.findViewById(R.id.relative).setBackgroundColor(Color.TRANSPARENT);
                                totalAmount();

                            } else {


                                viewHolder.mView.findViewById(R.id.relative).setBackgroundColor(getResources().getColor(R.color.colorAccent));
                                ordernos.add(model.getOrderNo());
                                String tax = model.getOtherPayment();
                                try {
                                    if (tax == null)
                                        setTotalPayment(textView, databaseReference.child(model.getOrderNo()), "0", true);
                                    else
                                        setTotalPayment(textView
                                                , databaseReference.child(model.getOrderNo()), tax, true);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                //long price;
                         /* */
                            }
                        }
                    });
            }
            }
        };
        recyclerView.setAdapter(FBRA);
    }
    void setTotalPayment(final TextView textView, final DatabaseReference databaseReference, final String tax, final boolean addtoarray){
        databaseReference.child("Transactions").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long total=0;
                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    try {
                        total+=Long.valueOf( (String)dataSnapshot1.child("Payment").getValue());
                    }catch (Exception ignored){}
                    Log.e("PendingList","Key is "+dataSnapshot1.getKey()+" total is "+total+" tax is "+tax);


                }
                try {
                                              /* try {
                               price= Long.parseLong(model.getTotalAmount());
                           }catch (Exception e){
                               price  =0;
                           }*/
             if (addtoarray) {
                 long taxs;

                 try {
                     taxs = Long.parseLong(tax);
                 } catch (Exception e) {
                     taxs = 0;
                 }
                 Tax.add(taxs);
                 Price.add(total - taxs);
                 Log.e("Order clicked ", "added size is " + Price.size());
                 totalAmount();
             }else{
                 textView.append(String.valueOf( total-Long.valueOf(tax))+".00");

             }

            }catch (Exception ignored){}
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    int returnIndex(String search){
        int i=0;
        for (String s:ordernos){
            if (search.equals(s))
                return i;
            i++;
        }
        return -1;
    }
    void totalAmount(){
        long  t=0;
        for (Long Price1:Price){
            t+=Price1;
        }
        if (t<=0){
            findViewById(R.id.pay).setVisibility(View.GONE);
        }else{
            Button b=findViewById(R.id.pay);
            b.setVisibility(View.VISIBLE);
            b.setText((Price.size()+" Orders "+ getFormatedAmount(t)));
        }
    }

    public void payAllClicked(View view) {
       // Toast.makeText(this,"Pay all clicked",Toast.LENGTH_SHORT).show();
     /*   Calendar c = Calendar.getInstance();

        SimpleDateFormat date = new SimpleDateFormat("yyMMddhhmmss",Locale.UK);
         String today = date.format(c.getTime());
        final String time=DateFormat.getDateTimeInstance().format(new Date());
        FirebaseDatabase.getInstance().getReference("Transactions/Payment_Received").
                child(today).child("Time").setValue(time);
        FirebaseDatabase.getInstance().getReference("Transactions/Payment_Received").child(today).
                child("TotalAmount").setValue(String.valueOf(payment_admin.totalAmount -
                payment_admin.tax));

        FirebaseDatabase.getInstance().getReference("Transactions/Pending_Payments").
                addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                {for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    FirebaseDatabase.getInstance().getReference("Transactions/Payments").
                            child((String) Objects.requireNonNull(dataSnapshot1.child("OrderNo").getValue()))
                            .child("APP_PAYMENT_RECEIVED").setValue(time);

                }
                    FirebaseDatabase.getInstance().getReference("Transactions/Payments").setValue(dataSnapshot.getValue(), new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError firebaseError, DatabaseReference firebase) {
                            if (firebaseError != null) {
                                Log.e("Moving node","Move not moved ");
                            } else {
                                 Log.e("Moving node","Move moved successfully");
                                System.out.println("Success");

                            }
                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Moving node","Move gives an error "+databaseError.toString());
            }
        });*/
    }
    String charat(String a){
         char at;
        StringBuilder change= new StringBuilder();
        for (int i = 0; i<4; i++) {
            at=   a.charAt(i);
            change.append(at);
        }
        //a = aBuilder.toString();
        return change.toString();
    }
    public void payAmountClicked(View view) {
          final String time=DateFormat.getDateTimeInstance().format(new Date());
        Calendar c = Calendar.getInstance();
        findViewById(R.id.progressbar).setVisibility(View.VISIBLE);
        SimpleDateFormat date = new SimpleDateFormat("yyMMddhhmmss",Locale.UK);
        String today = date.format(c.getTime());
         FirebaseDatabase.getInstance().getReference("Transactions/"+canteen+"/"+"Payment_Received").
                child(today).child("Time").setValue(time);

        String small=charat(today);
        FirebaseDatabase.getInstance().getReference("Transactions/"+canteen+"/"+"Payment_Received").
                child(today).child("key").setValue(small);

        FirebaseDatabase.getInstance().getReference("Transactions/"+canteen+"/"+"Payment_Received").child(today).
                child("TotalAmount").setValue(SomeAmount);
        long totalTax=0;
        for (long tax:Tax){
            totalTax+=tax;
        }
        FirebaseDatabase.getInstance().getReference("Transactions/"+canteen+"/"+"Payment_Received").child(today).
                child("OtherPayment").setValue(String.valueOf(totalTax));

        for (final String orderno:ordernos) {


    FirebaseDatabase.getInstance().getReference("Transactions/"+canteen+"/"+"Pending_Payments/" + orderno).
            addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {


                    {
                            FirebaseDatabase.getInstance().getReference("Transactions/"+canteen+"/"+"Payments").child(orderno).
                                setValue(dataSnapshot.getValue(), new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError firebaseError, DatabaseReference firebase) {
                                        if (firebaseError != null) {
                                            Log.e("Moving node", "Move not moved ");
                                        } else {
                                            FirebaseDatabase.getInstance().getReference("Transactions/"+canteen+"/"+"Payments/" + orderno).
                                                    child("APP_PAYMENT_RECEIVED").setValue(time).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    FirebaseDatabase.getInstance().getReference("Transactions/"+canteen+"/"+"Pending_Payments/" + orderno).setValue(null);
                                                   findViewById(R.id.progressbar).setVisibility(View.GONE);
                                                    finish();
                                                }
                                            });


                                        }
                                    }
                                });
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("Moving node", "Move gives an error " + databaseError.toString());
                }
            });
}
     }
    private String getFormatedAmount(long amount){
        try {
            SomeAmount=String.valueOf(amount);
        }catch (Exception e){
            SomeAmount="0";
        }
        return getString(R.string.rupeesSymbol)+" "+ NumberFormat.getNumberInstance(Locale.UK).format(amount)+".00";
    }

    public void endActivity(View view) {
        finish();
    }
}
