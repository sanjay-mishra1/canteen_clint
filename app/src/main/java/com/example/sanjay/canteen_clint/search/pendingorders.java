package com.example.sanjay.canteen_clint.search;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.sanjay.canteen_clint.R;
import com.example.sanjay.canteen_clint.adapter_allorders;
import com.example.sanjay.canteen_clint.album_allorders;
import com.example.sanjay.canteen_clint.editOrder;
import com.example.sanjay.canteen_clint.mainactiv_allorders;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class pendingorders extends AppCompatActivity {
    public String Selected_foodName[];
    public int Selected_foodPrice[];
    public String Name;
    public String M_name,M_price,M_quantity,extra_t;
   // private TextView total_food;
    ImageView next;
    String b="0" ;
    public int count = 0;
    FirebaseAuth mAuth;
    public DatabaseReference mDatabase;
   // private FirebaseAuth.AuthStateListener mAuthlistener;
    private RecyclerView recyclerView;
    private adapter_allorders adapter;
   // private List<album_allorders> albumList;
    RelativeLayout relativeLayout;
    LinearLayout mainLinear;
    ImageView dec;
    Button cancel;
    ProgressBar progressBar;

public View view;
    private Query query;
    private ImageView searchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // view = inflater.inflate(R.layout.recycler_allorders, container, false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_allorders);
        TextView         title=findViewById(R.id.title);
        searchButton=findViewById(R.id.search);
        title.setText("New Orders");
        Selected_foodName = new String[10];
        progressBar= findViewById(R.id.progressFood);
        progressBar.setVisibility(View.INVISIBLE);

         // Image = new String[10];
       // next = view.findViewById(R.id.next_button);
        recyclerView = (RecyclerView)  findViewById(R.id.recycler_view);
     //   total_food = view.findViewById(R.id.total_foodItem);
        //mainLinear=view.findViewById(R.id.main_relative_orders);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        //cancel=view.findViewById(R.id.cancel_order);
        mAuth=FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Orders/New Orders");

   //     super.onStart();
    onStarts("");
    searchBarListner();
    }
    void searchBarListner(){
        EditText searchbar=findViewById(R.id.searchBarEdittext);
        searchbar.setInputType(InputType.TYPE_CLASS_NUMBER);
        searchbar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                onStarts(editable.toString());
            }
        });
    }



    public void onStarts(String keys) {
          query=mDatabase.orderByChild("OrderNo").startAt(keys).endAt(keys+"\uf8ff");

         progressBar.setVisibility(View.VISIBLE);
        FirebaseRecyclerAdapter<album_allorders, mainactiv_allorders.FoodViewHolder> FBRA=new FirebaseRecyclerAdapter<album_allorders, mainactiv_allorders.FoodViewHolder>(
                album_allorders.class,
                R.layout.new_orders_layout,
                mainactiv_allorders.FoodViewHolder.class,
                query)
        {
            @Override
            protected void populateViewHolder(mainactiv_allorders.FoodViewHolder viewHolder, final album_allorders model, int position) {
                progressBar.setVisibility(View.GONE);
                  viewHolder.setOrderNo(model.getOrderNo());
                  viewHolder.setTime(model.getDelivery());

                  viewHolder.setStatus(model.getStatus());

                  viewHolder.setPayment(getString(R.string.rupeesSymbol)+" "+model.getPayment());
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
                       Load_User_Credentials(viewHolder.Username(), viewHolder.UserMobile(),
                               viewHolder.UserIMG(), FirebaseDatabase.getInstance().getReference("User Informations").child(model.getUserId()));
                   }catch (Exception ignored){}
                  viewHolder.Relative().setOnClickListener(new View.OnClickListener() {
                      @Override
                      public void onClick(View view) {
                          Intent intent=new Intent(pendingorders.this,editOrder.class);
                          intent.putExtra("OrderNo",model.getOrderNo());
                          intent.putExtra("Uid",model.getUserId());
                          startActivity(intent);
                      }
                  });


            }


        };
        recyclerView.setAdapter(FBRA);

    }
    void Load_User_Credentials(final TextView username, final TextView Mobile, final ImageView img, DatabaseReference databaseReference)
    {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
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
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void animation(boolean show) {
        final RelativeLayout cardView = findViewById(R.id.searchBar);

        int height = cardView.getHeight();
        int width = cardView.getWidth();
        int endRadius = (int) Math.hypot(width, height);
        int cx = (int) (searchButton.getX())+10;// + (cardView.getWidth()));
        int cy = (int) (searchButton.getY())+30;// + cardView.getHeight();

        if (show) {
            Animator revealAnimator = ViewAnimationUtils.createCircularReveal(cardView, cx, cy, cy, endRadius);
            revealAnimator.setDuration(500);
            revealAnimator.start();
            cardView.setVisibility(View.VISIBLE);
            // showZoomIn();
        } else {
            Animator anim =
                    ViewAnimationUtils.createCircularReveal(cardView, cx, cy, cx, 0);

            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    cardView.setVisibility(View.INVISIBLE);

                }
            });
            anim.setDuration(500);
            anim.start();
        }

    }

    public void searchuttonOnclick(View view) {
     //   findViewById(R.id.searchBar).setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            animation(true);
        }else{
            findViewById(R.id.searchBar).setVisibility(View.VISIBLE);
        }
    }

    public void emptySearchbarClicked(View view) {
        EditText editText=findViewById(R.id.searchBarEdittext);
        //  findViewById(R.id.searchBar).setVisibility(View.GONE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            animation(false);
        }else{
            findViewById(R.id.searchBar).setVisibility(View.INVISIBLE);
        }
        if (!editText.getText().toString().trim().isEmpty())
            editText.setText("");
        // firebase("");
        /*EditText editText=findViewById(R.id.searchBarEdittext);
        findViewById(R.id.searchBar).setVisibility(View.GONE);
        if (!editText.getText().toString().trim().isEmpty())
            editText.setText("");*/
        // firebase("");
    }
    public static void expand(final View v, int duration, int targetHeight) {

        int prevHeight = v.getHeight();

        v.setVisibility(View.VISIBLE);
        ValueAnimator valueAnimator = ValueAnimator.ofInt(prevHeight, targetHeight);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                v.getLayoutParams().height = (int) animation.getAnimatedValue();
                v.requestLayout();
            }
        });
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.setDuration(duration);
        valueAnimator.start();
    }

    public static void collapse(final View v, int duration, int targetHeight) {
        int prevHeight = v.getHeight();
         final ValueAnimator valueAnimator = ValueAnimator.ofInt(prevHeight, 0);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                v.getLayoutParams().height = (int) animation.getAnimatedValue();
                v.requestLayout();
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                v.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.setDuration(duration);
        valueAnimator.start();


    }

    public void endActivity(View view) {
        finish();
    }




}