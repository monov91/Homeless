package com.projects.radomonov.homeless.fragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.projects.radomonov.homeless.R;
import com.projects.radomonov.homeless.database.DatabaseInfo;
import com.projects.radomonov.homeless.model.Offer;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Tom on 21.10.2017.
 */

public class CreateOfferFragment extends Fragment {

    public static final int GALLERY_REQUEST = 1;
    public static final int CHOOSE_IMAGE = 2;
    private String title;
    private int price;
    private Offer.Currency currency;
    private int rooms;
    private String neighbourhood;
    ProgressDialog progressDialog;

    private Uri thumbnail;

    private FirebaseAuth mAuth;
    private EditText etTitle, etPrice, etRooms, etNeighbourhood;
    private Button btnSave, btnDelete;
    private ImageButton imgbtnChoose;
    private DatabaseReference offers;
    private DatabaseReference offer;
    private DatabaseReference currentUser;
    private StorageReference mStorage;
    private Uri imageUri;
    private String phoneNum;
    private RadioButton rdbtnEU;
    private RadioButton rddbtnBGN;
    private boolean isNewOffer = true;
    private Offer editOffer;
    private boolean changedImage = false;

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
        btnDelete = (Button) view.findViewById(R.id.btn_delete_create);
        btnDelete.setVisibility(View.GONE);

        imgbtnChoose = (ImageButton) view.findViewById(R.id.img_select_create);
        imgbtnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {/*
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image*//*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);*/
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                        CHOOSE_IMAGE);

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

            btnDelete.setVisibility(View.VISIBLE);
            setDeleteListener();
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


                if (isNewOffer) {
                    offer = offers.push();
                } else {
                    offer = offers.child(editOffer.getId());
                }

                if (isNewOffer) {
                    if (thumbnail != null) {
                        updateThumbnail();
                        writeToDB(offer);
                    } else {
                        Toast.makeText(getContext(), "Choose Image", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (changedImage) {
                        updateThumbnail();
                    }
                    writeToDB(offer);
                }
            }
        });
        return view;
    }

    private void setDeleteListener(){
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Offer> offersList ;
                String currentOfferID = editOffer.getId();
                offersList = DatabaseInfo.getOffersList();
                for(Offer of : offersList) {
                    if(of.getId().equals(currentOfferID)) {
                        offersList.remove(of);
                    }
                }

                final Query offerQuery = offers.child(currentOfferID);

                offerQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot offerSnapshot : dataSnapshot.getChildren()) {
                            offerSnapshot.getRef().removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                getActivity().getFragmentManager().beginTransaction().remove(CreateOfferFragment.this).commit();

            }
        });
    }

    private void updateThumbnail() {
        StorageReference filepath2 = mStorage.child("OfferImages").child("Offers").child(offer.getKey()).child("thumbnail");
        filepath2.putFile(thumbnail).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.i("thumb", " =====onSuccess=====");
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                offer.child("imageThumbnail").setValue(downloadUrl.toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("thumb", "=========onFail========");
            }
        });
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
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
        Glide.with(getContext()).load(offer.getImageThumbnail()).into(imgbtnChoose);
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
        //uj crop
        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK) {
            changedImage = true;
            //imageUri = data.getData();
            //imgbtnChoose.setImageURI(imageUri);
            Intent intent = CropImage.activity(data.getData()).setMaxCropResultSize(1100, 800)
                    .setCropShape(CropImageView.CropShape.RECTANGLE)
                    .setAspectRatio(5, 3)
                    .setFixAspectRatio(true).getIntent(getActivity());
            startActivityForResult(intent, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            try {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                Bitmap bitmap = MediaStore.Images.Media
                        .getBitmap(getActivity().getContentResolver(), result.getUri());
                /*Picasso.with(getActivity()).load(result.getUri())
                        .transform(new CropSquareTransformation())
                        .into(imgbtnChoose);*/
                thumbnail = getImageUri(getContext(), bitmap);
                imgbtnChoose.setImageURI(thumbnail);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    public void btnGone() {
        btnDelete.setVisibility(View.GONE);
    }
}
