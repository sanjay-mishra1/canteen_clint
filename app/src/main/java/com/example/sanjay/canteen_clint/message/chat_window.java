package com.example.sanjay.canteen_clint.message;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
 import android.content.Context;
 import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.LoginFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.sanjay.canteen_clint.R;
import com.example.sanjay.canteen_clint.adapter_allorders;
import com.example.sanjay.canteen_clint.album_allorders;
import com.example.sanjay.canteen_clint.mainactiv_allorders;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
  import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.prefs.Preferences;

public class chat_window extends AppCompatActivity {
    private static final int TAKE_PICTURE = 102;
    private static final int READ_EXTERNAL_STORAGE = 103;
    static TextView Edit_message;
     FirebaseAuth auth;
    public int count = 0;
    private long Total = 0;
    static boolean back = true;
    private static final int CHOOSE_IMAGE = 101;
    Uri uriProfileImage;
    boolean actionButtonStatus = false;
     private ImageButton actionButton;
     public static String OrderNo;
    RecyclerView recyclerView;
     static FragmentManager FM;
    static FrameLayout frameLayout;
    static String Uid;
    private String UserName;
    private String img="";
    public static String canteen;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_help_layout);
        findViewById(R.id.progressbar).setVisibility(View.GONE);
        receive();
        TextView textView=findViewById(R.id.toolbar);
        textView.setText(UserName);
           findViewById(R.id.end).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chat_window.super.onBackPressed();
            }
        });
        recyclerView = findViewById(R.id.reyclerview_message_list);
        LinearLayoutManager horizontal = new LinearLayoutManager(chat_window.this, LinearLayoutManager.VERTICAL, false);


        recyclerView.setLayoutManager(horizontal);
         DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().
                 child("Messages").
                child("Help").child(canteen).child(Uid + "/Messages");
        MyAdapter adapter;
        adapter = new MyAdapter(album_allorders.class, R.layout.item_message_sent,
                mainactiv_allorders.FoodViewHolder.class, databaseReference,UserName, img);
        img=null;
        horizontal.setStackFromEnd(true);
        ScrollPosition(databaseReference,recyclerView,adapter);

        recyclerView.setAdapter(adapter);

        frameLayout = findViewById(R.id.frameLayoutHelp);
        FM = getSupportFragmentManager();
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        Edit_message = findViewById(R.id.edittext_chatbox);
        actionButton = findViewById(R.id.button_send_message);

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setActionButton("TEXT");

            }
        });
        Edit_message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkMessageBox(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        super.onStart();

    }
