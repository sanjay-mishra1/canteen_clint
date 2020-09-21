package com.example.sanjay.canteen_clint;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseError;
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
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

/**
 * Created by sanjay on 03/07/2018.
 */

public class editDetailCake extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener{
    RecyclerView recyclerView;
    private String CakeName;
    private String Gm500;
    private adapter_allorders adapter;
    TextInputEditText name,gm500,gm200,gm1000,flavour,shape;
    private String ShapeString;
    private String FlavourString;
    ImageView imageView1,imageView2,imageView3;
     ArrayList<String> profileImageUrl;
    private static final int CHOOSE_IMAGE = 101;
    private static final int CHOOSE_Main_IMAGE = 102;
    Uri uriProfileImage;
    int i;
    private String Mainimage;
    private ArrayList<String> images;
    private int count=0;
    private String id=null;
    private String Gm500_d;
    private String Gm200;
    private String Gm200_d;
    private String Gm1000;
    private String Gm1000_d;
    boolean disablefood=false;
    private String canteen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editcake);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
         getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        images=new ArrayList<>();
        recyclerView=findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(adapter);

        gm500=findViewById(R.id.gm500);
        gm200=findViewById(R.id.gm200);
        gm1000=findViewById(R.id.gm1000);
        flavour=findViewById(R.id.Flavour);
        shape=findViewById(R.id.shape);
        imageView1=findViewById(R.id.Extraimage1);
        imageView2=findViewById(R.id.Extraimage2);
        imageView3=findViewById(R.id.Extraimage3);
        Receive();
        flavour.setText(FlavourString);
        shape.setText(ShapeString);

        profileImageUrl=new ArrayList<>();
        name=findViewById(R.id.CakeName);
        toolbar.setTitle(CakeName);
setters();
    }
void Receive(){
    Intent intent=getIntent();
   try {

       CakeName = intent.getStringExtra("CakeName");
       canteen = intent.getStringExtra("Address");
       Gm500 = intent.getStringExtra("gm500");
       Gm500_d = intent.getStringExtra("gm500_d");
       Gm200 = intent.getStringExtra("gm200");
       Gm200_d = intent.getStringExtra("gm200_d");
       Gm1000 = intent.getStringExtra("gm1000");
       Gm1000_d = intent.getStringExtra("gm1000_d");
       ShapeString = intent.getStringExtra("Shape");
       FlavourString = intent.getStringExtra("Flavour");
       Mainimage = intent.getStringExtra("Image");
       disablefood = intent.getBooleanExtra("From",false);

       Glide.with(getApplicationContext()).applyDefaultRequestOptions(RequestOptions.noTransformation()).load(Mainimage).into((ImageView) findViewById(R.id.mainCakeImage));
   }catch (Exception e ){e.printStackTrace();}
}
void setters(){
    TextInputEditText gm500d,gm200d,gm1000d;
    gm500d=findViewById(R.id.gm500_d);
    gm200d=findViewById(R.id.gm200_d);
    gm1000d=findViewById(R.id.gm1000_d);
    name.setText(CakeName);
   try {
       if (Gm200.isEmpty()||Gm200.equals(Gm200_d)) {
           gm200.setText(Gm200);
           gm200d.setText("");
       }
       else{
           gm200.setText(Gm200_d);
           gm200d.setText(Gm200);
       }
   }catch (Exception e){
       gm200.setText(Gm200);
       gm200d.setText("");
   }
    try {
        if (Gm500.isEmpty()||Gm500.equals(Gm500_d)) {
            gm500.setText(Gm500);
            gm200d.setText("");
        }
        else{
            gm500 .setText(Gm500_d);
            gm500d.setText(Gm500);
        }
    }catch (Exception e){
        gm200.setText(Gm500);
        gm200d.setText("");
    }
    try {
        if (Gm1000.isEmpty()||Gm200.equals(Gm1000_d)) {
            gm1000 .setText(Gm1000);
            gm1000d.setText("");
        }
        else{
            gm1000.setText(Gm1000_d);
            gm1000d.setText(Gm1000);
        }
    }catch (Exception e){
        gm1000.setText(Gm1000);
        gm1000d.setText("");
    }
    setTitle(CakeName);

}
    @Override
    protected void onStart() {
        super.onStart();

      //   DatabaseReference databaseReference1=FirebaseDatabase.getInstance().getReference("Food Items/Cake/"+CakeName+"/Price");
      /*  databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String,Object> map=(Map<String,Object>) dataSnapshot.getValue();
              try {
                  gm200.setText((String) map.get("gm200"));
                  gm1000.setText((String) map.get("gm1000"));
              }catch (Exception NullPointerException){}

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
*/
        DatabaseReference data=FirebaseDatabase.getInstance().getReference("Food Items/"+canteen+"/"+"Cake/"+CakeName+"/Images");
        data.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    album_allorders cake=dataSnapshot1.getValue(album_allorders.class);
                    //images[count]=cake.getFood_Image();
                    assert cake != null;
                    images.add(cake.getFood_Image());
                    count++;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        DatabaseReference databaseReference= FirebaseDatabase.getInstance()
                .getReference("Food Items/"+canteen+"/"+"Cake/"+CakeName+"/Images");

        FirebaseRecyclerAdapter<album_allorders,mainactiv_allorders.FoodViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<album_allorders, mainactiv_allorders.FoodViewHolder>(
                album_allorders.class,
                R.layout.layouteditcake,
                mainactiv_allorders.FoodViewHolder.class,
                databaseReference

        ) {
            @Override
            protected void populateViewHolder(mainactiv_allorders.FoodViewHolder viewHolder, final album_allorders model, final int position) {
                viewHolder.setImage6(model.getFood_Image(),getApplicationContext()).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                      id=model.getKey();
                        ChangeCakeImage(false,position);
                    }
                });
            }
        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);
      }
    public void ChangeCakeImage(boolean mainImage,int pos ) {
        if (mainImage){
         changeMainImage();
        }else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                start_image_dialog(  pos);
            }
        }
    }
