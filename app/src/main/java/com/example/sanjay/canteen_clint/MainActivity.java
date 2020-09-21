package com.example.sanjay.canteen_clint;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.sanjay.canteen_clint.info.food_info;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import static com.example.sanjay.canteen_clint.extra_classes.Constants.Canteen;

public class MainActivity extends AppCompatActivity {

     private DatabaseReference firebase;
    private RecyclerView recyclerView;
    private adapter_allorders adapter;
    private TextView title;

    private String Address;
    private ContentLoadingProgressBar progressBar;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            title=findViewById(R.id.title);
            switch (item.getItemId()) {
                case R.id.special:
                    title.setText(getResources().getString(R.string.special));
                    Address="Special";
                    //title.setText("Special");
                    checkfood();
                     return true;
                case R.id.snaks:
                    title.setText(getResources().getString(R.string.Snaks));
                    Address="Snaks";
                    checkfood();
                     return true;
                case R.id.Beverages:
                    title.setText(getResources().getString(R.string.Beverages));
                    Address="Beverages";
                    checkfood();
                     return true;
                case R.id.Meal:
                    Address="Meal";
                    checkfood();
                    title.setText(getResources().getString(R.string.Meal));
                     return true;
                case R.id.disable: Address="Disable";
                    checkfood();
                    title.setText(getResources().getString(R.string.disables));
                    return true;
            }
            return false;
        }
    };
    private ImageView searchButton;
    private String addr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView=findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        title=findViewById(R.id.title);
        searchButton=findViewById(R.id.search);
        title.setText(getResources().getString(R.string.special));
        Address="Special";
        checkfood();
        receive();
        searchBarListner();
         BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }
    private void receive() {
        Intent intent=getIntent();
        if (Canteen.equalsIgnoreCase("admin"))
            addr= intent.getStringExtra("Address");

        else
            addr=Canteen;


    }

    void checkfood( ){
        //firebase= FirebaseDatabase.getInstance().getReference().child("Food Items/"+Address);
        firebase= FirebaseDatabase.getInstance().getReference().child("Food Items/"+addr+"/"+Address);
        findViewById(R.id.progressbar).setVisibility(View.VISIBLE);
        findViewById(R.id.empty_food).setVisibility(View.GONE);

        firebase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                findViewById(R.id.progressbar).setVisibility(View.GONE);

                if (dataSnapshot.hasChildren()||dataSnapshot.exists())
                {   emptySearchbarClicked(findViewById(R.id.cancelsearcBar));
                    findViewById(R.id.search).setVisibility(View.VISIBLE);

                    firebase("" );

                }
                else {
                    findViewById(R.id.search).setVisibility(View.GONE);

                    findViewById(R.id.empty_food).setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    void searchBarListner(){
        EditText searchbar=findViewById(R.id.searchBarEdittext);
        searchbar.setHint("Food name");
        searchbar.addTextChangedListener(new TextWatcher() {
            String[] words;
            String key;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                key=  (editable.toString().trim());
                if (!key .isEmpty()) {
                    words = key.split("\\s");
                    key="";
                    for (String w : words) {
                        key = key + String.valueOf(w.charAt(0)).toUpperCase() + w.substring(1).toLowerCase() + " ";
                        Log.e("Searching","Data Original <"+w+"> modified <"+key+">");
                    }

                }
                firebase(key.trim() );

            }


            // firebase(editable.toString());

        });
    }

    void firebase(String keys){
        // firebase= FirebaseDatabase.getInstance().getReference().child("Food Items/"+Address);
         firebase= FirebaseDatabase.getInstance().getReference().child("Food Items/"+addr+"/"+Address);
        Query query=firebase.orderByChild("Food_name").startAt(keys).endAt(keys+"\uf8ff");

        FirebaseRecyclerAdapter<album_allorders, mainactiv_allorders.FoodViewHolder> FBRA=new FirebaseRecyclerAdapter<album_allorders, mainactiv_allorders.FoodViewHolder>(
                 album_allorders.class,
                R.layout.editfoodlayout,
                mainactiv_allorders.FoodViewHolder.class,
                query

        ) {
            @Override
            protected void populateViewHolder(mainactiv_allorders.FoodViewHolder viewHolder, final album_allorders model, int position) {


           //     progressBar.setVisibility(View.GONE);
              viewHolder.setFood_name(model.getFood_name());
              //viewHolder.setprice(String.valueOf(model.getPrice()));
                viewHolder.setDiscount(String .valueOf(model.getDiscount()),String.valueOf(model.getPrice()));
              viewHolder.setImage0(model.getFood_Image(),getApplicationContext());
              viewHolder.setDescription(model.getdescription());
              viewHolder.setrating(String.valueOf(model.getSum_of_Ratings()),String.valueOf(model.getTotal_NoOfTime_Rated()));
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent,intent2=getIntent();
                  try {
                      if (intent2.getStringExtra("INFO").equals("INFO"))
                          intent=new Intent(MainActivity.this,food_info.class);
                      else intent=new Intent(MainActivity.this,editFood.class);
                  }catch (Exception e)
                  {
                      intent=new Intent(MainActivity.this,editFood.class);
                  }
                        intent.putExtra("Name",model.getFood_name());
                        intent.putExtra("Address",addr);
                        intent.putExtra("Image",model.getFood_Image());
                        intent.putExtra("Type",model.getType());
                        intent.putExtra("Description",model.getdescription());
                        intent.putExtra("Price",String.valueOf(model.getPrice()));
                        if(title.getText().toString().trim().toUpperCase().contains("DISA"))
                        intent.putExtra("From",true);
                        else intent.putExtra("From",false);
                        intent.putExtra("Discount",String.valueOf(model.getDiscount()));
                        try{
                            Intent intent1=getIntent();
                            if (intent1.getStringExtra("Activity").contentEquals("Edit"))
                                intent.putExtra("Activity","Edit");
                            else intent.putExtra("Activity","Discount");
                        }catch (Exception e){e.printStackTrace();}
                        startActivity(intent);
                    }
                });
            }
        };

recyclerView.setAdapter(FBRA);
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void animation(boolean show) {
        final RelativeLayout cardView = findViewById(R.id.searchBar);

        int height = cardView.getHeight();
        int width = cardView.getWidth();
        int endRadius = (int) Math.hypot(width, height);
        int cx = (int) (searchButton.getX());// + (cardView.getWidth()));
        int cy = (int) (searchButton.getY());// + cardView.getHeight();

        if (show) {
            Animator revealAnimator = ViewAnimationUtils.createCircularReveal(cardView, cx, cy, cy, endRadius);
            revealAnimator.setDuration(400);
            revealAnimator.start();
            cardView.setVisibility(View.VISIBLE);
            // showZoomIn();
        } else {
            try {
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
            }catch (Exception ignored){}
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
        /*findViewById(R.id.searchBar).setVisibility(View.GONE);
        if (!editText.getText().toString().trim().isEmpty())
            editText.setText("");*/
        // firebase("");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            animation(false);
        }else{
            findViewById(R.id.searchBar).setVisibility(View.VISIBLE);
        }
        if (!editText.getText().toString().trim().isEmpty())
            editText.setText("");
    }

    public void endActivity(View view) {
        finish();
    }
}
