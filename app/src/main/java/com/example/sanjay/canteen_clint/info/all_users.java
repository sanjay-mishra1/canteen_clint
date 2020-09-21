package com.example.sanjay.canteen_clint.info;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sanjay.canteen_clint.R;
import com.example.sanjay.canteen_clint.adapter_allorders;
import com.example.sanjay.canteen_clint.album_allorders;
 import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import static com.example.sanjay.canteen_clint.extra_classes.Constants.Canteen;

public class all_users extends AppCompatActivity {
    private adapter_allorders adapter;
    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    private Query query;
    private ImageView searchButton;
    private String canteen;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_allorders);
        TextView title=findViewById(R.id.title);
        title.setText("All Users");
        searchButton=findViewById(R.id.search);
         recyclerView = (RecyclerView)  findViewById(R.id.recycler_view);
        //   total_food = view.findViewById(R.id.total_foodItem);
        //mainLinear=view.findViewById(R.id.main_relative_orders);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        receive();
          databaseReference= FirebaseDatabase.getInstance().getReference("User Informations");

        loadfirebase("");
        searchBarListner();
    }
    void receive(){
        Intent intent=getIntent();
        if (Canteen.equalsIgnoreCase("admin"))
            canteen= intent.getStringExtra("Address");

        else
            canteen=Canteen;
    }
    void searchBarListner(){
        EditText searchbar=findViewById(R.id.searchBarEdittext);
        searchbar.setHint("Enter name");
        searchbar.addTextChangedListener(new TextWatcher() {
              String[] words;
            String  key;

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
                loadfirebase(key.trim() );

            }
        });
    }

    void loadfirebase(String keys){
          query=databaseReference.orderByChild("Name").startAt(keys).endAt(keys+"\uf8ff");
        findViewById(R.id.progressFood).setVisibility(View.VISIBLE);
        FirebaseRecyclerAdapter<adapter, mainactiv_allorders.FoodViewHolder> FBRA=new FirebaseRecyclerAdapter<adapter, mainactiv_allorders.FoodViewHolder>(
                adapter.class,
                R.layout.all_users,
                mainactiv_allorders.FoodViewHolder.class,
                query
        ) {
            @Override
            protected void populateViewHolder(final mainactiv_allorders.FoodViewHolder viewHolder, final adapter model, int position) {

                findViewById(R.id.progressFood).setVisibility(View.GONE);
               viewHolder.mView. findViewById(R.id.text_message_body).setVisibility(View.VISIBLE);
                viewHolder.setUserImage(model.getUser_Img());
                viewHolder.setUser_name(model.getName());
                viewHolder.setMessage(model.getCanteen());
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(all_users.this,user_info.class);
                        if (model.getUid()!=null)
                        {  intent.putExtra("Uid",model.getUid());
                           intent.putExtra("img",model.getUser_Img());
                           intent.putExtra("Address",model.getCanteen());
                         //   ActivityOptionsCompat optionsCompat = ActivityOptionsCompat
                           //         .makeSceneTransitionAnimation
                             //               (all_users.this, viewHolder.getImageId(), "user_image");
                            startActivity(intent);

                        }else{
                            Toast.makeText(all_users.this,"Failed to load user info,might be an old account",Toast.LENGTH_SHORT).show();
                        }
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

    }

    public void emptySearchbarClicked(View view) {
        EditText editText=findViewById(R.id.searchBarEdittext);
     /*   findViewById(R.id.searchBar).setVisibility(View.GONE);
        if (!editText.getText().toString().trim().isEmpty())
            editText.setText("");*/
       //  loadfirebase("");
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
