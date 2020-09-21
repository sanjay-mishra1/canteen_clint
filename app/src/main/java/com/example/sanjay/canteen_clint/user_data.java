package com.example.sanjay.canteen_clint;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class user_data extends AppCompatActivity implements View.OnClickListener {
    //FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private TextView userInfo;
    private Button buttonLogout,save;
    EditText Username,phone,collegeId;
    ProgressBar progressBar;
    private FirebaseAuth mAuth;

    private ImageView imageView;
    //Uri uriProfileImage;
    //String profileImageUrl;
    //private static final int CHOOSE_IMAGE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_data);
       // firebaseAuth = FirebaseAuth.getInstance();
        mAuth = FirebaseAuth.getInstance();

        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        databaseReference= FirebaseDatabase.getInstance().getReference().child("User Informations");
        //imageView=findViewById(R.id.user);
        Username = (EditText) findViewById(R.id.name);
        //imageView = (ImageView) findViewById(R.id.imageView);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
         userInfo= (TextView) findViewById(R.id.user);
         phone= (EditText) findViewById(R.id.mobile);
         collegeId=(EditText) findViewById(R.id.clgid);
        save=findViewById(R.id.saves);
//FirebaseUser user=firebaseAuth.getCurrentUser();
       // userInfo.setText("Welcome "+user.getEmail());
        save.setOnClickListener(this);

        //loadUserInformation();

    }

//    @Override

    private void registerUser(String email,String password) {
      //  String email = editTextEmail.getText().toString().trim();
       // String password = editTextPassword.getText().toString().trim();



        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    finish();
                    Toast.makeText(getApplicationContext(), "Sucessfully registered", Toast.LENGTH_SHORT).show();

                } else {

                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(getApplicationContext(), "You are already registered", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

    }



    private void saveUserInformation()
            {      Bundle b = this.getIntent().getExtras();
                String email = b.getString("email");
                String password = b.getString("password");
                Toast.makeText(this,"Email "+email+" Password "+password,Toast.LENGTH_SHORT).show();
                String name=Username.getText().toString().trim();
                if (name==null) {
                    Username.setError("Invalid Name");
                    Username.requestFocus();
                    return;
                }
                String college=collegeId.getText().toString().trim();
                if (college==null) {
                    collegeId.setError("Invalid Colleger Id");
                    collegeId.requestFocus();
                    return;
                }
                String number=phone.getText().toString().trim();
                if (phone==null ) {
                    phone.setError("Invalid Phone Number");
                    phone.requestFocus();
                    return;
                }
                if (phone.length()>11 ) {
                    phone.setError("Invalid Phone Number");
                    phone.requestFocus();
                    return;
                }
                registerUser(email,password);
                UserInformations userInformation=new UserInformations(name,college,number);
              //  databaseReference.addO
                FirebaseUser user=mAuth.getCurrentUser();
                databaseReference.child(number).setValue(userInformation);
                ///////////////here//////////////////
                Toast.makeText(this,"Save",Toast.LENGTH_LONG).show();
                finish();
                startActivity(new Intent(this,Home.class));
            }





    @Override
    public void onClick(View view) {


if (view==save){
           saveUserInformation();


}
    }
}
