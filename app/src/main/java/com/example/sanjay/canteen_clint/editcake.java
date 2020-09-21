package com.example.sanjay.canteen_clint;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
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

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.FirebaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import static com.example.sanjay.canteen_clint.extra_classes.Constants.Canteen;

/**
 * Created by sanjay on 03/07/2018.
 */

public class editcake extends AppCompatActivity {
    private String address;
    private TextView title;
    RecyclerView recyclerView;
    private adapter_allorders adapter;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.cake:
                    setTitle(getResources().getString(R.string.cake));
                    address="Cake";
                    checkfood( );
                    return true;
                case R.id.disable:
                    setTitle(getResources().getString(R.string.disables));
                    address="Cake_Disable";

                    checkfood( );
                    return true;

            }
            return false;
        }
    };
    private DatabaseReference databaseReference;
    private FirebaseRecyclerAdapter<album_allorders, mainactiv_allorders.FoodViewHolder> firebaseRecyclerAdapter;
    private ImageView searchButton;
    private String canteen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_allorders);
        searchButton=findViewById(R.id.search);
        receive();
        findViewById(R.id.navigation).setVisibility(View.VISIBLE);
        title=findViewById(R.id.title);
        recyclerView=findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        setTitle(getResources().getString(R.string.cake));
        address="Cake";
        checkfood();
        searchBarListner();
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }
    void receive(){
        Intent intent=getIntent();
        if (Canteen.equalsIgnoreCase("admin"))
            canteen= intent.getStringExtra("Address");

        else
            canteen=Canteen;

    }

    void checkfood(){
       DatabaseReference Firebase= FirebaseDatabase.getInstance().getReference().child("Food Items/"+canteen+"/"+address);
        findViewById(R.id.nothingfound).setVisibility(View.GONE);
        findViewById(R.id.searchBar).setVisibility(View.INVISIBLE);
        title.setText(address);
        Firebase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChildren()||dataSnapshot.exists()) {
                    firebase("");
                    findViewById(R.id.search).setVisibility(View.VISIBLE);
                }
                else{
                    findViewById(R.id.progressFood).setVisibility(View.GONE);

                    findViewById(R.id.nothingfound).setVisibility(View.VISIBLE);
                    findViewById(R.id.search).setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    void searchBarListner(){
        EditText searchbar=findViewById(R.id.searchBarEdittext);
        searchbar.setHint("Cake name");
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
    void firebase(String keys) {
     //   super.onStart();
        findViewById(R.id.progressFood).setVisibility(View.VISIBLE);

        databaseReference= FirebaseDatabase.getInstance().getReference("Food Items/"+canteen+"/"+address);
        Query query=databaseReference.orderByChild("Food_name").startAt(keys).endAt(keys+"\uf8ff");

       firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<album_allorders, mainactiv_allorders.FoodViewHolder>(
                album_allorders.class,
                R.layout.editfoodlayout,
                mainactiv_allorders.FoodViewHolder.class,
                    query
        ) {
            @Override
            protected void populateViewHolder(mainactiv_allorders.FoodViewHolder viewHolder, final album_allorders model, int position) {
                findViewById(R.id.progressFood).setVisibility(View.GONE);

                viewHolder.setImage0(model.getFood_Image(),getApplicationContext());
                viewHolder.setFood_name(model.getFood_name());
//                viewHolder.setprice(String.valueOf(model.getGm500()));
                viewHolder.setDiscount(model.getGm500(),model.getGm500_d());
                viewHolder.setrating(String.valueOf(model.getSum_of_Ratings()),String.valueOf(model.getTotal_NoOfTime_Rated()));

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(editcake.this,editDetailCake.class);
                        intent.putExtra("CakeName",model.getFood_name());
                        intent.putExtra("gm500", model.getGm500());
                        intent.putExtra("Address", canteen);
                        intent.putExtra("gm200", model.getGm200());
                        intent.putExtra("gm1000", model.getGm1000());
                        intent.putExtra("gm500_d", model.getGm500_d());
                        intent.putExtra("gm200_d", model.getGm200_d());
                        intent.putExtra("gm1000_d", model.getGm1000_d());
                         intent.putExtra("Shape",model.getShape());
                        if(getTitle().toString().trim().toUpperCase().contains("DISA"))
                            intent.putExtra("From",true);
                        else intent.putExtra("From",false);
                        intent.putExtra("Discount",String.valueOf(model.getDiscount()));

                        intent.putExtra("Flavour",model.getFlavour());
                        intent.putExtra("Image",model.getFood_Image());
                        startActivity(intent);
                    }
                });
            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
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
    //    findViewById(R.id.searchBar).setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            animation(true);
        }else{
            findViewById(R.id.searchBar).setVisibility(View.VISIBLE);
        }
    }

    public void emptySearchbarClicked(View view) {
         EditText editText=findViewById(R.id.searchBarEdittext);
       /* findViewById(R.id.searchBar).setVisibility(View.GONE);
        if (!editText.getText().toString().trim().isEmpty())
        editText.setText("");*/
        // firebase("");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            animation(false);
        }else{
            findViewById(R.id.searchBar).setVisibility(View.INVISIBLE);
        }
        if (!editText.getText().toString().trim().isEmpty())
            editText.setText("");


    }

    public void endActivity(View view) {
        finish();
    }
}
