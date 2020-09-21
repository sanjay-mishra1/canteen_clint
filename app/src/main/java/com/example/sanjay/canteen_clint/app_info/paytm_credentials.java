package com.example.sanjay.canteen_clint.app_info;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.sanjay.canteen_clint.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class paytm_credentials extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.paytm_credentials);
        loadData();
    }
    void loadData(){
        FirebaseDatabase.getInstance().getReference("Extras/Extra_Payment_Credentials/Paytm").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {   TextView textView=findViewById(R.id.callback_textview);
                    textView.setText(String.format("CallBack\n %s", dataSnapshot.child("Callback").getValue()));
                    textView=findViewById(R.id.mid_textview);
                    textView.setText(String.format("MID\n %s", dataSnapshot.child("Mid").getValue()));
                    textView=findViewById(R.id.website_textview);
                    textView.setText(String.format("Website\n %s", dataSnapshot.child("WEBSITE").getValue()));
                    textView=findViewById(R.id.channel_Textview);
                    textView.setText(String.format("ChannelId\n %s", dataSnapshot.child("ChannelId").getValue()));
                    textView=findViewById(R.id.industry_textview);
                    textView.setText(String.format("Industry\n %s", dataSnapshot.child("Industry").getValue()));
                    RadioButton live=findViewById(R.id.live),stage=findViewById(R.id.stage);
                    if ((boolean)dataSnapshot.child("Type").getValue())
                    stage.setChecked(true);
                    else
                        live.setChecked(true);
                    ListnerRadioGroup();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void ListnerRadioGroup(){
        RadioGroup radioGroup=findViewById(R.id.radiogroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                findViewById(R.id.uploadButton).setVisibility(View.VISIBLE);
            }
        });
    }
    public void uplodadClicked(View view) {
        TextInputEditText channel=findViewById(R.id.CHANNEL);
        TextInputEditText mid=findViewById(R.id.MID);
        TextInputEditText website=findViewById(R.id.WEBSITE1);
        TextInputEditText callback=findViewById(R.id.CALLBACK);
        TextInputEditText industry=findViewById(R.id.INDUSTRY);
        if (!channel.getText().toString().trim().isEmpty()){
            FirebaseDatabase.getInstance().getReference("Extras/Extra_Payment_Credentials/Paytm").child("ChannelId").setValue(channel.getText().toString().trim());
        }
        if (!mid.getText().toString().trim().isEmpty()){
            FirebaseDatabase.getInstance().getReference("Extras/Extra_Payment_Credentials/Paytm").child("Mid").setValue(mid.getText().toString().trim());
        }
        if (!website.getText().toString().trim().isEmpty()){
            FirebaseDatabase.getInstance().getReference("Extras/Extra_Payment_Credentials/Paytm").child("WEBSITE").setValue(website.getText().toString().trim());
        }
        if (!callback.getText().toString().trim().isEmpty()){
            FirebaseDatabase.getInstance().getReference("Extras/Extra_Payment_Credentials/Paytm").child("Callback").setValue(callback.getText().toString().trim());
        }
        if (!industry.getText().toString().trim().isEmpty()){
            FirebaseDatabase.getInstance().getReference("Extras/Extra_Payment_Credentials/Paytm").child("Industry").setValue(industry.getText().toString().trim());
        }
        RadioButton live=findViewById(R.id.live);

        if (live.isChecked()){
            FirebaseDatabase.getInstance().getReference("Extras/Extra_Payment_Credentials/Paytm").child("Type").setValue(false);
        }else
            FirebaseDatabase.getInstance().getReference("Extras/Extra_Payment_Credentials/Paytm").child("Type").setValue(true);
        finish();
    }

    public void callbackClicked(View view) {
        findViewById(R.id.edit1).setVisibility(View.VISIBLE);
        findViewById(R.id.uploadButton).setVisibility(View.VISIBLE);
    }

    public void channel_clicked(View view) {
        findViewById(R.id.edit2).setVisibility(View.VISIBLE);
        findViewById(R.id.uploadButton).setVisibility(View.VISIBLE);
    }

    public void mid_Clicked(View view) {
        findViewById(R.id.edit3).setVisibility(View.VISIBLE);
        findViewById(R.id.uploadButton).setVisibility(View.VISIBLE);
    }

    public void industry_clicked(View view) {
        findViewById(R.id.edit5).setVisibility(View.VISIBLE);
        findViewById(R.id.uploadButton).setVisibility(View.VISIBLE);
    }

    public void website_Clicked(View view) {
        findViewById(R.id.edit4).setVisibility(View.VISIBLE);
        findViewById(R.id.uploadButton).setVisibility(View.VISIBLE);
    }
}


