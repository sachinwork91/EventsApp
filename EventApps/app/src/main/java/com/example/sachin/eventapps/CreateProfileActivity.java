package com.example.sachin.eventapps;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreateProfileActivity extends AppCompatActivity {



    private static final int GALLERY_REQ = 2;
    Uri mImageUri;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    StorageReference storageReference;
    private FirebaseDatabase database;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mDatabaseUsers;

    private CircleImageView profileImage;
    private TextView profileName;

    private TextView profileEmail;
    private TextView profilePhoneNo;
    private TextView profileLocation;
    private String imageUri;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        profileImage = (CircleImageView) findViewById(R.id.profile_image);


        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText( CreateProfileActivity.this, " Image is Clicked" , Toast.LENGTH_SHORT ).show();

                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("Image/*");
                startActivityForResult(galleryIntent, GALLERY_REQ);
            }
        });


        profileImage=(CircleImageView) findViewById(R.id.profile_image);
        profileEmail = (TextView) findViewById(R.id.profileemail);
        profileName = (TextView) findViewById(R.id.profilename);
        profilePhoneNo = (TextView) findViewById(R.id.profilePhoneNumber);
        profileLocation = (TextView) findViewById(R.id.profileLocation);



        //Setting up the Firebase details
        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
   //     databaseReference = database.getInstance().getReference().child("LearnWithMeUserProfiles");
        mCurrentUser = mAuth.getCurrentUser();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //We are able to get the image from gallery correctly
        if(requestCode == GALLERY_REQ && resultCode == RESULT_OK){

            Uri imageUri = data.getData();

            //Writing the Image Uri To Shared Preference
            SharedPreferences shre = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor edit=shre.edit();
            Log.d("Test","Create Profile Activity.... => "+ imageUri.toString());
            edit.putString("imagePath",imageUri.toString());
            edit.commit();

            // profileImage.setImageURI(imageUri);
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }




        if( requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE )
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode==RESULT_OK){
                mImageUri = result.getUri();
                profileImage.setImageURI(mImageUri);

            }else if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception error = result.getError();
            }
        }

    }


    public void onProfileSave(View view) {

        final String  name = profileName.getText().toString();
        final String  email = profileEmail.getText().toString();
        final String  phoneNo = profilePhoneNo.getText().toString();
        final String  location = profileLocation.getText().toString();
        Toast.makeText(CreateProfileActivity.this,  " BUtton Clicled " , Toast.LENGTH_SHORT).show();
        StorageReference filepath = storageReference.child("UserProfileImage").child(mImageUri.getLastPathSegment());

        filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
             public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                final Uri downloadurl = taskSnapshot.getDownloadUrl();
                Toast.makeText(CreateProfileActivity.this, "Upload Complete ", Toast.LENGTH_SHORT).show();
               // final DatabaseReference newPost = databaseReference.push();
                mDatabaseUsers.child("name").setValue(name);
                mDatabaseUsers.child("email").setValue(email);
                mDatabaseUsers.child("phoneNo").setValue(phoneNo);
                mDatabaseUsers.child("location").setValue(location);
                mDatabaseUsers.child("profileimage").setValue(downloadurl.toString());
                mDatabaseUsers.child("uid").setValue(mCurrentUser.getUid());


                Intent intent = new Intent(CreateProfileActivity.this, UserProfile.class);
                startActivity(intent);
               /* mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        newPost.child("name").setValue(name);
                        newPost.child("email").setValue(email);
                        newPost.child("phoneNo").setValue(phoneNo);
                        newPost.child("location").setValue(location);
                        newPost.child("profileimage").setValue(downloadurl.toString());
                        newPost.child("uid").setValue(mCurrentUser.getUid());

                        newPost.child("username").setValue(dataSnapshot.child("name").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {

                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                //If the task is successful start the new activity
                                if(task.isSuccessful()){
                                    Intent userProfileIntent = new Intent(CreateProfileActivity.this, UserProfile.class);
                                    String key = newPost.getRef().getKey();

                                    SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPref.edit();
                                    editor.putString ("UserProfileKey" , key);
                                    editor.commit();


                                    Log.d("Test", "CPA ref : "+ key);
                                    Log.d("Test", "NEW POST  ref : "+ newPost.getRef().getKey());
                                    Toast.makeText(CreateProfileActivity.this, "CPA ref" , Toast.LENGTH_SHORT).show();
                                    userProfileIntent.putExtra("UserProfileKey" , key );
                                    startActivity(userProfileIntent);
                                }
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                */

            }
        });




    }
}
