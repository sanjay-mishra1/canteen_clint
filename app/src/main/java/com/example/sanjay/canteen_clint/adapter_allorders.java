package com.example.sanjay.canteen_clint;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

public class adapter_allorders extends RecyclerView.Adapter<adapter_allorders.MyViewHolder> {
    public int a;
    private Context mContext;
   // private List<album2_allorders> albumList;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, count;
        public ImageView thumbnail, overflow;

        public MyViewHolder(View view) {
            super(view);
        }
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_allorders, parent, false);

        return new MyViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
    //    final album2_allorders album = albumList.get(position);
      //  holder.title.setText(album.getName());
        //holder.count.setText(album.getNumOfSongs());

        // loading album cover using Glide library
        //Glide.with(mContext).load(album.getThumbnail()).into(holder.thumbnail);
        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(),"Image Working",Toast.LENGTH_SHORT).show();
                // holder.thumbnail.set(R.drawable.);
               // Glide.with(mContext).load(R.drawable.arrow).into(holder.thumbnail);

            }
        });
        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext,"Working"+holder.title.getText().toString().trim(), Toast.LENGTH_LONG).show();
            }
        });

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //image(view);
                showPopupMenu(holder.overflow);
                // showPopupMenu(view);




            }
        });
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    /**
     * Showing popup menu when tapping on 3 dots
     */
    private void showPopupMenu(View view) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        //inflater.inflate(R.menu.menu_album, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
        popup.show();
    }

    /**
     * Click listener for popup menu items
     */
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        public MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
             //   case R.id.action_add_favourite:
                  //  Toast.makeText(mContext, "Add to favourite", Toast.LENGTH_SHORT).show();
                 //   return true;
               // case R.id.action_play_next:
                 //   Toast.makeText(mContext, "Play next", Toast.LENGTH_SHORT).show();
                   // return true;
                default:
            }
            return false;
        }
    }

    //@Override
   // public int getItemCount() {
      //  return albumList.size();
    //}
}
