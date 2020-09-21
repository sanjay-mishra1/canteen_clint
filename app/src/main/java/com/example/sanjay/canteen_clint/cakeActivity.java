package com.example.sanjay.canteen_clint;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
 import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

 import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
 import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;

import static com.example.sanjay.canteen_clint.extra_classes.Constants.Canteen;

/**
 * Created by sanjay on 03/07/2018.
 */

public class cakeActivity extends AppCompatActivity{
    private static final int CHOOSE_IMAGE = 101;
    private Button save,Reset;
    TextView textView;
    ImageView imageView,imageView1,imageView2,imageView3,imageView4,imageView5,imageView6;
    EditText Username;
    private String Type_Button;
    DatabaseReference databaseReference;
    Uri uriProfileImage;
     String check[];
    private EditText editText,desc,price,type;
    TextView clk_type;
    ArrayList<String> profileImageUrl;
    //String profileImageUrl,profileImageUrl1,profileImageUrl2,profileImageUrl3,profileImageUrl4,profileImageUrl5,profileImageUrl6;
    int i;
    ProgressBar progressBar,progressBar1,progressBar2,progressBar3,progressBar4,progressBar5,progressBar6;
    private String canteen;

     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newcake);
        receive();
        check=new String[7];
        profileImageUrl=new ArrayList<>();
        imageView=findViewById(R.id.MainImage);
        imageView1=findViewById(R.id.ExtraImage1);
        imageView2=findViewById(R.id.ExtraImage2);
        imageView3=findViewById(R.id.ExtraImage3);
        imageView4=findViewById(R.id.ExtraImage4);
        imageView5=findViewById(R.id.ExtraImage5);
        imageView6=findViewById(R.id.ExtraImage6);
        progressBar=findViewById(R.id.Progressbarmain);
        progressBar1=findViewById(R.id.Progressbar1);
        progressBar2=findViewById(R.id.progressBar2);
        progressBar3=findViewById(R.id.progressbar3);
        progressBar4=findViewById(R.id.progressbar4);
        progressBar5=findViewById(R.id.progressbar5);
        progressBar6=findViewById(R.id.progressbar6);

    }
    private void showImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Food Item Image"), CHOOSE_IMAGE);
    }
