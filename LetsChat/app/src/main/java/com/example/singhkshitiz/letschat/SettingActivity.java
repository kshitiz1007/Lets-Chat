package com.example.singhkshitiz.letschat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingActivity extends AppCompatActivity {

    private CircleImageView mCircleImageView;
    private TextView mdisplayName;
    private TextView mstatus;
    private Button mchangeImage,mchangeStatus;

    private FirebaseUser mFirebaseUser;

    private DatabaseReference mDatabaseReference;

    private StorageReference mStorageReference;

    String status="";

    //-------GETTING ITEM NO FOR IMAGE-------
    private static final int GALLERY_PICK = 1;
    String uid;
    ProgressDialog mProgressDialog;
    byte[] thumb_bytes=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mCircleImageView=(CircleImageView)findViewById(R.id.displayimage);
        mdisplayName=(TextView)findViewById(R.id.textViewDisplayname);
        mstatus=(TextView)findViewById(R.id.textViewStatus);
        mchangeImage=(Button)findViewById(R.id.buttonChangeImage);
        mchangeStatus=(Button)findViewById(R.id.buttonChangeStatus);
        mProgressDialog=new ProgressDialog(this);

        mFirebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        uid=mFirebaseUser.getUid();

        mDatabaseReference= FirebaseDatabase.getInstance().getReference().child("users").child(uid);
        mDatabaseReference.keepSynced(true);

        mStorageReference = FirebaseStorage.getInstance().getReference();


        //--------ADDING VIEW-------
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name =(String) dataSnapshot.child("name").getValue();
                status=(String)dataSnapshot.child("status").getValue();
                final String image=(String)dataSnapshot.child("image").getValue();
                String thumb=(String)dataSnapshot.child("thumb_image").getValue();

                mdisplayName.setText(name);
                mstatus.setText(status);


                if(!image.equals("default"))
                   // Picasso.with(SettingActivity.this).load(image).placeholder(R.drawable.user_img).into(mCircleImageView);
                    //----OFFLINE FEATURE-----
                    Picasso.with(SettingActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE).
                            placeholder(R.drawable.user_img).into(mCircleImageView, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(SettingActivity.this).load(image).placeholder(R.drawable.user_img).into(mCircleImageView);
                        }
                    });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
   public void buttonIsClicked(View view){

       switch(view.getId()){

           //------CHANGING IMAGE------
           case R.id.buttonChangeImage:

               Intent galleryIntent=new Intent();
               galleryIntent.setType("image/*");
               galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
               startActivityForResult(Intent.createChooser(galleryIntent,"Select Image"),GALLERY_PICK);
               break;

           //------CHANGING STATUS------
           case R.id.buttonChangeStatus:
               Intent intent =new Intent(SettingActivity.this,StatusActivity.class);
               intent.putExtra("current_status",status);
               startActivity(intent);

               break;

           default:
               break;

       }
   }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        //-----STARTING GALLERY----
        if(requestCode == GALLERY_PICK && resultCode == RESULT_OK){

            Uri sourceUri = data.getData();

            //-------CROPPING IMAGE AND SETTING MINIMUM SIZE TO 500 , 500------
            CropImage.activity(sourceUri).
                    setAspectRatio(1,1).
                    setMinCropWindowSize(500,500).
                    start(SettingActivity.this);

        }

        //------START CROP IMAGE ACTIVITY------
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE ) {

            //------CROP IMAGE RESULT------
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                mProgressDialog.setTitle("Uploading Image");
                mProgressDialog.setMessage("Please wait while we process and upload the image...");
                mProgressDialog.setCancelable(false);
                mProgressDialog.setProgress(ProgressDialog.STYLE_SPINNER);
                mProgressDialog.show();


                Uri resultUri = result.getUri();
                File thumb_filepath = new File(resultUri.getPath());
                try {

                    //--------COMPRESSING IMAGE--------
                    Bitmap thumb_bitmap = new Compressor(this).
                            setMaxWidth(200).
                            setMaxHeight(200).
                            setQuality(75).
                            compressToBitmap(thumb_filepath);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                     thumb_bytes= baos.toByteArray();


                } catch (IOException e) {
                    e.printStackTrace();
                }

                StorageReference filepath=mStorageReference.child("profile_image").child(uid+".jpg");
                final StorageReference thumb_file_path=mStorageReference.child("profile_image").child("thumbs").child(uid+".jpg");

                //------STORING IMAGE IN FIREBASE STORAGE--------
                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if(task.isSuccessful()){

                            @SuppressWarnings("VisibleForTests")
                           final String downloadUrl=  task.getResult().getDownloadUrl().toString();
                            UploadTask uploadTask = thumb_file_path.putBytes(thumb_bytes);

                            //---------- STORING THUMB IMAGE INTO STORAGE REFERENCE --------
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {
                                    @SuppressWarnings("VisibleForTests")
                                    String thumb_download_url=thumb_task.getResult().getDownloadUrl().toString();
                                    if(thumb_task.isSuccessful()){
                                        Map update_HashMap=new HashMap();
                                        update_HashMap.put("image",downloadUrl);
                                        update_HashMap.put("thumb_image",thumb_download_url);

                                        //--------ADDING URL INTO DATABASE REFERENCE--------
                                        mDatabaseReference.updateChildren(update_HashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if(task.isSuccessful()){
                                                    mProgressDialog.dismiss();
                                                    Toast.makeText(SettingActivity.this, "Uploaded Successfuly...", Toast.LENGTH_SHORT).show();

                                                }
                                                else{
                                                    mProgressDialog.dismiss();
                                                    Toast.makeText(getApplicationContext(), " Image is not uploading...", Toast.LENGTH_SHORT).show();

                                                }

                                            }
                                        });

                                    }
                                    else{
                                        mProgressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), " Error in uploading Thumbnail..", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });


                        }
                        else{
                            mProgressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), " Image is not uploading...", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();
            }
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        //mDatabaseReference.child("online").setValue("true");
    }

    @Override
    protected void onStop() {
        super.onStop();
        //mDatabaseReference.child("online").setValue(ServerValue.TIMESTAMP);

    }

}
