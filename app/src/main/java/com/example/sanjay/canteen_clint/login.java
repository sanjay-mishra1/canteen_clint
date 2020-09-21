package com.example.sanjay.canteen_clint;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;
import java.util.Vector;

public class login extends AppCompatActivity implements View.OnClickListener{
    FirebaseAuth mAuth;
    EditText editTextEmail, editTextPassword;
    ProgressBar progressBar;
    SharedPreferences shared;
    public static final String MYPREFERENCES="MyPrefs";
    private Map<Integer,String> canteen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         shared=this.getSharedPreferences(MYPREFERENCES,Context.MODE_PRIVATE);
        setContentView(R.layout.newlogin);

        mAuth = FirebaseAuth.getInstance();

        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);

//        findViewById(R.id.textViewSignup).setOnClickListener(this);
        findViewById(R.id.buttonLogin).setOnClickListener(this);
        if (mAuth.getCurrentUser() != null) {
            finish();
            String Name= shared.getString("Canteen","not available");
            if (Name.equalsIgnoreCase("admin")&&mAuth.getCurrentUser().getUid().equalsIgnoreCase(getString(R.string.uid))){
                //    setContentView(R.layout.newhome);
                startActivity(new Intent(login.this, com.example.sanjay.canteen_clint.extra_classes.Home.class));
            }else{
               startActivity( new Intent(login.this, Home.class));


            }

          //  startActivity(new Intent(login.this, Home.class));

        }
        else{
              shared=this.getSharedPreferences(MYPREFERENCES, Context.MODE_PRIVATE);
              //String defualtValue=getResources().getString(Integer.parseInt("Email"));
             String email=shared.getString("Email","");
             String password=shared.getString("Password","");
             if(email.isEmpty() ){

                  //userLogin();


             } else
             {automaticLogin(email,password);
              }
        }


    }
