package com.example.sanjay.canteen_clint.search;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.sanjay.canteen_clint.R;
import com.example.sanjay.canteen_clint.album_allorders;
import com.example.sanjay.canteen_clint.editOrder;
import com.example.sanjay.canteen_clint.mainactiv_allorders;
import com.example.sanjay.canteen_clint.payments.filters;
import com.example.sanjay.canteen_clint.payments.pending_from_app;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import static com.example.sanjay.canteen_clint.search.allOrders.OrderFilter;
import static com.example.sanjay.canteen_clint.search.allOrders.SearchKey;


public class MyAdapter extends FirebaseRecyclerAdapter<album_allorders,mainactiv_allorders.FoodViewHolder>{
    private static final int LAYOUT_ONE = 1;
    private static final int LAYOUT_TWO = 2;
     private HashMap<String,String> credentials=new HashMap<>();
     private Context context;
     private ProgressBar progressBar;
     String canteen;
     MyAdapter(Class<album_allorders> modelClass,
               int modelLayout,
               Class<mainactiv_allorders.FoodViewHolder> viewHolderClass,
               Query ref, Context context, ProgressBar progressBar,String Canteen) {

        super(modelClass, modelLayout, viewHolderClass, ref );
         this.context=context;
          this.progressBar=progressBar;
          canteen=Canteen;
      }


    @Override
    public mainactiv_allorders.FoodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;

        if (viewType == LAYOUT_ONE) {

            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_orders_layout, parent, false);
            return new mainactiv_allorders.FoodViewHolder(view);

        }
         else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_empty, parent, false);
            return new mainactiv_allorders.FoodViewHolder(view);

        }

    }

    @Override
    public int getItemViewType(int position){

        album_allorders model = getItem(position);

        try {
            switch (CheckFilters(model.getStatus(),model.getOrderNo())) {
                case 0:

                    return LAYOUT_ONE;
                case 1:

                     return LAYOUT_TWO;


            }
        }catch (Exception ignored){}
        return position;

    }



    private int CheckFilters(String Status,String Orderno){

         if (!OrderFilter.isEmpty()){
            if (OrderFilter.contains(Status))
            {   if (Orderno.contains(SearchKey))
                return 0;
            }

         } else { if (Orderno.contains(SearchKey))
                 return 0;

         }

        return 1;
    }

    @Override
    protected void populateViewHolder(final mainactiv_allorders.FoodViewHolder viewHolder, final album_allorders model, final int position) {
    progressBar.setVisibility(View.GONE);
    try {


        viewHolder.setOrderNo(model.getOrderNo());
        viewHolder.setTime(model.getDelivery());

        viewHolder.setStatus(model.getStatus());

        viewHolder.setPayment(context.getString(R.string.rupeesSymbol) + " " + model.getPayment());
        try {
            viewHolder.setPayment2(String.valueOf((Integer.parseInt(model.getTotalAmount()) - Integer.parseInt(model.getPayment()))), context);
        } catch (Exception ignored) {
        }
        try {
            //    viewHolder.setMore(String.valueOf((Integer.parseInt(model.getTotalFood()) - 4)));
        } catch (Exception ignored) {
        }
        try {
            viewHolder.setMore(String.valueOf(model.getTotalFood()));
        } catch (Exception ignored) {
        }
        viewHolder.setImage0(model.getImage0(), context);
        viewHolder.setImage1(model.getImage1(), context);
        viewHolder.setImage2(model.getImage2(), context);
        viewHolder.setImage3(model.getImage3(), context);

       if (!checkCredentials(model.getUserId(),viewHolder.Username(), viewHolder.UserMobile(),viewHolder.UserIMG())) {
           try {
               Load_User_Credentials(viewHolder.Username(), viewHolder.UserMobile(),
                       viewHolder.UserIMG(), FirebaseDatabase.getInstance().
                               getReference("User Informations").child(model.getUserId()), model.getUserId());
           } catch (Exception ignored) {
           }
       }
            viewHolder.Relative().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, editOrder.class);

                    intent.putExtra("OrderNo", model.getOrderNo());
                    intent.putExtra("Uid", model.getUserId());
                    intent.putExtra("Address", canteen);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    context.startActivity(intent);
                }
            });

    }
        catch(Exception e){

        }


    }

    private boolean checkCredentials(String uid,TextView name,TextView mobile,ImageView img) {
       if (credentials.containsKey(uid)){
           String cr=credentials.get(uid);
           name.setText(cr.substring(0,cr.indexOf("AND")));
           mobile.setText(cr.substring(cr.indexOf("AND")+3,cr.indexOf("IMG")));
          Log.e("IMG","From CheckCred <"+cr.substring((cr.indexOf("IMG"))+3)+">");
           Glide.with(context).applyDefaultRequestOptions(RequestOptions.circleCropTransform())
                   .load((cr.substring((cr.indexOf("IMG"))+3))).into(img);

           return true;
       }
       return false;
    }

    private void Load_User_Credentials(final TextView username, final TextView Mobile, final ImageView img, DatabaseReference databaseReference, final String uid)
    {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                username.setText((String) dataSnapshot.child("Name").getValue());
                Mobile.setText((String) dataSnapshot.child("Mobile_number").getValue());
                try {String IMG= (String) dataSnapshot.child("User_Img").getValue();

                    credentials.put(uid,  dataSnapshot.child("Name").getValue()+"AND"+
                            dataSnapshot.child("Mobile_number").getValue()+"IMG"+IMG);
                  //  if (IMG!=null)
                        Glide.with(context).applyDefaultRequestOptions
                                (RequestOptions.circleCropTransform()).load(IMG).into(img);
                }catch (Exception ignored){}
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    public Context getContext() {
        return context;

    }
}
