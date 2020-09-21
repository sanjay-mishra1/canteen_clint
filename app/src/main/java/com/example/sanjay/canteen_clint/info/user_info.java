package com.example.sanjay.canteen_clint.info;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.sanjay.canteen_clint.R;
import com.example.sanjay.canteen_clint.adapter_allorders;
import com.example.sanjay.canteen_clint.album_allorders;
import com.example.sanjay.canteen_clint.editFood;
import com.example.sanjay.canteen_clint.editOrder;
import com.example.sanjay.canteen_clint.mainactiv_allorders;
import com.example.sanjay.canteen_clint.newChatScreen.help_activity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class user_info extends AppCompatActivity {
    private DatabaseReference firebase;
    private RecyclerView recyclerView;
    private adapter_allorders adapter;
     private String Desc;
     private int dis=0;
    private int price=0;
    public FragmentManager FM;
    public  static  String uid="";
    public  static  String user="";
    public  static  String img="";
    public static String canteen;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_info_activity);
        FM = getSupportFragmentManager();
        FragmentTransaction FT;

        FT = FM.beginTransaction();
        FT.replace(R.id.contianerview, new favorites()).commit();



        receiveUid();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.fav:
                    FragmentTransaction FT;

                    FT = FM.beginTransaction();
                    FT.replace(R.id.contianerview, new favorites()).commit();

                    // checkfood("Favorite",true, FirebaseDatabase.getInstance().getReference("User Informations/"+uid));
                   // findViewById(R.id.recycler_view_order).setVisibility(View.GONE);
                    findViewById(R.id.recycler_view).setVisibility(View.VISIBLE);

                    return true;
                case R.id.order:

                    FT = FM.beginTransaction();
                    FT.replace(R.id.contianerview, new orders()).commit();

                    //checkfood("Orders/All orders",false,FirebaseDatabase.getInstance().getReference("User Informations/"+uid));
                    findViewById(R.id.recycler_view_order).setVisibility(View.VISIBLE);
                    findViewById(R.id.recycler_view).setVisibility(View.GONE);
                    return true;
            }
            return false;
        }
    };
    void receiveUid(){
        try{
            Intent intent=getIntent();
            Log.e("User info",intent.getStringExtra("Uid"));
            uid=intent.getStringExtra("Uid");
            canteen=intent.getStringExtra("Address");
            load_user_Personal_info(uid);
            load_total_messages();
         try {img=intent.getStringExtra("img");
             Glide.with(getApplicationContext()).applyDefaultRequestOptions(RequestOptions.
                     circleCropTransform()).load(img).
                     into((ImageView)findViewById(R.id.userimage));

             if (img!=null){
              findViewById(R.id.userimage).setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View view) {
                      Intent intent1=new Intent(user_info.this,UserImg.class);
                      intent1.putExtra("USERIMG",img);
                      ActivityOptionsCompat optionsCompat = ActivityOptionsCompat
                              .makeSceneTransitionAnimation
                                      (user_info.this,findViewById(R.id.userimage) , "user_image");
                      startActivity(intent1, optionsCompat.toBundle());


                  }
              });

             }
         }catch (Exception e){}
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    void checkfood(final String Address, final boolean load_fav, final DatabaseReference databaseReference){
        firebase= FirebaseDatabase.getInstance().getReference().
                child("User Informations/"+uid+"/"+Address);
 //        findViewById(R.id.empty_food).setVisibility(View.GONE);
        firebase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChildren()||dataSnapshot.exists())

                {
                    if (!load_fav)
                    firebase_for_order(databaseReference.child("Orders/All orders"));
                    else
                    firebase(databaseReference.child(Address));
                }
               // else findViewById(R.id.empty_food).setVisibility(View.VISIBLE);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void firebase_for_order(DatabaseReference child) {
        Log.e("User info","Inside order " );
        recyclerView=findViewById(R.id.recycler_view_order);
        recyclerView.setVisibility(View.VISIBLE);
       // findViewById(R.id.recycler_view_order).setVisibility(View.GONE);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(adapter);

          FirebaseRecyclerAdapter<album_allorders, mainactiv_allorders.FoodViewHolder> FBRA=new FirebaseRecyclerAdapter<album_allorders, mainactiv_allorders.FoodViewHolder>(
                album_allorders.class,
                R.layout.new_orders_layout,
                mainactiv_allorders.FoodViewHolder.class,
                child)
        {
            @Override
            protected void populateViewHolder(mainactiv_allorders.FoodViewHolder viewHolder, final album_allorders model, int position) {
             //   progressBar.setVisibility(View.GONE);
                Log.e("User info","Inside order "+model.getOrderNo() );

                viewHolder.setOrderNo(model.getOrderNo());
                viewHolder.setTime(model.getDelivery());

                viewHolder.setStatus(model.getStatus());

                viewHolder.setPayment(model.getPayment());
                try {
                    viewHolder.setPayment2(String.valueOf((Integer.parseInt(model.getTotalAmount()) - Integer.parseInt(model.getPayment()))),getApplicationContext());

                    viewHolder.setMore(String.valueOf((Integer.parseInt(model.getTotalFood()) - 4)));
                }catch (Exception ignored){}
                // viewHolder.setMore(String.valueOf(model.getTotalFood()));
                viewHolder.setImage0(model.getImage0(),getApplicationContext());
                viewHolder.setImage1(model.getImage1(),getApplicationContext());
                viewHolder.setImage2(model.getImage2(),getApplicationContext());
                viewHolder.setImage3(model.getImage3(),getApplicationContext());
                try {
                ////    Load_User_Credentials(viewHolder.Username(), viewHolder.UserMobile(),
                    //        viewHolder.UserIMG(), FirebaseDatabase.getInstance().getReference("User Informations").child(model.getUserId()));
                }catch (Exception ignored){}
                viewHolder.Relative().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(user_info.this,editOrder.class);
                        intent.putExtra("OrderNo",model.getOrderNo());
                        intent.putExtra("Uid",model.getUserId());
                        startActivity(intent);
                    }
                });


            }


        };
        recyclerView.setAdapter(FBRA);

    }
    void firebase(DatabaseReference child){
        Log.e("User info","Inside order " );
        recyclerView=findViewById(R.id.recycler_view);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(adapter);

        firebase= FirebaseDatabase.getInstance().getReference("User Informations/"+uid+"/Favorite");
        FirebaseRecyclerAdapter<album_allorders, mainactiv_allorders.FoodViewHolder> FBRA=new FirebaseRecyclerAdapter<album_allorders, mainactiv_allorders.FoodViewHolder>(
                album_allorders.class,
                R.layout.editfoodlayout,
                mainactiv_allorders.FoodViewHolder.class,
                child

        ) {
            @Override
            protected void populateViewHolder(final mainactiv_allorders.FoodViewHolder viewHolder, final album_allorders model, int position) {

                Log.e("User info","Inside order "+model.getFood_Image());
                 viewHolder.setFood_name(model.getFood_name());
              //  viewHolder.setDiscount(String .valueOf(model.getDiscount()),String.valueOf(model.getPrice()));
             //   viewHolder.setImage0(model.getFood_Image(),getApplicationContext());
                 viewHolder.setDescription(model.getdescription());
             //   viewHolder.setrating(String.valueOf(model.getSum_of_Ratings()),String.valueOf(model.getTotal_NoOfTime_Rated()));
                load_food_info(model.getFood_name(),model.getType(),viewHolder.mView,(TextView) viewHolder.mView.findViewById(R.id.description),(ImageView) viewHolder.mView.findViewById(R.id.img1));
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(user_info.this,editFood.class);
                        intent.putExtra("Name",model.getFood_name());
                        intent.putExtra("Image","");
                        intent.putExtra("Type",model.getType());
                        intent.putExtra("Description",viewHolder.returndesc());
                        intent.putExtra("Price",viewHolder.returnprice());
                          intent.putExtra("From",false);
                        intent.putExtra("Discount",viewHolder.returndiscount());
                        try{
                            Intent intent1=getIntent();
                          //  if (intent1.getStringExtra("Activity").contentEquals("Edit"))
                                intent.putExtra("Activity","Edit");
                         //   else intent.putExtra("Activity","Discount");
                        }catch (Exception e){e.printStackTrace();}
                        startActivity(intent);
                    }
                });
            }
        };

        recyclerView.setAdapter(FBRA);
    }
    public void setDiscount(String discount, String price,View mView) {
        TextView prices = mView.findViewById(R.id.count);
        TextView discounts= mView.findViewById(R.id.discount);
        try {
            if (discount==null||discount.equals("0")||price.equals("0"))
            {
                prices.setText(String.format("₹%s", price));
            }else {


                if (discount.isEmpty() || discount.equals(price)) {


                    prices.setText(String.format("₹%s", price));
                    discounts.setText("");
                    // t2.setText("");
                    //  t.setVisibility(View.GONE);
                } else {
                    discounts.setText("");
                    discounts.setPaintFlags(discounts.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                    discounts.setText(String.format("₹%s", price));
                    // t2.setVisibility(View.VISIBLE);
                    //  textView.setText("");
                    // t.setVisibility(View.VISIBLE);
                    prices.setText(String.format("₹%s", discount));
                }
            }
        } catch (Exception NullPointerException) {
            // mView.findViewById(R.id.CakePrice).setVisibility(View.VISIBLE);
            if (price!=null)
                prices.setText(String.format("₹%s", price));
            else prices.setText(String.format("₹%s", discount));
            discounts.setText("");
        }
    }

    void load_food_info(final String foodname, String type, final View view, final TextView description, final ImageView imageView){
        FirebaseDatabase.getInstance().getReference("Food Items/"+canteen+"/"+type+"/"+foodname).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()||!dataSnapshot.hasChildren()){
                    FirebaseDatabase.getInstance().getReference().child("User Informations/"+canteen+"/"+uid+"/Favorite/"+foodname).setValue(null);
                }else{  Desc=(String)dataSnapshot.child("Description").getValue();
                      description.setText(Desc);
                      img=(String)dataSnapshot.child("Food_image").getValue();
                      Glide.with(getApplicationContext()).load(img).into(imageView);
try {
    dis=(int)dataSnapshot.child("Discount").getValue();
}catch (Exception ignored){}
                  //    price=(int)dataSnapshot.child("price").getValue();
                      setDiscount(String.valueOf (dataSnapshot.child("Discount").getValue()),String.valueOf(
                              dataSnapshot.child("price").getValue()
                      ),view);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    void load_user_Personal_info(final String uid){
        final DatabaseReference databaseReference=FirebaseDatabase.getInstance().
                getReference("User Informations/"+uid);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TextView t;
                t=findViewById(R.id.userphone);
                t.setText((String) dataSnapshot.child("Mobile_number").getValue());

                t=findViewById(R.id.useremail);
                t.setText((String) dataSnapshot.child("Email").getValue());

               // t=findViewById(R.id.username);
               // t.setText((String) dataSnapshot.child("Name").getValue());
                user=(String) dataSnapshot.child("Name").getValue();
                setTitle(user);
                t=findViewById(R.id.usercollege);
                t.setText((String) dataSnapshot.child("College_id").getValue());

try {
    Glide.with(getApplicationContext()).applyDefaultRequestOptions(RequestOptions.
            circleCropTransform()).load((String) dataSnapshot.child("User_Img").
            getValue()).into((ImageView)findViewById(R.id.userimage));
}catch (Exception ignored){

}
                t=findViewById(R.id.order);
                t.setText(String.valueOf(( int) dataSnapshot.child("Orders/All orders").getChildrenCount()));

                t=findViewById(R.id.userfav);
                t.setText(String.valueOf ((int) dataSnapshot.child("Favorite").getChildrenCount()));

               // t=findViewById(R.id.usermessage);
              //  t.setText((String) dataSnapshot.child("Moble_number").getValue());
                //load_total_messages();
                //firebase_for_order(databaseReference.child("Orders/All orders"));
                //firebase(databaseReference.child("Favorite"));
               // checkfood("Favorite",true);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    void load_total_messages(){
       final DatabaseReference databaseReference=FirebaseDatabase.getInstance().
               getReference("Messages/Help/"+
               canteen+"/"+uid+"/Messages");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("MessageChild", String.valueOf(dataSnapshot.getChildrenCount())+" address <"+
                databaseReference+">");

                if (dataSnapshot.hasChildren()){
                  TextView  t=findViewById(R.id.usermessage);
                  t.setText(String.valueOf( (int) dataSnapshot.getChildrenCount()));
                }else{
                    TextView  t=findViewById(R.id.usermessage);
                    t.setText("0");

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void loadMessagesClcked(View view) {


        Intent intent=new Intent(this,help_activity.class);
        intent.putExtra("UID",uid);
        intent.putExtra("Address",canteen);
        intent.putExtra("UserName",user);
        intent.putExtra("img",img);
        startActivity(intent);
     }
}
