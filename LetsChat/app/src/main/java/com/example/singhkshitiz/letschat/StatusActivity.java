package com.example.singhkshitiz.letschat;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class StatusActivity extends AppCompatActivity {

    TextInputLayout mTextInputLayout;
    Button mButtonSubmit;
    DatabaseReference mDatabaseReference;
    private FirebaseUser mCurrentUser;
    ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        //-----CHANGING TITLE AND BACK BUTTON----
        this.setTitle("Change Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProgressDialog=new ProgressDialog(this);

        mCurrentUser=FirebaseAuth.getInstance().getCurrentUser();
        String uid=mCurrentUser.getUid();

        mDatabaseReference= FirebaseDatabase.getInstance().getReference().child("users").child(uid);
        mTextInputLayout=(TextInputLayout)findViewById(R.id.textInputStatus);
        mButtonSubmit=(Button)findViewById(R.id.buttonChangeStatus);

        //-----ADDING PREVIOUS STATUS AS DEFAULT-----
        String currentStatus=getIntent().getStringExtra("current_status");
        mTextInputLayout.getEditText().setText(currentStatus);
    }
    public void buttonIsClicked(View view){

        String status=mTextInputLayout.getEditText().getText().toString();
        if(TextUtils.isEmpty(status)){
            Toast.makeText(StatusActivity.this, "Please write something...", Toast.LENGTH_SHORT).show();
            return ;
        }
        mProgressDialog.setTitle("Updating Status");
        mProgressDialog.setMessage("Please wait while status is updating..");
        mProgressDialog.setProgress(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.show();


        //------ADDING STATUS TO DATABASE----
        mDatabaseReference.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mProgressDialog.dismiss();
                if(task.isSuccessful()){
                    Toast.makeText(StatusActivity.this, "Status Updated Successfully", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(StatusActivity.this, "Status cannot be updated", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
       // mDatabaseReference.child("online").setValue("true");
    }

    @Override
    protected void onStop() {
        super.onStop();
       // mDatabaseReference.child("online").setValue(ServerValue.TIMESTAMP);

    }
}
