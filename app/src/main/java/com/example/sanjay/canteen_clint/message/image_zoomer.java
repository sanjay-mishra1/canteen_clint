package com.example.sanjay.canteen_clint.message;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
 import android.graphics.Matrix;

import android.net.Uri;
import android.os.AsyncTask;
 import android.os.Bundle;
 import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
 import android.widget.EditText;
 import android.widget.ImageView;
 import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.sanjay.canteen_clint.R;
 import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import static com.example.sanjay.canteen_clint.message.chat_window.canteen;


public class image_zoomer extends AppCompatActivity {
    private ScaleGestureDetector mScaleGestureDetector;
    private float mScaleFactor = 1.0f;
    private ImageView mImageView;
    private int _no_of_time_rotated = 90;
    Bitmap bmp = null;
    private String from="";
    public static byte[] data;
    private String Uid=chat_window.Uid;
    private String UserName;

    public image_zoomer() {
    }

    String img;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_viewer);
        mImageView = (ImageView) findViewById(R.id.imageId);
        mImageView.setDrawingCacheEnabled(true);
        Uid=chat_window.Uid;
        receive();
        Glide.with(getApplicationContext()).load(img).into(mImageView);
try{
        if (!from.contains("Message_Image") || from == null) {


            Reduce_Image reduce_image = new Reduce_Image(img, getApplicationContext(), findViewById(R.id.imageId));
            reduce_image.doInBackground();
            //  Reducing_Image();
            mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());

            //Touch touch=new Touch(getApplicationContext());
            //touch()
            //CustomView=
            RotateImage();
        }
    }catch (Exception NullPointerException){
    Reduce_Image reduce_image = new Reduce_Image(img, getApplicationContext(), findViewById(R.id.imageId));
    reduce_image.doInBackground();
    //  Reducing_Image();
    mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());

    //Touch touch=new Touch(getApplicationContext());
    //touch()
    //CustomView=
    RotateImage();
}
    }

    void reduceImage(int quality) {
        try {
            bmp = BitmapFactory.decodeFile(img);

            //  bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(img));

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            bmp.compress(Bitmap.CompressFormat.JPEG, quality, baos);
            data = baos.toByteArray();
            Log.e("help image", " Image size is in bytes " + data.length);
            if ((data.length) / 1024 > 250) {
                Log.e("help image", " Image size is modified in bytes " + data.length);
                bmp = null;
                baos = null;
                reduceImage(quality - 15);

            } else {
                Glide.with(getApplicationContext()).load(bmp).into(mImageView);

            }
            // StoreTodatabase(data);

        } catch (Exception OutOfMemoryError) {
            Glide.with(getApplicationContext()).load(bmp).into(mImageView);
            //Toast.makeText(this,"Failed to send image might be due to image size is more",Toast.LENGTH_SHORT).show();
            Log.e("help image", "Out of memory exception");
        }

    }

    void receive() {
        try {
            Intent intent = getIntent();
            img = intent.getStringExtra("Image");
            from = intent.getStringExtra("From");
            //Uid= intent.getStringExtra("UID");
            UserName= intent.getStringExtra("UserName");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void RotateImage(int rotation) {
        if (rotation >= 360)
            rotation = 0;
        _no_of_time_rotated = 90;

        try {
            Bitmap bmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            Matrix mat = new Matrix();
            mat.postRotate(rotation);
            Bitmap bmapRotate = Bitmap.createBitmap(bmap, 0, 0,
                    bmap.getWidth(), bmap.getHeight(), mat, true);
            // BitmapDrawable bmd = new BitmapDrawable(bmapRotate);
            //  mImageView.setImageBitmap(bmapRotate);
            // mImageView.setImageDrawable(bmd);
            bmp = bmapRotate;
            //   mImageView.setImageBitmap(bmp);
            Glide.with(getApplicationContext()).load(bmp).into(mImageView);

            bmap = null;
            //data=bmp.to();
            // data = bmp.toByteArray();
            mat = null;
            bmapRotate = null;
            _no_of_time_rotated = rotation + 90;
        } catch (Exception OutOfMemoryError) {
            Toast.makeText(image_zoomer.this, "Failed to send image might be due to image size is more", Toast.LENGTH_SHORT).show();
            Log.e("help image", "Out of memory exception");
        }
    }

    void RotateImage() {
        findViewById(R.id.rotate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("Image", "Rotation " + _no_of_time_rotated);
                RotateImage(_no_of_time_rotated);
            }
        });


    }


    void StoreTodatabase() {
        try {
            findViewById(R.id.progressRelative).setVisibility(View.VISIBLE);
            //  mImageView.setDrawingCacheEnabled(true);
            // mImageView.buildDrawingCache();
            // Bitmap bitmap = mImageView.getDrawingCache();
            //  ByteArrayOutputStream baos = new ByteArrayOutputStream();
            // bitmap.compress(Bitmap.CompressFormat.JPEG,100, baos);

            // data = baos.toByteArray();
            Log.e("Image Store_Progress", "Converted into bytes");
            StorageReference profileImageRef =
                    FirebaseStorage.getInstance().getReference("Chat_Images/" +canteen+"/"+Uid+ "/" + System.currentTimeMillis() + ".jpg");

            UploadTask uploadTask = profileImageRef.putBytes(data);
            Log.e("Image Store_Progress", "Upload task started");

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // ImageUrl = taskSnapshot.getDownloadUrl().toString();
                    Log.e("Image Store_Progress", "Stored into Firebase_Storage");

                    TotalHelp(taskSnapshot.getDownloadUrl().toString());
                    Toast.makeText(image_zoomer.this, "Upload task completed", Toast.LENGTH_SHORT).show();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(image_zoomer.this, "Upload task failed " + e, Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.e("help image Firebase", " Error " + e.getMessage());
        }
    }
    void TotalHelp(final String img) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat date = new SimpleDateFormat("yyMMdd");
        final String key = date.format(c.getTime());
        date = new SimpleDateFormat("MMMM d, yyyy || h:mm a");
        final String messageTime = date.format(c.getTime());
        DatabaseReference database = FirebaseDatabase.getInstance().getReference().
                child("Messages").child("Help").child(canteen).child(Uid + "/Messages");//.setValue((message)+"  Email- "+email);
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long Total;
                Total = dataSnapshot.getChildrenCount();
                try {

                    Log.e("help", "  Total Found " + Total);

                    Total += 1;
                    if (Total==1){
                        Log.e("Message time",messageTime);
                        saveMessage(img,setDate1(messageTime),key,setTime(messageTime),false,Total);

                    }else
                        getDateStatus(setDate1(messageTime),FirebaseDatabase.getInstance().
                                getReference().child("Messages").
                                child("Help").child(canteen).child(Uid + "/Keys" ),img,key,setTime(messageTime),Total);
                    //saveMessage(type);
                } catch (Exception NullPointerException) {
                    Total = 1;
                    getDateStatus(setDate1(messageTime),FirebaseDatabase.getInstance().
                            getReference().child("Messages").
                            child("Help").child(canteen).child(Uid + "/Keys" ),img,key,setTime(messageTime),Total);
                    //saveMessage(type);
                    Log.e("help", "Exception  Total Found " + Total);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void saveMessage(String img, String date, String key, String Time,boolean Status,Long Total) {
        Log.e("Firebase Messaging","Inside save message  ");

        DatabaseReference   database = FirebaseDatabase.getInstance().getReference().child("Messages").
                child("Help").child(canteen).child(Uid + "/Messages").
                child(String.valueOf(Total));

        EditText message = findViewById(R.id.ImageCaption);


         if (!Status)
        {
            storenewKey(key,date, String.valueOf(Total+1));
            database.child("From").setValue("Date");
            database.child("Date").setValue(date.toUpperCase());
            database = FirebaseDatabase.getInstance().getReference().child("Messages").
                    child("Help").child(canteen).child(Uid + "/Messages").
                    child(String.valueOf(Total+1));
            database.child("Date").setValue(date);

        }else{
            database.child("Date").setValue("");

         }
        if (message.getText().toString().trim().isEmpty())
        {  database.child("Message").setValue("");
            save_Extra_info("IMG",Time);

        }

        else{ database.child("Message").setValue(message.getText().toString().trim());
            save_Extra_info(message.getText().toString().trim(),Time);

        }
        Log.e("help", " Message Saved ");
        database.child("Food_Image").setValue(img);
        database.child("Time").setValue(Time);
        database.child("Date").setValue(date);
        database.child("Status").setValue("UNREAD");
        database.child("From").setValue("ADMIN_IMG");
        finish();

    }
    private void save_Extra_info(String Message,String Time){
try {
    final DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Messages").
            child("Help").child(canteen).child(Uid);

    //SharedPreferences shared=this.getSharedPreferences("User_Credentials", Context.MODE_PRIVATE);
    database.child("UID").setValue(Uid);
    // database.child("User_Name").setValue(shared.getString("UserName"," "));
    database.child("User_last_Message").setValue(Message);
    database.child("Time").setValue(Time);
    database.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            String counter;
            try {

                counter = (String) dataSnapshot.child("Last_message_counter").getValue();
                Log.e("Last_message_counter", "Inside try " + String.valueOf(counter));
                database.child("Last_message_counter").setValue(String.valueOf(Integer.parseInt(counter + 1)));
            } catch (Exception e) {
                Log.e("Last_message_counter", "  " + String.valueOf("1"));
                database.child("Last_message_counter").setValue("1");
            }

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    });

}catch (Exception e){}


    }

    public String setTime(String time){

        time=time.substring(time.indexOf("||")+3,time.length());
        return (time);


    }


    void storenewKey(String key,String date,String Total){
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("Messages").
                child("Help").child(canteen).child(Uid + "/Keys/"+key);
        databaseReference.child("Date").setValue(date);
        databaseReference.child("key").setValue(key);
        databaseReference.child("Total").setValue(Total);
    }
    void getDateStatus(final String date, DatabaseReference address, final String type, final String key, final String Time, final Long Total)   {
        final boolean[] status = {false};

        final Query query=address.orderByValue().limitToLast(1);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {

                    for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                    {               // album_allorders data1=dataSnapshot1 .getValue(album_allorders.class);
                        Log.e("Firebase Messaging","Inside try  "+dataSnapshot1.
                                child("Date").getValue().toString()+" and date is "+date );

                        if (date.contains(dataSnapshot1.child("Date").getValue().toString()))
                        {Log.e("Firebase Messaging","Date is equal sending to save message");

                            saveMessage(type,date,key,Time,true,Total);
                            Log.e("Firebase Messaging","Date is equal out from save message");

                        }else{                        Log.e("Firebase Messaging","Date is notequal sending save message");

                            saveMessage(type,date,key,Time,false,Total);

                        }
                    }


                }catch (Exception e){
                    Log.e("Firebase Messaging","Inside catch  " );
                    saveMessage(type,date,key,Time,false,Total);

                    status[0]=false;}
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public String setDate1(String time){

        try {
            time= time.substring(0,time.indexOf("||")-1 );
            return (String.format("  %s  ", time));
        }catch (Exception ignored){}
        return "";
    }

  /* */

    @Override
    public void onBackPressed() {
        bmp = null;
        data = null;
        mScaleGestureDetector = null;
        super.onBackPressed();
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        mScaleGestureDetector.onTouchEvent(motionEvent);
        return true;
    }

    public void Onclick_storeToFirebase(View view) {
       try {
           if (!from.contains("Message_Image")) {
               StoreTodatabase();
           } else {
               TotalHelp(img);
           }
       }catch (Exception e){  StoreTodatabase();}
    }


    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            mScaleFactor *= scaleGestureDetector.getScaleFactor();
            mScaleFactor = Math.max(1.f,
                    Math.min(mScaleFactor, 10.0f));
            mImageView.setScaleX(mScaleFactor);
            mImageView.setScaleY(mScaleFactor);
            return true;
        }
    }


    public static class Reduce_Image extends AsyncTask<Void, Integer, String> {


        private Bitmap bmp;
        private String img;
        private Context context;
        private View view;


        public Reduce_Image(  String img, Context context, View view) {
             this.img = img;
            this.context = context;
            this.view = view;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // before executing doInBackground
            // update your UI
            // exp; make progressbar visible
        }

        @Override
        protected String doInBackground(Void... params) {
            Log.e("Reducing Image","Background task started");

            Reducing_Image();

            return null;
        }

        void Reducing_Image() {
            int size;
            Log.e("Reducing Image","Inside Reducing Image");

            size = ImageSize();
            if (size <= 4000) {
                reduceImage(55);
            } else if (size <= 5000) {
                reduceImage(70);
            } else if (size <= 6000) {
                reduceImage(85);
            }
        }

        int ImageSize() {
            int Image_size = 0;
            //  String scheme = img.getScheme();

            {
                try {
                    InputStream fileInputStream =
                            context.getContentResolver().openInputStream(Uri.parse(img));
                    if (fileInputStream != null) {
                        //      dataSize = fileInputStream.available();
                        Image_size = fileInputStream.available() / 1024;
                        Log.e("help images", "Size is " + String.valueOf(Image_size));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //System.out.println("File size in bytes"+dataSize);

            }


            return Image_size;

        }

        void reduceImage(int quality) {
            try {   Log.e("Reducing Image","Inside Reduce Image found quality "+quality);

                bmp = BitmapFactory.decodeFile(img);

                //  bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(img));

                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                bmp.compress(Bitmap.CompressFormat.JPEG, quality, baos);
                data = baos.toByteArray();
                Log.e("Reducing Image", " Image size is in bytes " + data.length);
                if ((data.length) / 1024 > 250) {
                    Log.e("Reducing Image", " Image size is modified in bytes " + data.length);
                    bmp = null;
                    baos = null;
                    reduceImage(quality - 15);

                } else {
                    Glide.with(context).load(bmp).into((ImageView) view);

                }
                // StoreTodatabase(data);

            } catch (Exception OutOfMemoryError) {
                Glide.with(context).load(bmp).into((ImageView) view);
                //Toast.makeText(this,"Failed to send image might be due to image size is more",Toast.LENGTH_SHORT).show();
                Log.e("Reducing Image", "Out of memory exception");
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            // back to main thread after finishing doInBackground
            // update your UI or take action after
            // exp; make progressbar gone
            Log.e("Reducing Image", "Background task completed");

            Glide.with(context).load(bmp).into((ImageView) view);

        }
    }
}