void receive(){
        try{
            Intent intent=getIntent();

           Uid= intent.getStringExtra("UID");
           canteen= intent.getStringExtra("Address");
           UserName= intent.getStringExtra("UserName");
           UserName= intent.getStringExtra("UserName");
            img=intent.getStringExtra("img");
    if (img!=null){
        Glide.with(getApplicationContext()).load(img).into((ImageView) findViewById(R.id.mainimage));
    }
        }catch (Exception e){
            e.printStackTrace();
        }
    Log.e("Receive","Uid "+Uid+" Username"+UserName);
}
    void checkMessageBox(String message) {
        String emo_regex = "([\\u20a0-\\u32ff\\ud83c\\udc00-\\ud83d\\udeff\\udbb9\\udce5-\\udbb9\\udcee])";
//.matches(".*\\w.*"
        if (!message.isEmpty()) {
                 Glide.with(getApplicationContext()).load(R.drawable.ic_send_black_24dp).into(actionButton);
            //Picasso.get().load(R.drawable.ic_send_black_24dp).into(actionButton);

            actionButtonStatus = true;
            //actionButton.T
        }

        else {

                Glide.with(getApplicationContext()).load(R.drawable.add_color).into(actionButton);
                actionButtonStatus = false;
               // Picasso.get().load(R.drawable.add_color).into(actionButton);
           // Picasso.with().load(R.drawable.add_color).into(actionButton);

            //yourString = "MY name is <(>Mohit Kumar<)>. <(>Sachin<)> is my role model. I am <(>12<)> year old. Currently i am in <(>delhi<)> but my hometown is <(>bangalore<)>.";
            //yourNewString =  yourString.replace("<(>",colorCodeStart); // <(> and <)> are different replace them with your color code String, First one with start tag
            //yourNewString=  yourNewString.replace("<)>",colorCodeEnd); // then end tag

            // Log.d("CheckNew",yourNewString);
            // me.setText((Html.fromHtml(yourNewString)));

        }
    }


    private void decodeMessage(String message) {
        if (message.contains("@")) {
            try {
                if (message.charAt(message.indexOf("@") - 1) == ' ') {
                    message = message.substring(message.indexOf("@"), message.length());
                    message = message.substring(0, message.indexOf(" "));
                //    message = message.replace("@", colorCodeStart);
                //    message = message.replace(message, message + colorCodeEnd);
                    Log.e("help", "Font color changed " + message);
                    Edit_message.setText((Html.fromHtml(message)));
                }
            } catch (Exception StringIndexOutOfBoundsException) {
            }
        }

    }

    void setActionButton(String type) {
        if (actionButtonStatus) {
            TotalHelp(type);
        } else {
            CardView cardView = findViewById(R.id.card_view);
            //showDiag();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                if (cardView.getVisibility() == View.INVISIBLE) {

                    animation(true);
                } else {
                    animation(false);
                }
            } else {
                if (cardView.getVisibility() == View.INVISIBLE) {
                    findViewById(R.id.card_view).setVisibility(View.VISIBLE);
                    showZoomIn();
                    ExtraOptions();
                } else {
                    findViewById(R.id.card_view).setVisibility(View.INVISIBLE);
                }
            }

        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        back = true;
        OrderNo = "";
    }

    void showZoomIn() {
        Animation zoomIn =
                AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.zoom_in);
        findViewById(R.id.relateGallery).startAnimation(zoomIn);
        findViewById(R.id.relateCamera).startAnimation(zoomIn);
        findViewById(R.id.relateOrder).startAnimation(zoomIn);
        ExtraOptions();

    }

    void ExtraOptions() {
        findViewById(R.id.gallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageChooser();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    animation(false);
                } else findViewById(R.id.card_view).setVisibility(View.INVISIBLE);
            }
        });
        findViewById(R.id.camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCamera();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    animation(false);
                } else findViewById(R.id.card_view).setVisibility(View.INVISIBLE);
            }
        });
        findViewById(R.id.order).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                back = false;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    // animation(false);
                    findViewById(R.id.card_view).setVisibility(View.INVISIBLE);
                    openOrders(true);
                } else {
                    findViewById(R.id.card_view).setVisibility(View.INVISIBLE);
                    openOrders(true);
                }
            }
        });

    }



    void TotalHelp(final String type) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat date = new SimpleDateFormat("yyMMdd");
        final String key = date.format(c.getTime());
        date = new SimpleDateFormat("MMMM d, yyyy || h:mm a");
        final String messageTime = date.format(c.getTime());
        DatabaseReference database = FirebaseDatabase.getInstance().getReference().
                child("Messages").child("Help").child(canteen).child(Uid + "/Messages");//.setValue((message)+"  Email- "+email);
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    Total = dataSnapshot.getChildrenCount();
                    Log.e("help", "  Total Found " + Total);

                    Total += 1;
                    if (Total==1){
                        Log.e("Message time",messageTime);
                        saveMessage(type,setDate1(messageTime),key,setTime(messageTime),false);

                    }else
                        getDateStatus(setDate1(messageTime),FirebaseDatabase.getInstance().getReference().
                                child("Messages").
                                child("Help").child(canteen).child(Uid + "/Keys" ),type,key,setTime(messageTime));
                    //saveMessage(type);
                } catch (Exception NullPointerException) {
                    Total = 1;
                    getDateStatus(setDate1(messageTime),FirebaseDatabase.getInstance().getReference().
                            child("Messages").
                            child("Help").child(canteen).child(Uid + "/Keys" ),type,key,setTime(messageTime));
                    //saveMessage(type);
                    Log.e("help", "Exception  Total Found " + Total);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void animation(boolean show) {
        final CardView cardView = findViewById(R.id.card_view);

        int height = cardView.getHeight();
        int width = cardView.getWidth();
        int endRadius = (int) Math.hypot(width, height);
        int cx = (int) (actionButton.getX() + (cardView.getWidth() / 2));
        int cy = (int) (actionButton.getY()) + cardView.getHeight() + 56;

        if (show) {
            Animator revealAnimator = ViewAnimationUtils.createCircularReveal(cardView, cx, cy, 0, endRadius);
            revealAnimator.setDuration(700);
            revealAnimator.start();
            cardView.setVisibility(View.VISIBLE);
            showZoomIn();
        } else {
            Animator anim =
                    ViewAnimationUtils.createCircularReveal(cardView, cx, cy, endRadius, 0);

            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    cardView.setVisibility(View.INVISIBLE);

                }
            });
            anim.setDuration(700);
            anim.start();
        }

    }


     void ScrollPosition(RecyclerView recyclerView, long pos, RecyclerView.Adapter adapter){

         recyclerView.scrollToPosition(adapter.getItemCount() );
    }
    void ScrollPosition(DatabaseReference Address, final RecyclerView recyclerView, final RecyclerView.Adapter adapter) {

        final long[] childrens = new long[1];
        Address.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                childrens[0] = dataSnapshot.getChildrenCount();
                //   Log.e("scrolling", "Address " + Address);
                Log.e("scrolling", "Childs " + childrens[0]);

                ScrollPosition(recyclerView,dataSnapshot.getChildrenCount(),adapter);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //  return (int) childrens[0];
    }

    void saveMessage(String type, String date, String key, String Time,boolean Status) {
        Log.e("Firebase Messaging","Inside save message  ");

        DatabaseReference   database = FirebaseDatabase.getInstance().getReference().child("Messages").
                child("Help").child(canteen).child(Uid + "/Messages").
                child(String.valueOf(Total));

        String message = Edit_message.getText().toString().trim();
        if (message.isEmpty()) {
            // Edit_message.setError("Please enter Something");
            Edit_message.requestFocus();
            Log.e("help", "  Error message is empty");
            Log.e("Firebase Messaging","Inside save message  message is empty");

            return;
        }
        Edit_message.setText("");
        if (!Status)
        {
            storenewKey(key,date, String.valueOf(Total));
            database.child("From").setValue("Date");
            database.child("Date").setValue(date.toUpperCase());
            database = FirebaseDatabase.getInstance().getReference().child("Messages").
                    child("Help").child(canteen).child(Uid + "/Messages").
                    child(String.valueOf(Total+1));
            database.child("Date").setValue(date);

        }else{
            database.child("Date").setValue("");
        }
        save_Extra_info(message,Time);
        Log.e("help", " Message Saved ");
        database.child("Message").setValue(message);
        database.child("Time").setValue(Time);

        database.child("Status").setValue("UNREAD");
        database.child("From").setValue("ADMIN_TEXT");
        Log.e("Notifications","Inside save message");
        send_notifications(message);

    }
    private void save_Extra_info(String Message,String Time){
        final DatabaseReference   database = FirebaseDatabase.getInstance().getReference().child("Messages").
                child("Help").child(canteen).child(Uid);
        //SharedPreferences shared=this.getSharedPreferences("User_Credentials", Context.MODE_PRIVATE);
        database.child("UId").setValue(Uid);
       // database.child("User_Name").setValue(shared.getString("UserName"," "));
        database.child("User_last_Message").setValue(Message);
        database.child("Time").setValue(Time);




    }
    public String setTime(String time){

        time=time.substring(time.indexOf("||")+3,time.length());
        return (time);


    }
    void storenewKey(String key,String date,String Total){
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("Messages").
                child("Help").child(canteen).child(Uid + "/Keys/"+key);
        databaseReference.child("Date").setValue(date);
        databaseReference.child("key").setValue(key);
        databaseReference.child("Total").setValue(Total);
    }
     void getDateStatus(final String date, DatabaseReference address, final String type, final String key, final String Time)   {
        final boolean[] status = {false};
         final Query query=address.orderByValue().limitToLast(1);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {

                    for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                    {               // album_allorders data1=dataSnapshot1 .getValue(album_allorders.class);
                        Log.e("Firebase Messaging","Inside try  "+dataSnapshot1.
                                child("Date").getValue().toString()+" and date is "+date );

                        if (date.contains(dataSnapshot1.child("Date").getValue().toString()))
                        {Log.e("Firebase Messaging","Date is equal sending to save message");

                            saveMessage(type,date,key,Time,true);
                            Log.e("Firebase Messaging","Date is equal out from save message");

                        }else{                        Log.e("Firebase Messaging","Date is notequal sending save message");

                            saveMessage(type,date,key,Time,false);

                        }
                    }


                }catch (Exception e){
                    Log.e("Firebase Messaging","Inside catch  " );
                    saveMessage(type,date,key,Time,false);

                    status[0]=false;}
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public String setDate1(String time){

        try {
            time= time.substring(0,time.indexOf("||")-1 );
            return (String.format("  %s  ", time));
        }catch (Exception ignored){}
        return "";
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void revealShow(View dialogView, boolean b, final Dialog dialog) {

        final View view = dialogView.findViewById(R.id.mainRelative);

        int w = view.getWidth();
        int h = view.getHeight();

        int endRadius = (int) Math.hypot(w, h);

        int cx = (int) (actionButton.getX() + (actionButton.getWidth() / 2));
        int cy = (int) (actionButton.getY()) + actionButton.getHeight() + 56;

//show
        if (b) {
            Animator revealAnimator = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, endRadius);

            view.setVisibility(View.VISIBLE);
            revealAnimator.setDuration(700);
            revealAnimator.start();

        } else {//hide with animation

            Animator anim =
                    ViewAnimationUtils.createCircularReveal(view, cx, cy, endRadius, 0);

            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    dialog.dismiss();
                    view.setVisibility(View.INVISIBLE);

                }
            });
            anim.setDuration(700);
            anim.start();
        }


    }



    public static void openOrders(boolean show) {

        if (show) {

            frameLayout.setVisibility(View.VISIBLE);
            FragmentTransaction fragmentTransaction = FM.beginTransaction();
            fragmentTransaction.replace(R.id.frameLayoutHelp, new orders()).commit();
            back = false;
        } else {
            FM.beginTransaction().remove(new orders()).commit();
            frameLayout.setVisibility(View.GONE);
            Edit_message.setText(String.format("%s @Order(%s)", Edit_message.getText().toString().trim(), OrderNo));
            back = true;
        }
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);


        startActivityForResult(intent, CHOOSE_IMAGE);
    }

    private void showImageChooser() {
        // Intent intent = new Intent();
        //intent.setType("image/*");
        // intent.setAction(Intent.ACTION_GET_CONTENT);
        requestPermission(this);

    }
    private void requestPermission(Activity context)   {
        boolean hasPermission = (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(context,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_EXTERNAL_STORAGE);
        } else {
            startActivity(new Intent(this, GallerySample.class));


        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case Preferences.MAX_VALUE_LENGTH: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivity(new Intent(this, GallerySample.class));
                    Toast.makeText(this, "The app was allowed to write to your storage!", Toast.LENGTH_LONG).show();
                    // Reload the activity with permission granted or use the features what required the permission
                } else {
                    Toast.makeText(this, "The app was not allowed to write to your storage. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
                }
            }
        }
    }



    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriProfileImage = data.getData();
            Log.e("help", "activity result successful");
            //uploadImageToFirebaseStorage();
            Intent intent = new Intent(chat_window.this, image_zoomer.class);
             intent.putExtra("UID",Uid);
            intent.putExtra("UserName",UserName );
            intent.putExtra("Image", new File(uriProfileImage.getPath()));
            startActivity(intent);


        } else {
            Log.e("help", "activity result unsuccessful reqcode" + requestCode + " resultcode " + requestCode
                    + " data ");
        }
    }

    @Override
    public void onBackPressed() {
        if (!back) {
            openOrders(false);
        } else
            super.onBackPressed();
    }

    public static class orders extends Fragment {
        public String Selected_foodName[];
        public int Selected_foodPrice[];
        public String Name;
        public String Image[];
        ImageView next;
        public int count = 0;
         public DatabaseReference mDatabase;
        private RecyclerView recyclerView;
        private adapter_allorders adapter;
        public View view;
        ProgressBar progressBar;

        public orders() {

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
              view = inflater.inflate(R.layout.recycler_allorders, container, false);
            progressBar = view.findViewById(R.id.progressFood);
            progressBar.setVisibility(View.INVISIBLE);

            view.setFocusableInTouchMode(true);
            view.requestFocus();
            view.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View view, int i, KeyEvent keyEvent) {
                    if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                        if (i == KeyEvent.KEYCODE_BACK) {
                            getActivity().finish();
                             return true;
                        }
                    }
                    return false;
                }
            });
            Selected_foodName = new String[10];
            Selected_foodPrice = new int[10];
            Image = new String[10];
            next = view.findViewById(R.id.next_button);
            recyclerView = view.findViewById(R.id.recycler_view);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(adapter);
             mDatabase = FirebaseDatabase.getInstance().getReference().
                    child("User Informations/"+Uid+"/Orders/All orders");



            return view;

        }


        @Override
        public void onStart() {
            super.onStart();
            FirebaseRecyclerAdapter<album_allorders, mainactiv_allorders.FoodViewHolder> FBRA = new FirebaseRecyclerAdapter<album_allorders, mainactiv_allorders.FoodViewHolder>(
                    album_allorders.class,
                    R.layout.new_orders_layout,
                    mainactiv_allorders.FoodViewHolder.class,
                    mDatabase) {
                @Override
                protected void populateViewHolder(mainactiv_allorders.FoodViewHolder viewHolder, final album_allorders model, int position) {

                    viewHolder.setOrderNo(model.getOrderNo());
                    viewHolder.setDelivery(model.getDelivery());

                    viewHolder.setStatus(model.getStatus());
                  //  viewHolder.getImageView().setVisibility(View.INVISIBLE);
                    viewHolder.setPayment(model.getPayment());
                    try {
                        viewHolder.setPayment2(String.valueOf((Integer.parseInt(model.getTotalAmount()) - Integer.parseInt(model.getPayment()))),getContext());
                        viewHolder.setMore(String.valueOf((Integer.parseInt(model.getTotalFood()) - 4)));
                    } catch (Exception NumberFormatException) {
                    }           // viewHolder.setMore(String.valueOf(model.getTotalFood()));
                    viewHolder.setImage0( model.getImage0(), getContext());
                    viewHolder.setImage1(model.getImage1(),getContext());
                    viewHolder.setImage2(model.getImage2(),getContext());
                    viewHolder.setImage3(model.getImage3(),getContext());


                    viewHolder.Relative().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Intent intent=new Intent(getActivity(),editOrder.class);
                            OrderNo = model.getOrderNo();
                            //intent.putExtra("OrderNo",model.getOrderNo());
                            // intent.putExtra("From","Orders");
                            openOrders(false);
                            // startActivity(intent);
                        }
                    });


                }


            };
            recyclerView.setAdapter(FBRA);

        }


    }
    void send_notifications(final String Message)
    {
        Log.e("Notifications","Inside save notifications");
        final DatabaseReference databaseReference=FirebaseDatabase.getInstance().
                getReference("User Informations/"+Uid+"/Notifications");
        Query query=databaseReference.orderByChild("Server_Time");
        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    Log.e("Notifications", "not exist");
                    String key = "Message_" + System.currentTimeMillis();
                    databaseReference.child(key).child("From").setValue("Message");
                    databaseReference.child(key).child("key").setValue(key);
                    databaseReference.child(key).child("Message").setValue(Message + " Time " + System.currentTimeMillis());
                    databaseReference.child(key).child("Server_Time").setValue(ServerValue.TIMESTAMP);
                    databaseReference.child(key).child("Status").setValue("UNSEEN");
                }else{
                    Log.e("Notifications", "  exist");

                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    try {
                        Log.e("Notifications", "Child " + dataSnapshot1.getChildrenCount());
                        if (dataSnapshot1.child("From").getValue().toString().trim().toUpperCase().contains("MESSAGE"))
                             { Log.e("Notifications", "inside get from "+dataSnapshot1.child("From").getValue());

                                 if (dataSnapshot1.child("Status").getValue().toString().trim().toUpperCase().contains("UNSEEN")) {
                                Log.e("Notifications", "message found");
                                String key = (String) dataSnapshot1.child("key").getValue();
                                databaseReference.child(key).child("From").setValue("Message");
                                String old_message = (String) dataSnapshot1.child("Message").getValue();
                                Log.e("Notifications", "Old message not null ");
                                databaseReference.child(key).child("Message").setValue(old_message + "\n" + Message + " Time " + System.currentTimeMillis());
                                databaseReference.child(key).child("Server_Time").setValue(ServerValue.TIMESTAMP);
                                databaseReference.child(key).child("Status").setValue("UNSEEN");
                                break;
                            } else {
                                Log.e("Notifications", "not found");
                                String key = "Message_" + System.currentTimeMillis();
                                databaseReference.child(key).child("From").setValue("Message");
                                databaseReference.child(key).child("key").setValue(key);
                                databaseReference.child(key).child("Message").setValue(  Message + " Time " + DateFormat.getDateTimeInstance().format(new Date()));
                                databaseReference.child(key).child("Server_Time").setValue(ServerValue.TIMESTAMP);
                                databaseReference.child(key).child("Status").setValue("UNSEEN");
                                break;
                            }
                        }else {
                            Log.e("Notifications", "not found");
                            String key = "Message_" + System.currentTimeMillis();
                            databaseReference.child(key).child("From").setValue("Message");
                            databaseReference.child(key).child("key").setValue(key);
                            databaseReference.child(key).child("Message").setValue(Message + " Time " + System.currentTimeMillis());
                            databaseReference.child(key).child("Server_Time").setValue(ServerValue.TIMESTAMP);
                            databaseReference.child(key).child("Status").setValue("UNSEEN");
                        }
                    } catch (Exception e) {
                        Log.e("Notifications", "not found catch");
                        String key = "Message_" + System.currentTimeMillis();
                        databaseReference.child(key).child("From").setValue("Message");
                        databaseReference.child(key).child("key").setValue(key);
                        databaseReference.child(key).child("Message").setValue(Message + " Time " + System.currentTimeMillis());
                        databaseReference.child(key).child("Server_Time").setValue(ServerValue.TIMESTAMP);
                        databaseReference.child(key).child("Status").setValue("UNSEEN");
                    }

                }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Notifications"," error firebase "+databaseError.toString());

            }
        });

    }

}

