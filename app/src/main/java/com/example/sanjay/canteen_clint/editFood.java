package com.example.sanjay.canteen_clint;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class editFood extends AppCompatActivity implements   PopupMenu.OnMenuItemClickListener {
    private static final int CHOOSE_IMAGE = 101;
    Button save;
    TextInputEditText Foodname;
    TextInputEditText Fooddesc;
    boolean disablefood=false;
    ImageView FoodImage;
    private String description;
    private String price;
    private String foodimage;
    private String foodname;
    Uri uriProfileImage;
   // ProgressBar progressBar, ProgressbarSave;
 //   ProgressBar horizontalProgress;

    String profileImageUrl;
    private String type;
    // private String activity;
    private TextInputEditText FoodPrice;
    private TextInputEditText FoodDiscount;
    private String discount;
    private String addr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_food);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        profileImageUrl = "";
        discount = "";
       // Toolbar collapsingToolbarLayout=findViewById(R.id.toolbar);
      //  collapsingToolbarLayout.setTitle(foodname);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
   //     progressBar = findViewById(R.id.progressbar);
      ////  horizontalProgress = findViewById(R.id.progressBarHorizontal);
      //  ProgressbarSave = findViewById(R.id.progressBarHorizontal);
        Receive();
        toolbar.setTitle(foodname);
        setTitle(foodname);
        setters();
        Listeners();
    }

    private void setters() {
        Foodname = findViewById(R.id.foodname);
        Fooddesc = findViewById(R.id.description);
        FoodPrice = findViewById(R.id.Price);
        FoodDiscount = findViewById(R.id.fooddiscount);
        FoodImage = findViewById(R.id.foodimage);
        Foodname.setText(foodname);
        Fooddesc.setText(description);
        FoodPrice.setText(price);
        FoodDiscount.setText(discount);
         Glide.with(getApplicationContext()).applyDefaultRequestOptions(RequestOptions.noTransformation()).load(foodimage).into(FoodImage);
      // Picasso.get().load(foodimage).into(FoodImage);
    }

    private void Receive() {
        try {
            Intent intent = getIntent();
            description = intent.getStringExtra("Description");
            foodimage = intent.getStringExtra("Image");
            price = intent.getStringExtra("Price");
            addr = intent.getStringExtra("Address");
            type = intent.getStringExtra("Type");
            foodname = intent.getStringExtra("Name");
            discount = intent.getStringExtra("Discount");
            double dis = ((Double.valueOf(price) - Double.valueOf(discount)) / Double.valueOf(price) );
            TextView FoodDiscount=findViewById(R.id.calcaulatedDiscount);
             //calculate.setText(String.format(getString(R.string.percentage), discount, price, Double.toString((dis) * 100)));
            FoodDiscount.setText("    =" + String.format("%.0f", dis * 100) + "%");
            disablefood=intent.getBooleanExtra("From",false);
            Log.e("Menu_bar",  disablefood+" Discount"+dis);
         } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void Listeners() {
        save = findViewById(R.id.save);
        calculatediscount();
         ImageView editImage = findViewById(R.id.foodimage);
        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageChooser();
                foodimage = profileImageUrl;
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               try {
                   if (!FoodPrice.getText().toString().trim().isEmpty()){
                       if (!FoodPrice.getText().toString().trim().equals("0")&&Integer.parseInt(FoodPrice.getText().toString().trim()) >= ( Integer.parseInt(FoodDiscount.getText().toString().trim())))
                       {
                           save();
                           finish();

                       }else Toast.makeText(editFood.this,"Invalid price",Toast.LENGTH_SHORT).show();
                   }else Toast.makeText(editFood.this,"price is empty",Toast.LENGTH_SHORT).show();
               }catch (Exception e){
                   save();
                   finish();

               }


            }
        });

    }

    void calculatediscount() {
        final TextView calculate = findViewById(R.id.calcaulatedDiscount);
        calculate.setVisibility(View.VISIBLE);

        FoodPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FoodDiscount.setText("0");
            }
        });

        FoodDiscount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int discount;
                try {
                    discount = Integer.parseInt(String.valueOf(charSequence));
                } catch (Exception NumberFormatException) {
                    discount = 0;
                }
                int Price = Integer.parseInt(price);
                try {
                    double dis = Double.parseDouble(Integer.toString((Price - discount / Price) * 100));
                    if (dis > 0 && Price >= discount) {
                        calculate.setText(String.format("ontext %s%%", String.format(" =%s", dis)));
                    } else {
                        calculate.setText(R.string.invalid_entry);
                        FoodDiscount.setError(getString(R.string.invalid_entry));

                    }

                } catch (Exception ArithmeticException) {
                    calculate.setText("0");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                double discount;
                try {
                    discount = Integer.parseInt(String.valueOf(editable));
                } catch (Exception NumberFormatException) {
                    discount = 0.0;
                }
                double Price = Integer.parseInt(price);
                try {
                    double dis = (Price - discount) / Price;
                    //calculate.setText(String.format(getString(R.string.percentage), discount, price, Double.toString((dis) * 100)));
                    calculate.setText("    =" + String.format("%.0f", dis * 100) + "%");
                } catch (Exception ArithmeticException) {
                    // calculate.setText("0");
                }
            }
        });


    }



    private void save() {
//       horizontalProgress.setVisibility(View.VISIBLE);
  //     progressBar.setVisibility(View.VISIBLE);
       save.setVisibility(View.GONE);
//        ProgressbarSave.setVisibility(View.VISIBLE);
       String name = new String(),desc = new String();
//        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().
  //              child("Food Items/"+type );
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().
                child("Food Items/"+addr+"/"+type );
        name=Foodname.getText().toString().trim();
      try {
          if (Foodname.getText().toString().trim().isEmpty()) {
              name = foodname;
          } else if (Foodname.getText().toString().trim().contentEquals(foodname)) {
              Toast.makeText(editFood.this, "FoodName is same", Toast.LENGTH_SHORT).show();
          } else name = Foodname.getText().toString().trim();
      }catch (Exception NullPointerException){
          name=foodname;
      }
      /* try {
             if (Fooddesc.getText().toString().trim().isEmpty()) {
               desc = description;
           } else if (Fooddesc.getText().toString().trim().contentEquals(description)) {
               Toast.makeText(editFood.this, "Description is same", Toast.LENGTH_SHORT).show();
               //  return;
           } else desc = Fooddesc.getText().toString().trim();
       }catch (Exception NullPointerException){
          desc=description;
       }*/
         try {
             if (profileImageUrl.isEmpty()) {
                 Toast.makeText(editFood.this, "Image is empty", Toast.LENGTH_SHORT).show();
                 profileImageUrl = foodimage;
             }
         }catch (Exception NullPointerException){
          profileImageUrl=foodimage;
         }

        databaseReference.child(foodname).setValue(null);
        DatabaseReference database=FirebaseDatabase.getInstance().getReference().
                child("Food Items/"+addr+"/"+type+"/"+name);
        database.child("Description").setValue(Fooddesc.getText().toString().trim());
        database.child("Food_Image").setValue(profileImageUrl);
        database.child("Food_name").setValue(name);
        database.child("Type").setValue(type);
        price=FoodPrice.getText().toString().trim();
      try {
          if (FoodDiscount.getText().toString().trim().isEmpty()  )
              discount=price;
          else if ( Integer.parseInt(FoodDiscount.getText().toString().trim())<=0)
              discount=price;
          else    discount=FoodDiscount.getText().toString().trim();
      }   catch (Exception e){discount=price;
      }

            database.child("discount").setValue(Integer.parseInt(discount));
            database.child("price").setValue(Integer.parseInt(price));


//        horizontalProgress.setVisibility(View.INVISIBLE);
  //      progressBar.setVisibility(View.INVISIBLE);
   //     ProgressbarSave.setVisibility(View.INVISIBLE);
        save.setVisibility(View.VISIBLE);
     /*   if (discount.equals("0")||discount.isEmpty())
        {
            database.child("discount").setValue(Integer.parseInt(price));
            database.child("price").setValue(Integer.parseInt(price)).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    horizontalProgress.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            horizontalProgress.setVisibility(View.INVISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);
                            ProgressbarSave.setVisibility(View.INVISIBLE);
                            save.setVisibility(View.VISIBLE);
                        }
                    });
        }else {
            database.child("discount").setValue(Integer.parseInt(price));
            database.child("price").setValue(Integer.parseInt(discount)).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    horizontalProgress.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            horizontalProgress.setVisibility(View.INVISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);
                            ProgressbarSave.setVisibility(View.INVISIBLE);
                            save.setVisibility(View.VISIBLE);
                        }
                    });*/

       // }
    }

    private void showImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Food  Image"), CHOOSE_IMAGE);
    }
    private void uploadImageToFirebaseStorage() {
        StorageReference profileImageRef =
                FirebaseStorage.getInstance().getReference("food/" + foodname + ".jpg");

        if (uriProfileImage != null) {
         //   progressBar.setVisibility(View.VISIBLE);
            save.setText("Please wait to upload the image");
            save.setTextColor(Color.BLACK);
            save.setClickable(false);
            save.setEnabled(false);
            profileImageRef.putFile(uriProfileImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                       //     progressBar.setVisibility(View.GONE);
                            profileImageUrl = taskSnapshot.getDownloadUrl().toString();
                            save.setClickable(true);
                            save.setEnabled(true);
                            save.setTextColor(Color.WHITE);
                            save.setText("Save");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                         //   progressBar.setVisibility(View.GONE);
                            Toast.makeText(editFood.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
                FoodImage.setImageBitmap(bitmap);
              //  saveuserinformation();
                uploadImageToFirebaseStorage();
                //       final Uri downloadUrl =taskSnapshot.getDownloadUrl();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case    R.id.disable:
                disablefood(); return true;
            case R.id.enable:
            enablefood(); return true;
            case R.id.remove:
            removefood(true);
                               return true;
            default:
            return super.onOptionsItemSelected(item);

        }
    }
void disablefood(){
    DatabaseReference databaseReference1= FirebaseDatabase.getInstance().getReference().
            child("Food Items/"+addr+"/"+type ).child(foodname);
    databaseReference1.setValue(null);
    disablefood=true;
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().
                getReference( "Food Items/"+addr+"/Disable"+"/"+foodname);
        databaseReference.child("Food_name").setValue(foodname);
        databaseReference.child("Description").setValue(description);
        databaseReference.child("Type").setValue(type);
        databaseReference.child("Food_Image").setValue(foodimage);
        databaseReference.child("price").setValue(Integer.parseInt(price));
        databaseReference.child("discount").setValue(Integer.parseInt(discount));
}
    void enablefood(){
        disablefood=false;
        DatabaseReference databaseReference1= FirebaseDatabase.getInstance().getReference().
                child("Food Items/"+addr+"/Disable" ).child(foodname);
        databaseReference1.setValue(null);
        DatabaseReference databaseReference=FirebaseDatabase.getInstance()
                .getReference("Food Items/"+addr+"/"+type+"/"+foodname);
        databaseReference.child("Food_name").setValue(foodname);
        databaseReference.child("Description").setValue(description);
        databaseReference.child("Type").setValue(type);
        databaseReference.child("Food_Image").setValue(foodimage);
        databaseReference.child("price").setValue(Integer.parseInt(price));
        databaseReference.child("discount").setValue(Integer.parseInt(discount));
    }
    void removefood(boolean from){
        if (!disablefood){
            DatabaseReference databaseReference1= FirebaseDatabase.getInstance().getReference().
                    child("Food Items/"+addr+"/"+type ).child(foodname);
            databaseReference1.setValue(null);
        }else{
            DatabaseReference databaseReference1= FirebaseDatabase.getInstance().getReference().
                    child("Food Items/"+addr+"/Disable" ).child(foodname);
            databaseReference1.setValue(null);
        }
    }
    public void open_food_option(View view) {
        PopupMenu popupMenu=new PopupMenu(this,view);
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_scrolling,popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener( this);
        if (disablefood){
           // disablefood=false;
            popupMenu.getMenu().findItem(R.id.disable).setVisible(false);
        }else{
           // disablefood=true;
            popupMenu.getMenu().findItem(R.id.enable).setVisible(false);

        }
        popupMenu.show();
     }


}

