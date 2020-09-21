package com.example.sanjay.canteen_clint;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import static com.example.sanjay.canteen_clint.extra_classes.Constants.Canteen;

public class NewFood extends AppCompatActivity   {

    private static final int CHOOSE_IMAGE = 101;
    private Button save;
    TextView textView;
    ImageView imageView;
    EditText Username;
    private String Type_Button="";
    DatabaseReference databaseReference;
    Uri uriProfileImage;
    ProgressBar progressBar;
    private EditText editText,desc,price,type;
    TextView clk_type;
    String profileImageUrl="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_layout_new_food);

        receive();
//        databaseReference= FirebaseDatabase.getInstance().getReference().child("Food Items");

        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        editText=  findViewById(R.id.editTextDisplayName);
        price= (EditText) findViewById(R.id.editPrice);
        desc=(EditText) findViewById(R.id.editDescription);
        final Button reset = findViewById(R.id.reset);
        Display display = getWindowManager().getDefaultDisplay();
        LinearLayout.LayoutParams params;

        DisplayMetrics realMatrices=new DisplayMetrics();
        display.getRealMetrics(realMatrices);
        params = new LinearLayout.LayoutParams( realMatrices.widthPixels/2, ViewGroup.LayoutParams.WRAP_CONTENT );
        findViewById(R.id.reset).setLayoutParams(params);

        save=findViewById(R.id.buttonSave);
         // save.setOnClickListener(this);

        imageView = (ImageView) findViewById(R.id.imageView);

         imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageChooser();
            }
        });

final TextView foodtypename=findViewById(R.id.textView2);
         reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setText("");
                price.setText("");
                desc.setText("");
                imageView.setImageDrawable(Drawable.createFromPath("ic_camera_alt_black_24dp.xml"));
                Type_Button=null;
                foodtypename.setText(R.string.select_type_for_food);
                findViewById(R.id.relativeLayout).setVisibility(View.GONE);
                findViewById(R.id.foodinfo).setVisibility(View.VISIBLE);
                reset.setVisibility(View.GONE);

            }
        });


    }

    private void receive() {
        Intent intent=getIntent();
        if (Canteen.equalsIgnoreCase("admin"))
            databaseReference= FirebaseDatabase.getInstance().getReference().child("Food Items/"+
                    intent.getStringExtra("Address"));

        else
            databaseReference= FirebaseDatabase.getInstance().getReference().child("Food Items/"+Canteen);


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
                FirebaseStorage.getInstance().getReference("food/"+editText.getText().toString().trim() );

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
                            Toast.makeText(NewFood.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
                imageView.setImageBitmap(bitmap);
                saveuserinformation();
                uploadImageToFirebaseStorage();
                //       final Uri downloadUrl =taskSnapshot.getDownloadUrl();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    boolean check_initial(){
        String name=editText.getText().toString().trim();
        //  String types=type.getText().toString().trim();
        String prices=price.getText().toString().trim();
        String Descriptions=desc.getText().toString().trim();
        if (name.isEmpty()) {
            editText.setError("Name is required");
            editText.requestFocus();
            return false;
        }
        if (Type_Button.isEmpty()) {
            Toast.makeText(this,"Select Type",Toast.LENGTH_SHORT).show();
            return false;
        }
        if (prices.isEmpty()) {
            price.setError("Price is required");
            price.requestFocus();
            return false;
        }
        if (Descriptions.isEmpty()) {
            desc.setError("Description is required");
            desc.requestFocus();
            return false;
        }
        return  true;
    }
     private void saveuserinformation()
    {String name=editText.getText().toString().trim();
        //  String types=type.getText().toString().trim();
        String prices=price.getText().toString().trim();
        String Descriptions=desc.getText().toString().trim();

        if (profileImageUrl.isEmpty()) {
             Toast.makeText(this,"Set the Image of the food",Toast.LENGTH_SHORT).show();
            imageView.requestFocus();

            return;
        }
        UserInformation userInformation=new UserInformation(name,Type_Button,Integer.parseInt(prices),Descriptions,profileImageUrl.toString(),Integer.parseInt(prices));
         databaseReference.child(Type_Button+"/"+name ).setValue(userInformation);
         Toast.makeText(this,"Saved",Toast.LENGTH_LONG).show();
        finish();
     }


   /* @Override
    public void onClick(View view) {

        if (view==save){
            saveuserinformation();

        }
    }*/



    public void saveButton(View view) {
        Button button=findViewById(R.id.buttonSave);
        if (button.getText().toString().trim().toUpperCase().equals("NEXT")){
            if (check_initial()) {
                button.setText(getString(R.string.save));
                findViewById(R.id.relativeLayout).setVisibility(View.VISIBLE);
                findViewById(R.id.foodinfo).setVisibility(View.GONE);
                findViewById(R.id.reset).setVisibility(View.VISIBLE);
            }
        }else{
            saveuserinformation();

        }
    }

    @Override
    public void onBackPressed() {
        Button button=findViewById(R.id.buttonSave);
        if (button.getText().toString().trim().toUpperCase().equals("SAVE")){
          //  Display display = getWindowManager().getDefaultDisplay();
          //  LinearLayout.LayoutParams params;
            button.setText("next");
            findViewById(R.id.relativeLayout).setVisibility(View.GONE);
            findViewById(R.id.foodinfo).setVisibility(View.VISIBLE);
        //    DisplayMetrics realMatrices=new DisplayMetrics();
           // display.getRealMetrics(realMatrices);
          //  params = new LinearLayout.LayoutParams( realMatrices.widthPixels/2, ViewGroup.LayoutParams.WRAP_CONTENT );
          //  findViewById(R.id.reset).setLayoutParams(params);
            findViewById(R.id.reset).setVisibility(View.GONE);
        }else
         super.onBackPressed();
    }

    public void food_type_selector_clicked(View view) {
        if (findViewById(R.id.food_type).getVisibility()==View.VISIBLE){
            findViewById(R.id.food_type).setVisibility(View.GONE);
        }else{
            findViewById(R.id.food_type).setVisibility(View.VISIBLE);
        }
    }

    public void snacksclicked(View view) {
          TextView foodtypename=findViewById(R.id.textView2);
                 Type_Button="Snaks";
                foodtypename.setText(R.string.Snaks);
                findViewById(R.id.food_type).setVisibility(View.GONE);


            }

    public void sepcialclicked(View view) {
          TextView foodtypename=findViewById(R.id.textView2);

        findViewById(R.id.food_type).setVisibility(View.GONE);
        foodtypename.setText(R.string.special);

        Type_Button="Special";
    }

    public void mealclicked(View view) {
          TextView foodtypename=findViewById(R.id.textView2);

        findViewById(R.id.food_type).setVisibility(View.GONE);
        foodtypename.setText(R.string.Meal);

        Type_Button="Meal";

    }

    public void beveragesclicked(View view) {
          TextView foodtypename=findViewById(R.id.textView2);

        findViewById(R.id.food_type).setVisibility(View.GONE);
        foodtypename.setText(R.string.Beverages);

        Type_Button="Beverages";
    }
}

