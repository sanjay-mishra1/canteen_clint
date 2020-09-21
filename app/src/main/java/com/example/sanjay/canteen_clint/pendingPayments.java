package com.example.sanjay.canteen_clint;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.sanjay.canteen_clint.transactions.MyAdapter;
import com.example.sanjay.canteen_clint.transactions.MyAdapter_All_Transaction;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.example.sanjay.canteen_clint.extra_classes.Constants.Canteen;

public class pendingPayments extends AppCompatActivity {
    RecyclerView recyclerView;
    private adapter_allorders adapter;
    DatabaseReference firebase;
    private BottomNavigationView bottomNavigationView;
    private String address;

    BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.PendingPayments:
                  //   firebase1("Orders/New Orders",1,R.layout.pendingpayments);
                    RecyclerView recyclerView= findViewById(R.id.recycler_view);
                    RecyclerView.LayoutManager mLayoutManager = new
                            LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false);
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());

                    recyclerView.setAdapter(    new MyAdapter(album_allorders.class, R.layout.payments,
                            mainactiv_allorders.FoodViewHolder.class,
                            FirebaseDatabase.getInstance().getReference().child("Orders/"+address+"/"+"New Orders")
                            ,getApplicationContext(),(ProgressBar)findViewById(R.id.progressbar),address));

                    return true;
                case R.id.Trasactions:
                      recyclerView= findViewById(R.id.recycler_view);
                     mLayoutManager = new
                            LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false);
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setNestedScrollingEnabled(false);
                    recyclerView.setAdapter(adapter);
                    Log.e("Canteen","inside pending "+address);

                     recyclerView.setAdapter(    new MyAdapter_All_Transaction
                            (album_allorders.class, R.layout.payments,
                            mainactiv_allorders.FoodViewHolder.class,
                            FirebaseDatabase.getInstance().getReference().
                                    child("Transactions/Mit Indore Canteen"),getApplicationContext(),
                                    (ProgressBar)findViewById(R.id.progressbar),address));
                   RecyclerView recyclerView1= findViewById(R.id.recycler_view_complete_list);
                    mLayoutManager = new
                            LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false);
                    recyclerView1.setLayoutManager(mLayoutManager);
                    recyclerView1.setItemAnimator(new DefaultItemAnimator());
                    recyclerView1.setNestedScrollingEnabled(false);
                    firebase2(R.layout.payments,recyclerView1);
                     return true;

              }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.payment_layout);
        receive();
        recyclerView=findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        findViewById(R.id.progressbar).setVisibility(View.VISIBLE);
        RecyclerView recyclerView= findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager1 = new
                LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(    new MyAdapter(album_allorders.class, R.layout.payments,
                mainactiv_allorders.FoodViewHolder.class,
                FirebaseDatabase.getInstance().getReference().child("Orders/"+address+"/New Orders"),getApplicationContext(),(ProgressBar)findViewById(R.id.progressbar),address ));


        //firebase1("Orders/New Orders",1,R.layout.pendingpayments);
         BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }
    void receive(){
        Intent intent=getIntent();
       // String addr=intent.getStringExtra("OrderAddress");
       // TextView textView=findViewById(R.id.title);
       // textView.setText(addr);
        if (Canteen.equalsIgnoreCase("admin"))
            address= intent.getStringExtra("Address");

        else
            address=Canteen;

    }

    void firebase2(int layout,RecyclerView recyclerView){
        firebase= FirebaseDatabase.getInstance().getReference().child("Transactions/"+address+"/Payments");
        final DatabaseReference finalFoodDatabase = firebase;
        FirebaseRecyclerAdapter<album_allorders, mainactiv_allorders.FoodViewHolder> FBRA=new FirebaseRecyclerAdapter<album_allorders, mainactiv_allorders.FoodViewHolder>(
                album_allorders.class,
               layout,
                mainactiv_allorders.FoodViewHolder.class,
                firebase

        ) {
            @Override
            protected void populateViewHolder(final mainactiv_allorders.FoodViewHolder viewHolder, final album_allorders model, int position) {

                    findViewById(R.id.progressbar).setVisibility(View.GONE);

                    viewHolder.setOrderNo(model.getOrderNo());
                    viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(pendingPayments.this, TransactionView.class);
                            intent.putExtra("OrderNo", model.getOrderNo());
                            intent.putExtra("Address", address);
                            intent.putExtra("Outside", false);
                            startActivity(intent);
                        }
                    });
                    finalFoodDatabase.addValueEventListener(new ValueEventListener() {
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

                }

        };

        recyclerView.setAdapter(FBRA);
    }
    void firebase1(final String Address, final int from, int layout){
        firebase= FirebaseDatabase.getInstance().getReference().child(Address);
         FirebaseRecyclerAdapter<album_allorders, mainactiv_allorders.FoodViewHolder> FBRA=new FirebaseRecyclerAdapter<album_allorders, mainactiv_allorders.FoodViewHolder>(
                album_allorders.class,
                layout,
                mainactiv_allorders.FoodViewHolder.class,
                firebase.orderByChild("Extra_Payment_Return").startAt("no")

        ) {
            @Override
            protected void populateViewHolder(final mainactiv_allorders.FoodViewHolder viewHolder, final album_allorders model, int position) {

                if (from==1){
                    findViewById(R.id.progressbar).setVisibility(View.GONE);

                    viewHolder.setOrderNo(model.getOrderNo());
                    viewHolder.setStatus(model.getStatus());
                    try {
                        extractRemainingPayments(model.getUId(), Address, model.getStatus(),
                                model.getOrderNo(), viewHolder.returnTextview(), Integer.parseInt(model.getPayment()),
                                Integer.parseInt(model.getTotalAmount()), model.getExtra_Payment_Return(),
                                model.getPay_Return_OnCancel(), viewHolder.relativeLayoutReturn());
                    }catch (Exception ignored){}
                }
            }
        };

        recyclerView.setAdapter(FBRA);
    }

    void extractRemainingPayments(String uid, String address, String status, String orderno, final TextView textView, int Payment, int TotalAmount, String ExtraPaymentPaid, String PayReturn, RelativeLayout relativeLayout){
        if (status.toUpperCase().contains("CANCEL")){
        try {
            if (PayReturn.contains("no")) {
                textView.setText("Pay Rs " + Payment + " to the clint");
            }
        }catch (Exception NullPointerException){
            DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference(address+"/"+orderno);
            databaseReference.child("Pay_Return_OnCancel").setValue("no");

            databaseReference=FirebaseDatabase.getInstance().getReference("User Informations/"+"Orders/All Orders/"+orderno);
            databaseReference.child("Pay_Return_OnCancel").setValue("no");
            textView.setText("Pay Rs " + Payment + " to the clint");
        }
        }else if (TotalAmount < Payment){
            try {
                Log.e("Payment Status","Total<payment TotalAmount "+TotalAmount+"Payment "+Payment+"   Orderno "+orderno);
                if (ExtraPaymentPaid.contains("no")) {
                    textView.setText("Pay extra Payment Rs" + ( Payment-TotalAmount) + " to the clint");
                }
            }catch (Exception NullPointerException){
                Log.e("Payment Status","Null Pointer Total<payment TotalAmount "+TotalAmount+"Payment "+Payment+"   Orderno "+orderno);
                DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference(address+"/"+orderno);
                databaseReference.child("Extra_Payment_Return").setValue("no");
                databaseReference=FirebaseDatabase.getInstance().getReference("User Informations"+"/Orders/All orders/"+orderno);
                databaseReference.child("Extra_Payment_Return").setValue("no");
              if (status.equals("Pending")||status.equals("Accepted")||status.equals("Completed")){
              databaseReference=FirebaseDatabase.getInstance().getReference("Orders/New Orders/"+orderno);
                  databaseReference.child("Extra_Payment_Return").setValue("no");
                  databaseReference=FirebaseDatabase.getInstance().getReference("User Informations"+"/Orders/Pending/"+orderno);
                  databaseReference.child("Extra_Payment_Return").setValue("no");
              }
                textView.setText(String.format("Pay extra Payment Rs%d to the clint",  Payment-TotalAmount ));
            }
        }/*else{
            Log.e("Payment Status","Nothing TotalAmount "+TotalAmount+" Payment "+Payment+"   Orderno "+orderno);
           textView.setText("No payment left");
           // relativeLayout.setVisibility(View.GONE);
        }*/


    }

}
