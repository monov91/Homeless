package com.projects.radomonov.homeless.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import com.projects.radomonov.homeless.adapters.OfferPhotosAdapter;
import com.projects.radomonov.homeless.model.Offer;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Tom on 21.10.2017.
 */

public class CreateOfferFragment extends Fragment implements View.OnClickListener {
    public static final int CHOOSE_OFFER_THUMBNAIL = 2;
    public static final int ADD_PHOTO = 3;



    private Uri thumbnail;

    private FirebaseAuth mAuth;
    private EditText etTitle, etPrice, etRooms;
    private TextInputEditText etDescription;
    private Button btnSave, btnDelete, btnCancel;
    private RadioButton rdbtnEU,rdbtnBGN;
    private ImageButton imgbtnChooseThumbnail;
    private ImageView imgbtnAdd;
    private DatabaseReference allOffers,offer,currentUser;
    private StorageReference mStorage;
    private String phoneNum,neighbourhood,description,price,title,rooms;
    private Offer.Currency currency;

    private Spinner spinnerNeigh;
    private boolean isNewOffer,changedImage;
    private Offer editOffer;

    private List<Uri> offerImagesUrls;
    private HashMap<String, Uri> originalPics;
    private HashMap<String, Uri> toDeletePics;

    private OfferPhotosAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_offer, container, false);

        initialiseVariables(view);
        setEditTextListeners();
        setUpRecyclerForPhotos(view);
        setUpNeighbourhoodsSpinner();

        setListeners();

        Bundle bundle = getArguments();
        if (bundle != null) {
            editOffer = (Offer) getArguments().getSerializable("offer");
        }
        if (editOffer != null) {
            isNewOffer = false;
            fillFields(editOffer);

            btnDelete.setVisibility(View.VISIBLE);
        }

        return view;
    }

    private void setListeners() {
        btnCancel.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        imgbtnAdd.setOnClickListener(this);
        imgbtnChooseThumbnail.setOnClickListener(this);
    }



    private void setUpNeighbourhoodsSpinner() {
        String[] arr = getResources().getStringArray(R.array.neighbourhoods);
        ArrayAdapter adapter2 = new ArrayAdapter(getContext(),R.layout.support_simple_spinner_dropdown_item,arr) {
            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                v.setMinimumHeight(70);
                return v;
            }
        };
        spinnerNeigh.setAdapter(adapter2);
        spinnerNeigh.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                neighbourhood = spinnerNeigh.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void deleteThumbnail() {
        FirebaseStorage mFireBaseStorage = FirebaseStorage.getInstance();
        StorageReference pic = mFireBaseStorage.getReferenceFromUrl(String.valueOf(editOffer.getImageThumbnail()));
        pic.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });
    }

    private void deletePics(final HashMap<String, Uri> pics) {
        for (Map.Entry<String, Uri> entry : pics.entrySet()) {
            Uri url = entry.getValue();
            // key is the random string used as key in both
            // storage and database to know which database entry
            // corresponds to the file(pic) in the storage
            final String key = entry.getKey();
            FirebaseStorage mFireBaseStorage = FirebaseStorage.getInstance();
            StorageReference pic = mFireBaseStorage.getReferenceFromUrl(String.valueOf(url));
            pic.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // Check method argument 'pics' to see if we need to remove
                    // the pic URLs stored in the Database (if it isn't toDeletePics,
                    // it is done in the btnDelete click listener and no need to do it here)
                    if (pics == toDeletePics) {
                        FirebaseDatabase.getInstance().getReference().child("Offers").child(offer.getKey())
                                .child("imageUrls").child(key).removeValue();
                    }
                }
            });
        }
    }

    private void updateThumbnail() {
        StorageReference filepath2 = mStorage.child("OfferImages").child("Offers").child(offer.getKey()).child("thumbnail");
        filepath2.putFile(thumbnail).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                offer.child("imageThumbnail").setValue(downloadUrl.toString());
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
        offer.child("price").setValue(Integer.parseInt(price));
        offer.child("currency").setValue(currency);
        offer.child("rooms").setValue(Integer.parseInt(rooms));
        offer.child("neighbourhood").setValue(neighbourhood);
        offer.child("description").setValue(description);
        // Read the owner's phonenumber from Database
        // and add it to current offer
        DatabaseReference phoneRef = currentUser.child("phoneNumber");
        phoneRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                phoneNum = (String) dataSnapshot.getValue();
                offer.child("phoneNumber").setValue(phoneNum);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        storeNewPics();
        offer.child("owner").setValue(currentUser.getKey().toString());
        SearchFragment searchFrag = new SearchFragment();
        getActivity().getFragmentManager().beginTransaction().replace(R.id.fragment_container_main, searchFrag, "searchFrag").commit();
    }

    private void storeNewPics() {
        for (Uri photo : offerImagesUrls) {
            if (!isValidURL(String.valueOf(photo))) {
                final String randomString = getRandomString();
                StorageReference pictureRef = mStorage.child("OfferImages")
                        .child("Offers").child(offer.getKey()).child("Photos").child(randomString);
                pictureRef.putFile(photo).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri link = taskSnapshot.getDownloadUrl();
                        DatabaseReference pictureDBRef = offer.child("imageUrls").child(randomString);
                        pictureDBRef.setValue(link.toString());
                    }
                });
            }
        }
    }

    public void fillFields(Offer offer) {
        thumbnail = Uri.parse(offer.getImageThumbnail());
        Glide.with(getContext()).load(thumbnail).into(imgbtnChooseThumbnail);
        etTitle.setText(offer.getTitle());
        etRooms.setText("" + offer.getRooms());
        etPrice.setText("" + offer.getPrice());
        etDescription.setText(offer.getDescription());
        String neighbourhood = offer.getNeighbourhood();
        Offer.Currency currency = offer.getCurrency();
        if (currency == Offer.Currency.BGN) {
            rdbtnBGN.setChecked(true);
        }
        if (currency == Offer.Currency.EU) {
            rdbtnEU.setChecked(true);
        }
        //Set the offer's neighbourhood in the spinner
        spinnerNeigh.setSelection(((ArrayAdapter) spinnerNeigh.getAdapter()).getPosition(neighbourhood));
        //Add the offer's photos in the photos recycler
        if (offer.getImageUrls() != null) {
            for (Map.Entry<String, String> entry : offer.getImageUrls().entrySet()) {
                originalPics.put(entry.getKey(), Uri.parse(entry.getValue()));
                offerImagesUrls.add(Uri.parse(entry.getValue()));
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHOOSE_OFFER_THUMBNAIL && resultCode == RESULT_OK) {
            changedImage = true;
            //Send intent to crop the chosen image into smaller
            //thumbnail for displaying in recycler view
            Intent intent = CropImage.activity(data.getData()).setMaxCropResultSize(1100, 800)
                    .setCropShape(CropImageView.CropShape.RECTANGLE)
                    .setAspectRatio(5, 3)
                    .setFixAspectRatio(true).getIntent(getActivity());
            startActivityForResult(intent, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);
        }
        //Receive the result from latter intent
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            try {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                Bitmap bitmap = MediaStore.Images.Media
                        .getBitmap(getActivity().getContentResolver(), result.getUri());
                //Convert cropped bitmap into URI and set the thumbnail in ImageButton
                thumbnail = getImageUri(getContext(), bitmap);
                imgbtnChooseThumbnail.setImageURI(thumbnail);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        //Receive result image for offer photos
        if (requestCode == ADD_PHOTO && resultCode == RESULT_OK) {
            Uri uncroppedPhoto = data.getData();
            offerImagesUrls.add(uncroppedPhoto);
            adapter.notifyDataSetChanged();
        }
    }

    private void setUpRecyclerForPhotos(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.recycler_photos);
        adapter = new OfferPhotosAdapter(getContext(), offerImagesUrls, new OfferPhotosAdapter.deleteClickListener() {
            @Override
            public void onDeleteClick(Uri uri) {
                offerImagesUrls.remove(uri);
                adapter.notifyDataSetChanged();
                if (isValidURL(String.valueOf(uri))) {
                    for (Map.Entry<String, Uri> entry : originalPics.entrySet()) {
                        if (entry.getValue().equals(uri)) {
                            toDeletePics.put(entry.getKey(), entry.getValue());
                        }
                    }
                }
            }
        });
        recyclerView.setAdapter(adapter);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(manager);
    }

    private boolean isValidURL(String urlStr) {
        try {
            URI uri = new URI(urlStr);
            return uri.getScheme().equals("http") || uri.getScheme().equals("https");
        } catch (Exception e) {
            return false;
        }
    }

    private String getRandomString() {
        String uuid = UUID.randomUUID().toString();
        return uuid;
    }

    private boolean hasValidEntries() {
        boolean result = true;
        if (TextUtils.isEmpty(description)) {
            etDescription.setError("Enter a Description");
            result = false;
        }
        if (TextUtils.isEmpty(title)) {
            etTitle.setError("Enter a Title");
            result = false;
        }
        if (TextUtils.isEmpty("" + rooms)) {
            etRooms.setError("Enter Rooms");
            result = false;
        }
        if (TextUtils.isEmpty("" + price)) {
            etPrice.setError("Enter Price");
            result = false;
        }
        if (offerImagesUrls.isEmpty()) {
            Toast.makeText(getContext(), "Add at least 1 picture", Toast.LENGTH_SHORT).show();
            result = false;
        }
        if (isNewOffer && thumbnail == null) {
            Toast.makeText(getContext(), "Choose offer Thumbnail", Toast.LENGTH_SHORT).show();
            result = false;
        }


        return result;
    }

    private void initialiseVariables(View view) {
        allOffers = FirebaseDatabase.getInstance().getReference().child("Offers");
        offerImagesUrls = new ArrayList<>();
        originalPics = new HashMap<>();
        toDeletePics = new HashMap<>();
        isNewOffer = true;
        changedImage = false;
        mAuth = FirebaseAuth.getInstance();
        currentUser = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        mStorage = FirebaseStorage.getInstance().getReference();
        rdbtnBGN = view.findViewById(R.id.rdbtn_bgn_create);
        rdbtnEU = view.findViewById(R.id.rdbtn_eu_create);
        etTitle = view.findViewById(R.id.et_title_create);
        etPrice = view.findViewById(R.id.et_price_create);
        etRooms = view.findViewById(R.id.et_rooms_create);
        etDescription = view.findViewById(R.id.et_description_create);
        btnDelete = view.findViewById(R.id.btn_delete_create);
        spinnerNeigh = view.findViewById(R.id.spinnner_neigh_create);
        btnSave = view.findViewById(R.id.btn_save_create);
        btnCancel = view.findViewById(R.id.btn_cancel_create);
        imgbtnChooseThumbnail = view.findViewById(R.id.imgbtn_thumbnail_create);
        imgbtnAdd = view.findViewById(R.id.imgbtn_add_photo);
    }

    private void setEditTextListeners() {
        etRooms.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                int length = etRooms.length();
                if (length == 1 && etRooms.getText().toString().equals("0")) {
                    etRooms.setError("Cannot Start With \"0\"");
                    etRooms.setText("");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            etRooms.setError(null);
                        }
                    }, 1500);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        etPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int length = etPrice.length();
                if (length == 1 && etPrice.getText().toString().equals("0")) {
                    etPrice.setError("Cannot Start With \"0\"");
                    etPrice.setText("");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            etPrice.setError(null);
                        }
                    }, 1500);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        MyOffersFragment myOffersFragment;
        switch (view.getId()) {
            case R.id.btn_save_create:
                description = etDescription.getText().toString();
                title = etTitle.getText().toString().trim();
                price = etPrice.getText().toString();

                if (rdbtnEU.isChecked()) {
                    currency = Offer.Currency.EU;
                } else {
                    currency = Offer.Currency.BGN;
                }
                rooms = etRooms.getText().toString();

                if (!hasValidEntries()) {
                    return;
                } else {
                    if (isNewOffer) {
                        // Create new child for the offer in database
                        offer = allOffers.push();
                        updateThumbnail();
                        writeToDB(offer);

                    } else {
                        // Set the DB reference to the current offer (editing the offer)
                        offer = allOffers.child(editOffer.getId());
                        if (changedImage) {
                            updateThumbnail();
                        }
                        deletePics(toDeletePics);
                        writeToDB(offer);
                    }
                }
                myOffersFragment = new MyOffersFragment();
                getActivity().getFragmentManager().beginTransaction().replace(R.id.fragment_container_main,
                        myOffersFragment, "searchFrag").commit();
                break;
            case R.id.btn_delete_create:
                // Delete offer from database
                String currentOfferID = editOffer.getId();
                final Query offerQuery = allOffers.child(currentOfferID);
                offerQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        dataSnapshot.getRef().removeValue();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                deletePics(originalPics);
                deleteThumbnail();
                myOffersFragment = new MyOffersFragment();
                getActivity().getFragmentManager().beginTransaction().replace(R.id.fragment_container_main,
                        myOffersFragment, "searchFrag").commit();
                break;

            case R.id.btn_cancel_create:
                myOffersFragment = new MyOffersFragment();
                getActivity().getFragmentManager().beginTransaction().replace(R.id.fragment_container_main,
                        myOffersFragment, "searchFrag").commit();
                break;

            case R.id.imgbtn_add_photo:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                        ADD_PHOTO);
                break;

            case R.id.imgbtn_thumbnail_create:
                Intent intent2 = new Intent();
                intent2.setType("image/*");
                intent2.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent2, "Select Picture"),
                        CHOOSE_OFFER_THUMBNAIL);
                break;
        }
    }
}
