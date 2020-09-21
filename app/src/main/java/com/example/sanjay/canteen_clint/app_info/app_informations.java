package com.example.sanjay.canteen_clint.app_info;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.sanjay.canteen_clint.R;

public class app_informations extends AppCompatActivity implements View.OnClickListener{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_info);
        findViewById(R.id.newApk).setOnClickListener(this);
        findViewById(R.id.apk_info).setOnClickListener(this);
        findViewById(R.id.charges).setOnClickListener(this);
        findViewById(R.id.paytm).setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.newApk:
                startActivity(new Intent(this, new_apk.class));

                break;
            case R.id.apk_info:
                startActivity(new Intent(this, apk_info.class));
                break;
            case R.id.charges:
                startActivity(new Intent(this, payment_credentials.class));

                break;
            case R.id.paytm:
                Intent intent = new Intent(this, paytm_credentials.class);
                startActivity(intent);
                break;

        }
    }
}
