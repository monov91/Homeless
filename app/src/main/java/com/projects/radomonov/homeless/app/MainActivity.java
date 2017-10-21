package com.projects.radomonov.homeless.app;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.projects.radomonov.homeless.R;
import com.projects.radomonov.homeless.fragments.CreateOfferFragment;
import com.projects.radomonov.homeless.fragments.NavigationDrawerFragment;
import com.projects.radomonov.homeless.model.Offer;
import com.squareup.picasso.Picasso;

import java.io.Serializable;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private DatabaseReference offers;
    private DatabaseReference mDatabaseUsers;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpToolbar();
        setUpNavigationDrawer();

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if(firebaseAuth.getCurrentUser() == null) {
                    Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                } else {
//                    Toast.makeText(MainActivity.this, "Welcome...", Toast.LENGTH_SHORT).show();
                }
            }
        };

        offers = FirebaseDatabase.getInstance().getReference().child("Offers");

        //setUpRecycler();

        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseUsers.keepSynced(true);

    }


    @Override
    protected void onStart() {
        super.onStart();
        checkUserExist();

        mAuth.addAuthStateListener(mAuthListener);

        FirebaseRecyclerAdapter<Offer,MainActivity.OfferViewHolder> adapter = new FirebaseRecyclerAdapter<Offer, OfferViewHolder>(
                Offer.class,
                R.layout.offer_item,
                OfferViewHolder.class,
                offers

        ) {
            @Override
            protected void populateViewHolder(OfferViewHolder viewHolder, Offer offer, int position) {
                viewHolder.setTitle(offer.getTitle());
                viewHolder.setCurrency(offer.getCurrency());
                viewHolder.setNeighbourhood(offer.getNeighbourhood());
                viewHolder.setPrice(offer.getPrice());
                viewHolder.setRooms(offer.getRooms());

            }
        };
    }

   /* private void setUpRecycler() {

        recyclerView = (RecyclerView) findViewById(R.id.recycler_main);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }*/

    private void setUpNavigationDrawer(){
        NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.nav_drawer_fragment);
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_main);
        drawerFragment.setUpDrawer(R.id.nav_drawer_fragment,drawerLayout,toolbar);
    }

    private void setUpToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Search Homes");

    }

    public static class OfferViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public OfferViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setTitle(String title){
            TextView titleItem = mView.findViewById(R.id.tv_title_item);
            titleItem.setText(title);
        }

        public void setPrice(int price){
            TextView priceItem = mView.findViewById(R.id.tv_price_item);
            priceItem.setText(price);
        }

        public void setCurrency(String currency){
            TextView currencyItem = mView.findViewById(R.id.tv_currency_item);
            currencyItem.setText(currency);
        }

        public void setRooms(int rooms){
            TextView roomsItem = mView.findViewById(R.id.tv_rooms_item);
            roomsItem.setText(rooms);
        }

        public void setNeighbourhood(String neighbourhood){
            TextView neighbourhoodItem = mView.findViewById(R.id.tv_neighbourhood_item);
            neighbourhoodItem.setText(neighbourhood);
        }

        public void setImage(Context context,String imageUri){
            ImageView image_item = mView.findViewById(R.id.img_item);
            Picasso.with(context).load(imageUri).into(image_item);
        }
    }

    private void checkUserExist() {

        if(mAuth.getCurrentUser() != null) {

            final String userID = mAuth.getCurrentUser().getUid();

            mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (!dataSnapshot.hasChild(userID)) {

//                        Intent mainIntent = new Intent(MainActivity.this, SetupActivity.class);
//                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        startActivity(mainIntent);


                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }
}
