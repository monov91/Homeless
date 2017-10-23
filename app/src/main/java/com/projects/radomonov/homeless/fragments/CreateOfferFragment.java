package com.projects.radomonov.homeless.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.projects.radomonov.homeless.R;
import com.projects.radomonov.homeless.app.CreateOfferActivity;

/**
 * Created by Tom on 21.10.2017.
 */

public class CreateOfferFragment extends Fragment {

    public static final int GALLERY_REQUEST = 1;

    private FirebaseAuth mAuth;
    private EditText etTitle, etPrice, etCurrency, etRooms, etNeighbourhood;
    private Button btnSave;
    private ImageButton imgbtnChoose;
    private DatabaseReference offers;
    private DatabaseReference currentUser;
    private StorageReference mStorage;
    private Uri imageUri;
    private String phoneNum;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_offer,container,false);

        offers = FirebaseDatabase.getInstance().getReference().child("Offers");

        mAuth = FirebaseAuth.getInstance();
        currentUser = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        mStorage = FirebaseStorage.getInstance().getReference();

        etTitle = (EditText) view.findViewById(R.id.et_title_create);
        etPrice = (EditText) view.findViewById(R.id.et_price_create);
        etCurrency = (EditText) view.findViewById(R.id.et_currency_create);
        etRooms = (EditText)view.findViewById(R.id.et_rooms_create);
        etNeighbourhood = (EditText) view.findViewById(R.id.et_neighbourhood_create);

        imgbtnChoose = (ImageButton) view.findViewById(R.id.img_select_create);
        imgbtnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);

            }
        });

        btnSave = (Button) view.findViewById(R.id.btn_save_create);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String title = etTitle.getText().toString().trim();
                final int price = Integer.parseInt(etPrice.getText().toString());
                final String currency = etCurrency.getText().toString().trim();
                final int rooms = Integer.parseInt(etRooms.getText().toString());
                final String neighbourhood = etNeighbourhood.getText().toString().trim();

                // if(nqkvi validacii)
                //DatabaseReference offerImages = newOffer.child("images");
                if (imageUri != null) {
                    //StorageReference filepath = mStorage.child("Blog_images").child(imageUri.getLastPathSegment());
                    StorageReference filepath = mStorage.child("OfferImages").child(imageUri.getLastPathSegment());
                    filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            final DatabaseReference newOffer = offers.push();
                            newOffer.child("title").setValue(title);
                                newOffer.child("price").setValue(price);
                                newOffer.child("currency").setValue(currency);
                                newOffer.child("rooms").setValue(rooms);
                                newOffer.child("neighbourhood").setValue(neighbourhood);
                                newOffer.child("image").setValue(downloadUrl.toString());
                                DatabaseReference phoneRef = currentUser.child("phoneNumber");
                            phoneRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    phoneNum = (String) dataSnapshot.getValue();
                                    newOffer.child("phoneNumber").setValue(phoneNum);
                                   // Toast.makeText(getActivity(), phoneNum, Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                  //  Toast.makeText(getActivity(), "Failed phone", Toast.LENGTH_SHORT).show();
                                }
                            });

                            newOffer.child("owner").setValue(currentUser.getKey().toString());
                            //finish();
                            getActivity().getFragmentManager().beginTransaction().remove(CreateOfferFragment.this).commit();
                        }
                    });
                    filepath.putFile(imageUri).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), "Couldnt upload file", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });




        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == getActivity().RESULT_OK) {

            imageUri = data.getData();
            imgbtnChoose.setImageURI(imageUri);

        }
    }
}
