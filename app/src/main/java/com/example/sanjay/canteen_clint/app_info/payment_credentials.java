package com.example.sanjay.canteen_clint.app_info;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.sanjay.canteen_clint.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class payment_credentials extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_credentials);
        loadData();
    }
    void loadData(){
        FirebaseDatabase.getInstance().getReference("Extras/Extra_Payment_Credentials").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {   TextView textView=findViewById(R.id.otherTextview);
                    textView.setText(String.format("OtherPayment\n %s", dataSnapshot.child("Other").getValue()));
                    textView=findViewById(R.id.TaxTextview);
                    textView.setText(String.format("TAX\n %s", dataSnapshot.child("Tax").getValue()));

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void tax_Clicked(View view) {
        findViewById(R.id.edit2).setVisibility(View.VISIBLE);
        findViewById(R.id.uploadButton).setVisibility(View.VISIBLE);
    }

    public void other_clicked(View view) {
        findViewById(R.id.edit1).setVisibility(View.VISIBLE);
        findViewById(R.id.uploadButton).setVisibility(View.VISIBLE);
    }

    public void uploadClicked(View view) {
        TextInputEditText tax=findViewById(R.id.tax);
        TextInputEditText other=findViewById(R.id.OtherPayment);
         if (!tax.getText().toString().trim().isEmpty()){
            FirebaseDatabase.getInstance().getReference("Extras/Extra_Payment_Credentials").child("Tax").setValue(tax.getText().toString().trim());
        }
        if (!other.getText().toString().trim().isEmpty()){
            FirebaseDatabase.getInstance().getReference("Extras/Extra_Payment_Credentials").child("Other").setValue(other.getText().toString().trim());
        }
         finish();
    }
}
