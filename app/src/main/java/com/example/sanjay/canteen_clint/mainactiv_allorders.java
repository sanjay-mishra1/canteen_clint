package com.example.sanjay.canteen_clint;


import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
//import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.RequestOptions;
import com.example.sanjay.canteen_clint.newChatScreen.help_activity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class mainactiv_allorders extends AppCompatActivity  implements View.OnClickListener{
         public Button cancel1,Edit1;

    private RecyclerView mFoodList;
    public DatabaseReference mDatabase;
    FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthlistener;
    private CheckBox checkBox;
    private Button add_food;
    Button  menu_button;
    private int count;
    String food0,food1,food2,food3,food4,food5,food6;
    String Total_foods;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatabase= FirebaseDatabase.getInstance().getReference().child("Food Items");
      }


    @Override
    protected void onStart() {
        super.onStart();

        //mAuth.addAuthStateListener(mAuthlistener);
        FirebaseRecyclerAdapter<album_allorders, FoodViewHolder> FBRA = new FirebaseRecyclerAdapter<album_allorders, FoodViewHolder>(
                album_allorders.class,
                R.layout.recycler_allorders,
                FoodViewHolder.class,
                mDatabase


        ) {
            @Override
            protected void populateViewHolder(FoodViewHolder viewHolder, album_allorders model, int position) {
              //   viewHolder.setDelivery(model.getDelivery());
                viewHolder.setOrderNo(model.getOrderNo());
                viewHolder.setStatus(model.getStatus());
                viewHolder.setTime(model.getTime());
                viewHolder.setTotal_Amount(model.getTotalAmount());
                viewHolder.setPayment(model.getPayment());
                viewHolder.setTotal_Food(model.getTotalFood());
                final String food_key = getRef(position).getKey().toString();
                Total_foods=model.getTotalFood();
            }
        };
     }

        @Override
    public void onClick(View view) {
        // if(view==add_food){
        //if(count==1)
        {
            //addfood();
            //   Picasso.get().load("drawable/tick.jpg").into(add_food);
            //      Toast.makeText(this,"Adding Food",Toast.LENGTH_SHORT).show();

            //add_food.setImageResource(R.drawable.tick);
            // count++;
        }
         /*   if(count==2){
                //Picasso.get().load("drawable/add_icon.jpg").into(add_food);
                //add_food.setImageResource();
                add_food.setImageResource(R.drawable.add_icon);

               // removefood();
                count=1;
            }*/
//            count++;
        //          Toast.makeText(this,"Adding Food",Toast.LENGTH_SHORT).show();
        //    }
        if(view==menu_button){
            Toast.makeText(this,"Adding Menu ",Toast.LENGTH_SHORT).show();

        }   }




    public static class FoodViewHolder extends RecyclerView.ViewHolder{
        public View mView;

        public FoodViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        private String Payment;
        private String Status;
        private String Time;
        private String Total_Amount="Total Amount";
        private String Total_Food="Total Food";
        private String Delivery;
        private String OrderNo;
        private CardView mainLinear;
        album_allorders model;
        public void setmView(View mView) {
            this.mView = mView;
        }
/**/

        public void setPayment(String payment) {
            Payment = payment;

            TextView food_name=(TextView) mView.findViewById(R.id.PaymentStatus1);
            food_name.setText( Payment  );
        }
        public void setPayment3(String payment) {
            Payment = payment;

            TextView food_name=(TextView) mView.findViewById(R.id.pay2);
            if (payment.contains("-")){
                food_name.setTextSize(14);
                food_name.setText(String.format("Get ₹%s from canteen   ",
                        String.valueOf(Integer.parseInt(Payment) * (-1))));
            }else
                food_name.setText(String.format(" ₹%s  ", Payment));
        }
        public void setPayment2(String payment,Context context) {
            Payment = payment;
            TextView food_name=(TextView) mView.findViewById(R.id.pay2);
            if (Integer.parseInt(payment)<0){
                food_name.setTextSize(10);
                food_name.setText(String.format("Pay extra %s %d", context.getString(R.string.rupeesSymbol), Integer.parseInt(payment) * -1));
            }else

            food_name.setText(String.format("%s %s", context.getString(R.string.rupeesSymbol), Payment));
        }
        public void setMode(String mode){
            TextView food_name=(TextView) mView.findViewById(R.id.pay2);
            food_name.setText( mode  );
        }
        public void setMore(String payment) {
           if (Integer.parseInt(payment)>=1) {
               Payment = payment;

               TextView food_name = (TextView) mView.findViewById(R.id.more);
               food_name.setVisibility(View.VISIBLE);
               food_name.setText(String.format("+ %s more", Payment));
           }
        }

        public void setTotalTransactions(String Transactions){
            TextView t=(TextView) mView.findViewById(R.id.totalTrasaction);
            t.setText(Transactions);
        }
        public ImageView setImage (String img, Context ctx )throws NullPointerException {
            ImageView food_img = (ImageView) mView.findViewById(R.id.img1);
            if (img!=null) {

                food_img.setVisibility(View.VISIBLE);
                Glide.with(ctx).applyDefaultRequestOptions(RequestOptions.circleCropTransform()).load(img).into(food_img);

             }
             return food_img;
        }
        public ImageView setImage6 (String img, Context ctx )throws NullPointerException {
            ImageView food_img = (ImageView) mView.findViewById(R.id.img1);
            if (img!=null) {

                food_img.setVisibility(View.VISIBLE);
                Glide.with(ctx).applyDefaultRequestOptions(RequestOptions.noTransformation()).load(img).into(food_img);

            }
            return food_img;
        }
        public void setImage0(String img, Context ctx ) {
            if (img!=null) {
                ImageView food_img = (ImageView) mView.findViewById(R.id.img1);
            food_img.setVisibility(View.VISIBLE);

            Glide.with(ctx).load(img).apply(RequestOptions.circleCropTransform()).into(food_img);
        }
        }
        public void setImage1(String img,Context ctx ) {
            if (img!=null) {

                ImageView food_img = (ImageView) mView.findViewById(R.id.img2);
            food_img.setVisibility(View.VISIBLE);
              //  Glide.with(ctx).load(img).into(food_img);
                 Glide.with(mView).load(img).apply(RequestOptions.circleCropTransform()).into(food_img);
        }
        }
        public void setImage2(String img,Context ctx) {
            if (img!=null) {

                ImageView food_img=(ImageView) mView.findViewById(R.id.img3);
            food_img.setVisibility(View.VISIBLE);
                //Glide.with(ctx).load(img).into(food_img);

                 Glide.with(ctx).load(img).apply(RequestOptions.circleCropTransform()).into(food_img);

        }}
        public void setImage3(String img,Context ctx) {
            if (img!=null) {
                ImageView food_img = (ImageView) mView.findViewById(R.id.img4);
                food_img.setVisibility(View.VISIBLE);
                //Glide.with(ctx).load(img).into(food_img);

                  Glide.with(mView).load(img).apply(RequestOptions.circleCropTransform()).into(food_img);
            }
        }
        public TextView Username(){
            return (TextView) mView.findViewById(R.id.UserName);
        }
        public TextView UserMobile(){
            return (TextView) mView.findViewById(R.id.mobile);
        }
        public ImageView UserIMG(){
            return (ImageView) mView.findViewById(R.id.image_message_profile);
        }
    public TextView returnTextview(){
            return mView.findViewById(R.id.count);
    }
    public RelativeLayout relativeLayoutReturn(){
            return mView.findViewById(R.id.PendingRelative);
    }
        public void setStatus(String status) {
            Status = status;
            int drawable = R.drawable.statusbackground;;
try {
    if (status.contains("Accepted")) {
        drawable = R.drawable.acceptedstatus_background;
    } else if (status.contains("Completed")) {
        drawable = R.drawable.completedstatus_background;
    } else if (status.contains("Cancel")) {
        drawable = R.drawable.cancelledstatus_background;
    }else if (status.contains("Delivered")) {
        drawable = R.drawable.deliveredstatus_background;}
    else {
        drawable = R.drawable.statusbackground;
    }
}catch (Exception NullPointerException){}
            TextView food_name=(TextView) mView.findViewById(R.id.Orderstatus1);
            food_name.setBackgroundResource(drawable);

            food_name.setText( Status  );
        }

        public RelativeLayout Relative( ) {

           RelativeLayout r=(RelativeLayout) mView.findViewById(R.id.main);
             return r;
        }
        public void setTime(String time) {
        try {
            Time = time;


            TextView food_name = (TextView) mView.findViewById(R.id.deliverytime);
            food_name.setText(Time);
        }catch (Exception e){}
        }
        public void setTime1(String time) {
            try {
                Time = time;

                TextView food_name = (TextView) mView.findViewById(R.id.usertime);
                food_name.setText(Time);
            }catch (Exception ignored){}
        }

        public void setTotal_Amount(String total_Amount) {
            Total_Amount = total_Amount;
            TextView food_name=(TextView) mView.findViewById(R.id.TotalAmount);
           food_name.setText(String.format("%s%s", itemView.getResources().getString(R.string.rupeesSymbol), Total_Amount));
        }

        public void setTotal_Food(String total_Food) {
            Total_Food = total_Food;

           // TextView food_name=(TextView) mView.findViewById(R.id.tota);
            //food_name.setText(Delivery);
        }
         public void setDelivery(String delivery) {
        try {
            Delivery = delivery;
            TextView food_name = (TextView) mView.findViewById(R.id.deliverytime);
            food_name.setText(Delivery);
        }catch (Exception ignored){}
        }

        public void setDeliveryedit(String delivery) {
            Delivery = delivery;
            EditText food_name=(EditText) mView.findViewById(R.id.deliverytime);
            food_name.setText(Delivery);}
        public void setOrderNo(String orderNo) {
            OrderNo = orderNo;
            TextView food_name=(TextView) mView.findViewById(R.id.orderno1);
            food_name.setText(OrderNo);}
        public void setFood_name(String name) {
            OrderNo = name;
            TextView food_name=(TextView) mView.findViewById(R.id.title);
            food_name.setText(OrderNo);}
          public void setQuantity(String name) {
            OrderNo = name;
            TextView food_name=(TextView) mView.findViewById(R.id.edit_quantity1);
            food_name.setText(OrderNo);
              }
        public void setprice(String name) {
            OrderNo = name;
            TextView food_name=(TextView) mView.findViewById(R.id.count);
            if (name.contains("-"))
                OrderNo=name.replace("-","");
            food_name.setText(OrderNo);}
        public void setrating(String sum_of_ratings, String total_noOfTime_rated) {
            try {
                if (!total_noOfTime_rated .equals("0") && !total_noOfTime_rated.isEmpty()) {
                  //  mView.findViewById(R.id.r).setVisibility(View.VISIBLE);
                 //   TextView textView = mView.findViewById(R.id.rating_star);
                    float rating = Float.parseFloat(sum_of_ratings) / Float.parseFloat(total_noOfTime_rated);
                    RatingBar ratingBar = mView.findViewById(R.id.rating);
                    ratingBar.setRating(rating);
                   // textView.setText(String.format("%.1f", rating));

                } else {
                    mView.findViewById(R.id.rating).setVisibility(View.GONE);
                }
            } catch (Exception e) {
                mView.findViewById(R.id.rating).setVisibility(View.GONE);

            }
        }
        public  void setrating(double rating){
            RatingBar ratingBar=itemView.findViewById(R.id.rating);
            ratingBar.setRating((float) rating);
        }
        public String returndiscount(){
            TextView discounts= mView.findViewById(R.id.discount);
            return discounts.getText().toString().trim();
        }

        public String returnprice(){
            TextView discounts= mView.findViewById(R.id.count);
            return discounts.getText().toString().trim();
        }

        public String returndesc(){
            TextView discounts= mView.findViewById(R.id.description);
            return discounts.getText().toString().trim();
        }
        public void setDiscount(String discount, String price) {
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

        public void setDescription(String description) {
            TextView text=(TextView) mView.findViewById(R.id.description);
            text.setVisibility(View.VISIBLE);
            text.setText(description);
        }

        public TextView setUser_name(String user_name)throws NullPointerException{
            TextView text=(TextView) mView.findViewById(R.id.username);
             text.setText(user_name);
             return text;
        }

        public void setlast_message(String last_message)throws NullPointerException {
            TextView text=(TextView) mView.findViewById(R.id.usermessage);
             text.setText(last_message);
         }

        public ImageView getImageView() {
            return mView.findViewById(R.id.message_body_img);
         }
        public ImageView getImageView1() {
            return mView.findViewById(R.id.img2);
        }

        public void setMessage(String Message){
         try {
             TextView textView=mView.findViewById(R.id.text_message_body);


            try {

                if (!Message.equals(""))
                {  textView.setText(Message);
                    textView.setVisibility(View.VISIBLE);
                }
            }catch (Exception e){
                textView.setText("");
            }
         }catch (Exception ignored){}
        }
        public void setMessage_Time(String time){
            try {
                TextView textView=mView.findViewById(R.id.text_message_time);
                // time=time.substring(time.indexOf("||")+3,time.length());
                textView.setText(time);
            }catch (Exception ignored){}

        }
        public void setImageMessage(Context ctx,  String image) {
            try {
                ImageView food_image=(ImageView) mView.findViewById(R.id.message_body_img);
                //Picasso.with(ctx).load(image).into(food_image);
                // Picasso.get().load(image).into(food_image);
               Glide.with(ctx).load(image).into(food_image);

            }catch (Exception ignored){}

        }
        public void setDate2(String time){
            try {
                TextView textView=mView.findViewById(R.id.date);
                 textView.setText(time);
            }catch (Exception ignored){}

        }

        public void setMessage_counter(String message_counter) {
            TextView textView=mView.findViewById(R.id.Unread_messages);

            try {
               if (!message_counter.isEmpty() ) {
                   if (!message_counter.equals("0")) {
                       textView.setVisibility(View.VISIBLE);
                       textView.setText(message_counter);
                   }
               }
           }catch (Exception ignored){

            }
        }

        public void setUserImage(String img ) {
            try{
                if (img!=null)
                Glide.with(itemView.getContext()).load(img).apply(RequestOptions.circleCropTransform()).into((ImageView) mView.findViewById(R.id.image_message_profile));

            }catch (Exception e){
             try {


                 Glide.with(itemView.getContext()).load(R.drawable.user).into((ImageView) mView.findViewById(R.id.image_message_profile));
             }catch (Exception ignored){}
            }
        }
        public void setIssueImage(String img ) {
            try{
                if (img!=null)
                Glide.with(itemView.getContext()).load(img).
                        apply(RequestOptions.noTransformation()).into((ImageView) mView.findViewById(R.id.image_message_profile));
                else{
                    mView.findViewById(R.id.image_message_profile).setVisibility(View.GONE);
                }
            }catch (Exception e){
             try {


                 Glide.with(itemView.getContext()).load(R.drawable.user).into((ImageView) mView.findViewById(R.id.image_message_profile));
             }catch (Exception ignored){}
            }
        }
        public ImageView giveimageid(){
            return mView.findViewById(R.id.image_message_profile);
        }
        public void setUsername(String username) {
            try{
                TextView textView=mView.findViewById(R.id.text_message_name);
                textView.setText(username);
            }catch (Exception ignored){}
         }
        public void setstatus(boolean status,Context context) {
            if (!status){
                Glide.with(context).applyDefaultRequestOptions(RequestOptions.noAnimation()).applyDefaultRequestOptions(RequestOptions.skipMemoryCacheOf(true))
                            .load(R.drawable.sent).into((ImageView)
                        itemView.findViewById(R.id.status));
            }
        }

        public void setIssueID(String uId) {
            TextView textView=mView.findViewById(R.id.issueid);
            textView.setText(uId);
        }
    }



    }




