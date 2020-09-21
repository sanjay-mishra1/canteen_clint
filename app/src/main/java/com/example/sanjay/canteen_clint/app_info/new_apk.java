package com.example.sanjay.canteen_clint.app_info;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sanjay.canteen_clint.NewFood;
import com.example.sanjay.canteen_clint.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class new_apk extends AppCompatActivity {
    private static final int CHOOSE_APK = 100;
    Uri uriProfileImage;
    ProgressBar progressBar;
     String profileImageUrl="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_apk);
        progressBar=findViewById(R.id.progressbar);
    }
    private void showAPKChooser() {
        Intent intent = new Intent();
        //intent.setType("apk/*");
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select new APK"), CHOOSE_APK);
    }
    private void uploadAPKToFirebaseStorage() {
        StorageReference profileImageRef =
                FirebaseStorage.getInstance().getReference("APK/" +"Mcafe" + ".apk");
         if (uriProfileImage != null) {
            progressBar.setVisibility(View.VISIBLE);
            profileImageRef.putFile(uriProfileImage).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
               double progress=(100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
               int currentprogress=(int) progress;
               progressBar.setProgress(currentprogress);
                }
            })      .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressBar.setVisibility(View.GONE);
                    profileImageUrl = taskSnapshot.getDownloadUrl().toString();
                    finish();
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(new_apk.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
         /*   profileImageRef.putFile(uriProfileImage).ad
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressBar.setVisibility(View.GONE);
                            profileImageUrl = taskSnapshot.getDownloadUrl().toString();
                             finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(new_apk.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });*/
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_APK && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriProfileImage = data.getData();
            try {
                findViewById(R.id.uploadButton).setVisibility(View.VISIBLE);
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriProfileImage);
                ImageView imageView=findViewById(R.id.select_app);
                imageView.setImageBitmap(bitmap);
                //saveuserinformation();
               // uploadImageToFirebaseStorage();
                //       final Uri downloadUrl =taskSnapshot.getDownloadUrl();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void upload_apk_clicked(View view) {
        showAPKChooser();
    }

    public void upload_button_clicked(View view) {
      uploadAPKToFirebaseStorage();
    }
}