private void changeCakeImage(){
    Intent intent = new Intent();
    intent.setType("image/*");
    intent.setAction(Intent.ACTION_GET_CONTENT);
    startActivityForResult(Intent.createChooser(intent, "Select Cake Image"), 10);
}

    private void changeMainImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select cake main image"), CHOOSE_Main_IMAGE);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    void start_image_dialog(final int pos ){

         final View dialogView = View.inflate( this, R.layout.settings_img_more_options, null);
        final Dialog dialog = new Dialog(Objects.requireNonNull(this),R.style.Dialog1);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
         lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        lp.windowAnimations = R.style.DialogAnimation;
        Objects.requireNonNull(dialog.getWindow()).setAttributes(lp);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(dialogView);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss();

                    return true;
                }

                return false;
            }
        });
        dialogView.findViewById(R.id.one).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                changeCakeImage();
               // showImageChooser();
            }
        });
        dialogView.findViewById(R.id.two).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();

                FirebaseDatabase.getInstance().getReference("Food Items/"+canteen+"/"+"Cake/"+CakeName).child("Images").child(id).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.e("Cake_Process","Deletion completed  "+images.get(i)+" pos is "+id);

                    }
                });
                FirebaseStorage.getInstance().getReferenceFromUrl(images.get(pos)).delete();
                images.remove(pos);
                count--;


            }
        });


        dialog.show();
    }

    private void showImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Food Item Image"), CHOOSE_IMAGE);
    }

    private void uploadImageToFirebaseStorage(boolean mainImage, final String ids) {
        Log.e("Cake_Process","Inside upload image mainImage is "+mainImage+" ids is "+ids);
      if (!mainImage) {
          Log.e("Cake_Process","Inside upload image not mainImage is "+mainImage+" ids is "+ids);

          String time= String.valueOf(System.currentTimeMillis());
          StorageReference profileImageRef =
                  FirebaseStorage.getInstance().getReference("Cake/"+canteen+"/"+CakeName+"/" +time  + ".jpg");
          final ProgressBar progress;
          if (i == 1) {
              progress = findViewById(R.id.Progressbar1);
              progress.setVisibility(View.VISIBLE);
          } else if (i == 2) {
              progress = findViewById(R.id.Progressbar2);
              progress.setVisibility(View.VISIBLE);
          } else {
              progress = findViewById(R.id.Progressbar3);
              progress.setVisibility(View.VISIBLE);
          }

          if (uriProfileImage != null) {

              profileImageRef.putFile(uriProfileImage)
                      .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                          @Override
                          public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                              progress.setVisibility(View.GONE);
                           /*   if (i == 1) {
                                  profileImageUrl1 = taskSnapshot.getDownloadUrl().toString();
                              } else if (i == 2) {
                                  profileImageUrl2 = taskSnapshot.getDownloadUrl().toString();
                              } else {
                                  profileImageUrl3 = taskSnapshot.getDownloadUrl().toString();
                              }
                            */
                              findViewById(R.id.button).setEnabled(true);

                              profileImageUrl.add(taskSnapshot.getDownloadUrl().toString());

                          }
                      })
                      .addOnFailureListener(new OnFailureListener() {
                          @Override
                          public void onFailure(@NonNull Exception e) {
                              findViewById(R.id.button).setEnabled(true);

                              progress.setVisibility(View.GONE);
                              Toast.makeText(editDetailCake.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                          }
                      });
          }
      }else  {StorageReference profileImageRef;
          if (ids==null) {
              profileImageRef =
                      FirebaseStorage.getInstance().getReference("Cake/" + CakeName + "/1.main" + ".jpg");
          }else{
              profileImageRef =
                      FirebaseStorage.getInstance().getReference("Cake/" + CakeName + "/"+ids + ".jpg");
          }
          if (uriProfileImage != null) {

              profileImageRef.putFile(uriProfileImage)
                      .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                          @Override
                          public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //  progress.setVisibility(View.GONE);
                              findViewById(R.id.button).setEnabled(true);
                         if (ids==null) {
                             FirebaseDatabase.getInstance().getReference("Food Items/"+canteen+"/"+"Cake").child(CakeName).child("Food_Image").
                                     setValue(taskSnapshot.getDownloadUrl().toString());
                             FirebaseDatabase.getInstance().getReference("Food Items/"+canteen+"/"+"Cake/"+CakeName+"/Images/1").child("Food_Image").
                                     setValue(taskSnapshot.getDownloadUrl().toString());
                             findViewById(R.id.mainprogress).setVisibility(View.GONE);


                         }else{   findViewById(R.id.recyclerprogress).setVisibility(View.GONE);

                             FirebaseDatabase.getInstance().getReference("Food Items/"+canteen+"/"+"Cake/"+CakeName+"/Images/"+ids).child("Food_Image").
                                         setValue(taskSnapshot.getDownloadUrl().toString());
                            // FirebaseDatabase.getInstance().getReference("Food Items/Cake/"+CakeName+"/Images/"+ids).
                                    // setValue(taskSnapshot.getDownloadUrl().toString());




                         }


                          }
                      })
                      .addOnFailureListener(new OnFailureListener() {
                          @Override
                          public void onFailure(@NonNull Exception e) {
                              findViewById(R.id.recyclerprogress).setVisibility(View.GONE);
                              findViewById(R.id.mainprogress).setVisibility(View.GONE);
                             Log.e("Cake_progress","Inside failed upload "+e.getMessage());
                              findViewById(R.id.button).setEnabled(true);
                          //    progress.setVisibility(View.GONE);
                              Toast.makeText(editDetailCake.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                          }
                      });
          }

      }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("Cake_Process","Inside onActivity result "+requestCode);
        findViewById(R.id.button).setEnabled(false);
        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriProfileImage = data.getData();
            Log.e("Cake_Process","Inside activity result choose image  ");

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriProfileImage);
                   if (i==1)

                    imageView1.setImageBitmap(bitmap);
                else if (i==2)
                    imageView2.setImageBitmap(bitmap);
                else
                    imageView3.setImageBitmap(bitmap);


                 uploadImageToFirebaseStorage(false, null);
             } catch (IOException e) {
                e.printStackTrace();
            }
        }else if (requestCode == CHOOSE_Main_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null){
        findViewById(R.id.mainprogress).setVisibility(View.VISIBLE);
            uriProfileImage = data.getData();
           ImageView i= findViewById(R.id.mainCakeImage);
            Bitmap bitmap ;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriProfileImage);
                i.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }



            uploadImageToFirebaseStorage(true,null);
          //  FirebaseDatabase.getInstance().getReference("Food Items/Cake").child(CakeName).child("Food_Image").setValue(data.getData());
        }else if (requestCode == 10 && resultCode == RESULT_OK && data != null && data.getData() != null){ findViewById(R.id.recyclerprogress).setVisibility(View.VISIBLE);
            uriProfileImage = data.getData();
            uploadImageToFirebaseStorage(true,id);
        }
    }


    public void extraimage1Onclick(View view) {
    showImageChooser();
        i=1;

    }

    public void extraimage2Onclick(View view) {
        showImageChooser();
        i=2;
}

    public void extraimage3Onclick(View view) {
        showImageChooser();
        i=3;
}

    void moveWnode(){

   /* final DatabaseReference fromPath=FirebaseDatabase.getInstance().getReference("Food Items/Cake/"+CakeName);
    String s;
    try {s=name.getText().toString().trim();
        if (name.getText().toString().trim().isEmpty())
            s = CakeName;
    }catch (Exception NullPointerException){            s = CakeName; }
    final DatabaseReference toPath=FirebaseDatabase.getInstance().getReference("Food Items/Cake/"+s+"/Images");
        fromPath.child("Images").addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                toPath.setValue(dataSnapshot.getValue()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        fromPath.setValue(null);
                        saveChanges();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(editDetailCake.this,"Failed to connect to the server",Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });*/
    }

    private void saveChanges() {
        String cake = name.getText().toString().trim();
        final String fl = flavour.getText().toString().trim();
        final String sh = shape.getText().toString().trim();
        final String gm5 = gm500.getText().toString().trim();
        final String gm1 = gm1000.getText().toString().trim();
        final String gm2 = gm200.getText().toString().trim();
        TextInputEditText gm500_d_input=findViewById(R.id.gm500_d);
        TextInputEditText gm200_d_input=findViewById(R.id.gm200_d);
        TextInputEditText gm10000_d_input=findViewById(R.id.gm1000_d);
        final String gm5_d = gm500_d_input.getText().toString().trim();
        final String gm1_d = gm10000_d_input.getText().toString().trim();
        final String gm2_d = gm200_d_input.getText().toString().trim();

        final DatabaseReference databaseReference ;

        if (cake.isEmpty())
        {    cake=CakeName;
        }
        databaseReference = FirebaseDatabase.getInstance().getReference("Food Items/"+canteen+"/"+"Cake/" + cake);
        final String finalCake = cake;
        databaseReference.child("Food_name").setValue(finalCake);
        databaseReference.child("shape").setValue(sh);
        databaseReference.child("flavour").setValue(fl);
        databaseReference.child("Food_Image").setValue(Mainimage);

        if (!gm2_d.isEmpty()&& !gm2_d.equals("0"))
        {
            databaseReference .child("gm200").setValue(gm2_d);
            databaseReference .child("gm200_d").setValue(gm2);
        }else{
            databaseReference .child("gm200").setValue(gm2);
            databaseReference .child("gm200_d").setValue("");
        }
        if (!gm5_d.isEmpty()&& !gm5_d.equals("0"))
        {
            databaseReference .child("gm500").setValue(gm5_d);
            databaseReference .child("gm500_d").setValue(gm5);
        }else{
            databaseReference .child("gm500").setValue(gm5);
            databaseReference .child("gm500_d").setValue("");
        }
        if (!gm1_d.isEmpty()&& !gm1_d.equals("0"))
        {
            databaseReference .child("gm1000").setValue(gm1_d);
            databaseReference .child("gm1000_d").setValue(gm1);
        }else{
            databaseReference .child("gm1000").setValue(gm1);
            databaseReference .child("gm1000_d").setValue("");
        }




            //get_total_images();
            String t;
            try {
                DatabaseReference databaseReference3=FirebaseDatabase.getInstance()
                        .getReference("Food Items/"+canteen+"/"+"Cake/"+ CakeName +"/Images");

                if (!profileImageUrl.get(0).isEmpty()) {
                    t = String.valueOf(System.currentTimeMillis());
                    databaseReference3.child(t + "/Food_Image").setValue(profileImageUrl.get(0));
                    databaseReference3.child(t + "/key").setValue(t);
                }
                if (!profileImageUrl.get(1).isEmpty()) {
                    t = String.valueOf(System.currentTimeMillis() );
                    databaseReference3.child(t + "/Food_Image").setValue(profileImageUrl.get(1));
                    databaseReference3.child(t  + "/key").setValue(t);
                }
                if (!profileImageUrl.get(2).isEmpty()) {
                    t = String.valueOf(System.currentTimeMillis());

                    databaseReference3.child(t+ "/Food_Image").setValue(profileImageUrl.get(2));
                    databaseReference3.child(t + "/key").setValue(t);
                }

            }catch (Exception e){
                Log.e("Cake_Process","Failed to upload images catch is "+e.toString());
            }






        finish();
    }
        void get_total_images(){
             DatabaseReference databaseReference= FirebaseDatabase.getInstance().
                     getReference("Food Items/Cake/"+CakeName+"/Images");
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int total_images=(int)dataSnapshot.getChildrenCount();
                    DatabaseReference databaseReference3=FirebaseDatabase.getInstance().getReference("Food Items/Cake/"+ CakeName +"/Images");
                    for (int i=total_images;i<total_images+3;i++){
                        databaseReference3.child(Integer.toString(i)+ "/Food_Image").setValue(images.get(i));
                        databaseReference3.child(Integer.toString(i) + "/key").setValue(i);
                    }
                    String t;
                    try {

                        if (!profileImageUrl.get(0).isEmpty()) {
                            t = String.valueOf(System.currentTimeMillis());
                            databaseReference3.child(Integer.toString(total_images+1) + "/Food_Image").setValue(profileImageUrl.get(0));
                            databaseReference3.child(Integer.toString(total_images+1) + "/key").setValue(t);
                        }
                        if (!profileImageUrl.get(1).isEmpty()) {
                            t = String.valueOf(System.currentTimeMillis() + 1);
                            databaseReference3.child(Integer.toString(total_images +2) + "/Food_Image").setValue(profileImageUrl.get(1));
                            databaseReference3.child(Integer.toString(total_images +2)  + "/key").setValue(t);
                        }
                        if (!profileImageUrl.get(2).isEmpty()) {
                            t = String.valueOf(System.currentTimeMillis() + 2);

                            databaseReference3.child(Integer.toString(total_images +3) + "/Food_Image").setValue(profileImageUrl.get(2));
                            databaseReference3.child(Integer.toString(total_images +3) + "/key").setValue(t);
                        }

                    }catch (Exception ignored){}
                 }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
         }
    public void saveButtonClicked(View view) {

        saveChanges();

    }

    public void mainImageOnclick(View view) {
    changeMainImage();
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case    R.id.disable:Toast.makeText(this,"Disable clicked",Toast.LENGTH_SHORT).show();
                disablefood(); return true;
            case R.id.enable:Toast.makeText(this,"enable clicked",Toast.LENGTH_SHORT).show();
                enablefood(); return true;
            case R.id.remove: Toast.makeText(this,"remove clicked",Toast.LENGTH_SHORT).show();
                removefood(true);
                return true;
            default:Toast.makeText(this,"default clicked",Toast.LENGTH_SHORT).show();
                return super.onOptionsItemSelected(item);

        }
    }
    public void open_Cake_option(View view) {

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
    void disablefood(){
        disablefood=true;
        //from
        FirebaseDatabase.getInstance().getReference("Food Items/"+canteen+"/"+"Cake").child(CakeName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
              //to
                FirebaseDatabase.getInstance().getReference("Food Items/"+canteen+"/"+"Cake_Disable").child(CakeName).setValue(dataSnapshot.getValue(), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError firebaseError, DatabaseReference firebase) {
                        if (firebaseError != null) {
                            System.out.println("Copy failed");
                        } else {
                            FirebaseDatabase.getInstance().getReference("Food Items/"+canteen+"/"+"Cake").child(CakeName).setValue(null);


                        }
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    void enablefood(){
        disablefood=false;
        FirebaseDatabase.getInstance().getReference("Food Items/"+canteen+"/"+"Cake_Disable").child(CakeName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FirebaseDatabase.getInstance().getReference("Food Items/"+canteen+"/"+"Cake").child(CakeName).setValue(dataSnapshot.getValue(), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError firebaseError, DatabaseReference firebase) {
                        if (firebaseError != null) {
                            System.out.println("Copy failed");
                        } else {
                            FirebaseDatabase.getInstance().getReference("Food Items/"+canteen+"/"+"Cake_Disable").child(CakeName).setValue(null);
                            System.out.println("Success");

                        }
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    void removefood(boolean from){
        if (!disablefood){
            DatabaseReference databaseReference1= FirebaseDatabase.getInstance().getReference().
                    child("Food Items/"+canteen+"/"+"Cake" ).child(CakeName);
            databaseReference1.setValue(null);
        }else{
            DatabaseReference databaseReference1= FirebaseDatabase.getInstance().getReference().
                    child("Food Items/"+canteen+"/"+"Cake_Disable").child(CakeName);
            databaseReference1.setValue(null);
        }
    }

}
