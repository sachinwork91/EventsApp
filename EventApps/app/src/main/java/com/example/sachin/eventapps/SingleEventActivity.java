package com.example.sachin.eventapps;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import static java.lang.System.load;

public class SingleEventActivity extends AppCompatActivity {

    private String post_key= null;
    private DatabaseReference mDatabase;
    private ImageView singlePostImage;
    private TextView singlePostTitle;
    private TextView singlePostDesc;
    private DatabaseReference mEventPostDatabase;
    private DatabaseReference mReadEventPostDatabase;


    //add Firebase Database stuff
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private RecyclerView recyclerView;
    private TextView emptyView;
    private Button addEventPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_event);

        recyclerView = (RecyclerView) findViewById(R.id.event_post_list_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        emptyView = (TextView) findViewById(R.id.empty_view);
        addEventPost = (Button) findViewById(R.id.addEventPost);



        post_key = getIntent().getExtras().getString("eventId");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("EventData");
        mEventPostDatabase = FirebaseDatabase.getInstance().getReference().child("EventPostData").child(post_key);
        mReadEventPostDatabase = FirebaseDatabase.getInstance().getReference().child("EventPostData");


        singlePostImage= (ImageView) findViewById(R.id.singleImageView);
        singlePostTitle= (TextView) findViewById(R.id.singleTitle);
        singlePostDesc= (TextView) findViewById(R.id.singleDesc);


        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()==null){

                    Intent intent = new Intent(SingleEventActivity.this, Registration.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK );
                    startActivity(intent);
                }
            }
        };

        mDatabase.child(post_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String post_title= (String) dataSnapshot.child("title").getValue();
                String post_desc= (String) dataSnapshot.child("desc").getValue();
                String post_image = (String) dataSnapshot.child("image").getValue();
             //   String post_uid= (String) dataSnapshot.child("uid").getValue();

                singlePostTitle.setText(post_title);
                singlePostDesc.setText(post_desc);
                Picasso.with(SingleEventActivity.this).load(post_image).into(singlePostImage);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void onAddEvenPostClicked(View view) {
      //  Toast.makeText(SingleEventActivity.this, "Button Clicked", Toast.LENGTH_SHORT).show();
       final  View dialogView = (LayoutInflater.from(SingleEventActivity.this)).inflate(R.layout.event_post_dialog, null);



        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(SingleEventActivity.this);
        alertBuilder.setView(dialogView);
        alertBuilder.setCancelable(true).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(SingleEventActivity.this, "Post Added ", Toast.LENGTH_SHORT).show();
                EditText userAns = (EditText) dialogView.findViewById(R.id.interested) ;
                EditText university = (EditText) dialogView.findViewById(R.id.dialog_university) ;
                EditText email = (EditText) dialogView.findViewById(R.id.dialog_email) ;


                mEventPostDatabase.child("interested").setValue( userAns.getText().toString());
                mEventPostDatabase.child("university").setValue( university.getText().toString());
                mEventPostDatabase.child("email").setValue( email.getText().toString());
            }
        });

        Dialog dialog= alertBuilder.create();
        dialog.show();
    }



    @Override
    public void onStart() {


        super.onStart();

        Log.d("Test", "OnStart Called ....");

        mAuth.addAuthStateListener(mAuthListener);

        FirebaseRecyclerAdapter<EventPost, EventPostViewHolder> FBRA = new FirebaseRecyclerAdapter<EventPost, EventPostViewHolder>(
                EventPost.class,
                R.layout.carview_post_in_events,
                EventPostViewHolder.class,
                mReadEventPostDatabase


        ) {
            @Override
            protected void populateViewHolder(EventPostViewHolder viewHolder, EventPost model, int position) {
                final String post_key= getRef(position).getKey().toString();
                Log.d("Test", "Data written in List Called ....");
                viewHolder.setInterested(model.getInterested());
                viewHolder.setUniversity(model.getUniversity());
                viewHolder.setEmail(model.getEmail());
            }
        };

        recyclerView.setAdapter(FBRA);



    }
    public static class EventPostViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public EventPostViewHolder(View itemView){
            super(itemView);
            mView = itemView;
        }


        public void setInterested(String title){
            TextView interested_yn= (TextView) mView.findViewById(R.id.post_title);
            interested_yn.setText(title);
        }

        public void setUniversity(String university){
            TextView university_tv= (TextView) mView.findViewById(R.id.post_university);
            university_tv.setText(university);
        }

        public void setEmail(String email){
            TextView email_tv= (TextView) mView.findViewById(R.id.post_email);
            email_tv.setText(email);
        }
    }

}
