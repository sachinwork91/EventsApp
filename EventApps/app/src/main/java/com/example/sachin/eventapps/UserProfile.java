package com.example.sachin.eventapps;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfile extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private String user_profile_key= null;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    StorageReference storageReference;
    private FirebaseDatabase database;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mDatabaseUsers;

    private CircleImageView profileImage;
    private TextView email;
    private TextView phoneno;
    private TextView location;
    private TextView university;
    private TextView resume;


    private  String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        email= (TextView) findViewById(R.id.userProfileemail);
        phoneno= (TextView) findViewById(R.id.userProfileNumber);
        location= (TextView) findViewById(R.id.userProfileLocation);
        university= (TextView) findViewById(R.id.userProfileUniversity);
        profileImage = (CircleImageView) findViewById(R.id.userProfileImage);


        Intent intent = getIntent();
        //Reading the Image from Shared preference
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String imagePath =  preferences.getString("imagePath",null);
        Uri imageUri =  Uri.parse(imagePath);
        profileImage.setImageURI(imageUri);


        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        mCurrentUser = mAuth.getCurrentUser();
        user_profile_key=mCurrentUser.getUid();

        userID = user.getUid();

        Toast.makeText(UserProfile.this," USER ID FROM USER PROF :" + userID, Toast.LENGTH_SHORT ).show();

        mDatabase= FirebaseDatabase.getInstance().getReference().child("Users");

        mDatabase.child(user_profile_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                Log.d("Test","eamil ..." + dataSnapshot.child("email").getValue());
                fillData(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }


    private void fillData(DataSnapshot dataSnapshot){
        email.setText(dataSnapshot.child("email").getValue()+"");
        phoneno.setText(dataSnapshot.child("phoneNo").getValue()+"");
        location.setText(dataSnapshot.child("location").getValue()+"");
        //    Picasso.with(UserProfile.this).load(dataSnapshot.child("profileimage").getValue()+"").into(profileImage);
        // for(DataSnapshot ds : dataSnapshot.getChildren()){
        //    ds.child().get

        // }

    }


    public void galleryClicked(View view) {

     //   Intent intent= new Intent(UserProfile.this, CatActivity.class);
     //  startActivity(intent);

    }
}

