package com.example.sanjay.canteen_clint.newChatScreen;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.sanjay.canteen_clint.R;
import com.example.sanjay.canteen_clint.album_allorders;
import com.example.sanjay.canteen_clint.mainactiv_allorders;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
 import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class help_activity extends AppCompatActivity {
     private   final int READ_EXTERNAL_STORAGE =102 ;
    private   final int OPEN_CAMERA =103 ;
      TextView Edit_message;
    boolean isActive;
    public int count = 0;
      private   final int CHOOSE_IMAGE = 101;
   public static Uri uriProfileImage;
    boolean actionButtonStatus = false;
      private ImageButton actionButton;
     MyAdapter adapter;
     public static byte[] messageImage;
     RecyclerView recyclerView;
    public static String Caption;
    public static String Uid;
    public static String Canteen;
    public static String UserName;
    public static String img;
    public    long lastseen;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_help_layout2);
        receive();
        Calendar c = Calendar.getInstance();
        SimpleDateFormat date = new SimpleDateFormat("ddMM", Locale.ENGLISH);
        lastseen=Integer.parseInt(date.format(c.getTime()));

        FirebaseDatabase.getInstance().getReference().child("Messages").
                child("Help").child(Canteen).child(Uid).child("CanteenLastSeen")
                .setValue(lastseen);
        setLastSeen();
         change_status_to_seen();
        undoCounter();
        findViewById(R.id.end).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                help_activity.super.onBackPressed();
            }
        });
        TextView textView=findViewById(R.id.toolbar);
        textView.setText(UserName);
        recyclerView = findViewById(R.id.reyclerview_message_list);
        LinearLayoutManager horizontal = new LinearLayoutManager(help_activity.this, LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(horizontal);
         DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().
                child("Messages").
                child("Help").child(Canteen).child(Uid + "/Messages");
        com.example.sanjay.canteen_clint.newChatScreen.MyAdapter adapter;
        adapter = new com.example.sanjay.canteen_clint.newChatScreen.MyAdapter(album_allorders.class, R.layout.item_message_sent,
                mainactiv_allorders.FoodViewHolder.class, databaseReference);
         horizontal.setStackFromEnd(true);
      ScrollPosition(databaseReference,recyclerView,adapter,horizontal);
        recyclerView.setAdapter(adapter);
        checkMessage(databaseReference);

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        Edit_message = findViewById(R.id.edittext_chatbox);
        actionButton = findViewById(R.id.button_send_message);

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setActionButton();

            }
        });
        Edit_message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkMessageBox(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        super.onStart();

    }
    public void setLastSeen() {

      {
          FirebaseDatabase.getInstance().getReference().child("Messages").
                  child("Help").child(Canteen).child(Uid).addListenerForSingleValueEvent(new ValueEventListener() {
              @Override
              public void onDataChange(DataSnapshot dataSnapshot) {
                  long lastSeen;
                  String online;
                  try {

                      lastSeen = (long) dataSnapshot.child("USERLastSeen").getValue();
                      online = (String) dataSnapshot.child("USEROnline").getValue();
                      Log.e("MessageStatus","USERLastSeen is "+lastSeen+"Online is "+online);
                      SimpleDateFormat sfd;
                      TextView textView = findViewById(R.id.lastseen);

                      if ( lastseen == lastSeen) {
                          sfd = new SimpleDateFormat("HH:mm", Locale.UK);
                          textView.setVisibility(View.VISIBLE);
                          try {
                              textView.setText(String.format("Last seen at %s", sfd.format(new Date(Long.parseLong(online)))));
                          }catch (Exception ignored){}

                          try {
                              if (online.equalsIgnoreCase("online"))
                                  textView.setText(R.string.online);
                              else{

                                  textView.setText(String.format("Last seen at %s", sfd.format(new Date(Long.parseLong(online)))));
                              }
                          } catch (Exception e) {
                              e.printStackTrace();
                              Log.e("MessageStatus","Crash "+e.toString());
                              textView.setVisibility(View.GONE);
                          }

                      } else {
                          textView.setVisibility(View.VISIBLE);

                          sfd = new SimpleDateFormat("MMM", Locale.UK);
                          textView.setText(String.format("Last seen on %s", sfd.format(new Date(Long.parseLong(online)))));

                      }
                  }catch (Exception e){
                      Log.e("MessageStatus","Crash "+e.toString());

                      findViewById(R.id.lastseen).setVisibility(View.GONE);
                  }
              }

              @Override
              public void onCancelled(DatabaseError databaseError) {

              }
          });
      }


    }

    void receive(){
        try{
            Intent intent=getIntent();

            Uid= intent.getStringExtra("UID");
            Canteen= intent.getStringExtra("Address");
            UserName= intent.getStringExtra("UserName");
           // UserName= intent.getStringExtra("UserName");
            img=intent.getStringExtra("img");
            if (img!=null){
                Glide.with(getApplicationContext()).applyDefaultRequestOptions(RequestOptions.circleCropTransform()).load(img).into((ImageView) findViewById(R.id.mainimage));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        Log.e("Receive","Uid "+Uid+" Username"+UserName);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        isActive=true;
        onlineStatus("Online");
try {
    if (messageImage.length>0){
        Glide.with(getApplicationContext()).
                applyDefaultRequestOptions(RequestOptions.noTransformation()).
                load(uriProfileImage.getPath()).into((ImageView)
                findViewById(R.id.messageImage));
        reduceImage(messageImage,Caption);
    }
}catch (Exception ignored){}
    }

    @Override
    protected void onStop() {
        super.onStop();
        isActive=false;
        onlineStatus(String.valueOf(System.currentTimeMillis()));

    }
    @Override
    protected void onStart() {
        super.onStart();
        onlineStatus("Online");
        change_status_to_seen();
        isActive=true;
        //back = true;
        //undo_notification();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onlineStatus(String.valueOf(System.currentTimeMillis()));
        isActive=false;
    }
    void onlineStatus(String time){


            FirebaseDatabase.getInstance().getReference().child("Messages").
                    child("Help").child(Canteen).child(Uid).child("CanteenOnline")
                    .setValue(time);

    }
    @Override
    protected void onResume() {
        super.onResume();
        isActive=true;
        change_status_to_seen();
    }
    void checkMessage(DatabaseReference Address){
        Log.e("CheckMessage","Inside check message");
        Address.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()||!dataSnapshot.hasChildren()){
                    //check_messages(0);
                    Log.e("CheckMessage","Inside check message inside not found");
                    findViewById(R.id.no_messages).setVisibility(View.VISIBLE);

                }else
                    Log.e("CheckMessage","Inside check message found");
                findViewById(R.id.progressbar).setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                findViewById(R.id.progressbar).setVisibility(View.GONE);
            }
        });

    }
    void checkMessageBox(String message) {
 //.matches(".*\\w.*"
        if (!message.trim().isEmpty()) {

             Glide.with(getApplicationContext()).applyDefaultRequestOptions(RequestOptions.noTransformation()).load(R.drawable.ic_send_black_24dp).into(actionButton);
            actionButtonStatus = true;
            //actionButton.T
        }

        else {

            Glide.with(getApplicationContext()).applyDefaultRequestOptions(RequestOptions.noTransformation()).load(R.drawable.add_color).into(actionButton);
            actionButtonStatus = false;
        }




    }



     void setActionButton() {
         if (actionButtonStatus) {
            //TotalHelp(type);
            new StoreMessage("ADMIN_TEXT",Uid,(EditText) Edit_message,(RelativeLayout)findViewById(R.id.progressRelative),Edit_message.getText().toString().trim());
        } else {
            CardView cardView = findViewById(R.id.card_view);
            //showDiag();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                if (cardView.getVisibility() == View.INVISIBLE) {

                    animation(true);
                } else {
                    animation(false);
                }
            } else {
                if (cardView.getVisibility() == View.INVISIBLE) {
                    findViewById(R.id.card_view).setVisibility(View.VISIBLE);
                    showZoomIn();
                    ExtraOptions();
                } else {
                    findViewById(R.id.card_view).setVisibility(View.INVISIBLE);
                }
            }

        }

    }



    void showZoomIn() {
        Animation zoomIn =
                AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.zoom_in);
        findViewById(R.id.relateGallery).startAnimation(zoomIn);
        findViewById(R.id.relateCamera).startAnimation(zoomIn);
        findViewById(R.id.relateOrder).startAnimation(zoomIn);
        ExtraOptions();

    }

    void ExtraOptions() {
        findViewById(R.id.gallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageChooser();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    animation(false);
                } else findViewById(R.id.card_view).setVisibility(View.INVISIBLE);
            }
        });
        findViewById(R.id.camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCamera();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    animation(false);
                } else findViewById(R.id.card_view).setVisibility(View.INVISIBLE);
            }
        });
        findViewById(R.id.order).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              //  back = false;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    // animation(false);
                    findViewById(R.id.card_view).setVisibility(View.INVISIBLE);
                    openOrders(true,"");
                } else {
                    findViewById(R.id.card_view).setVisibility(View.INVISIBLE);
                    openOrders(true,"");
                }
            }
        });

    }

    void storeToStorage(String caption) {
        int Image_size;
        String scheme = uriProfileImage.getScheme();
         if (scheme.equals(ContentResolver.SCHEME_CONTENT)) {
            try {
                InputStream fileInputStream =
                        getApplicationContext().getContentResolver().openInputStream(uriProfileImage);
                if (fileInputStream != null) {
                     Image_size = fileInputStream.available() / 1024;
                    Log.e("help images", "Size is " + String.valueOf(Image_size));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (scheme.equals(ContentResolver.SCHEME_FILE)) {
            String path = uriProfileImage.getPath();
            File f;
            try {
                f = new File(path);
                Image_size = (int) f.length() / 1024;
                Log.e("help images", "Size is " + String.valueOf(Image_size));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        reduceImage(55,caption);

    }


    void reduceImage(int quality, String caption) {
        try {
            Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), uriProfileImage);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            bmp.compress(Bitmap.CompressFormat.JPEG, quality, baos);
            byte[] data = baos.toByteArray();
            Log.e("help image", " Image size is in bytes " + data.length);
            if ((data.length) / 1024 > 250) {
                Log.e("help image", " Image size is modified in bytes " + data.length);
                bmp = null;
                baos = null;
                reduceImage(quality - 15,caption);

            } else
                reduceImage(data,caption);

        } catch (Exception OutOfMemoryError) {
            Toast.makeText(this, "Failed to send image might be due to image size is more", Toast.LENGTH_SHORT).show();
            Log.e("help image", "Out of memory exception");
        }

    }


    void reduceImage(byte[] data, final String caption) {
        try {
            StorageReference profileImageRef =
                    FirebaseStorage.getInstance().getReference("Chat_Images/"+
                            Uid+"/" + System.currentTimeMillis() + ".jpg");
            findViewById(R.id.imageCard).setVisibility(View.VISIBLE);
            final UploadTask uploadTask = profileImageRef.putBytes(data);
            final ProgressBar progressBar=findViewById(R.id.progressbarsmall);
            findViewById(R.id.cancel_message).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!uploadTask.isComplete()){
                        uploadTask.cancel();
                        findViewById(R.id.imageCard).setVisibility(View.GONE);
                    }
                }
            });
            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress=(100.0*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                    int currentprogress=(int) progress;
                    Log.e("firebase messaging","Progress is "+progress+" curre "+currentprogress);
                    progressBar.setProgress(currentprogress);

                }
            });
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                      Intent intent=new Intent(help_activity.this,image_zoomer.class);
                      intent.putExtra("Image", uriProfileImage.toString());
                     intent.putExtra("From", "Camera_Image");
                     intent.putExtra("Caption", caption.toString());
                     messageImage=null;
                     findViewById(R.id.imageCard).setVisibility(View.GONE);
                     startActivity(intent);

                    Toast.makeText(help_activity.this, "Upload task completed", Toast.LENGTH_SHORT).show();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    findViewById(R.id.imageCard).setVisibility(View.GONE);
                    messageImage=null;
                    Toast.makeText(help_activity.this, "Upload task failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception OutOfMemoryError) {
            Log.e("help image", "Out of memory exception");
        }
    }




    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void animation(boolean show) {
        final CardView cardView = findViewById(R.id.card_view);

        int height = cardView.getHeight();
        int width = cardView.getWidth();
        int endRadius = (int) Math.hypot(width, height);
        int cx = (int) (actionButton.getX() + (cardView.getWidth() / 2));
        int cy = (int) (actionButton.getY()) + cardView.getHeight() + 56;

        if (show) {
            Animator revealAnimator = ViewAnimationUtils.createCircularReveal(cardView, cx, cy, 0, endRadius);
            revealAnimator.setDuration(700);
            revealAnimator.start();
            cardView.setVisibility(View.VISIBLE);
            showZoomIn();
        } else {
            Animator anim =
                    ViewAnimationUtils.createCircularReveal(cardView, cx, cy, endRadius, 0);

            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    cardView.setVisibility(View.INVISIBLE);

                }
            });
            anim.setDuration(700);
            anim.start();
        }

    }


 void ScrollPosition(RecyclerView recyclerView, long pos, RecyclerView.Adapter adapter){
        recyclerView.scrollToPosition(adapter.getItemCount()-1);
     Log.e("Scrolling","Adapter item count "+adapter.getItemCount());

 }
     void ScrollPosition(DatabaseReference Address, final RecyclerView recyclerView, final MyAdapter adapter, final LinearLayoutManager horizontal) {
         final long[] childrens = new long[1];
         FirebaseDatabase.getInstance().getReference().child("Messages").
                 child("Help").child(Canteen).child(Uid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                childrens[0] = dataSnapshot.getChildrenCount();
              //  undo_notification();
                //change_status_to_seen();
                 Log.e("scrolling", "Childs " + childrens[0]);
                 //check_messages(childrens[0]);
                //change_status_to_seen();
//                findViewById(R.id.no_messages).setVisibility(View.GONE);
//                ScrollPosition(recyclerView,dataSnapshot.getChildrenCount(),adapter);
             }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                undoCounter();
                Log.e("scrolling", "Childs String s is "+s);
            try {
                if (s.toLowerCase().contains("last"))
                {
                    try{
                        Log.e("MessageStatus", "Childs CanteenLastSeen "+
                                (long)dataSnapshot.getValue());
                    }catch (Exception e){

                    }try{
                    Log.e("MessageStatus", "Childs CanteenOnline "+
                            (String)dataSnapshot.child("CanteenOnline").getValue());
                }catch (Exception e){

                }
                    setLastSeen( );
                }
            }catch (Exception ignored){

            }
                change_status_to_seen();
                findViewById(R.id.no_messages).setVisibility(View.GONE);
                ScrollPosition(recyclerView,dataSnapshot.getChildrenCount(),adapter);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
      //  return (int) childrens[0];
    }
    void undoCounter(){
        FirebaseDatabase.getInstance().getReference().child("Messages").
                child("Help").child(Canteen).child(Uid).child("Last_message_counter").setValue("0");
    }
    private void change_status_to_seen() {
        if (isActive) {
            Log.e("MessageStatus","Activity   running active is "+isActive);

            FirebaseDatabase.getInstance().getReference().child("Messages").
                    child("Help").child(Canteen).child(Uid + "/Messages")
                    .orderByChild("Seen").equalTo(false).addListenerForSingleValueEvent(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Messages").
                            child("Help").child(Canteen).child(
                            Uid + "/Messages");
                    Log.e("MessageStatus","changing all to seen "+dataSnapshot.getChildrenCount());
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        try {

                            if (dataSnapshot1.child("From").getValue().toString().contains("USER")) {
                                Log.e("scrolling", "change seen status completed");
                                databaseReference.child(dataSnapshot1.getKey()).child("Seen").setValue(true);


                            }
                        } catch (Exception ignored) {
                        }
                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }else{
            Log.e("MessageStatus","Activity not running active is "+isActive);

        }
    }


    public   void openOrders(boolean show,String order) {

        if (show) {
            orders_dialog();

        } else {

            Edit_message.setText(String.format("%s %s ", Edit_message.getText().toString().trim(),order));
         }
    }

    private void openCamera() {

        requestPermission(this, Manifest.permission.CAMERA,OPEN_CAMERA);
        Log.e("Camera","Inside Open_camera permission");
    }

    private void showImageChooser() {

        requestPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE);

    }
     @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
     private void requestPermission(Activity context, String permission, int value)  {
        boolean hasPermission = (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(context,
                    new String[]{permission},
                    value);
        } else {
            if (value==103){Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

               startActivityForResult(intent, CHOOSE_IMAGE);

            }
          else  startActivity(new Intent(this, GallerySample.class));
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                        startActivity(new Intent(this, GallerySample.class));
                        Toast.makeText(this, "The app was allowed to read to your storage!", Toast.LENGTH_LONG).show();



                 }
                break;
            }
            case OPEN_CAMERA:
                Log.e("Camera","Inside persmission result ");
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("Camera","Inside persmission result granted");
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, CHOOSE_IMAGE);



                }
                break;
        }
    }
    void send_to_Image_viewer(Uri url){
        start_camera_dialog(url);
    }
    void start_camera_dialog(final Uri uri){

            Log.e("Camera","Inside start_camera_dialog");
            final View dialogView = View.inflate(help_activity.this, R.layout.image_viewer2, null);

        //    final Dialog dialog = new Dialog(help_activity.this);
         //   dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
          Dialog dialog = new Dialog(help_activity.this,R.style.Dialog);
//          dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        Window window=dialog.getWindow();
        if (window != null) {
            Log.e("Camera","Inside start_camera_dialog window is not null");
            window.setLayout( LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        } else{
            dialog = new Dialog(help_activity.this);
              dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            Log.e("Camera","Inside start_camera_dialog  window is null");
        }
        dialog.setContentView(dialogView);
        Glide.with(getApplicationContext()).load(uri).into((ImageView) dialogView.findViewById(R.id.imageId));

        final Dialog finalDialog = dialog;
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                    if (i == KeyEvent.KEYCODE_BACK) {
                        finalDialog.dismiss();

                        return true;
                    }

                    return false;
                }
            });


           dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

         dialogView.findViewById(R.id.send).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EditText editText=dialogView. findViewById(R.id.ImageCaption);
                    Caption= editText.getText().toString().trim();
                    storeToStorage(Caption);
                    finalDialog.dismiss();
                 }
            });
     dialog.show();
    }
     public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriProfileImage = data.getData();
            Log.e("help", "activity result successful");
              Log.e("Camera", uriProfileImage.toString());
            Bitmap bitmap = null;
            try {ImageView imageView=findViewById(R.id.messageImage);
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriProfileImage);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

            send_to_Image_viewer(uriProfileImage);

        } else {
            Log.e("help", "activity result unsuccessful reqcode" + requestCode + " resultcode " + requestCode
                    + " data ");
        }
    }

    @Override
    public void onBackPressed() {


            Intent intent=getIntent();

            if (intent.getStringExtra("From")!=null&& intent.getStringExtra("From").equals("Notification")) {
                finish();
                //startActivity(new Intent(this, fin));
                finish();
            }
              else super.onBackPressed();
    }

       void orders_dialog(){

          final View view = View.inflate(help_activity.this, R.layout.recycler_allorders2, null);

          final Dialog dialog = new Dialog(help_activity.this,R.style.Dialog);

          Window window=dialog.getWindow();
          if (window != null) {
              window.setLayout( LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
          }
          dialog.setContentView(view);
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
        view.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    //      dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        view.findViewById(R.id.cardview).setVisibility(View.VISIBLE);

       RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
          RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
          recyclerView.setLayoutManager(mLayoutManager);
          recyclerView.setItemAnimator(new DefaultItemAnimator());

          recyclerView.setAdapter(adapter);
      // FirebaseAuth mAuth = FirebaseAuth.getInstance();
      DatabaseReference mDatabase = FirebaseDatabase.getInstance().
                  getReference().child(MessageFormat.format("User Informations/{0}/Orders/All orders",
              Uid));
        Load_orders(recyclerView,mDatabase,view,dialog);
           dialog.show();
      }
      void Load_orders(RecyclerView recyclerView, DatabaseReference databaseReference, final View view, final Dialog dialog) {
          if (checkOrders(databaseReference,view,dialog)) {


              FirebaseRecyclerAdapter<album_allorders, mainactiv_allorders.FoodViewHolder> FBRA = new FirebaseRecyclerAdapter<album_allorders, mainactiv_allorders.FoodViewHolder>(
                      album_allorders.class,
                      R.layout.new_orders_layout,
                      mainactiv_allorders.FoodViewHolder.class,
                      databaseReference) {
                  @Override
                  protected void populateViewHolder(mainactiv_allorders.FoodViewHolder viewHolder, final album_allorders model, int position) {
                      try {
                            viewHolder.mView.findViewById(R.id.user_cred).setVisibility(View.GONE);
                            view.findViewById(R.id.progressFood).setVisibility(View.GONE);
                          if (model.getStatus().toUpperCase().contains("CANCEL")) {
                              viewHolder.setStatus("Cancel");

                          } else {
                              viewHolder.setStatus(model.getStatus());
                          }
                      } catch (Exception ignored) {
                          viewHolder.setStatus("N/A");
                      }
                      viewHolder.setOrderNo(model.getOrderNo());
                      viewHolder.setDelivery(model.getDelivery());


                      viewHolder.setPayment(getString(R.string.rupeesSymbol)+ model.getPayment());
                      try {
                          viewHolder.setPayment3(String.valueOf((Integer.parseInt(model.getTotalAmount()) - Integer.parseInt(model.getPayment()))));
                          viewHolder.setMore(String.valueOf((Integer.parseInt(model.getTotalFood()) - 4)));
                      } catch (Exception ignored) {
                      }           // viewHolder.setMore(String.valueOf(model.getTotalFood()));
                      viewHolder.setImage0(model.getImage0(),getApplicationContext());
                      viewHolder.setImage1(model.getImage1(),getApplicationContext());
                      viewHolder.setImage2(model.getImage2(),getApplicationContext());
                      viewHolder.setImage3(model.getImage3(),getApplicationContext());


                      viewHolder.Relative().setOnClickListener(new View.OnClickListener() {
                          @Override
                          public void onClick(View view) {
                              //   OrderNo = model.getOrderNo();
                               dialog.dismiss();
                               openOrders(false, "@Order(" + model.getOrderNo() + ")");
                          }
                      });


                  }


              };
              recyclerView.setAdapter(FBRA);

          }
      }
     boolean checkOrders(DatabaseReference address, final View view, final Dialog dialog){
        final boolean[] status = {true};
        address.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount()==0){
                    view.findViewById(R.id.nothing_found).setVisibility(View.VISIBLE);
                    status[0] =false;
                    activate_ok_listener();
                  view. findViewById(R.id.progressFood).setVisibility(View.GONE);
                }
            }

            private void activate_ok_listener() {
                view.findViewById(R.id.ok_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                      //  openOrders(false,"");
                        dialog.dismiss();

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return status[0];
    }


}

