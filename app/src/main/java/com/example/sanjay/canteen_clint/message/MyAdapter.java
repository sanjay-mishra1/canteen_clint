package com.example.sanjay.canteen_clint.message;
import android.content.Intent;
 import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sanjay.canteen_clint.R;
import com.example.sanjay.canteen_clint.album_allorders;
import com.example.sanjay.canteen_clint.mainactiv_allorders;
 import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;


public class MyAdapter extends FirebaseRecyclerAdapter<album_allorders,mainactiv_allorders.FoodViewHolder>{
    private static final int LAYOUT_ONE = 1;
    private static final int LAYOUT_TWO = 2;
    private static final int LAYOUT_THREE = 3;
    private static final int LAYOUT_FOUR = 4;
    private static final int LAYOUT_FIVE = 5;

    private String username;
    private String UserImg;
    public MyAdapter(Class<album_allorders> modelClass,
                     int modelLayout,
                     Class<mainactiv_allorders.FoodViewHolder> viewHolderClass,
                     Query ref, String UserName, String img) {

        super(modelClass, modelLayout, viewHolderClass, ref  );
        username=UserName;
        UserImg=img;
     }


    @Override
    public mainactiv_allorders.FoodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;

        if (viewType == LAYOUT_ONE) {

            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sent, parent, false);
            return new mainactiv_allorders.FoodViewHolder(view);

        } else if (viewType == LAYOUT_TWO){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sent_img, parent, false);
            return new mainactiv_allorders.FoodViewHolder(view);
        }
        else if (viewType == LAYOUT_THREE){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_received, parent, false);
            return new mainactiv_allorders.FoodViewHolder(view);
        }
        else if (viewType == LAYOUT_FIVE){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.date_layout, parent, false);
            return new mainactiv_allorders.FoodViewHolder(view);
        }
        else  if(viewType == LAYOUT_FOUR)
        {

            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_received_img, parent, false);
            return new mainactiv_allorders.FoodViewHolder(view);

        } else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_empty, parent, false);
            return new mainactiv_allorders.FoodViewHolder(view);

        }

    }

    @Override
    public int getItemViewType(int position){

        album_allorders model = getItem(position);

        try {
            switch (model.getFrom()) {
                case "ADMIN_TEXT":
                    return LAYOUT_ONE;
                case "ADMIN_IMG":
                    return LAYOUT_TWO;

                case "USER_TEXT":
                    return LAYOUT_THREE;
                case "Date":
                    return LAYOUT_FIVE;
                 case "USER_IMG":
                    return LAYOUT_FOUR;
            }
        }catch (Exception ignored){}
        return position;

    }



    @Override
    protected void populateViewHolder(final mainactiv_allorders.FoodViewHolder viewHolder, final album_allorders model, final int position) {
        try {
            if (model.getFrom().contains("IMG")) {
                viewHolder.getImageView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(viewHolder.itemView.getContext(), Message_Image.class);
                        intent.putExtra("Image", model.getFood_Image());
                        intent.putExtra("Message", model.getMessage());

                        //  animation(position, viewHolder.itemView.getContext(),intent,viewHolder.itemView);
                        viewHolder.itemView.getContext().startActivity(intent);
                    }
                });
            }
        }catch (Exception ignored){
        }

        viewHolder.setMessage(model.getMessage());
         viewHolder.setMessage_Time(model.getTime());
        viewHolder.setImageMessage(viewHolder.itemView.getContext(),model.getFood_Image());
         viewHolder.setUserImage(model.getUser_Img());
        viewHolder.setDate2(model.getDate());
        viewHolder.setUsername(username);
       try {
           viewHolder.setUserImage(UserImg);
       }  catch (Exception ignored){}

    }



}
