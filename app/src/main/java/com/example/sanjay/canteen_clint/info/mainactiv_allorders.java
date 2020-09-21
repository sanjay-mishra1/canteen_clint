package com.example.sanjay.canteen_clint.info;


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
import com.example.sanjay.canteen_clint.R;
import com.example.sanjay.canteen_clint.album_allorders;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class mainactiv_allorders  {

    public static class FoodViewHolder extends RecyclerView.ViewHolder{
        View mView;

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
        // String F_food[]={model.getFood0(),model.getFood1(),model.getFood2(),model.getFood3(),model.getFood4(),model.getFood5(),model.getFood6()};
        public void setmView(View mView) {
            this.mView = mView;
        }

         public ImageView setImage (String img, Context ctx )throws NullPointerException {
            ImageView food_img = (ImageView) mView.findViewById(R.id.img1);
            if (img!=null) {

                food_img.setVisibility(View.VISIBLE);
                Glide.with(ctx).applyDefaultRequestOptions(RequestOptions.circleCropTransform()).load(img).into(food_img);

            }
            return food_img;
        }
         public TextView Username(){
            return (TextView) mView.findViewById(R.id.UserName);
        }
            public void setStatus(String status) {
            Status = status;
            int drawable = R.drawable.statusbackground;
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
            }catch (Exception ignored){}
            TextView food_name=(TextView) mView.findViewById(R.id.Orderstatus1);
            food_name.setBackgroundResource(drawable);

            food_name.setText( Status  );
        }

         public void setTime(String time) {
            Time = time;

            TextView food_name=(TextView) mView.findViewById(R.id.deliverytime);
            food_name.setText( Time  );
        }
        public void setTime1(String time) {
            try {
                Time = time;

                TextView food_name = (TextView) mView.findViewById(R.id.usertime);
                food_name.setText(Time);
            }catch (Exception ignored){}
        }


         public void setDelivery(String delivery) {
            try {
                Delivery = delivery;
                TextView food_name = (TextView) mView.findViewById(R.id.deliverytime);
                food_name.setText(Delivery);
            }catch (Exception ignored){}
        }

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

        public void setMessage(String Message){
            try {

                TextView textView=mView.findViewById(R.id.text_message_body);
                if (!Message.equals(""))
                {  textView.setText(Message);
                    textView.setVisibility(View.VISIBLE);
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


        public void setUserImage(String img ) {
            try{
               // if (img!=null)
                    Glide.with(itemView.getContext()).load(img).apply(RequestOptions.circleCropTransform()).into((ImageView) mView.findViewById(R.id.image_message_profile));

            }catch (Exception e){
                try {


                    Glide.with(itemView.getContext()).load(R.drawable.user).into((ImageView) mView.findViewById(R.id.image_message_profile));
                }catch (Exception ignored){}
            }
        }
        public ImageView getImageId(){
            return (ImageView) mView.findViewById(R.id.image_message_profile);
        }
        public void setUsername(String username) {
            try{
                TextView textView=mView.findViewById(R.id.text_message_name);
                textView.setText(username);
            }catch (Exception ignored){}
        }
    }



}




