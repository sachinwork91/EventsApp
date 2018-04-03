package com.example.sachin.eventapps;

import android.content.Context;
import android.content.Intent;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class EventsActivity extends AppCompatActivity {


    private RecyclerView mInstaList;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        mInstaList = (RecyclerView) findViewById(R.id.events_list);
        mInstaList.setHasFixedSize(true);
        mInstaList.setLayoutManager(new GridLayoutManager(this, 3));
        mDatabase = FirebaseDatabase.getInstance().getReference().child("EventData");
        mDatabase.keepSynced(true);


        mAuth = FirebaseAuth.getInstance();
        //Checking if the user is valid
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()==null){
                    Intent intent = new Intent(EventsActivity.this, Registration.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK );
                    startActivity(intent);
                }
            }
        };






    }

    @Override
    public void onStart(){
        super.onStart();


        //Setting up the Auth Listner
        mAuth.addAuthStateListener(mAuthListener);




        FirebaseRecyclerAdapter<Event, EventsViewHolder> FBRA = new FirebaseRecyclerAdapter<Event, EventsViewHolder>(
                Event.class,
                R.layout.cardview_item_book,
                EventsViewHolder.class,
                mDatabase


        ) {
            @Override
            protected void populateViewHolder(EventsViewHolder viewHolder, Event model, int position) {
                final String post_key= getRef(position).getKey().toString();

                viewHolder.setTitle(model.getTitle());
              //  viewHolder.setDesc(model.getDesc());
                viewHolder.setImage(getApplicationContext(), model.getImage());
            //    viewHolder.setUsername(model.getUsername());

                viewHolder.mView.setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View v){

                       Intent intent = new Intent (EventsActivity.this, SingleEventActivity.class);
                       intent.putExtra("eventId", post_key);
                       startActivity(intent);

                    }
                } );
            }
        };

        mInstaList.setAdapter(FBRA);

    }


    public static class EventsViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public EventsViewHolder(View itemView){
            super(itemView);
            mView= itemView;
        }


        public void setTitle(String title){
            TextView event_title= (TextView)  mView.findViewById(R.id.eventTitle);
            event_title.setText(title);
        }

    /*    public void setDesc(String desc){
            TextView post_desc = (TextView) mView.findViewById(R.id.textDescription);
            post_desc.setText(desc);
        }
*/

        public void setImage(final Context ctx, final String image){
            final ImageView post_image= (ImageView) mView.findViewById(R.id.event_image);
            Picasso.with(ctx).load(image).networkPolicy(NetworkPolicy.OFFLINE).into(post_image, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                 Picasso.with(ctx).load(image).into(post_image);
                }
            });



        }



     /*   public void setUsername(String username){
            TextView name= (TextView) mView.findViewById(R.id.username);
            name.setText(username);
        }
        */

    }









    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id= item.getItemId();

       if(id==R.id.action_add_event){
            Intent intent = new Intent(EventsActivity.this, AddEventActivity.class);
            startActivity(intent);
        }else if(id == R.id.action_profile  ){
             Intent intent = new Intent(EventsActivity.this, UserProfile.class);
            startActivity(intent);
        }else if(id == R.id.action_logout  ){
           mAuth.signOut();
       }



        return super.onOptionsItemSelected(item);
    }



}
