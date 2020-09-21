package com.example.sanjay.canteen_clint.search;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.sanjay.canteen_clint.R;
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

import java.util.HashSet;

import static com.example.sanjay.canteen_clint.extra_classes.Constants.Canteen;
import static com.example.sanjay.canteen_clint.search.pendingorders.expand;
import static com.example.sanjay.canteen_clint.search.pendingorders.collapse;

public class allOrders  extends AppCompatActivity {
     public static HashSet<String> OrderFilter;
      public DatabaseReference mDatabase;
     private RecyclerView recyclerView;
       ProgressBar progressBar;
    private Query query;
    private ImageView searchButton;
    MyAdapter adapter;
    public static String SearchKey="";
    private String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_allorders);
        progressBar= findViewById(R.id.progressFood);
          OrderFilter=new HashSet<>();
        searchButton=findViewById(R.id.search);

         recyclerView = (RecyclerView)  findViewById(R.id.recycler_view);
         RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        receive();
        check(recyclerView);


    //firebase("");
    searchBarListner();
    }
    void receive(){
        Intent intent=getIntent();
        String addr=intent.getStringExtra("OrderAddress");
        TextView textView=findViewById(R.id.title);
        textView.setText(addr);
        if (Canteen.equalsIgnoreCase("admin"))
            address= intent.getStringExtra("Address");

        else
            address=Canteen;
        if (addr.equalsIgnoreCase("new orders"))
            findViewById(R.id.deliveredCheckbox).setVisibility(View.GONE);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Orders/"+address+"/"+addr);

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
                //firebase(editable.toString());
              //  recyclerView.removeAllViews();
              //  query=mDatabase.orderByChild("OrderNo").startAt(editable.toString()).endAt(editable.toString()+"\uf8ff");

              //  adapter=new MyAdapter(album_allorders.class, R.layout.payments,
              //          mainactiv_allorders.FoodViewHolder.class, query,getApplicationContext(),progressBar);
              //  recyclerView.setAdapter(adapter);
                SearchKey=editable.toString();
               try {
                   adapter.notifyDataSetChanged();
               }catch (Exception ignored){}

            }
        });
    }
    void check(final RecyclerView recyclerView){
        query=mDatabase.orderByChild("OrderNo").startAt(SearchKey).endAt(SearchKey+"\uf8ff");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if ( !dataSnapshot.exists()){
                    findViewById(R.id.nothing_found).setVisibility(View.VISIBLE);
                    findViewById(R.id.search).setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }else{
                    adapter=    new MyAdapter(album_allorders.class, R.layout.payments,
                            mainactiv_allorders.FoodViewHolder.class, query,getApplicationContext(),progressBar,address);
                    recyclerView.setAdapter(adapter);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }
    void firebase(String keys){
        progressBar.setVisibility(View.VISIBLE);
          query=mDatabase.orderByChild("OrderNo").startAt(keys).endAt(keys+"\uf8ff");
        FirebaseRecyclerAdapter<album_allorders, mainactiv_allorders.FoodViewHolder> FBRA=new FirebaseRecyclerAdapter<album_allorders, mainactiv_allorders.FoodViewHolder>(
                album_allorders.class,
                R.layout.new_orders_layout,
                mainactiv_allorders.FoodViewHolder.class,
                query
        )
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
                }catch (Exception ignored){}
                             try {
                //    viewHolder.setMore(String.valueOf((Integer.parseInt(model.getTotalFood()) - 4)));
                             }catch (Exception ignored){}
                               try {
                    viewHolder.setMore(String.valueOf(model.getTotalFood()));
                               }catch (Exception ignored){}
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
                        Intent intent=new Intent(allOrders.this,editOrder.class);
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
            revealAnimator.setDuration(400);
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
            anim.setDuration(400);
            anim.start();
        }

    }

    public void searchuttonOnclick(View view) {
       // findViewById(R.id.searchBar).setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            animation(true);
        }else{
            findViewById(R.id.searchBar).setVisibility(View.VISIBLE);
        }

        expand(findViewById(R.id.order_filters),300,200);
           }

    public void emptySearchbarClicked(View view) {
        EditText editText=findViewById(R.id.searchBarEdittext);
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            animation(false);
        }else{
            findViewById(R.id.searchBar).setVisibility(View.INVISIBLE);
        }
        if (!editText.getText().toString().trim().isEmpty())
        {
            editText.setText("");
            SearchKey="";
            adapter.notifyDataSetChanged();

        }
          collapse(findViewById(R.id.order_filters),300,200);

    }

    public void endActivity(View view) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OrderFilter.clear();
        adapter=null;

    }

    public void pendingClicked(View view) {
        if (!OrderFilter.contains("Pending"))
            OrderFilter.add("Pending");
        else OrderFilter.remove("Pending");
        adapter.notifyDataSetChanged();
    }

    public void CompletedClicked(View view) {
        if (!OrderFilter.contains("Completed"))
            OrderFilter.add("Completed");
        else OrderFilter.remove("Completed");
        adapter.notifyDataSetChanged();
    }

    public void AcceptedClicked(View view) {
        if (!OrderFilter.contains("Accepted"))
            OrderFilter.add("Accepted");
        else OrderFilter.remove("Accepted");
        adapter.notifyDataSetChanged();}

    public void DeliveredClicked(View view) {
        if (!OrderFilter.contains("Delivered"))
            OrderFilter.add("Delivered");
        else OrderFilter.remove("Delivered");
        adapter.notifyDataSetChanged();
    }

    public void CancelledClicked(View view) {
       if (!OrderFilter.contains("Cancel_user"))
             OrderFilter.add("Cancel_user");
       else OrderFilter.remove("Cancel_user");
        adapter.notifyDataSetChanged();
    }

    public void okClicked(View view) {
        finish();
    }
}