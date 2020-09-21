package com.example.sanjay.canteen_clint.canteen_tools;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.sanjay.canteen_clint.NewFood;
import com.example.sanjay.canteen_clint.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

import static com.example.sanjay.canteen_clint.extra_classes.Constants.Canteen;

public class issue extends AppCompatActivity {
    private   final int CHOOSE_IMAGE = 101;

    Uri uriProfileImage;
    ProgressBar progressBar;

    String profileImageUrl="";
    private long issueid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.issue);
        issueid=System.currentTimeMillis();
        progressBar=findViewById(R.id.progressbar);
        loadUserData();
    }
    void loadUserData(){

        SharedPreferences shared =getSharedPreferences("MyPrefs",MODE_PRIVATE);
        String canteen=shared.getString("Canteen","not available");
        String canteenImage=shared.getString("CanteenImage", "");
        String Name=shared.getString("Name","not available");
        setTitle(canteen);

        String UserImage=shared.getString("UserImage","");
        if (Name.equalsIgnoreCase("admin")){
            Glide.with(getApplicationContext()).applyDefaultRequestOptions
                    (RequestOptions.circleCropTransform()).load(getResources().getDrawable(R.drawable.admin)).
                    into((ImageView)  findViewById(R.id.userimage));

        }else {
            if (UserImage.isEmpty()){
                Glide.with(getApplicationContext()).load(getResources().getDrawable(R.drawable.canteen)).
                        into((ImageView)  findViewById(R.id.userimage));

            }else
                Glide.with(getApplicationContext()).applyDefaultRequestOptions
                        (RequestOptions.circleCropTransform()).load(UserImage).
                        into((ImageView)  findViewById(R.id.userimage));

            Glide.with(getApplicationContext()).applyDefaultRequestOptions(RequestOptions.noTransformation())
                    .load(canteenImage).into((ImageView)  findViewById(R.id.canteenimage));

        }
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
                FirebaseStorage.getInstance().getReference("issue/"+issueid );

        if (uriProfileImage != null) {
            progressBar.setVisibility(View.VISIBLE);
            profileImageRef.putFile(uriProfileImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressBar.setVisibility(View.GONE);
                            profileImageUrl = taskSnapshot.getDownloadUrl().toString();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText( issue.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ImageView imageView=findViewById(R.id.image);
        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriProfileImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriProfileImage);
                imageView.setImageBitmap(bitmap);
                 uploadImageToFirebaseStorage();
             } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void uploadImageClicked(View view) {
        showImageChooser();
    }


    public void SendonClicked(View view) {
        save();
    }

    private void save() {
        EditText issue=findViewById(R.id.issue);
        if (issue.getText().toString().trim().isEmpty()){
            issue.setError("Enter the issue your faced");
            issue.requestFocus();
            return;
        }
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Messages/Issues/"+issueid);
        databaseReference.child("UId").setValue(String.valueOf(issueid));
        databaseReference.child("Canteen").setValue(Canteen);
        databaseReference.child("Date").setValue( DateFormat.getDateTimeInstance().format(new Date()));
        databaseReference.child("Message").setValue(issue.getText().toString().trim());
        if (!profileImageUrl.isEmpty()){
            databaseReference.child("User_Img").setValue(profileImageUrl);

        }
        finish();
    }
}
