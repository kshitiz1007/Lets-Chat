package com.example.singhkshitiz.letschat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.app.ActionBar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    FirebaseAuth mauth;
    TextInputLayout etdisplayname,etemail,etpassword;
    Button buttonsubmit;
    ProgressDialog progressDialog;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //this.setTitle("Register");

        etdisplayname=(TextInputLayout)findViewById(R.id.editText3);
        etemail=(TextInputLayout)findViewById(R.id.editText4);
        etpassword=(TextInputLayout)findViewById(R.id.editText5);
        buttonsubmit=(Button)findViewById(R.id.button3);
        progressDialog=new ProgressDialog(RegisterActivity.this);

        mauth=FirebaseAuth.getInstance();
        mDatabase=FirebaseDatabase.getInstance().getReference().child("users");
    }

    //-----REGISTER BUTTON IS PRESSED---
    public void buttonIsClicked(View view){

        if(view.getId()==R.id.button3){

            String displayname=etdisplayname.getEditText().getText().toString().trim();
            String email=etemail.getEditText().getText().toString().trim();
            String password=etpassword.getEditText().getText().toString().trim();

            //----CHECKING THE EMPTINESS OF THE EDITTEXT-----
            if(displayname.equals("")){
                Toast.makeText(RegisterActivity.this, "Please Fill the name", Toast.LENGTH_SHORT).show();
                return;
            }

            if(email.equals("")){
                Toast.makeText(RegisterActivity.this, "Please Fill the email", Toast.LENGTH_SHORT).show();
                return ;
            }

            if(password.length()<6){
                Toast.makeText(RegisterActivity.this, "Password is too short", Toast.LENGTH_SHORT).show();
                return;
            }

            progressDialog.setTitle("Registering User");
            progressDialog.setMessage("Please wait while we are creating your account... ");
            progressDialog.setCancelable(false);
            progressDialog.setProgress(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();
            register_user(displayname,email,password);
        }
    }


    //-----REGISTERING THE NEW USER------
    private void register_user(final String displayname, String email, String password) {

        mauth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                //------IF USER IS SUCCESSFULLY REGISTERED-----
                if(task.isSuccessful()){

                    FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                    final String uid=current_user.getUid();
                    String token_id = FirebaseInstanceId.getInstance().getToken();
                    Map userMap=new HashMap();
                    userMap.put("device_token",token_id);
                    userMap.put("name",displayname);
                    userMap.put("status","Hello Kshitiz");
                    userMap.put("image","default");
                    userMap.put("thumb_image","default");
                    userMap.put("online","true");

                    mDatabase.child(uid).setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task1) {
                            if(task1.isSuccessful()){

                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "New User is created", Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(RegisterActivity.this,MainActivity.class);

                                //----REMOVING THE LOGIN ACTIVITY FROM THE QUEUE----
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();



                            }
                            else{

                                Toast.makeText(RegisterActivity.this, "YOUR NAME IS NOT REGISTERED... MAKE NEW ACCOUNT-- ", Toast.LENGTH_SHORT).show();

                            }

                        }
                    });


                }
                //---ERROR IN ACCOUNT CREATING OF NEW USER---
                else{
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "ERROR REGISTERING USER....", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
