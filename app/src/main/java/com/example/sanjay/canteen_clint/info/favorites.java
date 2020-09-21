package com.example.sanjay.canteen_clint.info;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.sanjay.canteen_clint.R;
import com.example.sanjay.canteen_clint.adapter_allorders;
import com.example.sanjay.canteen_clint.album_allorders;
import com.example.sanjay.canteen_clint.editFood;
import com.example.sanjay.canteen_clint.editOrder;
import com.example.sanjay.canteen_clint.mainactiv_allorders;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import static com.example.sanjay.canteen_clint.info.user_info.canteen;

public class favorites extends Fragment {
    private adapter_allorders adapter;
    Map<String,String> image;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycler_allorders, container, false);
        view.findViewById(R.id.progressFood).setVisibility(View.GONE);
        view.        findViewById(R.id.appbar).setVisibility(View.GONE);
        image=new HashMap<>();
        firebase(FirebaseDatabase.getInstance().getReference().child("User Informations/"+user_info.uid+"/Favorite"),view);
        return view;
    }
    void firebase(DatabaseReference child,View view){
        Log.e("User info","Inside order " );
        RecyclerView
        recyclerView=view.findViewById(R.id.recycler_view);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(adapter);

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
                 viewHolder.setDescription(model.getdescription());
                 load_food_info(model.getFood_name(),model.getType(),viewHolder.mView,(TextView) viewHolder.mView.findViewById(R.id.description),(ImageView) viewHolder.mView.findViewById(R.id.img1));
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(getContext() ,editFood.class);
                        intent.putExtra("Name",model.getFood_name());
                     intent.putExtra("Image",image.get(model.getFood_name()));
                        intent.putExtra("Type",model.getType());
                        intent.putExtra("Description",viewHolder.returndesc());
                        intent.putExtra("Price",viewHolder.returnprice());
                        intent.putExtra("From",false);
                        intent.putExtra("Discount",viewHolder.returndiscount());

                        startActivity(intent);
                    }
                });
            }
        };

        recyclerView.setAdapter(FBRA);
    }
    public void setDiscount(String discount, String price,View mView) {
        TextView prices = mView.findViewById(R.id.count);
        Log.e("price+discount","Price "+price+" discount"+discount);
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
                    FirebaseDatabase.getInstance().getReference().child("User Informations/"+user_info.uid+"/Favorite/"+foodname).setValue(null);
                }else{  String Desc=(String)dataSnapshot.child("Description").getValue();
                    description.setText(Desc);
                  String  img=(String)dataSnapshot.child("Food_Image").getValue();
                  image.put(foodname,img);
                    Log.e("Image",img);
                  try {
                       Glide.with(getContext()).load(img).into(imageView);
                   }catch (Exception e){}

                    //    price=(int)dataSnapshot.child("price").getValue();
                    setDiscount(String.valueOf (dataSnapshot.child("discount").getValue()),String.valueOf(
                            dataSnapshot.child("price").getValue()
                    ),view);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
