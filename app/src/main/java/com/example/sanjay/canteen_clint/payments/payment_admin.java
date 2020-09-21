package com.example.sanjay.canteen_clint.payments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.sanjay.canteen_clint.R;
import com.example.sanjay.canteen_clint.adapter_allorders;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

import static com.example.sanjay.canteen_clint.extra_classes.Constants.Canteen;
import static com.example.sanjay.canteen_clint.payments.pending_from_app.canteen;
public class payment_admin extends AppCompatActivity
{
    private DataSnapshot dataSnapshot;
    public static long totalAmount=0;
    public static long totalamountFilters=0;
    private adapter_allorders adapter;
    public static long tax=0;
    public static long taxfilters=0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payments_from_app);
        TextView textView=findViewById(R.id.monthfilter);
        textView.setText(R.string.pay);
        textView.setVisibility(View.VISIBLE);
        receive();
        load_total_pending_payments();
          Load_filters();
        FragmentManager FM = getSupportFragmentManager();
        FragmentTransaction FT;
         FT = FM.beginTransaction();
        FT.replace(R.id.container, new tab_admin()).commit();

    }
    void receive(){
        Intent intent=getIntent();
        if (Canteen.equalsIgnoreCase("admin"))
            canteen= intent.getStringExtra("Address");

        else
            canteen=Canteen;
    }
    void setTotalPayment( final DatabaseReference databaseReference, final String taxs){
        databaseReference.child("Transactions").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               // long total=0;
             try {
                 tax+=Long.valueOf(taxs);
             }catch (Exception e){}
                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    try {
                        totalAmount+=Long.valueOf( (String)dataSnapshot1.child("Payment").getValue());
                    }catch (Exception e){e.printStackTrace();}
                    Log.e("PendingListAdmin", "Payment "+dataSnapshot1.child("Payment").getValue()+" total is "+totalAmount+" tax is "+taxs+" Orderno"+databaseReference.toString());


                }
                try {setDataForTotalAmount(Long.parseLong(taxs));
                }catch (Exception ignored){}


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    void setDataForTotalAmount(long taxs){
        Log.e("PendingListAdmin","Inside SetDataFor totalAmount"+totalAmount+" tax"+tax);
        totalAmount-=taxs;
        TextView textViewamount=findViewById(R.id.TotalAmount);

        TextView  textViewtax=findViewById(R.id.TotalTax);

        TextView textView1=findViewById(R.id.monthfilter);
        if (textView1.getText().toString().trim().contains(getString(R.string.pay))){
            textViewamount.setText( getFormatedAmount(totalAmount));
            textViewtax.setText(String.format("+ %s TAX", getFormatedAmount(tax)));
            // TextView textView2=findViewById(R.id.TotalAmount);
            //  textView.setText(getFormatedAmount(totalamountFilters));
            // textView2=findViewById(R.id.TotalTax);
            //textView1.setText(String.format("+ %s TAX", getFormatedAmount(taxfilters)));
        }
        if (totalAmount>0) {
             findViewById(R.id.monthfilter).setVisibility(View.VISIBLE);
            findViewById(R.id.TotalTax).setVisibility(View.VISIBLE);
        }

    }
    void load_total_pending_payments(){
        totalAmount=0;
          tax=0;
        FirebaseDatabase.getInstance().getReference("Transactions/"+canteen+"/"+"Pending_Payments").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int count=0;
                findViewById(R.id.progressbar).setVisibility(View.GONE);
                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    count++;
                    setTotalPayment(FirebaseDatabase.getInstance().getReference("Transactions/"+canteen+"/"+"Pending_Payments/"+
                            dataSnapshot1.child("OrderNo").getValue()),(String) dataSnapshot1.child("OtherPayment").getValue());

                /* */
                }

             /* */

              }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    void Load_filters(){
        FirebaseDatabase.getInstance().getReference("Transactions/"+canteen+"/Payment_Received").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot1) {
                dataSnapshot=dataSnapshot1;
                calculateAmount( );


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }



    private String getFormatedAmount(long amount){
        return getString(R.string.rupeesSymbol)+" "+NumberFormat.getNumberInstance(Locale.UK).format(amount)+".00";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        totalamountFilters=0;
        taxfilters=0;
        pending_from_app.filter=null;
    }

    void calculateAmount(){
        totalamountFilters = 0;
        taxfilters = 0;
        String filt="";
        if (pending_from_app.filter!=null)
            filt=pending_from_app.filter;
        Log.e("Filters","From payment admin filter "+filt);
         for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
            try {
               if (dataSnapshot1.child("key").getValue().toString().trim().
                       contains(filt)) {
                   totalamountFilters += Long.valueOf((String) dataSnapshot1.child("TotalAmount").getValue());

                   if (Objects.requireNonNull(dataSnapshot1.child("OtherPayment").exists())
                           )
                       taxfilters += Long.valueOf((String) dataSnapshot1.child("OtherPayment").getValue());
               }
            }catch (Exception e){Log.e("Filters","Exception "+e.toString());
           }

         }
         totalamountFilters-=taxfilters;
        TextView textView1=findViewById(R.id.monthfilter);
        if (!textView1.getText().toString().trim().contains(getString(R.string.pay))){
            TextView textView=findViewById(R.id.TotalAmount);
            textView.setText(getFormatedAmount(totalamountFilters));
            textView=findViewById(R.id.TotalTax);
            textView.setText(String.format("+ %s TAX", getFormatedAmount(taxfilters)));
       }
    }

    protected    void  change_amount(){

        TextView textView=findViewById(R.id.TotalAmount);
        textView.setText( getFormatedAmount(totalamountFilters));


    }
    public void action_clicked(View view) {
            TextView textView=findViewById(R.id.monthfilter) ;
            if (textView.getText().toString().trim().contains(getString(R.string.pay))){
                Intent intent=new Intent(this,show_order_admin.class);
                startActivity(intent);
        }else{openDialog();
         }
    }
    void openDialog() {
        startActivity(new Intent(this, filters.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
      try {
          calculateAmount( );
      }catch (Exception ignored) {}

    }
}
