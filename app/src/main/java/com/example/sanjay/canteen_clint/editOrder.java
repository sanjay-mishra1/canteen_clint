package com.example.sanjay.canteen_clint;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

import static com.example.sanjay.canteen_clint.extra_classes.Constants.Canteen;

public class editOrder extends AppCompatActivity {
    private adapter_allorders adapter;

    TextView order;
    private String Status;
    private String Total_Food="0";
    private String OrderNo;
    int pay;
    //TextView deliver;
    Button save, cancel, food_delivered, completed;
    TextView remainPay;

    private DatabaseReference data;
  //   private int Total_price;
  //  private String TotalAmount;
    private String UserId;
Dialog progressDialog;
    private long other;
    private long TaxPay;
    private String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_order);
        // deliver=findViewById(R.id.deliverytime);
        showProgress_dialog(true);
        save = findViewById(R.id.save_food);
        cancel = findViewById(R.id.cancel2);
        food_delivered = findViewById(R.id.delivered);
        completed = findViewById(R.id.remainingPay);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        remainPay = findViewById(R.id.remaining);
        //   foodname=new ArrayList<String>();
        //    foodQ=new ArrayList<String>();
        //    foodprice=new ArrayList<String>();
        ReceiveData();
        //  Reference();

        data = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                open(view, "cancel");


            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                open(view, "Accepted");

            }
        });
        findViewById(R.id.remainingPay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                open(view, "Completed");

            }
        });
        food_delivered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                open(view, "Delivered");

            }
        });
    }

    void cancel() {
         data.child("Orders").child(address+"/New Orders").child(OrderNo).child("Status").setValue("Cancel");
        data.child("Orders").child(address+"/All Orders").child(OrderNo)
                .child("Status").setValue("Cancel");
        data.child("User Informations").child(UserId).
                child("Orders/All orders").child(OrderNo).child("Status").setValue("Cancel");
          data.child("User Informations").child(UserId).
                 child("Orders/Pending").child(OrderNo).child("Status").setValue("Cancel");

         data.child("User Informations").child(UserId).
                child("Orders/Pending").child(OrderNo).child("Pay_Return_OnCancel").setValue("no");
        data.child("User Informations").child(UserId).
                child("Orders/All orders").child(OrderNo).child("Pay_Return_OnCancel").setValue("no");

        data.child("Orders").child(address+"/"+"New Orders").child(OrderNo).child("Pay_Return_OnCancel").setValue("no");
        data.child("Orders").child(address+"/"+"All Orders").child(OrderNo)
                .child("Pay_Return_OnCancel").setValue("no");



        send_notifications("Your Order (" + OrderNo + ") is cancelled by the Canteen ");
        store_extra_info("Your order is cancelled by the canteen. Sorry for the inconvenience.");
        UpdateTransactionForCancellation();
        Toast.makeText(editOrder.this, " Order cancelled successfully", Toast.LENGTH_SHORT).show();
        finish();
    }
    void UpdateTransactionForCancellation(){
        TextView textView=findViewById(R.id.Orderstatus1);
        if (textView.getText().toString().trim().toUpperCase().contains("PEND")){
            sendToTransaction("0",FirebaseDatabase.getInstance().getReference("Transactions/"+address+"/"+"/Pending_Payments").child(OrderNo),
                    FirebaseDatabase.getInstance().getReference("Transactions/"+address+"/" + OrderNo),true);

        }else{
            FirebaseDatabase.getInstance().getReference("Transactions/"+address+"/"+"Pending_Payments/"+OrderNo).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()){
                        TextView textView=findViewById(R.id.OtherPayment);
                        int amt= Integer.parseInt(ExtractNumber(textView.getText().toString().trim()));
             if (amt>0) {


                String time = DateFormat.getDateTimeInstance().format(new Date());
                updateTrasaction1(FirebaseDatabase.getInstance().getReference("Transactions/"+address+"/"+"Pending_Payments/" + OrderNo), String.valueOf(System.currentTimeMillis()),
                        time,String.valueOf(amt));
                updateTrasaction1(FirebaseDatabase.getInstance().getReference("Transactions/"+address+"/"+"Payments/" + OrderNo), String.valueOf(System.currentTimeMillis()),
                        time,String.valueOf(amt));
            }
                    }else{
                        FirebaseDatabase.getInstance().getReference("Transactions/"+address+"/"+"Pending_Payments/"+OrderNo).child("OtherPayment").setValue("0");
                        FirebaseDatabase.getInstance().getReference("Transactions/"+address+"/"+"Payments/"+OrderNo).child("OtherPayment").setValue("0");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
    void updateTrasaction1(DatabaseReference d, String TransactionNO, String date,String Payment){
        d.child("OrderNo").setValue(OrderNo);
        d.child("Time").setValue(date);
        d.child("OtherPayment").setValue("0");
        d.child("Transactions/"+TransactionNO).child("TransactionNumber").setValue(TransactionNO);
        d.child("Transactions/"+TransactionNO).child("OrderNo").setValue(OrderNo);
        d.child("Transactions/"+TransactionNO).child("Payment").setValue(Payment);
        d.child("Transactions/"+TransactionNO).child("Mode").setValue("Admin_Return");
        d.child("Transactions/"+TransactionNO).child("Bank").setValue("Admin_Return");
        d.child("Transactions/"+TransactionNO).child("Time").setValue(date);
        d.child("Transactions/"+TransactionNO).child("ADMINSTATUS").setValue(false);
    }
     void send_notifications(final String Message) {
        final DatabaseReference databaseReference =
                FirebaseDatabase.getInstance().getReference("User Informations/"+ UserId + "/Notifications/" + OrderNo);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String old_message = (String) dataSnapshot.child("Message").getValue();
                try {
                    if (old_message != null) {
                        databaseReference.child("Message").setValue(old_message + "\n" + Message + " Time " + ServerValue.TIMESTAMP);
                        databaseReference.child("From").setValue("@orderno(" + OrderNo + ")");
                        databaseReference.child("Server_Time").setValue(ServerValue.TIMESTAMP);
                        databaseReference.child("Status").setValue("UNSEEN");
                    } else {
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User Informations/"+ UserId + "/Notifications");
                        databaseReference.child(OrderNo).child("Message").setValue(Message + " Time " + ServerValue.TIMESTAMP);
                        databaseReference.child(OrderNo).child("key").setValue(OrderNo);
                        databaseReference.child(OrderNo).child("From").setValue("@orderno(" + OrderNo + ")");

                        databaseReference.child(OrderNo).child("Server_Time").setValue(ServerValue.TIMESTAMP);
                        databaseReference.child(OrderNo).child("Status").setValue("UNSEEN");
                    }
                } catch (Exception e) {
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User Informations/"+ UserId + "/Notifications");
                    databaseReference.child(OrderNo).child("Message").setValue(Message + " Time " + ServerValue.TIMESTAMP);
                    databaseReference.child(OrderNo).child("key").setValue(OrderNo);
                    databaseReference.child(OrderNo).child("From").setValue("@orderno(" + OrderNo + ")");

                    databaseReference.child(OrderNo).child("Server_Time").setValue(ServerValue.TIMESTAMP);
                    databaseReference.child(OrderNo).child("Status").setValue("UNSEEN");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    void store_extra_info(final String message) {

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Orders/"+address+"/"+"All Orders/" + OrderNo);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("Extra_Info").exists()) {
                    databaseReference.child("Extra_Info").setValue(dataSnapshot.child("Extra_Info").
                            getValue() + "\n\u2022  " + message + " " + DateFormat.getDateTimeInstance().format(new Date()));
                    FirebaseDatabase.getInstance().getReference("User Informations/"+ UserId + "/Orders/All orders/" + OrderNo + "/Extra_Info")
                            .setValue(dataSnapshot.child("Extra_Info").getValue() + "\n\u2022  " + message + " " +
                                    DateFormat.getDateTimeInstance().format(new Date()));

                } else {
                    databaseReference.child("Extra_Info").setValue("\u2022  " + message + " " +
                            DateFormat.getDateTimeInstance().format(new Date()));
                    FirebaseDatabase.getInstance().getReference("User Informations/" + UserId + "/Orders/All orders/" + OrderNo + "/Extra_Info")
                            .setValue("\u2022  " + message + " " +
                                    DateFormat.getDateTimeInstance().format(new Date()));

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private String ExtractNumber(String text){
        if (text.contains("."))
            text=text.substring(0,text.indexOf("."));
        String number=text.replaceAll("[^0-9]","");
        if (number.equals(""))
            number="0";
        return (number);
    }
    void yes(final String orderStatus) {

        if (orderStatus.contains("Delivered")) {

           if (pay>0) {
               AlertDialog.Builder alert1 = new AlertDialog.Builder(this);

               alert1.setMessage("Accept payment of " + getString(R.string.rupeesSymbol) + pay
                       + "\n You can accept " + getString(R.string.rupeesSymbol) + pay + " from any means\n 1.You can click on back button and request user to pay from the Mcafe app.After his successful payment you can click on delivered button" +
                       "\n2.You can accept " + getString(R.string.rupeesSymbol) + pay + " via your paytm QR code(Note - In App transaction window it will show cash instead of Paytm)\n3.User can pay " + getString(R.string.rupeesSymbol) + pay + " via cash");
               store_extra_info("Your order is delivered");
               alert1.setPositiveButton("User has paid via Cash/My Paytm", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialogInterface, int i) {
                   data.child("Orders/"+address+"/"+"New Orders/" + OrderNo).setValue(null);
                    data.child("Orders/"+address+"/"+"All Orders/" + OrderNo + "/Status").setValue(orderStatus);
                    data.child("User Informations/"+ UserId + "/Orders/All orders/"
                            + OrderNo + "/Status").setValue(orderStatus);
                    TextView textView=findViewById(R.id.total_final2);
                    data.child("Orders/"+address+"/"+"All Orders/" + OrderNo + "/Payment").setValue(ExtractNumber(textView.getText().toString().trim()));
                    data.child("User Informations/"+ UserId + "/Orders/All orders/"
                            + OrderNo + "/Payment").setValue(ExtractNumber(textView.getText().toString().trim()));
                    data.child("User Informations/"+ UserId + "/Orders/Pending/"
                            + OrderNo).setValue(null);

                    finish();
                    updateTrasaction(FirebaseDatabase.getInstance().
                            getReference("Transactions/"+address+"/"+"Payments/" + OrderNo),
                          String.valueOf(System.currentTimeMillis()),
                            "Cash",DateFormat.getDateTimeInstance().format(new Date()),
                           String.valueOf(pay));
                       send_notifications("Your Order (" + OrderNo + ") is ready and delivered by the Canteen ");
                       Toast.makeText(getApplicationContext(), "Payment received Rs" + pay, Toast.LENGTH_SHORT).show();
                   }
               });
               alert1.setNegativeButton("Back", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialogInterface, int i) {
                   }
               });

               AlertDialog alertDialog = alert1.create();
               alertDialog.show();
           }else{
               store_extra_info("Your order is delivered");
                     data.child("Orders/"+address+"/"+"New Orders/" + OrderNo).setValue(null);
                    data.child("Orders/"+address+"/"+"All Orders/" + OrderNo + "/Status").setValue(orderStatus);
                    data.child("User Informations/"+  UserId + "/Orders/All orders/"
                            + OrderNo + "/Status").setValue(orderStatus);
                    data.child("User Informations/"+ UserId + "/Orders/Pending/"
                            + OrderNo).setValue(null);

                    finish();
               send_notifications("Your Order (" + OrderNo + ") is ready and delivered by the Canteen ");

           }
        } else {
            data.child("Orders/"+address+"/"+"New Orders/" + OrderNo + "/Status").setValue(orderStatus);
            data.child("Orders/"+address+"/"+"All Orders/" + OrderNo + "/Status").setValue(orderStatus);
            data.child("User Informations/" + UserId + "/Orders/All orders/"
                    + OrderNo + "/Status").setValue(orderStatus);
            data.child("User Informations/" + UserId + "/Orders/Pending/"
                    + OrderNo + "/Status").setValue(orderStatus);
            if (orderStatus.toUpperCase().contains("COMPLETE")) {
                send_notifications("Your Order (" + OrderNo + ")  completed by the Canteen and ready to deliver. Please reach to the canteen to accept your order ");
                store_extra_info("Your food is ready to serve");
                finish();

            } else {
                sendToTransaction(String.valueOf(other+TaxPay),
                        FirebaseDatabase.getInstance().getReference("Transactions/"+address+"/"+"Pending_Payments").child(OrderNo),
                        FirebaseDatabase.getInstance().getReference("Transactions/" +address+"/"+ OrderNo),true);
                send_notifications("Your Order (" + OrderNo + ") is accepted by the Canteen. We will soon be notify you to get your order when cooked");
                store_extra_info("Your order is accepted by the canteen");
                finish();

            }
        }

    }
    void updateTrasaction(DatabaseReference d,String TransactionNO,String bank,String date,String pay){
        d.child("OrderNo").setValue(OrderNo);
        d.child("Time").setValue(date);

        d.child("Transactions/"+TransactionNO).child("TransactionNumber").setValue(TransactionNO);
        d.child("Transactions/"+TransactionNO).child("OrderNo").setValue(OrderNo);
        d.child("Transactions/"+TransactionNO).child("Payment").setValue(pay);
        d.child("Transactions/"+TransactionNO).child("Mode").setValue("Cash");
        d.child("Transactions/"+TransactionNO).child("Bank").setValue(bank);
        d.child("Transactions/"+TransactionNO).child("Time").setValue(date);
        //d.child("Transactions/"+TransactionNO).child("ADMINSTATUS").setValue(false);
    }


    private void ReceiveData() {
        try {
            Intent intent = getIntent();
            Total_Food = intent.getStringExtra("Total_Food");
            //Delivery = intent.getStringExtra("Delivery");
            OrderNo = intent.getStringExtra("OrderNo");

            if (Canteen.equalsIgnoreCase("admin"))
                address= intent.getStringExtra("Address");

            else
                address=Canteen;

            order = findViewById(R.id.orderno1);
            order.setText(OrderNo);
            setTitle(OrderNo);
          try {
              UserId=intent.getStringExtra("Uid");
              Load_User_Credentials(FirebaseDatabase.getInstance().getReference("User Informations").
                      child(UserId));
          }catch (Exception e){Log.e("User_cred",e.toString()+" uid ");}
        } catch (Exception e) {

            e.printStackTrace();
        }
        loadfood(FirebaseDatabase.getInstance().getReference().child("Orders/"+address+"/"+"All Orders/" + OrderNo));
    }

    public void deliveryFormat(String Times) {

        findViewById(R.id.deliveryFormat).setVisibility(View.VISIBLE);
        TextView textView = findViewById(R.id.month);
        try {
            textView.setText(String.format("%s\n%s", displayMonth(Times),

                    getThreeInitials(dayName(Times.substring(Times.indexOf("(") + 1, Times.length() - 1), "dd/MM/yy")).toUpperCase()));
        } catch (Exception NullPointerException) {
        }
        textView = findViewById(R.id.date);
        try {
            textView.setText(Times.substring(Times.indexOf("(") + 1, Times.indexOf("/")));
            textView = findViewById(R.id.expecteddelivery);
            textView.setText(String.format("%s%s", getString(R.string.earliest_delivery),
                    Times.substring(0, Times.indexOf("(") - 1)));
            textView = findViewById(R.id.deliveryToday);
           /* if (Today == 1) {
                textView.setText("Today");
            } else textView.setVisibility(View.GONE);*/
        } catch (Exception ignored) {
        }

    }

    public String getMonthName(int month) {
        return new DateFormatSymbols().getMonths()[month];
    }

    public String getThreeInitials(String month) {
        return month.substring(0, 3);
    }

    public String dayName(String inputDate, String format) {
        Date date = null;
        try {
            date = new SimpleDateFormat(format).parse(inputDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new SimpleDateFormat("EEEE").format(date);
    }

    public String displayMonth(String time) {

        return getThreeInitials(getMonthName(Integer.
                parseInt(time.substring(time.indexOf("/") + 1, time.length() - 4)) - 1)).toUpperCase();
    }

    public void open(View view, final String Message) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("Are you sure you want to " + Message + " the order");
        alert.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (Objects.equals(Message, "Accepted") || Message.contains("Completed")) {
                    yes(Message);
                    Toast.makeText(getApplicationContext(), Message, Toast.LENGTH_SHORT).show();
                } else if (Message.contains("Delivered")) {

                    yes(Message);
                } else{
                    Toast.makeText(getApplicationContext(), Message, Toast.LENGTH_SHORT).show();
                    cancel();
                }
            }
        });
        alert.setNegativeButton("no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        AlertDialog alertDialog = alert.create();
        alertDialog.show();

    }

    public void setStatus(String status) {
        Status = status;
        int drawable;
        if (status.contains("Accepted")) {
            drawable = R.drawable.acceptedstatus_background;
            cancel.setVisibility(View.VISIBLE);
            save.setVisibility(View.GONE);
            completed.setVisibility(View.VISIBLE);

        } else if (status.contains("Completed")) {
            food_delivered.setVisibility(View.VISIBLE);
            cancel.setVisibility(View.GONE);
            save.setVisibility(View.GONE);
            completed.setVisibility(View.GONE);
            drawable = R.drawable.completedstatus_background;
        } else if (status.contains("Cancel")) {

            cancel.setVisibility(View.GONE);
            save.setVisibility(View.GONE);
            completed.setVisibility(View.GONE);
            drawable = R.drawable.cancelledstatus_background;
        } else if (status.contains("Delivered")) {
            cancel.setVisibility(View.GONE);
            save.setVisibility(View.GONE);
            completed.setVisibility(View.GONE);
            drawable = R.drawable.deliveredstatus_background;
        } else {
            drawable = R.drawable.statusbackground;
            completed.setVisibility(View.GONE);

        }
        TextView food_name = (TextView) findViewById(R.id.Orderstatus1);
        food_name.setBackgroundResource(drawable);

        food_name.setText(Status);
    }

    @Override
    protected void onStart() {
        super.onStart();


        DatabaseReference foodDatabase = FirebaseDatabase.getInstance().getReference().child("Orders/All Orders/" + OrderNo);



    }

    void loadfood(DatabaseReference foodDatabase) {
        extract_Data_from_Database_(foodDatabase,true);
        foodDatabase = foodDatabase.child("Food");
        RecyclerView recyclerView = findViewById(R.id.recycler_editorder);
        recyclerView.setNestedScrollingEnabled(false);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

         FirebaseRecyclerAdapter<album_allorders, mainactiv_allorders.FoodViewHolder> FBRA = new FirebaseRecyclerAdapter<album_allorders, mainactiv_allorders.FoodViewHolder>(
                album_allorders.class,
                R.layout.layouteditorder,
                mainactiv_allorders.FoodViewHolder.class,
                foodDatabase
        ) {
            @Override
            protected void populateViewHolder(final mainactiv_allorders.FoodViewHolder viewHolder, final album_allorders model, int position) {

                viewHolder.setFood_name(model.getFood_name());
                viewHolder.setQuantity(model.getQuantity());
                viewHolder.setprice(String.valueOf(model.getPrice()));


            }
        };
        recyclerView.setAdapter(FBRA);
    }

    void extract_Data_from_Database_(final DatabaseReference FoodDatabase, final boolean load) {
        final RecyclerView recyclerView = findViewById(R.id.recycler_editorder);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        FoodDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                showProgress_dialog(false);
                //  showProgress_dialog(false);
                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                //Total_Food = (String) map.get("TotalFood");
                Log.e("Check_Save()_edit", "Total_Food is " + Total_Food);
                //order = findViewById(R.id.orderno1);
                //order.setText(OrderNo);
                String time=(String) map.get("Time");

                TextView textView2=findViewById(R.id.deliverytime);
                Log.e("Time order ",time);
                textView2.setText(time);
                int totalpay = Integer.parseInt((String) map.get("TotalAmount"));
              try {
                  pay = (totalpay) -
                          Integer.parseInt((String) map.get("Payment"));
              }catch (Exception ignored){}

                if (pay == 0) {
                    remainPay.setVisibility(View.INVISIBLE);
                } else if (pay < 0) {
                    remainPay.setText(String.format("%s %d to the clint", String.format("%s%s ", getString(R.string.collect_extra_payment), getString(R.string.rupeesSymbol)), pay * (-1)));
                    remainPay.setCompoundDrawables(null, null, null, null);


                    remainPay.setClickable(false);
                } else
                    remainPay.setText(String.format("%s ₹ %d", getString(R.string.pay_remaining), pay));
             //   order.setText((String) map.get(getString(R.string.OrderNo)));
                order = findViewById(R.id.PaymentStatus1);
                order.setText(String.format("%s%s", "Payment Received "+getString(R.string.rupeesSymbol)+" ", map.get("Payment")));
                order.append(".00");
                //   OriginalDelivery=(String) map.get("Delivery");
                //  Times=(String) map.get("Delivery");
                deliveryFormat((String) map.get("Delivery"));
              /*  try {
                    Time.setText((String) map.get("Time"));
                }catch (Exception ignored){}*/
                Status = (String) map.get("Status");


                setStatus(Status);
                order = findViewById(R.id.total_final2);
                int TotalFinal = Integer.parseInt((String) map.get("TotalAmount"));
                order.setText(String.format("%s%s", getString(R.string.rupeesSymbol), String.valueOf(TotalFinal)));
                order.append(".00");
                long tax = Integer.parseInt((String) map.get("Tax"));
                TaxPay=tax;

                  other = Integer.parseInt((String) map.get("OtherPayment"));
                 order = findViewById(R.id.PayAmount);
                try {
                    TextView textView = findViewById(R.id.Infos_for_order);
                    String message = (String) map.get("Extra_Info");
                    if (!message.isEmpty()) {
                        textView.setText(message);
                        textView.setVisibility(View.VISIBLE);

                    }

                } catch (Exception ignored) {

                }
                order.setText(String.format("%s %s.00", getString(R.string.rupeesSymbol), String.valueOf(TotalFinal - tax - other)));
                order = findViewById(R.id.tax);
                order.setText(String.format("%s %s.00", getString(R.string.rupeesSymbol), String.valueOf(tax)));
                order = findViewById(R.id.OtherPayment);
                order.setText(String.format("%s %s.00", getString(R.string.rupeesSymbol), String.valueOf(other)));

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

    }
    void sendToTransaction(final String Tax, final DatabaseReference to, DatabaseReference from, final boolean delete){
        from.
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {


                        {
                            to.
                                    setValue(dataSnapshot.getValue(), new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(DatabaseError firebaseError, DatabaseReference firebase) {
                                            if (firebaseError != null) {
                                                Log.e("Moving node", "Move not moved ");
                                            } else {
                                                   to.  child("OtherPayment").setValue(Tax).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (delete)
                                                        FirebaseDatabase.getInstance().getReference("Transactions/" +address+"/"+ OrderNo).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                sendToTransaction(String.valueOf(other),FirebaseDatabase.getInstance().getReference("Transactions/"+address+"/"+"Payments").child(OrderNo),
                                                                        FirebaseDatabase.getInstance().getReference("Transactions/"+address+"/"+"Pending_Payments").child(OrderNo)
                                                                        ,false);

                                                            }
                                                        });
                                                        //findViewById(R.id.progressbar).setVisibility(View.GONE);

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

    void Load_User_Credentials(DatabaseReference databaseReference) {
        Log.e("User credentials ",databaseReference.toString());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TextView username = findViewById(R.id.UserName);
                TextView Mobile = findViewById(R.id.mobile);

                username.setText((String) dataSnapshot.child("Name").getValue());
                Mobile.setText((String) dataSnapshot.child("Mobile_number").getValue());
                try {
                    String IMG = (String) dataSnapshot.child("User_Img").getValue();
                    if (IMG != null)
                        Glide.with(getApplicationContext()).applyDefaultRequestOptions(RequestOptions.circleCropTransform()).load(IMG).into((ImageView) findViewById(R.id.image_message_profile));
                } catch (Exception ignored) {
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    void showProgress_dialog(boolean enable){

        if (enable) {
         //   progressDialog = new Dialog(editOrder.this);
            progressDialog = new Dialog(editOrder.this,R.style.Dialog);

            final View dialogView = View.inflate(editOrder.this, R.layout.loading_layout, null);

          //  progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            progressDialog.setContentView(dialogView);

            Window window=progressDialog.getWindow();
            if (window != null) {
                Log.e("Camera","Inside start_camera_dialog window is not null");
                window.setLayout( LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            }
            progressDialog.setContentView(dialogView);

            progressDialog.setCanceledOnTouchOutside(false);

            progressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                    if (i == KeyEvent.KEYCODE_BACK) {
                        finish();
                        return true;
                    }

                    return false;
                }
            });
            Log.e("Progress_dialog1","Inside if to show");

          //  progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            progressDialog.show();
        }else{  Log.e("Progress_dialog1","Inside else to dismiss");
try {
    progressDialog.dismiss();
}catch (Exception ignored){}
        }

    }
}
