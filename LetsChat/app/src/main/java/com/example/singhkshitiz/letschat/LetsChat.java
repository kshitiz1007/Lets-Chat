package com.example.singhkshitiz.letschat;

import android.app.Activity;
import android.app.Application;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

/**
 * Created by HP on 3/21/2018.
 */

public class LetsChat extends Application{

    private DatabaseReference mUserDatabase;
    private FirebaseAuth mAuth;


    @Override
    public void onCreate() {
        super.onCreate();

        mAuth=FirebaseAuth.getInstance();
        FirebaseUser mUser=mAuth.getCurrentUser();

        //---WHENEVER USER IS NOT LOGGED IN THIS FEATURE WILL NOT WORK---
        //---DO IT WHENEVER YOU GET TIME---
        if(mUser!=null){

            //---FIREBASE OFFLINE FEATURE---
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);

            // ---PIACSSO OFFLINE FEATURE--
            mAuth=FirebaseAuth.getInstance();
            Picasso.Builder builder = new Picasso.Builder(this);
            builder.downloader(new OkHttpDownloader(this,Integer.MAX_VALUE));
            Picasso built = builder.build();
            built.setIndicatorsEnabled(true);
            built.setLoggingEnabled(true);
            Picasso.setSingletonInstance(built);

            //String user_id = mAuth.getCurrentUser().getUid();
           // Log.e("Current user inside : ",user_id);

            //---SEETING TIME_STAMP ON DISCONNECT-----
           mUserDatabase = FirebaseDatabase.getInstance().
                   getReference().child("users").child(mAuth.getCurrentUser().getUid());
           mUserDatabase.addValueEventListener(new ValueEventListener() {
               @Override
               public void onDataChange(DataSnapshot dataSnapshot) {
                   if(dataSnapshot!=null){

                       mUserDatabase.child("online").onDisconnect().setValue(ServerValue.TIMESTAMP);

                   }

               }

               @Override
               public void onCancelled(DatabaseError databaseError) {

               }
           });




       }

    }



}
