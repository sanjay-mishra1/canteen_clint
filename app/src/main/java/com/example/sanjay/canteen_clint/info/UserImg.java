package com.example.sanjay.canteen_clint.info;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.sanjay.canteen_clint.R;
import com.squareup.picasso.Picasso;

public class UserImg extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_display_image);
      //  findViewById(R.id.share).setVisibility(View.GONE);
        //findViewById(R.id.Save_into_storage).setVisibility(View.GONE);
        Intent intent=getIntent();
        String img=intent.getStringExtra("USERIMG");
       // Picasso.get().load(img).into((ImageView) findViewById(R.id.message_body_img));
        Glide.with(getApplicationContext()).applyDefaultRequestOptions(RequestOptions.noTransformation()).load(img).into((ImageView) findViewById(R.id.message_body_img));
        ListnerForImage();
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    void ListnerForImage(){
        findViewById(R.id.message_body_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (findViewById(R.id.top_bar).getVisibility()!=View.VISIBLE)
                    findViewById(R.id.top_bar).setVisibility(View.VISIBLE);
                else findViewById(R.id.top_bar).setVisibility(View.INVISIBLE);
            }
        });
    }
}
