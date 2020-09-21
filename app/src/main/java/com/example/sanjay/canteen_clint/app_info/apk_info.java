package com.example.sanjay.canteen_clint.app_info;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.sanjay.canteen_clint.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class apk_info extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.apl_informations);
        loadData();
        ListnerRadioGroup();
    }
    void loadData(){
        FirebaseDatabase.getInstance().getReference("Extras/App").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {   TextView textView=findViewById(R.id.app_version_text);
                    textView.setText(String.format("App Version\n %s", dataSnapshot.child("App_New_Version").getValue()));
                    textView=findViewById(R.id.update_text);
                    textView.setText(String.format("Update Link\n %s", dataSnapshot.child("UPDATE_LINK").getValue()));
                    RadioButton yes=findViewById(R.id.yes),no=findViewById(R.id.no);
                    if (dataSnapshot.child("Force_Update").getValue().toString().trim().toUpperCase().contains("YES")){
                        yes.setChecked(true);
                    }else{
                        no.setChecked(true);
                    }
                findViewById(R.id.uploadButton).setVisibility(View.GONE);
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
    public void uploadClicked(View view) {
        TextInputEditText update=findViewById(R.id.update_edit);
        TextInputEditText version=findViewById(R.id.app_version_edit);
        if (!update.getText().toString().trim().isEmpty()){
            FirebaseDatabase.getInstance().getReference("Extras/App").child("UPDATE_LINK").setValue(update.getText().toString().trim());
        }
        if (!version.getText().toString().trim().isEmpty()){
            FirebaseDatabase.getInstance().getReference("Extras/App").child("App_New_Version").setValue(version.getText().toString().trim());
        }
        RadioButton yes=findViewById(R.id.yes);
        if (yes.isChecked()){
            FirebaseDatabase.getInstance().getReference("Extras/App").child("Force_Update").setValue("YES");
        }else{
            FirebaseDatabase.getInstance().getReference("Extras/App").child("Force_Update").setValue("NO");
        }
        finish();

    }

    public void app_version_clicked(View view) {
        findViewById(R.id.edit1).setVisibility(View.VISIBLE);
        findViewById(R.id.uploadButton).setVisibility(View.VISIBLE);

    }

    public void update_link_clicked(View view) {
        findViewById(R.id.edit2).setVisibility(View.VISIBLE);
        findViewById(R.id.uploadButton).setVisibility(View.VISIBLE);

    }
}
