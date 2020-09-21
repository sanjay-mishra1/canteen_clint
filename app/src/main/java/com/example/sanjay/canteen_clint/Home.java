package com.example.sanjay.canteen_clint;

 import android.app.NotificationChannel;
 import android.app.NotificationManager;
 import android.app.PendingIntent;
 import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
 import android.graphics.BitmapFactory;
 import android.graphics.Color;
 import android.os.Build;
 import android.support.annotation.NonNull;
 import android.support.design.widget.AppBarLayout;
 import android.support.design.widget.NavigationView;
 import android.support.v4.app.NotificationCompat;
 import android.support.v4.app.NotificationManagerCompat;
 import android.support.v4.widget.DrawerLayout;
 import android.support.v7.app.ActionBarDrawerToggle;
 import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
 import android.support.v7.widget.Toolbar;
 import android.util.Log;
 import android.view.MenuItem;
 import android.view.View;
 import android.widget.ImageView;
 import android.widget.LinearLayout;
 import android.widget.RelativeLayout;
 import android.widget.TextView;

 import com.bumptech.glide.Glide;
 import com.bumptech.glide.request.RequestOptions;
 import com.example.sanjay.canteen_clint.app_info.app_informations;
 import com.example.sanjay.canteen_clint.canteen_tools.issue;
 import com.example.sanjay.canteen_clint.canteen_tools.settings;
 import com.example.sanjay.canteen_clint.feedback_system.all_feedbacks;
 import com.example.sanjay.canteen_clint.info.UserImg;
 import com.example.sanjay.canteen_clint.info.all_users;
 import com.example.sanjay.canteen_clint.newChatScreen.chat_app_Activity;
 import com.example.sanjay.canteen_clint.newChatScreen.help_activity;
 import com.example.sanjay.canteen_clint.payments.payment_admin;
 import com.example.sanjay.canteen_clint.payments.pending_from_app;
 import com.example.sanjay.canteen_clint.search.allOrders;
  import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
 import com.google.firebase.database.ChildEventListener;
 import com.google.firebase.database.DataSnapshot;
 import com.google.firebase.database.DatabaseError;
 import com.google.firebase.database.DatabaseReference;
 import com.google.firebase.database.FirebaseDatabase;
 import com.google.firebase.database.Query;
 import com.google.firebase.database.ValueEventListener;
 import com.squareup.picasso.Picasso;

 import static android.app.Notification.DEFAULT_SOUND;
 import static android.app.Notification.DEFAULT_VIBRATE;
 import static android.support.v4.app.NotificationCompat.PRIORITY_HIGH;
 import static com.example.sanjay.canteen_clint.extra_classes.Constants.Canteen;
 import static com.example.sanjay.canteen_clint.extra_classes.Constants.Canteen_Image;
 import static com.example.sanjay.canteen_clint.extra_classes.Constants.User_Image;

public class Home extends AppCompatActivity implements View.OnClickListener {
 FirebaseAuth auth;
    SharedPreferences shared;
    public static final String MYPREFERENCES="MyPrefs";
    private TextView headername;
    private   final int message = 444;
    private   final int order = 203;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

           auth = FirebaseAuth.getInstance();
        shared=this.getSharedPreferences(MYPREFERENCES, Context.MODE_PRIVATE);
        String Name=shared.getString("Name","not available");
        if (Name.equalsIgnoreCase("admin")&& auth.getCurrentUser().getUid().equalsIgnoreCase(getString(R.string.uid))){
            setContentView(R.layout.newhome);
        }else{
            setContentView(R.layout.activity_home);

        }
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();
        loadNewStuffs(databaseReference.child("Orders/"+Canteen+"/New Orders"),(TextView) findViewById(R.id.totalNewOrders));
        loadNewStuffs(databaseReference.child("Messages").child("Help").child(Canteen),(TextView) findViewById(R.id.totalnewMessages));
        loadNewStuffs(databaseReference.child("Orders/"+Canteen+"New Orders").orderByChild("Status").startAt("Cancel").endAt("Cancel"+"\uf8ff"),
                (TextView) findViewById(R.id.totalTransaction));
        drawer();
        findViewById(R.id.newFoodcard).setOnClickListener(this);
        findViewById(R.id.NewOrdercardId).setOnClickListener(this);
         findViewById(R.id.allorders ).setOnClickListener(this);

        findViewById(R.id.payments ).setOnClickListener(this);
        findViewById(R.id.edit ).setOnClickListener(this);
         findViewById(R.id.Cake).setOnClickListener(this);
        findViewById(R.id.editcake).setOnClickListener(this);
        findViewById(R.id.Messages).setOnClickListener(this);
        findViewById(R.id.tax).setOnClickListener(this);

