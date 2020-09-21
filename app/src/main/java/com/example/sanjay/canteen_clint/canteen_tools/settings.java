package com.example.sanjay.canteen_clint.canteen_tools;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.sanjay.canteen_clint.R;
import com.example.sanjay.canteen_clint.search.allOrders;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


import java.io.IOException;

import static com.example.sanjay.canteen_clint.Home.MYPREFERENCES;
import static com.example.sanjay.canteen_clint.search.pendingorders.expand;
import static com.example.sanjay.canteen_clint.search.pendingorders.collapse;

public class settings extends AppCompatActivity {
     String canteen;
    private SharedPreferences shared;
    private   final int CHOOSE_IMAGE = 101;
    Uri uriProfileImage;
    ProgressBar progressBar;

    String profileImageUrl="";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        shared=this.getSharedPreferences(MYPREFERENCES, Context.MODE_PRIVATE);
        progressBar=findViewById(R.id.progressbar);
        loadUserData();
    }
    void loadUserData(){

        FirebaseAuth auth= FirebaseAuth.getInstance();
        final String email = auth.getCurrentUser().getEmail();
          canteen=shared.getString("Canteen","not available");
        String canteenImage=shared.getString("CanteenImage", "");
        String Name=shared.getString("Name","not available");
        setTitle(canteen);

        String UserImage=shared.getString("UserImage","");
        if (Name.equalsIgnoreCase("admin")){
            Glide.with(getApplicationContext()).applyDefaultRequestOptions
                    (RequestOptions.circleCropTransform()).load(getResources().getDrawable(R.drawable.admin)).
                    into((ImageView)  findViewById(R.id.UserImage));
            Picasso.get().load(R.drawable.completedstatus_background).into(
                    ((ImageView) findViewById(R.id.canteenimage)));
        }else {
            if (UserImage.isEmpty()){
                Glide.with(getApplicationContext()).load(getResources().getDrawable(R.drawable.canteen)).
                        into((ImageView)  findViewById(R.id.UserImage));

            }else
            Glide.with(getApplicationContext()).applyDefaultRequestOptions
                    (RequestOptions.circleCropTransform()).load(UserImage).
                    into((ImageView)  findViewById(R.id.UserImage));

            Glide.with(getApplicationContext()).applyDefaultRequestOptions(RequestOptions.noTransformation())
                    .load(canteenImage).into((ImageView)  findViewById(R.id.canteenimage));
     //       Picasso.get().load(canteenImage).into
       //             ((ImageView)  findViewById(R.id.canteenimage));
        }
           setData(R.id.username_textview,Name);
          setData(R.id.email_textview,email);
          loadFromDatabase(canteen);
           }

           void loadFromDatabase(String name){
    FirebaseDatabase.getInstance().getReference("Canteen/"+name).addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            String cred=(String) dataSnapshot.child("PaytmNumber").getValue();
            if (cred==null){
                setData(R.id.paytm_textview,"PaytmNumber"+"\n"+"Empty");
            }else
            setData(R.id.paytm_textview,"PaytmNumber"+"\n"+cred);
            cred=(String) dataSnapshot.child("Bank").getValue();
            if (cred==null){
                setData(R.id.bank_Textview,"Bank"+"\n"+"empty");

            }else
            setData(R.id.bank_Textview,"Bank"+"\n"+cred);

            cred=(String)dataSnapshot.child("IFSC").getValue();
           if (cred==null)
               setData(R.id.ifsc_textview,"IFSC code"+"\n"+"empty");
          else  setData(R.id.ifsc_textview,"IFSC code"+"\n"+ cred);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    });


           }
    void setData(int id,String data){
        TextView textView=findViewById(id);
        textView.setText(data);
    }
    public void uplodadClicked(View view) {
        TextInputEditText username=findViewById(R.id.username);
        TextInputEditText paytm=findViewById(R.id.paytm);
        TextInputEditText bank=findViewById(R.id.bank);
        TextInputEditText ifsc=findViewById(R.id.ifsc);
//         if (!profileImageUrl.isEmpty()){
//             FirebaseDatabase.getInstance().getReference("Canteen/"+canteen).child("User_Img").
//                     setValue(profileImageUrl);
//             SharedPreferences.Editor editor=shared.edit();
//             editor.putString("UserImage",profileImageUrl);
//             editor.apply();
//          }
         if (!username.getText().toString().trim().isEmpty()){
             SharedPreferences.Editor editor = shared.edit();
             editor.putString("Name", username.getText().toString().trim());
                editor.apply();
             FirebaseDatabase.getInstance().getReference("Canteen/"+canteen).child("User_name").
                    setValue(username.getText().toString().trim());
        }
        if (!paytm.getText().toString().trim().isEmpty()){
            FirebaseDatabase.getInstance().getReference("Canteen/"+canteen).child("PaytmNumber").
                    setValue(paytm.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.e("Credentials","Data stored");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("Credentials","Data failed "+e.getMessage());
                }
            });
        }
        if (!bank.getText().toString().trim().isEmpty()){
            FirebaseDatabase.getInstance().getReference("Canteen/"+canteen).child("Bank").setValue(bank.getText().toString().trim());
        }
        if (!ifsc.getText().toString().trim().isEmpty()){
            FirebaseDatabase.getInstance().getReference("Canteen/"+canteen).child("IFSC").setValue(ifsc.getText().toString().trim());
        }


         finish();
    }

    public void callbackClicked(View view) {//username

        findViewById(R.id.uploadButton).setVisibility(View.VISIBLE);
        if (findViewById(R.id.edit1).getVisibility()==View.VISIBLE)
             collapse(findViewById(R.id.edit1),300,0);
        else
        expand(findViewById(R.id.edit1),300,120);

    }

    public void channel_clicked(View view) {//bank

        findViewById(R.id.uploadButton).setVisibility(View.VISIBLE);
        if (findViewById(R.id.edit2).getVisibility()==View.VISIBLE)
            collapse(findViewById(R.id.edit2),300,0);
        else
            expand(findViewById(R.id.edit2),300,120);

    }

    public void mid_Clicked(View view) {//ifsc
         findViewById(R.id.uploadButton).setVisibility(View.VISIBLE);
        if (findViewById(R.id.edit3).getVisibility()==View.VISIBLE)
            collapse(findViewById(R.id.edit3),300,0);
        else
            expand(findViewById(R.id.edit3),300,120);

    }

    public void industry_clicked(View view) {//paytm

        findViewById(R.id.uploadButton).setVisibility(View.VISIBLE);
        if (findViewById(R.id.edit5).getVisibility()==View.VISIBLE)
            collapse(findViewById(R.id.edit5),300,0);
        else
            expand(findViewById(R.id.edit5),300,120);

    }

    public void UploadUserImageClicked(View view) {
     showImageChooser();
    }
    private void showImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Food Item Image"), CHOOSE_IMAGE);
    }
    private void uploadImageToFirebaseStorage() {
        //TextView textView=findViewById(R.id.foodname);
        StorageReference profileImageRef =
                //FirebaseStorage.getInstance().getReference("food/"+editText.toString().trim() + System.currentTimeMillis() + ".jpg");
                FirebaseStorage.getInstance().getReference("CanteenUser/"+FirebaseAuth.getInstance().getUid());

        if (uriProfileImage != null) {
            progressBar.setVisibility(View.VISIBLE);
            profileImageRef.putFile(uriProfileImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressBar.setVisibility(View.GONE);
                            profileImageUrl = taskSnapshot.getDownloadUrl().toString();
                            FirebaseDatabase.getInstance().getReference("Canteen/"+canteen).child("User_Img").
                                    setValue(profileImageUrl);
                            SharedPreferences.Editor editor=shared.edit();
                            editor.putString("UserImage",profileImageUrl);
                            editor.apply();
                            findViewById(R.id.uploadButton).setVisibility(View.VISIBLE);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText( settings.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ImageView imageView=findViewById(R.id.UserImage);
        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriProfileImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriProfileImage);
                Glide.with(getApplicationContext()).applyDefaultRequestOptions(RequestOptions.circleCropTransform())
                        .load(bitmap).into(imageView);
               // imageView.setImageBitmap(bitmap);
                uploadImageToFirebaseStorage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
