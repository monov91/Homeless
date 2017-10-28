package com.projects.radomonov.homeless.fragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import com.projects.radomonov.homeless.model.Offer;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Random;

import static android.R.attr.data;

/**
 * Created by Tom on 21.10.2017.
 */

public class CreateOfferFragment extends Fragment {

    public static final int GALLERY_REQUEST = 1;

    private String title;
    private int price;
    private Offer.Currency currency;
    private int rooms;
    private String neighbourhood;
    ProgressDialog progressDialog;

    private FirebaseAuth mAuth;
    private EditText etTitle, etPrice, etRooms, etNeighbourhood;
    private Button btnSave;
    private ImageButton imgbtnChoose;
    private DatabaseReference offers;
    private DatabaseReference currentUser;
    private StorageReference mStorage;
    private Uri imageUri;
    private String phoneNum;
    private RadioButton rdbtnEU;
    private RadioButton rddbtnBGN;
    private boolean isNewOffer = true;
    private Offer editOffer;
    private Bitmap bitmap;
    private byte[] imgBytes;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_offer, container, false);

        offers = FirebaseDatabase.getInstance().getReference().child("Offers");

        mAuth = FirebaseAuth.getInstance();
        currentUser = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        mStorage = FirebaseStorage.getInstance().getReference();

        rdbtnEU = view.findViewById(R.id.rbtn_eu);
        rddbtnBGN = view.findViewById(R.id.rbtn_bgn);
        etTitle = (EditText) view.findViewById(R.id.et_title_create);
        etPrice = (EditText) view.findViewById(R.id.et_price_create);
        etRooms = (EditText) view.findViewById(R.id.et_rooms_create);
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
        // Check if this is editing or new offer
        Bundle bundle = getArguments();
        if (bundle != null) {
            editOffer = (Offer) getArguments().getSerializable("offer");
        }
        if (editOffer != null) {
            isNewOffer = false;
            fillFields(editOffer);
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = new ProgressDialog(getContext());
                progressDialog.show();

                title = etTitle.getText().toString().trim();
                price = Integer.parseInt(etPrice.getText().toString());
                if (rdbtnEU.isChecked()) {
                    currency = Offer.Currency.EU;
                } else {
                    currency = Offer.Currency.BGN;
                }
                rooms = Integer.parseInt(etRooms.getText().toString());
                neighbourhood = etNeighbourhood.getText().toString().trim();

                // if(nqkvi validacii)
                //DatabaseReference offerImages = newOffer.child("images");
                final DatabaseReference offer;
                if (isNewOffer) {
                    offer = offers.push();
                } else {
                    offer = offers.child(editOffer.getId());
                    offer.child("image").setValue(editOffer.getImage());
                }
                if (isNewOffer) {
                    if (imageUri != null) {
                        StorageReference filepath = mStorage.child("OfferImages").child(imageUri.getLastPathSegment());
                        filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Log.i("snimka", " =====onSuccess=====");
                                @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                offer.child("image").setValue(downloadUrl.toString());

                                writeToDB(offer);
                            }
                        });
                        filepath.putFile(imageUri).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.i("snimka", "=========onFail========");
                            }
                        });

                    } else {
                        Toast.makeText(getContext(), "Choose Image", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    writeToDB(offer);
                }

            }
        });
        return view;
    }

    private void writeToDB(final DatabaseReference offer) {
        offer.child("id").setValue(offer.getKey());
        offer.child("title").setValue(title);
        offer.child("price").setValue(price);
        offer.child("currency").setValue(currency);
        offer.child("rooms").setValue(rooms);
        offer.child("neighbourhood").setValue(neighbourhood);
        DatabaseReference phoneRef = currentUser.child("phoneNumber");
        phoneRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                phoneNum = (String) dataSnapshot.getValue();
                offer.child("phoneNumber").setValue(phoneNum);
                // Toast.makeText(getActivity(), phoneNum, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //  Toast.makeText(getActivity(), "Failed phone", Toast.LENGTH_SHORT).show();
            }
        });

        offer.child("owner").setValue(currentUser.getKey().toString());
        progressDialog.dismiss();
        getActivity().getFragmentManager().beginTransaction().remove(CreateOfferFragment.this).commit();
    }

    public void fillFields(Offer offer) {
        //Picasso.with(getContext()).load(offer.getImage()).into(imgbtnChoose);
        Glide.with(getContext()).load(offer.getImage()).override(200, 200).into(imgbtnChoose);
        etTitle.setText(offer.getTitle());
        etRooms.setText("" + offer.getRooms());
        etPrice.setText("" + offer.getPrice());
        Offer.Currency currency = offer.getCurrency();
        if (currency == Offer.Currency.BGN) {
            rddbtnBGN.setChecked(true);
        }
        if (currency == Offer.Currency.EU) {
            rdbtnEU.setChecked(true);
        }
        etNeighbourhood.setText(offer.getNeighbourhood());
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == getActivity().RESULT_OK) {

            imageUri = data.getData();
           // imgbtnChoose.setImageURI(imageUri);
            Glide.with(getContext()).load(imageUri).into(imgbtnChoose);
        }
    }
}
