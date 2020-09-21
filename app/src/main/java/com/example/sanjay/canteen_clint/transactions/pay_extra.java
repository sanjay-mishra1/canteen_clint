package com.example.sanjay.canteen_clint.transactions;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.sanjay.canteen_clint.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;
import java.util.Objects;

import static com.example.sanjay.canteen_clint.extra_classes.Constants.Canteen;

public class pay_extra extends AppCompatActivity {
    DatabaseReference mDatabase;
    String payableAmount="0";
    String OrderNo;
    String UserId;
    boolean delete=false;
    private long payment=0;
    private long totalamount=0;
    private String canteen;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_extra_layout);
        Intent intent=getIntent();
        OrderNo=intent.getStringExtra("ORDERNO");
        UserId=intent.getStringExtra("UID");
 //        if (Canteen.equalsIgnoreCase("admin"))
            canteen= intent.getStringExtra("Address");

        //else
          //  canteen=Canteen;
        Log.e("pay_extra","UID "+UserId);
        mDatabase = FirebaseDatabase.getInstance().getReference().child( "Orders/"+canteen+"/"+"All Orders/"+OrderNo);
        try {loadData();
            Load_User_Credentials((TextView ) findViewById(R.id.UserName),
                    (TextView ) findViewById(R.id.mobile),
                    (ImageView ) findViewById(R.id.image_message_profile), FirebaseDatabase
                            .getInstance().getReference("User Informations").child(UserId));
        }catch (Exception ignored){}

    }
    void loadData(){

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                findViewById(R.id.progressrelative).setVisibility(View.GONE);

                 setdata((TextView)findViewById(R.id.orderno),(String)dataSnapshot.child("OrderNo").getValue());
                setdata((TextView)findViewById(R.id.deliverytime),(String)dataSnapshot.child("Delivery").getValue());

              //  setStatus("123");

                setdata((TextView)findViewById(R.id.orderdate),
                        (String)dataSnapshot.child("Time").getValue());
                setdata((TextView)findViewById(R.id.TotalAmount),
                        getString(R.string.rupeesSymbol)+" "+(String)dataSnapshot.child("TotalAmount").getValue());
                setdata((TextView)findViewById(R.id.Extrapay),
                        getString(R.string.rupeesSymbol)+" "+   (String)dataSnapshot.child("Payment").getValue());
                Button b=findViewById(R.id.payextrabutton);
                b.setVisibility(View.VISIBLE);
                try {  payment=Integer.parseInt((String)dataSnapshot.child("Payment").getValue());
                      totalamount=Integer.parseInt((String)dataSnapshot.child("TotalAmount").getValue());
                   if ( !((String) dataSnapshot.child("Status").getValue()).toUpperCase().contains("CANCEL")) {


                       if (payment <= totalamount) {
                           b.setClickable(false);
                           b.setEnabled(false);
                           b.setText(R.string.nopayment);
                       } else if (payment > totalamount) {
                           payableAmount = String.valueOf((Integer.parseInt((String) dataSnapshot.child("Payment").getValue()) - Integer.parseInt((String) dataSnapshot.child("TotalAmount").getValue())));
                           b.setText(String.format("Pay extra %s %s", getString(R.string.rupeesSymbol), String.valueOf((Integer.parseInt((String) dataSnapshot.child("Payment").getValue()) - Integer.parseInt((String) dataSnapshot.child("TotalAmount").getValue())))));
                       } else {
                           delete = true;
                           payableAmount = (String) dataSnapshot.child("Payment").getValue();
                           b.setText(String.format("Pay extra %s %s", getString(R.string.rupeesSymbol), String.valueOf((Integer.parseInt((String) dataSnapshot.child("Payment").getValue())))));

                       }
                   }else{
                       delete = true;
                       payableAmount = (String) dataSnapshot.child("Payment").getValue();
                       b.setText(String.format("Pay extra %s %s", getString(R.string.rupeesSymbol), String.valueOf((Integer.parseInt((String) dataSnapshot.child("Payment").getValue())))));

                   }
                }catch (Exception e){
                    finish();
                    Toast.makeText(pay_extra.this,"Load failed...contact admin", Toast.LENGTH_SHORT).show();
                }
                try {if (((Integer.parseInt((String)dataSnapshot.child("TotalFood").getValue()) - 4))>0)
                    setdata((TextView)findViewById(R.id.morefood),""+"+ "+String.valueOf((Integer.parseInt((String)dataSnapshot.child("TotalFood").getValue()) - 4)));
                }catch (Exception ignored){}
                // viewHolder.setMore(String.valueOf(model.getTotalFood()));
                setdata((TextView)findViewById(R.id.Totalfood),""+(String)dataSnapshot.child("TotalFood").getValue());

                    setImg((ImageView)findViewById(R.id.img1),(String)dataSnapshot.child("Image0").getValue());

                setImg((ImageView)findViewById(R.id.img2),(String)dataSnapshot.child("Image1").getValue());
                setImg((ImageView)findViewById(R.id.img3),(String)dataSnapshot.child("Image2").getValue());
                setImg((ImageView)findViewById(R.id.img4),(String)dataSnapshot.child("Image3").getValue());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void setdata(TextView viewById, String s) {
        viewById.setText(s);
    }
private void setImg(ImageView viewById, String s) {
        if (s!=null) {
            Glide.with(getApplicationContext()).load(s).into(viewById);
            viewById.setVisibility(View.VISIBLE);
        }
}


    void Load_User_Credentials(final TextView username, final TextView Mobile, final ImageView img, DatabaseReference databaseReference)
    {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                findViewById(R.id.progressbar).setVisibility(View.GONE);
                username.setText((String) dataSnapshot.child("Name").getValue());
                Mobile.setText((String) dataSnapshot.child("Mobile_number").getValue());
                try {String IMG= (String) dataSnapshot.child("User_Img").getValue();
                    if (IMG!=null)
                        Glide.with(getApplicationContext()).applyDefaultRequestOptions(RequestOptions.circleCropTransform()).load(IMG).into(img);
                }catch (Exception ignored){}
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void payextraClicked(View view) {
         message();
    }
    void message(){
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("Are you sure you want to pay "+getString(R.string.rupeesSymbol)+" "+payableAmount+" to the user.\nPlease check the total amount and payment before clicking on YES button");
        alert.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            if (delete)
                ifOrderIsCancelled();
            else ifOrderIsNotCancelled();
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
    void ifOrderIsNotCancelled(){
        DatabaseReference data=FirebaseDatabase.getInstance().getReference();
        data.child("Orders/"+canteen).child("New Orders").child(OrderNo+"/Payment").
                setValue(String.valueOf(
                totalamount
        ));
        data.child("Orders/"+canteen).child("All Orders").child(OrderNo)
                .child("Payment").setValue(String.valueOf(
                totalamount
        ));
        data.child("User Informations").child(UserId).
                child("Orders/All orders").child(OrderNo).child("Payment").
                setValue(String.valueOf(totalamount));
        data.child("User Informations").child(UserId).
                child("Orders/Pending").child(OrderNo).child("Payment").setValue(String.valueOf(totalamount));
        send_notifications("You have received payment of "+getString(R.string.rupeesSymbol)+payableAmount+" from the Canteen for the Orderno "+OrderNo);
        store_extra_info("You have received payment of "+getString(R.string.rupeesSymbol)+payableAmount+" from the Canteen for the Orderno "+OrderNo);
        finish();
    }

    void ifOrderIsCancelled(){
        DatabaseReference data=FirebaseDatabase.getInstance().getReference();
        data.child("Orders").child(canteen+"/"+"New Orders").child(OrderNo).setValue(null);
        data.child("Orders").child(canteen+"/"+"All Orders").child(OrderNo)
                .child("Pay_Return_OnCancel").setValue("yes");
        data.child("User Informations").child(UserId).
                child("Orders/All orders").child(OrderNo).child("Pay_Return_OnCancel").setValue("yes");
        data.child("User Informations").child(UserId).
                child("Orders/Pending").child(OrderNo).setValue(null);
        send_notifications("You have received "+getString(R.string.rupeesSymbol)+payableAmount+" from the Canteen for the Orderno "+OrderNo);
        store_extra_info("You have received "+getString(R.string.rupeesSymbol)+payableAmount+" from the Canteen for the Orderno "+OrderNo);
        UpdateTransaction();
        finish();
    }

    private void UpdateTransaction() {

    }

    void send_notifications(final String Message) {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().
                getReference("User Informations/"+UserId + "/Notifications/" + OrderNo);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String old_message = (String) dataSnapshot.child("Message").getValue();
                try {
                    if (old_message != null) {
                        databaseReference.child("Message").setValue(old_message + "\n" + Message );
                        databaseReference.child("From").setValue("@orderno(" + OrderNo + ")");
                        databaseReference.child("Server_Time").setValue(ServerValue.TIMESTAMP);
                        databaseReference.child("Status").setValue("UNSEEN");
                    } else {
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().
                                getReference("User Informations"+"/"+UserId + "/Notifications");
                        databaseReference.child(OrderNo).child("Message").setValue(Message );
                        databaseReference.child(OrderNo).child("key").setValue(OrderNo);
                        databaseReference.child(OrderNo).child("From").setValue("@orderno(" + OrderNo + ")");

                        databaseReference.child(OrderNo).child("Server_Time").setValue(ServerValue.TIMESTAMP);
                        databaseReference.child(OrderNo).child("Status").setValue("UNSEEN");
                    }
                } catch (Exception e) {
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().
                            getReference("User Informations/" + UserId + "/Notifications");
                    databaseReference.child(OrderNo).child("Message").setValue(Message );
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

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().
                getReference("Orders/"+canteen+"/"+"All Orders/" + OrderNo);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                findViewById(R.id.progressbar).setVisibility(View.GONE);
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

    public void endActivity(View view) {
        finish();
    }
}