void automaticLogin(final String em, final String pwd){

    mAuth.signInWithEmailAndPassword(em, pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
          Intent intent;

        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            progressBar.setVisibility(View.GONE);
            if (task.isSuccessful()) {
                shared=getSharedPreferences(MYPREFERENCES, Context.MODE_PRIVATE);
                String Name=shared.getString("Canteen","not available");
                if (Name.equalsIgnoreCase("admin")&&mAuth.getCurrentUser().getUid().equalsIgnoreCase(getString(R.string.uid))){
                //    setContentView(R.layout.newhome);
                    intent = new Intent(login.this, com.example.sanjay.canteen_clint.extra_classes.Home.class);
                }else{
                    intent = new Intent(login.this, Home.class);


                }

                SharedPreferences.Editor editor=shared.edit();
                editor.putString("Email",em);
                editor.putString("Password",pwd);
                editor.apply();
                finish();
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    });
}

    private void userLogin() {
        final String email = editTextEmail.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();
        if (email.isEmpty()) {
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please enter a valid email");
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            editTextPassword.setError("Minimum length of password should be 6");
            editTextPassword.requestFocus();
            return;
        }
        findViewById(R.id.buttonLogin).setVisibility(View.INVISIBLE);

        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    checkuser(email,password);
                } else {
                    findViewById(R.id.buttonLogin).setVisibility(View.VISIBLE);
                    findViewById(R.id.progressbar).setVisibility(View.GONE);

                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void checkuser(final String email, final String password){
         final String uid=FirebaseAuth.getInstance().getUid();
        final boolean[] check = {false};
        if (uid.equals(getString(R.string.uid))){
           SharedPreferences.Editor editor = shared.edit();
           editor.putString("Email", email);
           editor.putString("Password", password);
            editor.putString("Canteen", "Admin");
             editor.putString("CanteenImage",  getResources().getDrawable(R.drawable.acceptedstatus_background).toString());
            editor.putString("Name", "Admin");
            editor.putString("UserImage",getResources().getDrawable(R.drawable.admin).toString() );

            editor.apply();
            Intent intent = new Intent(login.this, com.example.sanjay.canteen_clint.extra_classes.Home.class);
            finish();
           intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
           startActivity(intent);
       }else {


           FirebaseDatabase.getInstance().getReference("Canteen").addListenerForSingleValueEvent(new ValueEventListener() {
               @Override
               public void onDataChange(DataSnapshot dataSnapshot) {
                   String  key;
                   for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()) {


                       try {key=dataSnapshot1.getKey();
                           Log.e("Key",key);
                           if (dataSnapshot.child(key+"/UserId").getValue().toString().equals(uid)) {
                               Log.e("key",uid+" user "+dataSnapshot.child(key+"/UserId").getValue().toString());
                                check[0] =true;
                               SharedPreferences.Editor editor = shared.edit();
                               editor.putString("Email", email);
                               editor.putString("Password", password);
                               String builder;
                               builder= (String) dataSnapshot.child(key+"/Canteen").getValue();
                               if (builder!=null)
                               editor.putString("Canteen", String.valueOf(builder));

                               builder= (String) dataSnapshot.child(key+"/CanteenImage").getValue();
                               if (builder!=null)
                                editor.putString("CanteenImage",  String.valueOf(builder));

                               builder= (String) dataSnapshot.child(key+"/User_name").getValue();
                               if (builder!=null)
                               editor.putString("Name",   String.valueOf(builder) );

                               builder= (String) dataSnapshot.child(key+"/User_Img").getValue();

                               if (builder!=null)
                               editor.putString("UserImage", String.valueOf(builder) );


                                editor.apply();
                                progressBar.setVisibility(View.GONE);
                               Intent intent = new Intent(login.this, Home.class);
                               finish();
                               intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                               startActivity(intent);
                           }
                       } catch (Exception e) {
                           progressBar.setVisibility(View.GONE);
                            findViewById(R.id.buttonLogin).setVisibility(View.VISIBLE);
                           FirebaseAuth.getInstance().signOut();
                       }

                   }
                   if (!check[0]) {
                       Toast.makeText(login.this, "Email address not found. Please contact the mcafe team", Toast.LENGTH_SHORT).show();
                       findViewById(R.id.progressbar).setVisibility(View.GONE);
                       findViewById(R.id.buttonLogin).setVisibility(View.VISIBLE);
                       FirebaseAuth.getInstance().signOut();
                   }
               }

               @Override
               public void onCancelled(DatabaseError databaseError) {
                   progressBar.setVisibility(View.GONE);


               }
           });

       }

    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {


            case R.id.buttonLogin:
                userLogin();
                break;
        }
    }


    public void forgetpasswordclicked(View view) {
        Toast.makeText(login.this, "Checking your email address", Toast.LENGTH_SHORT).show();
        final ProgressBar progressDialog=findViewById(R.id.progressbar);
        findViewById(R.id.buttonLogin).setVisibility(View.INVISIBLE);
        if (getemail().isEmpty()) {
            findViewById(R.id.buttonLogin).setVisibility(View.VISIBLE);
            progressDialog.setVisibility(View.GONE);

            Toast.makeText(login.this, "Please enter the email address", Toast.LENGTH_SHORT).show();
            return;
        }
        FirebaseAuth.getInstance().sendPasswordResetEmail(getemail()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.setVisibility(View.GONE);
                findViewById(R.id.buttonLogin).setVisibility(View.VISIBLE);

                if (task.isSuccessful()) {

                    Toast.makeText(login.this, "Email sent", Toast.LENGTH_SHORT).show();
                }
                else{
                     Toast.makeText(login.this,"Email doesn't exist",Toast.LENGTH_SHORT).show();

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.setVisibility(View.GONE);
                findViewById(R.id.buttonLogin).setVisibility(View.VISIBLE);
                Toast.makeText(login.this,e.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });
    }

    public String getemail() {
        EditText editText=findViewById(R.id.editTextEmail);
        return editText.getText().toString().trim();
    }
}