        try {
          findViewById(R.id.Feedbacks).setOnClickListener(this);

          findViewById(R.id.UserInfo).setOnClickListener(this);
          findViewById(R.id.payment_admin).setOnClickListener(this);
          findViewById(R.id.foodinfo).setOnClickListener(this);
          findViewById(R.id.appinfo).setOnClickListener(this);
          findViewById(R.id.newCanteen).setOnClickListener(this);
      }catch (Exception ignored){}
        firebaseWatcher(FirebaseDatabase.getInstance().getReference().child("Messages/"+"Help"+"/"+Canteen)
                .orderByChild("Last_message_counter").startAt("0").limitToFirst(1), message," New messages received" );
        firebaseWatcher(FirebaseDatabase.getInstance().getReference().child("Orders/"+Canteen+"/Pending")
                .orderByChild("Status").equalTo("Pending").limitToFirst(1), order," New Orders have been received" );
    }

    private void drawer() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final DrawerLayout drawerLayout=findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        drawerLayout.addDrawerListener(toggle);

        toggle.getDrawerArrowDrawable().setColor(Color.WHITE);

        toggle.syncState();
        NavigationView   navigationView = findViewById(R.id.shitstuff);
        loadUserData(navigationView);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawerLayout.closeDrawers();
                if (item.getItemId() == R.id.issue) {
                    startActivity(new Intent(Home.this,issue.class));

                }

                if (item.getItemId() == R.id.logout) {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent=new Intent(Home.this,login.class);
                    SharedPreferences.Editor editor=shared.edit();
                    editor.clear();
                    editor.apply();
                    finish();
                     startActivity(intent);
                }

                if (item.getItemId() == R.id.settings) {
                    startActivity(new Intent(Home.this,settings.class));
                }
                return false;
            }
        });

    }
    void loadUserData(NavigationView navigationView){
        auth = FirebaseAuth.getInstance();

        final String email = auth.getCurrentUser().getEmail();
        String canteen=shared.getString("Canteen","not available");
        Canteen=canteen;
        String canteenImage=shared.getString("CanteenImage","" );
        String Name=shared.getString("Name","not available");
        setTitle(canteen);
        View header = navigationView.getHeaderView(0);

        String UserImage=shared.getString("UserImage","");
        if (Name.equalsIgnoreCase("admin")){
            Glide.with(getApplicationContext()).applyDefaultRequestOptions
            (RequestOptions.circleCropTransform()).load(getResources().getDrawable(R.drawable.admin)).
                    into((ImageView) header. findViewById(R.id.profile_img));
            Picasso.get().load(R.drawable.completedstatus_background).into(
                    ((ImageView)header. findViewById(R.id.canteenimage)));
        }else {
            if (UserImage.isEmpty()){
                Glide.with(getApplicationContext()).load(String.valueOf(getResources().getDrawable(R.drawable.canteen))).
                        into((ImageView) header. findViewById(R.id.profile_img));

            }
            Glide.with(getApplicationContext()).applyDefaultRequestOptions
                    (RequestOptions.circleCropTransform()).load(UserImage).
                    into((ImageView) header. findViewById(R.id.profile_img));
            User_Image= UserImage;
            Canteen_Image= canteenImage;

            Glide.with(getApplicationContext()).applyDefaultRequestOptions(RequestOptions.noTransformation())
                    .load(canteenImage).into((ImageView) header. findViewById(R.id.canteenimage));
            //Picasso.get().load(canteenImage).into
              //      ((ImageView) header.findViewById(R.id.canteenimage));
        }
        TextView textView=header.findViewById(R.id.phone_nav);
        textView.setText(email);
        headername=header.findViewById(R.id.username_nav);
        headername.setText(Name);
     }
    void automatic_auth(final String email, final String password){
    auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            // progressBar.setVisibility(View.GONE);
            if (task.isSuccessful()) {
             //   SharedPreferences.Editor editor = shared.edit();
              //  editor.putString("Email", email);
               // editor.putString("Password", password);
              //  editor.apply();



            }
        }
    });
}
    @Override
    protected void onStart() {
        super.onStart();
        shared=this.getSharedPreferences(MYPREFERENCES, Context.MODE_PRIVATE);
        final String email = shared.getString("Email", "");
        final String password = shared.getString("Password", "");
      try {
          headername.setText(shared.getString("Name", ""));
      }catch (Exception ignored){}
        if (!email.isEmpty()) {
            automatic_auth(email,password);
         }else{
            FirebaseAuth.getInstance().signOut();
            Intent intent=new Intent(this,login.class);
            SharedPreferences.Editor editor=shared.edit();
            editor.clear();

            editor.apply();
            finish();
             intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

    }
    public void loadNewStuffs(Query databaseReference, final TextView textView){
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

try {
    textView.setText(String.valueOf(dataSnapshot.getChildrenCount()));
    textView.setVisibility(View.VISIBLE);
}catch (Exception ignored){
 }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
     @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.newFoodcard:
                 startActivity(new Intent(this, NewFood.class));

                break;
            case R.id.NewOrdercardId:
                //startActivity(new Intent(this, pendingorders.class));
                Intent intent= new Intent(this, allOrders.class);
                intent.putExtra("OrderAddress","New Orders");
                startActivity(intent);

                break;
            case R.id.allorders:
                  intent= new Intent(this, allOrders.class);
                intent.putExtra("OrderAddress","All Orders");
                startActivity(intent);
                break;
            case R.id.edit:
                 intent=new Intent(this,MainActivity.class);
                 startActivity(intent);
               break;
            case R.id.payments:
                startActivity(new Intent(this,pendingPayments.class));
                break;

            case R.id.Cake:
                startActivity(new Intent(this,cakeActivity.class));
                break;
            case R.id.editcake:
                startActivity(new Intent(this,editcake.class));
                break;
            case R.id.Messages:
                startActivity(new Intent(this,chat_app_Activity.class));
                break;

            case R.id.UserInfo:
                startActivity(new Intent(this,all_users.class));
                break;

            case R.id.Feedbacks:
                startActivity(new Intent(this,all_feedbacks.class));
                break;
            case R.id.tax:
                startActivity(new Intent(this,pending_from_app.class));
                break;
            case R.id.payment_admin:
                startActivity(new Intent(this,payment_admin.class));
                break;
             case R.id.appinfo:
                startActivity(new Intent(this,app_informations.class));
                break;
            case R.id.newCanteen:
                startActivity(new Intent(this,NewCanteen.class));
                break;
            case R.id.foodinfo:
                Intent intent1=new Intent(this,MainActivity.class);
                intent1.putExtra("INFO","INFO");
                startActivity(new Intent(intent1));
                break;

        }
    }

    void firebaseWatcher(Query query, final int id, final String message){
        try {


            query.addChildEventListener(new ChildEventListener() {


                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    {  Log.e("Notification check","inside child added sending to notification");


                        system_notification(dataSnapshot, id,message);

                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    Log.e("Notification check","inside child changed sending to notification");
                    //   system_notification(dataSnapshot,i++);

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
        }catch (Exception ignored){}

    }

    void system_notification(DataSnapshot dataSnapshot, int id, String message){

        int priority;
       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            priority= NotificationManager.IMPORTANCE_HIGH;
        }else
            priority=PRIORITY_HIGH;*/


        String CHANNEL_ID="101";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name =  "Mcafe Canteen";
            String description =  "Mcafe Canteen" ;
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }


        Intent intent;// = new Intent(this, help_activity.class);
        //String from=(String)dataSnapshot.child("From").getValue();
        String from=dataSnapshot.toString();
        if (from != null ) {

            // String message = from;
            //message = message.substring(message.indexOf("(") + 1, message.indexOf(")"));
            Log.e("Notification check", message);
            String title;

            if (id== this.message){
                intent = new Intent(this, chat_app_Activity.class);

                title="New Messages";
                intent.putExtra("Address", "9");

            }else{
                  intent= new Intent(this, allOrders.class);
                intent.putExtra("OrderAddress","New Orders");
                title="New Orders";
                intent.putExtra("Address", "3");

            }
            //  intent.putExtra("From", "Orders");


            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);


            //    message =  ((String) dataSnapshot.child("OrderNo").getValue());
            long totalfooditems=dataSnapshot.getChildrenCount();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                priority=NotificationManager.IMPORTANCE_HIGH;
            }else  priority=PRIORITY_HIGH;


            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_add_shopping_cart_black_24dp)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round))
                    .setContentTitle(title)
                    .setContentText(totalfooditems+message)
                    .setStyle(new NotificationCompat.InboxStyle().addLine(
                            totalfooditems+message)
                    )
                    .setPriority(priority)
                    .setContentIntent(pendingIntent)
                    .setColor(getResources().getColor(R.color.colorAccent))
                    .setDefaults(DEFAULT_SOUND | DEFAULT_VIBRATE)
                     .setGroupSummary(true)
                    .setAutoCancel(true);
            NotificationManagerCompat notificationManager;

            notificationManager = NotificationManagerCompat.from(this);

            notificationManager.notify(id, mBuilder.build());



        }
    }
    }