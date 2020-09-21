package com.example.sanjay.canteen_clint;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Objects;

import static com.example.sanjay.canteen_clint.Home.MYPREFERENCES;

public class NewCanteen extends AppCompatActivity {
    private long collegeId;
    Uri uriProfileImage;
    ProgressBar progressBar;
    private   int CHOOSE_IMAGE = 101;

    String profileImageUrl="";
     @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newcanteen);
        collegeId=System.currentTimeMillis();
         progressBar=findViewById(R.id.progressbar);

     }
    private void uploadImageToFirebaseStorage() {
         StorageReference profileImageRef =
                 FirebaseStorage.getInstance().getReference("CollegeImage/"+collegeId );

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
                            Toast.makeText( NewCanteen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ImageView imageView=findViewById(R.id.uploadButton);
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

    private void showImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select image"), CHOOSE_IMAGE);
    }

    public void saveButtonClicked(View view) {
        save();
    }

    private void save() {
         if (profileImageUrl.isEmpty()){
             Toast.makeText(this,"Insert the college image",Toast.LENGTH_SHORT).show();
             return;
         }
        TextInputEditText canteen=findViewById(R.id.CanteenName);
         if (canteen.getText().toString().trim().isEmpty()){
             canteen.setError("Invalid canteen name");
             canteen.requestFocus();
             return;
         }

        TextInputEditText UserName=findViewById(R.id.UserName);
        if (UserName.getText().toString().trim().isEmpty()){
            UserName.setError("Invalid User name");
            UserName.requestFocus();
            return;
        }

        TextInputEditText Email=findViewById(R.id.Email);
        if (Email.getText().toString().trim().isEmpty()|| !Patterns.EMAIL_ADDRESS.matcher(Email.getText().toString().trim()).matches()){
            Email.setError("Invalid Email address");
            Email.requestFocus();
            return;
        }

        TextInputEditText editTextPassword=findViewById(R.id.editTextPassword);
        if (editTextPassword.getText().toString().trim().isEmpty()||(editTextPassword.getText().toString().trim().length())<6){
            editTextPassword.setError("Invalid Password");
            editTextPassword.requestFocus();
            return;
        }
        TextInputEditText editTextPassword2=findViewById(R.id.editTextPassword2);
        if (editTextPassword2.getText().toString().trim().isEmpty()){
            editTextPassword2.setError("Invalid Password");
            editTextPassword2.requestFocus();
            return;
        }
        if (!Objects.equals(editTextPassword.getText().toString().trim(), editTextPassword2.getText().toString().trim())) {
            editTextPassword2.setError("Password doesn't match");
            editTextPassword2.requestFocus();
            return ;
        }
         CreateAccount(Email.getText().toString().trim(),editTextPassword.getText().toString().trim(),
                UserName.getText().toString().trim(),canteen.getText().toString().trim());
    }

    private void CreateAccount(final String email, String pwd, final String username, final String canteen) {
        findViewById(R.id.progress).setVisibility(View.VISIBLE);

        final FirebaseAuth mAuth=FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    String Uid= Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                   mAuth.signOut();
                   SharedPreferences shared;
                    shared= getSharedPreferences(MYPREFERENCES, Context.MODE_PRIVATE);
                     String ema=shared.getString("Email","");
                    String pass=shared.getString("Password","");

                    automaticLogin(ema,pass,email,mAuth,Uid,username,canteen);

                } else {

                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(getApplicationContext(), "You are already registered", Toast.LENGTH_SHORT).show();
                        findViewById(R.id.progress).setVisibility(View.GONE);
                    } else {
                        findViewById(R.id.progress).setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

    }
    void automaticLogin(final String em, final String pwd, final String email, FirebaseAuth mAuth, final String Uid, final String username, final String canteen){
        mAuth.signInWithEmailAndPassword(em, pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                 if (task.isSuccessful()) {

                    storeData(email,username,canteen,Uid);

                } else {
                     findViewById(R.id.progress).setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void storeData(String email, String username, String canteen, String uid) {
        String key=canteen;
        if (!key .isEmpty()) {
            String[] words = key.split("\\s");
            key="";
            for (String w : words) {
                key = key + String.valueOf(w.charAt(0)).toUpperCase() + w.substring(1).toLowerCase() + " ";
                Log.e("Searching","Data Original <"+w+"> modified <"+key+">");
            }
        canteen=key.trim();
        }

 /*  Storedata storedata=      new Storedata(canteen,email,username,String.valueOf(collegeId),uid,
           profileImageUrl, DateFormat.getDateTimeInstance().format(new Date()));
*/
         DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Canteen/"+canteen);
    /*    databaseReference.setValue(storedata).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                findViewById(R.id.progress).setVisibility(View.GONE);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(NewCanteen.this,e.getMessage(),Toast.LENGTH_LONG).show();
                findViewById(R.id.progress).setVisibility(View.GONE);
            }
        });*/
          databaseReference.child("Canteen").setValue(canteen);
        databaseReference.child("Email").setValue(email);
        databaseReference.child("User_name").setValue(username);
        databaseReference.child("UId").setValue(String.valueOf(collegeId));
        databaseReference.child("UserId").setValue(uid);
        databaseReference.child("CanteenImage").setValue(profileImageUrl);
        databaseReference.child("Date").setValue(DateFormat.getDateTimeInstance().format(new Date())).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                findViewById(R.id.progress).setVisibility(View.GONE);
                finish();
            }
        });

    }

    public void uplodadImageClicked(View view) {
        showImageChooser();

    }

   public    class Storedata {
        public    String Canteen;public String Email;public String User_name;public String UId;
        public String UserId;public String CanteenImage;public String Date;

         public Storedata(String Canteen, String Email, String User_name, String UId, String UserId, String CanteenImage,String Date) {
             this. Canteen = Canteen;
             this. Email =Email;
             this. User_name = User_name;
            this.UId = UId;
             this.   UserId = UserId;
             this.CanteenImage = CanteenImage;
            this.Date=Date;
            Log.e("IDs are ","Present id is "+FirebaseAuth.getInstance().getCurrentUser().getUid()+"   user id is "+UserId);
        }
    }
}