void receive(){
    Intent intent=getIntent();
     if (Canteen.equalsIgnoreCase("admin"))
        canteen= intent.getStringExtra("Address");

    else
        canteen=Canteen;
}
    private void uploadImageToFirebaseStorage() {
        TextInputEditText name=findViewById(R.id.CakeName);
        StorageReference profileImageRef =
                FirebaseStorage.getInstance().getReference("Cake/" + name.getText().toString().trim() + "/" + System.currentTimeMillis() + ".jpg");

        if (i==0) {
                 profileImageRef =
                   FirebaseStorage.getInstance().getReference("Cake/" + name.getText().toString().trim() + "/" + "1.main" + ".jpg");
       }
       final ProgressBar progress;
        if (i==0){
            progress=findViewById(R.id.Progressbarmain);
            progress.setVisibility(View.VISIBLE);}
        else if (i==1)
        {
            progress=findViewById(R.id.Progressbar1);
            progress.setVisibility(View.VISIBLE);}
        else if (i==2)
        {
            progress=findViewById(R.id.progressBar2);
            progress.setVisibility(View.VISIBLE);}
        else if (i==3)
        {
            progress=findViewById(R.id.progressbar3);
            progress.setVisibility(View.VISIBLE);}
        else if (i==4)
        {
            progress=findViewById(R.id.progressbar4);
            progress.setVisibility(View.VISIBLE);}
        else if (i==5)
        {
            progress=findViewById(R.id.progressbar5);
            progress.setVisibility(View.VISIBLE);}
        else
        {
            progress=findViewById(R.id.progressbar6);
            progress.setVisibility(View.VISIBLE);}

        if (uriProfileImage != null) {

                 profileImageRef.putFile(uriProfileImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progress.setVisibility(View.GONE);
                            findViewById(R.id.save_food).setEnabled(true);
                            findViewById(R.id.save_food).setActivated(true);
                            profileImageUrl.add(java.util.Objects.requireNonNull(taskSnapshot.getDownloadUrl()).toString());


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progress.setVisibility(View.GONE);
                            Toast.makeText(cakeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
         if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriProfileImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriProfileImage);
               if (i==0)

                imageView.setImageBitmap(bitmap);
               else  if (i==1)

                   imageView1.setImageBitmap(bitmap);
               else if (i==2)
                   imageView2.setImageBitmap(bitmap);
               else if (i==3)
                   imageView3.setImageBitmap(bitmap);
               else if (i==4)
                   imageView4.setImageBitmap(bitmap);
               else if (i==5)
                   imageView5.setImageBitmap(bitmap);
               else
                   imageView6.setImageBitmap(bitmap);
                findViewById(R.id.save_food).setEnabled(false);
                findViewById(R.id.save_food).setActivated(false);

               // Toast.makeText(this," from onactivity i is"+i,Toast.LENGTH_SHORT).show();
                 uploadImageToFirebaseStorage();
                //       final Uri downloadUrl =taskSnapshot.getDownloadUrl();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void cakeCrede(Button save){
        TextInputEditText gm200=findViewById(R.id.gm200);
        TextInputEditText gm500=findViewById(R.id.gm500);
        TextInputEditText gm1000=findViewById(R.id.gm1000);
        TextInputEditText flavour=findViewById(R.id.Flavour);
        TextInputEditText shape=findViewById(R.id.shape);
        TextInputEditText name=findViewById(R.id.CakeName);
        String gm2=gm200.getText().toString().trim();
        String gm5=gm500.getText().toString().trim();
        String gm1=gm1000.getText().toString().trim();
        String sh=shape.getText().toString().trim();
        String fl=flavour.getText().toString().trim();
        String Cakename=name.getText().toString().trim();
        if (Cakename.isEmpty()) {
            name.setError("Name  required");
            name.requestFocus();
            return;
        }
        if (gm2.isEmpty()) {
            gm200.setError("Price  required");
            gm200.requestFocus();
            return;
        }
        if (gm5.isEmpty()) {
            gm500.setError("Price  required");
            gm500.requestFocus();
            return;
        }
        if (gm1.isEmpty()) {
            gm1000.setError("Price  required");
            gm1000.requestFocus();
            return;
        }
        if (fl.isEmpty()) {
            flavour.setError("Flavour  required");
            flavour.requestFocus();
            return;
        }
        if (sh.isEmpty()) {
            shape.setError("Shape  required");
            shape.requestFocus();
            return;
        }
       findViewById(R.id.InfoLinear).setVisibility(View.GONE);
        findViewById(R.id.mainRelative).setVisibility(View.VISIBLE);
         save.setText(getString(R.string.save));

    }
    private void cakeInformation()
    {
        TextInputEditText gm200=findViewById(R.id.gm200);
        TextInputEditText gm500=findViewById(R.id.gm500);
        TextInputEditText gm1000=findViewById(R.id.gm1000);
        TextInputEditText flavour=findViewById(R.id.Flavour);
        TextInputEditText shape=findViewById(R.id.shape);
        TextInputEditText name=findViewById(R.id.CakeName);
        String gm2=gm200.getText().toString().trim();
        String gm5=gm500.getText().toString().trim();
        String gm1=gm1000.getText().toString().trim();
        String sh=shape.getText().toString().trim();
        String fl=flavour.getText().toString().trim();
        String Cakename=name.getText().toString().trim();

         try{
        if (profileImageUrl.isEmpty()) {
            //imageView.setError("Name is required");
            Toast.makeText(this, "Set the Image of the food", Toast.LENGTH_SHORT).show();

            imageView.requestFocus();
            return;
        }}catch (Exception NullPointerException){
            Toast.makeText(this, "Set the Image of the food", Toast.LENGTH_SHORT).show();

            imageView.requestFocus();
            return;
        }
        databaseReference= FirebaseDatabase.getInstance().getReference("Food Items/"+canteen+"/"+"Cake/"+Cakename);
         databaseReference.child("gm500").setValue(gm5);
        databaseReference.child("Food_Image").setValue(profileImageUrl.get(0));
        databaseReference.child("Food_name").setValue(Cakename);
        databaseReference.child("shape").setValue(sh);
        databaseReference.child("flavour").setValue(fl);


        //DatabaseReference databaseReference1=FirebaseDatabase.getInstance().getReference("Food Items/Cake/"+Cakename+"/Price");
       //  databaseReference.child("gm500").setValue(gm5);
        databaseReference.child("gm200").setValue(gm2);
        databaseReference.child("gm1000").setValue(gm1);


        DatabaseReference databaseReference2=FirebaseDatabase.getInstance().getReference("Food Items/"+
                canteen+"/"+"Cake/"+Cakename+"/Images");
         databaseReference2.child("1/Food_Image").setValue(profileImageUrl.get(0));
         databaseReference2.child("1/key").setValue("1");
         profileImageUrl.remove(0);
        try {

    long i;
    for (String image:profileImageUrl){
        i=(System.currentTimeMillis());
        databaseReference2.child(String.valueOf(i)).child("Food_Image").setValue(image);
        databaseReference2.child(String.valueOf(i)).child("key").setValue(String.valueOf(i));

    }

        }catch (Exception ignored){}
        ///////////////here//////////////////
        Toast.makeText(this,"Save",Toast.LENGTH_LONG).show();
        finish();
        //startActivity(new Intent(this,Home.class));
     }

    public void mainImageOnclick(View view) {
        showImageChooser();
        i=0;
    }

    public void FirstImageOnclick(View view) {
        showImageChooser();
        i=1;
    }

    public void SecondImageOnclick(View view) {
        showImageChooser();
        i=2;
    }

    public void ThiredImageOnclick(View view) {
        showImageChooser();
        i=3;
    }

    public void FourthImageOnclick(View view) {
        showImageChooser();
        i=4;
    }

    public void FifthImageOnclick(View view) {
        showImageChooser();
        i=5;
    }

    public void SixthImageOnclick(View view) {
        showImageChooser();
        i=6;
    }

    public void MoreImageOnclick(View view) {
        findViewById(R.id.more).setVisibility(View.GONE);
        findViewById(R.id.ExtraImage4).setVisibility(View.VISIBLE);
        findViewById(R.id.ExtraImage5).setVisibility(View.VISIBLE);
        findViewById(R.id.ExtraImage6).setVisibility(View.VISIBLE);
    }

    public void saveButtonOnclick(View view) {
        Button save=findViewById(R.id.save_food);
        Log.e("Cake","Save button text is "+save.getText().toString().trim());
        if (save.getText().toString().trim().toUpperCase().contains("NEXT")){
            cakeCrede(save);
        }
            else{

            cakeInformation();
        }
    }

    @Override
    public void onBackPressed() {

        if (findViewById(R.id.InfoLinear).getVisibility()!=View.VISIBLE) {
            findViewById(R.id.InfoLinear).setVisibility(View.VISIBLE);
            findViewById(R.id.mainRelative).setVisibility(View.GONE);
            Button save=findViewById(R.id.save_food);
            save.setText("NEXT");
        }else{
            super.onBackPressed();
        }
        }
}
